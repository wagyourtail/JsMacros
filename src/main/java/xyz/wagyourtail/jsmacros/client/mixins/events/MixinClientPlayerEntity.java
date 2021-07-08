package xyz.wagyourtail.jsmacros.client.mixins.events;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovementInput;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.wagyourtail.jsmacros.client.access.ISignEditScreen;
import xyz.wagyourtail.jsmacros.client.api.classes.PlayerInput;
import xyz.wagyourtail.jsmacros.client.api.event.impl.*;
import xyz.wagyourtail.jsmacros.client.movement.MovementQueue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(EntityPlayerSP.class)
abstract class MixinClientPlayerEntity extends AbstractClientPlayer {
    @Shadow
    protected Minecraft mc;
    
    @Shadow
    @Final
    public NetHandlerPlayClient sendQueue;

    @Shadow
    public MovementInput movementInput;


    @Shadow public abstract boolean isSneaking();

    @Override
    public void setAir(int air) {
        if (air % 20 == 0) new EventAirChange(air);
        super.setAir(air);
    }
    
    @Inject(at = @At("HEAD"), method="setXPStats")
    public void onSetExperience(float progress, int total, int level, CallbackInfo info) {
        new EventEXPChange(progress, total, level);
    }
    
    @Inject(at = @At("TAIL"), method="damageEntity")
    private void onApplyDamage(DamageSource source, float amount, final CallbackInfo info) {
        new EventDamage(source, this.getHealth(), amount);
    }
    
    @Inject(at = @At("HEAD"), method="openEditSign", cancellable= true)
    public void onOpenEditSignScreen(TileEntitySign sign, CallbackInfo info) {
        List<String> lines = new ArrayList<>(Arrays.asList("", "", "", ""));
        final EventSignEdit event = new EventSignEdit(lines, sign.getPos().getX(), sign.getPos().getY(), sign.getPos().getZ());
        lines = event.signText;
        if (event.closeScreen) {
            for (int i = 0; i < 4; ++i) {
                sign.signText[i] = new ChatComponentText(lines.get(i));
            }
            sign.markDirty();
            sendQueue.addToSendQueue(new C12PacketUpdateSign(sign.getPos(), lines.stream().map(ChatComponentText::new).toArray(IChatComponent[]::new)));
            info.cancel();
            return;
        }
        //this part to not info.cancel is here for more compatibility with other mods.
        boolean cancel = false;
        for (String line : lines) {
            if (!line.equals("")) {
                cancel = true;
                break;
            }
        } //else
        if (cancel) {
            final GuiEditSign signScreen = new GuiEditSign(sign);
            mc.displayGuiScreen(signScreen);
            for (int i = 0; i < 4; ++i) {
                ((ISignEditScreen) signScreen).jsmacros_setLine(i, lines.get(i));
            }
            info.cancel();
        }
    }

    @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MovementInput;updatePlayerMoveState()V", shift = At.Shift.AFTER))
    public void overwriteInputs(CallbackInfo ci) {
        PlayerInput moveInput = MovementQueue.tick(mc.thePlayer);
        if (moveInput == null) {
            return;
        }
        this.movementInput.moveForward = moveInput.movementForward;
        this.movementInput.moveStrafe = moveInput.movementSideways;
        this.movementInput.jump = moveInput.jumping;
        this.movementInput.sneak = moveInput.sneaking;
        KeyBinding.setKeyBindState(this.mc.gameSettings.keyBindSprint.getKeyCode(), moveInput.sprinting);
        this.rotationYaw = moveInput.yaw;
        this.rotationPitch = moveInput.pitch;
    }


    public MixinClientPlayerEntity(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }
}
