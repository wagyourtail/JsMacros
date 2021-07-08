package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import xyz.wagyourtail.jsmacros.client.access.IMinecraftClient;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;

/**
 * @author Wagyourtail
 * @see xyz.wagyourtail.jsmacros.client.api.helpers.PlayerEntityHelper
 * @since 1.0.3
 */
@SuppressWarnings("unused")
public class ClientPlayerEntityHelper<T extends EntityPlayerSP> extends PlayerEntityHelper<T> {
    protected final Minecraft mc = Minecraft.getMinecraft();

    public ClientPlayerEntityHelper(T e) {
        super(e);
    }

    /**
     * @param yaw   (was pitch prior to 1.2.6)
     * @param pitch (was yaw prior to 1.2.6)
     * @return
     * @since 1.0.3
     */
    public ClientPlayerEntityHelper<T> lookAt(double yaw, double pitch) {
        pitch = MathHelper.clamp_double(pitch, -90.0D, 90.0D);
        base.prevRotationPitch = base.rotationPitch;
        base.prevRotationYaw = base.rotationYaw;
        base.rotationPitch = (float) pitch;
        base.rotationYaw = (float) MathHelper.wrapAngleTo180_double(yaw);
        return this;
    }

    /**
     * look at the specified coordinates.
     *
     * @param x
     * @param y
     * @param z
     * @return
     * @since 1.2.8
     */
    public ClientPlayerEntityHelper<T> lookAt(double x, double y, double z) {
        PositionCommon.Vec3D vec = new PositionCommon.Vec3D(base.posX, base.posY + base.getEyeHeight(), base.posZ, x, y, z);
        lookAt(vec.getYaw(), vec.getPitch());
        return this;
    }

    /**
     * @param entity
     * @since 1.5.0
     */
    public void attack(EntityHelper<?> entity) {
        if (entity.getRaw() == mc.thePlayer) throw new AssertionError("Can't interact with self!");
        assert mc.playerController != null;
        mc.playerController.attackEntity(mc.thePlayer, entity.getRaw());
        assert mc.thePlayer != null;
        mc.thePlayer.swingItem();
    }

    /**
     * @param x
     * @param y
     * @param z
     * @param direction 0-5 in order: [DOWN, UP, NORTH, SOUTH, WEST, EAST];
     * @since 1.5.0
     */
    public void attack(int x, int y, int z, int direction) {
        assert mc.playerController != null;
        mc.playerController.onPlayerDamageBlock(new BlockPos(x, y, z), EnumFacing.values()[direction]);
        assert mc.thePlayer != null;
        mc.thePlayer.swingItem();
    }

    /**
     * @param entity
     * @since 1.5.0
     */
    public void interact(EntityHelper<?> entity) {
        if (entity.getRaw() == mc.thePlayer) throw new AssertionError("Can't interact with self!");
        assert mc.playerController != null;
        boolean result = mc.playerController.interactWithEntitySendPacket(mc.thePlayer, entity.getRaw());
        assert mc.thePlayer != null;
        if (result)
            mc.thePlayer.swingItem();
    }

    /**
     * @since 1.5.0
     */
    public void interact() {
        assert mc.playerController != null;
        boolean result = mc.playerController.sendUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getHeldItem());
        assert mc.thePlayer != null;
        if (result)
            mc.thePlayer.swingItem();
    }

    /**
     * @param x
     * @param y
     * @param z
     * @param direction
     * @since 1.5.0
     */
    public void interact(int x, int y, int z, int direction) {
        assert mc.thePlayer != null;
        boolean result = mc.thePlayer.interactAt(mc.thePlayer,new Vec3(x, y, z));
        if (result)
            mc.thePlayer.swingItem();
    }

    /**
     * @since 1.5.0
     */
    public void interactDefault() {
        ((IMinecraftClient) mc).jsmacros_doItemUse();
    }

    public void attack() {
        ((IMinecraftClient) mc).jsmacros_doAttack();
    }

    /**
     * @return
     * @since 1.1.2
     */
    public int getFoodLevel() {
        return base.getFoodStats().getFoodLevel();
    }


    public String toString() {
        return "Client" + super.toString();
    }
}
