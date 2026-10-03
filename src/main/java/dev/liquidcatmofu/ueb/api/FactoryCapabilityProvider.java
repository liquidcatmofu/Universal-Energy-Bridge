package dev.liquidcatmofu.ueb.api;

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
 * Side-aware attached capability provider. Values are created at most once per side.
 */
public final class FactoryCapabilityProvider<T> implements ICapabilityProvider {
    private final Capability<T> capability;
    private final Function<Direction, T> factory;
    private final Map<Direction, LazyOptional<T>> sided = new EnumMap<>(Direction.class);
    private LazyOptional<T> unsided;

    public FactoryCapabilityProvider(Capability<T> capability, Function<Direction, T> factory) {
        this.capability = capability;
        this.factory = factory;
    }

    @Override
    public <C> @NotNull LazyOptional<C> getCapability(@NotNull Capability<C> requested, @Nullable Direction side) {
        if (requested != capability) {
            return LazyOptional.empty();
        }
        return get(side).cast();
    }

    private LazyOptional<T> get(@Nullable Direction side) {
        if (side == null) {
            if (unsided == null) {
                unsided = create(null);
            }
            return unsided;
        }
        return sided.computeIfAbsent(side, this::create);
    }

    private LazyOptional<T> create(@Nullable Direction side) {
        T value = factory.apply(side);
        return value == null ? LazyOptional.empty() : LazyOptional.of(() -> value);
    }

    public void invalidate() {
        if (unsided != null) {
            unsided.invalidate();
        }
        for (LazyOptional<T> optional : sided.values()) {
            optional.invalidate();
        }
        sided.clear();
    }
}
