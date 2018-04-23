package lando.systems.ld41.screens;

import com.badlogic.gdx.math.MathUtils;

/**
 * Created by Brian on 4/23/2018.
 */
public class DevData {


    public static DevData[] getDevData(int count, int holes) {
        String[] devs = new String[] { "Brian", "Doug", "Jeffery", "Luke", "Troy", "Matt" };
        DevData[] data = new DevData[count];
        for (int i = 0; i < count; i++) {
            data[i] = new DevData(devs[MathUtils.random(devs.length - 1)], holes);
        }
        return data;
    }

    public String[] scores;
    public String total;
    public String name;

    public DevData(String name, int count) {
        this.name = name;
        scores = new String[count];
        int totalScore = 0;
        for (int i = 0; i < count; i++) {
            int score = MathUtils.random(1, 4);
            scores[i] = Integer.toString(score);
            totalScore += score;
        }
        total = Integer.toString(totalScore);

    }
}
