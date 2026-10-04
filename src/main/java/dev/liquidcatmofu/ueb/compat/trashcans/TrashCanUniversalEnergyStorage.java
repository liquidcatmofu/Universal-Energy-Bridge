package dev.liquidcatmofu.ueb.compat.trashcans;

import com.supermartijn642.trashcans.TrashCanBlockEntity;
import dev.liquidcatmofu.ueb.api.IUniversalEnergyStorage;

/**
 * Signed-long sink view for Trash Cans' energy-capable trash cans.
 *
 * <p>Trash Cans intentionally discards accepted energy and exposes no extraction. When its
 * optional transfer limit is disabled, the original Forge Energy implementation accepts the
 * entire int request; this adapter preserves that "unlimited sink" intent without Forge
 * Energy's signed-int request-width ceiling. When the limit is enabled, the configured
 * Trash Cans limit is preserved exactly.</p>
 */
public final class TrashCanUniversalEnergyStorage implements IUniversalEnergyStorage {
    private final TrashCanBlockEntity trashCan;

    public TrashCanUniversalEnergyStorage(TrashCanBlockEntity trashCan) {
        this.trashCan = trashCan;
    }

    @Override
    public long insert(long amount, boolean simulate) {
        if (amount <= 0) {
            return 0;
        }
        if (!trashCan.useEnergyLimit) {
            return amount;
        }
        return Math.min(amount, Math.max(0L, trashCan.energyLimit));
    }

    @Override
    public long extract(long amount, boolean simulate) {
        return 0;
    }

    @Override
    public long getStored() {
        return 0;
    }

    @Override
    public long getCapacity() {
        return Long.MAX_VALUE;
    }
}
