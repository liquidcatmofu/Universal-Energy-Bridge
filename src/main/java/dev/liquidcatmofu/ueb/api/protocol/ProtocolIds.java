package dev.liquidcatmofu.ueb.api.protocol;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/** Stable identifiers used by UEB's built-in protocol integrations. */
public final class ProtocolIds {
    public static final ResourceLocation UNIVERSAL = id("universal_energy_bridge", "universal");
    public static final ResourceLocation FORGE_ENERGY = id("forge", "energy");
    public static final ResourceLocation BRANDONSCORE_OP = id("brandonscore", "op");
    public static final ResourceLocation MEKANISM_STRICT = id("mekanism", "strict_energy");
    public static final ResourceLocation FLUX_NETWORKS = id("fluxnetworks", "energy");

    private ProtocolIds() {}

    private static ResourceLocation id(String namespace, String path) {
        return Objects.requireNonNull(ResourceLocation.tryBuild(namespace, path));
    }
}
