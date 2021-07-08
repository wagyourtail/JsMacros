package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.util.ChatStyle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import xyz.wagyourtail.jsmacros.client.access.IStyle;

@Mixin(ChatStyle.class)
public class MixinStyle implements IStyle {

    @Unique
    boolean hasCustomColor = false;
    @Unique int customColor;

    @Override
    public ChatStyle setCustomColor(int color) {
        hasCustomColor = true;
        this.customColor = color;
        return (ChatStyle)(Object)this;
    }

    @Inject(method = "createShallowCopy", at = @At("RETURN"), cancellable = true)
    public void copyCustomColor(CallbackInfoReturnable<ChatStyle> cir) {
        if (this.hasCustomColor) {
            ((IStyle) cir.getReturnValue()).setCustomColor(this.customColor);
        }
    }
    
    @Inject(method = "createDeepCopy", at = @At("RETURN"), cancellable = true)
    public void copyCustomColorDeep(CallbackInfoReturnable<ChatStyle> cir) {
        if (this.hasCustomColor) {
            ((IStyle) cir.getReturnValue()).setCustomColor(this.customColor);
        }
    }

    @Inject(method = "getFormattingCode", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ChatStyle;getColor()Lnet/minecraft/util/EnumChatFormatting;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD)
    public void asStringCustomColorInject(CallbackInfoReturnable<String> cir, StringBuilder stringBuilder) {
        if (hasCustomColor) {
            String hex = Integer.toHexString(customColor);

            stringBuilder.append("\u00A7#");

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
