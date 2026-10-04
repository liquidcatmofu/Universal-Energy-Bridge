package dev.liquidcatmofu.ueb.core;

import dev.liquidcatmofu.ueb.UniversalEnergyBridge;
import dev.liquidcatmofu.ueb.api.CapabilityAttachUtil;
import dev.liquidcatmofu.ueb.api.UniversalEnergyCapabilities;
import dev.liquidcatmofu.ueb.api.conversion.EnergyConversion;
import dev.liquidcatmofu.ueb.api.endpoint.BlockEntityEndpointRegistration;
import dev.liquidcatmofu.ueb.api.endpoint.UniversalEndpointExporter;
import dev.liquidcatmofu.ueb.api.protocol.EnergyProtocol;
import dev.liquidcatmofu.ueb.api.protocol.StandardEnergyProtocols;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Runtime registries and BlockEntity exposure manager.
 *
 * <p>This is intentionally not a transfer interceptor. It exposes safe protocol views on
 * registered endpoints. Route planning will only apply where UEB itself owns the transfer or
 * an explicit integration delegates route selection to UEB.</p>
 */
public final class UniversalEnergyRuntime {
    private static final Map<ResourceLocation, EnergyProtocol<?>> PROTOCOLS = new LinkedHashMap<>();
    private static final List<EnergyConversion<?, ?>> CONVERSIONS = new ArrayList<>();
    private static final Map<ResourceLocation, BlockEntityEndpointRegistration> BLOCK_ENTITY_ENDPOINTS =
            new LinkedHashMap<>();
    private static final Map<ResourceLocation, UniversalEndpointExporter> EXPORTERS = new LinkedHashMap<>();

    private static boolean initialized;

    private UniversalEnergyRuntime() {}

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;
        registerProtocol(StandardEnergyProtocols.UNIVERSAL);
        registerProtocol(StandardEnergyProtocols.FORGE_ENERGY);
    }

    public static synchronized void registerProtocol(EnergyProtocol<?> protocol) {
        EnergyProtocol<?> previous = PROTOCOLS.putIfAbsent(protocol.id(), protocol);
        if (previous != null && previous != protocol && !previous.equals(protocol)) {
            throw new IllegalStateException("Energy protocol already registered: " + protocol.id());
        }
    }

    public static synchronized void registerConversion(EnergyConversion<?, ?> conversion) {
        registerProtocol(conversion.source());
        registerProtocol(conversion.target());
        CONVERSIONS.add(conversion);
    }

    public static synchronized void registerBlockEntityEndpoint(BlockEntityEndpointRegistration registration) {
        BlockEntityEndpointRegistration previous = BLOCK_ENTITY_ENDPOINTS.putIfAbsent(
                registration.id(), registration);
        if (previous != null && previous != registration && !previous.equals(registration)) {
            throw new IllegalStateException("BlockEntity endpoint already registered: " + registration.id());
        }
        UniversalEnergyBridge.LOGGER.info("Registered UEB endpoint {}", registration.id());
    }

    public static synchronized void registerExporter(UniversalEndpointExporter exporter) {
        registerProtocol(exporter.protocol());
        ResourceLocation protocolId = exporter.protocol().id();
        UniversalEndpointExporter previous = EXPORTERS.putIfAbsent(protocolId, exporter);
        if (previous != null && previous != exporter && !previous.getClass().equals(exporter.getClass())) {
            throw new IllegalStateException("Protocol exporter already registered: " + protocolId);
        }
        UniversalEnergyBridge.LOGGER.info("Registered UEB protocol exporter {}", protocolId);
    }

    public static synchronized List<EnergyProtocol<?>> protocols() {
        return List.copyOf(PROTOCOLS.values());
    }

    public static synchronized List<EnergyConversion<?, ?>> conversions() {
        return List.copyOf(CONVERSIONS);
    }

    @Nullable
    public static BlockEntityEndpointRegistration findBlockEntityEndpoint(BlockEntity blockEntity) {
        List<BlockEntityEndpointRegistration> registrations;
        synchronized (UniversalEnergyRuntime.class) {
            registrations = List.copyOf(BLOCK_ENTITY_ENDPOINTS.values());
        }

        BlockEntityEndpointRegistration matched = null;
        for (BlockEntityEndpointRegistration registration : registrations) {
            boolean matches;
            try {
                matches = registration.matcher().test(blockEntity);
            } catch (RuntimeException e) {
                UniversalEnergyBridge.LOGGER.error(
                        "UEB endpoint matcher {} failed for {}",
                        registration.id(), blockEntity.getClass().getName(), e);
                continue;
            }
            if (!matches) {
                continue;
            }
            if (matched != null) {
                UniversalEnergyBridge.LOGGER.warn(
                        "Multiple UEB endpoint registrations match {}: {} and {}; using {}",
                        blockEntity.getClass().getName(),
                        matched.id(), registration.id(), matched.id());
                continue;
            }
            matched = registration;
        }
        return matched;
    }

    public static void attach(AttachCapabilitiesEvent<BlockEntity> event) {
        BlockEntity blockEntity = event.getObject();
        BlockEntityEndpointRegistration registration = findBlockEntityEndpoint(blockEntity);
        if (registration == null) {
            return;
        }

        CapabilityAttachUtil.addSided(
                event,
                registration.attachmentPath("universal"),
                UniversalEnergyCapabilities.ENERGY,
                side -> registration.factory().create(blockEntity, side));

        List<UniversalEndpointExporter> exporters;
        synchronized (UniversalEnergyRuntime.class) {
            exporters = List.copyOf(EXPORTERS.values());
        }

        for (UniversalEndpointExporter exporter : exporters) {
            if (registration.nativeProtocols().contains(exporter.protocol().id())) {
                continue;
            }
            try {
                exporter.attach(event, registration);
            } catch (RuntimeException | LinkageError e) {
                UniversalEnergyBridge.LOGGER.error(
                        "Failed to expose endpoint {} through protocol {}",
                        registration.id(), exporter.protocol().id(), e);
            }
        }
    }
}
