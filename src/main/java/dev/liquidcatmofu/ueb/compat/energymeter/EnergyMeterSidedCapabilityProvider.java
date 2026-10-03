package dev.liquidcatmofu.ueb.compat.energymeter;

import com.github.almostreliable.energymeter.meter.MeterEntity;
import com.github.almostreliable.energymeter.util.TypeEnums.IO_SETTING;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Mirrors Energy Meter's sided capability visibility while keeping the capability
 * implementation stable enough for neighboring blocks to cache it.
 */
public final class EnergyMeterSidedCapabilityProvider<T> implements ICapabilityProvider {
    private final MeterEntity meter;
    private final Capability<T> capability;
    private final Function<Direction, T> factory;
    private final Map<Direction, LazyOptional<T>> sided = new EnumMap<>(Direction.class);

    public EnergyMeterSidedCapabilityProvider(MeterEntity meter, Capability<T> capability, Function<Direction, T> factory) {
        this.meter = meter;
        this.capability = capability;
        this.factory = factory;
    }

    @Override
    public <C> @NotNull LazyOptional<C> getCapability(@NotNull Capability<C> requested, @Nullable Direction side) {
        if (requested != capability || side == null || meter.getSideConfig().get(side) == IO_SETTING.OFF) {
            return LazyOptional.empty();
        }
        return sided.computeIfAbsent(side, this::create).cast();
    }

    private LazyOptional<T> create(Direction side) {
        T value = factory.apply(side);
        return value == null ? LazyOptional.empty() : LazyOptional.of(() -> value);
    }

    public void invalidate() {
        for (LazyOptional<T> optional : sided.values()) {
            optional.invalidate();
        }
        sided.clear();
    }
}
