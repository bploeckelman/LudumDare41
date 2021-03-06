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
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.gameobjects.*;
import lando.systems.ld41.particles.ParticleSystem;
import lando.systems.ld41.ui.Button;
import lando.systems.ld41.ui.HelpModalWindow;
import lando.systems.ld41.ui.PowerMeter;
import lando.systems.ld41.ui.screenshake.ScreenShakeCameraController;
import lando.systems.ld41.utils.Audio;
import lando.systems.ld41.utils.Config;
import lando.systems.ld41.utils.TankAssets;
import lando.systems.ld41.utils.Utils;
import lando.systems.ld41.utils.accessors.CameraAccessor;


/**
 * Created by Brian Ploeckelman <brian.ploeckelman@wisc.edu> on 4/13/18.
 */
public class GameScreen extends BaseScreen {

    private static final float CAMERA_ZOOM_MARGIN = 1.2f;

    public Tank playerTank;
    public FinalBoss boss;
    public int currentLevelNum = -1;
    public Level level;
    public PowerMeter powerMeter;
    public boolean showPowerMeter;
    public boolean levelZoomDone;
    private boolean levelTransitioning;
    public ParticleSystem particleSystem;
    public Array<EnemyTank> enemyTanks = new Array<EnemyTank>();
    public Array<Catapult> catapults = new Array<Catapult>();
    public Array<EnemyTurret> enemyTurrets = new Array<EnemyTurret>();
    private Vector2 oldBulletPosition;
    private Vector2 newBulletPosition;
    private Vector2 bulletCollisionPoint;
    private Vector2 normal;
    private Vector2 tempVec;
    public ScreenShakeCameraController screenShake;

    public static final Array<Bullet> activeBullets = new Array<Bullet>();
    public static final Pool<Bullet> bulletsPool = Pools.get(Bullet.class, 500);
    private Button restartLevelButton;
    private Button helpButton;
    private HelpModalWindow helpModalWindow;

    public Array<GameObject> gameObjects = new Array<GameObject>();
    public Array<Pickup> pickups = new Array<Pickup>();

    private PlayerHud hud;

    private double time;

    public GameScreen(int currentLevelNum) {
        time = 0;
        Gdx.input.setInputProcessor(this);

        hud = new PlayerHud(this);
        float buttonMargin = 20f;
        float buttonSize = 80f;
        restartLevelButton = new Button(LudumDare41.game.assets.refreshButton, hudCamera, hudCamera.viewportWidth - buttonMargin - buttonSize, buttonMargin);
        helpButton = new Button(LudumDare41.game.assets.helpButton, hudCamera, hudCamera.viewportWidth - buttonMargin - buttonSize,buttonMargin + buttonSize);
        helpModalWindow = new HelpModalWindow(hudCamera);
        setLevel(currentLevelNum);
        addPlayer();

        oldBulletPosition = new Vector2();
        newBulletPosition = new Vector2();
        bulletCollisionPoint = new Vector2();
        normal = new Vector2();
        tempVec = new Vector2();

        gameObjects.add(playerTank);
        for (EnemyTurretInfo info : level.enemyTurretInfos) {
            Vector2 position = new Vector2(info.x + EnemyTurret.TURRET_WIDTH / 2f, info.y + EnemyTurret.TURRET_HEIGHT / 2f);
            Vector2 direction = new Vector2(0f, 1f).rotate(info.facing);
            EnemyTurret turret = new EnemyTurret(this, playerTank, position, direction);
            turret.rotation = info.facing;
            enemyTurrets.add(turret);
            gameObjects.add(turret);
        }
        for (CatapultInfo info : level.catapultInfos) {
            Vector2 position = new Vector2(info.x + Catapult.TURRET_WIDTH / 2f, info.y + Catapult.TURRET_HEIGHT / 2f);
            Catapult catapult = new Catapult(this, playerTank, position);
            catapults.add(catapult);
            gameObjects.add(catapult);
        }
        for (PowerupInfo info : level.powerupInfos){
            pickups.add(new Pickup(this, info.type, info.x, info.y));
        }

        showPowerMeter = false;
        for (EnemyTankInfo info : level.enemyTankInfos) {
            enemyTanks.add(EnemyTank.create(this, info));
        }

        if (level.finalBossInfo != null){
            level.hole.position.x = -1000;
            boss = new FinalBoss(this, level.finalBossInfo.x, level.finalBossInfo.y);
        }
        particleSystem = new ParticleSystem();
        powerMeter = new PowerMeter(1f, playerTank);
        worldCamera.position.set(playerTank.position, 0);
        worldCamera.update();

        this.levelTransitioning = false;
        screenShake = new ScreenShakeCameraController(worldCamera);
        enterLevelZoom();
    }

