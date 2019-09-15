package org.manapart.enderports;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;

public class TeleporterNetwork extends WorldSavedData {
    private HashMap<String, ArrayList<BlockPos>> network = new HashMap<>();

    public TeleporterNetwork() {
        super("TeleporterSaveData");
    }

    @Override
    public void read(CompoundNBT nbt) {
        ListNBT nodes = nbt.getList("nodes", Constants.NBT.TAG_COMPOUND);
        for (INBT nodeI : nodes) {
            CompoundNBT node = (CompoundNBT) nodeI;
            String key = node.getString("key");
            BlockPos pos = new BlockPos(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z"));
            addTeleporter(key, pos);
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        CompoundNBT cnbt = new CompoundNBT();
        ListNBT nodes = new ListNBT();

        for (String key : network.keySet()) {
            for (BlockPos value : network.get(key)) {
                CompoundNBT node = new CompoundNBT();
                node.putString("key", key);
                node.putDouble("x", value.getX());
                node.putDouble("y", value.getY());
                node.putDouble("z", value.getZ());
                nodes.add(node);
            }
        }

        cnbt.put("nodes", nodes);
        return cnbt;
    }


    public void addTeleporter(String beneathBlockName, BlockPos pos) {
//        System.out.println("Adding teleporter with " + beneathBlockName);
        if (!network.containsKey(beneathBlockName)) {
            network.put(beneathBlockName, new ArrayList<>());
        }
        if (!network.get(beneathBlockName).contains(pos)) {
            network.get(beneathBlockName).add(pos);
        }
    }

    public void removeTeleporter(String beneathBlockName, BlockPos pos) {
//        System.out.println("Removing teleporter with " + beneathBlockName);
        if (network.containsKey(beneathBlockName) && network.get(beneathBlockName).contains(pos)) {
            network.get(beneathBlockName).remove(pos);
        }
    }

    public BlockPos getNextTeleporter(String beneathBlockName, BlockPos pos) {
//        System.out.println("Next teleporter with " + beneathBlockName);
        if (network.containsKey(beneathBlockName) && network.get(beneathBlockName).size() > 0) {
            ArrayList<BlockPos> positions = network.get(beneathBlockName);
            int index = positions.indexOf(pos) + 1;
            if (index >= positions.size()) index = 0;
            return positions.get(index);
        }
        return pos;
    }

    public static String getKey(World world, BlockPos pos) {
        ResourceLocation registryName = world.getBlockState(pos.down()).getBlock().getRegistryName();
        if (registryName != null) {
            return registryName.toString();
        }
        return "";
    }
}

