package dev.liquidcatmofu.ueb.api;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SingleCapabilityProvider<T> implements ICapabilityProvider {
    private final Capability<T> capability;
    private final LazyOptional<T> value;

    public SingleCapabilityProvider(Capability<T> capability, T value) {
        this.capability = capability;
        this.value = LazyOptional.of(() -> value);
    }

    @Override
    public <C> @NotNull LazyOptional<C> getCapability(@NotNull Capability<C> requested, @Nullable Direction side) {
        return requested == capability ? value.cast() : LazyOptional.empty();
    }

    public void invalidate() {
        value.invalidate();
    }
}
