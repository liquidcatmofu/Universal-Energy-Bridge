package dev.liquidcatmofu.ueb.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public final class UniversalEnergyCapabilities {
    public static final Capability<IUniversalEnergyStorage> ENERGY =
            CapabilityManager.get(new CapabilityToken<>() {});

    private UniversalEnergyCapabilities() {}
}
