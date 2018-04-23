package lando.systems.ld41.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.utils.Config;

public class EndScreen extends BaseScreen{

    private String heading = "Putt Putt Boom";
    private String theme = "Made for Ludum Dare 41:\nTheme: Two Incompatible Genre";
    private String thanks = "Thanks for playing our game!";
    private String developers = "Developed by:\nDoug Graham\nBrian Ploeckelman\nBrian Rossman\nJeffrey Hwang\nBrandon Humboldt\nTyler Pecora";
    private String artists = "Art by:\nMatt Neumann\nLuke Bain\nTroy Sullivan";
    private String emotionalSupport = "Emotional Support:\nAsuka the Shiba\nIan's Baklava";
    //TODO add song title
    private String music = "Music by:\nColin Kennedy";
    private String libgdx = "Made with <3 and LibGDX";

    public EndScreen(){

    }


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
        batch.draw(LudumDare41.game.assets.titleTexture, 0, 0,hudCamera.viewportWidth, hudCamera.viewportHeight);
        batch.setColor(new Color(0f, 0f, 0f, .9f));
        batch.draw(LudumDare41.game.assets.whitePixel, 0, 0, hudCamera.viewportWidth, hudCamera.viewportHeight);

        LudumDare41.game.assets.drawString(batch, heading, 0, hudCamera.viewportHeight - 10, Config.COLOR_TEXT, .8f, LudumDare41.game.assets.font, hudCamera.viewportWidth, Align.center);
        LudumDare41.game.assets.drawString(batch, theme, 0, hudCamera.viewportHeight - 60, Config.COLOR_TEXT, .35f, LudumDare41.game.assets.font, hudCamera.viewportWidth, Align.center);
        LudumDare41.game.assets.drawString(batch, developers, 0, hudCamera.viewportHeight - 120, Config.COLOR_TEXT, .3f, LudumDare41.game.assets.font, hudCamera.viewportWidth/2, Align.center);
        LudumDare41.game.assets.drawString(batch, emotionalSupport, 0, hudCamera.viewportHeight - 280, Config.COLOR_TEXT, .3f, LudumDare41.game.assets.font, hudCamera.viewportWidth/2, Align.center);
        LudumDare41.game.assets.drawString(batch, artists, hudCamera.viewportWidth/2, hudCamera.viewportHeight - 120, Config.COLOR_TEXT, .3f, LudumDare41.game.assets.font, hudCamera.viewportWidth/2, Align.center);
        LudumDare41.game.assets.drawString(batch, music, hudCamera.viewportWidth/2, hudCamera.viewportHeight - 220, Config.COLOR_TEXT, .3f, LudumDare41.game.assets.font, hudCamera.viewportWidth/2, Align.center);
        LudumDare41.game.assets.drawString(batch, libgdx, hudCamera.viewportWidth/2, hudCamera.viewportHeight - 300, Config.COLOR_TEXT, .4f, LudumDare41.game.assets.font, hudCamera.viewportWidth/2, Align.center);
        LudumDare41.game.assets.drawString(batch, thanks, 0, 200, Config.COLOR_TEXT, .3f, LudumDare41.game.assets.font, hudCamera.viewportWidth, Align.center);
        
        batch.end();
    }
}
