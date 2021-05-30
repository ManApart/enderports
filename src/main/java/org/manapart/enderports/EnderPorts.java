package org.manapart.enderports;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod(EnderPorts.MODID)
@Mod.EventBusSubscriber(modid = EnderPorts.MODID)
public class EnderPorts {

    public static final String MODID = "enderports";
    public static final Teleporter teleporter = createBlock();
    public static final TeleporterItem teleporterItem = createItem(teleporter);
    public static Item enderportsIcon = createIcon();

    public EnderPorts() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    private static Item createIcon() {
        Item icon = new Item(new Item.Properties());
        icon.setRegistryName(MODID + ":ep_icon");
        return icon;
    }

    private static Teleporter createBlock() {
        Teleporter teleporter = new Teleporter();
        teleporter.setRegistryName("teleporter");
        return teleporter;
    }

    private static TeleporterItem createItem(Teleporter block) {
        TeleporterItem teleporter = new TeleporterItem(block);
        teleporter.setRegistryName("teleporteritem");
        return teleporter;
    }


}
