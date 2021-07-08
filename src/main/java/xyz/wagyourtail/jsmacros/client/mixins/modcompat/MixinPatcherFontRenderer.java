package xyz.wagyourtail.jsmacros.client.mixins.modcompat;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "club.sk1er.patcher.hooks.FontRendererHook", remap = false)
public class MixinPatcherFontRenderer {
    
    @Unique float j;
    @Unique float k;
    @Unique float l;
    @Unique boolean wasCustomColor = false;
    
    @Redirect(method = "renderStringAtPos", at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C", remap = false, ordinal = 1))
    public char addCustomColorsPatcher(String text, int i) {
        char c = text.charAt(i);
        if (c == '#') {
            try {
                int col = Integer.parseInt(text.substring(i + 1, i + 7), 16);
                this.j = (col >> 16 & 255) / 255F;
                this.k = (col >> 8 & 255) / 255F;
                this.l = (col & 255) / 255F;
                this.wasCustomColor = true;
            } catch (NumberFormatException ignored) {}
        }
        return c;
    }
    
    @Redirect(method = "renderStringAtPos", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;func_179131_c(FFFF)V"))
    public void setColorModifyPatcher(float r, float g, float b, float a) {
        if (wasCustomColor) {
            GlStateManager.color(j,k,l,a);
        } else {
            GlStateManager.color(r,g,b,a);
        }
    }
    
    @ModifyArg(method = "renderStringAtPos", at = @At(value = "INVOKE", target = "Lclub/sk1er/patcher/util/enhancement/text/CachedString;setLastGreen(F)V", remap = false))
    public float setLastCustomGreen(float green) {
        if (wasCustomColor) {
            return k;
        } else {
            return green;
        }
    }
    
    @ModifyArg(method = "renderStringAtPos", at = @At(value = "INVOKE", target = "Lclub/sk1er/patcher/util/enhancement/text/CachedString;setLastBlue(F)V", remap = false))
    public float setLastCustomBlue(float blue) {
        if (wasCustomColor) {
            return l;
        } else {
            return blue;
        }
    }
    
    @ModifyArg(method = "renderStringAtPos", at = @At(value = "INVOKE", target = "Lclub/sk1er/patcher/util/enhancement/text/CachedString;setLastRed(F)V", remap = false))
    public float setLastCustomRed(float red) {
        if (wasCustomColor) {
            return j;
        } else {
            return red;
        }
    }
    @ModifyVariable(method = "renderStringAtPos", at = @At(value = "INVOKE", target = "Lclub/sk1er/patcher/util/enhancement/text/CachedString;setLastRed(F)V", remap = false, shift = At.Shift.AFTER), ordinal = 2)
    public int modifyIndexPatcher(int index) {
        if (wasCustomColor) {
            wasCustomColor = false;
            return index + 6;
        } else {
            return index;
        }
    }
    
    @Unique String shiftString = "";
    
    @Inject(method = "getUncachedWidth", at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C", ordinal = 1))
    public void fixStringWidthCustomColorPatcher(FontRenderer fontRenderer, String text, CallbackInfoReturnable<Float> cir) {
        shiftString = text;
    }
    
    @ModifyVariable(method = "getUncachedWidth", at = @At(value = "INVOKE", target = "Ljava/lang/String;charAt(I)C", ordinal = 1, shift = At.Shift.AFTER), ordinal = 0)
    public int shiftIndexCCPatcher(int j) {
        if (j < shiftString.length() && shiftString.charAt(j) == '#') {
            return j + 6;
        }
        return j;
    }
    
}
