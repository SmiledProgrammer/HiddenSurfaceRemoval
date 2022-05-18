package pl.szinton.gk;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Model3D {

    private final List<Vector3f> vertices;
    private final List<Vector2i> edges;

    public Model3D(List<Vector3f> vertices, List<Vector2i> edges) {
        this.vertices = new ArrayList<>(vertices);
        this.edges = new ArrayList<>(edges);
    }

    public void render(Graphics2D g, Camera3D camera) {
        for (Vector2i edge : edges) {
            Vector3f point1 = vertices.get(edge.getX());
            Vector3f point2 = vertices.get(edge.getY());
            Vector2i projectedPoint1 = camera.projectPoint(point1);
            Vector2i projectedPoint2 = camera.projectPoint(point2);
            g.drawLine(projectedPoint1.getX(), projectedPoint1.getY(),
                    projectedPoint2.getX(), projectedPoint2.getY());
        }
    }
}
