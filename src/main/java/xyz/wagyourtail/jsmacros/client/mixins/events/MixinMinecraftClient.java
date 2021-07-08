package xyz.wagyourtail.jsmacros.client.mixins.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventDimensionChange;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventDisconnect;
import xyz.wagyourtail.jsmacros.client.api.event.impl.EventOpenScreen;

@Mixin(Minecraft.class)
public class MixinMinecraftClient {

    @Shadow
    public GuiScreen currentScreen;
    
    @Inject(at = @At("HEAD"), method="loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;)V")
    public void onJoinWorld(WorldClient world, CallbackInfo info) {
        if (world != null)
            new EventDimensionChange(world.getWorldInfo().getWorldName());
        else
            new EventDisconnect();
    }
    
    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;currentScreen:Lnet/minecraft/client/gui/GuiScreen;", opcode = Opcodes.PUTFIELD), method="displayGuiScreen")
    public void onOpenScreen(GuiScreen screen, CallbackInfo info) {
        if (screen != currentScreen) new EventOpenScreen(screen);
    }
}
