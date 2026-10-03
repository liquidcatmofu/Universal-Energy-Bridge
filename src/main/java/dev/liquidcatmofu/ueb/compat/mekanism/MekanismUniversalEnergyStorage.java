package dev.liquidcatmofu.ueb.compat.mekanism;

import dev.liquidcatmofu.ueb.api.IUniversalEnergyStorage;
import dev.liquidcatmofu.ueb.api.SaturatingMath;
import mekanism.api.Action;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.math.FloatingLong;
import mekanism.common.util.UnitDisplayUtils.EnergyUnit;

/** Universal FE-equivalent view over Mekanism's native Joule/FloatingLong handler. */
public final class MekanismUniversalEnergyStorage implements IUniversalEnergyStorage {
    private final IStrictEnergyHandler storage;

    public MekanismUniversalEnergyStorage(IStrictEnergyHandler storage) {
        this.storage = storage;
    }

    @Override
    public long insert(long amount, boolean simulate) {
        if (amount <= 0) {
            return 0;
        }
        FloatingLong requested = EnergyUnit.FORGE_ENERGY.convertFrom(amount);
        FloatingLong remainder = storage.insertEnergy(requested, simulate ? Action.SIMULATE : Action.EXECUTE);
        return EnergyUnit.FORGE_ENERGY.convertToAsLong(requested.subtract(remainder));
    }

    @Override
    public long extract(long amount, boolean simulate) {
        if (amount <= 0) {
            return 0;
        }
        FloatingLong requested = EnergyUnit.FORGE_ENERGY.convertFrom(amount);
        FloatingLong extracted = storage.extractEnergy(requested, simulate ? Action.SIMULATE : Action.EXECUTE);
        return EnergyUnit.FORGE_ENERGY.convertToAsLong(extracted);
    }

    @Override
    public long getStored() {
        long result = 0;
        for (int i = 0; i < storage.getEnergyContainerCount(); i++) {
            result = SaturatingMath.add(result, EnergyUnit.FORGE_ENERGY.convertToAsLong(storage.getEnergy(i)));
        }
        return result;
    }

    @Override
    public long getCapacity() {
        long result = 0;
        for (int i = 0; i < storage.getEnergyContainerCount(); i++) {
            result = SaturatingMath.add(result, EnergyUnit.FORGE_ENERGY.convertToAsLong(storage.getMaxEnergy(i)));
        }
        return result;
    }
}
