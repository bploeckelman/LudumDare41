package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class GameObject {

    public abstract void update(float dt);
    public abstract void render(SpriteBatch batch);

}
