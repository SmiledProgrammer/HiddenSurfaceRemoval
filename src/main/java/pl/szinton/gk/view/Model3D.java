package pl.szinton.gk.view;

import pl.szinton.gk.math.Vector2i;
import pl.szinton.gk.math.Vector3f;
import pl.szinton.gk.utils.MatrixUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Model3D {

    private List<Vector3f> vertices;
    private final List<Vector2i> edges; //stores vertex pairs that form an edge
    private final List<List<Integer>> planes; //stores vertex lists that form a plane

    public Model3D(List<Vector3f> vertices, List<Vector2i> edges, List<List<Integer>> planes) {
        this.vertices = new ArrayList<>(vertices);
        this.edges = new ArrayList<>(edges);
        this.planes = new ArrayList<>(planes);
    }

    public void render(Graphics2D g, Camera3D camera) {
        for (Vector2i edge : edges) {
            Vector3f point1 = vertices.get(edge.getX());
            Vector3f point2 = vertices.get(edge.getY());
            Vector3f projectedPoint1 = camera.projectPoint(point1);
            Vector3f projectedPoint2 = camera.projectPoint(point2);
            Vector2i transformedPoint1 = MatrixUtils.convertVector3fToVector2i(projectedPoint1);
            Vector2i transformedPoint2 = MatrixUtils.convertVector3fToVector2i(projectedPoint2);
            g.drawLine(transformedPoint1.getX(), transformedPoint1.getY(),
                    transformedPoint2.getX(), transformedPoint2.getY());
        }
    }

    public void setVertices(List<Vector3f> vertices) {
        this.vertices = new ArrayList<>(vertices);
    }

    public List<Vector3f> getVertices() {
        return vertices;
    }

    public List<Vector2i> getEdges() {
        return edges;
    }

    public List<List<Integer>> getPlanes() {
        return planes;
    }
}
