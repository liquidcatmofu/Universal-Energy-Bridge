package dev.liquidcatmofu.ueb.compat.jade;

import dev.liquidcatmofu.ueb.UniversalEnergyBridge;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.TooltipPosition;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IDisplayHelper;

/**
 * Sneak/show-details diagnostics for Flux Networks devices.
 */
public enum FluxJadeDetailsProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final ResourceLocation UID = UniversalEnergyBridge.id("jade_flux_details");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag root = accessor.getServerData().getCompound(FluxJadeServerDataProvider.ROOT);
        if (!root.contains(FluxJadeServerDataProvider.DETAILS)) {
            return;
        }

        CompoundTag details = root.getCompound(FluxJadeServerDataProvider.DETAILS);

        Component type = Component.Serializer.fromJson(details.getString(FluxJadeServerDataProvider.TYPE));
        if (type != null) {
            tooltip.add(Component.translatable("jade.universal_energy_bridge.flux.type", type)
                    .withStyle(ChatFormatting.GRAY));
        }

        String network = details.getString(FluxJadeServerDataProvider.NETWORK);
        if (!network.isEmpty()) {
            tooltip.add(Component.translatable("jade.universal_energy_bridge.flux.network", network)
                    .withStyle(ChatFormatting.GRAY));
        }

        tooltip.add(Component.translatable(
                        "jade.universal_energy_bridge.flux.priority",
                        details.getInt(FluxJadeServerDataProvider.PRIORITY))
                .withStyle(ChatFormatting.GRAY));

        boolean surge = details.getBoolean(FluxJadeServerDataProvider.SURGE);
        tooltip.add(Component.translatable(
                        surge
                                ? "jade.universal_energy_bridge.flux.surge_on"
                                : "jade.universal_energy_bridge.flux.surge_off")
                .withStyle(surge ? ChatFormatting.GOLD : ChatFormatting.GRAY));

        long limit = details.getLong(FluxJadeServerDataProvider.LIMIT);
        String formattedLimit = IDisplayHelper.get().humanReadableNumber(limit, "FE/t", false);
        tooltip.add(Component.translatable("jade.universal_energy_bridge.flux.limit", formattedLimit)
                .withStyle(ChatFormatting.GRAY));

        long transfer = details.getLong(FluxJadeServerDataProvider.TRANSFER);
        tooltip.add(Component.translatable(
                        "jade.universal_energy_bridge.flux.transfer",
                        formatSignedRate(transfer))
                .withStyle(ChatFormatting.GRAY));

        if (details.getBoolean(FluxJadeServerDataProvider.CAN_EDIT)) {
            boolean bypass = details.getBoolean(FluxJadeServerDataProvider.BYPASS);
            boolean chunkLoading = details.getBoolean(FluxJadeServerDataProvider.CHUNK_LOADING);
            tooltip.add(Component.translatable(
                            "jade.universal_energy_bridge.flux.bypass",
                            localizedOnOff(bypass))
                    .withStyle(bypass ? ChatFormatting.YELLOW : ChatFormatting.GRAY));
            tooltip.add(Component.translatable(
                            "jade.universal_energy_bridge.flux.chunk_loading",
                            localizedOnOff(chunkLoading))
                    .withStyle(chunkLoading ? ChatFormatting.YELLOW : ChatFormatting.GRAY));
        }
    }

    private static String formatSignedRate(long value) {
        if (value == 0) {
            return IDisplayHelper.get().humanReadableNumber(0, "FE/t", false);
        }
        double magnitude = value == Long.MIN_VALUE ? (double) Long.MAX_VALUE : Math.abs((double) value);
        String formatted = IDisplayHelper.get().humanReadableNumber(magnitude, "FE/t", false);
        return (value > 0 ? "+" : "-") + formatted;
    }

    private static Component localizedOnOff(boolean value) {
        return Component.translatable(value
                ? "jade.universal_energy_bridge.common.on"
                : "jade.universal_energy_bridge.common.off");
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public int getDefaultPriority() {
        // Put diagnostics below the energy bar rather than crowding the title.
        return TooltipPosition.BODY + 200;
    }
}
