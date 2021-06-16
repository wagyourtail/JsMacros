package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.InputUtil;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.access.IRecipeBookWidget;
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
    static final MinecraftClient mc = MinecraftClient.getInstance();
    public final int action;
    public final String key;
    public final String mods;
    
    public EventKey(int key, int scancode, int action, int mods) {
        
        InputUtil.KeyCode keycode;
        if (key <= 7) keycode = InputUtil.Type.MOUSE.createFromCode(key);
        else keycode = InputUtil.Type.KEYSYM.createFromCode(key);
        
        this.action = action;
        this.key = keycode.getName();
        this.mods = getKeyModifiers(mods);
        
        
        if (JsMacros.keyBinding.matchesKey(key, scancode) && action == 1 && mc.currentScreen == null) {
            if (JsMacros.prevScreen instanceof EditorScreen) {
                ((EditorScreen) JsMacros.prevScreen).blockFirst = true;
            }
            mc.openScreen(JsMacros.prevScreen);
            return;
        }
        
        if (keycode == InputUtil.UNKNOWN_KEYCODE) return;
        
        synchronized (FKeyBind.pressedKeys) {
            if (action == 1) FKeyBind.pressedKeys.add(keycode.getName());
            else FKeyBind.pressedKeys.remove(keycode.getName());
        }

        if (mc.currentScreen != null) {
            if (Core.instance.config.getOptions(ClientConfigV2.class).disableKeyWhenScreenOpen) return;
            if (mc.currentScreen instanceof BaseScreen) return;
            Element focused = mc.currentScreen.getFocused();
            if (focused instanceof TextFieldWidget) return;
            if (focused instanceof RecipeBookWidget && ((IRecipeBookWidget)focused).jsmacros_isSearching()) return;
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
    

    
    /**
     * turn an {@link java.lang.Integer Integer} for key modifiers into a Translation Key. 
     * @param mods
     * @return
     */
    public static String getKeyModifiers(int mods) {
        String s = "";
        if ((mods & 1) == 1) {
            s += "key.keyboard.left.shift";
        }
        if ((mods & 2) == 2) {
            if (s.length() > 0) s += "+";
            s += "key.keyboard.left.control";
        }
        if ((mods & 4) == 4) {
            if (s.length() > 0) s += "+";
            s += "key.keyboard.left.alt";
        }
        return s;
    }
    
    /**
     * turn a Translation Key for modifiers into an {@link java.lang.Integer Integer}. 
     * @param mods
     * @return
     */
    public static int getModInt(String mods) {
        int i = 0;
        String[] modArr = mods.split("\\+");
        for (String mod : modArr) {
            switch (mod) {
                case "key.keyboard.left.shift":
                case "key.keyboard.right.shift":
                    i |= 1;
                    break;
                case "key.keyboard.left.control":
                case "key.keyboard.right.control":
                    i |= 2;
                    break;
                case "key.keyboard.left.alt":
                case "key.keyboard.right.alt":
                    i |= 4;
                    break;
                default:
            }
        }
        return i;
        
    }
}
