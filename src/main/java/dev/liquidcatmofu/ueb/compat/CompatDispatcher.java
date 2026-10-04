package dev.liquidcatmofu.ueb.compat;

import dev.liquidcatmofu.ueb.UniversalEnergyBridge;
import dev.liquidcatmofu.ueb.api.UniversalEnergyRegistration;
import dev.liquidcatmofu.ueb.api.endpoint.UniversalEndpointExporter;
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

        // Generic Universal-backed protocol exporters. Any endpoint registered with the
        // runtime automatically gains these views unless it declares the protocol native.
        registerExporterIfLoaded(new String[]{"draconicevolution"},
                "dev.liquidcatmofu.ueb.compat.draconic.DraconicUniversalExporter");
        registerExporterIfLoaded(new String[]{"mekanism"},
                "dev.liquidcatmofu.ueb.compat.mekanism.MekanismUniversalExporter");
        registerExporterIfLoaded(new String[]{"fluxnetworks"},
                "dev.liquidcatmofu.ueb.compat.flux.FluxUniversalExporter");

        // Setup-time endpoint registrations.
        registerRegistrationIfLoaded(new String[]{"trashcans"},
                "dev.liquidcatmofu.ueb.compat.trashcans.TrashCansEndpointCompat");

        // Legacy per-mod Universal capability providers. These will migrate to endpoint
        // registrations incrementally; keep their current behavior unchanged for alpha.20.
        registerIfLoaded(new String[]{"draconicevolution"},
                "dev.liquidcatmofu.ueb.compat.draconic.DraconicUniversalCompat");
        registerIfLoaded(new String[]{"mekanism"},
                "dev.liquidcatmofu.ueb.compat.mekanism.MekanismUniversalCompat");
        registerIfLoaded(new String[]{"fluxnetworks"},
                "dev.liquidcatmofu.ueb.compat.flux.FluxUniversalCompat");

        // Existing native high-throughput pairwise views. These remain until source/target
        // endpoint registrations can replace them without changing transfer ownership.
        registerIfLoaded(new String[]{"draconicevolution", "mekanism"},
                "dev.liquidcatmofu.ueb.compat.draconic.DraconicMekanismCompat");
        registerIfLoaded(new String[]{"draconicevolution", "fluxnetworks"},
                "dev.liquidcatmofu.ueb.compat.draconic.DraconicFluxCompat");
        registerIfLoaded(new String[]{"fluxnetworks", "draconicevolution"},
                "dev.liquidcatmofu.ueb.compat.flux.FluxDraconicCompat");
        registerIfLoaded(new String[]{"mekanism", "draconicevolution"},
                "dev.liquidcatmofu.ueb.compat.mekanism.MekanismDraconicCompat");

        // Energy Meter native protocol-preserving passthrough.
        registerIfLoaded(new String[]{"energymeter", "draconicevolution"},
                "dev.liquidcatmofu.ueb.compat.energymeter.EnergyMeterDraconicCompat");
        registerIfLoaded(new String[]{"energymeter", "mekanism"},
                "dev.liquidcatmofu.ueb.compat.energymeter.EnergyMeterMekanismCompat");
        registerIfLoaded(new String[]{"energymeter", "fluxnetworks"},
                "dev.liquidcatmofu.ueb.compat.energymeter.EnergyMeterFluxCompat");

        // AE2/AppliedFlux ecosystem integration remains separate from protocol registration.
        registerIfLoaded(new String[]{"draconicevolution", "ae2", "appflux"},
                "dev.liquidcatmofu.ueb.compat.draconic.DraconicAppliedFluxCompat");
        registerIfLoaded(new String[]{"mekanism", "ae2", "appflux"},
                "dev.liquidcatmofu.ueb.compat.mekanism.MekanismAppliedFluxCompat");
    }

    private static void registerIfLoaded(String[] modIds, String className) {
        if (!allLoaded(modIds)) {
            return;
        }
        try {
            Class<?> type = Class.forName(className);
            ATTACHERS.add((BlockEntityCompat) type.getDeclaredConstructor().newInstance());
            UniversalEnergyBridge.LOGGER.info("Enabled compat attacher {}", className);
        } catch (ReflectiveOperationException | LinkageError e) {
            UniversalEnergyBridge.LOGGER.error("Failed to enable compat attacher {}", className, e);
        }
    }

    private static void registerRegistrationIfLoaded(String[] modIds, String className) {
        if (!allLoaded(modIds)) {
            return;
        }
        try {
            Class<?> type = Class.forName(className);
            CompatRegistration registration =
                    (CompatRegistration) type.getDeclaredConstructor().newInstance();
            registration.register();
            UniversalEnergyBridge.LOGGER.info("Enabled compat registration {}", className);
        } catch (ReflectiveOperationException | LinkageError | RuntimeException e) {
            UniversalEnergyBridge.LOGGER.error("Failed to enable compat registration {}", className, e);
        }
    }

    private static void registerExporterIfLoaded(String[] modIds, String className) {
        if (!allLoaded(modIds)) {
            return;
        }
        try {
            Class<?> type = Class.forName(className);
            UniversalEndpointExporter exporter =
                    (UniversalEndpointExporter) type.getDeclaredConstructor().newInstance();
            UniversalEnergyRegistration.registerExporter(exporter);
            UniversalEnergyBridge.LOGGER.info("Enabled protocol exporter {}", className);
        } catch (ReflectiveOperationException | LinkageError | RuntimeException e) {
            UniversalEnergyBridge.LOGGER.error("Failed to enable protocol exporter {}", className, e);
        }
    }

    private static boolean allLoaded(String[] modIds) {
        for (String modId : modIds) {
            if (!ModList.get().isLoaded(modId)) {
                return false;
            }
        }
        return true;
    }

    public static void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        for (BlockEntityCompat attacher : ATTACHERS) {
            attacher.attach(event);
        }
    }
}
