package org.manapart.enderports

import net.minecraft.world.item.CreativeModeTabs
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.fml.common.Mod
import org.manapart.enderports.ModBlocks.TELEPORTER_BLOCK
import org.manapart.enderports.ModItems.TELEPORT_ITEM
import thedarkcolour.kotlinforforge.forge.MOD_BUS


const val MODID = "enderports"

@Mod(MODID)
object EnderPorts {

    init {
        ModBlocks.REGISTRY.register(MOD_BUS)
        ModItems.REGISTRY.register(MOD_BUS)
        ModEntities.REGISTRY.register(MOD_BUS)
        MOD_BUS.addListener(::buildContents)
    }

    fun buildContents(event: BuildCreativeModeTabContentsEvent) {
        if (event.tabKey == CreativeModeTabs.REDSTONE_BLOCKS) {
            event.accept(TELEPORT_ITEM)
            event.accept(TELEPORTER_BLOCK)
        }
    }

}