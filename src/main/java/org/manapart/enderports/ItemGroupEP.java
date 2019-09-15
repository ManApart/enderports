/*******************************************************************************
 * Copyright 2014-2019, the Biomes O' Plenty Team
 *
 * This work is licensed under a Creative Commons Attribution-NonCommercial-NoDerivatives 4.0 International Public License.
 *
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/4.0/.
 ******************************************************************************/
package org.manapart.enderports;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ItemGroupEP extends ItemGroup {
    public static final ItemGroupEP instance = new ItemGroupEP(ItemGroup.GROUPS.length, "enderports");

    private ItemGroupEP(int index, String label) {
        super(index, label);
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(EnderPorts.enderportsIcon);
    }

}
