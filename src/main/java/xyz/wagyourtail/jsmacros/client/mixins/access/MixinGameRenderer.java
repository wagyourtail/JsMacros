package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.classes.Draw3D;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FHud;

@Mixin(value = GameRenderer.class)
public class MixinGameRenderer {

    @Inject(at = @At(value = "INVOKE_STRING", target = "Lnet/minecraft/util/profiler/Profiler;swap(Ljava/lang/String;)V", args = {"ldc=hand"}), method = "renderCenter")
    public void render(float tickDelta, long endTime, CallbackInfo info) {
        
        synchronized (FHud.renders) {
            for (Draw3D d : FHud.renders) {
                try {
                    d.render();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
