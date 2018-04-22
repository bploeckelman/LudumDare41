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
import lando.systems.ld41.gameobjects.Level;
import lando.systems.ld41.gameobjects.Tank;
import lando.systems.ld41.particles.ParticleSystem;
import lando.systems.ld41.ui.PowerMeter;
import lando.systems.ld41.utils.Config;
import lando.systems.ld41.utils.accessors.CameraAccessor;
import lando.systems.ld41.gameobjects.Catapult;


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
    public Catapult catapult2;

    private Array<GameObject> gameObjects = new Array<GameObject>();

    public GameScreen() {
        level = new Level(this, "maps/test.tmx");
        Gdx.input.setInputProcessor(this);

        addPlayer();

        gameObjects.add(new Tank(this, "greentank", "green", 60, 60, new Vector2(200, 100)));
        gameObjects.add(new Tank(this, "greentank", "", 60, 60, new Vector2(300, 100)));
        gameObjects.add(new Tank(this, "browntank", "green", 60, 60, new Vector2(400, 100)));
        gameObjects.add(new Tank(this, "browntank", "greenpontoon", 60, 60, new Vector2(500, 100)));

        showPowerMeter = false;
        particleSystem = new ParticleSystem();
        powerMeter = new PowerMeter(1.5f, new Vector2(Gdx.graphics.getWidth() - 60, Gdx.graphics.getHeight() - 110));
        worldCamera.position.set(playerTank.position, 0);
        worldCamera.update();

        enterLevelZoom();
        catapult1 = new Catapult(this, playerTank, 20, 20, new Vector2(900, 100));
        catapult1.init(playerTank);
        catapult2 = new Catapult(this, playerTank,20, 20, new Vector2(900, 500));
        catapult2.init(playerTank);
    }

    private void addPlayer() {
        float rando = MathUtils.random();

        String body = (rando > 0.5) ? "browntank" : "greentank";
        String treads = "";
        if (rando > 0.6) {
            treads = "green";
        } else if (rando > 0.3) {
            treads = "greenpontoon";
        }

        System.out.println("body: " + body + " treads: " + treads);

        playerTank = new Tank(this, body, treads);


        gameObjects.add(playerTank);
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
        {
            game.setScreen(new TitleScreen());
        }

        if (showPowerMeter) {
            powerMeter.update(dt);
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
            catapult1.render(batch);
            catapult2.render(batch);
            for (GameObject gameObj : gameObjects) {
                gameObj.render(batch);
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


}
