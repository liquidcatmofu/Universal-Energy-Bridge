package dev.liquidcatmofu.ueb.mixin.draconic;

import com.brandon3055.draconicevolution.blocks.tileentity.TileEnergyCore;
import com.brandon3055.draconicevolution.client.gui.EnergyCoreGui;
import dev.liquidcatmofu.ueb.compat.draconic.exact.BigEnergyFormatter;
import dev.liquidcatmofu.ueb.compat.draconic.exact.ExactEnergyCoreIO;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.math.BigInteger;

/**
 * Replaces only the Energy Core GUI I/O text with UEB's exact BigInteger values.
 */
@Mixin(value = EnergyCoreGui.class, remap = false)
public abstract class EnergyCoreGuiExactIOMixin {

    @Inject(method = "genIOText", at = @At("HEAD"), cancellable = true)
    private void ueb$renderExactIO(TileEnergyCore tile, CallbackInfoReturnable<Component> cir) {
        if (!(tile instanceof ExactEnergyCoreIO exact)) {
            return;
        }

        String inputText = exact.ueb$getExactInputPerTick();
        String outputText = exact.ueb$getExactOutputPerTick();
        if (inputText.isEmpty() || outputText.isEmpty()) {
            return;
        }

        BigInteger input;
        BigInteger output;
        try {
            input = new BigInteger(inputText);
            output = new BigInteger(outputText);
        } catch (NumberFormatException ignored) {
            return;
        }

        String op = " ";
        Component opUnit = Component.translatable("mod_gui.brandonscore.energy_bar.op");

        if (Screen.hasShiftDown()) {
            MutableComponent in = Component.literal("+")
                    .withStyle(ChatFormatting.GREEN)
                    .append(BigEnergyFormatter.format(input))
                    .append(op)
                    .append(opUnit.copy())
                    .append("/t");

            MutableComponent out = Component.literal("-")
                    .withStyle(ChatFormatting.RED)
                    .append(BigEnergyFormatter.format(output))
                    .append(op)
                    .append(opUnit.copy())
                    .append("/t");

            cir.setReturnValue(Component.empty()
                    .append(in)
                    .append(", ")
                    .append(out));
            return;
        }

        BigInteger net = input.subtract(output);
        String prefix = net.signum() > 0 ? "+" : "";
        MutableComponent result = Component.literal(prefix + BigEnergyFormatter.format(net))
                .append(op)
                .append(opUnit.copy())
                .append("/t")
                .withStyle(net.signum() > 0
                        ? ChatFormatting.GREEN
                        : net.signum() < 0
                        ? ChatFormatting.RED
                        : ChatFormatting.GRAY);

        cir.setReturnValue(result);
    }
}
