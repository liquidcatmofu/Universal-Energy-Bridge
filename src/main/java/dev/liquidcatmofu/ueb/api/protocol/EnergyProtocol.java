package dev.liquidcatmofu.ueb.api.protocol;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/**
 * Metadata for one externally visible energy protocol.
 *
 * <p>Protocol identifiers are registry keys, not Java enum values, so addons may register
 * additional protocols without modifying UEB itself.</p>
 */
public record EnergyProtocol<A>(
        ResourceLocation id,
        AmountDomain<A> amountDomain,
        ResourceLocation semantics
) {
    public EnergyProtocol {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(amountDomain, "amountDomain");
        Objects.requireNonNull(semantics, "semantics");
    }
}
