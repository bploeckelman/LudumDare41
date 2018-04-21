package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld41.LudumDare41;

public class Level {

    private boolean showDebug;
    TiledMap map;
    TiledMapRenderer mapRenderer;

    Polyline boundary;
    Array<EllipseMapObject> circles;
    Vector2 tempVector;
    Vector2 tempVector2;


    public Level(String mapFileName){
        showDebug = true;

        tempVector = new Vector2();
        tempVector2 = new Vector2();

        map = (new TmxMapLoader()).load(mapFileName, new TmxMapLoader.Parameters() {{
            generateMipMaps = true;
            textureMinFilter = Texture.TextureFilter.MipMap;
            textureMagFilter = Texture.TextureFilter.MipMap;
        }});
        mapRenderer = new OrthoCachedTiledMapRenderer(map);
        ((OrthoCachedTiledMapRenderer) mapRenderer).setBlending(true);

        // TODO: load map entities

        // load collision polygons
        MapLayer collisionLayer = map.getLayers().get("collision");
        Array<PolylineMapObject> bounds = collisionLayer.getObjects().getByType(PolylineMapObject.class);
        if (bounds.size < 1) throw new GdxRuntimeException("Forgot to add boundary layer to the map");
        boundary = bounds.get(0).getPolyline();

        circles = collisionLayer.getObjects().getByType(EllipseMapObject.class);


    }

    public void update(float dt){

    }

    public void render(SpriteBatch batch, OrthographicCamera camera){
        mapRenderer.setView(camera);
        mapRenderer.render();

        if (showDebug){
            ShapeRenderer shapes = LudumDare41.game.assets.shapes;
            shapes.begin(ShapeRenderer.ShapeType.Line);
            shapes.setColor(Color.RED);
            shapes.setProjectionMatrix(LudumDare41.game.screen.worldCamera.combined);
            {
                int vertLength = boundary.getVertices().length;
                for (int i = 0; i < vertLength; i += 2) {
                    tempVector.set(boundary.getTransformedVertices()[i], boundary.getTransformedVertices()[i + 1]);
                    tempVector2.set(boundary.getTransformedVertices()[(i + 2) % vertLength],
                                    boundary.getTransformedVertices()[(i + 3) % vertLength]);
                    shapes.line(tempVector.x, tempVector.y, tempVector2.x, tempVector2.y);
                }

                for (int i = 0; i < circles.size; i++){
                    Ellipse circle = circles.get(i).getEllipse();
                    shapes.circle(circle.x, circle.y, circle.height);
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
        int vertLength = boundary.getVertices().length;
        for (int i = 0; i < vertLength; i+= 2){
            tempVector.set(boundary.getTransformedVertices()[i], boundary.getTransformedVertices()[i+1]);
            tempVector2.set(boundary.getTransformedVertices()[(i+2)%vertLength], boundary.getTransformedVertices()[(i+3)%vertLength]);

            // Check if the traveling path intersects the segment
            if (Intersector.intersectSegments(oldPosition, newPosition, tempVector, tempVector2, collisionPoint)){
                normal.set((tempVector2.y - tempVector.y), (tempVector2.x - tempVector.x));
                normal.nor();
                collisionPoint.add(normal.scl(radius));
                normal.nor();

                return true;
            }

            // Check if the segments are within the radius of the object
            if (Intersector.distanceSegmentPoint(tempVector, tempVector2, newPosition) < radius){
                Intersector.nearestSegmentPoint(tempVector, tempVector2, newPosition, collisionPoint);
                normal.set((tempVector2.y - tempVector.y),-1* (tempVector2.x - tempVector.x));
                normal.nor();
                collisionPoint.add(normal.scl(radius));
                normal.nor();

                return true;
            }

        }

        for (int i = 0; i < circles.size; i++){
            Ellipse circle = circles.get(i).getEllipse();
            if (newPosition.dst(circle.x, circle.y) < radius + circle.height){
                normal.set(newPosition);
                normal.sub(circle.x, circle.y);
                normal.nor();
                collisionPoint.set(circle.x, circle.y);
                normal.scl(radius + circle.height);
                collisionPoint.add(normal);
                normal.nor();
                return true;
            }
        }
        return false;
    }


}
