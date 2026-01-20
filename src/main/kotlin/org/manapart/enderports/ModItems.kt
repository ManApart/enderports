package org.manapart.enderports

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item

object ModItems {
    fun initialize() {}
    val TELEPORT_ITEM = register(teleporterBlockId) {
//        TeleporterItem(ModBlocks.TELEPORTER_BLOCK)
        Item(it)
    }

    fun <GenericItem : Item> register(name: String, settings: Item.Properties = Item.Properties(), itemFactory: (Item.Properties) -> GenericItem): GenericItem {
        val itemKey: ResourceKey<Item> = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MODID, name))
        val item: GenericItem = itemFactory(settings.setId(itemKey))
        Registry.register(BuiltInRegistries.ITEM, itemKey, item)

        return item
    }
}
