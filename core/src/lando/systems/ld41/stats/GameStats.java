package lando.systems.ld41.stats;

import com.badlogic.gdx.utils.Array;
import lando.systems.ld41.gameobjects.Tank;
import lando.systems.ld41.utils.Config;


/**
 * Created by Brian on 4/22/2018.
 */
public class GameStats {
    public HoleStats[] gameStats;

    public GameStats() {
        gameStats = new HoleStats[Config.HOLES];
        for (int i = 0; i < gameStats.length; i++) {
            gameStats[i] = new HoleStats();
        }
    }

    public void addStats(int level, float distance, int kills, int score, boolean isDead) {
        HoleStats stats = gameStats[level];

        // score and deaths will be displayed, so they need to stay with player - these values are current totals,
        // so don't add - just set

        if (isDead) {
            stats.deaths++;
            // 2 stroke penalty for death
            score += 2;
        }
        stats.score = score;

        stats.distance += distance;
        stats.kills += kills;
    }

    public HoleStats getLevelStats(int level) {
        return gameStats[level];
    }
}
