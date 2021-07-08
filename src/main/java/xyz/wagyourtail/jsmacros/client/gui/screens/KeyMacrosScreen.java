package xyz.wagyourtail.jsmacros.client.gui.screens;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.gui.GuiScreen;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventKey;
import xyz.wagyourtail.jsmacros.client.config.ClientConfigV2;
import xyz.wagyourtail.jsmacros.client.gui.containers.MacroContainer;
import xyz.wagyourtail.jsmacros.core.Core;
import xyz.wagyourtail.jsmacros.core.config.ScriptTrigger;
import xyz.wagyourtail.jsmacros.core.event.BaseListener;
import xyz.wagyourtail.jsmacros.core.event.Event;
import xyz.wagyourtail.jsmacros.core.event.IEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class KeyMacrosScreen extends MacroScreen {

    public KeyMacrosScreen(GuiScreen parent) {
        super(parent);
    }

    @Override
    public void initGui() {
        super.initGui();
        keyScreen.setColor(0x4FFFFFFF);

        eventScreen.onPress = (btn) -> {
            assert mc != null;
            mc.displayGuiScreen(new EventMacrosScreen(this));
        };

        Set<IEventListener> listeners = Core.instance.eventRegistry.getListeners().get(EventKey.class.getAnnotation(Event.class).value());
        List<ScriptTrigger> macros = new ArrayList<>();

        if (listeners != null) for (IEventListener event : ImmutableList.copyOf(listeners)) {
            if (event instanceof BaseListener && ((BaseListener) event).getRawTrigger().triggerType != ScriptTrigger.TriggerType.EVENT) macros.add(((BaseListener) event).getRawTrigger());
        }

        macros.sort(Core.instance.config.getOptions(ClientConfigV2.class).getSortComparator());

        for (ScriptTrigger macro : macros) {
            addMacro(macro);
        }
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        StringBuilder translationKey = new StringBuilder();
        if (modifiers != 0) {
            translationKey.append(modifiers).append("+");
        }
        translationKey.append(keyCode);
        for (MacroContainer macro : macros) {
            if (!macro.onKey(translationKey.toString())) return false;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        int mods = createModifiers();
        String translationKey = mods > 0 ? Integer.toString(mods) : "";
        if (!translationKey.equals("")) translationKey += "+";
        translationKey += (button-100);
        for (MacroContainer macro : macros) {
            if (!macro.onKey(translationKey)) return;
        }
        super.mouseReleased(mouseX, mouseY, button);
    }
}
