package dev.liquidcatmofu.ueb.compat.flux;

import dev.liquidcatmofu.ueb.api.IUniversalEnergyStorage;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;

public final class UniversalToFluxEnergyStorage implements IFNEnergyStorage {
    private final IUniversalEnergyStorage storage;

    public UniversalToFluxEnergyStorage(IUniversalEnergyStorage storage) {
        this.storage = storage;
    }

    @Override
    public long receiveEnergyL(long maxReceive, boolean simulate) {
        return storage.insert(maxReceive, simulate);
    }

    @Override
    public long extractEnergyL(long maxExtract, boolean simulate) {
        return storage.extract(maxExtract, simulate);
    }

    @Override
    public long getEnergyStoredL() {
        return storage.getStored();
    }

    @Override
    public long getMaxEnergyStoredL() {
        return storage.getCapacity();
    }

    @Override
    public boolean canExtract() {
        return storage.canExtract();
    }

    @Override
    public boolean canReceive() {
        return storage.canInsert();
    }
}
