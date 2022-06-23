package org.manapart.enderports

import net.minecraft.world.item.Item
import net.minecraftforge.fml.common.Mod
import thedarkcolour.kotlinforforge.forge.MOD_BUS

const val MODID = "enderports"

@Mod(MODID)
object EnderPorts {

    init {
        ModBlocks.REGISTRY.register(MOD_BUS)
        ModItems.REGISTRY.register(MOD_BUS)
    }

    val icon = Item(Item.Properties())

}