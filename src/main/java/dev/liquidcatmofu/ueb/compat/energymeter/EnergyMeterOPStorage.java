package dev.liquidcatmofu.ueb.compat.energymeter;

import com.brandon3055.brandonscore.api.power.IOPStorage;
import com.brandon3055.brandonscore.capability.CapabilityOP;
import com.github.almostreliable.energymeter.meter.MeterEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.ArrayList;
import java.util.List;

/** Native OP passthrough for an Energy Meter input side. */
public final class EnergyMeterOPStorage implements IOPStorage {
    private final MeterEntity meter;
    private final Direction side;

    public EnergyMeterOPStorage(MeterEntity meter, Direction side) {
        this.meter = meter;
        this.side = side;
    }

    @Override
    public long receiveOP(long maxReceive, boolean simulate) {
        if (maxReceive <= 0 || !canReceive()) {
            return 0;
        }
        if (EnergyMeterSupport.isConsumer(meter)) {
            if (!simulate) {
                EnergyMeterSupport.recordAcceptedFe(meter, maxReceive);
            }
            return maxReceive;
        }

        long accepted = EnergyMeterLongTransfer.transfer(maxReceive, collectSinks(), simulate);
        if (!simulate && accepted > 0) {
            EnergyMeterSupport.recordAcceptedFe(meter, accepted);
        }
        return accepted;
    }

    private List<EnergyMeterLongTransfer.Sink> collectSinks() {
        List<EnergyMeterLongTransfer.Sink> sinks = new ArrayList<>();
        for (Direction output : EnergyMeterSupport.outputDirections(meter)) {
            BlockEntity target = EnergyMeterSupport.outputTarget(meter, output);
            if (target == null) {
                continue;
            }

            IOPStorage op = target.getCapability(CapabilityOP.OP, output.getOpposite()).resolve().orElse(null);
            if (op != null) {
                // Native capability wins even when it currently refuses input. Falling
                // through to FE could bypass native sided/rate semantics.
                if (op.canReceive()) {
                    sinks.add(new EnergyMeterLongTransfer.Sink() {
                        @Override
                        public long simulate(long amount) {
                            return op.receiveOP(amount, true);
                        }

                        @Override
                        public long execute(long amount) {
                            return op.receiveOP(amount, false);
                        }
                    });
                }
                continue;
            }

            IEnergyStorage fe = target.getCapability(ForgeCapabilities.ENERGY, output.getOpposite()).resolve().orElse(null);
            if (fe != null && fe.canReceive()) {
                sinks.add(new EnergyMeterLongTransfer.Sink() {
                    @Override
                    public long simulate(long amount) {
                        int request = (int) Math.min(amount, Integer.MAX_VALUE);
                        return fe.receiveEnergy(request, true);
                    }

                    @Override
                    public long execute(long amount) {
                        int request = (int) Math.min(amount, Integer.MAX_VALUE);
                        return fe.receiveEnergy(request, false);
                    }
                });
            }
        }
        return sinks;
    }

    @Override
    public long extractOP(long maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public long getOPStored() {
        return 0;
    }

    @Override
    public long getMaxOPStored() {
        return canReceive() ? Long.MAX_VALUE : 0;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return EnergyMeterSupport.canReceive(meter, side);
    }

    @Override
    public long modifyEnergyStored(long amount) {
        return amount > 0 ? receiveOP(amount, false) : 0;
    }
}
