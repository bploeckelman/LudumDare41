package lando.systems.ld41.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.screens.GameScreen;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

public class BallIndicatorArrow {
    public Vector2 position;
    public GameScreen screen;
    public float rotation;

    public BallIndicatorArrow(GameScreen screen) {
        position = new Vector2();
        this.screen = screen;
    }

    public void update(float dt)
    {
        if (screen.playerTank.ball.isInWorldView()) {return;}
        Vector2 vec = new Vector2(screen.playerTank.ball.position);
        Vector2 magVec = vec.sub(screen.worldCamera.position.x, screen.worldCamera.position.y);
        rotation = (float)(Math.atan2(
                screen.playerTank.ball.position.y - screen.playerTank.position.y,
                screen.playerTank.ball.position.x - screen.playerTank.position.x) * 180 / Math.PI) - 90;
        
        position = magVec.nor().scl(screen.hudCamera.viewportWidth / 2 - 30f , screen.hudCamera.viewportHeight / 2 - 30f );
        position.add(screen.hudCamera.viewportWidth / 2, screen.hudCamera.viewportHeight / 2);
    }

    public void render(SpriteBatch batch)
    {
        if (!screen.playerTank.ball.isInWorldView())
        {
            Texture tex = LudumDare41.game.assets.testTexture.getTexture();
            batch.draw(LudumDare41.game.assets.testTexture, position.x, position.y, 15, 15 , tex.getWidth(), tex.getHeight(), .1f, .1f, rotation);
        }

    }
}
