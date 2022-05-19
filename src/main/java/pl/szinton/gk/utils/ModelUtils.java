package pl.szinton.gk.utils;

import pl.szinton.gk.math.Vector2i;
import pl.szinton.gk.math.Vector3f;
import pl.szinton.gk.view.Model3D;

import java.util.ArrayList;
import java.util.List;

public class ModelUtils {

    public static Model3D createModel(float[][] verticesData, int[][] edgesData) {
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2i> edges = new ArrayList<>();

        for (float[] vertex : verticesData) {
            if (vertex.length != 3) {
                throw new IllegalArgumentException("Each vertex must contain 3 float values.");
            }
            Vector3f point = new Vector3f(vertex[0], vertex[1], vertex[2]);
            vertices.add(point);
        }
        for (int[] edge : edgesData) {
            if (edge.length != 2) {
                throw new IllegalArgumentException("Each vertex must contain 2 integer values.");
            }
            Vector2i indexPair = new Vector2i(edge[0], edge[1]);
            edges.add(indexPair);
        }

        return new Model3D(vertices, edges);
    }

    public static Model3D createCuboidModel(Vector3f position, Vector3f size) {
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2i> edges = new ArrayList<>();

        float x = position.getX();
        float y = position.getY();
        float z = position.getZ();
        float sx = size.getX();
        float sy = size.getY();
        float sz = size.getZ();
        vertices.add(new Vector3f(x, y, z));
        vertices.add(new Vector3f(x + sx, y, z));
        vertices.add(new Vector3f(x, y, z + sz));
        vertices.add(new Vector3f(x + sx, y, z + sz));
        vertices.add(new Vector3f(x, y + sy, z));
        vertices.add(new Vector3f(x + sx, y + sy, z));
        vertices.add(new Vector3f(x, y + sy, z + sz));
        vertices.add(new Vector3f(x + sx, y + sy, z + sz));

        edges.add(new Vector2i(0, 1));
        edges.add(new Vector2i(0, 2));
        edges.add(new Vector2i(1, 3));
        edges.add(new Vector2i(2, 3));
        edges.add(new Vector2i(4, 5));
        edges.add(new Vector2i(4, 6));
        edges.add(new Vector2i(5, 7));
        edges.add(new Vector2i(6, 7));
        edges.add(new Vector2i(0, 4));
        edges.add(new Vector2i(1, 5));
        edges.add(new Vector2i(2, 6));
        edges.add(new Vector2i(3, 7));

        return new Model3D(vertices, edges);
    }
}
