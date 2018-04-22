package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.utils.Utils;

public class PinballBumper extends GameObject {

    public Vector2 position;
    public float radius;
    private float resetDelay;
    private float resetTimer;
    public boolean isOn;
    private Interpolation interp;
    private TextureRegion texOff;
    private TextureRegion texOn;

    public PinballBumper(float x, float y) {
        this.radius = LudumDare41.game.assets.pinballBumperOff.getRegionWidth() / 2f;
        this.position = new Vector2(x + radius, y + radius);
        this.resetDelay = 0.5f;
        this.resetTimer = 0f;
        this.isOn = false;
        this.interp = Interpolation.elasticOut;
        this.texOff = LudumDare41.game.assets.pinballBumperOff;
        this.texOn = LudumDare41.game.assets.pinballBumperOn;
    }

    public boolean checkForHit(Ball ball) {
        if (ball.onTank) return false;
        if (Utils.doCirclesIntersect(position, radius, ball.position, ball.radius)) {
            isOn = true;
            return true;
        }
        return false;
    }

    public void checkForHit(Tank tank) {
        if (Utils.doCirclesIntersect(position, radius, tank.position, tank.radius)) {
            isOn = true;
        }
    }

    @Override
    public void update(float dt) {
        if (isOn) {
            resetTimer += dt;
            if (resetTimer > resetDelay) {
                resetTimer = 0f;
                isOn = false;
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        float radiusScale = 1f;
        radiusScale += 0.25f * interp.apply(resetTimer / resetDelay);
        batch.draw(isOn ? texOn : texOff,
                   position.x - radius * radiusScale,
                   position.y - radius * radiusScale,
                   radius  * 2f * radiusScale,
                   radius * 2f * radiusScale);
    }

}
