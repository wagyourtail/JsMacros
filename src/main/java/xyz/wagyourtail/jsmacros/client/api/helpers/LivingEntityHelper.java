package xyz.wagyourtail.jsmacros.client.api.helpers;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class    LivingEntityHelper<T extends EntityLivingBase> extends EntityHelper<T> {

    public LivingEntityHelper(T e) {
        super(e);
    }

    /**
     * @since 1.2.7
     * @return entity status effects.
     */
    public List<StatusEffectHelper> getStatusEffects() {
        List<StatusEffectHelper> l = new ArrayList<>();
        for (PotionEffect i : ImmutableList.copyOf(base.getActivePotionEffects())) {
            l.add(new StatusEffectHelper(i));
        }
        return l;
    }
    
    /**
     * @since 1.2.7
     * @see xyz.wagyourtail.jsmacros.client.api.helpers.ItemStackHelper
     * @return the item in the entity's main hand.
     */
    public ItemStackHelper getMainHand() {
        return new ItemStackHelper(base.getHeldItem());
    }
    
    /**
     * @since 1.2.7
     * @return the item in the entity's off hand.
     */
    public ItemStackHelper getOffHand() {
        return null;
    }
    
    /**
     * @since 1.2.7
     * @return the item in the entity's head armor slot.
     */
    public ItemStackHelper getHeadArmor() {
        return new ItemStackHelper(base.getCurrentArmor(0));
    }
    
    /**
     * @since 1.2.7
     * @return the item in the entity's chest armor slot.
     */
    public ItemStackHelper getChestArmor() {
        return new ItemStackHelper(base.getCurrentArmor(1));
    }
    
    /**
     * @since 1.2.7
     * @return the item in the entity's leg armor slot.
     */
    public ItemStackHelper getLegArmor() {
        return new ItemStackHelper(base.getCurrentArmor(2));
    }
    
    /**
     * @since 1.2.7
     * @return the item in the entity's foot armor slot.
     */
    public ItemStackHelper getFootArmor() {
        return new ItemStackHelper(base.getCurrentArmor(3));
    }
    
    /**
     * @since 1.3.1
     * @return entity's health
     */
    public float getHealth() {
        return base.getHealth();
    }
    
    /**
     * @since 1.2.7
     * @return if the entity is in a bed.
     */
    public boolean isSleeping() {
        return base.isPlayerSleeping();
    }

    /**
     * @since 1.5.0
     * @return if the entity has elytra deployed
     */
    public boolean isFallFlying() {
        return false;
    }
    
}
