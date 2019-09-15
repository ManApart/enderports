package org.manapart.enderports;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class TeleporterItem extends BlockItem {
    public TeleporterItem(Teleporter block) {
        super(block, new Item.Properties().group(ItemGroupEP.instance));
        setRegistryName("teleporteritem");
    }

}
