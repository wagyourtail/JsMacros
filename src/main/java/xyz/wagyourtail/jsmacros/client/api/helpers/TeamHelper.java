package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.scoreboard.Team;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wagyourtail
 * @since 1.3.0
 */
@SuppressWarnings("unused")
public class TeamHelper extends BaseHelper<Team> {
    public TeamHelper(Team t) {
        super(t);
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public String getName() {
        return base.getRegisteredName();
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public TextHelper getDisplayName() {
        return new TextHelper(base.getRegisteredName());
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public List<String> getPlayerList() {
        return new ArrayList<>(base.getMembershipCollection());
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public int getColor() {
        return -1;
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public TextHelper getPrefix() {
        return null;
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public TextHelper getSuffix() {
        return null;
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public String getCollisionRule() {
        return null;
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public boolean isFriendlyFire() {
        return base.getAllowFriendlyFire();
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public boolean showFriendlyInvisibles() {
        return base.getSeeFriendlyInvisiblesEnabled();
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public String nametagVisibility() {
        return base.getNameTagVisibility().toString();
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public String deathMessageVisibility() {
        return base.getDeathMessageVisibility().toString();
    }
    
    public String toString() {
        return String.format("Team:{\"name\":\"%s\"}", getDisplayName().toString());
    }
}
