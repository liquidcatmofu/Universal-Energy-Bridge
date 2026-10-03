package dev.liquidcatmofu.ueb.compat.energymeter;

import com.github.almostreliable.energymeter.meter.MeterEntity;
import dev.liquidcatmofu.ueb.UniversalEnergyBridge;
import dev.liquidcatmofu.ueb.compat.BlockEntityCompat;
import dev.liquidcatmofu.ueb.config.BridgeConfig;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import sonar.fluxnetworks.api.FluxCapabilities;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;

public final class EnergyMeterFluxCompat implements BlockEntityCompat {
    @Override
    public void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        if (!BridgeConfig.ENERGY_METER_NATIVE_PASSTHROUGH.get() || !(event.getObject() instanceof MeterEntity meter)) {
            return;
        }

        EnergyMeterSidedCapabilityProvider<IFNEnergyStorage> provider =
                new EnergyMeterSidedCapabilityProvider<>(meter, FluxCapabilities.FN_ENERGY_STORAGE,
                        side -> new EnergyMeterFluxStorage(meter, side));
        event.addCapability(UniversalEnergyBridge.id("energy_meter_flux"), provider);
        event.addListener(provider::invalidate);
    }
}
