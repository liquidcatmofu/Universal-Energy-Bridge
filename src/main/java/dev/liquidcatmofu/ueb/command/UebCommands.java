package dev.liquidcatmofu.ueb.command;

import com.mojang.brigadier.CommandDispatcher;
import dev.liquidcatmofu.ueb.api.IUniversalEnergyStorage;
import dev.liquidcatmofu.ueb.api.UniversalEnergyCapabilities;
import dev.liquidcatmofu.ueb.api.UniversalEnergyRegistration;
import dev.liquidcatmofu.ueb.api.endpoint.BlockEntityEndpointRegistration;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;

public final class UebCommands {
    private UebCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("ueb")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("probe")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(context -> probe(
                                        context.getSource(),
                                        BlockPosArgument.getLoadedBlockPos(context, "pos"))))));
    }

    private static int probe(CommandSourceStack source, BlockPos pos) {
        BlockEntity blockEntity = source.getLevel().getBlockEntity(pos);
        if (blockEntity == null) {
            source.sendFailure(Component.literal("No block entity at " + pos.toShortString()));
            return 0;
        }

        BlockEntityEndpointRegistration registration =
                UniversalEnergyRegistration.findBlockEntityEndpoint(blockEntity);

        IUniversalEnergyStorage storage = blockEntity
                .getCapability(UniversalEnergyCapabilities.ENERGY, null)
                .resolve()
                .orElse(null);

        if (storage == null) {
            source.sendFailure(Component.literal(
                    "No Universal Energy capability at " + pos.toShortString()
                            + " endpoint=" + registrationId(registration)
                            + " block=" + blockId(blockEntity)
                            + " blockEntityType=" + blockEntityTypeId(blockEntity)));
            return 0;
        }

        long stored = storage.getStored();
        long capacity = storage.getCapacity();
        long simulatedInput = storage.insert(Long.MAX_VALUE, true);
        long simulatedOutput = storage.extract(Long.MAX_VALUE, true);

        source.sendSuccess(() -> Component.literal(
                "UEB @ " + pos.toShortString()
                        + " endpoint=" + registrationId(registration)
                        + " stored=" + stored
                        + " capacity=" + capacity
                        + " canIn=" + storage.canInsert()
                        + " canOut=" + storage.canExtract()
                        + " simIn=" + simulatedInput
                        + " simOut=" + simulatedOutput), false);
        return 1;
    }

    private static String registrationId(BlockEntityEndpointRegistration registration) {
        return registration == null ? "legacy/none" : registration.id().toString();
    }

    private static String blockId(BlockEntity blockEntity) {
        ResourceLocation id = ForgeRegistries.BLOCKS.getKey(blockEntity.getBlockState().getBlock());
        return id == null ? "unknown" : id.toString();
    }

    private static String blockEntityTypeId(BlockEntity blockEntity) {
        ResourceLocation id = ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(blockEntity.getType());
        return id == null ? "unknown" : id.toString();
    }
}
