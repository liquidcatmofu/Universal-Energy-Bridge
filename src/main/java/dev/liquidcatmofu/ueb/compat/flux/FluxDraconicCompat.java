package dev.liquidcatmofu.ueb.compat.flux;

import com.brandon3055.brandonscore.capability.CapabilityOP;
import dev.liquidcatmofu.ueb.api.CapabilityAttachUtil;
import dev.liquidcatmofu.ueb.compat.BlockEntityCompat;
import dev.liquidcatmofu.ueb.compat.draconic.UniversalToOPStorage;
import dev.liquidcatmofu.ueb.config.BridgeConfig;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import sonar.fluxnetworks.common.device.TileFluxPlug;

/**
 * Gives Flux Plugs a BrandonsCore OP receive view backed by FN_ENERGY_STORAGE.
 *
 * <p>This is required for BrandonsCore/Draconic blocks that actively push energy:
 * BrandonsCore probes OP first and Forge Energy second. Without this view a Creative
 * Power Source falls back to Forge Energy and is limited to Integer.MAX_VALUE per call.</p>
 */
public final class FluxDraconicCompat implements BlockEntityCompat {
    @Override
    public void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        if (!BridgeConfig.FLUX_NETWORKS_TO_DRACONIC.get()) {
            return;
        }
        if (event.getObject() instanceof TileFluxPlug plug) {
            CapabilityAttachUtil.addSided(event, "flux_networks_draconic",
                    CapabilityOP.OP,
                    side -> new UniversalToOPStorage(new FluxUniversalEnergyStorage(plug, side)));
        }
    }
}
