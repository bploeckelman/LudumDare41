package lando.systems.ld41.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Quad;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.gameobjects.GameObject;
import lando.systems.ld41.gameobjects.EnemyTank;
import lando.systems.ld41.gameobjects.Catapult;
import lando.systems.ld41.gameobjects.Level;
import lando.systems.ld41.gameobjects.Tank;
import lando.systems.ld41.particles.ParticleSystem;
import lando.systems.ld41.ui.PowerMeter;
import lando.systems.ld41.utils.Config;
import lando.systems.ld41.utils.TankAssets;
import lando.systems.ld41.utils.accessors.CameraAccessor;


/**
 * Created by Brian Ploeckelman <brian.ploeckelman@wisc.edu> on 4/13/18.
 */
public class GameScreen extends BaseScreen {

    private static final float CAMERA_ZOOM_MARGIN = 1.2f;

    public Tank playerTank;
    public Level level;
    public PowerMeter powerMeter;
    public boolean showPowerMeter;
    public boolean levelZoomDone;
    public ParticleSystem particleSystem;
    public Catapult catapult1;
    public Array<EnemyTank> enemyTanks = new Array<EnemyTank>();
    public Catapult catapult2;

    private Array<GameObject> gameObjects = new Array<GameObject>();

    public GameScreen() {
        Gdx.input.setInputProcessor(this);

        level = new Level(this, "maps/test.tmx");

        playerTank = new Tank(this, "browntank", "");
        playerTank.position.set(level.tee.pos);
        playerTank.rotation = level.tee.facing;

        catapult1 = new Catapult(this, playerTank, new Vector2(900, 100));
        catapult2 = new Catapult(this, playerTank, new Vector2(300, 500));

        gameObjects.add(playerTank);
        gameObjects.add(catapult1);
        gameObjects.add(catapult2);
  
        showPowerMeter = false;
        enemyTanks.add(new EnemyTank(this, "browntank", 60, 60, new Vector2(400, 100), 200f, 150f));
        particleSystem = new ParticleSystem();
        powerMeter = new PowerMeter(1.5f, new Vector2(Gdx.graphics.getWidth() - 60, Gdx.graphics.getHeight() - 110));
        worldCamera.position.set(playerTank.position, 0);
        worldCamera.update();

        enterLevelZoom();


    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
        {
            game.setScreen(new TitleScreen());
        }

        updateTank();

        if (showPowerMeter) {
            powerMeter.update(dt);
        }

        for (EnemyTank tank : enemyTanks)
        {
            tank.update(dt);
        }
        particleSystem.update(dt);
        catapult1.update(dt);
        catapult2.update(dt);
        for (GameObject gameObj : gameObjects) {
            gameObj.update(dt);
        }

        cameraTargetPos.set(playerTank.position, 0f);
        if (levelZoomDone) {
            updateCamera();
        } else {
            worldCamera.update();
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.setColor(Config.bgColor);
        batch.draw(LudumDare41.game.assets.whitePixel,0,0, hudCamera.viewportWidth, hudCamera.viewportHeight);
        batch.end();

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
            for (GameObject gameObj : gameObjects) {
                gameObj.render(batch);
            }
            for (EnemyTank tank : enemyTanks)
            {
                tank.render(batch);
            }
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
            LudumDare41.game.assets.backplateNinePatch.draw(batch, 0, hudCamera.viewportHeight - 50, hudCamera.viewportWidth, 50);
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
        if (button == 0) {
            if (playerTank.ball.onTank && showPowerMeter) {
                playerTank.shootBall(powerMeter.power);
            }

            showPowerMeter = false;
            powerMeter.reset();
        }
        return true;
    }

    private void enterLevelZoom() {
        levelZoomDone = false;
        cameraTargetPos.set(playerTank.position, 0f);
        float initialZoom = worldCamera.zoom;
        float targetZoom = Math.max(
                level.groundLayer.getWidth()  * level.groundLayer.getTileWidth()  * CAMERA_ZOOM_MARGIN / worldCamera.viewportWidth,
                level.groundLayer.getHeight() * level.groundLayer.getTileHeight() * CAMERA_ZOOM_MARGIN / worldCamera.viewportHeight);
        Timeline.createSequence()
                .pushPause(1.1f)
                .push(Tween.to(worldCamera, CameraAccessor.XYZ, 2f)
                           .target(level.groundLayer.getWidth()  / 2 * level.groundLayer.getTileWidth(),
                                   level.groundLayer.getHeight() / 2 * level.groundLayer.getTileHeight(),
                                   targetZoom)
                           .ease(Quad.INOUT))
                .pushPause(1f)
                .push(Tween.to(worldCamera, CameraAccessor.XYZ, 1f)
                           .target(cameraTargetPos.x, cameraTargetPos.y, initialZoom)
                           .ease(Quad.INOUT)
                           .setCallback(new TweenCallback() {
                               @Override
                               public void onEvent(int i, BaseTween<?> baseTween) {
                                   // pauseGame = false;
                                   levelZoomDone = true;
                               }
                           }))
                .start(LudumDare41.game.tween);
    }


    private String[] tankBodies = new String[] { "browntank", "greentank" };
    private String[] tankTreads = new String[] { "", "green", "greenpontoon" };
    private int bodyIndex = 0;
    private int treadIndex = 0;

    public void updateTank() {
        // update tank look - temp
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            if (++bodyIndex == tankBodies.length) {
                bodyIndex = 0;
            }
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
            if (++treadIndex == tankTreads.length) {
                treadIndex = 0;
            }
        } else {
            return;
        }

        playerTank.setAssets(TankAssets.getTankAssets(tankBodies[bodyIndex], tankTreads[treadIndex]));
    }
}
