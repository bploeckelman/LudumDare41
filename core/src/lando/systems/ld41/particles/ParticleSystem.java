package lando.systems.ld41.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.utils.Utils;

public class ParticleSystem {
    private final Array<Particle> activeParticles = new Array<Particle>();
    private final Array<Particle> activeGroundParticles = new Array<Particle>();
    private final Pool<Particle> particlePool = Pools.get(Particle.class, 500);

    private Color color;

    public ParticleSystem() {
        color = new Color();
    }

    public void addBarrelSmoke(float x, float y, float dx, float dy){
        int smokeParticles = 50;
        for (int i = 0; i < smokeParticles; i++){
            Particle particle = particlePool.obtain();
            float posX = x + MathUtils.random(-10f, 10f);
            float posY = y + MathUtils.random(-10f, 10f);

            float velX = dx * MathUtils.random(2, 10f) + MathUtils.random(-20f, 20f);
            float velY = dy * MathUtils.random(2, 10f) + MathUtils.random(-20f, 20f);
            float scale = MathUtils.random(5f, 20f);
            float ttl = MathUtils.random(.5f, 1.5f);
            float grayValue = MathUtils.random(.7f) + .3f;

            particle.init(posX, posY, velX, velY, -velX, -velY,
                    0.5f, grayValue, grayValue, grayValue, 1f,
                    grayValue, grayValue, grayValue, 0f, scale, ttl, LudumDare41.game.assets.smoke);
            activeParticles.add(particle);
        }
    }

    public void addBarrelSparks(float x, float y, float dx, float dy){
        int sparkParticles = 200;
        for (int i = 0; i < sparkParticles; i++){
            Particle particle = particlePool.obtain();
            float posX = x;
            float posY = y;

            float velX = dx * MathUtils.random(40, 100f) + MathUtils.random(-30f, 30f);
            float velY = dy * MathUtils.random(40, 100f) + MathUtils.random(-30f, 30f);
            float scale = MathUtils.random(.5f, 1.3f);
            float ttl = MathUtils.random(.5f, 1f);
            Utils.hsvToRgb(MathUtils.random(1f), 1, 1, color);

            particle.init(posX, posY, velX, velY, -velX, -velY,
                    0.5f, color.r, color.g, color.b, 1f,
                    color.r, color.g, color.b, 1f, scale, ttl, LudumDare41.game.assets.whitePixel);
            activeParticles.add(particle);
        }
    }

    public void addTracks(float x, float y, float speed)
    {
        for (int dX = -2; dX < 2; dX++)
        {
            for (int dY = -1; dY < speed; dY++)
            {
                Particle particle = particlePool.obtain();
                float posX = x + dX;
                float posY = y + dY;

                float velX = 0;
                float velY = 0;
                float scale = MathUtils.random(1f, 2f);
                float ttl = MathUtils.random(.5f, 2f);
                float grayValue = MathUtils.random(.3f);

                particle.init(posX, posY, velX, velY, -velX, -velY,
                              0.5f, grayValue, grayValue, grayValue, 1f,
                              grayValue, grayValue, grayValue, 0f, scale, ttl, LudumDare41.game.assets.whitePixel);
                activeGroundParticles.add(particle);
            }
        }
    }

    public void update(float dt)
    {
        int len = activeParticles.size;
        for (int i = len - 1; i >= 0; i--)
        {
            Particle part = activeParticles.get(i);
            part.update(dt);
            if (part.timeToLive <= 0)
            {
                activeParticles.removeIndex(i);
                particlePool.free(part);
            }
        }

        len = activeGroundParticles.size;
        for (int i = len - 1; i >= 0; i--)
        {
            Particle part = activeGroundParticles.get(i);
            part.update(dt);
            if (part.timeToLive <= 0)
            {
                activeGroundParticles.removeIndex(i);
                particlePool.free(part);
            }
        }
    }

    public void renderGround(SpriteBatch batch)
    {
        for (Particle part : activeGroundParticles)
        {
            part.render(batch);
        }
    }

    public void render(SpriteBatch batch)
    {
        batch.enableBlending();
        for (Particle part : activeParticles)
        {
            part.render(batch);
        }
    }
}
