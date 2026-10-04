package dev.liquidcatmofu.ueb.compat.mekanism;

import dev.liquidcatmofu.ueb.UniversalEnergyBridge;
import dev.liquidcatmofu.ueb.api.UniversalEnergyRegistration;
import dev.liquidcatmofu.ueb.api.endpoint.BlockEntityEndpointRegistration;
import dev.liquidcatmofu.ueb.api.protocol.ProtocolIds;
import dev.liquidcatmofu.ueb.compat.CompatRegistration;
import dev.liquidcatmofu.ueb.config.BridgeConfig;
import mekanism.common.tile.TileEntityEnergyCube;

/**
 * Registers Mekanism Energy Cubes with the generic UEB runtime.
 *
 * <p>Strict Energy and Forge Energy remain native. Universal resolves the sided Strict
 * Energy capability on every operation so Mekanism side configuration changes remain
 * authoritative. OP is an optional generic export; Flux is intentionally not added yet
 * because the pre-registry implementation did not expose a Flux capability on cubes.</p>
 */
public final class MekanismEndpointCompat implements CompatRegistration {

    @Override
    public void register() {
        UniversalEnergyRegistration.registerBlockEntityEndpoint(
                BlockEntityEndpointRegistration.builder(
                                UniversalEnergyBridge.id("mekanism_energy_cube"),
                                blockEntity -> blockEntity instanceof TileEntityEnergyCube,
                                MekanismUniversalEnergyStorage::new)
                        .nativeProtocol(ProtocolIds.MEKANISM_STRICT)
                        .nativeProtocol(ProtocolIds.FORGE_ENERGY)
                        .exportProtocolWhen(
                                ProtocolIds.BRANDONSCORE_OP,
                                () -> BridgeConfig.MEKANISM_TO_DRACONIC.get())
                        .exportProtocolWhen(ProtocolIds.FLUX_NETWORKS, () -> false)
                        .build());
    }
}
