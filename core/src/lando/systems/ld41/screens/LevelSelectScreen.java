package lando.systems.ld41.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.gameobjects.Level;
import lando.systems.ld41.utils.Assets;

public class LevelSelectScreen extends BaseScreen {
    EarClippingTriangulator triangulator;
    PolygonSpriteBatch polyBatch = new PolygonSpriteBatch();
    Array<Level> levels;
    Array<TextureRegion> thumbnails;

    public LevelSelectScreen() {
        Gdx.input.setInputProcessor(this);
        triangulator = new EarClippingTriangulator();

        levels = new Array<Level>();
        thumbnails = new Array<TextureRegion>();
        levels.add(new Level("maps/test.tmx"));
        levels.add(new Level("maps/test2.tmx"));

        for (int i = 0; i < levels.size; i++) {
            thumbnails.add(getLevelThumbnail(levels.get(i)));
        }
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render(SpriteBatch batch) {
        batch.begin();

        for (int i = 0; i < levels.size; i++) {
            float x = 10f + ((i + 1) * 200f);
            batch.draw(thumbnails.get(i), x, 10f, 200f, 200f);
            Assets.drawString(batch, levels.get(i).name, x, 210f, Color.CORAL, .5f, game.assets.font);
        }

        batch.end();
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
