package dev.liquidcatmofu.ueb.compat.draconic;

import com.brandon3055.draconicevolution.blocks.tileentity.TileCreativeOPCapacitor;
import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyPylon;
import dev.liquidcatmofu.ueb.UniversalEnergyBridge;
import dev.liquidcatmofu.ueb.api.UniversalEnergyRegistration;
import dev.liquidcatmofu.ueb.api.endpoint.BlockEntityEndpointRegistration;
import dev.liquidcatmofu.ueb.api.protocol.ProtocolIds;
import dev.liquidcatmofu.ueb.compat.CompatRegistration;
import dev.liquidcatmofu.ueb.config.BridgeConfig;

/**
 * Registers Draconic Evolution's long-capable OP endpoints with the generic UEB runtime.
 *
 * <p>OP remains native and is never re-exported by UEB. Universal is provided by the endpoint
 * factory, while optional Mekanism/Flux views are produced by generic exporters. AppliedFlux
 * remains an ecosystem integration and is intentionally handled separately.</p>
 */
public final class DraconicEndpointCompat implements CompatRegistration {

    @Override
    public void register() {
        UniversalEnergyRegistration.registerBlockEntityEndpoint(
                BlockEntityEndpointRegistration.builder(
                                UniversalEnergyBridge.id("draconic_energy_pylon"),
                                blockEntity -> blockEntity instanceof TileEnergyPylon,
                                (blockEntity, side) ->
                                        new DraconicUniversalEnergyStorage((TileEnergyPylon) blockEntity))
                        .nativeProtocol(ProtocolIds.BRANDONSCORE_OP)
                        .exportProtocolWhen(
                                ProtocolIds.MEKANISM_STRICT,
                                () -> BridgeConfig.DRACONIC_TO_MEKANISM.get())
                        .exportProtocolWhen(
                                ProtocolIds.FLUX_NETWORKS,
                                () -> BridgeConfig.DRACONIC_TO_FLUX_NETWORKS.get())
                        .build());

        UniversalEnergyRegistration.registerBlockEntityEndpoint(
                BlockEntityEndpointRegistration.builder(
                                UniversalEnergyBridge.id("draconic_creative_source"),
                                blockEntity -> blockEntity instanceof TileCreativeOPCapacitor,
                                (blockEntity, side) ->
                                        new DraconicCapabilityEnergyStorage(blockEntity, side))
                        .nativeProtocol(ProtocolIds.BRANDONSCORE_OP)
                        // Preserve alpha.22 behavior: Creative OP source had a Flux long view,
                        // but no Mekanism Strict Energy export.
                        .exportProtocolWhen(ProtocolIds.MEKANISM_STRICT, () -> false)
                        .exportProtocolWhen(
                                ProtocolIds.FLUX_NETWORKS,
                                () -> BridgeConfig.DRACONIC_TO_FLUX_NETWORKS.get())
                        .build());
    }
}
