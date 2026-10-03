package dev.liquidcatmofu.ueb.compat.draconic.exact;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * A tiny exact moving-average tracker for Energy Core I/O.
 *
 * <p>BrandonsCore's IOTracker averages 19 completed samples (the current tick is
 * skipped). This tracker mirrors that behaviour but keeps every per-tick total and
 * the average calculation in BigInteger, so aggregate I/O can exceed signed long.</p>
 */
public final class ExactEnergyIOTracker {
    private static final int WINDOW = 20;
    private static final int SAMPLE_COUNT = WINDOW - 1;
    private static final BigInteger DIVISOR = BigInteger.valueOf(SAMPLE_COUNT);

    private final long[] tickTags = new long[WINDOW];
    private final BigInteger[] input = new BigInteger[WINDOW];
    private final BigInteger[] output = new BigInteger[WINDOW];

    public ExactEnergyIOTracker() {
        Arrays.fill(tickTags, Long.MIN_VALUE);
        Arrays.fill(input, BigInteger.ZERO);
        Arrays.fill(output, BigInteger.ZERO);
    }

    public void recordInput(long gameTick, long amount) {
        if (amount > 0) {
            int index = prepare(gameTick);
            input[index] = input[index].add(BigInteger.valueOf(amount));
        }
    }

    public void recordOutput(long gameTick, long amount) {
        if (amount > 0) {
            int index = prepare(gameTick);
            output[index] = output[index].add(BigInteger.valueOf(amount));
        }
    }

    public BigInteger averageInput(long currentTick) {
        return average(input, currentTick);
    }

    public BigInteger averageOutput(long currentTick) {
        return average(output, currentTick);
    }

    private int prepare(long gameTick) {
        int index = Math.floorMod(gameTick, WINDOW);
        if (tickTags[index] != gameTick) {
            tickTags[index] = gameTick;
            input[index] = BigInteger.ZERO;
            output[index] = BigInteger.ZERO;
        }
        return index;
    }

    private BigInteger average(BigInteger[] values, long currentTick) {
        BigInteger sum = BigInteger.ZERO;
        for (int offset = 1; offset <= SAMPLE_COUNT; offset++) {
            long tick = currentTick - offset;
            int index = Math.floorMod(tick, WINDOW);
            if (tickTags[index] == tick) {
                sum = sum.add(values[index]);
            }
        }

        BigInteger[] divRem = sum.divideAndRemainder(DIVISOR);
        // All tracked amounts are non-negative. Round to nearest integer OP/t.
        if (divRem[1].shiftLeft(1).compareTo(DIVISOR) >= 0) {
            return divRem[0].add(BigInteger.ONE);
        }
        return divRem[0];
    }
}
