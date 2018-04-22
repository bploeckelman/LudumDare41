package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.screens.GameScreen;


public class Catapult extends GameObject {
    public final float FIRE_RATE = 4;
    public final float TURRET_WIDTH = 50;
    public final float TURRET_HEIGHT = 50;

    public Vector2 position;
    public Vector2 directionVector;
    public float rotation;
    public Bullet newBullet;
    public Tank playerTank;
    public boolean alive = true;
    private TextureRegion catapultFrame;
    private float timer = 0;
    private GameScreen screen;
    private Vector2 bulletPosition;



    public Catapult(GameScreen screen, Tank playerTank, Vector2 startPosition){
        this.screen = screen;
        this.position = startPosition;
        this.playerTank = playerTank;
        this.bulletPosition = new Vector2();
        directionVector = new Vector2();
        catapultFrame = LudumDare41.game.assets.catapultAnimation.getKeyFrame(0);

    }
    @Override
    public void update(float dt){
        timer+=dt;
        rotation = (float)(Math.atan2(
                playerTank.position.y - position.y,
                playerTank.position.x - position.x) * 180 / Math.PI);
        if (playerTank.isFirstBallFired && alive && timer > FIRE_RATE) {
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
        batch.draw(catapultFrame, position.x - TURRET_WIDTH/2f, position.y - TURRET_HEIGHT/2f, TURRET_WIDTH/2, TURRET_HEIGHT/2 , TURRET_WIDTH, TURRET_HEIGHT, 1, 1, rotation - 90);
    }
}
