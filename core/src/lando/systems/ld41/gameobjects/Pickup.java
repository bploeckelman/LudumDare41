package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.screens.GameScreen;
import lando.systems.ld41.utils.Assets;

/**
 * Created by Brian on 4/23/2018.
 */
public class Pickup extends GameObject {

    public enum PickupType { shield, pontoon, camo, invincible };

    public boolean visible = true;

    private TextureRegion image;
    private float accum = 0;

    private PickupType type;

    public Pickup(GameScreen screen, PickupType type, float x, float y) {
        this.screen = screen;
        this.type = type;
        position = new Vector2(x, y);
        radius = 16;

        Assets assets = LudumDare41.game.assets;

        switch (type) {
            case shield:
                image = assets.puShield;
                break;
            case pontoon:
                image = assets.puPontoon;
                break;
            case camo:
                image = assets.puCamo;
                break;
            case invincible:
                image = assets.puInvincible;
                break;
            default:
                throw new GdxRuntimeException("invalid pickup type: " + type);
        }
    }

    @Override
    public void update(float dt)
    {
        accum += dt;
    }


    @Override
    public void render(SpriteBatch batch) {
        if (!visible) return;

        float scale = 0.825f + MathUtils.sin(accum * 4f) * 0.125f;
        batch.draw(image, position.x - 16, position.y - 16, 16, 16, 32, 32, scale, scale, 0);
    }

    public void checkCollision(Tank tank){
        if (position.dst(tank.position) < radius + tank.radius){
            visible = false;
            tank.pickup(type);
        }
    }
}
