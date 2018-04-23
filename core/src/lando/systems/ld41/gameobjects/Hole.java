package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.screens.GameScreen;

public class Hole extends GameObject {

    public float width;
    private float height;
    private TextureRegion texture;
    private Vector3 tempVector3;

    private Indicator indicator;

    public Hole(float x, float y) {
        this.position = new Vector2(x + width/2, y + height/2);
        this.texture = LudumDare41.game.assets.hole;
        this.width = texture.getRegionWidth();
        this.height = texture.getRegionHeight();
        this.tempVector3 = new Vector3();

        indicator = new Indicator(this, 15);
    }

    public boolean isInside(Ball ball) {
        float dx = position.x - ball.position.x;
        float dy = position.y - ball.position.y;
        float d2 = dx*dx + dy*dy;
        return (d2 < (width * width / 4f)+ (ball.radius * ball.radius));
    }

    public boolean isInWorldView(GameScreen screen)
    {
        tempVector3.set(position.x, position.y, 0);
        screen.worldCamera.project(tempVector3);

        return ((tempVector3.x > 0 && tempVector3.x < screen.hudCamera.viewportWidth && tempVector3.y > 0 && tempVector3.y < screen.hudCamera.viewportHeight));
    }

    @Override
    public void update(float dt) {
        if (!screen.levelZoomDone)
        {
           indicator.update(dt);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.setColor(Color.ORANGE);
        batch.draw(texture, position.x - width/2, position.y - height/2);
        batch.setColor(Color.WHITE);

        if (!screen.levelZoomDone)
        {
            indicator.render(batch);
        }
    }
}
