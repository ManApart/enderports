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
            Double x = node.getDouble("x");
            Double y = node.getDouble("y");
            Double z = node.getDouble("z");
            BlockPos pos = new BlockPos(x, y, z);
            if (!(pos.getX() == 0 && pos.getY() == 0 && pos.getZ() == 0)) {
                addTeleporter(key, pos);
            }
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
        String beneathBlockName = getKey(pos);
//        System.out.println("Next teleporter with " + beneathBlockName);
        if (network.containsKey(beneathBlockName) && network.get(beneathBlockName).size() > 0) {
            ArrayList<BlockPos> positions = network.get(beneathBlockName);
            int index = positions.indexOf(pos) + 1;
            if (index >= positions.size()) index = 0;
            return positions.get(index);
        }
        return pos;
    }

    private String getKey(BlockPos pos) {
        ResourceLocation registryName = world.getBlockState(pos.down()).getBlock().getRegistryName();
        if (registryName != null) {
            return registryName.toString();
        }
        return "";
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

