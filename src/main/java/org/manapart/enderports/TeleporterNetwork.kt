package org.manapart.enderports

import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.ListNBT
import net.minecraft.util.math.BlockPos
import net.minecraft.world.server.ServerWorld
import net.minecraft.world.storage.WorldSavedData
import net.minecraftforge.common.util.Constants
import java.util.function.Supplier

const val DATA_NAME = MODID + "_TeleporterSaveData"

class TeleporterNetwork(private val world: ServerWorld) : WorldSavedData(DATA_NAME) {
    private val network = mutableMapOf<String, MutableSet<BlockPos>>()

    override fun load(nbt: CompoundNBT) {
        val nodes = nbt.getList("nodes", Constants.NBT.TAG_COMPOUND)
        for (nodeI in nodes) {
            val node = nodeI as CompoundNBT
            val key = node.getString("key")
            val x = node.getDouble("x")
            val y = node.getDouble("y")
            val z = node.getDouble("z")
            val pos = BlockPos(x, y, z)
            addTeleporter(key, pos)
        }
    }

    override fun save(compound: CompoundNBT): CompoundNBT {
        val cnbt = CompoundNBT()
        val nodes = ListNBT()
        for (key in network.keys) {
            for (value in network[key]!!) {
                val node = CompoundNBT()
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

    private fun addTeleporter(beneathBlockName: String, pos: BlockPos) {
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
        val staleTeleporters = mutableMapOf<String, MutableList<BlockPos>>()
        network.entries.forEach { (beneathBlockName, posList) ->
            posList.forEach { pos ->
                if (getKey(pos) != beneathBlockName) {
                    staleTeleporters.putIfAbsent(beneathBlockName, mutableListOf())
                    staleTeleporters[beneathBlockName]!!.add(pos)
                }
            }
        }
        staleTeleporters.entries.forEach { (beneathBlockName, posList) ->
            posList.forEach { pos ->
                network[beneathBlockName]!!.remove(pos)
                addTeleporter(pos)
            }
        }
    }

    private fun getKey(pos: BlockPos): String = world.getBlockState(pos.below()).block.registryName?.toString() ?: ""

    private fun isTeleporter(pos: BlockPos): Boolean {
        val key = world.getBlockState(pos).block.registryName?.toString() ?: ""
        return key == ModBlocks.teleporter.registryName.toString()
    }

    internal class NetworkSupplier(private val world: ServerWorld) : Supplier<TeleporterNetwork> {
        override fun get(): TeleporterNetwork = TeleporterNetwork(world)
    }
}

fun ServerWorld.getNetwork(): TeleporterNetwork {
    return dataStorage.computeIfAbsent(TeleporterNetwork.NetworkSupplier(this), DATA_NAME)
}