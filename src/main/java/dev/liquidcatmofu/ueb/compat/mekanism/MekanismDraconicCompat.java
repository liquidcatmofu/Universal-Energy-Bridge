package dev.liquidcatmofu.ueb.compat.mekanism;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import dev.liquidcatmofu.ueb.UniversalEnergyBridge;
import dev.liquidcatmofu.ueb.api.FactoryCapabilityProvider;
import dev.liquidcatmofu.ueb.compat.BlockEntityCompat;
import dev.liquidcatmofu.ueb.compat.draconic.UniversalToOPStorage;
import dev.liquidcatmofu.ueb.config.BridgeConfig;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public final class MekanismDraconicCompat implements BlockEntityCompat {
    @Override
    public void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        if (!BridgeConfig.MEKANISM_TO_DRACONIC.get()) {
            return;
        }
        BlockEntity blockEntity = event.getObject();
        if (!MekanismCompatTargets.isLargeEnergyEndpoint(blockEntity)) {
            return;
        }

        FactoryCapabilityProvider<IOPStorage> provider = new FactoryCapabilityProvider<>(CapabilityOP.OP, side ->
                blockEntity.getCapability(Capabilities.STRICT_ENERGY, side)
                        .resolve()
                        .map(MekanismUniversalEnergyStorage::new)
                        .map(UniversalToOPStorage::new)
                        .orElse(null));

        event.addCapability(new ResourceLocation(UniversalEnergyBridge.MOD_ID, "mekanism_draconic"), provider);
        event.addListener(provider::invalidate);
    }
}
