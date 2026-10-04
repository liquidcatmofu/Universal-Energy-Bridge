package dev.liquidcatmofu.ueb.api.endpoint;

import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A concrete energy endpoint port. Side is part of endpoint identity; {@code null} means
 * unsided access and is not equivalent to "all sides".
 */
public record EndpointPort<T>(T owner, @Nullable Direction side) {
    public EndpointPort {
        Objects.requireNonNull(owner, "owner");
    }
}
