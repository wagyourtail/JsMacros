package xyz.wagyourtail.jsmacros.client.api.event.impl;

import xyz.wagyourtail.jsmacros.client.api.helpers.BossBarHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "Bossbar", oldName = "BOSSBAR_UPDATE")
public class EventBossbar implements BaseEvent {
    public final BossBarHelper bossBar;
    public final String uuid = null;
    public final String type = null;
    
    public EventBossbar(BossBarHelper bossBar) {
        if (bossBar != null) this.bossBar = new BossBarHelper();
        else this.bossBar = null;
        
        profile.triggerEvent(this);
    }

    public String toString() {
        return String.format("%s:{\"bossBar\": %s}", this.getEventName(), bossBar != null ? bossBar.toString() : uuid);
    }
}
