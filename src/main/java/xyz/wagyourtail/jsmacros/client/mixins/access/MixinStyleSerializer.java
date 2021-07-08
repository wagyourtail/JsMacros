package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatStyle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ChatStyle.Serializer.class, priority = 1001)
public class MixinStyleSerializer {

    @Redirect(method = "serialize", at = @At(value = "INVOKE", target = "Lnet/minecraft/event/ClickEvent$Action;getCanonicalName()Ljava/lang/String;"))
    public String redirectClickGetAction(ClickEvent.Action action) {
        if (action == null) {
            return "custom";
        }
        return action.getCanonicalName();
    }
    
}
