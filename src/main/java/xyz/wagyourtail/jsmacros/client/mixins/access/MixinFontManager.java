package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.FontStorage;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IFontManager;

import java.util.Map;
import java.util.Set;

@Mixin(FontManager.class)
public class MixinFontManager implements IFontManager {
    @Shadow @Final private Map<Identifier, FontStorage> textRenderers;
    
    @Override
    public Set<Identifier> jsmacros_getFontList() {
        return textRenderers.keySet();
    }
    
}
