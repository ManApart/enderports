package org.manapart.enderports

import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.MOD_BUS

const val MODID = "enderports"

@Mod(MODID)
object EnderPorts {

    init {
        ModBlocks.REGISTRY.register(MOD_BUS)
        ModItems.REGISTRY.register(MOD_BUS)
        ModEntities.REGISTRY.register(MOD_BUS)
    }

}