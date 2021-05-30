package org.manapart.enderports;

import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import static org.manapart.enderports.EnderPorts.enderportsIcon;
import static org.manapart.enderports.EnderPorts.teleporterItem;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {
    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        if (!ForgeRegistries.ITEMS.containsKey(teleporterItem.getRegistryName())) {
            ForgeRegistries.ITEMS.register(teleporterItem);
            ForgeRegistries.ITEMS.register(enderportsIcon);
        }
    }
}
