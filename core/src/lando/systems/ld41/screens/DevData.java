package lando.systems.ld41.screens;

import com.badlogic.gdx.math.MathUtils;

/**
 * Created by Brian on 4/23/2018.
 */
public class DevData {


    public static DevData[] getDevData(int count, int holes) {
        DevData[] data = new DevData[count];
        for (int i = 0; i < count; i++) {
            data[i] = new DevData(getRandoDev(data), holes);
        }
        return data;
    }

    private static String getRandoDev(DevData[] data) {
        String[] devs = new String[] { "Brian R", "Brian P", "Doug", "Jeffery", "Luke", "Troy", "Matt" };

        String devName = "";
        do {
            devName = devs[MathUtils.random(devs.length - 1)];
        } while (isDupName(devName, data));
        return devName;
    }

    private static boolean isDupName(String name, DevData[] data) {
        for (DevData dd : data) {
            if (dd != null && dd.name == name) { return true; }
        }
        return false;
    }

    public String[] scores;
    public String total;
    public String name;

    public DevData(String name, int count) {
        this.name = name;
        int minScore = 1;
        int maxScore = 3;
        if (name == "Brian R") {
            maxScore = 1;
        } else if (name == "Doug") {
            minScore = 10;
            maxScore = 20;
        }


        scores = new String[count];
        int totalScore = 0;
        for (int i = 0; i < count; i++) {
            int score = MathUtils.random(minScore, maxScore);
            scores[i] = Integer.toString(score);
            totalScore += score;
        }
        total = Integer.toString(totalScore);

    }
}
