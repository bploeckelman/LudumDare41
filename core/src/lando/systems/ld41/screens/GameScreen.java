package lando.systems.ld41.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld41.gameobjects.Tank;
import lando.systems.ld41.utils.Assets;

/**
 * Created by Brian Ploeckelman <brian.ploeckelman@wisc.edu> on 4/13/18.
 */
public class GameScreen extends BaseScreen {

    public Tank playerTank;

    public GameScreen() {
        Gdx.input.setInputProcessor(this);
        playerTank = new Tank();
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.screen = new TitleScreen();
        }

        playerTank.update(dt);

        updateCamera();
    }

    @Override
    public void render(SpriteBatch batch) {
        renderGame(batch);
        renderUI(batch);
    }

    private void renderGame(SpriteBatch batch) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(worldCamera.combined);
        batch.begin();
        {
            batch.setColor(Color.WHITE);
            playerTank.render(batch);

        }
        batch.end();
    }

    private void renderUI(SpriteBatch batch) {
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            batch.setColor(Color.WHITE);
//            Assets.drawString(batch, "Game Screen", 10f, hudCamera.viewportHeight - 20f, Color.CORAL, 1.25f, game.assets.font);
        }
        batch.end();
    }

}
