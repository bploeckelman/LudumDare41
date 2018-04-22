package lando.systems.ld41.stats;

import com.badlogic.gdx.utils.Array;
import lando.systems.ld41.utils.Config;


/**
 * Created by Brian on 4/22/2018.
 */
public class GameStats {
    public Array<HoleStats> gameStats = new Array<HoleStats>(Config.HOLES);

    public void setStats(int level, HoleStats stats) {
        gameStats.insert(level, stats);
    }
}
