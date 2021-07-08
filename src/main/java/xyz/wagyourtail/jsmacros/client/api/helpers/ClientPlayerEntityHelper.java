package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import xyz.wagyourtail.jsmacros.client.access.IMinecraftClient;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;

/**
 * @author Wagyourtail
 * @see xyz.wagyourtail.jsmacros.client.api.helpers.PlayerEntityHelper
 * @since 1.0.3
 */
@SuppressWarnings("unused")
public class ClientPlayerEntityHelper<T extends ClientPlayerEntity> extends PlayerEntityHelper<T> {
    protected final MinecraftClient mc = MinecraftClient.getInstance();

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
        pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
        base.prevPitch = base.pitch;
        base.prevYaw = base.yaw;
        base.pitch = (float)pitch;
        base.yaw = MathHelper.wrapDegrees((float)yaw);
        if (base.getVehicle() != null) {
            base.getVehicle().onPassengerLookAround(base);
        }
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
        PositionCommon.Vec3D vec = new PositionCommon.Vec3D(base.getX(), base.getY() + base.getEyeHeight(base.getPose()), base.getZ(), x, y, z);
        lookAt(vec.getYaw(), vec.getPitch());
        return this;
    }

    /**
     * @param entity
     * @since 1.5.0
     */
    public void attack(EntityHelper<?> entity) {
        if (entity.getRaw() == mc.player) throw new AssertionError("Can't interact with self!");
        assert mc.interactionManager != null;
        mc.interactionManager.attackEntity(mc.player, entity.getRaw());
        assert mc.player != null;
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    /**
     * @param x
     * @param y
     * @param z
     * @param direction 0-5 in order: [DOWN, UP, NORTH, SOUTH, WEST, EAST];
     * @since 1.5.0
     */
    public void attack(int x, int y, int z, int direction) {
        assert mc.interactionManager != null;
        mc.interactionManager.attackBlock(new BlockPos(x, y, z), Direction.values()[direction]);
        assert mc.player != null;
        mc.player.swingHand(Hand.MAIN_HAND);
    }

    /**
     * @param entity
     * @param offHand
     * @since 1.5.0
     */
    public void interact(EntityHelper<?> entity, boolean offHand) {
        if (entity.getRaw() == mc.player) throw new AssertionError("Can't interact with self!");
        assert mc.interactionManager != null;
        Hand hand = offHand ? Hand.OFF_HAND : Hand.MAIN_HAND;
        ActionResult result = mc.interactionManager.interactEntity(mc.player, entity.getRaw(), hand);
        assert mc.player != null;
        if (result.isAccepted())
            mc.player.swingHand(hand);
    }

    /**
     * @param offHand
     * @since 1.5.0
     */
    public void interact(boolean offHand) {
        assert mc.interactionManager != null;
        Hand hand = offHand ? Hand.OFF_HAND : Hand.MAIN_HAND;
        ActionResult result = mc.interactionManager.interactItem(mc.player, mc.world, hand);
        assert mc.player != null;
        if (result.isAccepted())
            mc.player.swingHand(hand);
    }

    /**
     * @param x
     * @param y
     * @param z
     * @param direction
     * @param offHand
     * @since 1.5.0
     */
    public void interact(int x, int y, int z, int direction, boolean offHand) {
        Hand hand = offHand ? Hand.OFF_HAND : Hand.MAIN_HAND;
        assert mc.interactionManager != null;
        ActionResult result = mc.interactionManager.interactBlock(mc.player, mc.world, hand,
            new BlockHitResult(Vec3d.ZERO, Direction.values()[direction], new BlockPos(x, y, z), false)
        );
        assert mc.player != null;
        if (result.isAccepted())
            mc.player.swingHand(hand);
    }

    /**
     * @since 1.5.0
     */
    public void interact() {
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
        return base.getHungerManager().getFoodLevel();
    }


    public String toString() {
        return "Client" + super.toString();
    }
}
