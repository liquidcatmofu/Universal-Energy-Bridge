package dev.liquidcatmofu.ueb.compat;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;

@FunctionalInterface
public interface BlockEntityCompat {
    void attach(AttachCapabilitiesEvent<BlockEntity> event);
}
