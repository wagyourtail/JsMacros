package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatComponentText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.ISignEditScreen;

@Mixin(GuiEditSign.class)
public class MixinSignEditScreen implements ISignEditScreen {
    
    @Shadow
    private TileEntitySign tileSign;
    
    @Override
    public void jsmacros_setLine(int line, String text) {
        this.tileSign.signText[line] = new ChatComponentText(text);
    }
    
}
