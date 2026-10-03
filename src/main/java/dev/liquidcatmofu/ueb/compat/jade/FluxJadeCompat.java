package dev.liquidcatmofu.ueb.compat.jade;

import net.minecraft.world.level.block.Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import sonar.fluxnetworks.common.device.TileFluxDevice;

/**
 * Isolates direct Flux Networks class references so Jade can remain optional and
 * UEB can still load when Flux Networks is absent.
 */
final class FluxJadeCompat {
    private FluxJadeCompat() {}

    static void registerCommon(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(FluxJadeServerDataProvider.INSTANCE, TileFluxDevice.class);
    }

    static void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(FluxJadeTitleProvider.INSTANCE, Block.class);
        registration.registerBlockComponent(FluxJadeDetailsProvider.INSTANCE, Block.class);
    }
}
