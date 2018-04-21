package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.screens.GameScreen;

public class Ball {

    public boolean onTank;
    public Vector2 position;
    private Vector2 oldPosition;
    private Vector2 newPosition;
    private Vector2 collisionPoint;
    private Vector2 normal;
    private Vector2 tempVector;
    public Vector2 velocity;
    public float radius;
    public float pickupDelay;
    private GameScreen screen;

    public Ball(GameScreen screen){
        this.screen = screen;
        onTank = true;
        position = new Vector2();
        velocity = new Vector2();

        oldPosition = new Vector2();
        newPosition = new Vector2();
        collisionPoint = new Vector2();
        normal = new Vector2();
        tempVector = new Vector2();

        radius = 5;
    }

    public void update(float dt){
        if (onTank) return;
        pickupDelay = Math.max(pickupDelay - dt, 0);

        oldPosition.set(position);
        newPosition.set(position);
        newPosition.add(velocity.x * dt, velocity.y * dt);


        if (screen.level.checkCollision(oldPosition, newPosition, radius, collisionPoint, normal )){
            float currentSpeed = velocity.len();
            tempVector.set(newPosition.x - oldPosition.x, newPosition.y - oldPosition.y);
            // r=d−2(d⋅n)n
            float dot = 2f * tempVector.dot(normal);
            tempVector.sub(dot * normal.x, dot * normal.y);
            velocity.set(tempVector).nor().scl(currentSpeed);
            newPosition.set(collisionPoint);
        }

        position.set(newPosition);

        velocity.scl(.99f);
    }

    public void render(SpriteBatch batch){
        if (onTank) return;

        batch.draw(LudumDare41.game.assets.whiteCircle, position.x -radius, position.y-radius, radius*2, radius*2);
    }

    public void shootBall(Vector2 position, Vector2 velocity){
        this.position.set(position);
        this.velocity.set(velocity);
        pickupDelay = 2f;
        onTank = false;
    }

    public void checkCollision(Tank tank){
        if (onTank || pickupDelay > 0 || velocity.len() > 50) return;
        if (position.dst(tank.position) < radius + tank.radius){
            onTank = true;
        }
    }
}
