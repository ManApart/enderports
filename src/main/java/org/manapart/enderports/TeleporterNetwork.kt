package org.manapart.enderports

import net.minecraft.nbt.CompoundNBT
import net.minecraft.nbt.ListNBT
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.server.ServerWorld
import net.minecraft.world.storage.WorldSavedData
import net.minecraftforge.common.util.Constants
import java.util.function.Supplier

const val DATA_NAME = MODID + "_TeleporterSaveData"

class TeleporterNetwork(private val world: World) : WorldSavedData(DATA_NAME) {
    private val network = HashMap<String, ArrayList<BlockPos>>()
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

    fun addTeleporter(beneathBlockName: String, pos: BlockPos) {
//        System.out.println("Adding teleporter with " + beneathBlockName);
        if (!network.containsKey(beneathBlockName)) {
            network[beneathBlockName] = ArrayList()
        }
        if (!network[beneathBlockName]!!.contains(pos)) {
            network[beneathBlockName]!!.add(pos)
        }
        setDirty()
    }

    fun removeTeleporter(pos: BlockPos) {
        val beneathBlockName = getKey(pos)
        //        System.out.println("Removing teleporter with " + beneathBlockName);
        if (network.containsKey(beneathBlockName) && network[beneathBlockName]!!.contains(pos)) {
            network[beneathBlockName]!!.remove(pos)
        }
        setDirty()
    }

    fun getNextTeleporter(pos: BlockPos): BlockPos {
        return getNextTeleporterWithRetry(pos, true)
    }

    private fun getNextTeleporterWithRetry(pos: BlockPos, retry: Boolean): BlockPos {
        val beneathBlockName = getKey(pos)
        //        System.out.println("Next teleporter with " + beneathBlockName);
        if (network.containsKey(beneathBlockName) && network[beneathBlockName]!!.size > 0) {
            val positions = network[beneathBlockName]!!
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
        val staleTeleporters = HashMap<String, ArrayList<BlockPos>>()
        for (beneathBlockName in network.keys) {
            for (pos in network[beneathBlockName]!!) {
                if (getKey(pos) != beneathBlockName) {
                    if (!staleTeleporters.containsKey(beneathBlockName)) {
                        staleTeleporters[beneathBlockName] = ArrayList()
                    }
                    staleTeleporters[beneathBlockName]!!.add(pos)
                }
            }
        }
        for (beneathBlockName in staleTeleporters.keys) {
            for (pos in staleTeleporters[beneathBlockName]!!) {
                network[beneathBlockName]!!.remove(pos)
                addTeleporter(pos)
            }
        }
    }

    private fun getKey(pos: BlockPos): String {
        val registryName = world.getBlockState(pos.below()).block.registryName
        return registryName?.toString() ?: ""
    }

    private fun isTeleporter(pos: BlockPos): Boolean {
        val blockRegistryKey = world.getBlockState(pos).block.registryName
        var key = ""
        if (blockRegistryKey != null) {
            key = blockRegistryKey.toString()
        }
        return key == ModBlocks.teleporter.registryName.toString()
    }

    private class NetworkSupplier(private val world: ServerWorld) : Supplier<TeleporterNetwork> {
        override fun get(): TeleporterNetwork {
            return TeleporterNetwork(world)
        }
    }

    companion object {
        fun getNetwork(world: ServerWorld): TeleporterNetwork {
            return world.dataStorage.computeIfAbsent(NetworkSupplier(world), DATA_NAME)
            //        return world.getSavedData().getOrCreate(new NetworkSupplier(world), DATA_NAME);
        }
    }
}