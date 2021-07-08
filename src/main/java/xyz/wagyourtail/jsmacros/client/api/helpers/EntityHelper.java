package xyz.wagyourtail.jsmacros.client.api.helpers;

import com.google.common.collect.Lists;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import xyz.wagyourtail.jsmacros.client.access.IEntity;
import xyz.wagyourtail.jsmacros.client.api.sharedclasses.PositionCommon;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.List;

/**
 * @author Wagyourtail
 *
 */
@SuppressWarnings("unused")
public class EntityHelper<T extends Entity> extends BaseHelper<T> {
    
    public EntityHelper(T e) {
        super(e);
    }
    
    /**
     * @return entity position.
     */
    public PositionCommon.Pos3D getPos() {
        return new PositionCommon.Pos3D(base.posX, base.posY, base.posZ);
    }
    
    /**
     * @since 1.0.8
     * @return the {@code x} value of the entity.
     */
    public double getX() {
        return base.posX;
    }

    /**
     * @since 1.0.8
     * @return the {@code y} value of the entity.
     */
    public double getY() {
        return base.posY;
    }
    
    /**
     * @since 1.0.8
     * @return the {@code z} value of the entity.
     */
    public double getZ() {
        return base.posZ;
    }

    /**
     * @since 1.2.8
     * @return the current eye height offset for the entitye.
     */
    public double getEyeHeight() {
        return base.getEyeHeight();
    }

    /**
     * @since 1.0.8
     * @return the {@code pitch} value of the entity.
     */
    public float getPitch() {
        return base.rotationPitch;
    }
    
    /**
     * @since 1.0.8
     * @return the {@code yaw} value of the entity.
     */
    public float getYaw() {
        return MathHelper.wrapAngleTo180_float(base.rotationYaw);
    }
    
    /**
     * @return the name of the entity.
     */
    public String getName() {
        return base.getName();
    }
    
    /**
     * @return the type of the entity.
     */
    public String getType() {
        return EntityList.getEntityString(base);
    }
    
    /**
     * @since 1.1.9
     * @return if the entity has the glowing effect.
     */
    public boolean isGlowing() {
        return false;
    }
    
    /**
     * @since 1.1.9
     * @return if the entity is in lava.
     */
    public boolean isInLava() {
        return base.isInLava();
    }
    
    /**
     * @since 1.1.9
     * @return if the entity is on fire.
     */
    public boolean isOnFire() {
        return ((IEntity) base).isOnFire();
    }
    
    /**
     * @since 1.1.8 [citation needed]
     * @return the vehicle of the entity.
     */
    public EntityHelper<?> getVehicle() {
        Entity parent = base.ridingEntity;
        if (parent != null) return EntityHelper.create(parent);
        return null;
    }
    
    /**
     * @since 1.1.8 [citation needed]
     * @return the entity passengers.
     */
    public List<EntityHelper<?>> getPassengers() {
        return Lists.newArrayList(EntityHelper.create(base.riddenByEntity));
    }
    
    /**
     * @since 1.2.8
     * @return
     */
    public String getNBT() {
        return base.getNBTTagCompound().toString();
    }
    
    /**
     * Sets whether the entity is glowing.
     * @since 1.1.9
     * @param val
     * @return
     */
    public EntityHelper<T> setGlowing(boolean val) {
//        base.setGlowing(val);
        return this;
    }
    
    /**
     * Checks if the entity is still alive.
     * @since 1.2.8
     * @return
     */
    public boolean isAlive() {
        return base.isEntityAlive();
    }
    
    public String toString() {
        return String.format("Entity:{\"name\":\"%s\", \"type\":\"%s\"}", this.getName(), this.getType());
    }
    
    public static EntityHelper<?> create(Entity e) {
        if (e instanceof EntityPlayerSP) return new ClientPlayerEntityHelper<>((EntityPlayerSP) e);
        if (e instanceof EntityPlayer) return new PlayerEntityHelper<>((EntityPlayer) e);
        if (e instanceof EntityVillager) return new MerchantEntityHelper((EntityVillager) e);
        if (e instanceof EntityLivingBase) return new LivingEntityHelper<>((EntityLivingBase) e);
        return new EntityHelper<>(e);
    }
}