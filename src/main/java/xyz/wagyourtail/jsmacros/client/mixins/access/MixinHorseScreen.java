package xyz.wagyourtail.jsmacros.client.mixins.access;

import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import xyz.wagyourtail.jsmacros.client.access.IHorseScreen;

@Mixin(GuiScreenHorseInventory.class)
public class MixinHorseScreen implements IHorseScreen {
    @Shadow
    private EntityHorse horseEntity;

    @Override
    public Entity jsmacros_getEntity() {
        return horseEntity;
    }
}
