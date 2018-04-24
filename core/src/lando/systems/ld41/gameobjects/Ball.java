package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.screens.GameScreen;
import lando.systems.ld41.utils.Assets;
import lando.systems.ld41.utils.Audio;

public class Ball extends GameObject {

    public boolean onTank;
    private Vector2 oldPosition;
    private Vector2 newPosition;
    private Vector2 collisionPoint;
    private Vector2 normal;
    private Vector2 tempVector;
    private Vector3 tempVector3;
    public Vector2 velocity;
    public float radius;
    public float pickupDelay;
    private GameScreen screen;

    public TextureRegion image;
    public boolean visible = true;
    public boolean onSand = false;
    public boolean onWater = false;

    private Indicator indicator;

    public Ball(GameScreen screen, String ballImage){
        this.screen = screen;
        setImage(ballImage);

        onTank = true;
        position = new Vector2();
        velocity = new Vector2();

        oldPosition = new Vector2();
        newPosition = new Vector2();
        collisionPoint = new Vector2();
        normal = new Vector2();
        tempVector = new Vector2();
        tempVector3 = new Vector3();

        radius = 8;

        indicator = new Indicator(this, 10);
    }

    public void setImage(String ballImage) {
        this.image = Assets.getImage(ballImage);
    }

    public void update(float dt){
        if (onTank || !visible) return;
        pickupDelay = Math.max(pickupDelay - dt, 0);

        if (onSand) velocity.scl(0.95f);

        oldPosition.set(position);
        newPosition.set(position);
        newPosition.add(velocity.x * dt, velocity.y * dt);

        Level.CollisionType collision = screen.level.checkCollision(oldPosition, newPosition, radius, collisionPoint, normal);
        if ((collision != Level.CollisionType.None && collision != Level.CollisionType.Water) || checkCollisionWithEnemies()){
            if (collision == Level.CollisionType.Bumper) {
                velocity.scl(1.3f);
                screen.screenShake.addDamage(.2f);
                LudumDare41.game.audio.playSound(Audio.Sounds.bumper);
            }
            float currentSpeed = velocity.len();
            tempVector.set(velocity);
            // r=d−2(d⋅n)n
            float dot = 2f * tempVector.dot(normal);
            tempVector.sub(dot * normal.x, dot * normal.y);
            setVelocity(tempVector.nor().scl(currentSpeed));
            newPosition.set(collisionPoint);
        }

        // handle water with slow velocity, sink the ball, make a splash sound, re-drop it at the tee
        float tooSlowVel2 = 5000f;
        if ((collision == Level.CollisionType.Water || onWater) && velocity.len2() < tooSlowVel2) {
            LudumDare41.game.audio.playSound(Audio.Sounds.splash);
            screen.particleSystem.addWaterSplash(position.x, position.y, 0f, 0f);
            newPosition.set(screen.level.tee.pos);
            velocity.set(0f, 0f);
        }

        if (isNotMoving())
        {
            indicator.update(dt);
        }

        position.set(newPosition);

        velocity.scl(.99f);
    }

    public float holeCenterDist;
    public int holeCenterSide;
    public void setVelocity(Vector2 vel) {
        visible = true; // temp
        velocity.set(vel);
        Hole hole = screen.level.hole;
        holeCenterDist = Intersector.distanceLinePoint(position.x, position.y, position.x + vel.x, position.y + vel.y, hole.position.x, hole.position.y);
        holeCenterSide = Intersector.pointLineSide(position.x, position.y, position.x + vel.x, position.y + vel.y, hole.position.x, hole.position.y);
        //System.out.println("dist: " + holeCenterDist + " side: " + holeCenterSide);
    }

    public boolean isInWorldView()
    {
        tempVector3.set(position.x, position.y, 0);
        screen.worldCamera.project(tempVector3);

        return (onTank || (tempVector3.x > 0 && tempVector3.x < screen.hudCamera.viewportWidth && tempVector3.y > 0 && tempVector3.y < screen.hudCamera.viewportHeight));
    }

    private boolean isNotMoving()
    {
        return !onTank && velocity.len() < 30;
    }

    public void render(SpriteBatch batch){
        if (onTank || !visible) return;

        batch.draw(image, position.x -radius, position.y-radius, radius*2, radius*2);

        if (isNotMoving())
        {
            indicator.render(batch);
        }
    }

    public void shootBall(Vector2 position, Vector2 velocity){
        this.position.set(position);
        setVelocity(velocity);
        pickupDelay = 1f;
        onTank = false;
    }

    public void checkCollision(Tank tank){
        if (onTank || pickupDelay > 0) return;
        if (position.dst(tank.position) < radius + tank.radius){
            tank.pickupBall();
        }
    }


    public boolean checkCollisionWithEnemies() {
        if (velocity.len() < 30) return false;
        for(Catapult catapult : screen.catapults) {
            if (catapult.alive && !catapult.killingIt && catapult.position.dst(newPosition) < catapult.radius + radius) {
                normal.set(newPosition);
                normal.sub(catapult.position);
                normal.nor();
                collisionPoint.set(catapult.position);
                normal.scl(catapult.radius + radius);
                collisionPoint.add(normal);
                normal.nor();
                catapult.kill();
                return true;
            }
        }

        for(EnemyTurret turret : screen.enemyTurrets) {
            if (turret.alive && !turret.killingIt && turret.position.dst(newPosition) < turret.radius + radius) {
                normal.set(newPosition);
                normal.sub(turret.position);
                normal.nor();
                collisionPoint.set(turret.position);
                normal.scl(turret.radius + radius);
                collisionPoint.add(normal);
                normal.nor();
                turret.kill();
                return true;
            }
        }

        for (EnemyTank tank : screen.enemyTanks){
            if (!tank.dead && tank.position.dst(newPosition) < tank.radius + radius ) {
                normal.set(newPosition);
                normal.sub(tank.position);
                normal.nor();
                collisionPoint.set(tank.position);
                normal.scl(tank.radius + radius);
                collisionPoint.add(normal);
                normal.nor();
                tank.kill();
                return true;
            }
        }

        FinalBoss boss = screen.boss;
        if (boss != null && boss.position.dst(newPosition) < boss.radius + radius ) {
            normal.set(newPosition);
            normal.sub(boss.position);
            normal.nor();
            collisionPoint.set(boss.position);
            normal.scl(boss.radius + radius);
            collisionPoint.add(normal);
            normal.nor();
            velocity.scl(2f);
            if (velocity.len() < 400){
                velocity.nor().scl(400);
            }
            boss.takeDamage();
            return true;
        }
        return false;
    }
}
