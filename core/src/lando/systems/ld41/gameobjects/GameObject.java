package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.screens.GameScreen;

public abstract class GameObject {

    public Vector2 position;
    public GameScreen screen;
    public float rotation;
    public float speed;



    public abstract void update(float dt);
    public abstract void render(SpriteBatch batch);

}
