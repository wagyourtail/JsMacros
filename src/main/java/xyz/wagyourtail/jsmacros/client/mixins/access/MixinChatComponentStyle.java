package xyz.wagyourtail.jsmacros.client.mixins.access;

import com.mojang.brigadier.Message;
import net.minecraft.util.ChatComponentStyle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChatComponentStyle.class)
public abstract class MixinChatComponentStyle implements Message {

    @Shadow public abstract String getUnformattedText();

    @Override
    public String getString() {
        return getUnformattedText();
    }

}
