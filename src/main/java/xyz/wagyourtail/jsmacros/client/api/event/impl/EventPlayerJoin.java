package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.client.network.NetworkPlayerInfo;
import xyz.wagyourtail.jsmacros.client.api.helpers.PlayerListEntryHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

import java.util.UUID;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "PlayerJoin", oldName = "PLAYER_JOIN")
public class EventPlayerJoin implements BaseEvent {
    public final String UUID;
    public final PlayerListEntryHelper player;
    
    public EventPlayerJoin(UUID uuid, NetworkPlayerInfo player) {
        this.UUID = uuid.toString();
        this.player = new PlayerListEntryHelper(player);
        
        profile.triggerEvent(this);
    }
    
    public String toString() {
        return String.format("%s:{\"player\": %s}", this.getEventName(), player.toString());
    }
}
