package lando.systems.ld41;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import lando.systems.ld41.screens.BaseScreen;
import lando.systems.ld41.screens.TitleScreen;
import lando.systems.ld41.utils.Assets;
import lando.systems.ld41.utils.Audio;
import lando.systems.ld41.utils.Config;
import lando.systems.ld41.utils.accessors.*;

public class LudumDare41 extends ApplicationAdapter {

    public static LudumDare41 game;

    public Audio audio;
    public Assets assets;
    public TweenManager tween;

    public BaseScreen screen;

    @Override
    public void create () {
        LudumDare41.game = this;

        if (audio == null) {
            audio = new Audio();
        }

        if (assets == null) {
            assets = new Assets();
        }

        if (tween == null) {
            tween = new TweenManager();
            Tween.setWaypointsLimit(4);
            Tween.setCombinedAttributesLimit(4);
            Tween.registerAccessor(Color.class, new ColorAccessor());
            Tween.registerAccessor(Rectangle.class, new RectangleAccessor());
            Tween.registerAccessor(Vector2.class, new Vector2Accessor());
            Tween.registerAccessor(Vector3.class, new Vector3Accessor());
            Tween.registerAccessor(OrthographicCamera.class, new CameraAccessor());
        }

        screen = new TitleScreen();
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(Config.bgColor.r, Config.bgColor.g, Config.bgColor.b, Config.bgColor.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        float dt = Math.min(Gdx.graphics.getDeltaTime(), 1f / 30f);
        audio.update(dt);
        tween.update(dt);
        screen.update(dt);

        screen.render(assets.batch);
    }

    @Override
    public void dispose () {
        tween = null;
        screen = null;
        assets.dispose();
        audio.dispose();
    }

}
