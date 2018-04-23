package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.LudumDare41;

public class FinalBoss extends GameObject {

    private float width;
    private float height;
    private TextureRegion texture;

    public FinalBoss(float x, float y) {
        this.position = new Vector2(x, y);
        this.width = 128f;
        this.height = 128f;
        this.texture = LudumDare41.game.assets.testTexture;
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y, width, height);
    }

}
