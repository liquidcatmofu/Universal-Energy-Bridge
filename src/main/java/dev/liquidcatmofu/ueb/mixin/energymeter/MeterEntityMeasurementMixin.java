package dev.liquidcatmofu.ueb.mixin.energymeter;

import dev.liquidcatmofu.ueb.compat.energymeter.EnergyMeterMeasurement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.github.almostreliable.energymeter.meter.MeterEntity", remap = false)
public abstract class MeterEntityMeasurementMixin implements EnergyMeterMeasurement {
    @Shadow
    private boolean setupDone;

    @Shadow
    private double averageRate;

    @Shadow
    private double transferRate;

    @Inject(method = "getTransferRate", at = @At("HEAD"), cancellable = true, remap = false)
    private void ueb$preserveHighTransferRate(CallbackInfoReturnable<Double> cir) {
        // Energy Meter rounds to three decimals via Math.round(transferRate * 1000).
        // Math.round(double) returns long, so rates above Long.MAX_VALUE / 1000
        // otherwise saturate at about 9.22 PFE/t before the formatter sees them.
        if (Math.abs(transferRate) > Long.MAX_VALUE / 1_000.0) {
            cir.setReturnValue(transferRate);
        }
    }

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
