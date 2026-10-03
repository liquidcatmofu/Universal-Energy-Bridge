package dev.liquidcatmofu.ueb.compat.mekanism;

import mekanism.common.tile.TileEntityEnergyCube;
import mekanism.common.tile.TileEntityQuantumEntangloporter;
import mekanism.common.tile.multiblock.TileEntityInductionPort;
import mekanism.common.tile.transmitter.TileEntityUniversalCable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public final class MekanismCompatTargets {
    private static final String MEKANISM_EXTRAS = "mekanism_extras";
    private static final String EVOLVED_MEKANISM_EXTRAS = "emextras";

    private MekanismCompatTargets() {}

    public static boolean isLargeEnergyEndpoint(BlockEntity blockEntity) {
        return blockEntity instanceof TileEntityEnergyCube
                || blockEntity instanceof TileEntityInductionPort
                || blockEntity instanceof TileEntityQuantumEntangloporter
                || isMekanismExtrasLargeEnergyEndpoint(blockEntity);
    }

    public static boolean isDraconicEndpoint(BlockEntity blockEntity) {
        return isLargeEnergyEndpoint(blockEntity) || isUniversalCableEndpoint(blockEntity);
    }

    public static boolean isAppliedFluxEndpoint(BlockEntity blockEntity) {
        // AppliedFlux already has a dedicated direct handler for the vanilla Mekanism
        // Induction Port, but not for Mekanism Extras' reinforced matrix port.
        return blockEntity instanceof TileEntityEnergyCube
                || blockEntity instanceof TileEntityQuantumEntangloporter
                || isMekanismExtrasLargeEnergyEndpoint(blockEntity);
    }

    public static boolean isQuantumEntangloporter(BlockEntity blockEntity) {
        return blockEntity instanceof TileEntityQuantumEntangloporter;
    }

    public static boolean isUniversalCableEndpoint(BlockEntity blockEntity) {
        if (blockEntity instanceof TileEntityUniversalCable) {
            return true;
        }

        // Evolved Mekanism Extras has its own transmitter tile hierarchy rather than
        // subclassing Mekanism's TileEntityUniversalCable. Keep this optional by
        // recognizing only that addon's cable registry IDs; the actual energy view is
        // still resolved lazily through Mekanism STRICT_ENERGY.
        ResourceLocation id = registryId(blockEntity);
        return id != null
                && EVOLVED_MEKANISM_EXTRAS.equals(id.getNamespace())
                && id.getPath().endsWith("_universal_cable");
    }

    public static boolean isMekanismExtrasLargeEnergyEndpoint(BlockEntity blockEntity) {
        ResourceLocation id = registryId(blockEntity);
        if (id == null || !MEKANISM_EXTRAS.equals(id.getNamespace())) {
            return false;
        }
        String path = id.getPath();
        return "reinforced_induction_port".equals(path) || path.endsWith("_energy_cube");
    }

    private static @Nullable ResourceLocation registryId(BlockEntity blockEntity) {
        return ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(blockEntity.getType());
    }
}
