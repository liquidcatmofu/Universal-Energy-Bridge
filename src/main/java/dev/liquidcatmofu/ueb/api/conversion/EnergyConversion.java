package dev.liquidcatmofu.ueb.api.conversion;

import dev.liquidcatmofu.ueb.api.protocol.EnergyProtocol;

/**
 * Directed conversion edge between two energy protocols.
 *
 * <p>{@link #convertAcceptedBack(Object)} is intentionally part of the contract: transfer
 * implementations need to map the target's actually accepted amount back into source units
 * without silently creating or destroying energy through asymmetric rounding.</p>
 */
public interface EnergyConversion<S, T> {

    EnergyProtocol<S> source();

    EnergyProtocol<T> target();

    ConversionProperties properties();

    T convertRequest(S amount);

    S convertAcceptedBack(T acceptedAmount);
}
