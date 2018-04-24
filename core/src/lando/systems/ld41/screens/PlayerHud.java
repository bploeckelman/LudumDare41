package lando.systems.ld41.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.gameobjects.Tank;
import lando.systems.ld41.stats.GameStats;
import lando.systems.ld41.ui.BallIndicatorArrow;
import lando.systems.ld41.ui.HoleIndicator;
import lando.systems.ld41.utils.Assets;

/**
 * Created by Brian on 4/22/2018.
 */
public class PlayerHud {

    private BallIndicatorArrow ballIndicatorArrow;
    private HoleIndicator holeIndicator;
    private GameScreen screen;
    public OrthographicCamera hudCamera;
    private NinePatch border = LudumDare41.game.assets.backplateNinePatch;

    private int score = -1;
    private String scoreText = "";
    private int deaths = -1;
    private String deathText = "";
    private int kills = -1;
    private String killText = "";
    private long totalTime = 0;
    private String timeText = "";

    private int hole = 0;
    private String holeText = "";

    public PlayerHud(GameScreen screen) {
        this.screen = screen;
        hudCamera = screen.hudCamera;
        ballIndicatorArrow  = new BallIndicatorArrow(screen);
        holeIndicator = new HoleIndicator(screen);
    }

    // this is projected into screen coords

    public void update(float dt) {
        updateStats();
        ballIndicatorArrow.update(dt);
        holeIndicator.update(dt);
    }

    private void updateStats() {
        Tank player = screen.playerTank;
        if (player == null) return;

        if (hole != screen.currentLevelNum + 1) {
            hole = screen.currentLevelNum + 1;
            holeText = Integer.toString(hole);
        }

        if (score != player.shots) {
            score = player.shots;
            scoreText = Integer.toString(score);
        }

        if (deaths != player.deaths) {
            deaths = player.deaths;
            deathText = Integer.toString(deaths);
        }

        if (kills != player.kills) {
            kills = player.kills;
            killText = Integer.toString(kills);
        }

        long time = screen.getTime();
        if (totalTime != time || timeText == "") {
            totalTime = time;
            timeText = GameStats.toTimeString(totalTime);
        }
    }

    public void render(SpriteBatch batch) {
        ballIndicatorArrow.render(batch);
        holeIndicator.render(batch);
        renderPlayerInfo(batch, screen.playerTank);
    }

    private void renderPlayerInfo(SpriteBatch batch, Tank player) {
        if (player == null) return;

        float height = 150;
        float width = 120;
        float padding = 10;

        float x = hudCamera.viewportWidth - width - padding;
        float y = hudCamera.viewportHeight - height - padding;

        border.draw(batch, x, y, width, height);

        // text top down
        y += height - 10;
        x += padding;
        drawString(batch, "Hole:", x, y);
        drawString(batch, holeText, x + 70, y);
        y -= 20;
        drawString(batch, "Shots:", x, y);
        drawString(batch, scoreText, x + 70, y);
        y -= 20;
        drawString(batch, "Kills:", x, y);
        drawString(batch, killText, x + 70, y);
        y -= 20;
        drawString(batch, "Deaths:", x, y);
        drawString(batch, deathText, x + 70, y);
        y -= 20;
        drawString(batch, "Time:", x, y);
        drawString(batch, timeText, x + 45, y);

        x = hudCamera.viewportWidth - width - padding;
        y -= 45;
        if (player.isInvincible) {
            batch.setColor(getColor(player.invincibleTimer));
            batch.draw(LudumDare41.game.assets.puInvincible, x + 8, y, 0, 0, 20, 20, 1, 1, 0);
            batch.setColor(Color.WHITE);
        }

        if (!player.isVisible) {
            batch.setColor(getColor(player.camoTimer));
            batch.draw(LudumDare41.game.assets.puCamo, x + 36, y, 0, 0, 20, 20, 1, 1, 0);
            batch.setColor(Color.WHITE);
        }

        if (player.hasPontoons) {
            batch.draw(LudumDare41.game.assets.puPontoon, x + 92, y, 0, 0, 20, 20, 1, 1, 0);
        }

        if (player.hasShield) {
            batch.draw(LudumDare41.game.assets.puShield, x + 64, y, 0, 0, 20, 20, 1, 1, 0);
        }
    }

    private Color getColor(float timer) {
        return (timer < 2f) && ((int) (timer * 20) % 2 == 0) ? Color.RED : Color.WHITE;
    }

    private void drawString(SpriteBatch batch, String text, float x, float y) {
        Assets.drawString(batch, text, x, y, Color.WHITE, 0.25f, LudumDare41.game.assets.font);
    }


}
