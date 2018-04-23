package lando.systems.ld41.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class Utils {

    public static Color hsvToRgb(float hue, float saturation, float value, Color outColor) {
        if (outColor == null) outColor = new Color();

        int h = (int) (hue * 6);
        h = h % 6;
        float f = hue * 6 - h;
        float p = value * (1 - saturation);
        float q = value * (1 - f * saturation);
        float t = value * (1 - (1 - f) * saturation);

        switch (h) {
            case 0: outColor.set(value, t, p, 1f); break;
            case 1: outColor.set(q, value, p, 1f); break;
            case 2: outColor.set(p, value, t, 1f); break;
            case 3: outColor.set(p, q, value, 1f); break;
            case 4: outColor.set(t, p, value, 1f); break;
            case 5: outColor.set(value, p, q, 1f); break;
            default: throw new GdxRuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
        }
        return outColor;
    }

    public static boolean doCirclesIntersect(Vector2 center1, float r1, Vector2 center2, float r2) {
        return doCirclesIntersect(center1.x, center1.y, r1, center2.x, center2.y, r2);
    }

    public static boolean doCirclesIntersect(float x1, float y1, float r1, float x2, float y2, float r2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float d2 = dx*dx + dy*dy;
        return (d2 < (r2*r2 + r1*r1));
    }

    // https://stackoverflow.com/a/48542735
    static final Vector2 center = new Vector2();
    static final Vector2 vec1 = new Vector2();
    static final Vector2 vec2 = new Vector2();
    public static boolean overlaps(Polygon polygon, float centerX, float centerY, float radius) {
        float[] vertices = polygon.getTransformedVertices();
        float squareRadius = radius*radius;
        center.set(centerX, centerY);
        for (int i = 0; i < vertices.length; i += 2) {
            if (i == 0) {
                if (Intersector.intersectSegmentCircle(vec1.set(vertices[vertices.length - 2],
                                                                vertices[vertices.length - 1]),
                                                       vec2.set(vertices[i], vertices[i + 1]),
                                                       center, squareRadius)) {
                    return true;
                }
            } else {
                if (Intersector.intersectSegmentCircle(vec1.set(vertices[i - 2], vertices[i - 1]),
                                                       vec2.set(vertices[i], vertices[i + 1]),
                                                       center, squareRadius)) {
                    return true;
                }
            }
        }

        return polygon.contains(center);
    }

}
