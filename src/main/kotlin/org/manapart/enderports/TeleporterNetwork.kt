package org.manapart.enderports

import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.world.level.Level
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.saveddata.SavedDataType
import java.util.function.Supplier

const val DATA_NAME = MODID + "_TeleporterSaveData"

fun ServerLevel.getNetwork(): TeleporterNetwork {
    val storage = level.dataStorage
    return storage.computeIfAbsent(SavedDataType<TeleporterNetwork>(
        DATA_NAME,
        { TeleporterNetwork(this)},
        TeleporterNetwork.codec(this),
        DataFixTypes.LEVEL
    )).apply { buildTeleporterChain() }
}

class TeleporterNetwork(private val world: Level, private val network: MutableMap<String, MutableSet<BlockPos>> = mutableMapOf()) : SavedData() {
    private var teleporterChain = mapOf<BlockPos, BlockPos>()

    companion object {
        private val POS_SET_CODEC: Codec<MutableSet<BlockPos>> = BlockPos.CODEC.listOf().xmap(
            { list -> list.toMutableSet() },
            { set -> set.toList() }
        )

        val DATA_CODEC: Codec<Map<String, List<BlockPos>>> =
            Codec.unboundedMap(Codec.STRING, BlockPos.CODEC.listOf())

        fun codec(world: Level): Codec<TeleporterNetwork> {
            return DATA_CODEC.xmap(
                { dataMap ->
                    TeleporterNetwork(world, dataMap.mapValues { it.value.toMutableSet() }.toMutableMap())
                },
                { networkInstance ->
                    networkInstance.network.mapValues { it.value.toList() }
                }
            )
        }
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

        println("Rebalance complete in " + (System.currentTimeMillis() - start))
    }

    fun assureTeleporterChain(){
        if (teleporterChain.isEmpty()) buildTeleporterChain()
    }

    internal fun buildTeleporterChain() {
        val newChain = mutableMapOf<BlockPos, BlockPos>()
        network.values.map { it.toList() }.forEach { chain ->
            (0 until chain.size - 1).forEach { i ->
                newChain[chain[i]] = chain[i + 1]
            }
            if (chain.isNotEmpty()) {
                newChain[chain.last()] = chain.first()
            }
        }
        teleporterChain = newChain
    }

    private fun getKey(pos: BlockPos): String {
        return try {
            val state = world.getBlockState(pos.below())
            state.block.descriptionId
        } catch (e: Exception) {
            println("Unable to find teleporter key for $pos")
            ""
        }
    }

    private fun isTeleporter(pos: BlockPos): Boolean {
        val key = world.getBlockState(pos).block.descriptionId
        return key == ModBlocks.TELEPORTER_BLOCK.descriptionId
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
//    nbt.getList("nodes", 10).forEach {
//        val node = it as CompoundTag
//        val key = node.getString("key")
//        val x = node.getDouble("x").toInt()
//        val y = node.getDouble("y").toInt()
//        val z = node.getDouble("z").toInt()
//        val pos = BlockPos(x, y, z)
//        network.addTeleporter(key, pos)
//    }
//    network.buildTeleporterChain()
//    println("Teleport Network Loaded")
//    println(network.dumpText())
    return network
}



private var deleteMeNetwork: TeleporterNetwork? = null
