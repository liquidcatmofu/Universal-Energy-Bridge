package dev.liquidcatmofu.ueb.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class BridgeConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue DRACONIC_TO_MEKANISM;
    public static final ForgeConfigSpec.BooleanValue DRACONIC_TO_FLUX_NETWORKS;
    public static final ForgeConfigSpec.BooleanValue FLUX_NETWORKS_TO_DRACONIC;
    public static final ForgeConfigSpec.BooleanValue DRACONIC_TO_APPLIED_FLUX;
    public static final ForgeConfigSpec.BooleanValue MEKANISM_TO_DRACONIC;
    public static final ForgeConfigSpec.BooleanValue MEKANISM_TO_APPLIED_FLUX;
    public static final ForgeConfigSpec.BooleanValue FLUX_NETWORKS_OVERFLOW_GUARD;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("compat");
        DRACONIC_TO_MEKANISM = builder
                .comment("Expose Draconic Evolution Energy Pylons through Mekanism Strict Energy without Forge Energy's int bottleneck.")
                .define("draconicToMekanism", true);
        DRACONIC_TO_FLUX_NETWORKS = builder
                .comment("Expose supported Draconic Evolution OP endpoints through Flux Networks long energy capability.")
                .define("draconicToFluxNetworks", true);
        FLUX_NETWORKS_TO_DRACONIC = builder
                .comment("Expose Flux Plugs through BrandonsCore OP so actively-pushing Draconic devices can use Flux Networks' long receive path instead of Forge Energy.")
                .define("fluxNetworksToDraconic", true);
        DRACONIC_TO_APPLIED_FLUX = builder
                .comment("Expose Draconic Evolution Energy Pylons as AppliedFlux FE external storage to AE2 storage buses.")
                .define("draconicToAppliedFlux", true);
        MEKANISM_TO_DRACONIC = builder
                .comment("Expose selected Mekanism large-storage endpoints through BrandonsCore OP.")
                .define("mekanismToDraconic", true);
        MEKANISM_TO_APPLIED_FLUX = builder
                .comment("Expose selected Mekanism endpoints directly as AppliedFlux FE external storage. Induction Matrix already has native AppliedFlux handling; this is mainly useful for Energy Cubes and Quantum Entangloporters.")
                .define("mekanismToAppliedFlux", true);
        FLUX_NETWORKS_OVERFLOW_GUARD = builder
                .comment(
                        "Patch Flux Networks' network-wide long accumulators to saturate instead of wrapping negative.",
                        "This does not lower per-device transfer limits. It only guards statistics and the internal request limiter."
                )
                .define("fluxNetworksOverflowGuard", true);
        builder.pop();

        SPEC = builder.build();
    }

    private BridgeConfig() {}
}
