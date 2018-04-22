package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.LudumDare41;

public class Hole extends GameObject {

    public Vector2 pos;

    private final float size = 16f;
    private TextureRegion texture;

    public Hole(float x, float y) {
        this.pos = new Vector2(x, y);
        this.texture = LudumDare41.game.assets.whiteCircle;
    }

    @Override
    public void update(float dt) {}

    @Override
    public void render(SpriteBatch batch) {
        batch.setColor(Color.ORANGE);
        batch.draw(texture, pos.x - size / 2f, pos.y - size / 2f, size, size);
        batch.setColor(Color.WHITE);
    }
}
