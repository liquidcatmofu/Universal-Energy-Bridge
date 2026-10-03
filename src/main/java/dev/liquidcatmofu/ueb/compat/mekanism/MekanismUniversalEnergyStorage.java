package dev.liquidcatmofu.ueb.compat.mekanism;

import dev.liquidcatmofu.ueb.api.IUniversalEnergyStorage;
import dev.liquidcatmofu.ueb.api.SaturatingMath;
import mekanism.api.Action;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.util.UnitDisplayUtils.EnergyUnit;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Universal FE-equivalent view over Mekanism's native Joule/FloatingLong handler.
 *
 * <p>When constructed from a BlockEntity, the Strict Energy capability is resolved on
 * every operation. This is intentional: Mekanism multiblocks and sided configurations
 * can invalidate/recreate their energy handlers as structures form or modes change.</p>
 */
public final class MekanismUniversalEnergyStorage implements IUniversalEnergyStorage {
    private final @Nullable IStrictEnergyHandler fixedStorage;
    private final @Nullable BlockEntity blockEntity;
    private final @Nullable Direction side;

    public MekanismUniversalEnergyStorage(IStrictEnergyHandler storage) {
        this.fixedStorage = storage;
        this.blockEntity = null;
        this.side = null;
    }

    public MekanismUniversalEnergyStorage(BlockEntity blockEntity, @Nullable Direction side) {
        this.fixedStorage = null;
        this.blockEntity = blockEntity;
        this.side = side;
    }

    private @Nullable IStrictEnergyHandler storage() {
        if (fixedStorage != null) {
            return fixedStorage;
        }
        if (blockEntity == null || blockEntity.isRemoved()) {
            return null;
        }
        return blockEntity.getCapability(Capabilities.STRICT_ENERGY, side)
                .resolve()
                .orElse(null);
    }

    @Override
    public long insert(long amount, boolean simulate) {
        if (amount <= 0) {
            return 0;
        }
        IStrictEnergyHandler storage = storage();
        if (storage == null) {
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
        IStrictEnergyHandler storage = storage();
        if (storage == null) {
            return 0;
        }
        FloatingLong requested = EnergyUnit.FORGE_ENERGY.convertFrom(amount);
        FloatingLong extracted = storage.extractEnergy(requested, simulate ? Action.SIMULATE : Action.EXECUTE);
        return EnergyUnit.FORGE_ENERGY.convertToAsLong(extracted);
    }

    @Override
    public long getStored() {
        IStrictEnergyHandler storage = storage();
        if (storage == null) {
            return 0;
        }
        long result = 0;
        for (int i = 0; i < storage.getEnergyContainerCount(); i++) {
            result = SaturatingMath.add(result, EnergyUnit.FORGE_ENERGY.convertToAsLong(storage.getEnergy(i)));
        }
        return result;
    }

    @Override
    public long getCapacity() {
        IStrictEnergyHandler storage = storage();
        if (storage == null) {
            return 0;
        }
        long result = 0;
        for (int i = 0; i < storage.getEnergyContainerCount(); i++) {
            result = SaturatingMath.add(result, EnergyUnit.FORGE_ENERGY.convertToAsLong(storage.getMaxEnergy(i)));
        }
        return result;
    }
}
