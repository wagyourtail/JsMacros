package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.village.MerchantRecipe;
import xyz.wagyourtail.jsmacros.client.access.IMerchantScreen;
import xyz.wagyourtail.jsmacros.client.api.helpers.TradeOfferHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * @since 1.3.1
 */
@SuppressWarnings("unused")
public class VillagerInventory extends Inventory<GuiMerchant> {
    
    protected VillagerInventory(GuiMerchant inventory) {
        super(inventory);
    }
    
    /**
    *  select the trade by it's index
    *
     * @param index
     *
     * @return self for chaining
     *
     * @since 1.3.1
     */
    public VillagerInventory selectTrade(int index) {
        ((IMerchantScreen)inventory).jsmacros_selectIndex(index);
        return this;
    }
    
    /**
     * @return
     * @since 1.3.1
     */
    public int getExperience() {
        return 0;
    }
    
    /**
     * @return
     * @since 1.3.1
     */
    public int getLevelProgress() {
        return 0;
    }
    
    /**
     * @return
     * @since 1.3.1
     */
    public int getMerchantRewardedExperience() {
        return 0;
    }
    
    /**
     * @return
     * @since 1.3.1
     */
    public boolean canRefreshTrades() {
        return false;
    }
    
    /**
     * @return
     * @since 1.3.1
     */
    public boolean isLeveled() {
        return false;
    }
    
    /**
     * @return list of trade offers
     * @since 1.3.1
     */
    public List<TradeOfferHelper> getTrades() {
        List<TradeOfferHelper> offers = new LinkedList<>();
        int i = -1;
        for (MerchantRecipe offer : inventory.getMerchant().getRecipes(mc.thePlayer)) {
            offers.add(new TradeOfferHelper(offer, ++i, this));
        }
        return offers;
    }
}
