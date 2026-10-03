package dev.liquidcatmofu.ueb.compat.jade;

import dev.liquidcatmofu.ueb.UniversalEnergyBridge;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IServerDataProvider;
import sonar.fluxnetworks.api.network.AccessLevel;
import sonar.fluxnetworks.common.connection.FluxNetwork;
import sonar.fluxnetworks.common.device.TileFluxDevice;

/**
 * Sends Flux device metadata only when Jade needs it.
 *
 * <p>The custom device name is always sent. Diagnostic data is only sent while
 * Jade is in show-details mode (normally sneak) and only to players who can use
 * the network. Edit-only flags are restricted to FN access levels that can edit.</p>
 */
public enum FluxJadeServerDataProvider implements IServerDataProvider<BlockAccessor> {
    INSTANCE;

    static final String ROOT = "UEBFlux";
    static final String CUSTOM_NAME = "CustomName";
    static final String DETAILS = "Details";
    static final String TYPE = "Type";
    static final String NETWORK = "Network";
    static final String PRIORITY = "Priority";
    static final String SURGE = "Surge";
    static final String LIMIT = "Limit";
    static final String BYPASS = "Bypass";
    static final String TRANSFER = "Transfer";
    static final String CAN_EDIT = "CanEdit";
    static final String CHUNK_LOADING = "ChunkLoading";

    private static final ResourceLocation UID = UniversalEnergyBridge.id("jade_flux_server_data");

    @Override
    public void appendServerData(CompoundTag data, BlockAccessor accessor) {
        if (!(accessor.getBlockEntity() instanceof TileFluxDevice device)) {
            return;
        }

        CompoundTag root = new CompoundTag();
        String customName = device.getCustomName();
        if (!customName.isEmpty()) {
            root.putString(CUSTOM_NAME, customName);
        }

        if (accessor.showDetails()) {
            FluxNetwork network = device.getNetwork();
            if (network.isValid()) {
                AccessLevel access = network.getPlayerAccess(accessor.getPlayer());
                if (access.canUse()) {
                    CompoundTag details = new CompoundTag();
                    details.putString(TYPE, Component.Serializer.toJson(device.getDisplayStack().getHoverName()));
                    details.putString(NETWORK, network.getNetworkName());
                    details.putInt(PRIORITY, device.getRawPriority());
                    details.putBoolean(SURGE, device.getSurgeMode());
                    details.putLong(LIMIT, device.getRawLimit());
                    details.putLong(TRANSFER, device.getTransferChange());
                    details.putBoolean(CAN_EDIT, access.canEdit());
                    if (access.canEdit()) {
                        details.putBoolean(BYPASS, device.getDisableLimit());
                        details.putBoolean(CHUNK_LOADING, device.isForcedLoading());
                    }
                    root.put(DETAILS, details);
                }
            }
        }

        if (!root.isEmpty()) {
            data.put(ROOT, root);
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}
