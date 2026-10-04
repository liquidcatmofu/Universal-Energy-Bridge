package dev.liquidcatmofu.ueb.api.protocol;

import net.minecraft.resources.ResourceLocation;

/**
 * Describes the native numeric domain used by an energy protocol.
 *
 * <p>The domain is deliberately separate from {@link EnergyProtocol}: two unrelated
 * protocols may both use the same amount representation and still require an explicit
 * conversion edge because their units or semantics differ.</p>
 */
public interface AmountDomain<A> {

    ResourceLocation id();

    Class<A> valueType();

    A zero();

    int compare(A left, A right);
}
