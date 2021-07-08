package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.util.IChatComponent;
import xyz.wagyourtail.jsmacros.client.api.helpers.TextHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "RecvMessage", oldName = "RECV_MESSAGE")
public class EventRecvMessage implements BaseEvent {
    public TextHelper text;
    
    public EventRecvMessage(IChatComponent message) {
        this.text = new TextHelper(message);
        
        profile.triggerEventJoin(this);
    }
    
    public String toString() {
        return String.format("%s:{\"text\": %s}", this.getEventName(), text);
    }
}
