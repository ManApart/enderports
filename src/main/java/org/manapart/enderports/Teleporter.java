package org.manapart.enderports;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class Teleporter extends SlabBlock {

    public Teleporter() {
        super(createProps());
    }

    private static AbstractBlock.Properties createProps() {
        Material padMat = new Material.Builder(MaterialColor.COLOR_BLUE).build();
        AbstractBlock.Properties props = AbstractBlock.Properties.of(padMat);
        props.sound(SoundType.METAL);
        props.requiresCorrectToolForDrops();
        props.harvestTool(ToolType.PICKAXE);
        props.strength(4);
        return props;
    }



//    @Override
//    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
//        if (world instanceof ServerWorld) {
//            ServerWorld serverWorld = (ServerWorld) world;
//            BlockPos nextPos = TeleporterNetwork.getNetwork(serverWorld).getNextTeleporter(pos);
//            if (!pos.equals(nextPos)) {
//                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
//                serverPlayer.connection.setPlayerLocation(nextPos.getX() + .5, nextPos.getY() + 1, nextPos.getZ() + .5, serverPlayer.getYaw(0f), 0);
//                serverWorld.playSound(null, nextPos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1f, 1f );
//            }
//        }
//
//        return ActionResultType.PASS;
//    }
//
//    @Override
//    public void onBlockPlacedBy(World world, BlockPos blockPos, BlockState state, @Nullable LivingEntity entity, ItemStack itemStack) {
//        super.onBlockPlacedBy(world, blockPos, state, entity, itemStack);
//        if (world instanceof ServerWorld) {
//            TeleporterNetwork network = TeleporterNetwork.getNetwork((ServerWorld) world);
//            network.reBalance();
//            network.addTeleporter(blockPos);
//        }
//    }
//
//    @Override
//    public void onBlockHarvested(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
//        super.onBlockHarvested(world, blockPos, blockState, playerEntity);
//        if (world instanceof ServerWorld) {
//            TeleporterNetwork.getNetwork((ServerWorld) world).removeTeleporter(blockPos);
//        }
//    }
}
