package dev.liquidcatmofu.ueb.compat.trashcans;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Version-tolerant Trash Cans target detection.
 *
 * <p>Capability gathering happens from the BlockEntity base constructor, so this deliberately
 * uses only constructor-safe identity: the supplied BlockState/Block and the registered
 * BlockEntityType. It never reads Trash Cans instance fields.</p>
 */
final class TrashCansSupport {
    private static final String MOD_ID = "trashcans";

    private static final String ENERGY_BLOCK = "energy_trash_can";
    private static final String ULTIMATE_BLOCK = "ultimate_trash_can";

    // Older 1.20 builds used dedicated BlockEntityType ids with a _tile suffix.
    private static final String ENERGY_TYPE = "energy_trash_can_tile";
    private static final String ULTIMATE_TYPE = "ultimate_trash_can_tile";

    private TrashCansSupport() {}

    static boolean isEnergySink(BlockEntity blockEntity) {
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(blockEntity.getBlockState().getBlock());
        if (isTrashCansId(blockId, ENERGY_BLOCK, ULTIMATE_BLOCK)) {
            return true;
        }

        ResourceLocation typeId = ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(blockEntity.getType());
        return isTrashCansId(typeId, ENERGY_TYPE, ULTIMATE_TYPE);
    }

    private static boolean isTrashCansId(ResourceLocation id, String first, String second) {
        if (id == null || !MOD_ID.equals(id.getNamespace())) {
            return false;
        }
        String path = id.getPath();
        return first.equals(path) || second.equals(path);
    }
}
