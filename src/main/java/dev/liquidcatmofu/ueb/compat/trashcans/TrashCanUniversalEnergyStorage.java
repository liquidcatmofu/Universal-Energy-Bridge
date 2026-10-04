package dev.liquidcatmofu.ueb.compat.trashcans;

import dev.liquidcatmofu.ueb.api.IUniversalEnergyStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

/**
 * Signed-long sink view for Trash Cans' energy-capable trash cans.
 *
 * <p>Trash Cans discards accepted energy and exposes no extraction. Its Forge Energy handler
 * returns the configured transfer limit when limiting is enabled, and accepts the complete int
 * request when the limit is disabled. This adapter probes that public behavior rather than
 * depending on Trash Cans implementation fields.</p>
 */
public final class TrashCanUniversalEnergyStorage implements IUniversalEnergyStorage {
    private final BlockEntity trashCan;
    @Nullable
    private final Direction side;

    public TrashCanUniversalEnergyStorage(BlockEntity trashCan, @Nullable Direction side) {
        this.trashCan = trashCan;
        this.side = side;
    }

    @Override
    public long insert(long amount, boolean simulate) {
        if (amount <= 0) {
            return 0;
        }

        IEnergyStorage energy = trashCan.getCapability(ForgeCapabilities.ENERGY, side).orElse(null);
        if (energy == null || !energy.canReceive()) {
            return 0;
        }

        int maxAccepted = energy.receiveEnergy(Integer.MAX_VALUE, true);
        if (maxAccepted <= 0) {
            return 0;
        }

        if (maxAccepted < Integer.MAX_VALUE) {
            int request = (int) Math.min(amount, Integer.MAX_VALUE);
            return Math.max(0, energy.receiveEnergy(request, simulate));
        }

        // Trash Cans' unlimited mode is a stateless sink. Invoke the public FE handler once
        // so any implementation-side effects remain observable, then preserve the full long request.
        int request = (int) Math.min(amount, Integer.MAX_VALUE);
        if (request > 0) {
            energy.receiveEnergy(request, simulate);
        }
        return amount;
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
