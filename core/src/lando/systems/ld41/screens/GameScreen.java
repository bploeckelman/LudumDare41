package lando.systems.ld41.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld41.utils.Assets;

/**
 * Created by Brian Ploeckelman <brian.ploeckelman@wisc.edu> on 4/13/18.
 */
public class GameScreen extends BaseScreen {

    public GameScreen() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.screen = new TitleScreen();
        }

        updateCamera();
    }

    @Override
    public void render(SpriteBatch batch) {
        renderGame(batch);
        renderUI(batch);
    }

    private void renderGame(SpriteBatch batch) {
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        {
            float radius = 10f;
            batch.setColor(Color.MAGENTA);
            batch.draw(game.assets.whiteCircle, 0 - radius, 0 - radius, 2f * radius, 2f *radius);
            batch.setColor(Color.WHITE);
        }
        batch.end();
    }

    private void renderUI(SpriteBatch batch) {
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            batch.setColor(Color.WHITE);
            Assets.drawString(batch, "Game Screen", 10f, hudCamera.viewportHeight - 20f, Color.CORAL, 1.25f, game.assets.font);
        }
        batch.end();
    }

}
