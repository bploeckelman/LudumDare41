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
    public int currentLevelNum = -1;
    public Level level;
    public PowerMeter powerMeter;
    public boolean showPowerMeter;
    public boolean levelZoomDone;
    private boolean levelTransitioning;
    public ParticleSystem particleSystem;
    public Catapult catapult1;
    public Array<EnemyTank> enemyTanks = new Array<EnemyTank>();
    public Catapult catapult2;
    public EnemyTurret enemyTurret1;
    public Array<Catapult> catapults = new Array<Catapult>();
    public Array<EnemyTurret> enemyTurrets = new Array<EnemyTurret>();
    private Vector2 oldBulletPosition;
    private Vector2 newBulletPosition;
    private Vector2 bulletCollisionPoint;
    private Vector2 normal;
    private Vector2 tempVec;

    public static final Array<Bullet> activeBullets = new Array<Bullet>();
    public static final Pool<Bullet> bulletsPool = Pools.get(Bullet.class, 500);

    private Array<GameObject> gameObjects = new Array<GameObject>();

    private PlayerHud hud;

    public GameScreen(int currentLevelNum) {
        Gdx.input.setInputProcessor(this);

        hud = new PlayerHud(this);

        setLevel(currentLevelNum);
        addPlayer();

        catapult1 = new Catapult(this, playerTank, new Vector2(900, 100));
        catapult2 = new Catapult(this, playerTank, new Vector2(300, 500));
        enemyTurret1 = new EnemyTurret(this, playerTank, new Vector2(500, 500), new Vector2(500, 510));

        oldBulletPosition = new Vector2();
        newBulletPosition = new Vector2();
        bulletCollisionPoint = new Vector2();
        normal = new Vector2();
        tempVec = new Vector2();

        gameObjects.add(playerTank);
//        gameObjects.add(enemyTurret1);
//        gameObjects.add(catapult1);
//        gameObjects.add(catapult2);

//        catapults.add(catapult1);
//        catapults.add(catapult2);

        enemyTurrets.add(enemyTurret1);

        showPowerMeter = false;
        for (EnemyTankInfo info : level.enemyTankInfos) {
            enemyTanks.add(EnemyTank.create(this, info));
        }
        particleSystem = new ParticleSystem();
        powerMeter = new PowerMeter(1f, playerTank);
        worldCamera.position.set(playerTank.position, 0);
        worldCamera.update();

        this.levelTransitioning = false;
        enterLevelZoom();
    }

    public void setLevel(int levelIndex) {
        if (levelIndex == Config.HOLES) {
            // endCondition
            return;
        }

        currentLevelNum = levelIndex;

        int mapIndex = levelIndex % LudumDare41.game.assets.levelNumberToFileNameMap.size;
        level = new Level(this, LudumDare41.game.assets.levelNumberToFileNameMap.get(mapIndex));
    }

    private void addPlayer() {
        playerTank = new Tank(this, "browntank", "brown");

        playerTank.setStats(LudumDare41.game.gameStats.getLevelStats(currentLevelNum));
        playerTank.position.set(level.tee.pos);
        playerTank.rotation = level.tee.facing;
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(new TitleScreen());
        }

        hud.update(dt);

        if (!levelZoomDone){
            worldCamera.update();
            return;
        }

        cameraTargetPos.set(playerTank.position, 0f);
        updateCamera();

        level.update(dt);
        updateTank();
        final float velThreshold = 20f;
        if (!playerTank.ball.onTank && !levelTransitioning && playerTank.ball.velocity.len() <= velThreshold) {
            if (level.hole.isInside(playerTank.ball)) {
                // TODO: fancy up the level transition
                levelTransitioning = true;
                addStats(false);
                int nextLevelNum = ((currentLevelNum + 1) % LudumDare41.game.assets.levelNumberToFileNameMap.size);
                LudumDare41.game.setScreen(new GameScreen(nextLevelNum));
            }
        }
        updateObjects(dt);

        if (showPowerMeter) {
            powerMeter.update(dt);
        }

        for (EnemyTank tank : enemyTanks) {
            tank.update(dt);
        }
        particleSystem.update(dt);

        for (GameObject gameObj : gameObjects) {
            gameObj.update(dt);
        }



        for (PinballBumper bumper : level.pinballBumpers) {
            bumper.checkForHit(playerTank);
        }
    }

    private void addStats(boolean isDead) {
        LudumDare41.game.gameStats.addStats(currentLevelNum, playerTank.ball.totalDistance, 0, playerTank.shots, isDead);
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
        Gdx.gl.glClearColor(57f / 255f, 123f / 255f, 68f / 255f, 1f);
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
            if (showPowerMeter)
            {
                powerMeter.render(batch);
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
            hud.render(batch);

        }
        batch.end();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!levelZoomDone) return false;

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


    private String[] tankBodies = new String[] { "browntank", "greentank", "orangetank", "pinktank", "bluetank" };
    private String[] tankTreads = new String[] { "brown", "green", "orange", "pink", "blue", "greenpontoon", "brownpontoon", "orangepontoon", "pinkpontoon", "bluepontoon" };
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
                if (playerTank.dead) {
                    addStats(true);
                    LudumDare41.game.setScreen(new GameScreen(currentLevelNum));
                }
            }
            oldBulletPosition.set(b.position);
            newBulletPosition.set(b.position);
            newBulletPosition.add(b.velocity.x * b.bulletSpeed * dt, b.velocity.y * b.bulletSpeed * dt);
            //check collision with the walls
            if (level.checkCollision(oldBulletPosition, newBulletPosition, b.radius, bulletCollisionPoint, normal) != Level.CollisionType.None){
                b.alive = false;
            }
            if (!b.alive){
                activeBullets.removeIndex(i);
                bulletsPool.free(b);
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

    public void addBullet(GameObject owner, Vector2 position, Vector2 dir, TextureRegion tex){
        Bullet b = bulletsPool.obtain();
        b.init(position, dir, owner, tex);
        activeBullets.add(b);
    }
}
