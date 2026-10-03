package dev.liquidcatmofu.ueb.compat.draconic;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import dev.liquidcatmofu.ueb.api.IUniversalEnergyStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

/**
 * Lazy Universal Energy view over a BlockEntity's BrandonsCore OP capability.
 *
 * <p>The capability is resolved at operation time because Forge may attach capabilities
 * while a target BlockEntity is still inside its superclass constructor.</p>
 */
public final class DraconicCapabilityEnergyStorage implements IUniversalEnergyStorage {
    private final BlockEntity blockEntity;
    private final @Nullable Direction side;

    public DraconicCapabilityEnergyStorage(BlockEntity blockEntity, @Nullable Direction side) {
        this.blockEntity = blockEntity;
        this.side = side;
    }

    private @Nullable IOPStorage storage() {
        if (blockEntity.isRemoved()) {
            return null;
        }
        return blockEntity.getCapability(CapabilityOP.OP, side).resolve().orElse(null);
    }

    @Override
    public long insert(long amount, boolean simulate) {
        IOPStorage storage = storage();
        return amount <= 0 || storage == null || !storage.canReceive()
                ? 0
                : storage.receiveOP(amount, simulate);
    }

    @Override
    public long extract(long amount, boolean simulate) {
        IOPStorage storage = storage();
        return amount <= 0 || storage == null || !storage.canExtract()
                ? 0
                : storage.extractOP(amount, simulate);
    }

    @Override
    public long getStored() {
        IOPStorage storage = storage();
        return storage == null ? 0 : Math.max(0, storage.getOPStored());
    }

    @Override
    public long getCapacity() {
        IOPStorage storage = storage();
        return storage == null ? 0 : Math.max(0, storage.getMaxOPStored());
    }

    @Override
    public boolean canInsert() {
        IOPStorage storage = storage();
        return storage != null && storage.canReceive();
    }

    @Override
    public boolean canExtract() {
        IOPStorage storage = storage();
        return storage != null && storage.canExtract();
    }
}
