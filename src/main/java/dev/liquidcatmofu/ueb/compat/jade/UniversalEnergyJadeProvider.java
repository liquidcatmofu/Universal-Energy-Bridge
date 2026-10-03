package dev.liquidcatmofu.ueb.compat.jade;

import dev.liquidcatmofu.ueb.api.IUniversalEnergyStorage;
import dev.liquidcatmofu.ueb.api.UniversalEnergyCapabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.Accessor;
import snownee.jade.api.TooltipPosition;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.EnergyView;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ViewGroup;

import java.util.List;

/**
 * Uses Jade's native energy bar, but sources the numbers from UEB's long capability
 * instead of Forge Energy's int-limited IEnergyStorage.
 */
public enum UniversalEnergyJadeProvider implements
        IServerExtensionProvider<BlockEntity, CompoundTag>,
        IClientExtensionProvider<CompoundTag, EnergyView> {

    INSTANCE;

    @Override
    public ResourceLocation getUid() {
        return UebJadePlugin.ENERGY_UID;
    }

    @Override
    public int getDefaultPriority() {
        // Jade's built-in Forge Energy provider is BODY + 1000.
        // Run before it, returning null whenever UEB does not own the endpoint.
        return TooltipPosition.BODY + 100;
    }

    @Override
    public List<ViewGroup<CompoundTag>> getGroups(
            ServerPlayer player,
            ServerLevel world,
            BlockEntity target,
            boolean showDetails) {

        IUniversalEnergyStorage storage = target
                .getCapability(UniversalEnergyCapabilities.ENERGY, null)
                .resolve()
                .orElse(null);

        if (storage == null) {
            return null;
        }

        long capacity = storage.getCapacity();
        if (capacity <= 0) {
            return null;
        }

        long stored = Math.max(0, storage.getStored());
        if (stored > capacity) {
            stored = capacity;
        }

        ViewGroup<CompoundTag> group =
                new ViewGroup<>(List.of(EnergyView.of(stored, capacity)));
        group.getExtraData().putString("Unit", "FE");
        return List.of(group);
    }

    @Override
    public List<ClientViewGroup<EnergyView>> getClientGroups(
            Accessor<?> accessor,
            List<ViewGroup<CompoundTag>> groups) {
        return ClientViewGroup.map(
                groups,
                tag -> EnergyView.read(tag, "FE"),
                null);
    }
}
