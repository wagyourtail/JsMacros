package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.font.TrueTypeFont;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TrueTypeFont.class)
public class MixinTrueTypeFont {
    
    // cancel canceling spaces by making their calculated width/height not equal to 0 which cancels their existence in the font entirely
    // also I dont actually know which one is width and height, it doesn't matter
    
    
    @ModifyVariable(method = "getGlyph", at = @At("STORE"), ordinal = 1)
    public int modifyWidth(int w, char i) {
        if (i == 32) return 1;
        return w;
    }
    
    @ModifyVariable(method = "getGlyph", at = @At("STORE"), ordinal = 2)
    public int modifyHeight(int h, char i) {
        if (i == 32) return 1;
        return h;
    }
    
}
