package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.resources.Locale;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(Locale.class)
public interface MixinLocale {
    @Accessor(value = "properties")
    Map<String, String> getProperties();
}
