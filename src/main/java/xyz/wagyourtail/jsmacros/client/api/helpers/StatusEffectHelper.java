package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.registry.GameData;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * @author Wagyourtail
 * @since 1.2.4
 */
@SuppressWarnings("unused")
public class StatusEffectHelper extends BaseHelper<PotionEffect> {
    
    public StatusEffectHelper(PotionEffect s) {
        super(s);
    }
    
    /**
     * @since 1.2.4
     * @return
     */
    public String getId() {
        return GameData.getPotionRegistry().getNameForObject(Potion.potionTypes[base.getPotionID()]).toString();
    }
    
    /**
     * @since 1.2.4
     * @return
     */
    public int getStrength() {
        return base.getAmplifier();
    }
    
    /**
     * @since 1.2.4
     * @return
     */
    public int getTime() {
        return base.getDuration();
    }
}
