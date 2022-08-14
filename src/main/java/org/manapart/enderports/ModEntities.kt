package org.manapart.enderports

import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import org.manapart.enderports.ModBlocks.TELEPORTER_BLOCK
import thedarkcolour.kotlinforforge.forge.registerObject

object ModEntities {

    val REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID)

    val ENDERPOT_BLOCK_ENTITY by REGISTRY.registerObject("teleporter_entity") {
        createEntityType()
    }

}

private fun createEntityType(): BlockEntityType<TeleporterEntity> {
    return BlockEntityType.Builder.of({ pos, state -> TeleporterEntity(pos, state) }, TELEPORTER_BLOCK)
        .build(null)
}
