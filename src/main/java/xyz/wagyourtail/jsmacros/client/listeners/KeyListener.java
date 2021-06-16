package xyz.wagyourtail.jsmacros.client.listeners;

import net.minecraft.client.util.InputUtil;
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
    private String key;
    
    public KeyListener(ScriptTrigger macro, Core runner) {
        super(macro, runner);
        StringBuilder mods = new StringBuilder();
        this.mods = 0;
        try {
            String[] comb = macro.event.split("\\+");
            int i = 0;
            for (String key : comb) {
                if (++i == comb.length) this.key = key;
                else {
                    if (i > 1) mods.append("+");
                    mods.append(key);
                }
            }
            this.mods = EventKey.getModInt(mods.toString());
        } catch(Exception e) {
            key = InputUtil.UNKNOWN_KEYCODE.getName();
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
        if (event.key.equals(key) && (EventKey.getModInt(event.mods) & mods) == mods) {
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