package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.entity.boss.BossStatus;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

/**
 * @author Wagyourtail
 * @since 1.2.1
 */
@SuppressWarnings("unused")
public class BossBarHelper extends BaseHelper<BossStatus> {

    public BossBarHelper() {
        super(null);
    }
    
    /**
     * @since 1.2.1
     * @return boss bar uuid.
     */
    public String getUUID() {
        return null;
    }
    
    /**
     * @since 1.2.1
     * @return percent of boss bar remaining.
     */
    public float getPercent() {
        return BossStatus.healthScale;
    }
    
    /**
     * @since 1.2.1
     * @return boss bar color.
     */
    public String getColor() {
    
        if (BossStatus.hasColorModifier) {
            return "RAINBOW";
        } else {
            return "NORMAL";
        }
    }
    
    /**
     * @since 1.2.1
     * @return boss bar notch style.
     */
    public String getStyle() {
        return null;
    }
    
    /**
     * @since 1.2.1
     * @return name of boss bar
     */
    public TextHelper getName() {
        return new TextHelper(BossStatus.bossName);
    }
    
    public String toString() {
        return String.format("BossBar:{\"name:\":\"%s\", \"percent\":%f}",BossStatus.bossName, BossStatus.healthScale);
    }
}
