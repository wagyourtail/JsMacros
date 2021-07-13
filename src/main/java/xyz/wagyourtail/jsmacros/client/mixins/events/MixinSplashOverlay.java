package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.gui.screen.SplashOverlay;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventResourcesReloaded;

@Mixin(SplashOverlay.class)
public class MixinSplashOverlay {
    @Shadow @Final private boolean reloading;

    @Inject(method = "render", at = @At(value = "FIELD", target = "Lnet/minecraft/client/gui/screen/SplashOverlay;reloadCompleteTime:J", opcode = Opcodes.PUTFIELD))
    private void onReloadComplete(CallbackInfo ci) {
        new EventResourcesReloaded(!reloading);
    }
}
