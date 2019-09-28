package org.manapart.enderports;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

import static org.manapart.enderports.EnderPorts.MODID;

public class TeleporterNetwork extends WorldSavedData {
    public static final String DATA_NAME = MODID + "_TeleporterSaveData";
    private HashMap<String, ArrayList<BlockPos>> network = new HashMap<>();
    private World world;

    public TeleporterNetwork(World world) {
        super(DATA_NAME);
        this.world = world;
    }

    @Override
    public void read(CompoundNBT nbt) {
        ListNBT nodes = nbt.getList("nodes", Constants.NBT.TAG_COMPOUND);
        for (INBT nodeI : nodes) {
            CompoundNBT node = (CompoundNBT) nodeI;
            String key = node.getString("key");
            double x = node.getDouble("x");
            double y = node.getDouble("y");
            double z = node.getDouble("z");
            BlockPos pos = new BlockPos(x, y, z);
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

    public void addTeleporter(BlockPos pos) {
        String beneathBlockName = getKey(pos);
        addTeleporter(beneathBlockName, pos);
    }


    public void addTeleporter(String beneathBlockName, BlockPos pos) {
//        System.out.println("Adding teleporter with " + beneathBlockName);
        if (!network.containsKey(beneathBlockName)) {
            network.put(beneathBlockName, new ArrayList<>());
        }
        if (!network.get(beneathBlockName).contains(pos)) {
            network.get(beneathBlockName).add(pos);
        }
        markDirty();
    }

    public void removeTeleporter(BlockPos pos) {
        String beneathBlockName = getKey(pos);
//        System.out.println("Removing teleporter with " + beneathBlockName);
        if (network.containsKey(beneathBlockName) && network.get(beneathBlockName).contains(pos)) {
            network.get(beneathBlockName).remove(pos);
        }
        markDirty();
    }

    public BlockPos getNextTeleporter(BlockPos pos) {
        return getNextTeleporterWithRetry(pos, true);
    }

    private BlockPos getNextTeleporterWithRetry(BlockPos pos, boolean retry) {
        String beneathBlockName = getKey(pos);
//        System.out.println("Next teleporter with " + beneathBlockName);
        if (network.containsKey(beneathBlockName) && network.get(beneathBlockName).size() > 0) {
            ArrayList<BlockPos> positions = network.get(beneathBlockName);
            int index = positions.indexOf(pos) + 1;
            if (index >= positions.size()) index = 0;
            BlockPos nextPos = positions.get(index);
            if (isTeleporter(nextPos)) {
                if (getKey(nextPos).equals(beneathBlockName)) {
                    return nextPos;
                }
            } else {
                removeTeleporter(nextPos);
            }
        }
        if (retry){
            reBalance();
            return getNextTeleporterWithRetry(pos, false);
        }
        return pos;
    }

    public void reBalance() {
        System.out.println("Rebalancing teleporter network.");
        HashMap<String, ArrayList<BlockPos>> staleTeleporters = new HashMap<>();
        for (String beneathBlockName : network.keySet()) {
            for (BlockPos pos : network.get(beneathBlockName)) {
                if (!getKey(pos).equals(beneathBlockName)) {
                    if (!staleTeleporters.containsKey(beneathBlockName)) {
                        staleTeleporters.put(beneathBlockName, new ArrayList<>());
                    }
                    staleTeleporters.get(beneathBlockName).add(pos);
                }
            }
        }

        for (String beneathBlockName : staleTeleporters.keySet()) {
            for (BlockPos pos : staleTeleporters.get(beneathBlockName)) {
                network.get(beneathBlockName).remove(pos);
                addTeleporter(pos);
            }
        }
    }

    private String getKey(BlockPos pos) {
        ResourceLocation registryName = world.getBlockState(pos.down()).getBlock().getRegistryName();
        if (registryName != null) {
            return registryName.toString();
        }
        return "";
    }

    private boolean isTeleporter(BlockPos pos) {
        ResourceLocation blockRegistryKey = world.getBlockState(pos).getBlock().getRegistryName();
        String key = "";
        if (blockRegistryKey != null) {
            key = blockRegistryKey.toString();
        }
        return key.equals(EnderPorts.teleporter.getRegistryName().toString());
    }

    public static TeleporterNetwork getNetwork(ServerWorld world) {
        return world.getSavedData().getOrCreate(new NetworkSupplier(world), DATA_NAME);
    }

    private static class NetworkSupplier implements Supplier<TeleporterNetwork> {
        private ServerWorld world;

        public NetworkSupplier(ServerWorld world) {
            this.world = world;
        }

        @Override
        public TeleporterNetwork get() {
            return new TeleporterNetwork(world);
        }
    }

}

