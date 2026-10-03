package dev.liquidcatmofu.ueb.compat.mekanism;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import dev.liquidcatmofu.ueb.UniversalEnergyBridge;
import dev.liquidcatmofu.ueb.api.FactoryCapabilityProvider;
import dev.liquidcatmofu.ueb.compat.BlockEntityCompat;
import dev.liquidcatmofu.ueb.compat.draconic.UniversalToOPStorage;
import dev.liquidcatmofu.ueb.config.BridgeConfig;
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

        FactoryCapabilityProvider<IOPStorage> provider = new FactoryCapabilityProvider<>(
                CapabilityOP.OP,
                side -> new UniversalToOPStorage(new MekanismUniversalEnergyStorage(blockEntity, side)));

        event.addCapability(UniversalEnergyBridge.id("mekanism_draconic"), provider);
        event.addListener(provider::invalidate);
    }
}
