package org.manapart.enderports

import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.SlabBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

fun createTeleporterProps(): BlockBehaviour.Properties {
    return BlockBehaviour.Properties.of().apply {
        requiresCorrectToolForDrops()
        sound(SoundType.METAL)
        strength(4f)
    }
}

class Teleporter(props: Properties) : SlabBlock(props), EntityBlock {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = TeleporterEntity(pos, state)

    override fun <T : BlockEntity> getTicker(level: Level, state: BlockState, type: BlockEntityType<T>): BlockEntityTicker<T>? {
        return if (level.isClientSide) {
            super.getTicker(level, state, type)
        } else {
            TeleportTicker() as BlockEntityTicker<T>
        }
    }

    override fun useWithoutItem(state: BlockState, level: Level, pos: BlockPos, player: Player, blockHitResult: BlockHitResult): InteractionResult {
        if (!level.isClientSide) {
            val start = System.currentTimeMillis()
            level.playSound(null, player.blockPosition(), SoundEvents.SHULKER_BULLET_HIT, SoundSource.PLAYERS, 1f, 1f)
            val network = (level as ServerLevel).getNetwork()
            val nextPos = network.getNextTeleporter(pos)
            val serverPlayer = player as ServerPlayer
            network.assureTeleporterChain()
            return if (pos != nextPos) {
                val x = nextPos.x + .5
                val y = nextPos.y + 1.0
                val z = nextPos.z + .5
                (level.getBlockEntity(pos) as TeleporterEntity?)?.nextPos = nextPos
                player.connection.teleport(x, y, z, serverPlayer.yHeadRot, 0f)
                level.playSound(null, nextPos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1f, 1f)
                network.removeStaleLocation(nextPos)
                println("Teleported ${player.name.string} from $pos to $nextPos in " + (System.currentTimeMillis() - start))
                InteractionResult.SUCCESS
            } else {
                player.connection.teleport(pos.x+.5, pos.y+1.0, pos.z+.5, serverPlayer.yHeadRot, 0f)
                level.playSound(null, player.blockPosition(), SoundEvents.ENDERMITE_HURT, SoundSource.PLAYERS, 1f, 1f)
                InteractionResult.FAIL
            }
        } else {
            val nextPos = (level.getBlockEntity(pos) as TeleporterEntity?)?.nextPos?.above() ?: pos
            println("Client TP: $pos to $nextPos")
            with(nextPos.center) {
                player.absSnapTo(x, y, z, player.yHeadRot, 0f)
            }
        }
        return InteractionResult.PASS
    }

    override fun onPlace(blockState: BlockState, level: Level, pos: BlockPos, blockState2: BlockState, bl: Boolean) {
        super.onPlace(blockState, level, pos, blockState2, bl)
        if (!level.isClientSide) {
            (level as ServerLevel).getNetwork().addTeleporter(pos)
        }
    }

    override fun destroy(level: LevelAccessor, blockPos: BlockPos, blockState: BlockState) {
        super.destroy(level, blockPos, blockState)
        if (!level.isClientSide) {
            (level as ServerLevel).getNetwork().removeTeleporter(blockPos)
        }
    }

    override fun animateTick(blockState: BlockState, level: Level, pos: BlockPos, rand: RandomSource) {
        val j = rand.nextInt(2) * 2 - 1
        val k = rand.nextInt(2) * 2 - 1
        val d0 = pos.x.toDouble() + 0.5 + 0.25 * j.toDouble()
        val d1 = (pos.y.toFloat() + rand.nextFloat()).toDouble()
        val d2 = pos.z.toDouble() + 0.5 + 0.25 * k.toDouble()
        val d3 = (rand.nextFloat() * j.toFloat()).toDouble()
        val d4 = (rand.nextFloat().toDouble() - 0.5) * 0.125
        val d5 = (rand.nextFloat() * k.toFloat()).toDouble()
        level.addParticle(ParticleTypes.PORTAL, d0, d1, d2, d3, d4, d5)
    }
}
