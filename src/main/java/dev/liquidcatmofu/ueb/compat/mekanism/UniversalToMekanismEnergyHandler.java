package dev.liquidcatmofu.ueb.compat.mekanism;

import dev.liquidcatmofu.ueb.api.IUniversalEnergyStorage;
import dev.liquidcatmofu.ueb.api.SaturatingMath;
import mekanism.api.Action;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.math.FloatingLong;
import mekanism.common.util.UnitDisplayUtils.EnergyUnit;
import org.jetbrains.annotations.NotNull;

/** Mekanism Strict Energy view over the common FE-equivalent endpoint. */
public final class UniversalToMekanismEnergyHandler implements IStrictEnergyHandler {
    private final IUniversalEnergyStorage storage;

    public UniversalToMekanismEnergyHandler(IUniversalEnergyStorage storage) {
        this.storage = storage;
    }

    @Override
    public int getEnergyContainerCount() {
        return 1;
    }

    @Override
    public FloatingLong getEnergy(int container) {
        return container == 0 ? EnergyUnit.FORGE_ENERGY.convertFrom(storage.getStored()) : FloatingLong.ZERO;
    }

    @Override
    public void setEnergy(int container, FloatingLong energy) {
        if (container != 0) {
            return;
        }
        long target = EnergyUnit.FORGE_ENERGY.convertToAsLong(energy);
        long current = storage.getStored();
        if (target > current) {
            storage.insert(target - current, false);
        } else if (current > target) {
            storage.extract(current - target, false);
        }
    }

    @Override
    public FloatingLong getMaxEnergy(int container) {
        return container == 0 ? EnergyUnit.FORGE_ENERGY.convertFrom(storage.getCapacity()) : FloatingLong.ZERO;
    }

    @Override
    public FloatingLong getNeededEnergy(int container) {
        if (container != 0) {
            return FloatingLong.ZERO;
        }
        long needed = SaturatingMath.subtractFloorZero(storage.getCapacity(), storage.getStored());
        return EnergyUnit.FORGE_ENERGY.convertFrom(needed);
    }

    @Override
    public FloatingLong insertEnergy(int container, FloatingLong amount, @NotNull Action action) {
        if (container != 0 || amount.isZero()) {
            return amount;
        }
        long requestedFe = EnergyUnit.FORGE_ENERGY.convertToAsLong(amount);
        if (requestedFe <= 0) {
            return amount;
        }
        long insertedFe = storage.insert(requestedFe, action.simulate());
        return insertedFe <= 0 ? amount : amount.subtract(EnergyUnit.FORGE_ENERGY.convertFrom(insertedFe));
    }

    @Override
    public FloatingLong extractEnergy(int container, FloatingLong amount, @NotNull Action action) {
        if (container != 0 || amount.isZero()) {
            return FloatingLong.ZERO;
        }
        long requestedFe = EnergyUnit.FORGE_ENERGY.convertToAsLong(amount);
        if (requestedFe <= 0) {
            return FloatingLong.ZERO;
        }
        long extractedFe = storage.extract(requestedFe, action.simulate());
        return EnergyUnit.FORGE_ENERGY.convertFrom(extractedFe);
    }
}
