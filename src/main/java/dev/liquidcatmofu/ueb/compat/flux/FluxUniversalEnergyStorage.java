package dev.liquidcatmofu.ueb.compat.flux;

import dev.liquidcatmofu.ueb.api.IUniversalEnergyStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import sonar.fluxnetworks.api.FluxCapabilities;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;

/**
 * Lazy Universal Energy view over Flux Networks' signed-long energy capability.
 */
public final class FluxUniversalEnergyStorage implements IUniversalEnergyStorage {
    private final BlockEntity blockEntity;
    private final @Nullable Direction side;

    public FluxUniversalEnergyStorage(BlockEntity blockEntity, @Nullable Direction side) {
        this.blockEntity = blockEntity;
        this.side = side;
    }

    private @Nullable IFNEnergyStorage storage() {
        if (blockEntity.isRemoved()) {
            return null;
        }
        return blockEntity.getCapability(FluxCapabilities.FN_ENERGY_STORAGE, side)
                .resolve()
                .orElse(null);
    }

    @Override
    public long insert(long amount, boolean simulate) {
        IFNEnergyStorage storage = storage();
        return amount <= 0 || storage == null || !storage.canReceive()
                ? 0
                : storage.receiveEnergyL(amount, simulate);
    }

    @Override
    public long extract(long amount, boolean simulate) {
        IFNEnergyStorage storage = storage();
        return amount <= 0 || storage == null || !storage.canExtract()
                ? 0
                : storage.extractEnergyL(amount, simulate);
    }

    @Override
    public long getStored() {
        IFNEnergyStorage storage = storage();
        return storage == null ? 0 : Math.max(0, storage.getEnergyStoredL());
    }

    @Override
    public long getCapacity() {
        IFNEnergyStorage storage = storage();
        return storage == null ? 0 : Math.max(0, storage.getMaxEnergyStoredL());
    }

    @Override
    public boolean canInsert() {
        IFNEnergyStorage storage = storage();
        return storage != null && storage.canReceive();
    }

    @Override
    public boolean canExtract() {
        IFNEnergyStorage storage = storage();
        return storage != null && storage.canExtract();
    }
}
