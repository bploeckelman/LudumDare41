package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.screens.GameScreen;
import lando.systems.ld41.utils.Assets;
import lando.systems.ld41.utils.Audio;

public class EnemyTurret extends GameObject{
    public static final float FIRE_RATE = 3;
    public static final float TURRET_WIDTH = 50;
    public static final float TURRET_HEIGHT = 50;

    public Vector2 directionVector;
    public float rotation;
    public Tank playerTank;
    public boolean killingIt = false;
    private float explodeAnimTime = 0f;
    private TextureRegion enemyTurretFrame;
    private TextureRegion smokeFrame;
    private float timer = 0;
    private GameScreen screen;
    private Vector2 bulletPosition;


    public EnemyTurret(GameScreen screen, Tank playerTank, Vector2 startPosition, Vector2 direction){
        this.screen = screen;
        this.position = startPosition;
        this.playerTank = playerTank;
        this.bulletPosition = new Vector2();
        this.bulletSpeed = 100;
        this.bulletSize = 15f;
        this.directionVector = new Vector2(direction);
        this.enemyTurretFrame = LudumDare41.game.assets.enemyTurret;
        this.radius = Math.max(TURRET_WIDTH, TURRET_HEIGHT)/2f;
    }

    public void kill() {
        if (killingIt) return;
        screen.screenShake.addDamage(.4f);
        LudumDare41.game.audio.playSound(Audio.Sounds.explosion);
        killingIt = true;
        explodeAnimTime = 0f;
    }

    @Override
    public void update(float dt){
        if (killingIt) {
            explodeAnimTime += dt;
            if (explodeAnimTime >= LudumDare41.game.assets.explosionAnimation.getAnimationDuration()) {
                killingIt = false;
                alive = false;
            }
        }

        timer+=dt;
        if (!alive) {
            smokeFrame = LudumDare41.game.assets.smokeAnimation.getKeyFrame(timer);
        }
        if (playerTank.isFirstBallFired && !playerTank.dead && alive
                && timer > FIRE_RATE) {
            updateBullet(dt);
            timer = 0;
        }
        if (timer < 1) {
            enemyTurretFrame = LudumDare41.game.assets.enemyTurretRecoil;
        } else {
            enemyTurretFrame = LudumDare41.game.assets.enemyTurret;
        }
    }

    public void updateBullet(float dt) {
        if (!alive || killingIt) return;
        float startXPosition = position.x + directionVector.x * 40f;
        float startYPosition = position.y + directionVector.y * 40f;
        Vector2 startPosition = new Vector2(startXPosition, startYPosition);


        bulletPosition = startPosition;
        screen.addBullet(this, bulletPosition, directionVector, Assets.getImage(Assets.Balls.Purple));
        LudumDare41.game.audio.playSound(Audio.Sounds.enemy_shot);

    }

    @Override
    public void render(SpriteBatch batch){
        float halfX = TURRET_WIDTH / 2;
        float halfY = TURRET_HEIGHT / 2;
        float x = position.x - halfX;
        float y = position.y - halfY;
        if (!alive) {
            batch.draw(enemyTurretFrame, x, y, halfX, halfY, TURRET_WIDTH, TURRET_HEIGHT, 2, 2, rotation);
            batch.draw(smokeFrame, x, y, halfX, halfY, TURRET_WIDTH, TURRET_HEIGHT, 1, 1, 0f);
        } else {
            float warning = FIRE_RATE - timer;
            if (warning < .25f){
                if ((int)(warning * 20) % 2 == 0)
                    batch.setColor(Color.RED);
            }
            batch.draw(enemyTurretFrame,x, y, halfX, halfY , TURRET_WIDTH, TURRET_HEIGHT, 2, 2, rotation);
            batch.setColor(Color.WHITE);
            if (killingIt) {
                batch.draw(LudumDare41.game.assets.explosionAnimation.getKeyFrame(explodeAnimTime),
                           x - halfX, y - halfY, halfX, halfY,
                           TURRET_WIDTH * 2f, TURRET_HEIGHT * 2f,
                           1f, 1f, 0f);
            }
        }
    }

}
