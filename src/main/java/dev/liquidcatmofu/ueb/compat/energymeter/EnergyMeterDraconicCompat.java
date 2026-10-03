package dev.liquidcatmofu.ueb.compat.energymeter;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.github.almostreliable.energymeter.meter.MeterEntity;
import dev.liquidcatmofu.ueb.UniversalEnergyBridge;
import dev.liquidcatmofu.ueb.compat.BlockEntityCompat;
import dev.liquidcatmofu.ueb.config.BridgeConfig;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public final class EnergyMeterDraconicCompat implements BlockEntityCompat {
    @Override
    public void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        if (!BridgeConfig.ENERGY_METER_NATIVE_PASSTHROUGH.get() || !(event.getObject() instanceof MeterEntity meter)) {
            return;
        }

        EnergyMeterSidedCapabilityProvider<IOPStorage> provider =
                new EnergyMeterSidedCapabilityProvider<>(meter, CapabilityOP.OP,
                        side -> new EnergyMeterOPStorage(meter, side));
        event.addCapability(UniversalEnergyBridge.id("energy_meter_op"), provider);
        event.addListener(provider::invalidate);
    }
}
