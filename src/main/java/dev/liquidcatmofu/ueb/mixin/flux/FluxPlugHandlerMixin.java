package dev.liquidcatmofu.ueb.mixin.flux;

import dev.liquidcatmofu.ueb.config.BridgeConfig;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sonar.fluxnetworks.common.device.FluxConnectorHandler;
import sonar.fluxnetworks.common.device.FluxPlugHandler;
import sonar.fluxnetworks.common.device.SideTransfer;

import javax.annotation.Nonnull;

/**
 * Hardens Flux Plug's signed-long buffer arithmetic.
 *
 * <p>Flux Networks 1.20 computes receive room as
 * {@code min(limit, bufferLimiter - buffer) - buffer}, effectively subtracting the
 * existing buffer twice whenever the network limiter wins. At signed-long scale this
 * can stop a Plug even though the network still requests energy. This mixin restores
 * the intended {@code min(limit, bufferLimiter) - buffer} behavior and keeps removal
 * bookkeeping non-negative.</p>
 */
@Mixin(value = FluxPlugHandler.class, remap = false)
public abstract class FluxPlugHandlerMixin extends FluxConnectorHandler {

    @Shadow
    private long mReceived;

    @Shadow
    private long mRemoved;

    @Inject(method = "receive", at = @At("HEAD"), cancellable = true)
    private void ueb$safeReceive(long maxReceive,
                                 @Nonnull Direction side,
                                 boolean simulate,
                                 long bufferLimiter,
                                 CallbackInfoReturnable<Long> cir) {
        if (!BridgeConfig.FLUX_NETWORKS_OVERFLOW_GUARD.get()) {
            return;
        }

        ueb$repairBuffer();

        if (maxReceive <= 0 || bufferLimiter <= 0) {
            cir.setReturnValue(0L);
            return;
        }

        long capacity = Math.min(getLimit(), bufferLimiter);
        if (capacity <= mBuffer) {
            cir.setReturnValue(0L);
            return;
        }

        long op = Math.min(capacity - mBuffer, maxReceive);
        if (op <= 0) {
            cir.setReturnValue(0L);
            return;
        }

        if (!simulate) {
            mBuffer += op;
            mReceived = ueb$saturatingAddPositive(mReceived, op);

            SideTransfer transfer = mTransfers[side.get3DDataValue()];
            if (transfer != null) {
                transfer.receive(op);
            }
        }

        cir.setReturnValue(op);
    }

    @Inject(method = "removeFromBuffer", at = @At("HEAD"), cancellable = true)
    private void ueb$safeRemove(long energy, CallbackInfoReturnable<Long> cir) {
        if (!BridgeConfig.FLUX_NETWORKS_OVERFLOW_GUARD.get()) {
            return;
        }

        ueb$repairBuffer();

        if (energy <= 0 || mBuffer <= 0) {
            cir.setReturnValue(0L);
            return;
        }

        long limit = getLimit();
        long remainingLimit = mRemoved >= limit ? 0 : limit - mRemoved;
        long op = Math.min(Math.min(energy, mBuffer), remainingLimit);

        if (op <= 0) {
            cir.setReturnValue(0L);
            return;
        }

        mBuffer -= op;
        mRemoved += op;
        cir.setReturnValue(op);
    }

    @Inject(method = "onCycleEnd", at = @At("HEAD"))
    private void ueb$repairCorruptedState(CallbackInfo ci) {
        if (!BridgeConfig.FLUX_NETWORKS_OVERFLOW_GUARD.get()) {
            return;
        }

        ueb$repairBuffer();
        if (mReceived < 0) {
            mReceived = Long.MAX_VALUE;
        }
        if (mRemoved < 0) {
            mRemoved = Long.MAX_VALUE;
        }
    }

    private void ueb$repairBuffer() {
        if (mBuffer < 0) {
            mBuffer = 0;
        }
        if (mReceived < 0) {
            mReceived = Long.MAX_VALUE;
        }
        if (mRemoved < 0) {
            mRemoved = Long.MAX_VALUE;
        }
    }

    private static long ueb$saturatingAddPositive(long a, long b) {
        if (a < 0 || b <= 0 || a == Long.MAX_VALUE) {
            return a < 0 ? Long.MAX_VALUE : a;
        }
        return a > Long.MAX_VALUE - b ? Long.MAX_VALUE : a + b;
    }
}
