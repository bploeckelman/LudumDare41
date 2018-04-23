package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import lando.systems.ld41.LudumDare41;

/**
 * Created by Brian on 4/23/2018.
 */
public class Indicator {
    public float indicatorRadius = 0f;
    public Interpolation indicatorInterp = Interpolation.pow2;
    private float accum = 0;

    private GameObject indicatedObject;
    public float grow;

    public Indicator(GameObject obj, float grow) {
        indicatedObject = obj;
        this.grow = grow;
    }

    public void update(float dt) {
        accum += dt;
        indicatorRadius = grow + grow * indicatorInterp.apply((accum/.5f)%1f);
    }

    public void render(SpriteBatch batch) {
        batch.draw(LudumDare41.game.assets.indicator, indicatedObject.position.x - indicatorRadius, indicatedObject.position.y - indicatorRadius, indicatorRadius * 2f, indicatorRadius * 2f);
    }
}
