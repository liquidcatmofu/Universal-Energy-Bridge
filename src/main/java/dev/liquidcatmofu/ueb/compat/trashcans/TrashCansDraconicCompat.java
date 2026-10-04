package dev.liquidcatmofu.ueb.compat.trashcans;

import com.brandon3055.brandonscore.capability.CapabilityOP;
import dev.liquidcatmofu.ueb.api.CapabilityAttachUtil;
import dev.liquidcatmofu.ueb.compat.BlockEntityCompat;
import dev.liquidcatmofu.ueb.compat.draconic.UniversalToOPStorage;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;

/**
 * Exposes Trash Cans' stateless sink through BrandonsCore OP so Draconic sources do not fall
 * back to Forge Energy's signed-int transfer width.
 */
public final class TrashCansDraconicCompat implements BlockEntityCompat {
    @Override
    public void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        BlockEntity blockEntity = event.getObject();
        if (!TrashCansSupport.isEnergySink(blockEntity)) {
            return;
        }

        CapabilityAttachUtil.addSided(event, "trashcans_draconic",
                CapabilityOP.OP,
                side -> new UniversalToOPStorage(new TrashCanUniversalEnergyStorage(blockEntity, side)));
    }
}
