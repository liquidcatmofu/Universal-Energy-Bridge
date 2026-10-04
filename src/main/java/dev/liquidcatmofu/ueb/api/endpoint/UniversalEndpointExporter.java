package dev.liquidcatmofu.ueb.api.endpoint;

import dev.liquidcatmofu.ueb.api.protocol.EnergyProtocol;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;

/**
 * Forge 1.20.1 exposure hook that publishes a Universal endpoint through another protocol.
 *
 * <p>Protocol modules may register exporters externally. The runtime skips an exporter when
 * the endpoint registration declares that protocol as native, preventing UEB from wrapping a
 * native capability back into itself.</p>
 */
public interface UniversalEndpointExporter {

    EnergyProtocol<?> protocol();

    void attach(AttachCapabilitiesEvent<BlockEntity> event,
                BlockEntityEndpointRegistration registration);
}
