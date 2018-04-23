package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.ai.StateMachine;
import lando.systems.ld41.ai.Transition;
import lando.systems.ld41.ai.conditions.PlayerCloserThan;
import lando.systems.ld41.ai.conditions.PlayerFurtherThan;
import lando.systems.ld41.ai.states.EvadePlayerState;
import lando.systems.ld41.ai.states.TargetPlayerState;
import lando.systems.ld41.ai.states.WaitState;
import lando.systems.ld41.ai.states.WanderState;
import lando.systems.ld41.screens.GameScreen;
import lando.systems.ld41.utils.Assets;
import lando.systems.ld41.utils.Audio;
import lando.systems.ld41.utils.TankAssets;

public class EnemyTank extends GameObject {
    public enum EnemyType {orange, green, pink, blue}

    public static float rotationSpeed = 120;
    public static float TURRET_SPEED = 360;
    private final float TRACK_OFFSET = 20f;
    private final float MAX_SPEED = 100f;

    public TankAssets tank;
    private TextureRegion leftTread;
    private TextureRegion rightTread;
    private TextureRegion bulletTexture;
    private float leftTime;
    private float rightTime;

    private float width;
    private float height;
    public float turretRotation;
    public float turrentTargetRotation;

    public Vector2 directionVector;
    public Vector2 oldPosition;
    public Vector2 newPosition;
    public Vector2 collisionPoint;
    private Vector2 normal;
    private Vector2 tempVec;
    public float radius;

    public Interpolation lerp = Interpolation.linear;
    public StateMachine stateMachine;
    public float reloadTimer = 5f;
    public float reloadDelay = 5f;

    public boolean dead;
    public boolean killingIt = false;
    public boolean onWater = false;
    public boolean onSand = false;
    private float explodeAnimTime = 0f;
    private float accum;
    public EnemyType type;

    public static EnemyTank create(GameScreen screen, EnemyTankInfo info) {
        EnemyType type = EnemyType.green;
        if      (info.color.equalsIgnoreCase("orange"))  type = EnemyType.orange;
        else if (info.color.equalsIgnoreCase("green"))   type = EnemyType.green;
        else if (info.color.equalsIgnoreCase("magenta")) type = EnemyType.pink;
        else if (info.color.equalsIgnoreCase("blue"))    type = EnemyType.blue;
//        else {
//                throw new GdxRuntimeException("Unable to create EnemyTank with color '" + info.color + "'");
//        }
        EnemyTank tank = new EnemyTank(screen, type, 60, 60, new Vector2(info.x, info.y));
        tank.rotation = info.facing;
        return tank;
    }

    public EnemyTank(GameScreen screen, EnemyType type, float width, float height, Vector2 startPosition)
    {
        this.type = type;
        this.tempVec = new Vector2();
        this.bouncyBullets = true;
        this.width = width;
        this.height = height;
        this.radius = Math.max(width, height)/2f;
        this.position = startPosition;
        this.oldPosition = new Vector2();
        this.newPosition = new Vector2();
        this.collisionPoint = new Vector2();
        this.normal = new Vector2();
        this.screen = screen;
        this.directionVector = new Vector2();
        this.speed = MAX_SPEED;
        this.bulletSpeed = 300;
        this.dead = false;
        switch(type){
            case orange: initializeOrange(); break;
            case green:  initializeGreen(); break;
            case pink:   initializePink(); break;
            case blue:   initializeBlue(); break;
            default:     initializeOrange();
        }

        leftTread = tank.leftTreads.getKeyFrame(leftTime, true);
        rightTread = tank.rightTreads.getKeyFrame(rightTime, true);
    }

    private void initializeOrange(){
        tank = TankAssets.getTankAssets("orangetank");
        bulletTexture = Assets.getImage(Assets.Balls.Orange);

        WaitState wait = new WaitState(this);
        WanderState wander = new WanderState(this);
        TargetPlayerState targetPlayer = new TargetPlayerState(this);
        EvadePlayerState evadePlayer = new EvadePlayerState(this);

        PlayerCloserThan insideWander = new PlayerCloserThan(this, 500);
        PlayerCloserThan insideTarget = new PlayerCloserThan(this, 300);
        PlayerCloserThan insideEvade = new PlayerCloserThan(this, 100);
        PlayerFurtherThan outsideWander = new PlayerFurtherThan(this, 600);
        PlayerFurtherThan outsideTarget = new PlayerFurtherThan(this, 400);
        PlayerFurtherThan outsideEvade = new PlayerFurtherThan(this, 150);

        Array<Transition> transitions = new Array<Transition>();
        transitions.add(new Transition(wait, insideWander, wander));
        transitions.add(new Transition(wander, outsideWander, wait));

        transitions.add(new Transition(wander, insideTarget, targetPlayer));
        transitions.add(new Transition(targetPlayer, outsideTarget, wander));

        transitions.add(new Transition(targetPlayer, insideEvade, evadePlayer));
        transitions.add(new Transition(evadePlayer, outsideEvade, targetPlayer));

        stateMachine = new StateMachine(wait, transitions);
    }

