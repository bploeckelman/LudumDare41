package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.screens.GameScreen;
import lando.systems.ld41.stats.HoleStats;
import lando.systems.ld41.ui.PowerMeter;
import lando.systems.ld41.utils.Audio;
import lando.systems.ld41.utils.TankAssets;

public class Tank extends GameObject {
    public enum TankMovement {
        None, LeftForward, RightForward, Forward, LeftBack, RightBack, Back, SpinLeft, SpinRight
    }

    public float MAX_SPEED = 200;
    public float speed = MAX_SPEED;
    public static float rotationSpeed = 120;
    private final float TRACK_OFFSET = 20f;

    public Vector3 camera = new Vector3();
    public Vector2 position;
    private Vector2 oldPosition;
    private Vector2 newPosition;
    private Vector2 collisionPoint;
    private Vector2 normal;
    public float radius;
    public float rotation;
    public float turretRotation;
    public Vector2 directionVector = new Vector2();
    public Vector2 tempVector;

    private TankAssets tank;
    private TextureRegion leftTread;
    private TextureRegion rightTread;
    private TextureRegion smoke;
    private TextureRegion forceShield;
    private float leftTime;
    private float rightTime;
    private float recoilTime;
    private float time;

    private float width;
    private float height;
    private GameScreen screen;
    public PlayerBall ball;
    public boolean isFirstBallFired = false;
    public boolean dead;
    public boolean onSand = false;
    public boolean onWater = false;
    public String tankName;
    public String treadType;

    public int shots;
    public int deaths;
    public int kills;
    public int health;

    // pickups
    public boolean hasShield;
    public boolean hasPontoons = false;
    public boolean isInvincible = false;
    public boolean isVisible = true;

    private float explosionTimer = 0;
    public float invincibleTimer = 0;
    public float camoTimer = 0;

    public Tank(GameScreen screen, String body, String treads) {
        this(screen, body, treads, 60, 60, new Vector2(100, 100));
    }

    public Tank(GameScreen screen, String tankName, String treadType, float width, float height, Vector2 startPosition) {
        this.tankName = tankName;
        this.treadType = treadType;
        setAssets(TankAssets.getTankAssets(tankName, treadType));

        this.width = width;
        this.height = height;
        this.radius = Math.max(width, height)/2f;
        position = startPosition;
        oldPosition = new Vector2();
        newPosition = new Vector2();
        collisionPoint = new Vector2();
        normal = new Vector2();
        this.screen = screen;
        ball = new PlayerBall(screen);
        tempVector = new Vector2();
        dead = false;
        health = 2;
        explosionTimer = 0;
        smoke = tank.smoke.getKeyFrame(0);
    }

    public void setAssets(TankAssets assets) {
        tank = assets;
        leftTread = tank.leftTreads.getKeyFrame(0);
        rightTread = tank.rightTreads.getKeyFrame(0);
    }

    @Override
    public void update(float dt){
        handleMovement(dt);
        setTurretRotation();

        time += dt;

        if (recoilTime > 0) {
            recoilTime -= dt;
        }

        radius = Math.max(width, height)/2f;
        if (hasShield) {
            radius += 15;
        }
        if (health < 2) {
            smoke = tank.smoke.getKeyFrame(time);
        } else {
            forceShield = tank.forceShield.getKeyFrame(time);
        }

        if (ball.onTank) {
            directionLength = MathUtils.lerp(directionLength, 1f, dt);
        }

        ball.update(dt);
        ball.checkCollision(this);

        updatePickups(dt);
    }

    private void updatePickups(float dt) {
        if (camoTimer > 0) {
            camoTimer -= dt;
        }
        isVisible = camoTimer <= 0;

        if (invincibleTimer > 0) {
            invincibleTimer -= dt;
        }
        isInvincible = invincibleTimer > 0;
    }

    private void handleMovement(float dt) {
        // Don't move if you have the ball
        if (dead) return;

        speed = (onSand || onWater) ? MAX_SPEED * 0.5f : MAX_SPEED;

        if (!handleManMovement(dt)) {
            handleNoobMovement(dt);
        }
    }

    private boolean handleManMovement(float dt) {
        TankMovement movement = getTankMovement();
        if (movement == TankMovement.None) return false;

        // manly bonus
        dt *= 2f;

        float speedDx = 0;
        float rotationDx = 0;
        float halfDt = dt/2;
        float fullSpeed = speed*dt;
        float rt = 0;
        float lt = 0;

        switch (movement) {
            case RightForward:
                rotationDx = rotationSpeed * halfDt;
                rt = dt;
                lt = halfDt;
                speedDx = speed * halfDt;
                break;
            case Forward:
                rt = dt;
                lt = dt;
                speedDx = fullSpeed;
                break;
            case LeftForward:
                rotationDx = -rotationSpeed * halfDt;
                rt = halfDt;
                lt = dt;
                speedDx = speed * halfDt;
                break;
            case RightBack:
                rotationDx = -rotationSpeed * halfDt;
                rt = -dt;
                lt =- halfDt;
                speedDx = -speed * halfDt;
                break;
            case Back:
                rt = -dt;
                lt = -dt;
                speedDx = -(fullSpeed * 0.75f);
                break;
            case LeftBack:
                rotationDx = rotationSpeed * halfDt;
                rt = -halfDt;
                lt = -dt;
                speedDx = -speed * halfDt;
                break;
            case SpinLeft:
                rightTime += dt;
                leftTime -= dt;
                rotationDx = rotationSpeed * dt;
                break;
            case SpinRight:
                rightTime -= dt;
                leftTime += dt;
                rotationDx = -rotationSpeed * dt;
                break;
        }

        if (!ball.onTank) {
            rightTime += rt;
            leftTime += lt;
        }

        updatePosition(speedDx, rotationDx);
        return true;
    }

