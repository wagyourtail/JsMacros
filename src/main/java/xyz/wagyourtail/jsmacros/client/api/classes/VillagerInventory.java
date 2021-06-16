package xyz.wagyourtail.jsmacros.client.api.classes;

import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.village.TradeOffer;
import xyz.wagyourtail.jsmacros.client.access.IMerchantScreen;
import xyz.wagyourtail.jsmacros.client.api.helpers.TradeOfferHelper;

import java.util.LinkedList;
import java.util.List;

/**
 * @since 1.3.1
 */
@SuppressWarnings("unused")
public class VillagerInventory extends Inventory<MerchantScreen> {
    
    protected VillagerInventory(MerchantScreen inventory) {
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
        return inventory.getContainer().getExperience();
    }
    
    /**
     * @return
     * @since 1.3.1
     */
    public int getLevelProgress() {
        return inventory.getContainer().getLevelProgress();
    }
    
    /**
     * @return
     * @since 1.3.1
     */
    public int getMerchantRewardedExperience() {
        return inventory.getContainer().getTraderRewardedExperience();
    }
    
    /**
     * @return
     * @since 1.3.1
     */
    public boolean canRefreshTrades() {
        return inventory.getContainer().canRefreshTrades();
    }
    
    /**
     * @return
     * @since 1.3.1
     */
    public boolean isLeveled() {
        return inventory.getContainer().isLevelled();
    }
    
    /**
     * @return list of trade offers
     * @since 1.3.1
     */
    public List<TradeOfferHelper> getTrades() {
        List<TradeOfferHelper> offers = new LinkedList<>();
        int i = -1;
        for (TradeOffer offer : inventory.getContainer().getRecipes()) {
            offers.add(new TradeOfferHelper(offer, ++i, this));
        }
        return offers;
    }
}
