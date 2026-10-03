package dev.liquidcatmofu.ueb.util;

/**
 * Overflow-safe signed-long arithmetic for compatibility code that aggregates values
 * from APIs whose individual transfer unit is already a signed long.
 */
public final class SaturatingLongMath {
    private SaturatingLongMath() {}

    public static long add(long a, long b) {
        if (b > 0 && a > Long.MAX_VALUE - b) {
            return Long.MAX_VALUE;
        }
        if (b < 0 && a < Long.MIN_VALUE - b) {
            return Long.MIN_VALUE;
        }
        return a + b;
    }

    public static long subtract(long a, long b) {
        if (b > 0 && a < Long.MIN_VALUE + b) {
            return Long.MIN_VALUE;
        }
        if (b < 0 && a > Long.MAX_VALUE + b) {
            return Long.MAX_VALUE;
        }
        return a - b;
    }

    public static long dividePreservingSaturation(long value, long divisor) {
        if (value == Long.MAX_VALUE || value == Long.MIN_VALUE) {
            return value;
        }
        return value / divisor;
    }
}
