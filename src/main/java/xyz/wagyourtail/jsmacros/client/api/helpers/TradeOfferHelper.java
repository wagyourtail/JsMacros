package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;
import xyz.wagyourtail.jsmacros.client.api.classes.VillagerInventory;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class TradeOfferHelper extends BaseHelper<MerchantRecipe> {
    private final VillagerInventory inv;
    private final int index;
    public TradeOfferHelper(MerchantRecipe base, int index, VillagerInventory inv) {
        super(base);
        this.inv = inv;
        this.index = index;
    }
    
    /**
     * @return list of input items required
     */
    public List<ItemStackHelper> getInput() {
        List<ItemStackHelper> items = new ArrayList<>();
        ItemStack first = base.getItemToBuy();
        if (first != null) items.add(new ItemStackHelper(first));
        ItemStack second = base.getSecondItemToBuy();
        if (second != null) items.add(new ItemStackHelper(second));
        return items;
    }
    
    /**
     * @return output item that will be recieved
     */
    public ItemStackHelper getOutput() {
        return new ItemStackHelper(base.getItemToSell());
    }
    
    /**
     * select trade offer on screen
     */
    public void select() {
        if (inv != null && Minecraft.getMinecraft().currentScreen == inv.getRawContainer())
            inv.selectTrade(index);
    }
    
    /**
     * @return
     */
    public boolean isAvailable() {
        return !base.isRecipeDisabled();
    }
    
    /**
     * @return trade offer as nbt tag
     */
    public String getNBT() {
        return base.writeToTags().toString();
    }
    
    /**
     * @return current number of uses
     */
    public int getUses() {
        return base.getToolUses();
    }
    
    /**
     * @return max uses before it locks
     */
    public int getMaxUses() {
        return base.getMaxTradeUses();
    }
    
    /**
     * @return experience gained for trade
     */
    public int getExperience() {
        return 0;
    }
    
    /**
     * @return current price adjustment, negative is discount.
     */
    public int getCurrentPriceAdjustment() {
        return 0;
    }
    
    public String toString() {
        return String.format("TradeOffer:{\"inputs\":%s, \"output\":%s}", getInput().toString(), getOutput().toString());
    }
}
