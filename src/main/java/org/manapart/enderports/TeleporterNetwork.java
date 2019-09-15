package org.manapart.enderports;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;

public class TeleporterNetwork {
    private HashMap<String, ArrayList<BlockPos>> network = new HashMap<>();

    public void addTeleporter(World world, BlockPos pos) {
        String beneathBlockName = getKey(world, pos);
//        System.out.println("Adding teleporter with " + beneathBlockName);
        if (!network.containsKey(beneathBlockName)) {
            network.put(beneathBlockName, new ArrayList<>());
        }
        if (!network.get(beneathBlockName).contains(pos)) {
            network.get(beneathBlockName).add(pos);
        }
    }

    public void removeTeleporter(World world, BlockPos pos) {
        String beneathBlockName = getKey(world, pos);
//        System.out.println("Removing teleporter with " + beneathBlockName);
        if (network.containsKey(beneathBlockName) && network.get(beneathBlockName).contains(pos)) {
            network.get(beneathBlockName).remove(pos);
        }
    }

    public BlockPos getNextTeleporter(World world, BlockPos pos) {
        String beneathBlockName = getKey(world, pos);
//        System.out.println("Next teleporter with " + beneathBlockName);
        if (network.containsKey(beneathBlockName) && network.get(beneathBlockName).size() > 0) {
            ArrayList<BlockPos> positions = network.get(beneathBlockName);
            int index = positions.indexOf(pos) + 1;
            if (index >= positions.size()) index = 0;
            return positions.get(index);
        }
        return pos;
    }

    private String getKey(World world, BlockPos pos) {
        ResourceLocation registryName = world.getBlockState(pos.down()).getBlock().getRegistryName();
        if (registryName != null) {
            return registryName.toString();
        }
        return "";
    }

}
