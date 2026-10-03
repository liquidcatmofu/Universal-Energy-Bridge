package dev.liquidcatmofu.ueb.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * Server/world-specific gameplay tweaks.
 */
public final class BridgeServerConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue MEKANISM_QE_UNLIMITED_ENERGY_BUFFER;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("mekanism");
        MEKANISM_QE_UNLIMITED_ENERGY_BUFFER = builder
                .comment(
                        "Ignore Mekanism's Quantum Entangloporter frequency energyBuffer limit.",
                        "When enabled, QE frequencies use FloatingLong.MAX_VALUE as their native energy buffer and therefore as their maximum transfer per tick per frequency.",
                        "This is a gameplay/balance change and intentionally defaults to false.",
                        "Requires a server/world restart so existing frequency containers are reconstructed with the new capacity.")
                .worldRestart()
                .define("quantumEntangloporterUnlimitedEnergyBuffer", false);
        builder.pop();

        SPEC = builder.build();
    }

    private BridgeServerConfig() {}
}
