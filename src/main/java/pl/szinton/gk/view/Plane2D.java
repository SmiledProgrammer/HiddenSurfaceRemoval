package pl.szinton.gk.view;

import pl.szinton.gk.math.Vector3f;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Plane2D {

    private final List<Vector3f> vertices;
    private final Set<Integer> verticesOrder;
    private final Color color;

    public Plane2D(List<Vector3f> vertices, Set<Integer> verticesOrder) {
        this.vertices = new ArrayList<>(vertices);
        this.verticesOrder = new HashSet<>(verticesOrder);

        Random random = new Random();
        int redValue = random.nextInt(130) + 126;
        int blueValue = random.nextInt(130) + 126;
        this.color = new Color(redValue, 0, blueValue);
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

    public Set<Integer> getVerticesOrder() {
        return verticesOrder;
    }

    public Color getColor() {
        return color;
    }
}
