package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.screens.GameScreen;

public class Hole extends GameObject {

    public Vector2 centerPos = new Vector2();

    public float width;
    private float height;
    private TextureRegion texture;
    private Vector3 tempVector3;

    public Hole(float x, float y) {
        this.position = new Vector2(x, y);
        this.texture = LudumDare41.game.assets.hole;
        this.width = texture.getRegionWidth();
        this.height = texture.getRegionHeight();
        this.tempVector3 = new Vector3();

        centerPos.set(x + width/2, y + height/2);
    }

    public boolean isInside(Ball ball) {
        float dx = centerPos.x - ball.position.x;
        float dy = centerPos.y - ball.position.y;
        float d2 = dx*dx + dy*dy;
        return (d2 < (width * width / 4f)+ (ball.radius * ball.radius));
    }

    public boolean isInWorldView(GameScreen screen)
    {
        tempVector3.set(centerPos.x, centerPos.y, 0);
        screen.worldCamera.project(tempVector3);

        return ((tempVector3.x > 0 && tempVector3.x < screen.hudCamera.viewportWidth && tempVector3.y > 0 && tempVector3.y < screen.hudCamera.viewportHeight));
    }

    @Override
    public void update(float dt) {}

    @Override
    public void render(SpriteBatch batch) {
        batch.setColor(Color.ORANGE);
        batch.draw(texture, position.x, position.y);
        batch.setColor(Color.WHITE);
    }
}
