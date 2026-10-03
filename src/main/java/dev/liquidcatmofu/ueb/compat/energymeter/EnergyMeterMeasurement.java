package dev.liquidcatmofu.ueb.compat.energymeter;

/**
 * Mixin bridge into Energy Meter's existing measurement accumulator.
 *
 * <p>The transfer path stays native. Only the accepted amount is converted to an
 * FE-equivalent double for Energy Meter's existing display/statistics model.</p>
 */
public interface EnergyMeterMeasurement {
    boolean ueb$isMeterReady();

    void ueb$recordTransferFe(double amount);
}
