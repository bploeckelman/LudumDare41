package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.screens.GameScreen;

public class Bullet {

    private static int BULLET_WIDTH = 15;
    private static int BULLET_HEIGHT = 15;
    public Vector2 velocity;
    public float radius;
    private GameScreen screen;
    public Vector2 position;
    private Vector2 oldPosition;
    private Vector2 newPosition;
    private Vector2 collisionPoint;
    private Vector2 normal;
    public Vector2 directionVector = new Vector2();
    public boolean alive = true;
    private Tank playerTank;




    public Bullet(GameScreen screen, Tank playerTank, float x, float y, float targetX, float targetY) {
        this.screen = screen;
        position = new Vector2( x , y );
        velocity = new Vector2( 0 , 0 );
        velocity.set(targetX - position.x, targetY - position.y);
        oldPosition = new Vector2();
        newPosition = new Vector2();
        collisionPoint = new Vector2();
        normal = new Vector2();
        this.radius = Math.max(BULLET_WIDTH, BULLET_HEIGHT)/2f;
        this.playerTank = playerTank;

    }

    public void update(float dt) {
        directionVector.set(1, 0);
        oldPosition.set(position);
        newPosition.set(position);
        newPosition.add(velocity.nor().x * 100 * dt, velocity.nor().y * 100 * dt);
        if (screen.level.checkCollision(oldPosition, newPosition, radius, collisionPoint, normal) || checkCollision(playerTank)) {
            alive = false;
        } else {
            position.set(newPosition);
        }
    }

    public void render(SpriteBatch batch){
        batch.draw(LudumDare41.game.assets.whitePixel, position.x, position.y, position.x, position.y , BULLET_WIDTH, BULLET_HEIGHT, 1, 1, 0);
    }

    public boolean checkCollision(Tank tank){
        if (position.dst(tank.position) < radius + tank.radius){
            return true;
        } else {
            return false;
        }
    }
}

