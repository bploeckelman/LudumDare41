package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.screens.GameScreen;
import lando.systems.ld41.utils.TankAssets;

public class Tank extends GameObject {
    public float speed = 200;
    public static float rotationSpeed = 120;
    private final float TRACK_OFFSET = 20f;

    private Vector3 camera = new Vector3();
    public Vector2 position;
    private Vector2 oldPosition;
    private Vector2 newPosition;
    private Vector2 collisionPoint;
    private Vector2 normal;
    public float radius;
    public float rotation;
    public float turretRotation;
    public Vector2 directionVector = new Vector2();
    public Vector2 tempVector;

    private TankAssets tank;
    private TextureRegion leftTread;
    private TextureRegion rightTread;
    private float leftTime;
    private float rightTime;

    private float width;
    private float height;
    private GameScreen screen;
    public Ball ball;

    public Tank(GameScreen screen) {
        this(screen, "browntank", 60, 60, new Vector2(100, 100));
    }

    public Tank(GameScreen screen, String tankName, float width, float height, Vector2 startPosition) {
        tank = TankAssets.getTankAssets(tankName);

        this.width = width;
        this.height = height;
        this.radius = Math.max(width, height)/2f;
        position = startPosition;
        oldPosition = new Vector2();
        newPosition = new Vector2();
        collisionPoint = new Vector2();
        normal = new Vector2();
        this.screen = screen;
        ball = new Ball(screen);
        tempVector = new Vector2();
    }

    @Override
    public void update(float dt){
        if (Gdx.input.isKeyPressed(Input.Keys.A)){
            rotation += rotationSpeed*dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)){
            rotation -= rotationSpeed*dt;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)){
            leftTime += dt;
            rightTime += dt;
            updatePosition(speed*dt);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)){
            leftTime -= dt;
            rightTime -= dt;
            updatePosition(-speed*dt);
        }

        leftTread = tank.leftTreads.getKeyFrame(leftTime, true);
        rightTread = tank.rightTreads.getKeyFrame(rightTime, true);

        ball.update(dt);
        ball.checkCollision(this);
        if (ball.onTank) {
            setTurretRotation();
        }
    }

    private void updatePosition(float speed) {
        // Don't move if you have the ball
        if (ball.onTank) return;

        //TODO make this use up stopped velocity so it can slide along edges found later in the boundary
        directionVector.set(1, 0);
        directionVector.setAngle(rotation + 90);
        directionVector.scl(speed);
        oldPosition.set(position);
        newPosition.set(position);
        newPosition.add(directionVector);

        if (screen.level.checkCollision(oldPosition, newPosition, radius, collisionPoint, normal )){
            newPosition.set(collisionPoint);
        }

        position.set(newPosition);

        // Add track trail for both left and right tracks
        float yTrackOffset = MathUtils.sinDeg(rotation) * TRACK_OFFSET;
        float xTrackOffset = MathUtils.cosDeg(rotation) * TRACK_OFFSET;
        
        screen.addTireTrack(position.x - xTrackOffset, position.y - yTrackOffset, 1f);
        screen.addTireTrack(position.x + xTrackOffset, position.y + yTrackOffset, 1f);
    }

    private void setTurretRotation() {
        camera.set(Gdx.input.getX(), Gdx.input.getY(), 0);

        LudumDare41.game.screen.worldCamera.unproject(camera);

        turretRotation = (float)(Math.atan2(
                camera.y - position.y,
                camera.x - position.x) * 180 / Math.PI);
    }

    @Override
    public void render(SpriteBatch batch){
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

        if (ball.onTank){
            directionVector.set(0, 1);
            directionVector.setAngle(turretRotation);

            tempVector.set(position).add(directionVector.scl(30));
            batch.draw(LudumDare41.game.assets.whiteCircle, position.x + directionVector.x - 5, position.y + directionVector.y -5,  5, 5, 10, 10, 1, 1, turretRotation - 90);
        }
        ball.render(batch);
    }

    public void shootBall(float power){
        directionVector.set(0, 1);
        directionVector.setAngle(turretRotation);

        tempVector.set(position).add(directionVector.scl(30));
        directionVector.nor();

        directionVector.scl(20 + (5 * power));

        ball.shootBall(tempVector, directionVector);
    }
}
