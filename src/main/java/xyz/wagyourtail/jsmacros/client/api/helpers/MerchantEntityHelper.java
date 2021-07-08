package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.village.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class MerchantEntityHelper extends LivingEntityHelper<EntityVillager> {
    
    public MerchantEntityHelper(EntityVillager e) {
        super(e);
    }
    
    /**
     * @return
     */
    public List<TradeOfferHelper> getTrades() {
        List<TradeOfferHelper> offers = new ArrayList<>();
        for (MerchantRecipe offer : base.getRecipes(Minecraft.getMinecraft().thePlayer)) {
            offers.add(new TradeOfferHelper(offer, 0, null));
        }
        return offers;
    }
    
    /**
     * @return
     */
    public int getExperience() {
        return 0;
    }
    
    /**
     * @return
     */
    public boolean hasCustomer() {
        return base.getCustomer() != null;
    }
}
