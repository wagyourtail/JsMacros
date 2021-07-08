package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.boss.IBossDisplayData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventBossbar;
import xyz.wagyourtail.jsmacros.client.api.helpers.BossBarHelper;

@Mixin(BossStatus.class)
public class MixinBossStatus {
    
    @Inject(at = @At("TAIL"), method = "setBossStatus")
    private static void onBossStatus(IBossDisplayData displayData, boolean hasColorModifierIn, CallbackInfo ci) {
        new EventBossbar(new BossBarHelper());
    }
    
}
