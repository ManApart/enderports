package org.manapart.enderports

import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack

class ItemGroupEP private constructor(index: Int, label: String) : ItemGroup(index, label) {
    override fun makeIcon(): ItemStack {
        return ItemStack(EnderPorts.enderportsIcon)
    }

    companion object {
        val instance = ItemGroupEP(getGroupCountSafe(), "enderports")
    }
}