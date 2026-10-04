package dev.liquidcatmofu.ueb.api.protocol;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/** Common numeric representations used by built-in scalar energy protocols. */
public final class StandardAmountDomains {
    public static final AmountDomain<Long> SIGNED_LONG = new AmountDomain<>() {
        private final ResourceLocation id = id("signed_long");

        @Override
        public ResourceLocation id() {
            return id;
        }

        @Override
        public Class<Long> valueType() {
            return Long.class;
        }

        @Override
        public Long zero() {
            return 0L;
        }

        @Override
        public int compare(Long left, Long right) {
            return Long.compare(left, right);
        }
    };

    public static final AmountDomain<Integer> SIGNED_INT = new AmountDomain<>() {
        private final ResourceLocation id = id("signed_int");

        @Override
        public ResourceLocation id() {
            return id;
        }

        @Override
        public Class<Integer> valueType() {
            return Integer.class;
        }

        @Override
        public Integer zero() {
            return 0;
        }

        @Override
        public int compare(Integer left, Integer right) {
            return Integer.compare(left, right);
        }
    };

    private StandardAmountDomains() {}

    private static ResourceLocation id(String path) {
        return Objects.requireNonNull(ResourceLocation.tryBuild("universal_energy_bridge", path));
    }
}
