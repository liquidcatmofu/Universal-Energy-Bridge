package dev.liquidcatmofu.ueb.compat.trashcans;

import dev.liquidcatmofu.ueb.api.CapabilityAttachUtil;
import dev.liquidcatmofu.ueb.compat.BlockEntityCompat;
import dev.liquidcatmofu.ueb.compat.mekanism.UniversalToMekanismEnergyHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;

/**
 * Exposes Trash Cans' stateless sink through Mekanism Strict Energy so Universal Cables and
 * other native Mekanism routes can retain their high-throughput path.
 */
public final class TrashCansMekanismCompat implements BlockEntityCompat {
    @Override
    public void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        BlockEntity blockEntity = event.getObject();
        if (!TrashCansSupport.isEnergySink(blockEntity)) {
            return;
        }

        CapabilityAttachUtil.addSided(event, "trashcans_mekanism",
                Capabilities.STRICT_ENERGY,
                side -> new UniversalToMekanismEnergyHandler(
                        new TrashCanUniversalEnergyStorage(blockEntity, side)));
    }
}
