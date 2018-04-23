package lando.systems.ld41.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.stats.GameStats;
import lando.systems.ld41.utils.Config;

/**
 * Created by Brian on 4/23/2018.
 */
public class ScoreCard extends BaseScreen {

    private GameStats gameStats;
    private Rectangle cardBounds;
    private String[][] data;
    private Rectangle[][] grid;
    private String[] stats = new String[3];
    private Rectangle[] statsBounds = new Rectangle[3];
    private TextureRegion white = LudumDare41.game.assets.whitePixel;
    private ShapeRenderer sr = new ShapeRenderer();
    private GlyphLayout layout = LudumDare41.game.assets.layout;
    private int devCount = 5;

    private int holes;

    private String rules;
    private Rectangle rulesBounds;

    public ScoreCard() {
        holes = LudumDare41.game.assets.levelNumberToFileNameMap.size;
        gameStats = LudumDare41.game.gameStats;
        initializeGrid();

        rules = "Putt Putt Boom Course Rules:\n" +
                "1. Diplomats cannot take their tanks off the course\n" +
                "2. Play each hole per turn. No skipping holes!\n" +
                "3. No Fucking Curse Words\n";
    }

    public void setDemoStats() {
        GameStats gs = new GameStats();
        for (int i = 0; i < holes; i++) {
            gs.addStats(i, (float)MathUtils.random(20000, 2000000), MathUtils.random(20, 200), MathUtils.random(20, 200), MathUtils.randomBoolean(), MathUtils.random(2000, 200000000));
        }
        gameStats = gs;
    }

    private void initializeGrid() {
        int totalRows = devCount + 2;
        int totalCols = holes + 2;

        data = new String[totalRows][totalCols];
        grid = new Rectangle[totalRows][totalCols];

        float padding = 20;
        float dPadding = 40;

        float x = padding;
        float y = padding;
        float width = hudCamera.viewportWidth - dPadding;
        float height = hudCamera.viewportHeight - dPadding;

        cardBounds = new Rectangle(x, y, width, height);

        y += height/3;

        x += padding;
        width -= dPadding;

        float ix = x;
        float iy = hudCamera.viewportHeight - dPadding;
        float cellHeight = game.assets.font.getLineHeight() * 2;//(hudCamera.viewportHeight - padding - y) / (grid.length + 1);
        float cellWidth = width / (grid[0].length + 2);
        for (int row = 0; row < grid.length; row++) {
            iy -= cellHeight;
            for (int col = 0; col < grid[row].length; col++) {
                float itemWidth = cellWidth;
                if (col == 0) {
                    itemWidth *= 2.45f;
                } else if ((col + 1) == grid[row].length) {
                    itemWidth *= 1.55f;
                }
                grid[row][col] = new Rectangle(ix, iy, itemWidth, cellHeight);
                ix += itemWidth;
            }
            ix = x;
        }

        DevData[] devData = DevData.getDevData(devCount, holes);

        for (int row = 0; row < data.length; row++) {
            String text;
            for (int col = 0; col < data[row].length; col++) {
                if (row == 0) {
                    // header
                    if (col == 0) {
                        text = "NAME";
                    } else if (col + 1 == data[row].length) {
                        text = "TOTAL";
                    } else {
                        text = Integer.toString(col);
                    }
                } else if (row == 1) {
                    // user
                    if (col == 0) {
                        text = "You";
                    } else if (col + 1 != data[row].length) {
                        text = getScore(col - 1);
                    } else {
                        text = getTotalScore();
                    }
                } else {
                    // devs
                    if (col == 0) {
                        text = devData[row - 2].name;
                    } else if (col + 1 != data[row].length) {
                        text = devData[row - 2].scores[col - 1];
                    } else {
                        text = devData[row - 2].total;
                    }
                }
                data[row][col] = text;
            }
        }

        stats[0] = "Time: " + gameStats.totalTime();
        stats[1] = "Deaths: " + gameStats.totalDeaths();
        stats[2] = "Kills: " + gameStats.totalKills();

        statsBounds = new Rectangle[3];
        x = dPadding;
        y = dPadding;
        cellWidth = width / 3;
        statsBounds = new Rectangle[] {
                new Rectangle(x, y, cellWidth, cellHeight),
                new Rectangle(x + cellWidth, y, cellWidth, cellHeight),
                new Rectangle(x + (cellWidth * 2), y, cellWidth, cellHeight)
        };

        float ruleHeight = hudCamera.viewportHeight - (dPadding * 3) - ((data.length + 1) * cellHeight);
        rulesBounds = new Rectangle(x, cellHeight + dPadding, width, ruleHeight);
    }

    private String getScore(int index) {
        int score = gameStats.getLevelStats(index).score;
        return (score > 0) ? Integer.toString(score) : "-";
    }

    private String getTotalScore() {
        return Integer.toString(gameStats.totalScore());
    }

    @Override
    public void update(float dt) {
        if (Gdx.input.justTouched()) {
            game.setScreen(new EndScreen());
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        BitmapFont.BitmapFontData fontData = game.assets.font.getData();

        float scaleX = fontData.scaleX;
        float scaleY = fontData.scaleY;
        fontData.setScale(0.25f);

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            batch.setColor(Config.bgColor);
            batch.draw(white, 0, 0, hudCamera.viewportWidth, hudCamera.viewportHeight);

            batch.setColor(new Color(244/255f, 180/255f, 27/255f, 1f));
            batch.draw(white, cardBounds.x, cardBounds.y, cardBounds.width, cardBounds.height);

            for (int row = 0; row < grid.length; row++) {
                for (int col = 0; col < grid[row].length; col++) {
                    int align = col == 0 ? Align.left : Align.center;
                    drawString(batch, data, grid, row, col, align);
                }
            }

            layout.setText(game.assets.font, rules, Color.BLACK, rulesBounds.width, Align.left, false);
            game.assets.font.draw(batch, layout, rulesBounds.x,  rulesBounds.y + rulesBounds.height);

            for (int col = 0; col < statsBounds.length; col++) {
                drawString(batch, stats[col], statsBounds[col].x, statsBounds[col].y, statsBounds[col].width, Color.BLACK, Align.left);
            }
        }
        batch.end();

        sr.setProjectionMatrix(hudCamera.combined);
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(Color.BLACK);
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                Rectangle r = grid[row][col];
                sr.rect(r.x, r.y, r.width, r.height);
            }
        }
        sr.end();

        fontData.setScale(scaleX, scaleY);
    }

    private void drawString(SpriteBatch batch, String[][] data, Rectangle[][] grid, int x, int y, int align) {
        float gx = grid[x][y].x;
        float gw = grid[x][y].width;
        if (align == Align.left) {
            gx += 10;
            gw -= 10;
        }

        Color color = (x == 1) ? Color.WHITE : Color.BLACK;
        drawString(batch, data[x][y], gx, grid[x][y].y, gw, color, align);
    }

    private void drawString(SpriteBatch batch, String text, float x, float y, float width, Color color, int align) {
        layout.setText(game.assets.font, text, color, width, align, false);
        game.assets.font.draw(batch, layout, x, y + game.assets.font.getLineHeight());
    }
}
