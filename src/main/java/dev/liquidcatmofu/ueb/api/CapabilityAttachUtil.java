package dev.liquidcatmofu.ueb.api;

import dev.liquidcatmofu.ueb.UniversalEnergyBridge;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import java.util.function.Function;

public final class CapabilityAttachUtil {
    private CapabilityAttachUtil() {}

    public static <T> void add(AttachCapabilitiesEvent<BlockEntity> event, String path,
                               Capability<T> capability, T value) {
        SingleCapabilityProvider<T> provider = new SingleCapabilityProvider<>(capability, value);
        event.addCapability(UniversalEnergyBridge.id(path), provider);
        event.addListener(provider::invalidate);
    }

    public static <T> void addSided(AttachCapabilitiesEvent<BlockEntity> event, String path,
                                    Capability<T> capability, Function<Direction, T> factory) {
        FactoryCapabilityProvider<T> provider = new FactoryCapabilityProvider<>(capability, factory);
        event.addCapability(UniversalEnergyBridge.id(path), provider);
        event.addListener(provider::invalidate);
    }
}