    public void setLevel(int levelIndex) {
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
            game.setScreen(new LevelSelectScreen());
        }
        screenShake.update(dt);
        hud.update(dt);
        particleSystem.update(dt);

        for (int i = pickups.size - 1; i >= 0; i--) {
            Pickup pu = pickups.get(i);
            pu.update(dt);
            pu.checkCollision(playerTank);
            if (!pu.visible) {
                pickups.removeIndex(i);
            }
        }

        // debug
        //updateTank();

        level.update(dt);
        if (!levelZoomDone){
            worldCamera.update();
            return;
        }

        if (helpModalWindow.isActive) {
            return;
        }
        cameraTargetPos.set(playerTank.position, 0f);
        updateCamera();
        updateObjects(dt);

        if (showPowerMeter) {
            powerMeter.update(dt);
        }

        if (boss != null) {
            boss.update(dt);
        }

        for (EnemyTank tank : enemyTanks) {
            tank.update(dt);
        }

        for (GameObject gameObj : gameObjects) {
            gameObj.update(dt);
        }

        for (PinballBumper bumper : level.pinballBumpers) {
            bumper.checkForHit(playerTank);
        }

        playerTank.onWater = false;
        playerTank.ball.onWater = false;
        for (Polygon waterPoly : level.waterRegions) {
            if (Utils.overlaps(waterPoly, playerTank.position.x, playerTank.position.y, playerTank.radius)) {
                playerTank.onWater = true;
            }
            if (Utils.overlaps(waterPoly, playerTank.ball.position.x, playerTank.ball.position.y, playerTank.ball.radius)) {
                playerTank.ball.onWater = true;
            }
        }

        playerTank.onSand = false;
        playerTank.ball.onSand = false;
        for (EnemyTank tank : enemyTanks) {
            tank.onSand = false;
        }
        for (Polygon sandPoly : level.sandRegions) {
            if (Utils.overlaps(sandPoly, playerTank.position.x, playerTank.position.y, playerTank.radius)) {
                playerTank.onSand = true;
            }
            if (Utils.overlaps(sandPoly, playerTank.ball.position.x, playerTank.ball.position.y, playerTank.ball.radius)) {
                playerTank.ball.onSand = true;
            }
            for (EnemyTank tank : enemyTanks) {
                if (Utils.overlaps(sandPoly, tank.position.x, tank.position.y, tank.radius)) {
                    tank.onSand = true;
                }
            }
        }

