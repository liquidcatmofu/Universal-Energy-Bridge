package dev.liquidcatmofu.ueb.compat.trashcans;

import dev.liquidcatmofu.ueb.api.CapabilityAttachUtil;
import dev.liquidcatmofu.ueb.api.UniversalEnergyCapabilities;
import dev.liquidcatmofu.ueb.compat.BlockEntityCompat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Adds UEB's signed-long sink view to Trash Cans' Energy Trash Can and Ultimate Trash Can.
 *
 * <p>This intentionally identifies the target by its registered block-entity type instead of
 * reading Trash Cans fields. Forge gathers capabilities from the BlockEntity base constructor,
 * before Trash Cans has initialized its instance fields, and newer Trash Cans builds also keep
 * those fields private.</p>
 */
public final class TrashCansUniversalCompat implements BlockEntityCompat {
    private static final String MOD_ID = "trashcans";
    private static final String ENERGY_TYPE = "energy_trash_can_tile";
    private static final String ULTIMATE_TYPE = "ultimate_trash_can_tile";

    @Override
    public void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        BlockEntity blockEntity = event.getObject();
        ResourceLocation typeId = ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(blockEntity.getType());
        if (typeId == null || !MOD_ID.equals(typeId.getNamespace())) {
            return;
        }

        String path = typeId.getPath();
        if (!ENERGY_TYPE.equals(path) && !ULTIMATE_TYPE.equals(path)) {
            return;
        }

        CapabilityAttachUtil.addSided(event, "trashcans_universal",
                UniversalEnergyCapabilities.ENERGY,
                side -> new TrashCanUniversalEnergyStorage(blockEntity, side));
    }
}
