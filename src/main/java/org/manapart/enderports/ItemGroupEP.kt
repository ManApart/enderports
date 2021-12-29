package org.manapart.enderports

import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

class ItemGroupEP private constructor(index: Int, label: String) : CreativeModeTab(index, label) {
    override fun makeIcon(): ItemStack {
        return ItemStack(EnderPorts.enderportsIcon)
    }

    companion object {
        val instance = ItemGroupEP(getGroupCountSafe(), "enderports")
    }
}