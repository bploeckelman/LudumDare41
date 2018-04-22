package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.screens.GameScreen;


public class Catapult extends GameObject {
    public final float FIRE_RATE = 7;
    public final float TURRET_WIDTH = 50;
    public final float TURRET_HEIGHT = 50;

    public Vector2 position;
    public Vector2 directionVector;
    public float rotation;
    public Tank playerTank;
    public boolean alive = true;
    private TextureRegion catapultFrame;
    private TextureRegion smokeFrame;
    private float timer = 0;
    private GameScreen screen;
    private Vector2 bulletPosition;
    public float radius;



    public Catapult(GameScreen screen, Tank playerTank, Vector2 startPosition){
        this.screen = screen;
        this.position = startPosition;
        this.playerTank = playerTank;
        this.bulletPosition = new Vector2();
        directionVector = new Vector2();
        catapultFrame = LudumDare41.game.assets.catapultAnimation.getKeyFrame(0);
        this.radius = Math.max(TURRET_WIDTH, TURRET_HEIGHT)/2f;

    }
    @Override
    public void update(float dt){
        timer+=dt;
        if (!alive) {
            smokeFrame = LudumDare41.game.assets.smokeAnimation.getKeyFrame(timer);
        } else {
            rotation = (float)(Math.atan2(
                    playerTank.position.y - position.y,
                    playerTank.position.x - position.x) * 180 / Math.PI);
        }
        if (playerTank.isFirstBallFired && !playerTank.dead && alive && timer > FIRE_RATE) {
            updateBullet(dt);
            timer = 0;
        }
        if (timer > 2) {
            catapultFrame = LudumDare41.game.assets.catapultAnimation.getKeyFrame(0);
        }
        else if (timer > 1) {
            catapultFrame = LudumDare41.game.assets.catapultAnimation.getKeyFrame(1);
        }
        else {
            catapultFrame = LudumDare41.game.assets.catapultAnimation.getKeyFrame(2);
        }

    }

    public void updateBullet(float dt) {
        bulletPosition.set(position.x, position.y);
        screen.addBullet(this, 100, bulletPosition, LudumDare41.game.assets.ballOrange);
    }

    @Override
    public void render(SpriteBatch batch){
        float halfX = TURRET_WIDTH / 2;
        float halfY = TURRET_HEIGHT / 2;
        float x = position.x - halfX;
        float y = position.y - halfY;
        if (!alive) {
            batch.draw(LudumDare41.game.assets.catapultAnimation.getKeyFrame(2), x, y, halfX, halfY, TURRET_WIDTH, TURRET_HEIGHT, 1, 1, rotation - 90);
            batch.draw(smokeFrame, x, y, halfX, halfY, TURRET_WIDTH, TURRET_HEIGHT, 1, 1, rotation - 90);
        } else {
            batch.draw(catapultFrame,x, y, halfX, halfY , TURRET_WIDTH, TURRET_HEIGHT, 1, 1, rotation - 90);
        }
    }
}
