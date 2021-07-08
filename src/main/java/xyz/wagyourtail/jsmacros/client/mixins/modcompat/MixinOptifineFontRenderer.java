package xyz.wagyourtail.jsmacros.client.mixins.modcompat;

import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = FontRenderer.class)
public class MixinOptifineFontRenderer {
    
    @Unique
    boolean extraShiftInTrimString = false;
    
    @Inject(method = "trimStringToWidth(Ljava/lang/String;IZ)Ljava/lang/String;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;getCharWidthFloat(C)F", remap = false), locals = LocalCapture.CAPTURE_FAILHARD)
    public void fixTrimStringOF(String text, int width, boolean rightToLeft, CallbackInfoReturnable<String> cir, StringBuilder stringBuilder, float i, int j, int k, boolean flag, boolean flag1, int l, char c0) {
        if (flag && c0 == '#') {
            extraShiftInTrimString = true;
        }
    }
    
    @ModifyVariable(method = "trimStringToWidth(Ljava/lang/String;IZ)Ljava/lang/String;", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;getCharWidthFloat(C)F", shift = At.Shift.AFTER, remap = false), index = 10)
    public int shiftIndexTrimStringOF(int index) {
        if (extraShiftInTrimString) return index + 6;
        return index;
    }
    
    @Inject(method = "trimStringToWidth(Ljava/lang/String;IZ)Ljava/lang/String;", at = @At(value = "INVOKE", target = "Ljava/lang/StringBuilder;append(C)Ljava/lang/StringBuilder;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    public void addOtherCharsOF(String text, int width, boolean rightToLeft, CallbackInfoReturnable<String> cir, StringBuilder stringBuilder, float i, int j, int k, boolean flag, boolean flag1, int l, char c0, float i1) {
        if (extraShiftInTrimString) {
            extraShiftInTrimString = false;
            stringBuilder.append(text, l - 5, l + 1);
        }
    }
}