        checkShot();
    }

    private Vector2 orthVector = new Vector2();
    private void checkShot() {
        Hole hole = level.hole;
        Ball ball = playerTank.ball;
        if (!(ball.visible && hole.isInside(ball))) { return; }

        // scale by middle of hole
        float spdScl = 1 - (0.15f* (1 - ball.holeCenterDist/(hole.width/2)));
        ball.velocity.scl(spdScl);

        float ballVelocity = ball.velocity.len();

        if (ballVelocity < 15) {
            ball.visible = false;
            playerScores();
            return;
        }

        float x = ball.holeCenterDist / hole.width/2;
        float spdMod = (0.01f*(1-x));

        if (ballVelocity < 200) {
            float scale = 5; //(1.2f - ((ballVelocity - 20) / 180));
            //System.out.println("scale " + scale + " bv: " + ball.velocity.len() + "sclspd " + spdScl );

            if (ball.holeCenterSide > 0) {
                orthVector.set(-ball.velocity.y, ball.velocity.x);
            } else {
                orthVector.set(ball.velocity.y, -ball.velocity.y);
            }
            ball.velocity.add(orthVector.nor().scl(scale));
        }

        ball.velocity.scl(1 - spdMod);
    }

    private void playerScores() {
        // TODO: fancy up the level transition
        if (!levelZoomDone) return;
        levelZoomDone = false;
        LudumDare41.game.audio.playSound(Audio.Sounds.in_the_hole);
        addStats(false);
        removeAllBullets();
        Tween.to(worldCamera, CameraAccessor.XYZ, 2f)
            .target(level.hole.position.x, level.hole.position.y, .3f)
            .setCallback(new TweenCallback() {
                @Override
                public void onEvent(int i, BaseTween<?> baseTween) {
                    int maxLevel = game.assets.levelNumberToFileNameMap.size;
                    int nextLevelNum = (currentLevelNum + 1);
                    // cycle through maps if there are more holes and created
                    int level = nextLevelNum % LudumDare41.game.assets.levelNumberToFileNameMap.size;
                    BaseScreen screen = (nextLevelNum == maxLevel)
                            ? new ScoreCard() : new GameScreen(level);

                    ShaderProgram transition = (nextLevelNum == maxLevel)
                            ? LudumDare41.game.assets.doomShader : LudumDare41.game.assets.circleCropShader;

                    LudumDare41.game.setScreen(screen, transition, 1.4f);
                }
            })
             .start(LudumDare41.game.tween);
    }

    private void addStats(boolean isDead) {
        double totalTime = System.currentTimeMillis() - time;
        LudumDare41.game.gameStats.addStats(currentLevelNum, playerTank.ball.totalDistance, playerTank.kills, playerTank.shots, isDead, totalTime);
    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();
        batch.setProjectionMatrix(hudCamera.combined);
        batch.setColor(Config.bgColor);
        batch.draw(LudumDare41.game.assets.whitePixel,0,0, hudCamera.viewportWidth, hudCamera.viewportHeight);
        batch.end();

        renderGame(batch);
        if (levelZoomDone) {
            renderUI(batch);
        }
    }

    private void renderGame(SpriteBatch batch) {
        Gdx.gl.glClearColor(60/255f, 89/255f, 86/255f, 1f);
//        Gdx.gl.glClearColor(57f / 255f, 123f / 255f, 68f / 255f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        level.render(batch, screenShake.getViewCamera());

        batch.setProjectionMatrix(screenShake.getCombinedMatrix());
        batch.begin();
        {
            batch.setColor(Color.WHITE);
            particleSystem.renderGround(batch);

            for (Pickup pu : pickups) {
                pu.render(batch);
            }

            for (GameObject gameObj : gameObjects) {
                gameObj.render(batch);
            }
            for (EnemyTank tank : enemyTanks)
            {
                tank.render(batch);
            }

            if (boss != null) {
                boss.render(batch);
            }

            for (Bullet b : activeBullets){
                b.render(batch);
            }
            playerTank.render(batch);

            particleSystem.render(batch);
            if (showPowerMeter)
            {
                powerMeter.render(batch);
            }
            powerMeter.renderSuperShot(batch);
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
            restartLevelButton.render(batch);
            helpButton.render(batch);
            if (helpModalWindow.isActive) {
                helpModalWindow.render(batch);
            }
        }
        batch.end();
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (!levelZoomDone) return false;
        if (restartLevelButton.checkForTouch(screenX, screenY) && levelZoomDone) {
            return true;
        }
        if (helpButton.checkForTouch(screenX, screenY)) {
            return true;
        }
        if (button == 0 && playerTank.ball.onTank){
            showPowerMeter = true;
        }

        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (restartLevelButton.checkForTouch(screenX, screenY) && levelZoomDone) {
            playerTank.health = 0;
            showPowerMeter = false;
            addStats(true);
            levelZoomDone = false;
            LudumDare41.game.audio.playSound(Audio.Sounds.lose_level);
            Tween.to(worldCamera, CameraAccessor.XYZ, 2f)
                    .target(playerTank.position.x, playerTank.position.y, .3f)
                    .setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int i, BaseTween<?> baseTween) {
                            LudumDare41.game.setScreen(new GameScreen(currentLevelNum), LudumDare41.game.assets.doomShader, 1.4f);
                        }
                    })
                    .start(LudumDare41.game.tween);
            return true;
        }
        else if (helpButton.checkForTouch(screenX, screenY)) {
            helpModalWindow.show();
            return true;
        }
        else if (button == 0) {
            if (helpModalWindow.isActive) {
                helpModalWindow.hide();
            }
            if (playerTank.ball.onTank && showPowerMeter) {
                playerTank.shootBall(powerMeter);
            }
            showPowerMeter = false;
            powerMeter.reset();
        }
        return true;
    }

    private void enterLevelZoom() {
        levelZoomDone = false;
        cameraTargetPos.set(playerTank.position, 0f);
        targetZoom.setValue(1.2f); // Already defined
        float initialZoom = 1.2f;
        float targetZoom = Math.max( // So wtf is this targetZoom?
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
                                   time = System.currentTimeMillis();
                               }
                           }))
                .start(LudumDare41.game.tween);
    }


    private String[] tankBodies = new String[] { "browntank", "greentank", "orangetank", "pinktank", "bluetank" };
    private String[] tankTreads = new String[] { "brown", "green", "orange", "pink", "blue", "greenpontoon", "brownpontoon", "orangepontoon", "pinkpontoon", "bluepontoon" };
    private int bodyIndex = 0;
    private int treadIndex = 0;

