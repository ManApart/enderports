package org.manapart.enderports

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.SlabBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Material
import net.minecraft.world.level.material.MaterialColor
import net.minecraft.world.phys.BlockHitResult


private fun createProps(): BlockBehaviour.Properties {
    val padMat = Material.Builder(MaterialColor.COLOR_BLUE).build()
    val props = BlockBehaviour.Properties.of(padMat)
    props.sound(SoundType.METAL)
//    props.requiresCorrectToolForDrops()
//    props.harvestTool(ToolType.PICKAXE)
    props.strength(4f)
    return props
}

class Teleporter : SlabBlock(createProps()) {

    override fun use(state: BlockState, world: Level, pos: BlockPos, player: Player, hand: InteractionHand, rayTraceResult: BlockHitResult): InteractionResult {
        if (!world.isClientSide) {
            val nextPos = (world as ServerLevel).getNetwork().getNextTeleporter(pos)
            if (pos != nextPos) {
                val serverPlayer = player as ServerPlayer
                serverPlayer.connection.teleport(nextPos.x + .5, (nextPos.y + 1).toDouble(), nextPos.z + .5, serverPlayer.yHeadRot, 0f)
                world.playSound(null, nextPos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1f, 1f)
            }
        }
        return InteractionResult.PASS
    }

    override fun onPlace(state: BlockState, world: Level, pos: BlockPos, newState: BlockState, boolThing: Boolean) {
        super.onPlace(state, world, pos, newState, boolThing)
        if (!world.isClientSide) {
            val network = (world as ServerLevel).getNetwork()
            network.reBalance()
            network.addTeleporter(pos)
        }
    }

    override fun onBlockExploded(state: BlockState, world: Level, pos: BlockPos, explosion: Explosion) {
        super.onBlockExploded(state, world, pos, explosion)
        if (!world.isClientSide) {
            (world as ServerLevel).getNetwork().removeTeleporter(pos)
        }
    }

}