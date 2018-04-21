package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld41.LudumDare41;

public class Tank {
    public float speed = 200;
    public static float rotationSpeed = 120;

    private Vector3 camera = new Vector3();
    public Vector2 position;
    public float rotation;
    public float turretRotation;
    public Vector2 directionVector = new Vector2();

    private TextureRegion body;
    private TextureRegion turret;

    private float width;
    private float height;

    public Tank() {
        this("browntank", 60, 60, new Vector2(100, 100));
    }

    public Tank(String image, float width, float height, Vector2 startPosition) {
        body = LudumDare41.getImage(image + "body");
        turret = LudumDare41.getImage(image + "turret");

        this.width = width;
        this.height = height;

        position = startPosition;
    }

    public void update(float dt){
        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            rotation += rotationSpeed*dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)){
            rotation -= rotationSpeed*dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)){
            updatePosition(speed*dt);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)){
            updatePosition(-speed*dt);
        }

        setTurretRotation();
    }

    private void updatePosition(float speed) {
        directionVector.set(1, 0);
        directionVector.setAngle(rotation + 90);
        directionVector.scl(speed);
        position.add(directionVector);
    }

    private void setTurretRotation() {
        camera.set(Gdx.input.getX(), Gdx.input.getY(), 0);

        LudumDare41.game.screen.worldCamera.unproject(camera);

        turretRotation = (float)(Math.atan2(
                camera.y - position.y,
                camera.x - position.x) * 180 / Math.PI) - 90;
    }

    public void render(SpriteBatch batch){
        batch.draw(body, position.x - width/2, position.y - height/2, width/2, height/2, width, height, 1, 1, rotation);
        batch.draw(turret, position.x - width/2, position.y - height/2, width/2, height/2 , width, height, 1, 1, turretRotation);
    }
}
