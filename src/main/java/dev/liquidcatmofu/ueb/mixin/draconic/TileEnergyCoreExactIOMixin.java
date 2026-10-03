package dev.liquidcatmofu.ueb.mixin.draconic;

import com.brandon3055.brandonscore.lib.datamanager.DataFlags;
import com.brandon3055.brandonscore.lib.datamanager.ManagedString;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCore;
import dev.liquidcatmofu.ueb.compat.draconic.exact.ExactEnergyCoreIO;
import dev.liquidcatmofu.ueb.compat.draconic.exact.ExactEnergyIOTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Adds exact BigInteger I/O state to Draconic Evolution Energy Cores.
 */
@Mixin(value = TileEnergyCore.class, remap = false)
public abstract class TileEnergyCoreExactIOMixin implements ExactEnergyCoreIO {
    @Unique
    private ExactEnergyIOTracker ueb$exactIOTracker;

    @Unique
    private ManagedString ueb$exactInput;

    @Unique
    private ManagedString ueb$exactOutput;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void ueb$initExactIO(BlockPos pos, BlockState state, CallbackInfo ci) {
        TileEnergyCore core = (TileEnergyCore) (Object) this;
        ueb$exactIOTracker = new ExactEnergyIOTracker();
        ueb$exactInput = core.register(new ManagedString("ueb_exact_input", DataFlags.SYNC_CONTAINER));
        ueb$exactOutput = core.register(new ManagedString("ueb_exact_output", DataFlags.SYNC_CONTAINER));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void ueb$updateExactIO(CallbackInfo ci) {
        TileEnergyCore core = (TileEnergyCore) (Object) this;
        if (core.getLevel() == null || core.getLevel().isClientSide || ueb$exactIOTracker == null) {
            return;
        }

        long tick = core.getLevel().getGameTime();
        ueb$exactInput.set(ueb$exactIOTracker.averageInput(tick).toString());
        ueb$exactOutput.set(ueb$exactIOTracker.averageOutput(tick).toString());
    }

    @Override
    public void ueb$recordExactInput(long gameTick, long amount) {
        if (ueb$exactIOTracker != null) {
            ueb$exactIOTracker.recordInput(gameTick, amount);
        }
    }

    @Override
    public void ueb$recordExactOutput(long gameTick, long amount) {
        if (ueb$exactIOTracker != null) {
            ueb$exactIOTracker.recordOutput(gameTick, amount);
        }
    }

    @Override
    public String ueb$getExactInputPerTick() {
        return ueb$exactInput == null ? "" : ueb$exactInput.get();
    }

    @Override
    public String ueb$getExactOutputPerTick() {
        return ueb$exactOutput == null ? "" : ueb$exactOutput.get();
    }
}
