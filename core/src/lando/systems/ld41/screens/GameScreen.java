package lando.systems.ld41.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.gameobjects.Level;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.gameobjects.Tank;
import lando.systems.ld41.particles.ParticleSystem;
import lando.systems.ld41.ui.PowerMeter;

/**
 * Created by Brian Ploeckelman <brian.ploeckelman@wisc.edu> on 4/13/18.
 */
public class GameScreen extends BaseScreen {

    public Tank playerTank;
    public Level level;
    public PowerMeter powerMeter;
    public boolean showPowerMeter;
    public ParticleSystem particleSystem;

    public GameScreen() {
        Gdx.input.setInputProcessor(this);
        playerTank = new Tank(this);
        level = new Level("maps/test.tmx");
        showPowerMeter = false;
        particleSystem = new ParticleSystem();
        powerMeter = new PowerMeter(1.5f, new Vector2(Gdx.graphics.getWidth() - 60, Gdx.graphics.getHeight() - 110));
        worldCamera.position.set(playerTank.position, 0);
        worldCamera.update();
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
        {
            game.screen = new TitleScreen();
        }

        if (showPowerMeter) {
            powerMeter.update(dt);
        }

        playerTank.update(dt);
        particleSystem.update(dt);

        cameraTargetPos.set(playerTank.position, 0f);
        updateCamera();
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
            particleSystem.render(batch);
            playerTank.render(batch);
        }
        batch.end();
    }

    public void addTireTrack(float x, float y, float speed)
    {
        particleSystem.addTracks(x, y, speed);
    }

    private void renderUI(SpriteBatch batch) {
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            batch.setColor(Color.WHITE);
            if (showPowerMeter)
            {
                powerMeter.render(batch);
            }
//            Assets.drawString(batch, "Game Screen", 10f, hudCamera.viewportHeight - 20f, Color.CORAL, 1.25f, game.assets.font);
        }
        batch.end();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == 0 && playerTank.ball.onTank){
            showPowerMeter = true;
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == 0){
            if (playerTank.ball.onTank && showPowerMeter){
                playerTank.shootBall(powerMeter.power);
            }

            showPowerMeter = false;
            powerMeter.reset();
        }
        return true;
    }

}
