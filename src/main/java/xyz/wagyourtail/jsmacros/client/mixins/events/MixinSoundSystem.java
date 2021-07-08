package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventSound;

@Mixin(SoundHandler.class)
public class MixinSoundSystem {
    @Inject(at = @At("HEAD"), method="playSound")
    public void onPlay(ISound instance, CallbackInfo info) {
        String id = null;
        try {
            id = instance.getSoundLocation().toString();
        } catch (NullPointerException e) {}
        float volume = 1.0F;
        float pitch = 1.0F;
        try {
            volume = instance.getVolume();
            pitch = instance.getPitch();
        } catch (NullPointerException e) {}
        
        new EventSound(id, volume, pitch, instance.getXPosF(), instance.getYPosF(), instance.getZPosF());
    }
}
