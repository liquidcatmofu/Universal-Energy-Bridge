package dev.liquidcatmofu.ueb.compat.trashcans;

import com.supermartijn642.trashcans.TrashCanBlockEntity;
import dev.liquidcatmofu.ueb.api.CapabilityAttachUtil;
import dev.liquidcatmofu.ueb.api.UniversalEnergyCapabilities;
import dev.liquidcatmofu.ueb.compat.BlockEntityCompat;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;

/**
 * Adds UEB's signed-long sink view to Trash Cans' Energy Trash Can and Ultimate Trash Can.
 */
public final class TrashCansUniversalCompat implements BlockEntityCompat {
    @Override
    public void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        if (!(event.getObject() instanceof TrashCanBlockEntity trashCan) || !trashCan.energy) {
            return;
        }

        CapabilityAttachUtil.add(event, "trashcans_universal",
                UniversalEnergyCapabilities.ENERGY,
                new TrashCanUniversalEnergyStorage(trashCan));
    }
}
