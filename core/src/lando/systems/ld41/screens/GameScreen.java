package lando.systems.ld41.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld41.gameobjects.Level;
import lando.systems.ld41.gameobjects.Tank;

/**
 * Created by Brian Ploeckelman <brian.ploeckelman@wisc.edu> on 4/13/18.
 */
public class GameScreen extends BaseScreen {

    public Tank playerTank;
    public Level level;

    public GameScreen() {
        Gdx.input.setInputProcessor(this);
        playerTank = new Tank();
        level = new Level("maps/test.tmx");
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.screen = new TitleScreen();
        }

        playerTank.update(dt);

        // TODO: remove when proper camera controls are added
        float moveSpeed = 200f * dt;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT))  worldCamera.translate(-moveSpeed, 0f);
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) worldCamera.translate( moveSpeed, 0f);
        if (Gdx.input.isKeyPressed(Input.Keys.UP))    worldCamera.translate(0f,  moveSpeed);
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))  worldCamera.translate(0f, -moveSpeed);
        worldCamera.update();
//        updateCamera();
    }

    @Override
    public void render(SpriteBatch batch) {
        renderGame(batch);
        renderUI(batch);
    }

    private void renderGame(SpriteBatch batch) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        level.render(batch, worldCamera);

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
