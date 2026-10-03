package dev.liquidcatmofu.ueb.mixin.flux;

import dev.liquidcatmofu.ueb.config.BridgeConfig;
import dev.liquidcatmofu.ueb.util.SaturatingLongMath;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sonar.fluxnetworks.common.connection.FluxNetwork;
import sonar.fluxnetworks.common.connection.ServerFluxNetwork;
import sonar.fluxnetworks.common.connection.TransferHandler;
import sonar.fluxnetworks.common.device.TileFluxDevice;

/**
 * Recomputes Flux Networks' network-wide request limiter with saturating addition.
 *
 * <p>The original implementation sums every handler request with plain signed-long
 * addition. Native long-energy endpoints can make that sum exceed Long.MAX_VALUE,
 * wrap negative, and feed the wrapped value back into Flux Plug receive logic.</p>
 */
@Mixin(value = ServerFluxNetwork.class, remap = false)
public abstract class ServerFluxNetworkMixin {
    @Shadow private long mBufferLimiter;

    @Inject(method = "onEndServerTick", at = @At("TAIL"))
    private void ueb$recomputeBufferLimiter(CallbackInfo ci) {
        if (!BridgeConfig.FLUX_NETWORKS_OVERFLOW_GUARD.get()) {
            return;
        }

        ServerFluxNetwork self = (ServerFluxNetwork) (Object) this;
        long limiter = 0;
        for (TileFluxDevice device : self.getLogicalDevices(FluxNetwork.ANY)) {
            TransferHandler handler = device.getTransferHandler();
            limiter = SaturatingLongMath.add(limiter, handler.getRequest());
        }
        mBufferLimiter = limiter;
    }
}
