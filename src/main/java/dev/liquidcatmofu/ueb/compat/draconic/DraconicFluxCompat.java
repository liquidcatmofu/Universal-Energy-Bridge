package dev.liquidcatmofu.ueb.compat.draconic;

import com.brandon3055.draconicevolution.blocks.tileentity.TileCreativeOPCapacitor;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon;
import dev.liquidcatmofu.ueb.api.CapabilityAttachUtil;
import dev.liquidcatmofu.ueb.compat.BlockEntityCompat;
import dev.liquidcatmofu.ueb.compat.flux.UniversalToFluxEnergyStorage;
import dev.liquidcatmofu.ueb.config.BridgeConfig;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import sonar.fluxnetworks.api.FluxCapabilities;

public final class DraconicFluxCompat implements BlockEntityCompat {
    @Override
    public void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        if (!BridgeConfig.DRACONIC_TO_FLUX_NETWORKS.get()) {
            return;
        }

        if (event.getObject() instanceof TileEnergyPylon pylon) {
            DraconicUniversalEnergyStorage endpoint = new DraconicUniversalEnergyStorage(pylon);
            CapabilityAttachUtil.add(event, "draconic_flux_networks",
                    FluxCapabilities.FN_ENERGY_STORAGE,
                    new UniversalToFluxEnergyStorage(endpoint));
        } else if (event.getObject() instanceof TileCreativeOPCapacitor source) {
            // Resolve OP lazily: this event may fire before the DE constructor has populated capManager.
            CapabilityAttachUtil.addSided(event, "draconic_creative_flux_networks",
                    FluxCapabilities.FN_ENERGY_STORAGE,
                    side -> new UniversalToFluxEnergyStorage(
                            new DraconicCapabilityEnergyStorage(source, side)));
        }
    }
}
