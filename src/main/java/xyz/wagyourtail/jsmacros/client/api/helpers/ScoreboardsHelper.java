package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.List;
import java.util.stream.Collectors;

/**
* @since 1.2.9
 * @author Wagyourtail
 */
@SuppressWarnings("unused")
public class ScoreboardsHelper extends BaseHelper<Scoreboard> {
    
    public ScoreboardsHelper(Scoreboard board) {
        super(board);
    }
    
    /**
     * @param index
     * @since 1.2.9
     * @return
     */
    public ScoreboardObjectiveHelper getObjectiveForTeamColorIndex(int index) {
        return null;
    }
    
    /**
    * {@code 0} is tablist, {@code 1} or {@code 3 + getPlayerTeamColorIndex()} is sidebar, {@code 2} should be tab list.
    * therefore max slot number is 18.
     * @param slot
     * @since 1.2.9
     * @return
     */
    public ScoreboardObjectiveHelper getObjectiveSlot(int slot) {
        ScoreObjective obj = null;
        if (slot >= 0) obj = base.getObjectiveInDisplaySlot(slot);
        return obj == null ? null : new ScoreboardObjectiveHelper(obj);
    }
    
    /**
     * @param entity
     * @since 1.2.9
     * @return
     */
    public int getPlayerTeamColorIndex(PlayerEntityHelper<EntityPlayer> entity) {
        return getPlayerTeamColorIndex(entity.getRaw());
    }
    
    /**
     * @since 1.3.0
     * @return
     */
    public List<TeamHelper> getTeams() {
        return base.getTeams().stream().map(TeamHelper::new).collect(Collectors.toList());
    }
    
    /**
     * @param p
     * @since 1.3.0
     * @return
     */
    public TeamHelper getPlayerTeam(PlayerEntityHelper<EntityPlayer> p) {
        return new TeamHelper(getPlayerTeam(p.getRaw()));
    }
    
    /**
     * @param p
     * @since 1.3.0
     * @return
     */
    protected Team getPlayerTeam(EntityPlayer p) {
        return p.getTeam();
    }
    
    /**
     * @param entity
     * @since 1.2.9
     * @return
     */
    protected int getPlayerTeamColorIndex(EntityPlayer entity) {
        return 0;
    }
    
    /**
     * @since 1.2.9
     * @return the {@link ScoreboardObjectiveHelper} for the currently displayed sidebar scoreboard.
     */
    public ScoreboardObjectiveHelper getCurrentScoreboard() {
        Minecraft mc = Minecraft.getMinecraft();
        ScoreboardObjectiveHelper h = getObjectiveSlot(1);
        if (h == null) h = getObjectiveSlot(1);
        return h;
    }
    
    public String toString() {
        return String.format("Scoreboard:{\"current\":%s}", getCurrentScoreboard().toString());
    }
}
