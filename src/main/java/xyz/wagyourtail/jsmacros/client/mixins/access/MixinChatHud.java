package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.IChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IChatHud;

@Mixin(GuiNewChat.class)
public class MixinChatHud implements IChatHud {

    @Shadow
    public void printChatMessageWithOptionalDeletion(IChatComponent message, int messageId) {}
    
    @Override
    public void jsmacros_addMessageBypass(IChatComponent message) {
        printChatMessageWithOptionalDeletion(message, 0);
    }

}
