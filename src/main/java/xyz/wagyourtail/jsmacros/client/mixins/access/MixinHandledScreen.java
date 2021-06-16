package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import net.minecraft.container.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IInventory;

@Mixin(ContainerScreen.class)
class MixinHandledScreen implements IInventory {

    @Shadow
    private Slot getSlotAt(double x, double y) {
        return null;
    }
    
    @Override
    public Slot jsmacros_getSlotUnder(double x, double y) {
        return getSlotAt(x, y);
    }
    
}
