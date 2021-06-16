package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.entity.Entity;
import net.minecraft.text.HoverEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import xyz.wagyourtail.jsmacros.client.access.IEntity;

@Mixin(Entity.class)
public abstract class MixinEntity implements IEntity {
    
    @Override
    @Invoker("getHoverEvent")
    public abstract HoverEvent jsmacros_getHoverEvent();
}