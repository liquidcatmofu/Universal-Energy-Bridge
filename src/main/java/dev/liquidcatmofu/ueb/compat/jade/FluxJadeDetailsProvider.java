package dev.liquidcatmofu.ueb.compat.jade;

import dev.liquidcatmofu.ueb.UniversalEnergyBridge;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.TooltipPosition;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IDisplayHelper;

/**
 * Always-visible Flux summary plus sneak/show-details diagnostics.
 */
public enum FluxJadeDetailsProvider implements IBlockComponentProvider {
    INSTANCE;

    private static final ResourceLocation UID = UniversalEnergyBridge.id("jade_flux_details");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        CompoundTag root = accessor.getServerData().getCompound(FluxJadeServerDataProvider.ROOT);
        if (root.isEmpty()) {
            return;
        }

        addNetworkSummary(tooltip, root);
        addTransferSummary(tooltip, root);

        if (!root.contains(FluxJadeServerDataProvider.DETAILS)) {
            return;
        }

        CompoundTag details = root.getCompound(FluxJadeServerDataProvider.DETAILS);

        Component type = Component.Serializer.fromJson(details.getString(FluxJadeServerDataProvider.TYPE));
        if (type != null) {
            tooltip.add(Component.translatable("jade.universal_energy_bridge.flux.type", type)
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

    private static void addNetworkSummary(ITooltip tooltip, CompoundTag root) {
        String network = root.getString(FluxJadeServerDataProvider.NETWORK);
        if (network.isEmpty()) {
            return;
        }

        int rgb = root.getInt(FluxJadeServerDataProvider.NETWORK_COLOR) & 0xFFFFFF;
        Component coloredName = Component.literal(network)
                .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(rgb)));

        tooltip.add(Component.translatable(
                        "jade.universal_energy_bridge.flux.network",
                        coloredName)
                .withStyle(ChatFormatting.GRAY));
    }

    private static void addTransferSummary(ITooltip tooltip, CompoundTag root) {
        if (!root.contains(FluxJadeServerDataProvider.TRANSFER)) {
            return;
        }

        long transfer = root.getLong(FluxJadeServerDataProvider.TRANSFER);
        ChatFormatting color = transfer > 0
                ? ChatFormatting.GREEN
                : transfer < 0
                ? ChatFormatting.RED
                : ChatFormatting.GOLD;

        tooltip.add(Component.translatable(
                        "jade.universal_energy_bridge.flux.transfer",
                        formatSignedRate(transfer))
                .withStyle(color));
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
        // Keep the summary/details immediately below the energy bar.
        return TooltipPosition.BODY + 200;
    }
}
