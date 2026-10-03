package dev.liquidcatmofu.ueb.compat.flux;

import dev.liquidcatmofu.ueb.api.CapabilityAttachUtil;
import dev.liquidcatmofu.ueb.api.UniversalEnergyCapabilities;
import dev.liquidcatmofu.ueb.compat.BlockEntityCompat;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import sonar.fluxnetworks.common.device.TileFluxPlug;
import sonar.fluxnetworks.common.device.TileFluxPoint;

/**
 * Exposes Flux Networks connector buffers through UEB's signed-long capability.
 *
 * <p>Flux Plug and Point already expose IFNEnergyStorage; this is only an additional
 * common view so Jade and other UEB consumers do not fall back to Forge Energy's int API.</p>
 */
public final class FluxUniversalCompat implements BlockEntityCompat {
    @Override
    public void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        if (event.getObject() instanceof TileFluxPlug plug) {
            CapabilityAttachUtil.addSided(event, "flux_plug_universal",
                    UniversalEnergyCapabilities.ENERGY,
                    side -> new FluxUniversalEnergyStorage(plug, side));
        } else if (event.getObject() instanceof TileFluxPoint point) {
            CapabilityAttachUtil.addSided(event, "flux_point_universal",
                    UniversalEnergyCapabilities.ENERGY,
                    side -> new FluxUniversalEnergyStorage(point, side));
        }
    }
}
