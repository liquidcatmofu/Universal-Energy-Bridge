package dev.liquidcatmofu.ueb.compat.draconic;

import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon;
import dev.liquidcatmofu.ueb.api.CapabilityAttachUtil;
import dev.liquidcatmofu.ueb.api.UniversalEnergyCapabilities;
import dev.liquidcatmofu.ueb.compat.BlockEntityCompat;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public final class DraconicUniversalCompat implements BlockEntityCompat {
    @Override
    public void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        if (event.getObject() instanceof TileEnergyPylon pylon) {
            CapabilityAttachUtil.add(event, "draconic_universal",
                    UniversalEnergyCapabilities.ENERGY,
                    new DraconicUniversalEnergyStorage(pylon.opAdapter));
        }
    }
}