    private TankMovement getTankMovement() {
        boolean leftForward = Gdx.input.isKeyPressed(Input.Keys.R);
        boolean leftBack = !leftForward && Gdx.input.isKeyPressed(Input.Keys.F);
        boolean rightForward = Gdx.input.isKeyPressed(Input.Keys.T);
        boolean rightBack = !rightForward && Gdx.input.isKeyPressed(Input.Keys.G);

        if (leftForward && rightForward) {
            return TankMovement.Forward;
        } else if (leftForward) {
            return (rightBack) ? TankMovement.SpinRight : TankMovement.LeftForward;
        } else if (rightForward) {
            return (leftBack) ? TankMovement.SpinLeft : TankMovement.RightForward;
        } else if (leftBack && rightBack) {
            return TankMovement.Back;
        } else if (leftBack) {
            return TankMovement.LeftBack;
        } else if (rightBack) {
            return TankMovement.RightBack;
        }
        return TankMovement.None;
    }

    private void handleNoobMovement(float dt) {
        float rotationDx = 0;
        float speedDx = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            rotationDx = rotationSpeed*dt;
            leftTime += dt;
            rightTime -= dt;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            rotationDx = -rotationSpeed * dt;
            leftTime -= dt;
            rightTime += dt;
        }

        if (!ball.onTank) {
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                leftTime += dt;
                rightTime += dt;
                speedDx = speed * dt;
            } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                leftTime -= dt;
                rightTime -= dt;
                speedDx = -speed * dt;
            }
        }
        updatePosition(speedDx, rotationDx);
    }

    private void updatePosition(float speedUpdate, float rotationUpdate) {
        if (ball.onTank) {
            speedUpdate = 0;
        }

        rotation += rotationUpdate;

        // handle negative time for key frame
        if (leftTime < 0) {
            leftTime += tank.leftLoopTime;
        }
        if (rightTime < 0) {
            rightTime += tank.rightLoopTime;
        }

        directionVector.set(1, 0);
        directionVector.setAngle(rotation + 90);
        directionVector.scl(speedUpdate);
        oldPosition.set(position);
        newPosition.set(position);
        newPosition.add(directionVector);

        Level.CollisionType collisionType = screen.level.checkCollision(oldPosition, newPosition, radius, collisionPoint, normal );
        if (collisionType != Level.CollisionType.None) {
            if (collisionType == Level.CollisionType.Water && hasPontoons) {
            } else {
                newPosition.set(collisionPoint);
            }
        }

        for (EnemyTank enemyTank : screen.enemyTanks){
            if (enemyTank.dead) continue;
            if (newPosition.dst(enemyTank.position) < radius + enemyTank.radius){
                tempVector.set(position).sub(enemyTank.position).nor().scl(3);
                newPosition.add(tempVector);
            }
        }

        if (screen.boss != null && screen.boss.health > 0){
            if (newPosition.dst(screen.boss.position) < radius + screen.boss.radius){
                tempVector.set(position).sub(screen.boss.position).nor().scl(3);
                newPosition.add(tempVector);
            }
        }

        for (int i = 0; i < screen.gameObjects.size; i++){
            GameObject obj = screen.gameObjects.get(i);
            if (obj == this || !obj.alive) continue;
            if (newPosition.dst(obj.position) < radius + obj.radius){
                tempVector.set(position).sub(obj.position).nor().scl(5);
                newPosition.add(tempVector);
            }
        }

        position.set(newPosition);

        // Add track trail for both left and right tracks
        float yTrackOffset = MathUtils.sinDeg(rotation) * TRACK_OFFSET;
        float xTrackOffset = MathUtils.cosDeg(rotation) * TRACK_OFFSET;
        
        screen.addTireTrack(position.x - xTrackOffset, position.y - yTrackOffset, directionVector.len());
        screen.addTireTrack(position.x + xTrackOffset, position.y + yTrackOffset, directionVector.len());

        // switch to pontoons
        if (onWater) {
            if (hasPontoons) {
                setAssets(TankAssets.getTankAssets(tankName, treadType + "pontoon"));
            }
        } else {
            setAssets(TankAssets.getTankAssets(tankName, treadType));
        }

        leftTread = tank.leftTreads.getKeyFrame(leftTime, true);
        rightTread = tank.rightTreads.getKeyFrame(rightTime, true);
    }

    private void setTurretRotation() {
        if (dead) return;

        camera.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        screen.worldCamera.unproject(camera);

        float tRotation = (float)(Math.atan2(
                camera.y - position.y,
                camera.x - position.x) * 180 / Math.PI);

        turretRotation = MathUtils.lerpAngleDeg(turretRotation, tRotation, 0.05f);
    }

    @Override
    public void render(SpriteBatch batch){
        float halfX = width / 2;
        float halfY = height / 2;
        float x = position.x - halfX;
        float y = position.y - halfY;

        // can be invisibly dead
        if (!isVisible) {
            batch.setColor(1, 1, 1, 0.2f);
        }

        if (dead) {
            batch.draw(tank.dead, x, y, halfX, halfY, width, height, 1, 1, rotation);
            batch.draw(tank.deadTurret, x, y, halfX, halfY, width, height, 1, 1, turretRotation - 90);
            screen.particleSystem.addSmoke(position.x, position.y);
            explosionTimer += Gdx.graphics.getDeltaTime();
            if (explosionTimer < LudumDare41.game.assets.explosionAnimation.getAnimationDuration()) {
                batch.draw(LudumDare41.game.assets.explosionAnimation.getKeyFrame(explosionTimer),
                        x - halfX, y - halfY, halfX, halfY,
                        width * 2f, height * 2f,
                        1f, 1f, 0f);
            }
        } else {
            if (isInvincible) {
                if ((int)(invincibleTimer * 20) % 2 == 0 ) {
                    batch.setColor(Color.RED);
                }
            }

            batch.draw(leftTread, x, y, halfX, halfY, width, height, 1, 1, rotation);
            batch.draw(rightTread, x, y, halfX, halfY, width, height, 1, 1, rotation);

            batch.draw(tank.body, x, y, halfX, halfY, width, height, 1, 1, rotation);

            if (ball.onTank) {
                directionVector.set(0, directionLength);
                directionVector.setAngle(turretRotation);

                tempVector.set(position).add(directionVector.scl(30));
                batch.draw(ball.image, position.x + directionVector.x - 5, position.y + directionVector.y - 5, 5, 5, 10, 10, 1, 1, turretRotation - 90);
            }

            batch.draw((recoilTime > 0) ? tank.turretRecoil : tank.turret, x, y, halfX, halfY, width, height, 1, 1, turretRotation - 90);

            if (hasShield) {
                batch.setColor(1, 1, 1, 0.8f);
                batch.draw(forceShield, x - 15, y - 15, halfX + 15, halfY + 15, width + 30, height + 30, 1, 1, 0);
                batch.setColor(Color.WHITE);
            }

            //if (isInvincible) {
            //    batch.draw(screen.game.assets.puInvincible, x - 15, y - 15, 0, 0, 10, 10, 1, 1, rotation);
            //}
        }

        batch.setColor(Color.WHITE);
        if (health == 1 && !hasShield){

            batch.draw(smoke, x, y, halfX, halfY, width, height, 1, 1, 0);

        }
        ball.render(batch);
    }

    public void takeHit() {
        if (health < 1) return;
        screen.screenShake.addDamage(.4f);
        // todo play sounds for shield loss and explosion
        if (hasShield) {
            hasShield = false;
        } else if (!isInvincible) {
            health--;
            if (health < 1) {
                dead = true;
            }
        }
    }

    private float directionLength = 1;

    public void pickupBall() {
        directionLength = 0.8f;
        ball.onTank = true;
    }

    public void pickup(Pickup.PickupType type) {
        switch (type) {
            case shield:
                hasShield = true;
                break;
            case camo:
                invincibleTimer = 0;
                camoTimer = 15;
                break;
            case invincible:
                camoTimer = 0;
                invincibleTimer = 15;
                break;
            case pontoon:
                hasPontoons = true;
                break;
        }
    }

    public void shootBall(PowerMeter meter){
        if (dead) return;

        float power = meter.power;

        recoilTime = 0.3f;
        directionVector.set(0, 1);
        directionVector.setAngle(turretRotation);

        tempVector.set(position).add(directionVector.scl(30));
        directionVector.nor();
        screen.particleSystem.addBarrelSmoke(tempVector.x, tempVector.y, directionVector.x, directionVector.y);

        if (power > 90){
            meter.setSuperShot();
            screen.particleSystem.addBarrelSparks(tempVector.x, tempVector.y, directionVector.x, directionVector.y);
            LudumDare41.game.audio.playSound(Audio.Sounds.sassy_boom);

        } else {
            LudumDare41.game.audio.playSound(Audio.Sounds.shot);

        }
        directionVector.scl(20 + (8 * power));

        isFirstBallFired = true;
        screen.screenShake.addDamage(power/200f);
        ball.shootBall(tempVector, directionVector);
        shots++;
    }

    public void setStats(HoleStats stats) {
        deaths = stats.deaths;
        shots = stats.score;
        kills = stats.kills;
    }
}
