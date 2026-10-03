package dev.liquidcatmofu.ueb.compat.jade;

import dev.liquidcatmofu.ueb.UniversalEnergyBridge;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.ModList;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

/**
 * Jade integration loaded only when Jade itself is present.
 */
@WailaPlugin("jade")
public final class UebJadePlugin implements IWailaPlugin {
    public static final ResourceLocation ENERGY_UID = UniversalEnergyBridge.id("universal_energy");
    public static final ResourceLocation TOOLTIP_OVERRIDE_UID = UniversalEnergyBridge.id("universal_energy_tooltip");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerEnergyStorage(UniversalEnergyJadeProvider.INSTANCE, BlockEntity.class);
        if (ModList.get().isLoaded("fluxnetworks")) {
            FluxJadeCompat.registerCommon(registration);
        }
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEnergyStorageClient(UniversalEnergyJadeProvider.INSTANCE);
        registration.registerBlockComponent(UebJadeTooltipOverride.INSTANCE, Block.class);
        if (ModList.get().isLoaded("fluxnetworks")) {
            FluxJadeCompat.registerClient(registration);
        }
    }
}
