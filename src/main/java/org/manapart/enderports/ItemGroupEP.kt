package org.manapart.enderports

import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

class ItemGroupEP private constructor(index: Int, label: String) : CreativeModeTab(index, label) {
    override fun makeIcon() = ItemStack(EnderPorts.icon)

    companion object {
        val instance = ItemGroupEP(getGroupCountSafe(), "enderports")
    }
}