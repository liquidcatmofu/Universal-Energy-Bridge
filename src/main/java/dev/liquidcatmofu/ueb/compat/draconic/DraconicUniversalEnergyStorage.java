package dev.liquidcatmofu.ueb.compat.draconic;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import dev.liquidcatmofu.ueb.api.IUniversalEnergyStorage;

public final class DraconicUniversalEnergyStorage implements IUniversalEnergyStorage {
    private final IOPStorage storage;

    public DraconicUniversalEnergyStorage(IOPStorage storage) {
        this.storage = storage;
    }

    @Override
    public long insert(long amount, boolean simulate) {
        return amount <= 0 || !storage.canReceive() ? 0 : storage.receiveOP(amount, simulate);
    }

    @Override
    public long extract(long amount, boolean simulate) {
        return amount <= 0 || !storage.canExtract() ? 0 : storage.extractOP(amount, simulate);
    }

    @Override
    public long getStored() {
        return Math.max(0, storage.getOPStored());
    }

    @Override
    public long getCapacity() {
        return Math.max(0, storage.getMaxOPStored());
    }

    @Override
    public boolean canInsert() {
        return storage.canReceive();
    }

    @Override
    public boolean canExtract() {
        return storage.canExtract();
    }
}
