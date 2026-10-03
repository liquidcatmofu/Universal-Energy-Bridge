package dev.liquidcatmofu.ueb.compat.mekanism;

import mekanism.common.tile.TileEntityEnergyCube;
import mekanism.common.tile.TileEntityQuantumEntangloporter;
import mekanism.common.tile.multiblock.TileEntityInductionPort;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class MekanismCompatTargets {
    private MekanismCompatTargets() {}

    public static boolean isLargeEnergyEndpoint(BlockEntity blockEntity) {
        return blockEntity instanceof TileEntityEnergyCube
                || blockEntity instanceof TileEntityInductionPort
                || blockEntity instanceof TileEntityQuantumEntangloporter;
    }

    public static boolean isAppliedFluxEndpoint(BlockEntity blockEntity) {
        // AppliedFlux already has a dedicated direct Induction Port handler.
        return blockEntity instanceof TileEntityEnergyCube
                || blockEntity instanceof TileEntityQuantumEntangloporter;
    }

    public static boolean isQuantumEntangloporter(BlockEntity blockEntity) {
        return blockEntity instanceof TileEntityQuantumEntangloporter;
    }
}
