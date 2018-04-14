package lando.systems.ld41.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld41.utils.Assets;

public class TitleScreen extends BaseScreen {

    public TitleScreen() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        if (Gdx.input.justTouched()) {
            game.screen = new GameScreen();
        }

        // ...
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            batch.setColor(Color.DARK_GRAY);
            batch.draw(game.assets.whitePixel, hudCamera.viewportWidth / 2f - 150f, hudCamera.viewportHeight / 2f - 150f, 300f, 300f);

            batch.setColor(Color.WHITE);
            batch.draw(game.assets.testTexture, hudCamera.viewportWidth / 2f - 100f, hudCamera.viewportHeight / 2f - 100f, 200f, 200f);

            batch.setColor(Color.WHITE);
            Assets.drawString(batch, "Ludum Dare 41", 10f, hudCamera.viewportHeight - 20f, Color.CORAL, 1.25f, game.assets.font);
            Assets.drawString(batch, "A Game of Jams", 10f, hudCamera.viewportHeight - 20f - 100f, Color.MAROON, 0.5f, game.assets.font);
        }
        batch.end();
    }

}
