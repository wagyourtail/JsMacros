package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.library.impl.FKeyBind;
import xyz.wagyourtail.jsmacros.client.config.ClientConfigV2;
import xyz.wagyourtail.jsmacros.client.gui.screens.BaseScreen;
import xyz.wagyourtail.jsmacros.client.gui.screens.EditorScreen;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "Key", oldName = "KEY")
public class EventKey implements BaseEvent {
    static final Minecraft mc = Minecraft.getMinecraft();
    public final int action;
    public final int key;
    public final int mods;
    
    public EventKey(int key, int scancode, int action, int mods) {
        this.action = action;
        this.key = key;
        this.mods = mods;
        
        
        if (JsMacros.keyBinding.isPressed() && action == 1 && mc.currentScreen == null) {
            if (JsMacros.prevScreen instanceof EditorScreen) {
                ((EditorScreen) JsMacros.prevScreen).blockFirst = true;
            }
            mc.displayGuiScreen(JsMacros.prevScreen);
            return;
        }
        
        if (key == Keyboard.KEY_NONE) return;
        
        synchronized (FKeyBind.pressedKeys) {
            if (action == 1) FKeyBind.pressedKeys.add(key);
            else FKeyBind.pressedKeys.remove(key);
        }

        if (mc.currentScreen != null) {
            if (Core.instance.config.getOptions(ClientConfigV2.class).disableKeyWhenScreenOpen) return;
            if (mc.currentScreen instanceof BaseScreen) return;
        }
        
        if (action == 1) {
            if (key == 340 || key == 344) mods -= 1;
            else if (key == 341 || key == 345) mods -= 2;
            else if (key == 342 || key == 346) mods -= 4;
        }
        
        profile.triggerEvent(this);
        
    }

    public String toString() {
        return String.format("%s:{\"key\": \"%s\"}", this.getEventName(), key);
    }
}
