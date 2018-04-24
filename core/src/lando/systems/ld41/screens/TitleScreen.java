package lando.systems.ld41.screens;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.utils.Audio;
import lando.systems.ld41.utils.Config;
import lando.systems.ld41.utils.accessors.Vector2Accessor;

public class TitleScreen extends BaseScreen {

    boolean allowTouch;
    public Vector2 putterPos;
    public MutableFloat putt1Size;
    public MutableFloat putt2Size;
    public MutableFloat alpha;
    public MutableFloat boomSize;

    public TitleScreen() {
        allowTouch = false;
        putterPos = new Vector2(0, hudCamera.viewportHeight);
        putt1Size = new MutableFloat(0);
        putt2Size = new MutableFloat(0);
        alpha = new MutableFloat(0);
        boomSize = new MutableFloat(0);

        Timeline.createSequence()
                .pushPause(.4f)
                .push(Tween.to(putterPos, Vector2Accessor.Y, .5f)
                        .target(267))
                .push(Tween.to(putt1Size, 1, .5f)
                        .target(1f))
                .push(Tween.to(putt2Size, 1, .5f)
                        .target(1f))
                .pushPause(.3f)
                .push(Tween.set(alpha, 1)
                        .target(1))
                .push(Tween.call(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        LudumDare41.game.audio.playSound(Audio.Sounds.explosion2);
                    }
                }))
                .push(Tween.set(boomSize, 1)
                        .target(1))
                .push(Tween.to(alpha, 1, .5f)
                        .target(0))
                .push(Tween.call(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        allowTouch = true;
                    }
                }))
                .start(LudumDare41.game.tween);

        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void update(float dt) {
        if (Gdx.app.getType() == Application.ApplicationType.Desktop && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        if (allowTouch && Gdx.input.justTouched()) {
            game.setScreen(new LevelSelectScreen());
        }

        if (Gdx.app.getType() == Application.ApplicationType.Desktop && Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            ScoreCard card = new ScoreCard();
            card.setDemoStats();
            game.setScreen(card);
        }

        if (Gdx.app.getType() == Application.ApplicationType.Desktop && Gdx.input.isKeyJustPressed(Input.Keys.J)) {
            EndScreen screen = new EndScreen();
            game.setScreen(screen);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            batch.setColor(Color.WHITE);
            batch.draw(game.assets.titleTexture, 0f, 0f, hudCamera.viewportWidth, hudCamera.viewportHeight);
            batch.draw(game.assets.titlePutter, putterPos.x, putterPos.y);
            float putt1Width = 273 * putt1Size.floatValue();
            float putt1Height = 145 * putt1Size.floatValue();
            batch.draw(game.assets.titlePutt1, (273 - putt1Width)/2f , 347 + (145 - putt1Height)/2, putt1Width, putt1Height);

            float putt2Width = 223 * putt2Size.floatValue();
            float putt2Height = 146 * putt2Size.floatValue();
            batch.draw(game.assets.titlePutt2, 255 +(223 - putt2Width)/2f , 446 + (146 - putt2Height)/2, putt2Width, putt2Height);

            batch.draw(game.assets.titleBoom, 250, 194, 551 * boomSize.floatValue(), 408 * boomSize.floatValue());
            if (alpha.floatValue() > 0) {
                batch.setColor(1, 1, 1, alpha.floatValue());
                batch.draw(game.assets.whitePixel, 0, 0, hudCamera.viewportWidth, hudCamera.viewportHeight);
            }
        }
        batch.end();
    }

}
