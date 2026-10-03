package dev.liquidcatmofu.ueb.compat.draconic.exact;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Compact engineering-format display for arbitrary-size OP values.
 */
public final class BigEnergyFormatter {
    private static final String[] PREFIXES = {"", "K", "M", "G", "T", "P", "E", "Z", "Y"};

    private BigEnergyFormatter() {}

    public static String format(BigInteger value) {
        if (value.signum() == 0) {
            return "0";
        }

        boolean negative = value.signum() < 0;
        BigInteger magnitude = value.abs();
        int digits = magnitude.toString().length();
        int group = Math.max(0, (digits - 1) / 3);

        String formatted;
        if (group == 0) {
            formatted = magnitude.toString();
        } else if (group < PREFIXES.length) {
            BigDecimal scaled = new BigDecimal(magnitude)
                    .movePointLeft(group * 3)
                    .setScale(3, RoundingMode.HALF_UP)
                    .stripTrailingZeros();
            formatted = scaled.toPlainString() + PREFIXES[group];
        } else {
            BigDecimal scientific = new BigDecimal(magnitude)
                    .movePointLeft(digits - 1)
                    .setScale(3, RoundingMode.HALF_UP)
                    .stripTrailingZeros();
            formatted = scientific.toPlainString() + "e" + (digits - 1);
        }

        return negative ? "-" + formatted : formatted;
    }
}
