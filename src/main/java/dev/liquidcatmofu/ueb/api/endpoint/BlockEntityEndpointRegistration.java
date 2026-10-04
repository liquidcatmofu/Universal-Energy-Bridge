package dev.liquidcatmofu.ueb.api.endpoint;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Declares a family of BlockEntity endpoints that UEB may expose through Universal and
 * compatible protocol exporters.
 */
public record BlockEntityEndpointRegistration(
        ResourceLocation id,
        Predicate<BlockEntity> matcher,
        UniversalEndpointFactory factory,
        Set<ResourceLocation> nativeProtocols
) {
    public BlockEntityEndpointRegistration {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(matcher, "matcher");
        Objects.requireNonNull(factory, "factory");
        nativeProtocols = Set.copyOf(Objects.requireNonNull(nativeProtocols, "nativeProtocols"));
    }

    public static Builder builder(ResourceLocation id, Predicate<BlockEntity> matcher,
                                  UniversalEndpointFactory factory) {
        return new Builder(id, matcher, factory);
    }

    public String attachmentPath(String suffix) {
        return "endpoint/" + id.getNamespace() + "/" + id.getPath() + "/" + suffix;
    }

    public static final class Builder {
        private final ResourceLocation id;
        private final Predicate<BlockEntity> matcher;
        private final UniversalEndpointFactory factory;
        private final Set<ResourceLocation> nativeProtocols = new LinkedHashSet<>();

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

        public BlockEntityEndpointRegistration build() {
            return new BlockEntityEndpointRegistration(id, matcher, factory, nativeProtocols);
        }
    }
}
