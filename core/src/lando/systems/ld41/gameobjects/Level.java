package lando.systems.ld41.gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
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
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import lando.systems.ld41.LudumDare41;
import lando.systems.ld41.screens.GameScreen;

public class Level {
    public enum CollisionType {None, Wall, Bumper}

    private boolean showDebug = false;

    public String name;
    public int par;
    TiledMap map;
    TiledMapRenderer mapRenderer;
    public MapLayer collisionLayer;
    public TiledMapTileLayer groundLayer;
    public TiledMapTileLayer wallsLayer;
    public MapLayer objectsLayer;

    // Map objects
    public Tee tee;
    public Hole hole;
    public Array<PinballBumper> pinballBumpers;
    public Array<EnemyTankInfo> enemyTankInfos;
    public Array<EnemyTurretInfo> enemyTurretInfos;
    public Array<CatapultInfo> catapultInfos;
    public Array<Polygon> waterRegions;
    public Array<Polygon> sandRegions;
    public Array<PolygonSprite> waterSprites;
    public Array<PolygonSprite> sandSprites;

    public Array<PolylineMapObject> boundaries;
    public PolylineMapObject exteriorBoundry;
    public Array<EllipseMapObject> circles;
    Vector2 tempVector;
    Vector2 tempVector2;
    GameScreen screen;
    PolygonSpriteBatch polys;

    public Level(String mapFileName) {
        polys = LudumDare41.game.assets.polys;
        tempVector = new Vector2();
        tempVector2 = new Vector2();

        map = (new TmxMapLoader()).load(mapFileName, new TmxMapLoader.Parameters() {{
            generateMipMaps = true;
            textureMinFilter = Texture.TextureFilter.MipMap;
            textureMagFilter = Texture.TextureFilter.MipMap;
        }});
        mapRenderer = new OrthoCachedTiledMapRenderer(map);
        ((OrthoCachedTiledMapRenderer) mapRenderer).setBlending(true);

        this.name = map.getProperties().get("name", "[UNNAMED]", String.class);
        this.par = map.getProperties().get("par", -1, Integer.class);

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

        // load objects
        pinballBumpers = new Array<PinballBumper>();
        enemyTankInfos = new Array<EnemyTankInfo>();
        enemyTurretInfos = new Array<EnemyTurretInfo>();
        catapultInfos = new Array<CatapultInfo>();
        waterRegions = new Array<Polygon>();
        sandRegions = new Array<Polygon>();
        for (MapObject object : objects) {
            final MapProperties props = object.getProperties();
            String type = (String) props.get("type");
//            Gdx.app.log("Obj Type", "" + type);
            if (type == null) continue;
            if (type.equalsIgnoreCase("bumper")) {
                pinballBumpers.add(new PinballBumper(props.get("x", Float.class), props.get("y", Float.class)));
            }
            else if (type.equalsIgnoreCase("tank")) {
                if (props.get("x")      == null) throw new GdxRuntimeException("Missing 'x' property on tank");
                if (props.get("y")      == null) throw new GdxRuntimeException("Missing 'y' property on tank");
                if (props.get("facing") == null) throw new GdxRuntimeException("Missing 'facing' property on tank");
                if (props.get("color")  == null) throw new GdxRuntimeException("Missing 'color' property on tank");
                enemyTankInfos.add(new EnemyTankInfo() {{
                    x      = props.get("x", Float.class);
                    y      = props.get("y", Float.class);
                    facing = props.get("facing", Integer.class);
                    color  = props.get("color", String.class);
                }});
            }
            else if (type.equalsIgnoreCase("turret")) {
                if (props.get("x")      == null) throw new GdxRuntimeException("Missing 'x' property on turret");
                if (props.get("y")      == null) throw new GdxRuntimeException("Missing 'y' property on turret");
                if (props.get("facing") == null) throw new GdxRuntimeException("Missing 'facing' property on turret");
                enemyTurretInfos.add(new EnemyTurretInfo() {{
                    x      = props.get("x", Float.class);
                    y      = props.get("y", Float.class);
                    facing = props.get("facing", Integer.class);
                }});
            }
            else if (type.equalsIgnoreCase("catapult")) {
                if (props.get("x") == null) throw new GdxRuntimeException("Missing 'x' property on catapult");
                if (props.get("y") == null) throw new GdxRuntimeException("Missing 'y' property on catapult");
                catapultInfos.add(new CatapultInfo() {{
                    x = props.get("x", Float.class);
                    y = props.get("y", Float.class);
                }});
            }
            else if (type.equalsIgnoreCase("tee")) {
                tee = new Tee(props.get("x", Float.class),
                              props.get("y", Float.class),
                              props.get("facing", Integer.class));
            }
            else if (type.equalsIgnoreCase("hole")) {
                hole = new Hole(props.get("x", Float.class),
                                props.get("y", Float.class));
            }
            else if (type.equalsIgnoreCase("water")) {
                // NOTE: this will blow up if the polyline isn't closed
                Polyline polyline = ((PolylineMapObject) object).getPolyline();
                Polygon polygon = new Polygon(polyline.getTransformedVertices());
                waterRegions.add(polygon);
            }
            else if (type.equalsIgnoreCase("sand")) {
                // NOTE: this will blow up if the polyline isn't closed
                Polyline polyline = ((PolylineMapObject) object).getPolyline();
                Polygon polygon = new Polygon(polyline.getTransformedVertices());
                sandRegions.add(polygon);
            }
        }
        if (tee == null) {
            throw new GdxRuntimeException("Map missing 'tee' object");
        }
        if (hole == null) {
            throw new GdxRuntimeException("Map missing 'hole' object");
        }

        // load collision polygons
        boundaries = collisionLayer.getObjects().getByType(PolylineMapObject.class);
        boolean missingExterior = true;
        for (PolylineMapObject boundary : boundaries) {
            String type = (String) boundary.getProperties().get("type");
            if (type != null && type.equalsIgnoreCase("exterior")) {
                this.exteriorBoundry = boundary;
                missingExterior = false;
                break;
            }
        }
        if (missingExterior || boundaries.size < 1) {
            throw new GdxRuntimeException("Forgot to add boundary layer to the map");
        }

        // create polygon sprites for water and sand
        EarClippingTriangulator triangulator = new EarClippingTriangulator();
        waterSprites = new Array<PolygonSprite>();
        for (Polygon waterPoly : waterRegions) {
            PolygonRegion polyRegion = new PolygonRegion(
                    LudumDare41.game.assets.waterTextureRegion,
                    waterPoly.getTransformedVertices(),
                    triangulator.computeTriangles(waterPoly.getTransformedVertices()).toArray()
            );
            PolygonSprite sprite = new PolygonSprite(polyRegion);
            sprite.setOrigin(0, 0);
            waterSprites.add(sprite);
        }
        sandSprites = new Array<PolygonSprite>();
        for (Polygon sandPoly : sandRegions) {
            PolygonRegion polyRegion = new PolygonRegion(
                    LudumDare41.game.assets.sandTextureRegion,
                    sandPoly.getTransformedVertices(),
                    triangulator.computeTriangles(sandPoly.getTransformedVertices()).toArray()
            );
            PolygonSprite sprite = new PolygonSprite(polyRegion);
            sprite.setOrigin(0, 0);
            sandSprites.add(sprite);
        }

        circles = collisionLayer.getObjects().getByType(EllipseMapObject.class);
    }

