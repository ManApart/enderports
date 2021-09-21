package org.manapart.enderports

import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.KDeferredRegister

object ModItems {
    val REGISTRY = KDeferredRegister(ForgeRegistries.ITEMS, MODID)

    val TELEPORT_ITEM by REGISTRY.registerObject("teleporteritem") {
        TeleporterItem(ModBlocks.teleporter)
    }
}