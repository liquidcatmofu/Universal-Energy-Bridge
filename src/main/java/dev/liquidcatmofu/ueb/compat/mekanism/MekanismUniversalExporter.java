package dev.liquidcatmofu.ueb.compat.mekanism;

import dev.liquidcatmofu.ueb.api.CapabilityAttachUtil;
import dev.liquidcatmofu.ueb.api.IUniversalEnergyStorage;
import dev.liquidcatmofu.ueb.api.endpoint.BlockEntityEndpointRegistration;
import dev.liquidcatmofu.ueb.api.endpoint.UniversalEndpointExporter;
import dev.liquidcatmofu.ueb.api.protocol.AmountDomain;
import dev.liquidcatmofu.ueb.api.protocol.EnergyProtocol;
import dev.liquidcatmofu.ueb.api.protocol.ProtocolIds;
import dev.liquidcatmofu.ueb.api.protocol.StandardEnergyProtocols;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import java.util.Objects;

public final class MekanismUniversalExporter implements UniversalEndpointExporter {
    private static final AmountDomain<FloatingLong> FLOATING_LONG = new AmountDomain<>() {
        private final ResourceLocation id = Objects.requireNonNull(
                ResourceLocation.tryBuild("mekanism", "floating_long"));

        @Override
        public ResourceLocation id() {
            return id;
        }

        @Override
        public Class<FloatingLong> valueType() {
            return FloatingLong.class;
        }

        @Override
        public FloatingLong zero() {
            return FloatingLong.ZERO;
        }

        @Override
        public int compare(FloatingLong left, FloatingLong right) {
            if (left.smallerThan(right)) {
                return -1;
            }
            if (left.greaterThan(right)) {
                return 1;
            }
            return 0;
        }
    };

    private static final EnergyProtocol<FloatingLong> PROTOCOL = new EnergyProtocol<>(
            ProtocolIds.MEKANISM_STRICT,
            FLOATING_LONG,
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
                registration.attachmentPath("mekanism_strict"),
                Capabilities.STRICT_ENERGY,
                side -> {
                    IUniversalEnergyStorage endpoint = registration.factory().create(blockEntity, side);
                    return endpoint == null ? null : new UniversalToMekanismEnergyHandler(endpoint);
                });
    }
}
