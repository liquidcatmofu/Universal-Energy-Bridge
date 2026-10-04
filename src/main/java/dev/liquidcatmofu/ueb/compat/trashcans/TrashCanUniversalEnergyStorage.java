package dev.liquidcatmofu.ueb.compat.trashcans;

import dev.liquidcatmofu.ueb.UniversalEnergyBridge;
import dev.liquidcatmofu.ueb.api.IUniversalEnergyStorage;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Signed-long sink view for Trash Cans' energy-capable trash cans.
 *
 * <p>Trash Cans is a stateless energy sink. UEB therefore only needs to preserve the optional
 * transfer limit. Newer Trash Cans releases expose that setting through public accessors, while
 * older 1.20 releases exposed the fields directly. Reflection keeps the compat binary-tolerant
 * across both layouts without linking UEB to Trash Cans implementation classes.</p>
 */
public final class TrashCanUniversalEnergyStorage implements IUniversalEnergyStorage {
    private static final Access ACCESS = new Access();

    private final BlockEntity trashCan;
    @Nullable
    private final Direction side;

    public TrashCanUniversalEnergyStorage(BlockEntity trashCan, @Nullable Direction side) {
        this.trashCan = trashCan;
        this.side = side;
    }

    @Override
    public long insert(long amount, boolean simulate) {
        if (amount <= 0) {
            return 0;
        }
        long limit = ACCESS.maxInsertion(trashCan, side);
        return limit <= 0 ? 0 : Math.min(amount, limit);
    }

    @Override
    public long extract(long amount, boolean simulate) {
        return 0;
    }

    @Override
    public long getStored() {
        return 0;
    }

    @Override
    public long getCapacity() {
        return Long.MAX_VALUE;
    }

    @Override
    public boolean canInsert() {
        return ACCESS.maxInsertion(trashCan, side) > 0;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    private static final class Access {
        private volatile Class<?> resolvedClass;
        private volatile Method isEnergyLimited;
        private volatile Method getEnergyLimit;
        private volatile Method getMaxEnergyInsertion;
        private volatile Field useEnergyLimit;
        private volatile Field energyLimit;

        long maxInsertion(BlockEntity blockEntity, @Nullable Direction side) {
            Class<?> type = blockEntity.getClass();
            ensureResolved(type);

            try {
                if (isEnergyLimited != null && getEnergyLimit != null) {
                    boolean limited = (boolean) isEnergyLimited.invoke(blockEntity);
                    if (!limited) {
                        return Long.MAX_VALUE;
                    }
                    return Math.max(0L, ((Number) getEnergyLimit.invoke(blockEntity)).longValue());
                }

                if (useEnergyLimit != null && energyLimit != null) {
                    boolean limited = useEnergyLimit.getBoolean(blockEntity);
                    if (!limited) {
                        return Long.MAX_VALUE;
                    }
                    return Math.max(0L, energyLimit.getLong(blockEntity));
                }

                if (getMaxEnergyInsertion != null) {
                    long max = Math.max(0L, ((Number) getMaxEnergyInsertion.invoke(blockEntity)).longValue());
                    return max == Integer.MAX_VALUE ? Long.MAX_VALUE : max;
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                UniversalEnergyBridge.LOGGER.debug("Failed to read Trash Cans energy limit through public API", e);
            }

            // Last-resort compatibility path for an unknown Trash Cans layout.
            IEnergyStorage energy = blockEntity.getCapability(ForgeCapabilities.ENERGY, side).orElse(null);
            if (energy == null || !energy.canReceive()) {
                return 0;
            }
            int accepted = energy.receiveEnergy(Integer.MAX_VALUE, true);
            return accepted == Integer.MAX_VALUE ? Long.MAX_VALUE : Math.max(0L, accepted);
        }

        private synchronized void ensureResolved(Class<?> type) {
            if (resolvedClass == type) {
                return;
            }

            resolvedClass = type;
            isEnergyLimited = publicMethod(type, "isEnergyLimited");
            getEnergyLimit = publicMethod(type, "getEnergyLimit");
            getMaxEnergyInsertion = publicMethod(type, "getMaxEnergyInsertion");
            useEnergyLimit = publicField(type, "useEnergyLimit");
            energyLimit = publicField(type, "energyLimit");
        }

        @Nullable
        private static Method publicMethod(Class<?> type, String name) {
            try {
                return type.getMethod(name);
            } catch (NoSuchMethodException ignored) {
                return null;
            }
        }

        @Nullable
        private static Field publicField(Class<?> type, String name) {
            try {
                return type.getField(name);
            } catch (NoSuchFieldException ignored) {
                return null;
            }
        }
    }
}
