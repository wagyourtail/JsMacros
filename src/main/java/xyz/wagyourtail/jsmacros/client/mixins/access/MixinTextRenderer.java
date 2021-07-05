package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.Tessellator;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(TextRenderer.class)
public class MixinTextRenderer {
    
    @Unique float j;
    @Unique float k;
    @Unique float l;
    @Unique boolean wasCustomColor = false;
    
    @Inject(method = "drawLayer", at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C", remap = false, ordinal = 1), locals = LocalCapture.CAPTURE_FAILHARD)
    public void addCustomColors(String text, float x, float y, int color, boolean shadow, CallbackInfoReturnable<Float> cir, float f, float g, float h, float i, float j, float k, float l, float m, Tessellator tessellator, BufferBuilder bufferBuilder, Identifier identifier, boolean bl, boolean bl2, boolean bl3, boolean bl4, boolean bl5, List list, int n, char c) {
        if (text.charAt(n+1) == '#') {
            try {
                int col = Integer.parseInt(text.substring(n + 2, n + 8), 16);
                this.j = (col >> 16 & 255) / 255F * f;
                this.k = (col >> 8 & 255) / 255F * f;
                this.l = (col & 255) / 255F * f;
                this.wasCustomColor = true;
            } catch (NumberFormatException ignored) {
                this.j = j;
                this.k = k;
                this.l = l;
            }
        } else {
            this.j = j;
            this.k = k;
            this.l = l;
        }
    }
    
    @ModifyVariable(method = "drawLayer", at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C", remap = false, ordinal = 1, shift = At.Shift.AFTER), ordinal = 6)
    public float modifyR(float red) {
        return j;
    }
    
    @ModifyVariable(method = "drawLayer", at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C", remap = false, ordinal = 1, shift = At.Shift.AFTER), ordinal = 7)
    public float modifyG(float green) {
        return k;
    }
    
    @ModifyVariable(method = "drawLayer", at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C", remap = false, ordinal = 1, shift = At.Shift.AFTER), ordinal = 8)
    public float modifyB(float blue) {
        return l;
    }
    
    @ModifyVariable(method = "drawLayer", at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C", remap = false, ordinal = 1, shift = At.Shift.AFTER), ordinal = 1)
    public int modifyIndex(int n) {
        if (wasCustomColor) {
            wasCustomColor = false;
            return n + 6;
        } else {
            return n;
        }
    }
    
    @Unique boolean shiftStringWidth = false;
    
    @Inject(method = "getStringWidth", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Formatting;byCode(C)Lnet/minecraft/util/Formatting;"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void fixStringWidthCustomColor(String text, CallbackInfoReturnable<Integer> cir, float f, boolean bl, int i, char c) {
        if (text.charAt(i) == '#') {
            shiftStringWidth = true;
        }
    }
    
    @ModifyVariable(method = "getStringWidth", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Formatting;byCode(C)Lnet/minecraft/util/Formatting;", shift = At.Shift.AFTER))
    public int shiftIndexCC(int i) {
        if (shiftStringWidth) return i + 6;
        return i;
    }
    
    @ModifyVariable(method = "getStringWidth", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Formatting;byCode(C)Lnet/minecraft/util/Formatting;", shift = At.Shift.AFTER))
    public boolean updateIsModifierCC(boolean bl) {
        if (shiftStringWidth) {
            shiftStringWidth = false;
            return false;
        }
        return bl;
    }
    
    @Unique boolean extraShiftInTrimString = false;
    
    @Inject(method = "trimToWidth(Ljava/lang/String;IZ)Ljava/lang/String;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Formatting;byCode(C)Lnet/minecraft/util/Formatting;"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void fixTrimString(String text, int width, boolean rightToLeft, CallbackInfoReturnable<String> cir, StringBuilder stringBuilder, float f, int i, int j, boolean bl, boolean bl2, int k, char c) {
        if (c == '#') {
            extraShiftInTrimString = true;
        }
    }
    
    @ModifyVariable(method = "trimToWidth(Ljava/lang/String;IZ)Ljava/lang/String;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Formatting;byCode(C)Lnet/minecraft/util/Formatting;", shift = At.Shift.AFTER), ordinal = 3)
    public int shiftIndexTrimString(int index) {
        if (extraShiftInTrimString) return index + 6;
        return index;
    }
    
    @ModifyVariable(method = "trimToWidth(Ljava/lang/String;IZ)Ljava/lang/String;", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Formatting;byCode(C)Lnet/minecraft/util/Formatting;", shift = At.Shift.AFTER), ordinal = 1)
    public boolean isModifierTrimString(boolean bl2) {
        if (extraShiftInTrimString) {
            return false;
        }
        return bl2;
    }
    
    @Inject(method = "trimToWidth(Ljava/lang/String;IZ)Ljava/lang/String;", at = @At(value = "INVOKE", target = "Ljava/lang/StringBuilder;append(C)Ljava/lang/StringBuilder;", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    public void addOtherChars(String text, int width, boolean rightToLeft, CallbackInfoReturnable<String> cir, StringBuilder stringBuilder, float f, int i, int j, boolean bl, boolean bl2, int k, char c) {
        if (extraShiftInTrimString) {
            extraShiftInTrimString = false;
            stringBuilder.append(text, k - 5, k + 1);
        }
    }
    
    @Unique boolean internalTrimExtraShift = false;
    
    @Inject(method = "getCharacterCountForWidth", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Formatting;byCode(C)Lnet/minecraft/util/Formatting;"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void internalTrimCustomColor(String text, int offset, CallbackInfoReturnable<Integer> cir, int i, int j, float f, int k, int l, boolean bl, boolean bl2, char c) {
        if (text.charAt(k) == '#') {
            internalTrimExtraShift = true;
        }
    }
    
    @ModifyVariable(method = "getCharacterCountForWidth", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Formatting;byCode(C)Lnet/minecraft/util/Formatting;", shift = At.Shift.AFTER), ordinal = 0)
    public boolean isModifierInternalTrim(boolean bl) {
        if (internalTrimExtraShift) return false;
        return bl;
    }
    
    @ModifyVariable(method = "getCharacterCountForWidth", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Formatting;byCode(C)Lnet/minecraft/util/Formatting;", shift = At.Shift.AFTER), ordinal = 2)
    public int shiftInternalTrimIndex(int index) {
        if (internalTrimExtraShift) {
            internalTrimExtraShift = false;
            return index + 6;
        }
        return index;
    }
}
