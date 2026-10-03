package dev.liquidcatmofu.ueb.mixin.flux;

import dev.liquidcatmofu.ueb.config.BridgeConfig;
import it.unimi.dsi.fastutil.longs.LongList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sonar.fluxnetworks.common.connection.FluxNetwork;
import sonar.fluxnetworks.common.connection.NetworkStatistics;
import sonar.fluxnetworks.common.device.TileFluxDevice;

import java.util.List;

/**
 * Keeps Flux Networks' statistics in the signed-long domain without allowing
 * intermediate accumulator overflow.
 *
 * <p>The original implementation sums four 5-tick samples and divides by four,
 * which can overflow even when the resulting per-tick average is still
 * representable. UEB accumulates quotient/remainder pairs instead, so ordinary
 * values remain exact and only genuinely unrepresentable results saturate.</p>
 */
@Mixin(value = NetworkStatistics.class, remap = false)
public abstract class NetworkStatisticsMixin {

    @Shadow @Final
    private FluxNetwork network;

    @Shadow public int fluxPlugCount;
    @Shadow public int fluxPointCount;
    @Shadow public int fluxControllerCount;
    @Shadow public int fluxStorageCount;

    @Shadow public long energyInput;
    @Shadow public long energyOutput;
    @Shadow public long totalBuffer;
    @Shadow public long totalEnergy;

    @Shadow @Final
    public LongList energyChange;

    @Shadow public int averageTickMicro;
    @Shadow private long runningTotalNano;

    @Unique private long ueb$inputAverage;
    @Unique private long ueb$inputRemainder;
    @Unique private long ueb$outputAverage;
    @Unique private long ueb$outputRemainder;

    @Unique private long ueb$changeAverage;
    @Unique private long ueb$changeRemainder;

    @Inject(method = "weakTick", at = @At("HEAD"), cancellable = true)
    private void ueb$overflowSafeWeakTick(CallbackInfo ci) {
        if (!BridgeConfig.FLUX_NETWORKS_OVERFLOW_GUARD.get()) {
            return;
        }

        List<TileFluxDevice> plugs = network.getLogicalDevices(FluxNetwork.PLUG);
        for (TileFluxDevice plug : plugs) {
            if (!plug.getDeviceType().isStorage()) {
                long change = plug.getTransferChange();
                // Plug input should never be negative. If its own per-tick counter
                // already wrapped, the only safe representable statistic is saturation.
                ueb$addInput(change < 0 ? Long.MAX_VALUE : change);
            }
        }

        List<TileFluxDevice> points = network.getLogicalDevices(FluxNetwork.POINT);
        for (TileFluxDevice point : points) {
            if (!point.getDeviceType().isStorage()) {
                long change = point.getTransferChange();
                long amount;
                if (change == Long.MIN_VALUE) {
                    amount = Long.MAX_VALUE;
                } else if (change < 0) {
                    amount = -change;
                } else {
                    amount = 0;
                }
                ueb$addOutput(amount);
            }
        }

        ci.cancel();
    }

    @Inject(method = "weakerTick", at = @At("HEAD"), cancellable = true)
    private void ueb$overflowSafeWeakerTick(CallbackInfo ci) {
        if (!BridgeConfig.FLUX_NETWORKS_OVERFLOW_GUARD.get()) {
            return;
        }

        totalBuffer = 0;
        totalEnergy = 0;

        List<TileFluxDevice> devices = network.getLogicalDevices(FluxNetwork.ANY);
        for (TileFluxDevice device : devices) {
            if (!device.getDeviceType().isStorage()) {
                totalBuffer = ueb$saturatingAdd(totalBuffer, device.getTransferBuffer());
            }
        }

        List<TileFluxDevice> storages = network.getLogicalDevices(FluxNetwork.STORAGE);
        for (TileFluxDevice storage : storages) {
            totalEnergy = ueb$saturatingAdd(totalEnergy, storage.getTransferBuffer());
        }

        fluxControllerCount = network.getLogicalDevices(FluxNetwork.CONTROLLER).size();
        fluxStorageCount = storages.size();
        fluxPlugCount = network.getLogicalDevices(FluxNetwork.PLUG).size() - fluxStorageCount;
        fluxPointCount = network.getLogicalDevices(FluxNetwork.POINT).size() - fluxStorageCount - fluxControllerCount;

        energyInput = ueb$inputAverage;
        energyOutput = ueb$outputAverage;
        ueb$inputAverage = 0;
        ueb$inputRemainder = 0;
        ueb$outputAverage = 0;
        ueb$outputRemainder = 0;

        ueb$addChange(Math.max(energyInput, energyOutput));

        averageTickMicro = (int) Math.min(runningTotalNano / 20000, Integer.MAX_VALUE);
        runningTotalNano = 0;

        ci.cancel();
    }

    @Inject(method = "weakestTick", at = @At("HEAD"), cancellable = true)
    private void ueb$overflowSafeWeakestTick(CallbackInfo ci) {
        if (!BridgeConfig.FLUX_NETWORKS_OVERFLOW_GUARD.get()) {
            return;
        }

        for (int i = 1; i < NetworkStatistics.CHANGE_COUNT; i++) {
            energyChange.set(i - 1, energyChange.getLong(i));
        }
        energyChange.set(NetworkStatistics.CHANGE_COUNT - 1, ueb$changeAverage);
        ueb$changeAverage = 0;
        ueb$changeRemainder = 0;

        ci.cancel();
    }

    @Unique
    private void ueb$addInput(long amount) {
        if (amount <= 0) {
            return;
        }
        ueb$inputAverage = ueb$saturatingAddPositive(ueb$inputAverage, amount / 4);
        ueb$inputRemainder += amount % 4;
        ueb$inputAverage = ueb$saturatingAddPositive(ueb$inputAverage, ueb$inputRemainder / 4);
        ueb$inputRemainder %= 4;
    }

    @Unique
    private void ueb$addOutput(long amount) {
        if (amount <= 0) {
            return;
        }
        ueb$outputAverage = ueb$saturatingAddPositive(ueb$outputAverage, amount / 4);
        ueb$outputRemainder += amount % 4;
        ueb$outputAverage = ueb$saturatingAddPositive(ueb$outputAverage, ueb$outputRemainder / 4);
        ueb$outputRemainder %= 4;
    }

    @Unique
    private void ueb$addChange(long amount) {
        if (amount <= 0) {
            return;
        }
        ueb$changeAverage = ueb$saturatingAddPositive(ueb$changeAverage, amount / 5);
        ueb$changeRemainder += amount % 5;
        ueb$changeAverage = ueb$saturatingAddPositive(ueb$changeAverage, ueb$changeRemainder / 5);
        ueb$changeRemainder %= 5;
    }

    @Unique
    private static long ueb$saturatingAddPositive(long a, long b) {
        if (b <= 0 || a == Long.MAX_VALUE) {
            return a;
        }
        return a > Long.MAX_VALUE - b ? Long.MAX_VALUE : a + b;
    }

    @Unique
    private static long ueb$saturatingAdd(long a, long b) {
        if (b > 0 && a > Long.MAX_VALUE - b) {
            return Long.MAX_VALUE;
        }
        if (b < 0 && a < Long.MIN_VALUE - b) {
            return Long.MIN_VALUE;
        }
        return a + b;
    }
}
