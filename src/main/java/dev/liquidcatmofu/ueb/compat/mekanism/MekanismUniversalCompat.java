package dev.liquidcatmofu.ueb.compat.mekanism;

import dev.liquidcatmofu.ueb.UniversalEnergyBridge;
import dev.liquidcatmofu.ueb.api.FactoryCapabilityProvider;
import dev.liquidcatmofu.ueb.api.UniversalEnergyCapabilities;
import dev.liquidcatmofu.ueb.compat.BlockEntityCompat;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public final class MekanismUniversalCompat implements BlockEntityCompat {
    @Override
    public void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        BlockEntity blockEntity = event.getObject();
        if (!MekanismCompatTargets.isLargeEnergyEndpoint(blockEntity)) {
            return;
        }

        FactoryCapabilityProvider<dev.liquidcatmofu.ueb.api.IUniversalEnergyStorage> provider =
                new FactoryCapabilityProvider<>(UniversalEnergyCapabilities.ENERGY, side ->
                        blockEntity.getCapability(Capabilities.STRICT_ENERGY, side)
                                .resolve()
                                .map(MekanismUniversalEnergyStorage::new)
                                .orElse(null));

        event.addCapability(new ResourceLocation(UniversalEnergyBridge.MOD_ID, "mekanism_universal"), provider);
        event.addListener(provider::invalidate);
    }
}
