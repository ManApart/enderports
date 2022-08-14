package org.manapart.enderports

import net.minecraft.core.BlockPos
import net.minecraft.network.Connection
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class TeleporterEntity(pos: BlockPos, state: BlockState) : BlockEntity(createEntityType(), pos, state) {
    var nextPos = BlockPos(pos.x, 400, pos.y)
        private set

    override fun onDataPacket(net: Connection?, pkt: ClientboundBlockEntityDataPacket?) {
        super.onDataPacket(net, pkt)
    }
}