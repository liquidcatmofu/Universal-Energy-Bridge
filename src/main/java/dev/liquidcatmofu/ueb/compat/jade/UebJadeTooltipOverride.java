package dev.liquidcatmofu.ueb.compat.jade;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;
import snownee.jade.addon.universal.EnergyStorageProvider;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.Identifiers;
import snownee.jade.api.TooltipPosition;
import snownee.jade.api.config.IPluginConfig;

import java.util.Objects;

/**
 * Restores Jade's standard energy bar after Mekanism removes it for foreign blocks.
 *
 * <p>Mekanism's Jade integration deliberately removes Jade's universal energy element
 * whenever it emits Mek data. UEB exposes Mekanism Strict Energy on foreign endpoints
 * such as a Draconic Energy Pylon, so Mekanism can otherwise replace the long UEB view
 * with its own presentation. For native Mekanism/Mekanism Extras block entities we keep
 * the native Mekanism renderer because it can represent FloatingLong values beyond long.</p>
 */
public enum UebJadeTooltipOverride implements IBlockComponentProvider {
    INSTANCE;

    private static final ResourceLocation MEKANISM_ENERGY =
            Objects.requireNonNull(ResourceLocation.tryBuild("mekanism", "energy"));

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!UebJadePlugin.ENERGY_UID.toString()
                .equals(accessor.getServerData().getString("JadeEnergyStorageUid"))) {
            return;
        }

        BlockEntity blockEntity = accessor.getBlockEntity();
        if (blockEntity != null) {
            ResourceLocation typeId = ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(blockEntity.getType());
            if (typeId != null && isNativeMekanismNamespace(typeId.getNamespace())) {
                return;
            }
        }

        // Mekanism runs at TAIL and may have removed Jade's universal energy element.
        // Remove either possible energy representation and append the UEB-backed one last.
        tooltip.remove(Identifiers.UNIVERSAL_ENERGY_STORAGE);
        tooltip.remove(MEKANISM_ENERGY);
        EnergyStorageProvider.append(tooltip, accessor, config);
    }

    private static boolean isNativeMekanismNamespace(String namespace) {
        return "mekanism".equals(namespace) || "mekanism_extras".equals(namespace);
    }

    @Override
    public ResourceLocation getUid() {
        return UebJadePlugin.TOOLTIP_OVERRIDE_UID;
    }

    @Override
    public int getDefaultPriority() {
        return TooltipPosition.TAIL + 100;
    }
}
