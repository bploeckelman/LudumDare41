package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.screens.GameScreen;

public class Powerup extends GameObject {

    public enum PowerupType {
        SAND_TREADS,
        SHIELD,
        TOUCH_DAMAGE,
        WATER_TREADS,
    }

    public Vector2 position;
    public int width = 20;
    public int height = 20;
    public final PowerupType powerupType;
    private final TextureRegion textureRegion;
    private final GameScreen gameScreen;

    public Powerup(GameScreen gameScreen, PowerupType powerupType, Vector2 position) {
        this.position = position;
        this.gameScreen = gameScreen;
        this.powerupType = powerupType;
        switch (powerupType) {
            case SAND_TREADS:
            case SHIELD:
            case TOUCH_DAMAGE:
            case WATER_TREADS:
            default:
                textureRegion = gameScreen.game.assets.whitePixel;
        }
    }

    @Override
    public void update(float dt) {}

    @Override
    public void render(SpriteBatch batch) {
        Color c = batch.getColor();
        batch.setColor(Color.RED);
        batch.draw(textureRegion, position.x - width/2, position.y - height/2, width, height);
    }
}
