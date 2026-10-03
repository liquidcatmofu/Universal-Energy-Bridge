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
 * Sends Flux device metadata to Jade.
 *
 * <p>The custom device name is always public. Network/transfer summary is sent only
 * to players who can use the network. Extended diagnostic data is sent only in
 * show-details mode (normally sneak). Edit-only flags remain restricted to access
 * levels that can edit the network/device.</p>
 */
public enum FluxJadeServerDataProvider implements IServerDataProvider<BlockAccessor> {
    INSTANCE;

    static final String ROOT = "UEBFlux";
    static final String CUSTOM_NAME = "CustomName";
    static final String NETWORK = "Network";
    static final String NETWORK_COLOR = "NetworkColor";
    static final String TRANSFER = "Transfer";
    static final String DETAILS = "Details";
    static final String TYPE = "Type";
    static final String PRIORITY = "Priority";
    static final String SURGE = "Surge";
    static final String LIMIT = "Limit";
    static final String BYPASS = "Bypass";
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

        FluxNetwork network = device.getNetwork();
        if (network.isValid()) {
            AccessLevel access = network.getPlayerAccess(accessor.getPlayer());
            if (access.canUse()) {
                root.putString(NETWORK, network.getNetworkName());
                root.putInt(NETWORK_COLOR, network.getNetworkColor());
                root.putLong(TRANSFER, device.getTransferChange());

                if (accessor.showDetails()) {
                    CompoundTag details = new CompoundTag();
                    details.putString(TYPE, Component.Serializer.toJson(device.getDisplayStack().getHoverName()));
                    details.putInt(PRIORITY, device.getRawPriority());
                    details.putBoolean(SURGE, device.getSurgeMode());
                    details.putLong(LIMIT, device.getRawLimit());
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
