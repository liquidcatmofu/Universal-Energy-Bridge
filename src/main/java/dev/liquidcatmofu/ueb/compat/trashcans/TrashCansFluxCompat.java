package dev.liquidcatmofu.ueb.compat.trashcans;

import dev.liquidcatmofu.ueb.api.CapabilityAttachUtil;
import dev.liquidcatmofu.ueb.compat.BlockEntityCompat;
import dev.liquidcatmofu.ueb.compat.flux.UniversalToFluxEnergyStorage;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import sonar.fluxnetworks.api.FluxCapabilities;

/**
 * Exposes Trash Cans' stateless sink through Flux Networks' signed-long energy capability.
 */
public final class TrashCansFluxCompat implements BlockEntityCompat {
    @Override
    public void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        BlockEntity blockEntity = event.getObject();
        if (!TrashCansSupport.isEnergySink(blockEntity)) {
            return;
        }

        CapabilityAttachUtil.addSided(event, "trashcans_flux",
                FluxCapabilities.FN_ENERGY_STORAGE,
                side -> new UniversalToFluxEnergyStorage(
                        new TrashCanUniversalEnergyStorage(blockEntity, side)));
    }
}
