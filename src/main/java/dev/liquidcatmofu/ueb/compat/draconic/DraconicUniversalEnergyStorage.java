package dev.liquidcatmofu.ueb.compat.draconic;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon;
import dev.liquidcatmofu.ueb.api.IUniversalEnergyStorage;
import org.jetbrains.annotations.Nullable;

/**
 * Lazy view over a Draconic Evolution Energy Pylon.
 *
 * <p>Forge may fire AttachCapabilitiesEvent from the BlockEntity superclass constructor,
 * before TileEnergyPylon field initializers have assigned opAdapter. Keep the pylon itself
 * and resolve opAdapter only when the capability is actually used.</p>
 */
public final class DraconicUniversalEnergyStorage implements IUniversalEnergyStorage {
    private final TileEnergyPylon pylon;

    public DraconicUniversalEnergyStorage(TileEnergyPylon pylon) {
        this.pylon = pylon;
    }

    private @Nullable IOPStorage storage() {
        return pylon.opAdapter;
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
