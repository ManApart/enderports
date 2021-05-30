package org.manapart.enderports;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ItemGroupEP extends ItemGroup {
    public static final ItemGroupEP instance = new ItemGroupEP(ItemGroup.getGroupCountSafe(), "enderports");

    private ItemGroupEP(int index, String label) {
        super(index, label);
    }

    @Override
    public ItemStack makeIcon() {
        return new ItemStack(EnderPorts.enderportsIcon);
    }

}
