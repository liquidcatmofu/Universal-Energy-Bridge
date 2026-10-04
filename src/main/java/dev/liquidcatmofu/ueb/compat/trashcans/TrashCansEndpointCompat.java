package dev.liquidcatmofu.ueb.compat.trashcans;

import dev.liquidcatmofu.ueb.UniversalEnergyBridge;
import dev.liquidcatmofu.ueb.api.UniversalEnergyRegistration;
import dev.liquidcatmofu.ueb.api.endpoint.BlockEntityEndpointRegistration;
import dev.liquidcatmofu.ueb.api.protocol.ProtocolIds;
import dev.liquidcatmofu.ueb.compat.CompatRegistration;

/**
 * Registers Trash Cans once as a Universal sink endpoint. Installed protocol exporters then
 * add OP, Mekanism Strict Energy and Flux Networks views without Trash Cans-specific pairwise
 * compat classes.
 */
public final class TrashCansEndpointCompat implements CompatRegistration {
    @Override
    public void register() {
        UniversalEnergyRegistration.registerBlockEntityEndpoint(
                BlockEntityEndpointRegistration.builder(
                                UniversalEnergyBridge.id("trashcans_energy_sink"),
                                TrashCansSupport::isEnergySink,
                                TrashCanUniversalEnergyStorage::new)
                        .nativeProtocol(ProtocolIds.FORGE_ENERGY)
                        .build());
    }
}
