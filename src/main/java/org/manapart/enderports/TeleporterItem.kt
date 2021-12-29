package org.manapart.enderports

import net.minecraft.world.item.BlockItem


class TeleporterItem(block: Teleporter) : BlockItem(block, Properties().tab(ItemGroupEP.instance))