package dev.liquidcatmofu.ueb.api;

import dev.liquidcatmofu.ueb.api.conversion.EnergyConversion;
import dev.liquidcatmofu.ueb.api.endpoint.BlockEntityEndpointRegistration;
import dev.liquidcatmofu.ueb.api.endpoint.UniversalEndpointExporter;
import dev.liquidcatmofu.ueb.api.protocol.EnergyProtocol;
import dev.liquidcatmofu.ueb.core.UniversalEnergyRuntime;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Public registration facade for UEB protocol, conversion and endpoint extensions.
 *
 * <p>Registrations should be made during mod/common setup, before worlds create BlockEntities.</p>
 */
public final class UniversalEnergyRegistration {
    private UniversalEnergyRegistration() {}

    public static void registerProtocol(EnergyProtocol<?> protocol) {
        UniversalEnergyRuntime.registerProtocol(protocol);
    }

    public static void registerConversion(EnergyConversion<?, ?> conversion) {
        UniversalEnergyRuntime.registerConversion(conversion);
    }

    public static void registerBlockEntityEndpoint(BlockEntityEndpointRegistration registration) {
        UniversalEnergyRuntime.registerBlockEntityEndpoint(registration);
    }

    public static void registerExporter(UniversalEndpointExporter exporter) {
        UniversalEnergyRuntime.registerExporter(exporter);
    }

    public static List<EnergyProtocol<?>> protocols() {
        return UniversalEnergyRuntime.protocols();
    }

    public static List<EnergyConversion<?, ?>> conversions() {
        return UniversalEnergyRuntime.conversions();
    }

    @Nullable
    public static BlockEntityEndpointRegistration findBlockEntityEndpoint(BlockEntity blockEntity) {
        return UniversalEnergyRuntime.findBlockEntityEndpoint(blockEntity);
    }
}
