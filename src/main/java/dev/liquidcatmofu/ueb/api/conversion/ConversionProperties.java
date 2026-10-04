package dev.liquidcatmofu.ueb.api.conversion;

/**
 * Route-planning metadata for a protocol conversion edge.
 *
 * <p>UEB-owned transfer planners should prefer semantic preservation, exactness and range
 * before considering cheaper but lossy fallback paths.</p>
 */
public record ConversionProperties(
        boolean exact,
        boolean rangeLimited,
        boolean rounding,
        boolean semanticLoss,
        int cost
) {
    public ConversionProperties {
        if (cost < 0) {
            throw new IllegalArgumentException("cost must be >= 0");
        }
    }

    public static ConversionProperties exact(int cost) {
        return new ConversionProperties(true, false, false, false, cost);
    }
}
