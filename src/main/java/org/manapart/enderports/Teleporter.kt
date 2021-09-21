package org.manapart.enderports

import net.minecraft.block.AbstractBlock
import net.minecraft.block.BlockState
import net.minecraft.block.SlabBlock
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.material.MaterialColor
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.util.ActionResultType
import net.minecraft.util.Hand
import net.minecraft.util.SoundCategory
import net.minecraft.util.SoundEvents
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.BlockRayTraceResult
import net.minecraft.world.Explosion
import net.minecraft.world.World
import net.minecraft.world.server.ServerWorld
import net.minecraftforge.common.ToolType

private fun createProps(): AbstractBlock.Properties {
    val padMat = Material.Builder(MaterialColor.COLOR_BLUE).build()
    val props = AbstractBlock.Properties.of(padMat)
    props.sound(SoundType.METAL)
    props.requiresCorrectToolForDrops()
    props.harvestTool(ToolType.PICKAXE)
    props.strength(4f)
    return props
}

class Teleporter : SlabBlock(createProps()) {
    override fun use(state: BlockState, world: World, pos: BlockPos, player: PlayerEntity, hand: Hand, rayTraceResult: BlockRayTraceResult): ActionResultType {
        if (world is ServerWorld) {
            val nextPos = world.getNetwork().getNextTeleporter(pos)
            if (pos != nextPos) {
                val serverPlayer = player as ServerPlayerEntity
                serverPlayer.connection.teleport(nextPos.x + .5, (nextPos.y + 1).toDouble(), nextPos.z + .5, serverPlayer.yHeadRot, 0f)
                world.playSound(null, nextPos, SoundEvents.ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1f, 1f)
            }
        }
        return ActionResultType.PASS
    }

    override fun onPlace(state: BlockState, world: World, pos: BlockPos, newState: BlockState, boolThing: Boolean) {
        super.onPlace(state, world, pos, newState, boolThing)
        if (world is ServerWorld) {
            val network = world.getNetwork()
            network.reBalance()
            network.addTeleporter(pos)
        }
    }

    override fun onBlockExploded(state: BlockState, world: World, pos: BlockPos, explosion: Explosion) {
        super.onBlockExploded(state, world, pos, explosion)
        if (world is ServerWorld) {
            world.getNetwork().removeTeleporter(pos)
        }
    }

}