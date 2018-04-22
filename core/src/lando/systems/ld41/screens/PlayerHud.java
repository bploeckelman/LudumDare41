package lando.systems.ld41.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.gameobjects.Tank;
import lando.systems.ld41.ui.BallIndicatorArrow;
import lando.systems.ld41.utils.Assets;

/**
 * Created by Brian on 4/22/2018.
 */
public class PlayerHud {

    private BallIndicatorArrow ballIndicatorArrow;
    private GameScreen screen;
    public OrthographicCamera hudCamera;
    private NinePatch border = LudumDare41.game.assets.backplateNinePatch;

    private int score = -1;
    private String scoreText;
    private int deaths = -1;
    private String deathText;

    public PlayerHud(GameScreen screen) {
        this.screen = screen;
        hudCamera = screen.hudCamera;
        ballIndicatorArrow  = new BallIndicatorArrow(screen);
    }

    // this is projected into screen coords

    public void update(float dt) {
        updateStats();
        ballIndicatorArrow.update(dt);
    }

    private void updateStats() {
        Tank player = screen.playerTank;
        if (player == null) return;

        if (score != player.shots) {
            score = player.shots;
            scoreText = "" + score;
        }

        if (deaths != player.deaths) {
            deaths = player.deaths;
            deathText = "" + deaths;
        }
    }

    public void render(SpriteBatch batch) {
        ballIndicatorArrow.render(batch);

        renderPlayerInfo(batch, screen.playerTank);
    }

    private void renderPlayerInfo(SpriteBatch batch, Tank player) {
        if (player == null) return;

        float height = 55;
        float width = 120;
        float padding = 10;

        float x = hudCamera.viewportWidth - width - padding;
        float y = hudCamera.viewportHeight - height - padding;

        border.draw(batch, x, y, width, height);

        // text top down
        y += height - 10;
        x += padding;
        drawString(batch, "Shots:", x, y);
        drawString(batch, scoreText, x + 70, y);
        y -= 20;
        drawString(batch, "Deaths:", x, y);
        drawString(batch, deathText, x + 70, y);
    }

    private void drawString(SpriteBatch batch, String text, float x, float y) {
        Assets.drawString(batch, text, x, y, Color.WHITE, 0.25f, LudumDare41.game.assets.font);
    }


}
