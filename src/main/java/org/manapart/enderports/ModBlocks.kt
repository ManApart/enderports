package org.manapart.enderports

import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.registerObject

object ModBlocks {
    val teleporter = Teleporter()

    val REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID)

    val TELEPORTER_BLOCK by REGISTRY.registerObject("teleporter") {
        teleporter
    }
}