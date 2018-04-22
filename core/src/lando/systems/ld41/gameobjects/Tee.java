package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.LudumDare41;

public class Tee extends GameObject {

    public Vector2 pos;
    public int facing;

    private final float size = 16f;
    private TextureRegion texture;

    public Tee(float x, float y, int facing) {
        this.pos = new Vector2(x, y);
        this.facing = facing;
        this.texture = LudumDare41.game.assets.whiteCircle;
    }

    @Override
    public void update(float dt) {}

    @Override
    public void render(SpriteBatch batch) {
        batch.setColor(Color.RED);
        batch.draw(texture, pos.x - size / 2f, pos.y - size / 2f, size, size);
        batch.setColor(Color.WHITE);
    }

}
