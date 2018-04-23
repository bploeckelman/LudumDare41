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
import lando.systems.ld41.gameobjects.Tank;
import lando.systems.ld41.utils.Assets;
import lando.systems.ld41.utils.Utils;

public class PowerMeter {
    float width = 100;
    float height = 20;
    public float power;
    public Interpolation interp;
    public float elapsed = 0f;
    public float lifeTime;
    public int maxPower = 100;
    public boolean isGoingUp = true;
    private Color color;
    private Tank player;

    public PowerMeter(float lifeTime, Tank player)
    {
        interp = Interpolation.pow2In;
        this.lifeTime = lifeTime;
        this.player = player;
        color = new Color();
    }

    public void render(SpriteBatch batch)
    {
        float x = player.position.x - width/2f;
        float y = player.position.y - player.radius - height - 5;
        float n = power / (float) maxPower;
        color = Utils.hsvToRgb(((n * 120f) - 20) / 365f, 1.0f, 1.0f, color);
        batch.setColor(color);
        batch.draw(LudumDare41.game.assets.whitePixel, x, y, width * (power/maxPower), height);
        batch.setColor(Color.WHITE);
        LudumDare41.game.assets.boxNinePatch.draw(batch, x, y, width, height);

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
