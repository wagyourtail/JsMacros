package xyz.wagyourtail.jsmacros.client.listeners;

import org.lwjgl.input.Keyboard;
import xyz.wagyourtail.Pair;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventKey;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.BaseListener;
import xyz.wagyourtail.jsmacros.core.language.ContextContainer;
import xyz.wagyourtail.jsmacros.core.language.ScriptContext;

import java.util.concurrent.Semaphore;

public class KeyListener extends BaseListener {
    private int mods;
    private int key;
    
    public KeyListener(ScriptTrigger macro, Core runner) {
        super(macro, runner);
        this.mods = 0;
        try {
            String[] comb = macro.event.split("\\+");
            int i = 0;
            for (String key : comb) {
                if (++i == comb.length) {
                    if (key.equals("")) this.key = Keyboard.KEY_NONE;
                    else this.key = Integer.parseInt(key);
                }
                else {
                    if (key.equals("")) this.mods = Keyboard.KEY_NONE;
                    else this.mods = Integer.parseInt(key);
                }
            }
        } catch(Exception e) {
            key = Keyboard.KEY_NONE;
            mods = 0;
        }
    }
    
    @Override
    public ContextContainer<?> trigger(BaseEvent event) {
        if (check((EventKey) event)) {
            return runScript(event);
        }
        return null;
    }
    
    private boolean check(EventKey event) {
        boolean keyState = event.action == 1;
        if (event.key == key && (event.mods & mods) == mods) {
            switch(getRawTrigger().triggerType) {
                case KEY_FALLING:
                    return !keyState;
                case KEY_RISING:
                    return keyState;
                case EVENT:
                case KEY_BOTH:
                default:
                    return true;
            }
        }
        return false;
    }
}