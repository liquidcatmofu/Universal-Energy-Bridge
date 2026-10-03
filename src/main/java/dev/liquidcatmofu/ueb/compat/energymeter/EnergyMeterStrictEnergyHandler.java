package dev.liquidcatmofu.ueb.compat.energymeter;

import com.github.almostreliable.energymeter.meter.MeterEntity;
import mekanism.api.Action;
import mekanism.api.energy.IStrictEnergyHandler;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.Capabilities;
import mekanism.common.util.UnitDisplayUtils.EnergyUnit;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Native Mekanism FloatingLong passthrough for an Energy Meter input side.
 *
 * <p>Strict-to-Strict transfer never converts through signed long. Conversion to FE
 * happens only for the meter's display accumulator or for an actual FE fallback output.</p>
 */
public final class EnergyMeterStrictEnergyHandler implements IStrictEnergyHandler {
    private final MeterEntity meter;
    private final Direction side;

    public EnergyMeterStrictEnergyHandler(MeterEntity meter, Direction side) {
        this.meter = meter;
        this.side = side;
    }

    @Override
    public int getEnergyContainerCount() {
        return 1;
    }

    @Override
    public FloatingLong getEnergy(int container) {
        return FloatingLong.ZERO;
    }

    @Override
    public void setEnergy(int container, FloatingLong energy) {
        // Energy Meter is a stateless passthrough/consumer.
    }

    @Override
    public FloatingLong getMaxEnergy(int container) {
        return container == 0 && canReceive() ? FloatingLong.MAX_VALUE : FloatingLong.ZERO;
    }

    @Override
    public FloatingLong getNeededEnergy(int container) {
        return getMaxEnergy(container);
    }

    @Override
    public FloatingLong insertEnergy(int container, FloatingLong amount, @NotNull Action action) {
        if (container != 0 || amount.isZero() || !canReceive()) {
            return amount;
        }

        FloatingLong accepted;
        if (EnergyMeterSupport.isConsumer(meter)) {
            accepted = amount;
        } else {
            accepted = transfer(amount, collectSinks(), action.simulate());
        }

        if (action.execute() && !accepted.isZero()) {
            double acceptedFe = EnergyUnit.FORGE_ENERGY.convertTo(accepted).doubleValue();
            EnergyMeterSupport.recordAcceptedFe(meter, acceptedFe);
        }
        return amount.subtract(accepted);
    }

    @Override
    public FloatingLong extractEnergy(int container, FloatingLong amount, @NotNull Action action) {
        return FloatingLong.ZERO;
    }

    private boolean canReceive() {
        return EnergyMeterSupport.canReceive(meter, side);
    }

    private List<Sink> collectSinks() {
        List<Sink> sinks = new ArrayList<>();
        for (Direction output : EnergyMeterSupport.outputDirections(meter)) {
            BlockEntity target = EnergyMeterSupport.outputTarget(meter, output);
            if (target == null) {
                continue;
            }

            IStrictEnergyHandler strict = target.getCapability(Capabilities.STRICT_ENERGY, output.getOpposite()).resolve().orElse(null);
            if (strict != null) {
                sinks.add(new Sink() {
                    @Override
                    public FloatingLong simulate(FloatingLong amount) {
                        FloatingLong remainder = strict.insertEnergy(amount, Action.SIMULATE);
                        return amount.subtract(remainder);
                    }

                    @Override
                    public FloatingLong execute(FloatingLong amount) {
                        FloatingLong remainder = strict.insertEnergy(amount, Action.EXECUTE);
                        return amount.subtract(remainder);
                    }
                });
                continue;
            }

            IEnergyStorage fe = target.getCapability(ForgeCapabilities.ENERGY, output.getOpposite()).resolve().orElse(null);
            if (fe != null && fe.canReceive()) {
                sinks.add(new Sink() {
                    @Override
                    public FloatingLong simulate(FloatingLong amount) {
                        return receiveForge(fe, amount, true);
                    }

                    @Override
                    public FloatingLong execute(FloatingLong amount) {
                        return receiveForge(fe, amount, false);
                    }
                });
            }
        }
        return sinks;
    }

    private static FloatingLong receiveForge(IEnergyStorage fe, FloatingLong joules, boolean simulate) {
        long feRequested = EnergyUnit.FORGE_ENERGY.convertToAsLong(joules);
        if (feRequested <= 0) {
            return FloatingLong.ZERO;
        }
        int request = (int) Math.min(feRequested, Integer.MAX_VALUE);
        int accepted = fe.receiveEnergy(request, simulate);
        return accepted <= 0 ? FloatingLong.ZERO : EnergyUnit.FORGE_ENERGY.convertFrom(accepted);
    }

    private static FloatingLong transfer(FloatingLong amount, List<Sink> sinks, boolean simulate) {
        if (amount.isZero() || sinks.isEmpty()) {
            return FloatingLong.ZERO;
        }

        List<State> active = new ArrayList<>(sinks.size());
        FloatingLong totalCapacity = FloatingLong.ZERO;
        for (Sink sink : sinks) {
            FloatingLong capacity = clamp(sink.simulate(amount), amount);
            if (capacity.isZero()) {
                continue;
            }
            active.add(new State(sink, capacity.copy()));
            totalCapacity = totalCapacity.add(capacity);
            if (!totalCapacity.smallerThan(amount)) {
                totalCapacity = amount;
                break;
            }
        }

        if (simulate || active.isEmpty()) {
            return totalCapacity.smallerThan(amount) ? totalCapacity : amount;
        }

        FloatingLong remaining = amount.copy();
        while (!remaining.isZero() && !active.isEmpty()) {
            FloatingLong share = remaining.divide(active.size());
            if (share.isZero()) {
                // FloatingLong has four decimal places. If a split rounds to zero,
                // let one sink take the tiny remainder instead of stalling.
                share = remaining;
            }

            boolean progressed = false;
            Iterator<State> iterator = active.iterator();
            while (iterator.hasNext() && !remaining.isZero()) {
                State state = iterator.next();
                FloatingLong requested = min(min(share, state.capacity), remaining);
                if (requested.isZero()) {
                    iterator.remove();
                    continue;
                }

                FloatingLong accepted = clamp(state.sink.execute(requested), requested);
                if (!accepted.isZero()) {
                    remaining = remaining.subtract(accepted);
                    state.capacity = state.capacity.subtract(accepted);
                    progressed = true;
                }

                if (accepted.smallerThan(requested) || state.capacity.isZero()) {
                    iterator.remove();
                }
            }

            if (!progressed) {
                break;
            }
        }
        return amount.subtract(remaining);
    }

    private static FloatingLong clamp(FloatingLong value, FloatingLong max) {
        return value.greaterThan(max) ? max : value;
    }

    private static FloatingLong min(FloatingLong left, FloatingLong right) {
        return left.smallerThan(right) ? left : right;
    }

    private interface Sink {
        FloatingLong simulate(FloatingLong amount);
        FloatingLong execute(FloatingLong amount);
    }

    private static final class State {
        private final Sink sink;
        private FloatingLong capacity;

        private State(Sink sink, FloatingLong capacity) {
            this.sink = sink;
            this.capacity = capacity;
        }
    }
}
