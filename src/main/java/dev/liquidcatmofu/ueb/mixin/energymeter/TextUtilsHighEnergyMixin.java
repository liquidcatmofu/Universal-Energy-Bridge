package dev.liquidcatmofu.ueb.mixin.energymeter;

import net.minecraft.util.Tuple;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

@Pseudo
@Mixin(targets = "com.github.almostreliable.energymeter.util.TextUtils", remap = false)
public abstract class TextUtilsHighEnergyMixin {
    private static final String[] UEB_UNITS = {"", "k", "M", "G", "T", "P", "E", "Z", "Y"};

    @Inject(method = "formatEnergy", at = @At("HEAD"), cancellable = true, remap = false)
    private static void ueb$formatHighEnergy(Number number, boolean extended,
                                              CallbackInfoReturnable<Tuple<String, String>> cir) {
        if (extended) {
            return;
        }

        double value = number.doubleValue();
        double absolute = Math.abs(value);
        if (!Double.isFinite(absolute) || absolute < 1.0E18) {
            return;
        }

        int exponent = (int) Math.floor(Math.log10(absolute));
        int unitExponent = 3 * (exponent / 3);
        if (unitExponent <= 24) {
            double normalized = value / Math.pow(10, unitExponent);
            NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
            format.setRoundingMode(RoundingMode.DOWN);
            format.setMinimumFractionDigits(1);
            format.setMaximumFractionDigits(2);
            cir.setReturnValue(new Tuple<>(format.format(normalized), UEB_UNITS[unitExponent / 3] + "FE"));
            return;
        }

        cir.setReturnValue(new Tuple<>(String.format(Locale.ROOT, "%.2e", value), "FE"));
    }
}
