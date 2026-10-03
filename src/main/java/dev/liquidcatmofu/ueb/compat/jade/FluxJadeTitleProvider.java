package dev.liquidcatmofu.ueb.compat.jade;

import dev.liquidcatmofu.ueb.UniversalEnergyBridge;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.Identifiers;
import snownee.jade.api.TooltipPosition;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

/**
 * Replaces "Flux Plug/Point/Storage" with the configured Flux device custom name.
 */
public enum FluxJadeTitleProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final ResourceLocation UID = UniversalEnergyBridge.id("jade_flux_title");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag root = accessor.getServerData().getCompound(FluxJadeServerDataProvider.ROOT);
        String customName = root.getString(FluxJadeServerDataProvider.CUSTOM_NAME);
        if (customName.isEmpty()) {
            return;
        }

        tooltip.remove(Identifiers.CORE_OBJECT_NAME);
        tooltip.add(0,
                IThemeHelper.get().title(Component.literal(customName)),
                Identifiers.CORE_OBJECT_NAME);
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public int getDefaultPriority() {
        // Jade's object-name provider is HEAD - 100. Replace it immediately afterwards.
        return TooltipPosition.HEAD - 50;
    }
}
