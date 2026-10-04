package dev.liquidcatmofu.ueb.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class BridgeConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue DRACONIC_TO_MEKANISM;
    public static final ForgeConfigSpec.BooleanValue DRACONIC_TO_FLUX_NETWORKS;
    public static final ForgeConfigSpec.BooleanValue FLUX_NETWORKS_TO_DRACONIC;
    public static final ForgeConfigSpec.BooleanValue FLUX_NETWORKS_OVERFLOW_GUARD;
    public static final ForgeConfigSpec.BooleanValue DRACONIC_TO_APPLIED_FLUX;
    public static final ForgeConfigSpec.BooleanValue MEKANISM_TO_DRACONIC;
    public static final ForgeConfigSpec.BooleanValue MEKANISM_TO_APPLIED_FLUX;
    public static final ForgeConfigSpec.BooleanValue ENERGY_METER_NATIVE_PASSTHROUGH;

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
        FLUX_NETWORKS_OVERFLOW_GUARD = builder
                .comment(
                        "Patch Flux Networks 1.20 signed-long bookkeeping and Flux Plug buffer arithmetic so overflow/corruption cannot wrap negative.",
                        "This preserves configured transfer limits while allowing long-capable endpoints to use the full signed-long range.")
                .define("fluxNetworksOverflowGuard", true);
        DRACONIC_TO_APPLIED_FLUX = builder
                .comment("Expose Draconic Evolution Energy Pylons as AppliedFlux FE external storage to AE2 storage buses.")
                .define("draconicToAppliedFlux", true);
        MEKANISM_TO_DRACONIC = builder
                .comment("Expose selected Mekanism large-storage endpoints and Universal Cables through BrandonsCore OP using native sided Strict Energy.")
                .define("mekanismToDraconic", true);
        MEKANISM_TO_APPLIED_FLUX = builder
                .comment("Expose selected Mekanism endpoints directly as AppliedFlux FE external storage. Induction Matrix already has native AppliedFlux handling; this is mainly useful for Energy Cubes and Quantum Entangloporters.")
                .define("mekanismToAppliedFlux", true);
        ENERGY_METER_NATIVE_PASSTHROUGH = builder
                .comment(
                        "Expose Energy Meter inputs through native OP, Mekanism Strict Energy and Flux Networks long capabilities.",
                        "Transfers keep the incoming native protocol when the output supports it and only fall back to Forge Energy when necessary.")
                .define("energyMeterNativePassthrough", true);
        builder.pop();

        SPEC = builder.build();
    }

    private BridgeConfig() {}
}
