package org.manapart.enderports;

import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Teleporter extends SlabBlock {
    public Teleporter() {
        super(Block.Properties.create(Material.IRON, MaterialColor.BLUE));
        setRegistryName("teleporter");

    }

    public void onEntityWalk(World worldIn, BlockPos pos, Entity entity) {
        System.out.println("On entity Walk");
        if (entity instanceof PlayerEntity) {
            System.out.println("Teleporting");
            entity.setPosition(entity.posX + 10, entity.posY + 10, entity.posZ);
        }
    }


}
