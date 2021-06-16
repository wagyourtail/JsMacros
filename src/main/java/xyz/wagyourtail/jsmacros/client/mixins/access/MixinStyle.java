package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.text.Style;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.wagyourtail.jsmacros.client.access.IStyle;

@Mixin(Style.class)
public class MixinStyle implements IStyle {
    @Unique boolean hasCustomColor = false;
    @Unique int customColor;
    
    @Override
    public Style setCustomColor(int color) {
        hasCustomColor = true;
        this.customColor = color;
        return (Style)(Object)this;
    }
    
    @Inject(method = "copy", at = @At("RETURN"), cancellable = true)
    public void copyCustomColor(CallbackInfoReturnable<Style> cir) {
        if (this.hasCustomColor) {
            ((IStyle) cir.getReturnValue()).setCustomColor(this.customColor);
        }
    }
    
    @Inject(method = "asString", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Style;getColor()Lnet/minecraft/util/Formatting;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    public void asStringCustomColorInject(CallbackInfoReturnable<String> cir, StringBuilder stringBuilder) {
        if (hasCustomColor) {
            String hex = Integer.toHexString(customColor);
            
            stringBuilder.append("§#");
            
            for (int i = hex.length(); i < 6; ++i) {
                stringBuilder.append("0");
            }
            
            stringBuilder.append(hex);
        }
    }
    
    @Inject(method = "isEmpty", at = @At("RETURN"), cancellable = true)
    public void isEmptyCustomColor(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() || hasCustomColor) cir.setReturnValue(false);
        else cir.setReturnValue(true);
    }
    
}
