package xyz.wagyourtail.jsmacros.client.api.event.impl;

import net.minecraft.util.DamageSource;
import xyz.wagyourtail.jsmacros.client.api.helpers.EntityHelper;
import xyz.wagyourtail.jsmacros.core.event.BaseEvent;
import xyz.wagyourtail.jsmacros.core.event.Event;

/**
 * @author Wagyourtail
 * @since 1.2.7
 */
 @Event(value = "Damage", oldName = "DAMAGE")
public class EventDamage implements BaseEvent {
    public final EntityHelper<?> attacker;
    public final String source;
    public final float health;
    public final float change;
    
    public EventDamage(DamageSource source, float health, float change) {
        if (source.getSourceOfDamage() == null) this.attacker = null;
        else this.attacker = EntityHelper.create(source.getSourceOfDamage());
        this.source = source.getDamageType();
        this.health = health;
        this.change = change;
        
        profile.triggerEvent(this);
    }
    
    public String toString() {
        return String.format("%s:{\"health\": %f}", this.getEventName(), health);
    }
}
