package dev.liquidcatmofu.ueb.api;

/**
 * Common signed-long energy view used by Universal Energy Bridge.
 *
 * <p>The canonical unit is one Forge Energy equivalent (1 FE). Implementations for
 * energy systems with a different native unit are responsible for conversion.</p>
 *
 * <p>Implementations whose exact stored amount/capacity exceeds {@link Long#MAX_VALUE}
 * should saturate {@link #getStored()} and {@link #getCapacity()} at Long.MAX_VALUE.
 * Transfer methods intentionally remain signed-long because the surrounding 1.20.1
 * high-throughput APIs (BrandonsCore OP, Flux Networks and AE2 MEStorage) are signed-long.</p>
 */
public interface IUniversalEnergyStorage {

    long insert(long amount, boolean simulate);

    long extract(long amount, boolean simulate);

    long getStored();

    long getCapacity();

    default boolean canInsert() {
        return insert(1, true) > 0;
    }

    default boolean canExtract() {
        return extract(1, true) > 0;
    }
}
