package org.manapart.enderports;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryBuilder;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EnderPorts.MODID)
@Mod.EventBusSubscriber
public class EnderPorts {

    public static final String MODID = "enderports";
    public static final Teleporter teleporter = new Teleporter();
    public static final TeleporterItem teleporterItem = new TeleporterItem(teleporter);
    public static Item enderportsIcon = createIcon();
    public static final TeleporterNetwork teleporterNetwork = new TeleporterNetwork();

    public EnderPorts() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerBlocks);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerItems);
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModelRegistry);
    }

//    @SubscribeEvent
//    public void onBreak(BlockEvent.BreakEvent event) {
//        System.out.println("Break block");
//        Block block = event.getState().getBlock();
//        BlockPos pos = event.getPos();
//        event.getPlayer().addItemStackToInventory(new ItemStack(teleporterItem));
//
//    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        System.out.println("Registering blocks");
        ForgeRegistries.BLOCKS.register(teleporter);
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        System.out.println("Registering items");
        ForgeRegistries.ITEMS.register(teleporterItem);
        ForgeRegistries.ITEMS.register(enderportsIcon);
    }

    @SubscribeEvent
    public void registerTeleporters(RegistryEvent.NewRegistry event) {
        System.out.println("Registering new registry");
//        new RegistryBuilder()
//                .setName(new ResourceLocation("enderports:teleporternetwork"))
//                .setType(TeleporterNetwork.class)
//                .create();
    }

    private static Item createIcon() {
        Item icon = new Item(new Item.Properties());
        icon.setRegistryName("ep_icon");
        return icon;
    }

//    @SubscribeEvent
//    public void onModelRegistry(ModelRegistryEvent event) {
//        System.out.println("Model Registry");
//        registerRender(Item.getItemFromBlock(tutorialBlock));
//    }
//
//    public static void registerRender(Item item) {
////        ModelLoaderRegistry.registerLoader();
//        ModelLoaderRegistry.
//        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation( item.getRegistryName(), "inventory"));
//    }


}
