package dev.liquidcatmofu.ueb.compat.draconic;

import com.brandon3055.brandonscore.capability.CapabilityOP;
import dev.liquidcatmofu.ueb.api.CapabilityAttachUtil;
import dev.liquidcatmofu.ueb.api.IUniversalEnergyStorage;
import dev.liquidcatmofu.ueb.api.endpoint.BlockEntityEndpointRegistration;
import dev.liquidcatmofu.ueb.api.endpoint.UniversalEndpointExporter;
import dev.liquidcatmofu.ueb.api.protocol.EnergyProtocol;
import dev.liquidcatmofu.ueb.api.protocol.ProtocolIds;
import dev.liquidcatmofu.ueb.api.protocol.StandardAmountDomains;
import dev.liquidcatmofu.ueb.api.protocol.StandardEnergyProtocols;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public final class DraconicUniversalExporter implements UniversalEndpointExporter {
    private static final EnergyProtocol<Long> PROTOCOL = new EnergyProtocol<>(
            ProtocolIds.BRANDONSCORE_OP,
            StandardAmountDomains.SIGNED_LONG,
            StandardEnergyProtocols.SCALAR_ENERGY_SEMANTICS);

    @Override
    public EnergyProtocol<?> protocol() {
        return PROTOCOL;
    }

    @Override
    public void attach(AttachCapabilitiesEvent<BlockEntity> event,
                       BlockEntityEndpointRegistration registration) {
        BlockEntity blockEntity = event.getObject();
        CapabilityAttachUtil.addSided(
                event,
                registration.attachmentPath("brandonscore_op"),
                CapabilityOP.OP,
                side -> {
                    IUniversalEnergyStorage endpoint = registration.factory().create(blockEntity, side);
                    return endpoint == null ? null : new UniversalToOPStorage(endpoint);
                });
    }
}
