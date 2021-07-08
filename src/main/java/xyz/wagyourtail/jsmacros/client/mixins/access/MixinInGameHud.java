package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.classes.Draw2D;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IDraw2D;

@Mixin(GuiIngame.class)
class MixinInGameHud {
    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/settings/GameSettings;showDebugInfo:Z"), method = "renderGameOverlay")
    public void renderHud(float f, final CallbackInfo info) {
        
        synchronized (FHud.overlays) {
            for (IDraw2D<Draw2D> h : FHud.overlays) {
                try {
                    ((Draw2D)h).render(0, 0, 0);
                } catch (Exception ignored) {}
            }
        }
    
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableAlpha();
    }
}
