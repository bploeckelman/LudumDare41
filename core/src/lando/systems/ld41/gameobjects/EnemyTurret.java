package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.screens.GameScreen;
import lando.systems.ld41.utils.Assets;

public class EnemyTurret extends GameObject{
    public final float FIRE_RATE = 3;
    public final float TURRET_WIDTH = 50;
    public final float TURRET_HEIGHT = 50;

    public Vector2 position;
    public Vector2 directionVector;
    public float rotation;
    public Tank playerTank;
    public boolean alive = true;
    private TextureRegion enemyTurretFrame;
    private TextureRegion smokeFrame;
    private float timer = 0;
    private GameScreen screen;
    private Vector2 bulletPosition;
    public float radius;
    private Vector2 tempVec;
    public Vector2 targetPosition;


    public EnemyTurret(GameScreen screen, Tank playerTank, Vector2 startPosition, Vector2 targetPosition){
        this.screen = screen;
        this.position = startPosition;
        this.playerTank = playerTank;
        this.bulletPosition = new Vector2();
        this.bulletSpeed = 100;
        this.bulletSize = 15f;
        directionVector = new Vector2();
        tempVec = new Vector2();
        enemyTurretFrame = LudumDare41.game.assets.enemyTurret;
        this.radius = Math.max(TURRET_WIDTH, TURRET_HEIGHT)/2f;
        this.targetPosition = targetPosition;

    }
    @Override
    public void update(float dt){
        timer+=dt;
        if (!alive) {
            smokeFrame = LudumDare41.game.assets.smokeAnimation.getKeyFrame(timer);
        } else {
            rotation = (float)(Math.atan2(
                    targetPosition.y - position.y,
                    targetPosition.x - position.x) * 180 / Math.PI);
        }
        if (playerTank.isFirstBallFired && !playerTank.dead && alive
                && timer > FIRE_RATE) {
            updateBullet(dt);
            timer = 0;
        }

    }

    public void updateBullet(float dt) {
        bulletPosition.set(position.x, position.y);
        tempVec.set(targetPosition.x - position.x, targetPosition.y - position.y);
        screen.addBullet(this, bulletPosition, tempVec, Assets.getImage(Assets.Balls.Purple));
    }

    @Override
    public void render(SpriteBatch batch){
        float halfX = TURRET_WIDTH / 2;
        float halfY = TURRET_HEIGHT / 2;
        float x = position.x - halfX;
        float y = position.y - halfY;
        if (!alive) {
            batch.draw(enemyTurretFrame, x, y, halfX, halfY, TURRET_WIDTH, TURRET_HEIGHT, 2, 2, rotation - 90);
            batch.draw(smokeFrame, x, y, halfX, halfY, TURRET_WIDTH, TURRET_HEIGHT, 1, 1, rotation - 90);
        } else {
            float warning = FIRE_RATE - timer;
            if (warning < .25f){
                if ((int)(warning * 20) % 2 == 0)
                    batch.setColor(Color.RED);
            }
            batch.draw(enemyTurretFrame,x, y, halfX, halfY , TURRET_WIDTH, TURRET_HEIGHT, 2, 2, rotation - 90);
            batch.setColor(Color.WHITE);
        }
    }
}
