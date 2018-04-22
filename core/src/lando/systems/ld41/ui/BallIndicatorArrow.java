package lando.systems.ld41.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.screens.GameScreen;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

public class BallIndicatorArrow {
    public Vector2 position;
    public GameScreen screen;
    public float rotation;
    private Vector2 tempVec;
    private float size;
    private float accum;

    public BallIndicatorArrow(GameScreen screen) {
        position = new Vector2();
        tempVec = new Vector2();
        this.screen = screen;
        size = 30;
        accum = 0;
    }

    public void update(float dt)
    {
        accum += dt;
    }

    public void render(SpriteBatch batch)
    {
        if (!screen.playerTank.ball.isInWorldView())
        {
            size = 40f + MathUtils.sin(accum * 8f) * 10;
            tempVec.set(screen.playerTank.ball.position);
            tempVec.sub(screen.playerTank.position);
            rotation = tempVec.angle();
            position.set(tempVec.nor().scl(100));
            position.add(screen.hudCamera.position.x, screen.hudCamera.position.y);
            batch.draw(LudumDare41.game.assets.arrow, position.x - size/2f, position.y-size/2f, size/2f, size/2f , size, size, 1f, 1f, rotation);
        }

    }
}
