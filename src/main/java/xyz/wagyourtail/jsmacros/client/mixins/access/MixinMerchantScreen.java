package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IMerchantScreen;

@Mixin(MerchantScreen.class)
public abstract class MixinMerchantScreen implements IMerchantScreen {
    
    @Shadow private int field_19161;
    
    @Shadow protected abstract void syncRecipeIndex();
    
    @Override
    public void jsmacros_selectIndex(int index) {
        field_19161 = index;
        syncRecipeIndex();
    }
    
}
