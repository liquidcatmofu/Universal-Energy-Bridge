package dev.liquidcatmofu.ueb.compat.mekanism;

import dev.liquidcatmofu.ueb.UniversalEnergyBridge;
import dev.liquidcatmofu.ueb.api.FactoryCapabilityProvider;
import dev.liquidcatmofu.ueb.api.IUniversalEnergyStorage;
import dev.liquidcatmofu.ueb.api.UniversalEnergyCapabilities;
import dev.liquidcatmofu.ueb.compat.BlockEntityCompat;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public final class MekanismUniversalCompat implements BlockEntityCompat {
    @Override
    public void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        BlockEntity blockEntity = event.getObject();
        if (MekanismCompatTargets.isEnergyCube(blockEntity)
                || !MekanismCompatTargets.isLargeEnergyEndpoint(blockEntity)) {
            return;
        }

        FactoryCapabilityProvider<IUniversalEnergyStorage> provider =
                new FactoryCapabilityProvider<>(UniversalEnergyCapabilities.ENERGY,
                        side -> new MekanismUniversalEnergyStorage(blockEntity, side));

        event.addCapability(UniversalEnergyBridge.id("mekanism_universal"), provider);
        event.addListener(provider::invalidate);
    }
}
