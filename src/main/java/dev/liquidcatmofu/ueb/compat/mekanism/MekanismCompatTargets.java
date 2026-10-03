package dev.liquidcatmofu.ueb.compat.mekanism;

import mekanism.common.tile.TileEntityQuantumEntangloporter;
import mekanism.common.tile.multiblock.TileEntityInductionPort;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class MekanismCompatTargets {
    private MekanismCompatTargets() {}

    public static boolean isLargeEnergyEndpoint(BlockEntity blockEntity) {
        return blockEntity instanceof TileEntityInductionPort
                || blockEntity instanceof TileEntityQuantumEntangloporter;
    }

    public static boolean isQuantumEntangloporter(BlockEntity blockEntity) {
        return blockEntity instanceof TileEntityQuantumEntangloporter;
    }
}
