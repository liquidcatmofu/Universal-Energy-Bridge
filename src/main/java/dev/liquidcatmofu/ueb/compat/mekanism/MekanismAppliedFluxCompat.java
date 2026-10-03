package dev.liquidcatmofu.ueb.compat.mekanism;

import appeng.api.storage.MEStorage;
import dev.liquidcatmofu.ueb.UniversalEnergyBridge;
import dev.liquidcatmofu.ueb.api.FactoryCapabilityProvider;
import dev.liquidcatmofu.ueb.compat.BlockEntityCompat;
import dev.liquidcatmofu.ueb.compat.appliedflux.AppliedFluxMEStorage;
import dev.liquidcatmofu.ueb.config.BridgeConfig;
import mekanism.common.tile.TileEntityEnergyCube;
import mekanism.common.tile.TileEntityQuantumEntangloporter;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public final class MekanismAppliedFluxCompat implements BlockEntityCompat {
    @Override
    public void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        if (!BridgeConfig.MEKANISM_TO_APPLIED_FLUX.get()) {
            return;
        }
        BlockEntity blockEntity = event.getObject();

        if (!MekanismCompatTargets.isAppliedFluxEndpoint(blockEntity)) {
            return;
        }

        Component description;
        if (blockEntity instanceof TileEntityEnergyCube) {
            description = Component.literal("Mekanism Energy Cube");
        } else if (blockEntity instanceof TileEntityQuantumEntangloporter) {
            description = Component.literal("Mekanism Quantum Entangloporter");
        } else {
            description = Component.literal("Mekanism Extras Energy Storage");
        }

        FactoryCapabilityProvider<MEStorage> provider = new FactoryCapabilityProvider<>(
                appeng.capabilities.Capabilities.STORAGE,
                side -> new AppliedFluxMEStorage(
                        new MekanismUniversalEnergyStorage(blockEntity, side),
                        description));

        event.addCapability(UniversalEnergyBridge.id("mekanism_applied_flux"), provider);
        event.addListener(provider::invalidate);
    }
}
