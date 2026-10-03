package dev.liquidcatmofu.ueb.compat.draconic.exact;

/**
 * Exact BigInteger-backed I/O view mixed into Draconic Evolution's Energy Core.
 *
 * <p>The public values are decimal strings because BrandonsCore ManagedData syncs
 * strings cleanly to the Energy Core container without imposing another numeric
 * range limit.</p>
 */
public interface ExactEnergyCoreIO {
    void ueb$recordExactInput(long gameTick, long amount);

    void ueb$recordExactOutput(long gameTick, long amount);

    String ueb$getExactInputPerTick();

    String ueb$getExactOutputPerTick();
}
