package dev.liquidcatmofu.ueb.api.protocol;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/**
 * Protocol descriptors that are always known to UEB.
 *
 * <p>Universal signed-long is a guaranteed fallback protocol, not a claim that every
 * native protocol should be converted through signed-long.</p>
 */
public final class StandardEnergyProtocols {
    public static final ResourceLocation SCALAR_ENERGY_SEMANTICS =
            Objects.requireNonNull(ResourceLocation.tryBuild("universal_energy_bridge", "scalar_energy"));

    public static final EnergyProtocol<Long> UNIVERSAL = new EnergyProtocol<>(
            ProtocolIds.UNIVERSAL,
            StandardAmountDomains.SIGNED_LONG,
            SCALAR_ENERGY_SEMANTICS);

    public static final EnergyProtocol<Integer> FORGE_ENERGY = new EnergyProtocol<>(
            ProtocolIds.FORGE_ENERGY,
            StandardAmountDomains.SIGNED_INT,
            SCALAR_ENERGY_SEMANTICS);

    private StandardEnergyProtocols() {}
}
