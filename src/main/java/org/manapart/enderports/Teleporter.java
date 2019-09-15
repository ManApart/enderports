package org.manapart.enderports;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class Teleporter extends SlabBlock {
    public Teleporter() {
        super(Block.Properties.create(Material.IRON, MaterialColor.BLUE));
        setRegistryName("teleporter");
    }


    @Override
    public void onBlockClicked(BlockState state, World world, BlockPos pos, PlayerEntity entity) {
        super.onBlockClicked(state, world, pos, entity);
        System.out.println("On block clicked");
        teleport(world, pos, entity);
    }

    @Override
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        boolean result = super.onBlockActivated(state, world, pos, player, hand, rayTraceResult);
        System.out.println("On block activated");
        teleport(world, pos, player);
        return result;
    }

    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        System.out.println("On entity Walk");
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
//            teleport(world, pos, player);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos blockPos, BlockState state, @Nullable LivingEntity entity, ItemStack itemStack) {
        super.onBlockPlacedBy(world, blockPos, state, entity, itemStack);
        EnderPorts.teleporterNetwork.addTeleporter(world, blockPos);
    }

    @Override
    public void onBlockHarvested(World world, BlockPos blockPos, BlockState blockState, PlayerEntity playerEntity) {
        super.onBlockHarvested(world, blockPos, blockState, playerEntity);
        EnderPorts.teleporterNetwork.removeTeleporter(world, blockPos);
    }

    private void teleport(World worldIn, BlockPos pos, PlayerEntity entity) {
        System.out.println("Teleporting");
        BlockPos newPos = EnderPorts.teleporterNetwork.getNextTeleporter(worldIn, pos);
        if (!pos.equals(newPos)) {
            entity.setPosition(newPos.getX(), newPos.getY() + 2, newPos.getZ());
        }
    }
}
