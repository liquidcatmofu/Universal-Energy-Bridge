package dev.liquidcatmofu.ueb.api.endpoint;

import dev.liquidcatmofu.ueb.api.IUniversalEnergyStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Side-aware factory for a Universal signed-long endpoint.
 *
 * <p>Factories are invoked lazily on capability query, not during BlockEntity capability
 * attachment. This avoids reading subclass state while the BlockEntity superclass constructor
 * is still gathering capabilities.</p>
 */
@FunctionalInterface
public interface UniversalEndpointFactory {

    @Nullable
    IUniversalEnergyStorage create(BlockEntity blockEntity, @Nullable Direction side);
}
