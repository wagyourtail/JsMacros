package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
        base.setItemDamage(damage);
        return this;
    }
    
    /**
     * @since 1.2.0
     * @return
     */
    public boolean isDamageable() {
        return base.isItemStackDamageable();
    }
    
    /**
     * @since 1.2.0
     * @return
     */
    public boolean isEnchantable() {
        return base.isItemEnchantable();
    }
    
    /**
     * @return
     */
    public int getDamage() {
        return base.getItemDamage();
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
        return base.getItem().getItemStackDisplayName(base);
    }
    
    /**
     * @return
     */
    public String getName() {
        return base.getDisplayName();
    }
    
    /**
     * @return
     */
    public int getCount() {
        return base.stackSize;
    }
    
    /**
     * @return
     */
    public int getMaxCount() {
        return base.getMaxStackSize();
    }
    
    /**
     * @since 1.1.6
     * @return
     */
    public String getNBT() {
        NBTTagCompound tag = base.getTagCompound();
        if (tag != null) return tag.toString();
        else return "{}";
    }
    
    /**
     * @since 1.1.3
     * @return
     */
    public String getCreativeTab() {
        CreativeTabs g = base.getItem().getCreativeTab();
        if (g != null)
            return g.getTabLabel();
        else
            return null;
    }
    
    /**
     * @return
     */
    public String getItemID() {
        return Item.itemRegistry.getNameForObject(base.getItem()).toString();
    }
    
    /**
     * @return
     */
    public boolean isEmpty() {
        return base == null || base.stackSize == 0;
    }
    
    public String toString() {
        return String.format("ItemStack:{\"id\":\"%s\", \"damage\": %d, \"count\": %d}", this.getItemID(), base.getItemDamage(), base.stackSize);
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
        return base.isItemEqual(ish.getRaw()) && base.getItemDamage() == ish.getRaw().getItemDamage();
    } 
    
    /**
     * @since 1.1.3 [citation needed]
     * @param is
     * @return
     */
    public boolean isItemEqual(ItemStack is) {
        return base.isItemEqual(is) && base.getItemDamage() == is.getItemDamage();
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param ish
     * @return
     */
    public boolean isItemEqualIgnoreDamage(ItemStackHelper ish) {
        return this.base == ish.base;
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param is
     * @return
     */
    public boolean isItemEqualIgnoreDamage(ItemStack is) {
        return base == is;
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param ish
     * @return
     */
    public boolean isNBTEqual(ItemStackHelper ish) {
        return ItemStack.areItemStackTagsEqual(base, ish.getRaw());
    }
    
    /**
     * @since 1.1.3 [citation needed]
     * @param is
     * @return
     */
    public boolean isNBTEqual(ItemStack is) {
        return ItemStack.areItemStackTagsEqual(base, is);
    }
    
    /**
     * @since 1.2.0
     * @return
     */
    public ItemStackHelper copy() {
        return new ItemStackHelper(base.copy());
    }
}
