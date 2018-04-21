package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld41.LudumDare41;

public class Level {

    private boolean showDebug;
    TiledMap map;
    TiledMapRenderer mapRenderer;
    Polyline boundry;
    Vector2 tempVector;
    Vector2 tempVector2;


    public Level(String mapFileName){
        showDebug = true;
        map = (new TmxMapLoader()).load(mapFileName);
        mapRenderer = new OrthoCachedTiledMapRenderer(map);
        ((OrthoCachedTiledMapRenderer) mapRenderer).setBlending(true);

        // TODO: load map entities

        // load collision polygons
        MapLayer collisionLayer = map.getLayers().get("collision");
        Array<PolylineMapObject> bounds = collisionLayer.getObjects().getByType(PolylineMapObject.class);
        boundry = bounds.get(0).getPolyline();
        tempVector = new Vector2();
        tempVector2 = new Vector2();
        map.getTileSets();
    }

    public void update(float dt){

    }

    public void render(SpriteBatch batch, OrthographicCamera camera){
        mapRenderer.setView(camera);
        mapRenderer.render();

        if (showDebug){
            ShapeRenderer shapes = LudumDare41.game.assets.shapes;
            shapes.begin(ShapeRenderer.ShapeType.Line);
            shapes.setProjectionMatrix(LudumDare41.game.screen.worldCamera.combined);
            {
                int vertLength = boundry.getVertices().length;
                for (int i = 0; i < vertLength; i += 2) {
                    tempVector.set(boundry.getTransformedVertices()[i], boundry.getTransformedVertices()[i + 1]);
                    tempVector2.set(boundry.getTransformedVertices()[(i + 2) % vertLength],
                                    boundry.getTransformedVertices()[(i + 3) % vertLength]);
                    shapes.line(tempVector.x, tempVector.y, tempVector2.x, tempVector2.y);
                }
            }
            shapes.end();
        }
    }


    /**
     *
     * @param oldPosition the position last frame
     * @param newPosition the new position this frame
     * @param radius the radius of the collision object
     * @param collisionPoint this is filled with the intersection point
     * @param normal the normal of the collision for reflections
     * @return if there was an intersection
     */
    public boolean checkCollision(Vector2 oldPosition, Vector2 newPosition, float radius, Vector2 collisionPoint, Vector2 normal){
//        Intersector.intersectSegmentCircleDisplace()
        return false;
    }


}