    private void initializeGreen(){
        tank = TankAssets.getTankAssets("greentank");
        bulletTexture = Assets.getImage(Assets.Balls.Purple); // TODO: where's my green balls?

        WaitState wait = new WaitState(this);
        WanderState wander = new WanderState(this);
        TargetPlayerState targetPlayer = new TargetPlayerState(this);
        EvadePlayerState evadePlayer = new EvadePlayerState(this);

        PlayerCloserThan insideWander = new PlayerCloserThan(this, 500);
        PlayerCloserThan insideTarget = new PlayerCloserThan(this, 300);
        PlayerCloserThan insideEvade = new PlayerCloserThan(this, 100);
        PlayerFurtherThan outsideWander = new PlayerFurtherThan(this, 600);
        PlayerFurtherThan outsideTarget = new PlayerFurtherThan(this, 400);
        PlayerFurtherThan outsideEvade = new PlayerFurtherThan(this, 150);

        Array<Transition> transitions = new Array<Transition>();
        transitions.add(new Transition(wait, insideWander, wander));
        transitions.add(new Transition(wander, outsideWander, wait));

        transitions.add(new Transition(wander, insideTarget, targetPlayer));
        transitions.add(new Transition(targetPlayer, outsideTarget, wander));

        transitions.add(new Transition(targetPlayer, insideEvade, evadePlayer));
        transitions.add(new Transition(evadePlayer, outsideEvade, targetPlayer));

        stateMachine = new StateMachine(wait, transitions);
    }


    private void initializeBlue(){
        tank = TankAssets.getTankAssets("bluetank");
        bulletTexture = Assets.getImage(Assets.Balls.Blue);

        WaitState wait = new WaitState(this);
        WanderState wander = new WanderState(this);
        TargetPlayerState targetPlayer = new TargetPlayerState(this);
        EvadePlayerState evadePlayer = new EvadePlayerState(this);

        PlayerCloserThan insideWander = new PlayerCloserThan(this, 500);
        PlayerCloserThan insideTarget = new PlayerCloserThan(this, 300);
        PlayerCloserThan insideEvade = new PlayerCloserThan(this, 100);
        PlayerFurtherThan outsideWander = new PlayerFurtherThan(this, 600);
        PlayerFurtherThan outsideTarget = new PlayerFurtherThan(this, 400);
        PlayerFurtherThan outsideEvade = new PlayerFurtherThan(this, 150);

        Array<Transition> transitions = new Array<Transition>();
        transitions.add(new Transition(wait, insideWander, wander));
        transitions.add(new Transition(wander, outsideWander, wait));

        transitions.add(new Transition(wander, insideTarget, targetPlayer));
        transitions.add(new Transition(targetPlayer, outsideTarget, wander));

        transitions.add(new Transition(targetPlayer, insideEvade, evadePlayer));
        transitions.add(new Transition(evadePlayer, outsideEvade, targetPlayer));

        stateMachine = new StateMachine(wait, transitions);
    }

    private void initializePink(){
        tank = TankAssets.getTankAssets("pinktank");
        bulletTexture = Assets.getImage(Assets.Balls.Pink);

        WaitState wait = new WaitState(this);
        WanderState wander = new WanderState(this);
        TargetPlayerState targetPlayer = new TargetPlayerState(this);
        EvadePlayerState evadePlayer = new EvadePlayerState(this);

        PlayerCloserThan insideWander = new PlayerCloserThan(this, 500);
        PlayerCloserThan insideTarget = new PlayerCloserThan(this, 300);
        PlayerCloserThan insideEvade = new PlayerCloserThan(this, 100);
        PlayerFurtherThan outsideWander = new PlayerFurtherThan(this, 600);
        PlayerFurtherThan outsideTarget = new PlayerFurtherThan(this, 400);
        PlayerFurtherThan outsideEvade = new PlayerFurtherThan(this, 150);

        Array<Transition> transitions = new Array<Transition>();
        transitions.add(new Transition(wait, insideWander, wander));
        transitions.add(new Transition(wander, outsideWander, wait));

        transitions.add(new Transition(wander, insideTarget, targetPlayer));
        transitions.add(new Transition(targetPlayer, outsideTarget, wander));

        transitions.add(new Transition(targetPlayer, insideEvade, evadePlayer));
        transitions.add(new Transition(evadePlayer, outsideEvade, targetPlayer));

        stateMachine = new StateMachine(wait, transitions);
    }

