package org.manapart.enderports

import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.KDeferredRegister

object ModBlocks {
    val teleporter = Teleporter()

    val REGISTRY = KDeferredRegister(ForgeRegistries.BLOCKS, MODID)

    val TELEPORTER_BLOCK by REGISTRY.registerObject("teleporter") {
        teleporter
    }
}