/*
    public void updateTank() {
        // update tank look - temp
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            addPickup(Pickup.PickupType.shield, 200, 100);
            addPickup(Pickup.PickupType.camo, 300, 100);
            addPickup(Pickup.PickupType.invincible, 400, 100);
            addPickup(Pickup.PickupType.pontoon, 500, 100);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
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
    */

    private void updateObjects(float dt) {
        for(int i = activeBullets.size - 1; i >= 0; i--) {
            Bullet b = activeBullets.get(i);
            b.update(dt);
            if (b.checkCollision(playerTank)) {
                b.alive = false;
                playerTank.takeHit();
                if (playerTank.dead) {
                    addStats(true);
                    levelZoomDone = false;
                    LudumDare41.game.audio.playSound(Audio.Sounds.lose_level);
                    Tween.to(worldCamera, CameraAccessor.XYZ, 2f)
                            .target(playerTank.position.x, playerTank.position.y, .3f)
                            .setCallback(new TweenCallback() {
                                @Override
                                public void onEvent(int i, BaseTween<?> baseTween) {
                                    LudumDare41.game.setScreen(new GameScreen(currentLevelNum), LudumDare41.game.assets.doomShader, 1.4f);
                                }
                            })
                            .start(LudumDare41.game.tween);
                }
            }
            oldBulletPosition.set(b.position);
            newBulletPosition.set(b.position);
            newBulletPosition.add(b.velocity.x * b.bulletSpeed * dt, b.velocity.y * b.bulletSpeed * dt);
            //check collision with the walls & water
            Level.CollisionType collisionType = level.checkCollision(oldBulletPosition, newBulletPosition, b.radius, bulletCollisionPoint, normal);
            if (collisionType != Level.CollisionType.None && collisionType != Level.CollisionType.Water){
                if (b.bouncyBullet){
                    float currentSpeed = b.velocity.len();
                    tempVec.set(b.velocity);
                    // r=d−2(d⋅n)n
                    float dot = 2f * tempVec.dot(normal);
                    tempVec.sub(dot * normal.x, dot * normal.y);
                    b.velocity.set(tempVec).nor().scl(currentSpeed);
                    b.position.set(bulletCollisionPoint);
                } else {
                    b.alive = false;
                }
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

    /*
    public Pickup addPickup(Pickup.PickupType pickupType, float x, float y) {
        Pickup pickup = new Pickup(this, pickupType, x, y);
        pickups.add(pickup);
        return pickup;
    }
    */

    public long getTime() {
        return (long)(System.currentTimeMillis() - time) / 1000;
    }
}
