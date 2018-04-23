package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.screens.GameScreen;
import lando.systems.ld41.utils.Assets;
import lando.systems.ld41.utils.Audio;


public class Catapult extends GameObject {
    public static final float FIRE_RATE = 7;
    public static final float TURRET_WIDTH = 50;
    public static final float TURRET_HEIGHT = 50;

    public Vector2 directionVector;
    public float rotation;
    public Tank playerTank;
    public boolean killingIt = false;
    private float explodeAnimTime = 0f;
    private TextureRegion catapultFrame;
    private TextureRegion smokeFrame;
    private float timer = 0;
    private GameScreen screen;
    private Vector2 bulletPosition;

    private Vector2 tempVec;


    public Catapult(GameScreen screen, Tank playerTank, Vector2 startPosition){
        this.screen = screen;
        this.position = startPosition;
        this.playerTank = playerTank;
        this.bulletPosition = new Vector2();
        this.bulletSpeed = 100;
        this.bulletSize = 15f;
        this.bulletTimeToLive = 10f;
        this.directionVector = new Vector2();
        this.tempVec = new Vector2();
        this.catapultFrame = LudumDare41.game.assets.catapultAnimation.getKeyFrame(0);
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
        if (playerTank.isFirstBallFired) {
            timer += dt;
        }
        if (!alive) {
            smokeFrame = LudumDare41.game.assets.smokeAnimation.getKeyFrame(timer);
        } else {
            rotation = (float)(Math.atan2(
                    playerTank.position.y - position.y,
                    playerTank.position.x - position.x) * 180 / Math.PI);
        }
        if (playerTank.isFirstBallFired && !playerTank.dead && alive
                && timer > FIRE_RATE
                && screen.level.canSeeBetween(position, playerTank.position)) {
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
        if (!alive || killingIt) return;
        bulletPosition.set(position.x, position.y);
        tempVec.set(playerTank.position.x - position.x, playerTank.position.y - position.y);
        screen.addBullet(this, bulletPosition, tempVec, Assets.getImage(Assets.Balls.Purple));
        LudumDare41.game.audio.playSound(Audio.Sounds.enemy_shot);

    }

    @Override
    public void render(SpriteBatch batch){
        float halfX = TURRET_WIDTH / 2;
        float halfY = TURRET_HEIGHT / 2;
        float x = position.x - halfX;
        float y = position.y - halfY;
        if (!alive) {
            batch.draw(LudumDare41.game.assets.catapultAnimation.getKeyFrame(2), x, y, halfX, halfY, TURRET_WIDTH, TURRET_HEIGHT, 1, 1, rotation - 90);
            batch.draw(smokeFrame, x, y, halfX, halfY, TURRET_WIDTH, TURRET_HEIGHT, 1, 1, 0);
        } else {
            float warning = FIRE_RATE - timer;
            if (warning < .25f){
                if ((int)(warning * 20) % 2 == 0 )
                    batch.setColor(Color.RED);
            }
            batch.draw(catapultFrame,x, y, halfX, halfY , TURRET_WIDTH, TURRET_HEIGHT, 1, 1, rotation - 90);
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
