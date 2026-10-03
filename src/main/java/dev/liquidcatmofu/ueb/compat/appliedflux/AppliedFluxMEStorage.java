package dev.liquidcatmofu.ueb.compat.appliedflux;

import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.stacks.AEKey;
import appeng.api.stacks.KeyCounter;
import appeng.api.storage.MEStorage;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.me.storage.ITickingMonitor;
import com.glodblock.github.appflux.common.me.key.FluxKey;
import com.glodblock.github.appflux.common.me.key.type.EnergyType;
import dev.liquidcatmofu.ueb.api.IUniversalEnergyStorage;
import net.minecraft.network.chat.Component;

/** Exposes a Universal Energy endpoint as AppliedFlux's FE AEKey. */
public final class AppliedFluxMEStorage implements MEStorage, ITickingMonitor {
    private static final FluxKey FE_KEY = FluxKey.of(EnergyType.FE);
    private final IUniversalEnergyStorage storage;
    private final Component description;
    private long lastObservedStored = Long.MIN_VALUE;

    public AppliedFluxMEStorage(IUniversalEnergyStorage storage, Component description) {
        this.storage = storage;
        this.description = description;
    }

    @Override
    public boolean isPreferredStorageFor(AEKey what, IActionSource source) {
        return FE_KEY.equals(what) && storage.canInsert() && storage.getStored() > 0;
    }

    @Override
    public long insert(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (!FE_KEY.equals(what) || amount <= 0) {
            return 0;
        }
        return storage.insert(amount, mode == Actionable.SIMULATE);
    }

    @Override
    public long extract(AEKey what, long amount, Actionable mode, IActionSource source) {
        if (!FE_KEY.equals(what) || amount <= 0) {
            return 0;
        }
        return storage.extract(amount, mode == Actionable.SIMULATE);
    }

    @Override
    public void getAvailableStacks(KeyCounter out) {
        if (storage.canExtract()) {
            long stored = storage.getStored();
            if (stored > 0) {
                out.add(FE_KEY, stored);
            }
        }
    }

    @Override
    public TickRateModulation onTick() {
        long stored = storage.getStored();
        boolean changed = stored != lastObservedStored;
        lastObservedStored = stored;
        return changed ? TickRateModulation.URGENT : TickRateModulation.SLOWER;
    }

    @Override
    public Component getDescription() {
        return description;
    }
}
