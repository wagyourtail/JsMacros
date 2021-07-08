package xyz.wagyourtail.jsmacros.client.api.library.impl;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import xyz.wagyourtail.jsmacros.core.library.BaseLibrary;
import xyz.wagyourtail.jsmacros.core.library.Library;

import java.util.*;

/**
 *
 * Functions for getting and modifying key pressed states.
 * 
 * An instance of this class is passed to scripts as the {@code KeyBind} variable.
 * 
 * @author Wagyourtail
 */
 @Library("KeyBind")
 @SuppressWarnings("unused")
public class FKeyBind extends BaseLibrary {
    private static final Minecraft mc = Minecraft.getMinecraft();
    /**
     * Don't modify
     */
    public static final Set<Integer> pressedKeys = new HashSet<>();
    
    /**
     * Dont use this one... get the raw minecraft keycode class.
     *
     * @param keyName
     * @return the raw minecraft keycode class
     */
    public int getKeyCode(String keyName) {
        //TODO:
//        try {
//            return InputUtil.fromName(keyName);
//        } catch (Exception e) {
//            return InputUtil.UNKNOWN_KEYCODE;
//        }
        return 0;
    }
    
    /**
     * @since 1.2.2
     *
     * @return A {@link java.util.Map Map} of all the minecraft keybinds.
     */
    public Map<String, Integer> getKeyBindings() {
        Map<String, Integer> keys = new HashMap<>();
        for (KeyBinding key : ImmutableList.copyOf(mc.gameSettings.keyBindings)) {
            keys.put(key.getKeyDescription(), key.getKeyCode());
        }
        return keys;
    }
    
    /**
     * Sets a minecraft keybind to the specified key.
     *
     * @since 1.2.2
     *
     * @param bind
     * @param key
     */
    public void setKeyBind(String bind, int key) {
        for (KeyBinding keybind : mc.gameSettings.keyBindings) {
            if (keybind.getKeyDescription().equals(bind)) {
                keybind.setKeyCode(key);
                return;
            }
        }
    }
    
    /**
     * Set a key-state for a key.
     *
     * @param keyName
     * @param keyState
     */
    public void key(String keyName, boolean keyState) {
        key(getKeyCode(keyName), keyState);
    }
    
    /**
     * Don't use this one... set the key-state using the raw minecraft keycode class.
     *
     * @param keyBind
     * @param keyState
     */
    protected void key(int keyBind, boolean keyState) {
        KeyBinding.setKeyBindState(keyBind, keyState);
    }
    
    /**
     * Set a key-state using the name of the keybind rather than the name of the key.
     *
     * This is probably the one you should use.
     *
     * @since 1.2.2
     *
     * @param keyBind
     * @param keyState
     */
    public void keyBind(int keyBind, boolean keyState) {
        KeyBinding.setKeyBindState(keyBind, keyState);
    }
    
    /**
     * Don't use this one... set the key-state using the raw minecraft keybind class.
     *
     * @param keyBind
     * @param keyState
     */
    protected void key(KeyBinding keyBind, boolean keyState) {
        KeyBinding.setKeyBindState(keyBind.getKeyCode(), keyState);
    }
    
    /**
     * @since 1.2.6
     *
     * @return a list of currently pressed keys.
     */
    public List<Integer> getPressedKeys() {
        synchronized (pressedKeys) {
            return new ArrayList<>(pressedKeys);
        }
    }
}
