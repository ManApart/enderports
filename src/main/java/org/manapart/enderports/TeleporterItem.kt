package org.manapart.enderports

import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab


class TeleporterItem(block: Teleporter) : BlockItem(block, Properties().tab(CreativeModeTab.TAB_MISC))