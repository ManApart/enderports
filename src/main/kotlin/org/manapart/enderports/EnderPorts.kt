package org.manapart.enderports

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents
import net.minecraft.world.item.CreativeModeTabs
import org.manapart.enderports.ModItems.TELEPORT_ITEM


const val MODID = "enderports"

object EnderPorts : ModInitializer {

    override fun onInitialize() {
        ModEntities.initialize()
        ModBlocks.initialize()
        ModItems.initialize()
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.REDSTONE_BLOCKS).register(ItemGroupEvents.ModifyEntries { itemGroup: FabricItemGroupEntries ->
            itemGroup.accept(TELEPORT_ITEM)
        })
    }

}
