package pl.szinton.gk.view;

import pl.szinton.gk.math.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Plane2D {

    private final List<Vector3f> vertices;
    private final List<Integer> verticesOrder;
    private final Color color;

    public Plane2D(List<Vector3f> vertices, List<Integer> verticesOrder, int seedSalt) {
        this.vertices = new ArrayList<>(vertices);
        this.verticesOrder = new ArrayList<>(verticesOrder);

        int seed = generateSeed(verticesOrder);
        Random random = new Random(seed + seedSalt);
        int redValue = random.nextInt(130) + 126;
        int blueValue = random.nextInt(130) + 126;
        this.color = new Color(redValue, 0, blueValue);
    }

    private int generateSeed(List<Integer> verticesOrder) {
        int seed = 0;
        for (int i = 0; i < verticesOrder.size(); i++) {
            seed += i * verticesOrder.get(i) * verticesOrder.get(i) * verticesOrder.get(i);
        }
        return (seed * 31) / 17;
    }

    public Vector3f normalVector() {
        Vector3f p1 = vertices.get(0);
        Vector3f p2 = vertices.get(1);
        Vector3f p3 = vertices.get(2);
        Vector3f u = new Vector3f(p2.getX() - p1.getX(), p2.getY() - p1.getY(), p2.getZ() - p1.getZ());
        Vector3f v = new Vector3f(p3.getX() - p1.getX(), p3.getY() - p1.getY(), p3.getZ() - p1.getZ());
        return Vector3f.crossProduct(u, v);
    }

    public List<Vector3f> getVertices() {
        return vertices;
    }

    public List<Integer> getVerticesOrder() {
        return verticesOrder;
    }

    public Color getColor() {
        return color;
    }
}
