package org.manapart.enderports

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.saveddata.SavedData
import java.util.function.Supplier


const val DATA_NAME = MODID + "_TeleporterSaveData"

class TeleporterNetwork(private val world: Level) : SavedData() {
    private val network = mutableMapOf<String, MutableSet<BlockPos>>()
    private var teleporterChain = mapOf<BlockPos, BlockPos>()

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
        network.putIfAbsent(beneathBlockName, mutableSetOf())
        network[beneathBlockName]?.add(pos)
        buildTeleporterChain()
        setDirty()
    }

    fun removeTeleporter(pos: BlockPos) {
        val beneathBlockName = getKey(pos)
        if (network.containsKey(beneathBlockName) && network[beneathBlockName]?.contains(pos) == true) {
            network[beneathBlockName]?.remove(pos)
            buildTeleporterChain()
            setDirty()
            reBalance(beneathBlockName)
        }
    }

    fun getNextTeleporter(pos: BlockPos): BlockPos {
        return teleporterChain[pos] ?: pos
    }

    //In the case we teleport someone to a stale location, at least remove it so it doesn't happen again
    fun removeStaleLocation(pos: BlockPos) {
        if (!isTeleporter(pos)) {
            removeTeleporter(pos)
        }
    }

    private fun reBalance(key: String) {
        println("Balancing teleporter network for $key")
        val start = System.currentTimeMillis()

        if (network[key] != null) {
            val validSpots = network[key]?.filter { isTeleporter(it) }?.toMutableSet() ?: mutableSetOf()
            if (validSpots != network[key]) {
                network[key] = validSpots
                buildTeleporterChain()
                setDirty()
            }
        }

        println("Rebalance complete in "+ (System.currentTimeMillis() - start))
    }

    internal fun buildTeleporterChain() {
        val newChain = mutableMapOf<BlockPos, BlockPos>()
        network.values.map { it.toList() }.forEach { chain ->
            (0 until chain.size - 1).forEach { i ->
                newChain[chain[i]] = chain[i + 1]
            }
            newChain[chain.last()] = chain.first()
        }
        teleporterChain = newChain
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
    network.buildTeleporterChain()
//    println("Teleport Network Loaded")
//    println(network.dumpText())
    return network
}

fun ServerLevel.getNetwork(): TeleporterNetwork {
    val loadFunction = { nbt: CompoundTag -> load(nbt, this) }
    return dataStorage.computeIfAbsent(loadFunction, TeleporterNetwork.NetworkSupplier(this), DATA_NAME)
}