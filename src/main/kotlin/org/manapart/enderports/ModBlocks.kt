package org.manapart.enderports

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour

const val teleporterBlockId = "teleporter"

object ModBlocks {
    fun initialize() {}

    val TELEPORTER_BLOCK = register(teleporterBlockId, createTeleporterProps()) {
        Teleporter(it)
    }

    private fun register(
        name: String,
        settings: BlockBehaviour.Properties = BlockBehaviour.Properties.of(),
        shouldRegisterItem: Boolean = false,
        blockFactory: (BlockBehaviour.Properties) -> Block
    ): Block {
        val blockKey: ResourceKey<Block> = keyOfBlock(name)
        val block: Block = blockFactory(settings.setId(blockKey))

        if (shouldRegisterItem) {
            val itemKey = keyOfItem(name)

            val blockItem = BlockItem(block, Item.Properties().setId(itemKey).useBlockDescriptionPrefix())
            Registry.register(BuiltInRegistries.ITEM, itemKey, blockItem)
        }

        return Registry.register(BuiltInRegistries.BLOCK, blockKey, block)
    }

    private fun keyOfBlock(name: String): ResourceKey<Block> {
        return ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(MODID, name))
    }

    private fun keyOfItem(name: String): ResourceKey<Item> {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MODID, name))
    }
}
