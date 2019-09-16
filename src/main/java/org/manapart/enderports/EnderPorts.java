package org.manapart.enderports;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(EnderPorts.MODID)
@Mod.EventBusSubscriber(modid = EnderPorts.MODID)
public class EnderPorts {

    public static final String MODID = "enderports";
    public static final Teleporter teleporter = createBlock();
    public static final TeleporterItem teleporterItem = createItem(teleporter);
    public static Item enderportsIcon = createIcon();

    public EnderPorts() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerBlocks);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerItems);
//        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onEntityTeleported);
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        System.out.println("Registering blocks");
        if (!ForgeRegistries.BLOCKS.containsKey(teleporter.getRegistryName())) {
            ForgeRegistries.BLOCKS.register(teleporter);
        }
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        System.out.println("Registering items");
        if (!ForgeRegistries.ITEMS.containsKey(teleporterItem.getRegistryName())) {
            ForgeRegistries.ITEMS.register(teleporterItem);
            ForgeRegistries.ITEMS.register(enderportsIcon);
        }
    }

//    @SubscribeEvent()
//    public void onEntityTeleported(TeleportEvent event) {
//        PlayerEntity player = (PlayerEntity) event.getEntity();
//        player.setPosition(event.getNextPos().getX() + .5, event.getNextPos().getY() + 1, event.getNextPos().getZ() + .5);
//        System.out.println("Player moved to " + player.getPosition().toString());
//    }

    private static Item createIcon() {
        Item icon = new Item(new Item.Properties());
        icon.setRegistryName(MODID + ":ep_icon");
        return icon;
    }

    private static Teleporter createBlock() {
        Teleporter teleporter = new Teleporter();
        teleporter.setRegistryName(new ResourceLocation(MODID + ":teleporter"));
        return teleporter;
    }

    private static TeleporterItem createItem(Teleporter block) {
        TeleporterItem teleporter = new TeleporterItem(block);
        teleporter.setRegistryName(new ResourceLocation(MODID + ":teleporteritem"));
        return teleporter;
    }


}
