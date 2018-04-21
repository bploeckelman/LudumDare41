package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.LudumDare41;

public class Tank {
    public static float SPEED = 200;
    public static float ROTATIONSPEED = 120;

    public Vector2 position;
    public float rotation;
    public Vector2 directionVector;

    public Tank(){
        position = new Vector2(100, 100);
        directionVector = new Vector2();
    }

    public void update(float dt){
        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            rotation += ROTATIONSPEED*dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)){
            rotation -= ROTATIONSPEED*dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)){
            directionVector.set(1, 0);
            directionVector.setAngle(rotation);
            directionVector.scl(SPEED * dt);
            position.add(directionVector);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)){
            directionVector.set(1, 0);
            directionVector.setAngle(rotation);
            directionVector.scl(-SPEED * dt);
            position.add(directionVector);
        }
    }

    public void render(SpriteBatch batch){
        batch.draw(LudumDare41.game.assets.testTexture, position.x, position.y, 15, 15 , 30, 30, 1, 1, rotation);
    }
}
