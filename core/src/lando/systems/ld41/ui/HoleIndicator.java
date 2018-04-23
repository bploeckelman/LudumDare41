package lando.systems.ld41.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.gameobjects.Hole;
import lando.systems.ld41.screens.GameScreen;

public class HoleIndicator {
    public Vector2 position;
    public GameScreen screen;
    private Vector2 tempVec;
    private float size;
    private float accum;

    public HoleIndicator(GameScreen screen) {
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
        Hole hole = screen.level.hole;

        if (!hole.isInWorldView(screen))
        {
            size = 35f + MathUtils.sin(accum * 8f) * 5;
            tempVec.set(hole.centerPos);
            tempVec.sub(screen.playerTank.position);
            position.set(tempVec.nor().scl(250));
            position.add(screen.hudCamera.position.x, screen.hudCamera.position.y);
            batch.draw(LudumDare41.game.assets.flag, position.x - size/2f, position.y-size/2f, size/2f, size/2f , size, size, 1f, 1f, 0);
        }

    }
}
