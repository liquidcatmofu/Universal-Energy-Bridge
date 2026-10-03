package dev.liquidcatmofu.ueb.compat.energymeter;

import com.github.almostreliable.energymeter.meter.MeterEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import sonar.fluxnetworks.api.FluxCapabilities;
import sonar.fluxnetworks.api.energy.IFNEnergyStorage;

import java.util.ArrayList;
import java.util.List;

/** Native Flux Networks long-energy passthrough for an Energy Meter input side. */
public final class EnergyMeterFluxStorage implements IFNEnergyStorage {
    private final MeterEntity meter;
    private final Direction side;

    public EnergyMeterFluxStorage(MeterEntity meter, Direction side) {
        this.meter = meter;
        this.side = side;
    }

    @Override
    public long receiveEnergyL(long maxReceive, boolean simulate) {
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

            IFNEnergyStorage fn = target.getCapability(FluxCapabilities.FN_ENERGY_STORAGE, output.getOpposite()).resolve().orElse(null);
            if (fn != null) {
                if (fn.canReceive()) {
                    sinks.add(new EnergyMeterLongTransfer.Sink() {
                        @Override
                        public long simulate(long amount) {
                            return fn.receiveEnergyL(amount, true);
                        }

                        @Override
                        public long execute(long amount) {
                            return fn.receiveEnergyL(amount, false);
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
    public long extractEnergyL(long maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public long getEnergyStoredL() {
        return 0;
    }

    @Override
    public long getMaxEnergyStoredL() {
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
}
