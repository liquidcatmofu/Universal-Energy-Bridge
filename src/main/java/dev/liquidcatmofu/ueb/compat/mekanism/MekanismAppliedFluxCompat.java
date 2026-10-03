package dev.liquidcatmofu.ueb.compat.mekanism;

import appeng.api.storage.MEStorage;
import dev.liquidcatmofu.ueb.UniversalEnergyBridge;
import dev.liquidcatmofu.ueb.api.FactoryCapabilityProvider;
import dev.liquidcatmofu.ueb.compat.BlockEntityCompat;
import dev.liquidcatmofu.ueb.compat.appliedflux.AppliedFluxMEStorage;
import dev.liquidcatmofu.ueb.config.BridgeConfig;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public final class MekanismAppliedFluxCompat implements BlockEntityCompat {
    @Override
    public void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        if (!BridgeConfig.MEKANISM_TO_APPLIED_FLUX.get()) {
            return;
        }
        BlockEntity blockEntity = event.getObject();

        // AppliedFlux already has a dedicated direct Induction Port handler.
        // Add a native long path here only for the Quantum Entangloporter.
        if (!MekanismCompatTargets.isQuantumEntangloporter(blockEntity)) {
            return;
        }

        FactoryCapabilityProvider<MEStorage> provider = new FactoryCapabilityProvider<>(
                appeng.capabilities.Capabilities.STORAGE,
                side -> blockEntity.getCapability(Capabilities.STRICT_ENERGY, side)
                        .resolve()
                        .map(MekanismUniversalEnergyStorage::new)
                        .map(storage -> (MEStorage) new AppliedFluxMEStorage(
                                storage, Component.literal("Mekanism Quantum Entangloporter")))
                        .orElse(null));

        event.addCapability(new ResourceLocation(UniversalEnergyBridge.MOD_ID, "mekanism_applied_flux"), provider);
        event.addListener(provider::invalidate);
    }
}
