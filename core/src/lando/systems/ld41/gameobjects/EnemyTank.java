package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.screens.GameScreen;
import lando.systems.ld41.utils.TankAssets;

public class EnemyTank extends GameObject {
    public float speed = 180;
    public static float rotationSpeed = 120;
    private final float TRACK_OFFSET = 20f;

    public TankAssets tank;
    private TextureRegion leftTread;
    private TextureRegion rightTread;
    private float leftTime;
    private float rightTime;

    private float width;
    private float height;
    private GameScreen screen;
    public Vector2 position;
    public float rotation;
    public float turretRotation;

    public Vector2 directionVector;
    public Vector2 oldPosition;
    public Vector2 newPosition;
    public Vector2 collisionPoint;
    private Vector2 normal;
    public float radius;

    public Vector3 camera = new Vector3();

    public boolean moveForward = true;
    public boolean moveBackward;
    public boolean rotateLeft;
    public boolean rotateRight;

    public float leftRotateTime;
    public float rightRotateTime;
    public float moveForwardTime;
    public float moveBackwardTime;

    public float aggroRadius;
    public boolean aggroPlayer;
    public Interpolation lerp = Interpolation.linear;

    public EnemyTank(GameScreen screen, String tankName, float width, float height, Vector2 startPosition, float aggro, float speed)
    {
        tank = TankAssets.getTankAssets(tankName);
        this.speed = speed;

        this.aggroRadius = aggro;
        this.aggroPlayer = false;

        this.width = width;
        this.height = height;
        this.radius = Math.max(width, height)/2f;
        position = startPosition;
        oldPosition = new Vector2();
        newPosition = new Vector2();
        collisionPoint = new Vector2();
        normal = new Vector2();
        this.screen = screen;
        this.directionVector = new Vector2();

        TimeMoveLeft();
        TimeMoveBackward();
        TimeMoveForward();
        TimeMoveRight();
    }

    public void TimeMoveLeft()
    {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                leftRotateTime = MathUtils.random(1, 3);
            }
        }, MathUtils.random(2, 4));
    }

    public void TimeMoveRight()
    {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                rightRotateTime = MathUtils.random(1, 3);
            }
        }, MathUtils.random(2, 4));
    }

    public void TimeMoveForward()
    {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                moveForwardTime = MathUtils.random(10, 20);
            }
        }, MathUtils.random(1, 2));
    }

    public void TimeMoveBackward()
    {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                moveBackwardTime = MathUtils.random(10, 20);
            }
        }, MathUtils.random(1, 2));
    }

    public void checkAggro()
    {
        if (position.dst(screen.playerTank.position) <= aggroRadius)
        {
            this.aggroPlayer = true;
        } else {
            this.aggroPlayer = false;
        }
    }

    public void update(float dt)
    {
        checkAggro();

        if (!aggroPlayer)
        {
            moveNormal(dt);
        } else {
            moveAggro(dt);
        }

        leftTread = tank.leftTreads.getKeyFrame(leftTime, true);
        rightTread = tank.rightTreads.getKeyFrame(rightTime, true);

        setTurretPosition();
    }

    private  void moveAggro(float dt)
    {
        float angleToLerp = (float)(Math.atan2(
                screen.playerTank.position.y - position.y,
                screen.playerTank.position.x - position.x) * 180 / Math.PI) - 90;

        if (angleToLerp == rotation) {

        } else {
            rotation = angleToLerp;
        }

        updatePosition(speed * dt);

    }

    private void moveForward(float dt)
    {

    }

    private void moveNormal(float dt)
    {
        moveForward = moveForwardTime > 0;
        moveBackward = moveBackwardTime > 0;
        rotateRight = rightRotateTime > 0;
        rotateLeft = leftRotateTime > 0;

        if (moveForward)
        {
            moveForwardTime -= 10 * dt;
            if (moveForwardTime <= 0)
            {
                TimeMoveForward();
            }
            leftTime += dt;
            rightTime += dt;
            updatePosition(speed * dt);
        }

        if (moveBackward)
        {
            moveBackwardTime -= 10 * dt;
            if (moveBackwardTime <= 0)
            {
                TimeMoveBackward();
            }
            leftTime -= dt;
            rightTime -= dt;
            if (leftTime < 0)
            {
                leftTime = 0;
            }
            if (rightTime < 0)
            {
                rightTime = 0;
            }
            updatePosition(-speed * dt);
        }

        if (rotateLeft)
        {
            leftRotateTime -= 10 * dt;
            if (leftRotateTime <= 0)
            {
                TimeMoveLeft();
            }
            rotation += rotationSpeed * dt;
        }

        if (rotateRight)
        {
            rightRotateTime -= 10 * dt;
            if (rightRotateTime <= 0)
            {
                TimeMoveRight();
            }
            rotation -= rotationSpeed * dt;
        }
    }
    private void setTurretPosition()
    {
        // Track player
        turretRotation = (float)(Math.atan2(
                screen.playerTank.position.y - position.y,
                screen.playerTank.position.x - position.x) * 180 / Math.PI);
    }

    private void updatePosition(float speed)
    {
        //TODO make this use up stopped velocity so it can slide along edges found later in the boundary
        directionVector.set(1, 0);
        directionVector.setAngle(rotation + 90);
        directionVector.scl(speed);
        oldPosition.set(position);
        newPosition.set(position);
        newPosition.add(directionVector);

        if (screen.level.checkCollision(oldPosition, newPosition, radius, collisionPoint, normal )){
            newPosition.set(collisionPoint);
            moveForward = !moveForward;
            moveBackward = !moveBackward;
        }

        position.set(newPosition);

        // Add track trail for both left and right tracks
        float yTrackOffset = MathUtils.sinDeg(rotation) * TRACK_OFFSET;
        float xTrackOffset = MathUtils.cosDeg(rotation) * TRACK_OFFSET;

        screen.addTireTrack(position.x - xTrackOffset, position.y - yTrackOffset, directionVector.len());
        screen.addTireTrack(position.x + xTrackOffset, position.y + yTrackOffset, directionVector.len());
    }

    public void render(SpriteBatch batch)
    {
        float halfX = width/2;
        float halfY = height/2;
        float x = position.x - halfX;
        float y = position.y - halfY;

        if (leftTread != null) {
            batch.draw(leftTread, x, y, halfX, halfY, width, height, 1, 1, rotation);
            batch.draw(rightTread, x, y, halfX, halfY, width, height, 1, 1, rotation);
        }

        batch.draw(tank.body, x, y, halfX, halfY, width, height, 1, 1, rotation);
        batch.draw(tank.turret, x, y, halfX, halfY, width, height, 1, 1, turretRotation - 90);
    }
}
