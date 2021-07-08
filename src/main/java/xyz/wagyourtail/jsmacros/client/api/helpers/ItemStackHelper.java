package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.registry.Registry;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * @author Wagyourtail
 *
 */
@SuppressWarnings("unused")
public class ItemStackHelper extends BaseHelper<ItemStack> {
    
    public ItemStackHelper(ItemStack i) {
        super(i);
    }
    
    /**
     * Sets the item damage value.
     * 
     * You may want to use {@link ItemStackHelper#copy()} first.
     * 
     * @since 1.2.0
     * 
     * @param damage
     * @return
     */
    public ItemStackHelper setDamage(int damage) {
        base.setDamage(damage);
        return this;
    }
    
    /**
     * @since 1.2.0
     * @return
     */
    public boolean isDamageable() {
        return base.isDamageable();
    }
    
    /**
     * @since 1.2.0
     * @return
     */
    public boolean isEnchantable() {
        return base.isEnchantable();
    }
    
    /**
     * @return
     */
    public int getDamage() {
        return base.getDamage();
    }
    
    /**
     * @return
     */
    public int getMaxDamage() {
        return base.getMaxDamage();
    }
    
    /**
     * @since 1.2.0
     * @return
     */
    public String getDefaultName() {
        return base.getItem().getName().getString();
    }
    
    /**
     * @return
     */
    public String getName() {
        return base.getName().getString();
    }
    
    /**
     * @return
     */
    public int getCount() {
        return base.getCount();
    }
    
    /**
     * @return
     */
    public int getMaxCount() {
        return base.getMaxCount();
    }
    
    /**
     * @since 1.1.6
     * @return
     */
    public String getNBT() {
        CompoundTag tag = base.getTag();
        if (tag != null) return tag.toString();
        else return "{}";
    }
    
    /**
     * @since 1.1.3
     * @return
     */
    public String getCreativeTab() {
        ItemGroup g = base.getItem().getGroup();
        if (g != null)
            return g.getName();
        else
            return null;
    }
    
    /**
     * @return
     */
    public String getItemID() {
        return Registry.ITEM.getId(base.getItem()).toString();
    }
    
    /**
     * @return
     */
    public boolean isEmpty() {
        return base.isEmpty();
    }
    
    public String toString() {
        return String.format("ItemStack:{\"id\":\"%s\", \"damage\": %d, \"count\": %d}", this.getItemID(), base.getDamage(), base.getCount());
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param ish
     * @return
     */
    public boolean equals(ItemStackHelper ish) {
        return base.equals(ish.getRaw());
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param is
     * @return
     */
    public boolean equals(ItemStack is) {
        return base.equals(is);
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param ish
     * @return
     */
    public boolean isItemEqual(ItemStackHelper ish) {
        return base.isItemEqual(ish.getRaw()) && base.getDamage() == ish.getRaw().getDamage();
    } 
    
    /**
     * @since 1.1.3 [citation needed]
     * @param is
     * @return
     */
    public boolean isItemEqual(ItemStack is) {
        return base.isItemEqual(is) && base.getDamage() == is.getDamage();
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param ish
     * @return
     */
    public boolean isItemEqualIgnoreDamage(ItemStackHelper ish) {
        return base.isItemEqualIgnoreDamage(ish.getRaw());
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param is
     * @return
     */
    public boolean isItemEqualIgnoreDamage(ItemStack is) {
        return base.isItemEqualIgnoreDamage(is);
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param ish
     * @return
     */
    public boolean isNBTEqual(ItemStackHelper ish) {
        return ItemStack.areTagsEqual(base, ish.getRaw());
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param is
     * @return
     */
    public boolean isNBTEqual(ItemStack is) {
        return ItemStack.areTagsEqual(base, is);
    }
    
    /**
     * @since 1.2.0
     * @return
     */
    public ItemStackHelper copy() {
        return new ItemStackHelper(base.copy());
    }
}