    public Level(GameScreen screen, String mapFileName){
        this(mapFileName);
        this.screen = screen;
        this.hole.screen = screen;
    }

    public void update(float dt){
        for (PinballBumper bumper : pinballBumpers) {
            bumper.update(dt);
        }
        hole.update(dt);
    }

    public void render(SpriteBatch batch, OrthographicCamera camera){
        mapRenderer.setView(camera);
        mapRenderer.render();

        polys.setProjectionMatrix(camera.combined);
        polys.begin();
        {
            for (PolygonSprite sprite : waterSprites) {
                sprite.draw(polys);
            }
            for (PolygonSprite sprite : sandSprites) {
                sprite.draw(polys);
            }
        }
        polys.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        {
            hole.render(batch);
            tee.render(batch);
            for (PinballBumper bumper : pinballBumpers) {
                bumper.render(batch);
            }
        }
        batch.end();

        if (showDebug){
            ShapeRenderer shapes = LudumDare41.game.assets.shapes;
            shapes.begin(ShapeRenderer.ShapeType.Line);
            shapes.setColor(Color.RED);
            shapes.setProjectionMatrix(camera.combined);
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
                    shapes.circle(circle.x + circle.height/2f, circle.y + circle.height/2f, circle.height / 2);
                }

                for (int j = 0; j < waterRegions.size; ++j) {
                    Polygon boundary = waterRegions.get(j);
                    int vertLength = boundary.getVertices().length;
                    for (int i = 0; i < vertLength; i += 2) {
                        tempVector.set(boundary.getTransformedVertices()[i], boundary.getTransformedVertices()[i + 1]);
                        tempVector2.set(boundary.getTransformedVertices()[(i + 2) % vertLength],
                                        boundary.getTransformedVertices()[(i + 3) % vertLength]);
                        shapes.line(tempVector.x, tempVector.y, tempVector2.x, tempVector2.y);
                    }
                }
            }
            shapes.end();
        }
    }


    public boolean canSeeBetween(Vector2 position1, Vector2 position2){
        for (int i = 0; i < boundaries.size; i++){
            Polyline boundary = boundaries.get(i).getPolyline();
            int vertLength = boundary.getVertices().length;
            for (int j = 0; j < vertLength; j += 2) {
                tempVector.set(boundary.getTransformedVertices()[j], boundary.getTransformedVertices()[j + 1]);
                tempVector2.set(boundary.getTransformedVertices()[(j + 2) % vertLength], boundary.getTransformedVertices()[(j + 3) % vertLength]);
                if (Intersector.intersectSegments(position1, position2, tempVector, tempVector2, null)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     *
     * @param oldPosition the position last frame
     * @param newPosition the new position this frame
     * @param radius the radius of the collision object
     * @param collisionPoint this is filled with the intersection point
     * @param normal the normal of the collision for reflections
     * @return the Type of Collision
     */
    public CollisionType checkCollision(Vector2 oldPosition, Vector2 newPosition, float radius, Vector2 collisionPoint, Vector2 normal){
        boolean unresolvedCollisions = false;
        CollisionType collisionType = CollisionType.None;
        collisionPoint.set(newPosition);
        int fuckThisShit = 0;
        do {
            fuckThisShit++;
            unresolvedCollisions = false;
            newPosition.set(collisionPoint);
            for (int j = 0; j < boundaries.size; j++) {
                Polyline boundary = boundaries.get(j).getPolyline();
                boolean collided = false;
                float nearestCollision = Float.MAX_VALUE;
                int vertLength = boundary.getVertices().length;
                for (int i = 0; i < vertLength; i += 2) {
                    tempVector.set(boundary.getTransformedVertices()[i], boundary.getTransformedVertices()[i + 1]);
                    tempVector2.set(boundary.getTransformedVertices()[(i + 2) % vertLength], boundary.getTransformedVertices()[(i + 3) % vertLength]);

                    // Check if the traveling path intersects the segment
                    if (Intersector.intersectSegments(oldPosition, newPosition, tempVector, tempVector2, collisionPoint)) {
                        normal.set(-1 * (tempVector2.y - tempVector.y), (tempVector2.x - tempVector.x));
                        if (Intersector.pointLineSide(tempVector, tempVector2, oldPosition) != Intersector.pointLineSide(tempVector.x, tempVector.y, tempVector2.x, tempVector2.y, collisionPoint.x + normal.x, collisionPoint.y + normal.y)) {
                            normal.set((tempVector2.y - tempVector.y), -1 * (tempVector2.x - tempVector.x));
                        }
                        normal.nor();
                        collisionPoint.add(normal.scl(radius+1f));

                        normal.nor();

                        collided = true;
                        nearestCollision = 0;
                    }

                    // Check if the segments are within the radius of the object
                    float dist = Intersector.distanceSegmentPoint(tempVector, tempVector2, newPosition);
                    if (dist < nearestCollision && dist < radius) {
                        Intersector.nearestSegmentPoint(tempVector, tempVector2, newPosition, collisionPoint);
                        // If it is on an end point bounce back towards where you came from
                        if (collisionPoint.epsilonEquals(tempVector) || collisionPoint.epsilonEquals(tempVector2)) {
                            normal.set(newPosition.x - collisionPoint.x, newPosition.y - collisionPoint.y);
                        } else {
                            // bounce away from the normal of the segment
                            normal.set(-1 * (tempVector2.y - tempVector.y), (tempVector2.x - tempVector.x));
                            if (Intersector.pointLineSide(tempVector, tempVector2, oldPosition) != Intersector.pointLineSide(tempVector.x, tempVector.y, tempVector2.x, tempVector2.y, collisionPoint.x + normal.x, collisionPoint.y + normal.y)) {
                                // Normal is facing the wrong way, flip it
                                normal.set((tempVector2.y - tempVector.y), -1 * (tempVector2.x - tempVector.x));
                            }
                        }
                        normal.nor();
                        collisionPoint.add(normal.scl(radius+1f));

                        normal.nor();

                        collided = true;
                        nearestCollision = dist;
                    }

                }
                if (collided) {
                    unresolvedCollisions = true;
                    if (collisionType != CollisionType.Bumper){
                        collisionType = CollisionType.Wall;
                    }
                }
            }

            for (int i = 0; i < circles.size; i++) {
                Ellipse circle = circles.get(i).getEllipse();
                if (newPosition.dst(circle.x + circle.height / 2f, circle.y + circle.height / 2f) < radius + circle.height / 2f) {
                    normal.set(newPosition);
                    normal.sub(circle.x + circle.height / 2f, circle.y + circle.height / 2f);
                    normal.nor();
                    collisionPoint.set(circle.x + circle.height / 2f, circle.y + circle.height / 2f);
                    normal.scl(radius + circle.height / 2f);
                    collisionPoint.add(normal);
                    normal.nor();
                    unresolvedCollisions = true;
                    if (collisionType != CollisionType.Bumper){
                        collisionType = CollisionType.Wall;
                    }
                }
            }

            for (PinballBumper bumper : pinballBumpers) {
                if (newPosition.dst(bumper.position) < radius + bumper.radius) {
                    bumper.isOn = true;
                    normal.set(newPosition);
                    normal.sub(bumper.position);
                    normal.nor();
                    collisionPoint.set(bumper.position);
                    normal.scl(bumper.radius + radius + 10);
                    collisionPoint.add(normal);
                    normal.nor();
                    unresolvedCollisions = true;
                    collisionType = CollisionType.Bumper;
                }
            }
        }while(unresolvedCollisions && fuckThisShit < 10);

        return collisionType;
    }


}
