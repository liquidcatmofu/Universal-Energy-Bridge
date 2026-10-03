package dev.liquidcatmofu.ueb.mixin.draconic;

import com.brandon3055.draconicevolution.lib.OPStorageOP;
import dev.liquidcatmofu.ueb.compat.draconic.exact.ExactEnergyCoreIO;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Records the actual accepted/extracted amount from Energy Core OPStorageOP calls.
 *
 * <p>Each individual call remains a signed long, but the UEB tracker accumulates
 * multiple calls in BigInteger so the per-tick total is not capped at Long.MAX_VALUE.</p>
 */
@Mixin(value = OPStorageOP.class, remap = false)
public abstract class OPStorageOPExactIOMixin {
    @Shadow @Final
    private BlockEntity tile;

    @Inject(method = "receiveOP", at = @At("RETURN"))
    private void ueb$recordReceive(long maxReceive, boolean simulate,
                                   CallbackInfoReturnable<Long> cir) {
        if (simulate || tile == null || tile.getLevel() == null) {
            return;
        }

        long received = cir.getReturnValue();
        if (received > 0 && tile instanceof ExactEnergyCoreIO exact) {
            exact.ueb$recordExactInput(tile.getLevel().getGameTime(), received);
        }
    }

    @Inject(method = "extractOP", at = @At("RETURN"))
    private void ueb$recordExtract(long maxExtract, boolean simulate,
                                   CallbackInfoReturnable<Long> cir) {
        if (simulate || tile == null || tile.getLevel() == null) {
            return;
        }

        long extracted = cir.getReturnValue();
        if (extracted > 0 && tile instanceof ExactEnergyCoreIO exact) {
            exact.ueb$recordExactOutput(tile.getLevel().getGameTime(), extracted);
        }
    }
}
