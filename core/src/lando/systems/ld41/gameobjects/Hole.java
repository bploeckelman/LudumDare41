package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.LudumDare41;

public class Hole extends GameObject {

    public Vector2 pos;

    private TextureRegion texture;

    public Hole(float x, float y) {
        this.pos = new Vector2(x, y);
        this.texture = LudumDare41.game.assets.hole;
    }

    @Override
    public void update(float dt) {}

    @Override
    public void render(SpriteBatch batch) {
        batch.setColor(Color.ORANGE);
        batch.draw(texture, pos.x, pos.y);
        batch.setColor(Color.WHITE);
    }
}
