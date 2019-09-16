package org.manapart.enderports;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class Teleporter extends SlabBlock {

    public Teleporter() {
        super(Block.Properties.create(Material.IRON, MaterialColor.BLUE).hardnessAndResistance(4f).sound(SoundType.METAL));
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return ToolType.PICKAXE;
    }

    @Override
    public int getHarvestLevel(BlockState state) {
        return 2; //Harvest level of 2 means it requires iron or better to harvest
    }

    @Override
    public ResourceLocation getLootTable() {
        return super.getLootTable();
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        boolean result = super.onBlockActivated(state, world, pos, player, hand, rayTraceResult);
//        System.out.println("On block clicked");
        if (world instanceof ServerWorld) {
            ServerWorld serverWorld = (ServerWorld) world;
            BlockPos nextPos = TeleporterNetwork.getNetwork(serverWorld).getNextTeleporter(pos);
            if (!pos.equals(nextPos)) {
                ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
                serverPlayer.connection.setPlayerLocation(nextPos.getX() + .5, nextPos.getY() + 1, nextPos.getZ() + .5, serverPlayer.getYaw(0f), 0);
                serverWorld.playSound(null, nextPos, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1f, 1f );
            }
        }

        return result;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos blockPos, BlockState state, @Nullable LivingEntity entity, ItemStack itemStack) {
        super.onBlockPlacedBy(world, blockPos, state, entity, itemStack);
        if (world instanceof ServerWorld) {
            TeleporterNetwork.getNetwork((ServerWorld) world).addTeleporter(blockPos);
        }
    }

    @Override
    public void onBlockHarvested(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
        super.onBlockHarvested(world, blockPos, blockState, playerEntity);
        if (world instanceof ServerWorld) {
            TeleporterNetwork.getNetwork((ServerWorld) world).removeTeleporter(blockPos);
        }
    }
}
