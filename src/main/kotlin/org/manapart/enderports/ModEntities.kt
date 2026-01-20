package org.manapart.enderports

import net.fabricmc.fabric.api.`object`.builder.v1.block.entity.FabricBlockEntityTypeBuilder
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType

object ModEntities {
    fun initialize() {}

    val ENDERPOT_BLOCK_ENTITY = register<TeleporterEntity>("teleporter_entity", ModBlocks.TELEPORTER_BLOCK) { pos, state ->
        TeleporterEntity(pos, state)
    }

    private fun <T : BlockEntity> register(
        name: String,
        block: Block,
        entityFactory: FabricBlockEntityTypeBuilder.Factory<out T>,
    ): BlockEntityType<T> {
        val id: Identifier = Identifier.fromNamespaceAndPath(MODID, name)
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.create(entityFactory, block).build())
    }

}
