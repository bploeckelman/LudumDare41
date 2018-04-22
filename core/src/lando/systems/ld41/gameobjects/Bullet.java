package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.LudumDare41;

public class Bullet {

    private final int BULLET_WIDTH = 30;
    private final int BULLET_HEIGHT = 30;
    public int bulletVelocityMultiplier;
    public Vector2 velocity;
    public float radius;
    public Vector2 position;
    public Catapult owner;
    public boolean alive = true;
    public TextureRegion texture;

    public Bullet() {
        position = new Vector2();

    }

    public void init(Tank playerTank, Vector2 pos, int bulletVelocityMultiplier, Catapult owner, TextureRegion tex){
        position.set(pos);
        velocity = new Vector2();
        velocity.set(playerTank.position.x - position.x, playerTank.position.y - position.y);
        velocity.nor();
        this.bulletVelocityMultiplier = bulletVelocityMultiplier;
        this.owner = owner;
        this.texture = tex;
        alive = true;
    }

    public void update(float dt) {
        position.add(velocity.x * bulletVelocityMultiplier * dt, velocity.y * bulletVelocityMultiplier * dt);
    }

    public void render(SpriteBatch batch){
        batch.draw(texture, position.x - BULLET_WIDTH/2f, position.y - BULLET_HEIGHT/2f, BULLET_WIDTH/2, BULLET_HEIGHT/2 , BULLET_WIDTH, BULLET_HEIGHT, 1, 1, 0);
    }

    public boolean checkCollision(Tank tank){
        if (position.dst(tank.position) < radius + tank.radius){
            return true;
        } else {
            return false;
        }
    }
}

