package lando.systems.ld41.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.gameobjects.Level;
import lando.systems.ld41.utils.Assets;

import java.util.Iterator;

public class LevelSelectScreen extends BaseScreen {
    EarClippingTriangulator triangulator;
    PolygonSpriteBatch polyBatch = new PolygonSpriteBatch();
    Array<Level> levels;
    Array<TextureRegion> thumbnails;
    int currentLevelIdx;

    TextureRegion arrow;
    float arrowSize = 64f;
    Rectangle arrowLeftClickTarget;
    Rectangle arrowRightClickTarget;

    NinePatch signPatch;
    float signSize;
    float padBetween = 32f;
    float currentWidth;
    Rectangle currentClickTarget;

    public LevelSelectScreen() {
        Gdx.input.setInputProcessor(this);
        triangulator = new EarClippingTriangulator();
        arrow = LudumDare41.game.assets.arrow;

        levels = new Array<Level>();
        thumbnails = new Array<TextureRegion>();

        Iterator<IntMap.Entry<String>> iterator = game.assets.levelNumberToFileNameMap.iterator();
        while (iterator.hasNext()) {
            IntMap.Entry<String> el = iterator.next();
            levels.add(new Level(el.value));
        }

        for (int i = 0; i < levels.size; i++) {
            thumbnails.add(getLevelThumbnail(levels.get(i)));
        }

        currentLevelIdx = 0;
        arrowLeftClickTarget = new Rectangle(10, (hudCamera.viewportHeight/2) - (arrowSize/2), arrowSize, arrowSize);
        arrowRightClickTarget = new Rectangle(hudCamera.viewportWidth - 10f - arrowSize, (hudCamera.viewportHeight/2) - (arrowSize/2), arrowSize, arrowSize);

        signPatch = LudumDare41.game.assets.backplateNinePatch;

        signSize = (hudCamera.viewportWidth - (arrowLeftClickTarget.width + 20f + (padBetween * 2))) / 4;
        float x = 10f + arrowSize + signSize + padBetween;
        currentWidth = hudCamera.viewportWidth - (x * 2);
        currentClickTarget = new Rectangle(x, (hudCamera.viewportHeight/2) - currentWidth, currentWidth, currentWidth);
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch batch) {
        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();

        Assets.drawString(batch, "Tee Off", 10f, hudCamera.viewportHeight - 20f, Color.CORAL, 1.25f, game.assets.font);
        Assets.drawString(batch, "Select a hole", 10f, hudCamera.viewportHeight - 100f, Color.CORAL, .5f, game.assets.font);
        float x = 10f + arrowSize;
        float y = hudCamera.viewportHeight/2;

        if (currentLevelIdx > 0) {
            // Draw left arrow (reversed)
            batch.draw(arrow, arrowLeftClickTarget.x + arrowSize, arrowLeftClickTarget.y, -arrowSize, arrowSize);

            // Draw left box
            renderSign(batch, currentLevelIdx - 1, x, y - (signSize/2));
        }

        // Draw current
        renderSign(batch, currentLevelIdx, (hudCamera.viewportWidth/2) - signSize/2, y);
        batch.draw(thumbnails.get(currentLevelIdx), currentClickTarget.x, currentClickTarget.y, currentWidth, currentWidth);

        if (currentLevelIdx != levels.size - 1) {
            x = 10f + arrowSize + signSize;

            // Draw right box
            renderSign(batch, currentLevelIdx + 1, hudCamera.viewportWidth - x, y - (signSize/2));

            // Draw right arrow
            batch.draw(arrow, arrowRightClickTarget.x, arrowRightClickTarget.y, arrowSize, arrowSize);
        }

        batch.end();
    }

    public void renderSign(SpriteBatch batch, int holeIdx, float x, float y) {
        Level level = levels.get(holeIdx);

        float top = y + signSize;

        game.assets.layout.setText(
            game.assets.font,
            "Hole: " + (holeIdx + 1) + "\n" +
                level.name + "\n" +
                "Par: " + level.par,
            Color.CORAL,
            signSize,
            1,
            false
        );

        signPatch.draw(batch, x, y, signSize, signSize);
        game.assets.font.draw(batch, game.assets.layout, x, top - 6f);
        game.assets.layout.reset();
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        Vector3 unprojected = hudCamera.unproject(new Vector3(screenX, screenY, 0));
        if (button == 0) {
            if (currentLevelIdx > 0 && arrowLeftClickTarget.contains(screenX, screenY)) {
                currentLevelIdx--;
            } else if (currentLevelIdx != levels.size - 1 && arrowRightClickTarget.contains(screenX, screenY)) {
                currentLevelIdx++;
            } else if (currentClickTarget.contains(unprojected.x, unprojected.y)) {
                game.setScreen(new GameScreen(currentLevelIdx), LudumDare41.game.assets.circleCropShader, 1.4f);
            }
        }

        return true;
    }

    TextureRegion getLevelThumbnail(Level level) {
        int levelWidth = level.groundLayer.getWidth() * (int)level.groundLayer.getTileWidth();
        int levelHeight = level.groundLayer.getHeight() * (int)level.groundLayer.getTileHeight();
        int frameSize = Math.max(levelWidth, levelHeight);
        FrameBuffer fb = new FrameBuffer(Pixmap.Format.RGBA8888, frameSize, frameSize, false);
        OrthographicCamera camera = new OrthographicCamera(frameSize, frameSize);
        camera.setToOrtho(false, fb.getWidth(), fb.getHeight());
        TextureRegion fbRegion = new TextureRegion(fb.getColorBufferTexture());
        fbRegion.flip(false, true);

        fb.begin();
        polyBatch.setProjectionMatrix(camera.combined);
        polyBatch.begin();

        Polyline exteriorPolyline = level.exteriorBoundry.getPolyline();
        PolygonRegion exteriorPolyReg = new PolygonRegion(
                LudumDare41.game.assets.thumbnailBg,
                exteriorPolyline.getTransformedVertices(),
                triangulator.computeTriangles(exteriorPolyline.getTransformedVertices()).toArray()
        );
        PolygonSprite exteriorPoly = new PolygonSprite(exteriorPolyReg);
        exteriorPoly.setOrigin(0, 0);
        exteriorPoly.draw(polyBatch);

        for (PolylineMapObject boundry : level.boundaries) {
            if (boundry == level.exteriorBoundry) {
                continue;
            }

            Polyline polyline = boundry.getPolyline();
            PolygonRegion polyReg = new PolygonRegion(
                    LudumDare41.game.assets.thumbnailBoundries,
                    polyline.getTransformedVertices(),
                    triangulator.computeTriangles(polyline.getTransformedVertices()).toArray()
            );
            PolygonSprite poly = new PolygonSprite(polyReg);
            poly.setOrigin(0, 0);
            poly.draw(polyBatch);
        }

        polyBatch.end();

        ShapeRenderer shapes = LudumDare41.game.assets.shapes;
        shapes.begin(ShapeRenderer.ShapeType.Filled);
        shapes.setColor(Color.RED);
        shapes.setProjectionMatrix(camera.combined);
        {
            for (int i = 0; i < level.circles.size; i++){
                Ellipse circle = level.circles.get(i).getEllipse();
                shapes.circle(circle.x, circle.y, circle.height / 2);
            }
        }
        shapes.end();

        fb.end();

        return fbRegion;
    }
}
