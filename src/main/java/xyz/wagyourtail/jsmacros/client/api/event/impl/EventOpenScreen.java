package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.client.gui.GuiScreen;
import xyz.wagyourtail.jsmacros.client.JsMacros;
import xyz.wagyourtail.jsmacros.client.api.sharedinterfaces.IScreen;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "OpenScreen", oldName = "OPEN_SCREEN")
public class EventOpenScreen implements BaseEvent {
    public final IScreen screen;
    public final String screenName;
    
    public EventOpenScreen(GuiScreen screen) {
        this.screen = (IScreen) screen;
        this.screenName = JsMacros.getScreenName(screen);
        
        profile.triggerEvent(this);
    }
    
    public String toString() {
        return String.format("%s:{\"screenName\": \"%s\"}", this.getEventName(), screenName);
    }
    
}
