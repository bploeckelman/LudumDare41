package lando.systems.ld41.utils;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld41.LudumDare41;

/**
 * Created by Brian on 4/21/2018.
 */
public class TankAssets {
    public TextureRegion body;
    public TextureRegion turret;

    public Animation<TextureRegion> leftTreads;
    public Animation<TextureRegion> rightTreads;

    public static TankAssets getTankAssets(String tankName) {
        Assets gameAssets = LudumDare41.game.assets;

        TankAssets assets = new TankAssets();
        assets.body = gameAssets.tanks.get(tankName);
        assets.turret = gameAssets.tanks.get(tankName + "turret");

        assets.leftTreads = gameAssets.tankAnimations.get("lefttread");
        assets.rightTreads = gameAssets.tankAnimations.get("righttread");

        return assets;
    }
}