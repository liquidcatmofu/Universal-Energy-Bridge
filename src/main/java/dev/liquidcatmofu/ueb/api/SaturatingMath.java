package dev.liquidcatmofu.ueb.api;

public final class SaturatingMath {
    private SaturatingMath() {}

    public static long add(long a, long b) {
        if (a < 0 || b < 0) {
            throw new IllegalArgumentException("Energy values must be non-negative");
        }
        if (Long.MAX_VALUE - a < b) {
            return Long.MAX_VALUE;
        }
        return a + b;
    }

    public static long subtractFloorZero(long a, long b) {
        if (a <= b) {
            return 0;
        }
        return a - b;
    }
}
