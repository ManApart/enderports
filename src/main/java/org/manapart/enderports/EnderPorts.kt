package org.manapart.enderports

import net.minecraftforge.event.TickEvent
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_BUS

const val MODID = "enderports"

@Mod(MODID)
object EnderPorts {

    init {
        ModBlocks.REGISTRY.register(MOD_BUS)
        ModItems.REGISTRY.register(MOD_BUS)
        FORGE_BUS.addListener { event: TickEvent.LevelTickEvent -> onTick(event) }
    }

}