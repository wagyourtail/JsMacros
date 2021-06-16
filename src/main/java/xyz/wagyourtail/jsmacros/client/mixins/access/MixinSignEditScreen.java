package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.ISignEditScreen;

@Mixin(SignEditScreen.class)
public class MixinSignEditScreen implements ISignEditScreen {
    
    @Shadow
    @Final
    private SignBlockEntity sign;
    
    @Override
    public void jsmacros_setLine(int line, String text) {
        this.sign.setTextOnRow(line, new LiteralText(text));
    }
    
}
