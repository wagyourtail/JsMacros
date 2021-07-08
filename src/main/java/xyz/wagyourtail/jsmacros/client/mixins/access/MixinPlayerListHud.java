package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IPlayerListHud;

@Mixin(GuiPlayerTabOverlay.class)
public class MixinPlayerListHud implements IPlayerListHud {
    
    
    @Shadow private IChatComponent header;
    
    @Shadow private IChatComponent footer;
    
    @Override
    public IChatComponent jsmacros_getHeader() {
        return this.header;
    }
    
    @Override
    public IChatComponent jsmacros_getFooter() {
        return this.footer;
    }
    
}
