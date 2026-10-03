package dev.liquidcatmofu.ueb.mixin.mekanism;

import dev.liquidcatmofu.ueb.config.BridgeServerConfig;
import mekanism.api.math.FloatingLong;
import mekanism.common.content.entangloporter.InventoryFrequency;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * Optional QE energy-buffer override.
 *
 * <p>Mekanism creates the shared Quantum Entangloporter frequency energy container
 * in InventoryFrequency#presetVariables using the configured energyBuffer value.
 * Replacing only that constructor argument leaves every other Mekanism energy
 * container and the original Mekanism config untouched.</p>
 */
@Mixin(value = InventoryFrequency.class, remap = false)
public abstract class InventoryFrequencyMixin {

    @ModifyArg(
            method = "presetVariables",
            at = @At(
                    value = "INVOKE",
                    target = "Lmekanism/common/capabilities/energy/BasicEnergyContainer;create(Lmekanism/api/math/FloatingLong;Lmekanism/api/IContentsListener;)Lmekanism/common/capabilities/energy/BasicEnergyContainer;",
                    remap = false
            ),
            index = 0
    )
    private FloatingLong ueb$overrideQuantumEntangloporterEnergyBuffer(FloatingLong configuredMaximum) {
        return BridgeServerConfig.MEKANISM_QE_UNLIMITED_ENERGY_BUFFER.get()
                ? FloatingLong.MAX_VALUE
                : configuredMaximum;
    }
}
