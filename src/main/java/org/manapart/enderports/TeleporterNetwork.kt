package org.manapart.enderports

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level
import net.minecraft.world.level.saveddata.SavedData
import net.minecraftforge.event.TickEvent
import java.util.function.Supplier


const val DATA_NAME = MODID + "_TeleporterSaveData"

class TeleporterNetwork(private val world: Level) : SavedData() {
    private val network = mutableMapOf<String, MutableSet<BlockPos>>()

    override fun save(cnbt: CompoundTag): CompoundTag {
        val nodes = ListTag()
        for (key in network.keys) {
            for (value in network[key]!!) {
                val node = CompoundTag()
                node.putString("key", key)
                node.putDouble("x", value.x.toDouble())
                node.putDouble("y", value.y.toDouble())
                node.putDouble("z", value.z.toDouble())
                nodes.add(node)
            }
        }
        cnbt.put("nodes", nodes)
        return cnbt
    }

    fun addTeleporter(pos: BlockPos) {
        val beneathBlockName = getKey(pos)
        addTeleporter(beneathBlockName, pos)
    }

    internal fun addTeleporter(beneathBlockName: String, pos: BlockPos) {
//        System.out.println("Adding teleporter with " + beneathBlockName);
        network.putIfAbsent(beneathBlockName, mutableSetOf())
        network[beneathBlockName]!!.add(pos)
        setDirty()
    }

    fun removeTeleporter(pos: BlockPos) {
        //        System.out.println("Removing teleporter with " + beneathBlockName);
        val beneathBlockName = getKey(pos)
        if (network.containsKey(beneathBlockName) && network[beneathBlockName]!!.contains(pos)) {
            network[beneathBlockName]!!.remove(pos)
        }
        setDirty()
    }

    fun getNextTeleporter(pos: BlockPos): BlockPos {
        return getNextTeleporterWithRetry(pos, true)
    }

    private fun getNextTeleporterWithRetry(pos: BlockPos, retry: Boolean): BlockPos {
        //        System.out.println("Next teleporter with " + beneathBlockName);
        val beneathBlockName = getKey(pos)
        if (network.containsKey(beneathBlockName) && network[beneathBlockName]!!.size > 0) {
            val positions = network[beneathBlockName]!!.toList()
            var index = positions.indexOf(pos) + 1
            if (index >= positions.size) index = 0
            val nextPos = positions[index]
            if (isTeleporter(nextPos)) {
                if (getKey(nextPos) == beneathBlockName) {
                    return nextPos
                }
            } else {
                removeTeleporter(nextPos)
            }
        }
        if (retry) {
            reBalance()
            return getNextTeleporterWithRetry(pos, false)
        }
        return pos
    }

    fun reBalance() {
        println("Rebalancing teleporter network.")
        val oldNetwork = network.toMap()
//        forceChunks(false)
        network.clear()

        oldNetwork.values.flatten().forEach { pos ->
            //Is a valid teleporter
            if (getKey(pos.above()) == "block.enderports.teleporter") {
                val beneathBlockName = getKey(pos)
                network.putIfAbsent(beneathBlockName, mutableSetOf())
                network[beneathBlockName]!!.add(pos)
            }
        }
//        forceChunks(true)

        println("Rebalance complete.")
    }

    private fun forceChunks(stayLoaded: Boolean) {
        if (world is ServerLevel) {
            val chunksUpdated = allTeleporterPositions()
                .asSequence()
                .map { world.setChunkForced(it.x, it.y, stayLoaded) }
                .count()
            println("Forced $chunksUpdated chunks")
        }
    }


    private fun getKey(pos: BlockPos): String {
        return try {
            val state = world.getBlockState(pos.below())
            state.block.descriptionId.toString()
        } catch (e: Exception) {
            println("Unable to find teleporter key for $pos")
            ""
        }
    }

    fun allTeleporterPositions(): List<BlockPos> {
        return network.values.flatten()
    }

    private fun isTeleporter(pos: BlockPos): Boolean {
        val key = world.getBlockState(pos).block.descriptionId.toString()
        return key == ModBlocks.TELEPORTER_BLOCK.descriptionId.toString()
    }

    fun dumpText(): String {
        return network.entries.joinToString("\n") { (blockId, positions) ->
            blockId + "\n\t" + positions.joinToString(",") { "(${it.x},${it.y},${it.z})" }
        }
    }

    internal class NetworkSupplier(private val world: Level) : Supplier<TeleporterNetwork> {
        override fun get(): TeleporterNetwork = TeleporterNetwork(world)
    }
}

fun load(nbt: CompoundTag, world: Level): TeleporterNetwork {
    val network = TeleporterNetwork(world)
    //Constants.NBT.TAG_COMPOUND - not sure where this constant lives now
    nbt.getList("nodes", 10).forEach {
        val node = it as CompoundTag
        val key = node.getString("key")
        val x = node.getDouble("x")
        val y = node.getDouble("y")
        val z = node.getDouble("z")
        val pos = BlockPos(x, y, z)
        network.addTeleporter(key, pos)
    }
//    println("Teleport Network Loaded")
//    println(network.dumpText())
    return network
}


fun ServerLevel.getNetwork(): TeleporterNetwork {
    val loadFunction = { nbt: CompoundTag -> load(nbt, this) }
    return dataStorage.computeIfAbsent(loadFunction, TeleporterNetwork.NetworkSupplier(this), DATA_NAME)
}

private var currentTick = 0
fun onTick(event: TickEvent.LevelTickEvent) {
    if (event.phase !== TickEvent.Phase.END || event.level !is ServerLevel) return
    currentTick++
    if (currentTick < 200) return
    currentTick = 0

    val level = event.level as ServerLevel
    val tickSpeed = level.gameRules.getInt(GameRules.RULE_RANDOMTICKING)
    if (tickSpeed > 0) {
        try {
            val chunksUpdated = level.getNetwork().allTeleporterPositions()
                .asSequence()
                .map { level.getChunk(it).pos }.toSet()
                .filter { level.chunkSource.chunkMap.getPlayers(it, false).isEmpty() }
                .map { pos ->
                    level.tickChunk(level.getChunk(pos.x, pos.z), tickSpeed)
                    1
                }.count()
            println("Updated $chunksUpdated chunks")
        } catch (e: Exception) {
            println("Failed to tick teleporters!")
            println(e)
        }
    }
}