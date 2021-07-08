package xyz.wagyourtail.jsmacros.client.access;

import net.minecraft.event.HoverEvent;

public interface IEntity {
    HoverEvent jsmacros_getHoverEvent();
    
    boolean isOnFire();
}