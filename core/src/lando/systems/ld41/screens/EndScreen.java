package lando.systems.ld41.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld41.utils.Assets;
import lando.systems.ld41.utils.Config;

public class EndScreen extends BaseScreen{

    private String heading = "Putt-Putt Boom!";
    private String theme = "Made for Ludum Dare 41\nTheme: Combine 2 Incompatible Genres";
    private String thanks = "Thanks for playing our game!";
    private String developers = "Code:\nDoug Graham\nBrian Ploeckelman\nBrian Rossman\nJeffrey Hwang\nBrandon Humboldt\nColin Kennedy";
    private String artists = "Art:\nMatt Neumann\nLuke Bain\nTroy Sullivan";
    private String emotionalSupport = "Emotional Support:\nAsuka the Shiba\nIan's Baklava";
    private String music = "Music:\nTyler Pecora\n\nSounds:\nBrian's Mouth";
    private String libgdx = "Made with <3 and LibGDX";

    @Override
    public void update(float dt) {
        if (Gdx.input.justTouched()) {
            game.setScreen(new TitleScreen());
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClearColor(Config.bgColor.r, Config.bgColor.g, Config.bgColor.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        batch.setColor(Color.WHITE);
        batch.draw(game.assets.titleTexture, 0, 0,hudCamera.viewportWidth, hudCamera.viewportHeight);
        batch.setColor(new Color(0f, 0f, 0f, .9f));
        batch.draw(game.assets.whitePixel, 0, 0, hudCamera.viewportWidth, hudCamera.viewportHeight);

        Assets.drawString(batch, heading, 0, hudCamera.viewportHeight - 10, Config.COLOR_TEXT, .8f, game.assets.font, hudCamera.viewportWidth, Align.center);
        Assets.drawString(batch, theme, 0, hudCamera.viewportHeight - 60, Config.COLOR_TEXT, .35f, game.assets.font, hudCamera.viewportWidth, Align.center);
        Assets.drawString(batch, developers, 0, hudCamera.viewportHeight - 120, Config.COLOR_TEXT, .3f, game.assets.font, hudCamera.viewportWidth/2, Align.center);
        Assets.drawString(batch, emotionalSupport, 0, hudCamera.viewportHeight - 280, Config.COLOR_TEXT, .3f, game.assets.font, hudCamera.viewportWidth/2, Align.center);
        Assets.drawString(batch, artists, hudCamera.viewportWidth/2, hudCamera.viewportHeight - 120, Config.COLOR_TEXT, .3f, game.assets.font, hudCamera.viewportWidth/2, Align.center);
        Assets.drawString(batch, music, hudCamera.viewportWidth/2, hudCamera.viewportHeight - 220, Config.COLOR_TEXT, .3f, game.assets.font, hudCamera.viewportWidth/2, Align.center);
        Assets.drawString(batch, thanks, 0, 200, Config.COLOR_TEXT, .3f, game.assets.font, hudCamera.viewportWidth, Align.center);
        Assets.drawString(batch, libgdx, 0, 100, Config.COLOR_TEXT, .4f, game.assets.font, hudCamera.viewportWidth, Align.center);

        batch.end();
    }

}
