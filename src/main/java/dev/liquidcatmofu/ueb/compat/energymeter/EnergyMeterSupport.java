package dev.liquidcatmofu.ueb.compat.energymeter;

import com.github.almostreliable.energymeter.meter.MeterEntity;
import com.github.almostreliable.energymeter.util.TypeEnums.IO_SETTING;
import com.github.almostreliable.energymeter.util.TypeEnums.MODE;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class EnergyMeterSupport {
    private EnergyMeterSupport() {}

    public static boolean canReceive(MeterEntity meter, @Nullable Direction side) {
        if (side == null || meter.isRemoved() || meter.getLevel() == null || meter.getLevel().isClientSide) {
            return false;
        }
        if (!(meter instanceof EnergyMeterMeasurement measurement) || !measurement.ueb$isMeterReady()) {
            return false;
        }
        if (meter.getSideConfig().get(side) != IO_SETTING.IN) {
            return false;
        }
        return meter.getMode() == MODE.CONSUMER || meter.getSideConfig().hasOutput();
    }

    public static boolean isConsumer(MeterEntity meter) {
        return meter.getMode() == MODE.CONSUMER;
    }

    public static List<Direction> outputDirections(MeterEntity meter) {
        List<Direction> result = new ArrayList<>(4);
        for (Direction direction : Direction.values()) {
            if (meter.getSideConfig().get(direction) == IO_SETTING.OUT) {
                result.add(direction);
            }
        }
        return result;
    }

    public static @Nullable BlockEntity outputTarget(MeterEntity meter, Direction direction) {
        if (meter.getLevel() == null) {
            return null;
        }
        BlockEntity target = meter.getLevel().getBlockEntity(meter.getBlockPos().relative(direction));
        // Preserve Energy Meter's original loop prevention.
        return target instanceof MeterEntity ? null : target;
    }

    public static void recordAcceptedFe(MeterEntity meter, double amount) {
        if (amount > 0 && Double.isFinite(amount) && meter instanceof EnergyMeterMeasurement measurement) {
            measurement.ueb$recordTransferFe(amount);
        }
    }
}
