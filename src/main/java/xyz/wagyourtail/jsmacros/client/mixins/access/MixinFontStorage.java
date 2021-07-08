package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FontRenderer.class)
public abstract class MixinFontStorage {
    //TODO:
//
//    @Shadow @Final private static Glyph SPACE;
//
//    @Shadow @Final private Int2ObjectMap<CharArrayList> charactersByWidth;
//
//    @Shadow protected abstract RenderableGlyph getRenderableGlyph(char character);
//
//    // compute proper width of ttf space
//    @Inject(method = "setFonts", at = @At("TAIL"))
//    private void fixSpaceCharWidth(List<Font> fonts, CallbackInfo ci) {
//        if (fonts.size() == 0) return;
//        for (Font f : fonts) {
//            if (f instanceof TrueTypeFont) {
//                Glyph space = f.getGlyph(' ');
//                if (space != null) {
//                    charactersByWidth.getOrDefault(MathHelper.ceil(SPACE.getAdvance(false)), new CharArrayList()).rem(' ');
//                    this.charactersByWidth.computeIfAbsent(MathHelper.ceil(space.getAdvance(false)), (ix) -> new CharArrayList()).add(' ');
//                    return;
//                }
//            } else {
//                return;
//            }
//        }
//    }
//
//    // allow ttf space to be loaded
//    @Group(name = "getGlyphOF", min = 1, max = 1)
//    @ModifyArg(method = "getGlyph", at = @At(value = "INVOKE", target = "Lit/unimi/dsi/fastutil/chars/Char2ObjectMap;computeIfAbsent(CLjava/util/function/IntFunction;)Ljava/lang/Object;", remap = false))
//    private IntFunction<Glyph> modifyLambda(IntFunction<Glyph> original) {
//        return (ix) -> {
//            Glyph g = this.getRenderableGlyph((char)ix);
//            // null check is for below inject which makes get return null if it hits non ttf font on space char
//            if (ix == 32 && g == null) return SPACE;
//            return g;
//        };
//    }
//
//    @Group(name = "getGlyphOF", min = 1, max = 1)
//    @ModifyVariable(method = "getGlyph", at = @At(value = "STORE", ordinal = 1), ordinal = 0)
//    private Glyph redirectGlyphOF(Glyph space, int charIn) {
//        Glyph g = this.getRenderableGlyph((char) charIn);
//        // null check is for below inject which makes get return null if it hits non ttf font on space char
//        if (charIn == 32 && g == null) return SPACE;
//        return g;
//    }
//
//    // return null if space and not TTF, see above method
//    @Inject(method = "getRenderableGlyph", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/Font;getGlyph(C)Lnet/minecraft/client/font/RenderableGlyph;", ordinal = 0), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
//    private void nonTTFSpaceSizeFix(char c, CallbackInfoReturnable<RenderableGlyph> cir, Iterator<Font> var2, Font font) {
//        if (c == ' ' && !(font instanceof TrueTypeFont)) {
//            cir.setReturnValue(null);
//            cir.cancel();
//        }
//    }
}
