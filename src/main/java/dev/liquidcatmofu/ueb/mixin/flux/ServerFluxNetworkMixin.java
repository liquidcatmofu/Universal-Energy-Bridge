package dev.liquidcatmofu.ueb.mixin.flux;

import dev.liquidcatmofu.ueb.config.BridgeConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sonar.fluxnetworks.common.connection.FluxNetwork;
import sonar.fluxnetworks.common.connection.ServerFluxNetwork;
import sonar.fluxnetworks.common.connection.TransferHandler;
import sonar.fluxnetworks.common.device.TileFluxDevice;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * Recomputes the network request limiter with saturating addition.
 *
 * <p>The upstream implementation performs a plain {@code limiter += request}.
 * Once that wraps negative, Flux Plug receive calls inherit the invalid limiter.
 * Recomputing at the end of the network cycle is sufficient because the limiter
 * is consumed by external receives during the following cycle.</p>
 */
@Mixin(value = ServerFluxNetwork.class, remap = false)
public abstract class ServerFluxNetworkMixin {

    @Shadow
    private long mBufferLimiter;

    @Shadow @Nonnull
    public abstract ArrayList<TileFluxDevice> getLogicalDevices(int logic);

    @Inject(method = "onEndServerTick", at = @At("TAIL"))
    private void ueb$repairBufferLimiter(CallbackInfo ci) {
        if (!BridgeConfig.FLUX_NETWORKS_OVERFLOW_GUARD.get()) {
            return;
        }

        long limiter = 0;
        for (TileFluxDevice device : getLogicalDevices(FluxNetwork.ANY)) {
            TransferHandler handler = device.getTransferHandler();
            long request = handler.getRequest();
            if (request <= 0) {
                continue;
            }
            limiter = ueb$saturatingAddPositive(limiter, request);
            if (limiter == Long.MAX_VALUE) {
                break;
            }
        }
        mBufferLimiter = limiter;
    }

    @Unique
    private static long ueb$saturatingAddPositive(long a, long b) {
        if (b <= 0 || a == Long.MAX_VALUE) {
            return a;
        }
        return a > Long.MAX_VALUE - b ? Long.MAX_VALUE : a + b;
    }
}
