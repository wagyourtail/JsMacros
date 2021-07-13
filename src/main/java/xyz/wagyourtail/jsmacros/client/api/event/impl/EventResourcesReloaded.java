package xyz.wagyourtail.jsmacros.client.api.event.impl;

import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * This event is fired after resources have been reloaded, i.e. after the splash screen has finished.
 * This includes when the game is finished loading and the title screen becomes visible, which you can check using
 * {@code isGameStart}.
 *
 * @since 1.5.1
 */
@Event("ResourcesReloaded")
public class EventResourcesReloaded implements BaseEvent {
    public final boolean isGameStart;

    public EventResourcesReloaded(boolean isGameStart) {
        this.isGameStart = isGameStart;

        profile.triggerEvent(this);
    }

    @Override
    public String toString() {
        return String.format("%s:{\"isGameStart\": %s}", this.getEventName(), isGameStart);
    }
}
