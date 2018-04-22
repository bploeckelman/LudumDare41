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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.gameobjects.*;
import lando.systems.ld41.particles.ParticleSystem;
import lando.systems.ld41.ui.BallIndicatorArrow;
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
    public int currentLevelNum;
    public Level level;
    public PowerMeter powerMeter;
    public boolean showPowerMeter;
    public boolean levelZoomDone;
    private boolean levelTransitioning;
    public ParticleSystem particleSystem;
    public Catapult catapult1;
    public Array<EnemyTank> enemyTanks = new Array<EnemyTank>();
    public Catapult catapult2;
    public BallIndicatorArrow ballIndicatorArrow;
    public Array<Catapult> catapults = new Array<Catapult>();
    private Vector2 oldBulletPosition;
    private Vector2 newBulletPosition;
    private Vector2 bulletCollisionPoint;
    private Vector2 normal;

    public static final Array<Bullet> activeBullets = new Array<Bullet>();
    public static final Pool<Bullet> bulletsPool = Pools.get(Bullet.class, 500);


    private Array<GameObject> gameObjects = new Array<GameObject>();

    public GameScreen(int currentLevelNum) {
        Gdx.input.setInputProcessor(this);

        this.levelTransitioning = false;
        this.currentLevelNum = currentLevelNum;
        level = new Level(this, LudumDare41.game.assets.levelNumberToFileNameMap.get(currentLevelNum));

        ballIndicatorArrow  = new BallIndicatorArrow(this);
        playerTank = new Tank(this, "browntank", "brown");

        playerTank.position.set(level.tee.pos);
        playerTank.rotation = level.tee.facing;

        catapult1 = new Catapult(this, playerTank, new Vector2(900, 100));
        catapult2 = new Catapult(this, playerTank, new Vector2(300, 500));

        oldBulletPosition = new Vector2();
        newBulletPosition = new Vector2();
        bulletCollisionPoint = new Vector2();
        normal = new Vector2();

        gameObjects.add(playerTank);
        gameObjects.add(catapult1);
        gameObjects.add(catapult2);

        catapults.add(catapult1);
        catapults.add(catapult2);

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
        final float velThreshold = 20f;
        if (!playerTank.ball.onTank && !levelTransitioning && playerTank.ball.velocity.len() <= velThreshold) {
            if (level.hole.isInside(playerTank.ball)) {
                // TODO: fancy up the level transition
                levelTransitioning = true;
                int nextLevelNum = ((currentLevelNum + 1) % LudumDare41.game.assets.levelNumberToFileNameMap.size);
                LudumDare41.game.setScreen(new GameScreen(nextLevelNum));
            }
        }
        updateObjects(dt);

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
        ballIndicatorArrow.update(dt);

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
            particleSystem.renderGround(batch);
            for (GameObject gameObj : gameObjects) {
                gameObj.render(batch);
            }
            for (EnemyTank tank : enemyTanks)
            {
                tank.render(batch);
            }
            for (Bullet b : activeBullets){
                b.render(batch);
            }
            particleSystem.render(batch);

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
            ballIndicatorArrow.render(batch);
//            LudumDare41.game.assets.backplateNinePatch.draw(batch, 0, hudCamera.viewportHeight - 50, hudCamera.viewportWidth, 50);
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


    private String[] tankBodies = new String[] { "browntank", "greentank", "orangetank", "pinktank" };
    private String[] tankTreads = new String[] { "brown", "green", "orange", "pink", "greenpontoon", "brownpontoon", "orangepontoon", "pinkpontoon" };
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
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            playerTank.hasShield = !playerTank.hasShield;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            playerTank.dead = false;
        } else {
            return;
        }

        playerTank.setAssets(TankAssets.getTankAssets(tankBodies[bodyIndex], tankTreads[treadIndex]));
    }

    private void updateObjects(float dt) {
        for(int i = activeBullets.size - 1; i >= 0; i--) {
            Bullet b = activeBullets.get(i);
            b.update(dt);
            if (b.checkCollision(playerTank)) {
                b.alive = false;
                playerTank.takeHit();
            }
            oldBulletPosition.set(b.position);
            newBulletPosition.set(b.position);
            newBulletPosition.add(b.velocity.x * b.bulletVelocityMultiplier * dt, b.velocity.y * b.bulletVelocityMultiplier * dt);
            //check collision with the walls
            if (level.checkCollision(oldBulletPosition, newBulletPosition, b.radius, bulletCollisionPoint, normal)){
                b.alive = false;
            }
            if (!b.alive){
                activeBullets.removeIndex(i);
                bulletsPool.free(b);
            }
        }

        for(int i = catapults.size - 1; i>=0; i--) {
            Catapult c = catapults.get(i);
            c.update(dt);
            if (playerTank.ball.position.dst(c.position) < playerTank.ball.radius + c.radius) {
                c.alive = false;
            }
        }

        if (playerTank.dead){
            removeAllBullets();
        }
    }


    public void removeAllBullets(){
        bulletsPool.freeAll(activeBullets);
        activeBullets.clear();
    }

    public void addBullet(Catapult owner, int velocityMultiplier, Vector2 position, TextureRegion tex){
        Bullet b = bulletsPool.obtain();
        b.init(playerTank, position, velocityMultiplier, owner, tex);
        activeBullets.add(b);
    }
}
