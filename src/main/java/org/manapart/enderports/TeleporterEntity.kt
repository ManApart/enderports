package org.manapart.enderports

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.Connection
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import org.manapart.enderports.ModEntities.ENDERPOT_BLOCK_ENTITY

class TeleportTicker : BlockEntityTicker<TeleporterEntity> {
    override fun tick(level: Level, pos: BlockPos, state: BlockState, tp: TeleporterEntity) {
        if (level is ServerLevel) {
            tp.updateNextPos(level)
        }
    }
}

class TeleporterEntity(private val pos: BlockPos, private val state: BlockState) : BlockEntity(ENDERPOT_BLOCK_ENTITY, pos, state) {
    private var tick = 0
    var nextPos = BlockPos(pos.x, 400, pos.y)

    override fun getUpdatePacket(): Packet<ClientGamePacketListener>? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun getUpdateTag(): CompoundTag {
        return CompoundTag().apply {
            putDouble("x", nextPos.x.toDouble())
            putDouble("y", nextPos.y.toDouble())
            putDouble("z", nextPos.z.toDouble())
        }
    }

    override fun onDataPacket(net: Connection?, pkt: ClientboundBlockEntityDataPacket?) {
        super.onDataPacket(net, pkt)
        if (pkt != null) {
            with(pkt.tag!!) {
                val x = getDouble("x")
                val y = getDouble("y")
                val z = getDouble("z")
                nextPos = BlockPos(x, y, z)
            }
        }
    }

    fun updateNextPos(level: ServerLevel) {
        tick++
        if (tick > 10) {
            tick = 0
            nextPos = level.getNetwork().getNextTeleporter(pos)

            level.blockEntityChanged(pos)
            level.sendBlockUpdated(pos, state, state, 3)
        }
    }

}