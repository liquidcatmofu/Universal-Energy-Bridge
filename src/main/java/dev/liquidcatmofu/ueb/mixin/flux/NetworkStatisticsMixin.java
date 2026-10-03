package dev.liquidcatmofu.ueb.mixin.flux;

import dev.liquidcatmofu.ueb.config.BridgeConfig;
import dev.liquidcatmofu.ueb.util.SaturatingLongMath;
import it.unimi.dsi.fastutil.longs.LongList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sonar.fluxnetworks.common.connection.FluxNetwork;
import sonar.fluxnetworks.common.connection.NetworkStatistics;
import sonar.fluxnetworks.common.device.TileFluxDevice;

import java.util.List;

/**
 * Prevents Flux Networks' statistics accumulators from wrapping signed long values
 * when native long-energy endpoints move extreme amounts of energy.
 */
@Mixin(value = NetworkStatistics.class, remap = false)
public abstract class NetworkStatisticsMixin {
    @Shadow @Final private FluxNetwork network;

    @Shadow public int fluxPlugCount;
    @Shadow public int fluxPointCount;
    @Shadow public int fluxControllerCount;
    @Shadow public int fluxStorageCount;

    @Shadow public long energyInput;
    @Shadow public long energyOutput;

    @Shadow @Final public LongList energyChange;

    @Shadow public long totalBuffer;
    @Shadow public long totalEnergy;

    @Shadow private long energyChange5;
    @Shadow private long energyInput4;
    @Shadow private long energyOutput4;

    @Shadow public int averageTickMicro;
    @Shadow private long runningTotalNano;

    @Inject(method = "weakTick", at = @At("HEAD"), cancellable = true)
    private void ueb$weakTick(CallbackInfo ci) {
        if (!BridgeConfig.FLUX_NETWORKS_OVERFLOW_GUARD.get()) {
            return;
        }

        List<TileFluxDevice> plugs = network.getLogicalDevices(FluxNetwork.PLUG);
        for (TileFluxDevice plug : plugs) {
            if (!plug.getDeviceType().isStorage()) {
                energyInput4 = SaturatingLongMath.add(energyInput4, plug.getTransferChange());
            }
        }

        List<TileFluxDevice> points = network.getLogicalDevices(FluxNetwork.POINT);
        for (TileFluxDevice point : points) {
            if (!point.getDeviceType().isStorage()) {
                energyOutput4 = SaturatingLongMath.subtract(energyOutput4, point.getTransferChange());
            }
        }

        ci.cancel();
    }

    @Inject(method = "weakerTick", at = @At("HEAD"), cancellable = true)
    private void ueb$weakerTick(CallbackInfo ci) {
        if (!BridgeConfig.FLUX_NETWORKS_OVERFLOW_GUARD.get()) {
            return;
        }

        totalBuffer = 0;
        totalEnergy = 0;

        List<TileFluxDevice> devices = network.getLogicalDevices(FluxNetwork.ANY);
        for (TileFluxDevice device : devices) {
            if (!device.getDeviceType().isStorage()) {
                totalBuffer = SaturatingLongMath.add(totalBuffer, device.getTransferBuffer());
            }
        }

        List<TileFluxDevice> storages = network.getLogicalDevices(FluxNetwork.STORAGE);
        for (TileFluxDevice storage : storages) {
            totalEnergy = SaturatingLongMath.add(totalEnergy, storage.getTransferBuffer());
        }

        fluxControllerCount = network.getLogicalDevices(FluxNetwork.CONTROLLER).size();
        fluxStorageCount = storages.size();
        fluxPlugCount = network.getLogicalDevices(FluxNetwork.PLUG).size() - fluxStorageCount;
        fluxPointCount = network.getLogicalDevices(FluxNetwork.POINT).size() - fluxStorageCount - fluxControllerCount;

        energyInput = SaturatingLongMath.dividePreservingSaturation(energyInput4, 4);
        energyOutput = SaturatingLongMath.dividePreservingSaturation(energyOutput4, 4);
        energyInput4 = 0;
        energyOutput4 = 0;

        energyChange5 = SaturatingLongMath.add(energyChange5, Math.max(energyInput, energyOutput));

        averageTickMicro = (int) Math.min(runningTotalNano / 20_000, Integer.MAX_VALUE);
        runningTotalNano = 0;

        ci.cancel();
    }

    @Inject(method = "weakestTick", at = @At("HEAD"), cancellable = true)
    private void ueb$weakestTick(CallbackInfo ci) {
        if (!BridgeConfig.FLUX_NETWORKS_OVERFLOW_GUARD.get()) {
            return;
        }

        for (int i = 1; i < NetworkStatistics.CHANGE_COUNT; i++) {
            energyChange.set(i - 1, energyChange.getLong(i));
        }
        energyChange.set(
                NetworkStatistics.CHANGE_COUNT - 1,
                SaturatingLongMath.dividePreservingSaturation(energyChange5, 5)
        );
        energyChange5 = 0;

        ci.cancel();
    }
}
