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
    public TextureRegion turretRecoil;
    public TextureRegion dead;
    public TextureRegion deadTurret;
    public Animation<TextureRegion> smoke;
    public Animation<TextureRegion> forceShield;

    public Animation<TextureRegion> leftTreads;
    public float leftLoopTime;
    public Animation<TextureRegion> rightTreads;
    public float rightLoopTime;

    public static TankAssets getTankAssets(String tankName) {
        return getTankAssets(tankName, "brown");
    }

    public static TankAssets getTankAssets(String tankName, String treadType) {
        Assets gameAssets = LudumDare41.game.assets;

        TankAssets assets = new TankAssets();
        assets.body = gameAssets.tanks.get(tankName);
        assets.turret = gameAssets.tanks.get(tankName + "turret");
        assets.turretRecoil = gameAssets.tanks.get(tankName + "turretrecoil");
        assets.dead = gameAssets.tanks.get(tankName + "broken");
        assets.deadTurret = gameAssets.tanks.get(tankName + "turretbroken");
        assets.smoke = gameAssets.tankAnimations.get("smoke");
        assets.forceShield = gameAssets.tankAnimations.get("forceshield");

        assets.leftTreads = gameAssets.tankAnimations.get(treadType + "lefttread");
        assets.rightTreads = gameAssets.tankAnimations.get(treadType + "righttread");

        assets.leftLoopTime = assets.leftTreads.getAnimationDuration();
        assets.rightLoopTime = assets.rightTreads.getAnimationDuration();

        return assets;
    }
}
