package lando.systems.ld41.gameobjects;

import lando.systems.ld41.screens.GameScreen;
import lando.systems.ld41.utils.Assets;

/**
 * Created by Brian on 4/22/2018.
 */
public class PlayerBall extends Ball {

    public float totalDistance;

    public PlayerBall(GameScreen screen) {
        super(screen, Assets.Balls.Orange);
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        totalDistance += (velocity.len() * dt);
    }
}
