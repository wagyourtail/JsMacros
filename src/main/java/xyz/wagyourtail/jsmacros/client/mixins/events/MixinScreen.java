package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventSendMessage;

@Mixin(GuiScreen.class)
abstract class MixinScreen {

    @Shadow
    public abstract void sendChatMessage(String message, boolean toHud);

    @Inject(at = @At("HEAD"), method = "sendChatMessage(Ljava/lang/String;)V", cancellable = true)
    private void onSendMessage(String message, final CallbackInfo info) {
        final String result = new EventSendMessage(message).message;
        if (result == null || result.equals("")) {
            info.cancel();
        } else if (!result.equals(message)) {
            info.cancel();
            sendChatMessage(result, true);
        }
    }
}
