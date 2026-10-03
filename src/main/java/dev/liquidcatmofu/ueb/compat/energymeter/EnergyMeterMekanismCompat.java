package dev.liquidcatmofu.ueb.compat.energymeter;

import com.github.almostreliable.energymeter.meter.MeterEntity;
import dev.liquidcatmofu.ueb.UniversalEnergyBridge;
import dev.liquidcatmofu.ueb.compat.BlockEntityCompat;
import dev.liquidcatmofu.ueb.config.BridgeConfig;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public final class EnergyMeterMekanismCompat implements BlockEntityCompat {
    @Override
    public void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        if (!BridgeConfig.ENERGY_METER_NATIVE_PASSTHROUGH.get() || !(event.getObject() instanceof MeterEntity meter)) {
            return;
        }

        EnergyMeterSidedCapabilityProvider<IStrictEnergyHandler> provider =
                new EnergyMeterSidedCapabilityProvider<>(meter, Capabilities.STRICT_ENERGY,
                        side -> new EnergyMeterStrictEnergyHandler(meter, side));
        event.addCapability(UniversalEnergyBridge.id("energy_meter_mekanism"), provider);
        event.addListener(provider::invalidate);
    }
}
