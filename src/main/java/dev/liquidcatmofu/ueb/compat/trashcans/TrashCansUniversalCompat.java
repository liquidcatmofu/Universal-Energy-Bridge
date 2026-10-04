package dev.liquidcatmofu.ueb.compat.trashcans;

import dev.liquidcatmofu.ueb.api.CapabilityAttachUtil;
import dev.liquidcatmofu.ueb.api.UniversalEnergyCapabilities;
import dev.liquidcatmofu.ueb.compat.BlockEntityCompat;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;

/**
 * Adds UEB's signed-long sink view to Trash Cans' Energy Trash Can and Ultimate Trash Can.
 */
public final class TrashCansUniversalCompat implements BlockEntityCompat {
    @Override
    public void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        BlockEntity blockEntity = event.getObject();
        if (!TrashCansSupport.isEnergySink(blockEntity)) {
            return;
        }

        CapabilityAttachUtil.addSided(event, "trashcans_universal",
                UniversalEnergyCapabilities.ENERGY,
                side -> new TrashCanUniversalEnergyStorage(blockEntity, side));
    }
}
