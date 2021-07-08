package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.entity.Entity;
import net.minecraft.event.HoverEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import xyz.wagyourtail.jsmacros.client.access.IEntity;

@Mixin(Entity.class)
public abstract class MixinEntity implements IEntity {
    
    @Shadow private int fire;
    
    @Override
    @Invoker("getHoverEvent")
    public abstract HoverEvent jsmacros_getHoverEvent();
    
    @Override
    public boolean isOnFire() {
        return fire > 0;
    }
}