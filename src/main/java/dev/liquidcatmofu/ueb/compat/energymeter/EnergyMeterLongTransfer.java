package dev.liquidcatmofu.ueb.compat.energymeter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Overflow-safe fair distribution for signed-long native energy APIs. */
public final class EnergyMeterLongTransfer {
    public interface Sink {
        long simulate(long amount);
        long execute(long amount);
    }

    private EnergyMeterLongTransfer() {}

    public static long transfer(long amount, List<Sink> sinks, boolean simulate) {
        if (amount <= 0 || sinks.isEmpty()) {
            return 0;
        }

        List<State> active = new ArrayList<>(sinks.size());
        long totalCapacity = 0;
        for (Sink sink : sinks) {
            long capacity = clamp(sink.simulate(amount), amount);
            if (capacity <= 0) {
                continue;
            }
            active.add(new State(sink, capacity));
            totalCapacity = saturatingAdd(totalCapacity, capacity);
            if (totalCapacity >= amount) {
                totalCapacity = amount;
            }
        }

        if (simulate || active.isEmpty()) {
            return Math.min(amount, totalCapacity);
        }

        long remaining = amount;
        while (remaining > 0 && !active.isEmpty()) {
            long share = remaining / active.size();
            if (share <= 0) {
                share = 1;
            }

            boolean progressed = false;
            Iterator<State> iterator = active.iterator();
            while (iterator.hasNext() && remaining > 0) {
                State state = iterator.next();
                long requested = Math.min(Math.min(share, state.capacity), remaining);
                if (requested <= 0) {
                    iterator.remove();
                    continue;
                }

                long accepted = clamp(state.sink.execute(requested), requested);
                if (accepted > 0) {
                    remaining -= accepted;
                    state.capacity -= accepted;
                    progressed = true;
                }

                // If execute accepted less than simulation promised, do not hammer the
                // same output again during this receive call; reroute to other outputs.
                if (accepted < requested || state.capacity <= 0) {
                    iterator.remove();
                }
            }

            if (!progressed) {
                break;
            }
        }
        return amount - remaining;
    }

    private static long clamp(long value, long max) {
        return value <= 0 ? 0 : Math.min(value, max);
    }

    private static long saturatingAdd(long left, long right) {
        return left >= Long.MAX_VALUE - right ? Long.MAX_VALUE : left + right;
    }

    private static final class State {
        private final Sink sink;
        private long capacity;

        private State(Sink sink, long capacity) {
            this.sink = sink;
            this.capacity = capacity;
        }
    }
}
