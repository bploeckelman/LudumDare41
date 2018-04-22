package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.screens.GameScreen;

public class Level {

    private boolean showDebug;

    TiledMap map;
    TiledMapRenderer mapRenderer;
    public MapLayer collisionLayer;
    public TiledMapTileLayer groundLayer;
    public TiledMapTileLayer wallsLayer;
    public MapLayer objectsLayer;

    public Tee tee;
    public Hole hole;

    Array<PolylineMapObject> boundaries;
    Array<EllipseMapObject> circles;
    Vector2 tempVector;
    Vector2 tempVector2;
    GameScreen screen;

    public Level(GameScreen screen, String mapFileName){
        showDebug = true;

        this.screen = screen;
        tempVector = new Vector2();
        tempVector2 = new Vector2();

        map = (new TmxMapLoader()).load(mapFileName, new TmxMapLoader.Parameters() {{
            generateMipMaps = true;
            textureMinFilter = Texture.TextureFilter.MipMap;
            textureMagFilter = Texture.TextureFilter.MipMap;
        }});
        mapRenderer = new OrthoCachedTiledMapRenderer(map);
        ((OrthoCachedTiledMapRenderer) mapRenderer).setBlending(true);

        // Validate that required map layers are available
        MapLayers layers = map.getLayers();
        collisionLayer = layers.get("collision");
        groundLayer    = (TiledMapTileLayer) layers.get("ground");
        wallsLayer     = (TiledMapTileLayer) layers.get("walls");
        objectsLayer   = layers.get("objects");
        if (collisionLayer == null || groundLayer == null || wallsLayer == null || objectsLayer == null) {
            throw new GdxRuntimeException("Missing required map layer. (required: 'collision', 'ground', 'walls', 'objects'");
        }

        // load map objects
        MapObjects objects = objectsLayer.getObjects();
        if (objects.getCount() < 2) {
            throw new GdxRuntimeException("Map must have at least 2 objects ('tee' and 'hole')");
        }
        MapObject obj = objects.get("tee");
        if (obj != null) {
            MapProperties props = obj.getProperties();
            tee = new Tee(props.get("x", Float.class),
                          props.get("y", Float.class),
                          props.get("facing", Integer.class));
        }
        obj = objects.get("hole");
        if (obj != null) {
            MapProperties props = obj.getProperties();
            hole = new Hole(props.get("x", Float.class),
                            props.get("y", Float.class));
        }

        // load collision polygons
        boundaries = collisionLayer.getObjects().getByType(PolylineMapObject.class);
        if (boundaries.size < 1) throw new GdxRuntimeException("Forgot to add boundary layer to the map");

        circles = collisionLayer.getObjects().getByType(EllipseMapObject.class);
    }

    public void update(float dt){

    }

    public void render(SpriteBatch batch, OrthographicCamera camera){
        mapRenderer.setView(camera);
        mapRenderer.render();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        {
            if (hole != null) {
                hole.render(batch);
            }
        }
        batch.end();

        if (showDebug){
            ShapeRenderer shapes = LudumDare41.game.assets.shapes;
            shapes.begin(ShapeRenderer.ShapeType.Line);
            shapes.setColor(Color.RED);
            shapes.setProjectionMatrix(screen.worldCamera.combined);
            {
                for (int j=0; j < boundaries.size; j++) {
                    Polyline boundary = boundaries.get(j).getPolyline();

                    int vertLength = boundary.getVertices().length;
                    for (int i = 0; i < vertLength; i += 2) {
                        tempVector.set(boundary.getTransformedVertices()[i], boundary.getTransformedVertices()[i + 1]);
                        tempVector2.set(boundary.getTransformedVertices()[(i + 2) % vertLength],
                                boundary.getTransformedVertices()[(i + 3) % vertLength]);
                        shapes.line(tempVector.x, tempVector.y, tempVector2.x, tempVector2.y);
                    }
                }

                for (int i = 0; i < circles.size; i++){
                    Ellipse circle = circles.get(i).getEllipse();
                    shapes.circle(circle.x, circle.y, circle.height / 2);
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
        for (int j=0; j < boundaries.size; j++) {
            Polyline boundary = boundaries.get(j).getPolyline();
            boolean collided = false;
            float nearestCollision = Float.MAX_VALUE;
            int vertLength = boundary.getVertices().length;
            for (int i = 0; i < vertLength; i += 2) {
                tempVector.set(boundary.getTransformedVertices()[i], boundary.getTransformedVertices()[i + 1]);
                tempVector2.set(boundary.getTransformedVertices()[(i + 2) % vertLength], boundary.getTransformedVertices()[(i + 3) % vertLength]);

                // Check if the traveling path intersects the segment
                if (Intersector.intersectSegments(oldPosition, newPosition, tempVector, tempVector2, collisionPoint)) {
                    normal.set(-1*(tempVector2.y - tempVector.y), (tempVector2.x - tempVector.x));
                    if (Intersector.pointLineSide(tempVector, tempVector2, oldPosition) != Intersector.pointLineSide(tempVector.x, tempVector.y, tempVector2.x, tempVector2.y, collisionPoint.x + normal.x, collisionPoint.y + normal.y)){
                        normal.set((tempVector2.y - tempVector.y), -1*(tempVector2.x - tempVector.x));
                    }
                    normal.nor();
                    collisionPoint.add(normal.scl(radius));

                    normal.nor();

                    collided = true;
                    nearestCollision = 0;
                }

                // Check if the segments are within the radius of the object
                float dist = Intersector.distanceSegmentPoint(tempVector, tempVector2, newPosition);
                if (dist < nearestCollision && dist < radius) {
                    Intersector.nearestSegmentPoint(tempVector, tempVector2, newPosition, collisionPoint);
                    if (collisionPoint.epsilonEquals(tempVector) || collisionPoint.epsilonEquals(tempVector2)){
                        normal.set(newPosition.x - collisionPoint.x, newPosition.y - collisionPoint.y);
                    } else {
                        normal.set(-1 * (tempVector2.y - tempVector.y), (tempVector2.x - tempVector.x));
                        if (Intersector.pointLineSide(tempVector, tempVector2, oldPosition) != Intersector.pointLineSide(tempVector.x, tempVector.y, tempVector2.x, tempVector2.y, collisionPoint.x + normal.x, collisionPoint.y + normal.y)) {
                            normal.set((tempVector2.y - tempVector.y), -1 * (tempVector2.x - tempVector.x));
                        }
                    }
                    normal.nor();
                    collisionPoint.add(normal.scl(radius));

                    normal.nor();

                    collided = true;
                    nearestCollision = dist;
                }

            }
            if (collided){
                return true;
            }
        }

        for (int i = 0; i < circles.size; i++){
            Ellipse circle = circles.get(i).getEllipse();
            if (newPosition.dst(circle.x, circle.y) < radius + circle.height / 2f){
                normal.set(newPosition);
                normal.sub(circle.x, circle.y);
                normal.nor();
                collisionPoint.set(circle.x, circle.y);
                normal.scl(radius + circle.height / 2f);
                collisionPoint.add(normal);
                normal.nor();
                return true;
            }
        }
        return false;
    }


}
