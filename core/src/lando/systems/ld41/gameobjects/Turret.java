package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.*;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.screens.GameScreen;


public class Turret {
    public static float FIRE_RATE = 200;
    public static float TURRET_WIDTH = 30;
    public static float TURRET_HEIGHT = 30;

    public Array<Bullet> activeBullets = new Array<Bullet>();

    public Vector2 position;
    public Vector2 directionVector;
    public int width;
    public int height;
    public float rotation;
    public Bullet newBullet;
    public Tank playerTank;
    public boolean alive = true;
    private int timer = 0;
    private GameScreen screen;

    public Turret(GameScreen screen, Tank playerTank, int width, int height, Vector2 startPosition){
        this.screen = screen;
        this.position = startPosition;
        this.width = width;
        this.height = height;
        this.playerTank = playerTank;
        directionVector = new Vector2();
    }

    public void init(Tank playerTank){
    }

    public void update(float dt){
        timer++;
        if (alive && timer > FIRE_RATE) {
            fireBullet();
            timer = 0;
        }

        for (Bullet bullet : activeBullets) {
            if (bullet.alive) {
                bullet.update(dt);
            } else {
                activeBullets.removeValue(bullet, true);
            }
        }
    }
    private void fireBullet() {
        newBullet = new Bullet(screen, playerTank, position.x, position.y, playerTank.position.x, playerTank.position.y);
        activeBullets.add(newBullet);
    }

    public void render(SpriteBatch batch){
        batch.draw(LudumDare41.game.assets.testTexture, position.x, position.y, TURRET_WIDTH/2, TURRET_HEIGHT/2 , TURRET_WIDTH, TURRET_HEIGHT, 1, 1, rotation);
        for (Bullet bullet : activeBullets) {
            bullet.render(batch);
        }
    }
}
