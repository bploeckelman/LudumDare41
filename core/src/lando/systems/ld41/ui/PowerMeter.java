package lando.systems.ld41.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.utils.Assets;

public class PowerMeter {
    public float power;
    public Vector2 position;
    public Interpolation interp;
    public float elapsed = 0f;
    public float lifeTime;
    public int maxPower = 100;
    public boolean isGoingUp = true;
    public TextureRegion textureRegion;
    public Texture texture;
    public ShapeRenderer shapeRenderer;

    public PowerMeter(float lifeTime, Vector2 position)
    {
        interp = Interpolation.pow2In;
        this.lifeTime = lifeTime;
        this.position = position;
        textureRegion = LudumDare41.game.assets.testTexture;
        shapeRenderer = new ShapeRenderer();
        texture = textureRegion.getTexture();
    }

    public void render(SpriteBatch batch)
    {
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(position.x, position.y, 50, power);
        shapeRenderer.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.rect(position.x, position.y, 50, 100);
        shapeRenderer.end();
        batch.begin();
    }

    public void update(float dt)
    {
        float progress = 0f;
        if (this.isGoingUp)
        {
            elapsed += dt;
            progress = Math.min(1f, elapsed/lifeTime);
        }
        else
        {
            elapsed -= dt;
            progress = Math.max(0f, elapsed/lifeTime);
        }

        this.power = maxPower * interp.apply(progress);
        if(this.power == this.maxPower && this.isGoingUp)
        {
            this.isGoingUp = false;
        }

        if(this.power == 0 && !this.isGoingUp)
        {
            this.isGoingUp = true;
        }
    }

    public void reset()
    {
        this.power = 0;
        this.elapsed = 0;
        this.isGoingUp = true;
    }
}
