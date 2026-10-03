package dev.liquidcatmofu.ueb.compat.draconic;

import appeng.api.storage.MEStorage;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon;
import dev.liquidcatmofu.ueb.api.CapabilityAttachUtil;
import dev.liquidcatmofu.ueb.compat.BlockEntityCompat;
import dev.liquidcatmofu.ueb.compat.appliedflux.AppliedFluxMEStorage;
import dev.liquidcatmofu.ueb.config.BridgeConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public final class DraconicAppliedFluxCompat implements BlockEntityCompat {
    @Override
    public void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        if (!BridgeConfig.DRACONIC_TO_APPLIED_FLUX.get()) {
            return;
        }
        if (event.getObject() instanceof TileEnergyPylon pylon) {
            DraconicUniversalEnergyStorage endpoint = new DraconicUniversalEnergyStorage(pylon.opAdapter);
            MEStorage storage = new AppliedFluxMEStorage(endpoint, Component.literal("Draconic Energy Core"));
            CapabilityAttachUtil.add(event, "draconic_applied_flux",
                    appeng.capabilities.Capabilities.STORAGE,
                    storage);
        }
    }
}
