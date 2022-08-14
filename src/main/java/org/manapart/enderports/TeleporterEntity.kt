package org.manapart.enderports

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.Connection
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class TeleporterEntity(val pos: BlockPos, state: BlockState) : BlockEntity(createEntityType(), pos, state) {
    var nextPos = BlockPos(pos.x, 400, pos.y)

    override fun getUpdatePacket(): Packet<ClientGamePacketListener>? {
        return ClientboundBlockEntityDataPacket.create(this) {
            CompoundTag().apply {
                putDouble("x", nextPos.x.toDouble())
                putDouble("y", nextPos.y.toDouble())
                putDouble("z", nextPos.z.toDouble())
            }
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

}