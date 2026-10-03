package dev.liquidcatmofu.ueb.compat.mekanism;

import dev.liquidcatmofu.ueb.UniversalEnergyBridge;
import dev.liquidcatmofu.ueb.api.FactoryCapabilityProvider;
import dev.liquidcatmofu.ueb.api.IUniversalEnergyStorage;
import dev.liquidcatmofu.ueb.api.UniversalEnergyCapabilities;
import dev.liquidcatmofu.ueb.compat.BlockEntityCompat;
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

        FactoryCapabilityProvider<IUniversalEnergyStorage> provider =
                new FactoryCapabilityProvider<>(UniversalEnergyCapabilities.ENERGY,
                        side -> new MekanismUniversalEnergyStorage(blockEntity, side));

        event.addCapability(new ResourceLocation(UniversalEnergyBridge.MOD_ID, "mekanism_universal"), provider);
        event.addListener(provider::invalidate);
    }
}
