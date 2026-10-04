package dev.liquidcatmofu.ueb.api.endpoint;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

/**
 * Declares a family of BlockEntity endpoints that UEB may expose through Universal and
 * compatible protocol exporters.
 */
public record BlockEntityEndpointRegistration(
        ResourceLocation id,
        Predicate<BlockEntity> matcher,
        UniversalEndpointFactory factory,
        Set<ResourceLocation> nativeProtocols,
        Map<ResourceLocation, BooleanSupplier> exportConditions
) {
    public BlockEntityEndpointRegistration {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(matcher, "matcher");
        Objects.requireNonNull(factory, "factory");
        nativeProtocols = Set.copyOf(Objects.requireNonNull(nativeProtocols, "nativeProtocols"));
        exportConditions = Map.copyOf(Objects.requireNonNull(exportConditions, "exportConditions"));
    }

    public static Builder builder(ResourceLocation id, Predicate<BlockEntity> matcher,
                                  UniversalEndpointFactory factory) {
        return new Builder(id, matcher, factory);
    }

    public String attachmentPath(String suffix) {
        return "endpoint/" + id.getNamespace() + "/" + id.getPath() + "/" + suffix;
    }

    /**
     * Returns whether UEB may expose this endpoint through the requested protocol.
     *
     * <p>Protocols are allowed by default. Registrations only need to add a condition when
     * an export is config-gated or intentionally unsupported for that endpoint family.</p>
     */
    public boolean shouldExport(ResourceLocation protocolId) {
        BooleanSupplier condition = exportConditions.get(protocolId);
        return condition == null || condition.getAsBoolean();
    }

    public static final class Builder {
        private final ResourceLocation id;
        private final Predicate<BlockEntity> matcher;
        private final UniversalEndpointFactory factory;
        private final Set<ResourceLocation> nativeProtocols = new LinkedHashSet<>();
        private final Map<ResourceLocation, BooleanSupplier> exportConditions = new LinkedHashMap<>();

        private Builder(ResourceLocation id, Predicate<BlockEntity> matcher,
                        UniversalEndpointFactory factory) {
            this.id = Objects.requireNonNull(id, "id");
            this.matcher = Objects.requireNonNull(matcher, "matcher");
            this.factory = Objects.requireNonNull(factory, "factory");
        }

        public Builder nativeProtocol(ResourceLocation protocolId) {
            nativeProtocols.add(Objects.requireNonNull(protocolId, "protocolId"));
            return this;
        }

        /**
         * Adds a dynamic condition for exporting this endpoint through one protocol.
         *
         * <p>The supplier is evaluated when capabilities are attached, so Forge config values
         * may be used without freezing their state during common setup.</p>
         */
        public Builder exportProtocolWhen(ResourceLocation protocolId, BooleanSupplier condition) {
            exportConditions.put(
                    Objects.requireNonNull(protocolId, "protocolId"),
                    Objects.requireNonNull(condition, "condition"));
            return this;
        }

        public BlockEntityEndpointRegistration build() {
            return new BlockEntityEndpointRegistration(
                    id, matcher, factory, nativeProtocols, exportConditions);
        }
    }
}
