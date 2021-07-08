package xyz.wagyourtail.jsmacros.client.mixins.modcompat;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Pseudo
@Mixin(targets = "bre.smoothfont.FontRendererHook", remap = false)
public class MixinSmoothFontRenderer {
    @Unique
    String shiftString = "";
    
    @Inject(method = "getStringWidthFloatHook", at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C", ordinal = 1))
    public void fixStringWidthCustomColorSmoothFont(String text, CallbackInfoReturnable<Integer> cir) {
        shiftString = text;
    }
    
    @ModifyVariable(method = "getStringWidthFloatHook", at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C", ordinal = 1, shift = At.Shift.AFTER), index = 4)
    public int shiftIndexCCSmoothFont(int j) {
        if (j < shiftString.length() && shiftString.charAt(j) == '#') {
            return j + 6;
        }
        return j;
    }
    
    @Unique boolean deco;
    
    @Inject(method = "trimStringToWidthFloatHook", at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void trimStringIsDeco(String text, int width, boolean reverse, CallbackInfoReturnable<String> cir, StringBuilder builder, float wid, int len, int start, int step, boolean deco) {
        this.deco = deco;
    }
    
    @Unique boolean doExtraTrim = false;
    
    @Redirect(method = "trimStringToWidthFloatHook", at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C"))
    public char trimCharAtExtraTrim(String s, int i) {
        char c = s.charAt(i);
        if (deco && c == '#') {
            doExtraTrim = true;
        }
        return c;
    }
    
    @ModifyVariable(method = "trimStringToWidthFloatHook", at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C", shift = At.Shift.AFTER), ordinal = 4)
    public int trimStringExtraTrim(int i) {
        if (doExtraTrim) {
            doExtraTrim = false;
            return i + 6;
        } else {
            return i;
        }
    }
    
    @Unique
    String internalTrimExtra = "";
    
    @Inject(method = "sizeStringToWidthFloatHook", at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C", ordinal = 1))
    public void internalTrimCustomColor(String text, int offset, CallbackInfoReturnable<Integer> cir) {
        internalTrimExtra = text;
    }
    
    @ModifyVariable(method = "sizeStringToWidthFloatHook", at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C", shift = At.Shift.AFTER), ordinal = 2, require = 0)
    public int shiftInternalTrimIndex(int k) {
        if (k < internalTrimExtra.length() && internalTrimExtra.charAt(k) == '#') {
            return k + 6;
        }
        return k;
    }
    
}
