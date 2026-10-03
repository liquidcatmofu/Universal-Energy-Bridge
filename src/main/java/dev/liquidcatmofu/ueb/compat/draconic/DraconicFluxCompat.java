package dev.liquidcatmofu.ueb.compat.draconic;

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
            DraconicUniversalEnergyStorage endpoint = new DraconicUniversalEnergyStorage(pylon.opAdapter);
            CapabilityAttachUtil.add(event, "draconic_flux_networks",
                    FluxCapabilities.FN_ENERGY_STORAGE,
                    new UniversalToFluxEnergyStorage(endpoint));
        }
    }
}
