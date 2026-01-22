package org.manapart.enderports

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import org.manapart.enderports.ModEntities.ENDERPORT_BLOCK_ENTITY

class TeleportTicker : BlockEntityTicker<TeleporterEntity> {
    private var tick = 0
    override fun tick(level: Level, pos: BlockPos, state: BlockState, tp: TeleporterEntity) {
        tick++
        if (tick > 20) {
            tick = 0
            if (level is ServerLevel) {
                tp.updateNextPos(level)
            }
        }
    }
}

class TeleporterEntity(private val pos: BlockPos, private val state: BlockState) : BlockEntity(ENDERPORT_BLOCK_ENTITY, pos, state) {
    //Give height to load area before server kicks in
    var nextPos = BlockPos(pos.x, 400, pos.y)

    fun updateNextPos(level: ServerLevel) {
        nextPos = level.getNetwork().getNextTeleporter(pos)

        level.blockEntityChanged(pos)
        level.sendBlockUpdated(pos, state, state, 3)
    }

}
