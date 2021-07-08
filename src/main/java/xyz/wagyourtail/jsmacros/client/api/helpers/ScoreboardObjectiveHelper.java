package xyz.wagyourtail.jsmacros.client.api.helpers;

import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import xyz.wagyourtail.jsmacros.core.helpers.BaseHelper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Wagyourtail
 * @since 1.2.9
 */
@SuppressWarnings("unused")
public class ScoreboardObjectiveHelper extends BaseHelper<ScoreObjective> {
    
    public ScoreboardObjectiveHelper(ScoreObjective o) {
        super(o);
    }
    
    /**
     * @return player name to score map
     */
    public Map<String, Integer> getPlayerScores() {
        Map<String, Integer> scores  = new LinkedHashMap<>();
        for (Score pl : base.getScoreboard().getSortedScores(base)) {
            scores.put(pl.getPlayerName(), pl.getScorePoints());
        }
        return scores;
    }
    
    /**
     * @return name of scoreboard
     * @since 1.2.9
     */
    public String getName() {
        return base.getName();
    }
    
    /**
     * @return name of scoreboard
     * @since 1.2.9
     */
    public TextHelper getDisplayName() {
        return new TextHelper(base.getDisplayName());
    }
}
