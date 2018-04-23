package lando.systems.ld41.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.utils.Config;

public class TitleScreen extends BaseScreen {

    public TitleScreen() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        if (Gdx.input.justTouched()) {
            game.setScreen(new LevelSelectScreen());
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            ScoreCard card = new ScoreCard();
            card.setDemoStats();
            game.setScreen(card);
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            batch.setColor(Config.bgColor);
            batch.draw(LudumDare41.game.assets.whitePixel,0,0, hudCamera.viewportWidth, hudCamera.viewportHeight);

            batch.setColor(Color.WHITE);
            batch.draw(game.assets.titleTexture, 0f, 0f, hudCamera.viewportWidth, hudCamera.viewportHeight);
        }
        batch.end();
    }

}
