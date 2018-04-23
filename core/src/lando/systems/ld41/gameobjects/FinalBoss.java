package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.ai.StateMachine;
import lando.systems.ld41.ai.Transition;
import lando.systems.ld41.ai.conditions.HealthBelowCondition;
import lando.systems.ld41.ai.conditions.PlayerCloserThan;
import lando.systems.ld41.ai.conditions.PlayerFurtherThan;
import lando.systems.ld41.ai.states.*;
import lando.systems.ld41.screens.GameScreen;
import lando.systems.ld41.utils.Assets;

public class FinalBoss extends GameObject {

    private float width;
    private float height;
    private TextureRegion bodytexture;
    private TextureRegion turretTexture;
    public float health;
    float smallTurretSize;
    float mainTurretSize;
    Vector2 leftTurretOffset;
    Vector2 rightTurretOffset;
    public float mainTurretRotation;
    float immunityDelay;
    StateMachine stateMachine;

    public Vector2 directionVector;
    public Vector2 oldPosition;
    public Vector2 newPosition;
    public Vector2 collisionPoint;
    private Vector2 normal;
    private Vector2 tempVec;
    private Vector2 tempVec2;

    public FinalBoss(GameScreen screen, float x, float y) {
        this.screen = screen;
        this.position = new Vector2(x, y);
        this.width = 128f;
        this.height = 128f;
        this.radius = 64;
        this.health = 4;
        this.smallTurretSize = 64;
        this.mainTurretSize = 128;
        this.mainTurretRotation = 180;
        this.leftTurretOffset = new Vector2(-40, -40);
        this.rightTurretOffset = new Vector2( 40, -40);
        this.bodytexture = LudumDare41.game.assets.tanks.get("greentank");
        this.turretTexture = LudumDare41.game.assets.tanks.get("greentankturret");
        rotation = 180;
        directionVector = new Vector2();
        oldPosition = new Vector2();
        newPosition = new Vector2();
        collisionPoint= new Vector2();
        normal = new Vector2();
        tempVec = new Vector2();
        tempVec2 = new Vector2();
        this.bouncyBullets = true;
        this.bulletTimeToLive = 5;

        initializeStates();
    }


    public void initializeStates(){
        WaitState wait = new WaitState(this);
        BossFullHealthState fullHeath = new BossFullHealthState(this);
        BossOneTurretState oneTurretState = new BossOneTurretState(this);
        BossNoTurretState noTurretState = new BossNoTurretState(this);
        BossClownState clownState = new BossClownState(this);
        BossDeadState deadState = new BossDeadState(this);

        PlayerCloserThan beginAssault = new PlayerCloserThan(this, 500);
        HealthBelowCondition below4 = new HealthBelowCondition(this, 4);
        HealthBelowCondition below3 = new HealthBelowCondition(this, 3);
        HealthBelowCondition below2 = new HealthBelowCondition(this, 2);
        HealthBelowCondition below1 = new HealthBelowCondition(this, 1);

        Array<Transition> transitions = new Array<Transition>();
        transitions.add(new Transition(wait, beginAssault,fullHeath));
        transitions.add(new Transition(fullHeath, below4, oneTurretState));
        transitions.add(new Transition(oneTurretState, below3, noTurretState));
        transitions.add(new Transition(noTurretState, below2, clownState));
        transitions.add(new Transition(clownState, below1, deadState));


        stateMachine = new StateMachine(wait, transitions);
    }

    @Override
    public void update(float dt) {
        immunityDelay = Math.max(immunityDelay - dt, 0);
        if (screen.playerTank.isFirstBallFired){
            stateMachine.update(dt);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        leftTurretOffset.set(-40, 40).rotate(rotation);
        rightTurretOffset.set( 40, 40).rotate(rotation);

        batch.draw(bodytexture, position.x - width/2f, position.y - height/2, width/2, height/2, width, height, 1, 1, rotation);
        if (health > 3) {
            batch.draw(turretTexture, position.x + leftTurretOffset.x - smallTurretSize / 2, position.y + leftTurretOffset.y - smallTurretSize / 2, smallTurretSize / 2f, smallTurretSize / 2f, smallTurretSize, smallTurretSize, 1, 1, rotation);
        }
        if (health > 2) {
            batch.draw(turretTexture, position.x + rightTurretOffset.x - smallTurretSize / 2, position.y + rightTurretOffset.y - smallTurretSize / 2, smallTurretSize / 2f, smallTurretSize / 2f, smallTurretSize, smallTurretSize, 1, 1, rotation);
        }
        if (health > 1) {
            batch.draw(turretTexture, position.x - mainTurretSize / 2, position.y - mainTurretSize / 2, mainTurretSize / 2f, mainTurretSize / 2f, mainTurretSize, mainTurretSize, 1, 1, mainTurretRotation - 90);
        }
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
        if (screen.level.checkCollision(oldPosition, newPosition, radius, collisionPoint, normal ) != Level.CollisionType.None){
            newPosition.set(collisionPoint);
            collision = true;
        }

        position.set(newPosition);

        return collision;
    }

    public boolean moveTo(Vector2 targetPos, float dt, float speed, float rotationSpeed){
        float angleToLerp = (float)(Math.atan2(
                targetPos.y - position.y,
                targetPos.x - position.x) * 180 / Math.PI) - 90;
        if (angleToLerp - rotation < -180) rotation -= 360;
        if (angleToLerp - rotation > 180) rotation += 360;
        float amountToRotate = Math.abs(angleToLerp - rotation);
        if (angleToLerp != rotation){
            float rotationAmount = rotationSpeed * dt;
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

    public void takeDamage(){
        if (immunityDelay > 0) return;
        health --;
    }

    public void shootLeftTurret(){
        directionVector.set(32, 0);
        directionVector.setAngle(rotation + 90);


        tempVec.set(position.x+leftTurretOffset.x + directionVector.x, position.y+leftTurretOffset.y + directionVector.y);
        directionVector.rotate(MathUtils.random(-10, 10));
        screen.addBullet(this, tempVec, directionVector, Assets.getImage(Assets.Balls.Purple));
    }

    public void shootRightTurret(){
        directionVector.set(32, 0);
        directionVector.setAngle(rotation + 90);

        tempVec.set(position.x+rightTurretOffset.x + directionVector.x, position.y+rightTurretOffset.y + directionVector.y);
        directionVector.rotate(MathUtils.random(-10, 10));
        screen.addBullet(this, tempVec, directionVector, Assets.getImage(Assets.Balls.Purple));
    }

    public void shootMainGun(){
        directionVector.set(64,0).rotate(mainTurretRotation);
        tempVec.set(position.x + directionVector.x, position.y + directionVector.y);
        directionVector.rotate(MathUtils.random(-10, 10));
        screen.addBullet(this, tempVec, directionVector, Assets.getImage(Assets.Balls.White));
    }

    public void shootClown(){
        float startAngle = MathUtils.random(360);
        int totalBullets = 10;
        float dTheta = 360f/totalBullets;
        directionVector.set(1,0).rotate(startAngle);

        for (int i = 0; i < totalBullets; i++){
            directionVector.rotate(dTheta);
            tempVec.set(position.x + directionVector.x, position.y + directionVector.y);
            screen.addBullet(this, tempVec, directionVector, Assets.getImage(Assets.Balls.White));
        }
    }

}
