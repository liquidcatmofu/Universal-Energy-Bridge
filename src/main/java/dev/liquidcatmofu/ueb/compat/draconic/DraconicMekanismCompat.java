package dev.liquidcatmofu.ueb.compat.draconic;

import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon;
import dev.liquidcatmofu.ueb.api.CapabilityAttachUtil;
import dev.liquidcatmofu.ueb.compat.BlockEntityCompat;
import dev.liquidcatmofu.ueb.compat.mekanism.UniversalToMekanismEnergyHandler;
import dev.liquidcatmofu.ueb.config.BridgeConfig;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public final class DraconicMekanismCompat implements BlockEntityCompat {
    @Override
    public void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        if (!BridgeConfig.DRACONIC_TO_MEKANISM.get()) {
            return;
        }
        if (event.getObject() instanceof TileEnergyPylon pylon) {
            DraconicUniversalEnergyStorage endpoint = new DraconicUniversalEnergyStorage(pylon);
            CapabilityAttachUtil.add(event, "draconic_mekanism",
                    Capabilities.STRICT_ENERGY,
                    new UniversalToMekanismEnergyHandler(endpoint));
        }
    }
}
