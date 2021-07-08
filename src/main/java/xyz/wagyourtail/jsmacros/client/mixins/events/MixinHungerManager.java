package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.util.FoodStats;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventHungerChange;

@Mixin(FoodStats.class)
public class MixinHungerManager {
    
    @Shadow
    private int foodLevel;
    
    @Inject(at = @At("HEAD"), method= "setFoodLevel")
    public void onSetFoodLevel(int foodLevel, CallbackInfo info) {
        if (foodLevel != this.foodLevel) {
            new EventHungerChange(foodLevel);
        }
    }
}