    public void kill() {
        if (killingIt) return;
        killingIt = true;
        screen.screenShake.addDamage(.4f);
        LudumDare41.game.audio.playSound(Audio.Sounds.explosion);
        explodeAnimTime = 0f;
        screen.playerTank.kills++;
    }

    public void update(float dt)
    {
        if (!screen.playerTank.isFirstBallFired) return;
        if (killingIt) {
            explodeAnimTime += dt;
            if (explodeAnimTime >= LudumDare41.game.assets.explosionAnimation.getAnimationDuration()) {
                killingIt = false;
                dead = true;
            }
        }

        accum += dt;
        if (dead || killingIt) return;
        stateMachine.update(dt);


        updateTurretRotation(dt);
//        setTurretPosition();
    }


    private void updateTurretRotation(float dt)
    {
        if (killingIt) return;
        if (turrentTargetRotation - turretRotation < -180) turretRotation -= 360;
        if (turrentTargetRotation - turretRotation > 180) turretRotation += 360;

        float amountToRotate = Math.abs(turrentTargetRotation - turretRotation);
        if (turrentTargetRotation != turretRotation){
            float rotationAmount = TURRET_SPEED * dt;
            if (amountToRotate < rotationAmount){
                turretRotation = turrentTargetRotation;
            } else {
                turretRotation += Math.signum(turrentTargetRotation - rotation) * rotationAmount;
            }
        }
//
//        // Track player
//        turretRotation = (float)(Math.atan2(
//                screen.playerTank.position.y - position.y,
//                screen.playerTank.position.x - position.x) * 180 / Math.PI);
    }

    public boolean updatePosition(float speed)
    {
        if (killingIt) return false;
        directionVector.set(1, 0);
        directionVector.setAngle(rotation + 90);
        directionVector.scl(speed);
        oldPosition.set(position);
        newPosition.set(position);
        newPosition.add(directionVector);
        boolean collision = false;
        if (screen.level.checkCollision(oldPosition, newPosition, radius, collisionPoint, normal ) != Level.CollisionType.None){
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

        if (dead) {
            batch.draw(tank.dead, x, y, halfX, halfY, width, height, 1, 1, rotation);
            batch.draw(tank.deadTurret, x, y, halfX, halfY, width, height, 1, 1, turretRotation - 90);
            batch.draw(tank.smoke.getKeyFrame(accum), x, y, halfX, halfY, width, height, 1, 1, 0f);
        } else {
            if (reloadTimer < .25f){
                if ((int)(reloadTimer * 20) % 2 == 0 )
                batch.setColor(Color.RED);
            }
            batch.draw(tank.body, x, y, halfX, halfY, width, height, 1, 1, rotation);
            batch.draw(tank.turret, x, y, halfX, halfY, width, height, 1, 1, turretRotation - 90);
            batch.setColor(Color.WHITE);
            if (killingIt) {
                batch.draw(tank.dead, x, y, halfX, halfY, width, height, 1, 1, rotation);
                batch.draw(tank.deadTurret, x, y, halfX, halfY, width, height, 1, 1, turretRotation - 90);
                batch.draw(LudumDare41.game.assets.explosionAnimation.getKeyFrame(explodeAnimTime),
                           x - halfX, y - halfY, halfX, halfY,
                           width * 2f, height * 2f, 1f, 1f, 0f);
            }
        }
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
            speed = (onSand) ? MAX_SPEED * 0.5f : MAX_SPEED;
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

    public void shoot(float dt){
        reloadTimer -= dt;
        if (reloadTimer > 0){
            return;
        }
        reloadTimer += reloadDelay;
        directionVector.set(0, 1);
        directionVector.setAngle(turretRotation + MathUtils.random(-10f, 10f));

        tempVec.set(position).add(directionVector.scl(30));
        directionVector.nor();
        screen.particleSystem.addBarrelSmoke(tempVec.x, tempVec.y, directionVector.x, directionVector.y);

        LudumDare41.game.audio.playSound(Audio.Sounds.enemy_shot);

        screen.addBullet(this, tempVec, directionVector, bulletTexture );
//        ball.shootBall(tempVector, directionVector);
    }
}
