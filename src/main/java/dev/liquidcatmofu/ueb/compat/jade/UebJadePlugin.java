package dev.liquidcatmofu.ueb.compat.jade;

import dev.liquidcatmofu.ueb.UniversalEnergyBridge;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

/**
 * Jade integration loaded only when Jade itself is present.
 *
 * <p>Registers a higher-priority energy storage provider for block entities that
 * expose UEB's signed-long Universal Energy capability. Jade's normal Forge Energy
 * provider remains the fallback for every other block.</p>
 */
@WailaPlugin("jade")
public final class UebJadePlugin implements IWailaPlugin {
    public static final ResourceLocation ENERGY_UID =
            new ResourceLocation(UniversalEnergyBridge.MOD_ID, "universal_energy");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerEnergyStorage(UniversalEnergyJadeProvider.INSTANCE, BlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEnergyStorageClient(UniversalEnergyJadeProvider.INSTANCE);
    }
}
