package lando.systems.ld41.stats;

import lando.systems.ld41.LudumDare41;

/**
 * Created by Brian on 4/22/2018.
 */
public class GameStats {
    public HoleStats[] gameStats;

    public GameStats() {
        gameStats = new HoleStats[LudumDare41.game.assets.levelNumberToFileNameMap.size];
        for (int i = 0; i < gameStats.length; i++) {
            gameStats[i] = new HoleStats();
        }
    }

    public void addStats(int level, float distance, int kills, int score, boolean isDead, double time) {
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
        stats.timeMs += time;
    }

    public HoleStats getLevelStats(int level) {
        return gameStats[level];
    }

    public int totalScore() {
        int total = 0;
        for (int i = 0; i < gameStats.length; i++) {
            total += gameStats[i].score;
        }
        return total;
    }

    public String totalTime() {
        double total = 0;
        for (int i = 0; i < gameStats.length; i++) {
            total += gameStats[i].timeMs;
        }
        return toTimeString((long) total / 1000);
    }

    public static String toTimeString(long totalSeconds) {
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return (minutes > 0)
                ? minutes + "m " + seconds + "s"
                : seconds + "s";
    }


    public int totalDeaths() {
        int total = 0;
        for (int i = 0; i < gameStats.length; i++) {
            total += gameStats[i].deaths;
        }
        return total;
    }


    public int totalKills() {
        int total = 0;
        for (int i = 0; i < gameStats.length; i++) {
            total += gameStats[i].kills;
        }
        return total;
    }
}
