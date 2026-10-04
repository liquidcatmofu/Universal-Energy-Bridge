package dev.liquidcatmofu.ueb.mixin.trashcans;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Makes Forge-attached capabilities visible on Trash Cans versions whose custom
 * getCapability implementation does not delegate unknown capabilities to BlockEntity.
 *
 * <p>The original Trash Cans result always wins. UEB only falls back to the inherited
 * capability dispatcher when Trash Cans itself returned an empty LazyOptional, so item,
 * fluid and Forge Energy behavior remains owned by Trash Cans.</p>
 */
@Pseudo
@Mixin(targets = "com.supermartijn642.trashcans.TrashCanBlockEntity", remap = false)
public abstract class TrashCanCapabilityFallbackMixin extends BlockEntity {

    protected TrashCanCapabilityFallbackMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(
            method = "getCapability",
            at = @At("RETURN"),
            cancellable = true,
            require = 0,
            remap = false
    )
    private <T> void ueb$exposeForgeAttachedCapabilities(
            @NotNull Capability<T> capability,
            @Nullable Direction side,
            CallbackInfoReturnable<LazyOptional<T>> cir) {
        LazyOptional<T> original = cir.getReturnValue();
        if (original != null && original.isPresent()) {
            return;
        }

        LazyOptional<T> attached = super.getCapability(capability, side);
        if (attached.isPresent()) {
            cir.setReturnValue(attached);
        }
    }
}
