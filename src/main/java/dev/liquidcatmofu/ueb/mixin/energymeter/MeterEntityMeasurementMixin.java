package dev.liquidcatmofu.ueb.mixin.energymeter;

import dev.liquidcatmofu.ueb.compat.energymeter.EnergyMeterMeasurement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;

@Pseudo
@Mixin(targets = "com.github.almostreliable.energymeter.meter.MeterEntity", remap = false)
public abstract class MeterEntityMeasurementMixin implements EnergyMeterMeasurement {
    @Shadow
    private boolean setupDone;

    @Shadow
    private double averageRate;

    @Override
    public boolean ueb$isMeterReady() {
        return setupDone;
    }

    @Override
    public void ueb$recordTransferFe(double amount) {
        if (amount > 0 && Double.isFinite(amount)) {
            averageRate += amount;
        }
    }
}
