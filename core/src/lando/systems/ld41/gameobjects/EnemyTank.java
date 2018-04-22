package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.ai.StateMachine;
import lando.systems.ld41.ai.Transition;
import lando.systems.ld41.ai.conditions.PlayerCloserThan;
import lando.systems.ld41.ai.conditions.PlayerFurtherThan;
import lando.systems.ld41.ai.states.TargetPlayerState;
import lando.systems.ld41.ai.states.WaitState;
import lando.systems.ld41.ai.states.WanderState;
import lando.systems.ld41.screens.GameScreen;
import lando.systems.ld41.utils.TankAssets;

public class EnemyTank extends GameObject {
    public static float rotationSpeed = 120;
    private final float TRACK_OFFSET = 20f;

    public TankAssets tank;
    private TextureRegion leftTread;
    private TextureRegion rightTread;
    private float leftTime;
    private float rightTime;

    private float width;
    private float height;
    public float turretRotation;

    public Vector2 directionVector;
    public Vector2 oldPosition;
    public Vector2 newPosition;
    public Vector2 collisionPoint;
    private Vector2 normal;
    private Vector2 tempVec;
    public float radius;

    public Interpolation lerp = Interpolation.linear;
    public StateMachine stateMachine;

    public EnemyTank(GameScreen screen, String tankName, float width, float height, Vector2 startPosition, float aggro, float speed)
    {
        tempVec = new Vector2();
        tank = TankAssets.getTankAssets(tankName);
        this.speed = speed;

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

        initializeStates();
    }

    private void initializeStates(){
        WaitState wait = new WaitState(this);
        WanderState wander = new WanderState(this);
        TargetPlayerState targetPlayer = new TargetPlayerState(this);

        PlayerCloserThan closerThan200 = new PlayerCloserThan(this, 200);
        PlayerCloserThan closerThan100 = new PlayerCloserThan(this, 100);
        PlayerFurtherThan furtherThanWanderRange = new PlayerFurtherThan(this, 500);
        PlayerFurtherThan furtherThan150 = new PlayerFurtherThan(this, 150);

        Array<Transition> transitions = new Array<Transition>();
        transitions.add(new Transition(wait, closerThan200, wander));
        transitions.add(new Transition(wander, furtherThanWanderRange, wait));

//        transitions.add(new Transition(wander, closerThan100, targetPlayer));
        transitions.add(new Transition(targetPlayer, furtherThan150, wander));

        stateMachine = new StateMachine(wait, transitions);
    }



    public void update(float dt)
    {
        stateMachine.update(dt);

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


    private void setTurretPosition()
    {
        // Track player
        turretRotation = (float)(Math.atan2(
                screen.playerTank.position.y - position.y,
                screen.playerTank.position.x - position.x) * 180 / Math.PI);
    }

    public boolean updatePosition(float speed)
    {
        directionVector.set(1, 0);
        directionVector.setAngle(rotation + 90);
        directionVector.scl(speed);
        oldPosition.set(position);
        newPosition.set(position);
        newPosition.add(directionVector);
        boolean collision = false;
        if (screen.level.checkCollision(oldPosition, newPosition, radius, collisionPoint, normal )){
            newPosition.set(collisionPoint);
            collision = true;
        }

        position.set(newPosition);

        // Add track trail for both left and right tracks
        float yTrackOffset = MathUtils.sinDeg(rotation) * TRACK_OFFSET;
        float xTrackOffset = MathUtils.cosDeg(rotation) * TRACK_OFFSET;

        screen.addTireTrack(position.x - xTrackOffset, position.y - yTrackOffset, directionVector.len());
        screen.addTireTrack(position.x + xTrackOffset, position.y + yTrackOffset, directionVector.len());
        return collision;
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

    /**
     *  Attempt to rotate and move towards a target point
     * @param targetPos the target to move towards
     * @param dt the delta time
     * @return if it reached it's endpoint or ran into a wall
     */
    public boolean rotateAndMove(Vector2 targetPos, float dt){
        float angleToLerp = (float)(Math.atan2(
                targetPos.y - position.y,
                targetPos.x - position.x) * 180 / Math.PI) - 90;
        if (angleToLerp - rotation < -180) rotation -= 360;
        if (angleToLerp - rotation > 180) rotation += 360;
        float amountToRotate = Math.abs(angleToLerp - rotation);
        if (angleToLerp != rotation){
            float rotationAmount = 120 * dt;
            if (amountToRotate < rotationAmount){
                rotation = angleToLerp;
            } else {
                rotation += Math.signum(angleToLerp - rotation) * rotationAmount;
            }
        }
        float distanceToTarget = position.dst(targetPos);

        if (amountToRotate == 0 || amountToRotate < distanceToTarget / 5f){
            float distanceToMove = speed * dt;
            if (distanceToMove > distanceToTarget) {
                distanceToMove = distanceToTarget;
            }

            if (distanceToMove == 0 || updatePosition(distanceToMove)) {
                return true;
            }

        }
        return false;
    }
}
