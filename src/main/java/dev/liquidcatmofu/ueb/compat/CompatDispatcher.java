package dev.liquidcatmofu.ueb.compat;

import dev.liquidcatmofu.ueb.UniversalEnergyBridge;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.ModList;

import java.util.ArrayList;
import java.util.List;

public final class CompatDispatcher {
    private static final List<BlockEntityCompat> ATTACHERS = new ArrayList<>();
    private static boolean initialized;

    private CompatDispatcher() {}

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;

        // Universal capability providers.
        registerIfLoaded(new String[]{"draconicevolution"},
                "dev.liquidcatmofu.ueb.compat.draconic.DraconicUniversalCompat");
        registerIfLoaded(new String[]{"mekanism"},
                "dev.liquidcatmofu.ueb.compat.mekanism.MekanismUniversalCompat");
        registerIfLoaded(new String[]{"fluxnetworks"},
                "dev.liquidcatmofu.ueb.compat.flux.FluxUniversalCompat");

        // Native high-throughput views.
        registerIfLoaded(new String[]{"draconicevolution", "mekanism"},
                "dev.liquidcatmofu.ueb.compat.draconic.DraconicMekanismCompat");
        registerIfLoaded(new String[]{"draconicevolution", "fluxnetworks"},
                "dev.liquidcatmofu.ueb.compat.draconic.DraconicFluxCompat");
        registerIfLoaded(new String[]{"fluxnetworks", "draconicevolution"},
                "dev.liquidcatmofu.ueb.compat.flux.FluxDraconicCompat");
        registerIfLoaded(new String[]{"mekanism", "draconicevolution"},
                "dev.liquidcatmofu.ueb.compat.mekanism.MekanismDraconicCompat");

        // AE2/AppliedFlux external storage views.
        registerIfLoaded(new String[]{"draconicevolution", "ae2", "appflux"},
                "dev.liquidcatmofu.ueb.compat.draconic.DraconicAppliedFluxCompat");
        registerIfLoaded(new String[]{"mekanism", "ae2", "appflux"},
                "dev.liquidcatmofu.ueb.compat.mekanism.MekanismAppliedFluxCompat");
    }

    private static void registerIfLoaded(String[] modIds, String className) {
        for (String modId : modIds) {
            if (!ModList.get().isLoaded(modId)) {
                return;
            }
        }
        try {
            Class<?> type = Class.forName(className);
            ATTACHERS.add((BlockEntityCompat) type.getDeclaredConstructor().newInstance());
            UniversalEnergyBridge.LOGGER.info("Enabled compat attacher {}", className);
        } catch (ReflectiveOperationException | LinkageError e) {
            UniversalEnergyBridge.LOGGER.error("Failed to enable compat attacher {}", className, e);
        }
    }

    public static void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        for (BlockEntityCompat attacher : ATTACHERS) {
            attacher.attach(event);
        }
    }
}
