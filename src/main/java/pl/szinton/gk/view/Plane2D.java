package pl.szinton.gk.view;

import pl.szinton.gk.math.Vector3f;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Plane2D {

    private final List<Vector3f> vertices;
    private final List<Integer> verticesOrder;
    private final Color color;

    public Plane2D(List<Vector3f> vertices, List<Integer> verticesOrder) {
        this.vertices = new ArrayList<>(vertices);
        this.verticesOrder = new ArrayList<>(verticesOrder);

        int seed = generateSeed(verticesOrder);
        Random random = new Random(seed);
        int redValue = random.nextInt(130) + 126;
        int blueValue = random.nextInt(130) + 126;
        this.color = new Color(redValue, 0, blueValue);
        System.out.println(color);
    }

    private int generateSeed(List<Integer> verticesOrder) {
        int seed = 0;
        for (int i = 0; i < verticesOrder.size(); i++) {
            seed += i * verticesOrder.get(i) * verticesOrder.get(i) * verticesOrder.get(i);
        }
        return seed;
    }

    public Vector3f[] getSortedVertices(){
        Vector3f[] sortedVertices = new Vector3f[vertices.size()];
        for(Integer index : verticesOrder) {
            sortedVertices[index] = vertices.get(index);
        }
        return sortedVertices;
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
