package dev.liquidcatmofu.ueb.compat.trashcans;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Version-tolerant Trash Cans target detection.
 *
 * <p>Capability gathering happens from the BlockEntity base constructor, so this deliberately
 * uses only the already-registered BlockEntityType and never reads Trash Cans instance fields.</p>
 */
final class TrashCansSupport {
    private static final String MOD_ID = "trashcans";
    private static final String ENERGY_TYPE = "energy_trash_can_tile";
    private static final String ULTIMATE_TYPE = "ultimate_trash_can_tile";

    private TrashCansSupport() {}

    static boolean isEnergySink(BlockEntity blockEntity) {
        ResourceLocation typeId = ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(blockEntity.getType());
        if (typeId == null || !MOD_ID.equals(typeId.getNamespace())) {
            return false;
        }
        String path = typeId.getPath();
        return ENERGY_TYPE.equals(path) || ULTIMATE_TYPE.equals(path);
    }
}
