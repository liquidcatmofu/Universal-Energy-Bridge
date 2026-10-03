package dev.liquidcatmofu.ueb.compat.draconic;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import dev.liquidcatmofu.ueb.api.IUniversalEnergyStorage;

public final class UniversalToOPStorage implements IOPStorage {
    private final IUniversalEnergyStorage storage;

    public UniversalToOPStorage(IUniversalEnergyStorage storage) {
        this.storage = storage;
    }

    @Override
    public long receiveOP(long maxReceive, boolean simulate) {
        return storage.insert(maxReceive, simulate);
    }

    @Override
    public long extractOP(long maxExtract, boolean simulate) {
        return storage.extract(maxExtract, simulate);
    }

    @Override
    public long getOPStored() {
        return storage.getStored();
    }

    @Override
    public long getMaxOPStored() {
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

    @Override
    public long modifyEnergyStored(long amount) {
        if (amount >= 0) {
            return storage.insert(amount, false);
        }
        long requested = amount == Long.MIN_VALUE ? Long.MAX_VALUE : -amount;
        return storage.extract(requested, false);
    }
}
