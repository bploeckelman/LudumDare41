package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld41.screens.GameScreen;
import lando.systems.ld41.stats.HoleStats;
import lando.systems.ld41.utils.TankAssets;

public class Tank extends GameObject {
    public enum TankMovement {
        None, LeftForward, RightForward, Forward, LeftBack, RightBack, Back, SpinLeft, SpinRight
    }

    public float speed = 200;
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
    public boolean hasShield;

    public int shots;
    public int deaths;

    public Tank(GameScreen screen, String body, String treads) {
        this(screen, body, treads, 60, 60, new Vector2(100, 100));
    }

    public Tank(GameScreen screen, String tankName, String treadType, float width, float height, Vector2 startPosition) {
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
        if (dead) {
            smoke = tank.smoke.getKeyFrame(time);
        } else {
            forceShield = tank.forceShield.getKeyFrame(time);
        }

        ball.update(dt);
        ball.checkCollision(this);
    }

    private void handleMovement(float dt) {
        // Don't move if you have the ball
        if (ball.onTank || dead) return;

        if (!handleManMovement(dt)) {
            handleNoobMovement(dt);
        }
    }

    private boolean handleManMovement(float dt) {
        TankMovement movement = getTankMovement();
        if (movement == TankMovement.None) return false;

        // manly bonus
        dt *= 3f;

        float speedDx = 0;
        float rotationDx = 0;
        float halfDt = dt/2;
        float fullSpeed = speed*dt;

        switch (movement) {
            case RightForward:
                rotationDx = rotationSpeed * halfDt;
                rightTime += dt;
                leftTime += halfDt;
                speedDx = speed * halfDt;
                break;
            case Forward:
                rightTime += dt;
                leftTime += dt;
                speedDx = fullSpeed;
                break;
            case LeftForward:
                rotationDx = -rotationSpeed * halfDt;
                rightTime += halfDt;
                leftTime += dt;
                speedDx = speed * halfDt;
                break;
            case RightBack:
                rotationDx = -rotationSpeed * halfDt;
                rightTime -= dt;
                leftTime -= halfDt;
                speedDx = -speed * halfDt;
                break;
            case Back:
                rightTime -= dt;
                leftTime -= dt;
                speedDx = -(fullSpeed * 0.75f);
                break;
            case LeftBack:
                rotationDx = rotationSpeed * halfDt;
                rightTime -= halfDt;
                leftTime -= dt;
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
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            rotationDx = -rotationSpeed * dt;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.W)){
            leftTime += dt;
            rightTime += dt;
            speedDx = speed*dt;
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)){
            leftTime -= dt;
            rightTime -= dt;
            speedDx = -speed*dt;
        }
        updatePosition(speedDx, rotationDx);
    }

    private void updatePosition(float speedUpdate, float rotationUpdate) {

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

        if (screen.level.checkCollision(oldPosition, newPosition, radius, collisionPoint, normal ) != Level.CollisionType.None){
            newPosition.set(collisionPoint);
        }

        position.set(newPosition);

        // Add track trail for both left and right tracks
        float yTrackOffset = MathUtils.sinDeg(rotation) * TRACK_OFFSET;
        float xTrackOffset = MathUtils.cosDeg(rotation) * TRACK_OFFSET;
        
        screen.addTireTrack(position.x - xTrackOffset, position.y - yTrackOffset, directionVector.len());
        screen.addTireTrack(position.x + xTrackOffset, position.y + yTrackOffset, directionVector.len());

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

        if (dead) {
            batch.draw(tank.dead, x, y, halfX, halfY, width, height, 1, 1, rotation);
            batch.draw(tank.deadTurret, x, y, halfX, halfY, width, height, 1, 1, turretRotation - 90);
            batch.draw(smoke, x, y, halfX, halfY, width, height, 1, 1, turretRotation - 90);
        } else {
            batch.draw(leftTread, x, y, halfX, halfY, width, height, 1, 1, rotation);
            batch.draw(rightTread, x, y, halfX, halfY, width, height, 1, 1, rotation);

            batch.draw(tank.body, x, y, halfX, halfY, width, height, 1, 1, rotation);
            batch.draw((recoilTime > 0) ? tank.turretRecoil : tank.turret, x, y, halfX, halfY, width, height, 1, 1, turretRotation - 90);

            if (ball.onTank) {
                directionVector.set(0, 1);
                directionVector.setAngle(turretRotation);

                tempVector.set(position).add(directionVector.scl(30));
                batch.draw(ball.image, position.x + directionVector.x - 5, position.y + directionVector.y - 5, 5, 5, 10, 10, 1, 1, turretRotation - 90);
            }

            if (hasShield) {
                batch.setColor(1, 1, 1, 0.8f);
                batch.draw(forceShield, x - 15, y - 15, halfX + 15, halfY + 15, width + 30, height + 30, 1, 1, 0);
                batch.setColor(Color.WHITE);
            }
        }
        ball.render(batch);
    }

    public void takeHit() {
        // todo play sounds for shield loss and explosion
        if (hasShield) {
            hasShield = false;
        } else {
            dead = true;
        }
    }

    public void shootBall(float power){
        if (dead) return;
        recoilTime = 0.3f;
        directionVector.set(0, 1);
        directionVector.setAngle(turretRotation);

        tempVector.set(position).add(directionVector.scl(30));
        directionVector.nor();
        screen.particleSystem.addBarrelSmoke(tempVector.x, tempVector.y, directionVector.x, directionVector.y);

        directionVector.scl(20 + (5 * power));

        isFirstBallFired = true;
        ball.shootBall(tempVector, directionVector);
        shots++;
    }

    public void setStats(HoleStats stats) {
        deaths = stats.deaths;
        shots = stats.score;
    }
}
