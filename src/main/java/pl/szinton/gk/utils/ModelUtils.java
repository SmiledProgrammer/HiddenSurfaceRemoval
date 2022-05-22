package pl.szinton.gk.utils;

import pl.szinton.gk.math.Vector2i;
import pl.szinton.gk.math.Vector3f;
import pl.szinton.gk.view.Model3D;

import java.util.ArrayList;
import java.util.List;

public class ModelUtils {

    public static Model3D createCuboidModel(Vector3f position, Vector3f size) {
        List<Vector3f> vertices = new ArrayList<>();
        List<Vector2i> edges = new ArrayList<>();
        List<List<Integer>> planes = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            planes.add(new ArrayList<>());
        }

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

        //bottom rectangle
        planes.get(0).add(2);
        planes.get(0).add(3);
        planes.get(0).add(1);
        planes.get(0).add(0);

        //top rectangle
        planes.get(1).add(4);
        planes.get(1).add(5);
        planes.get(1).add(7);
        planes.get(1).add(6);

        //-z rectangle
        planes.get(2).add(5);
        planes.get(2).add(4);
        planes.get(2).add(0);
        planes.get(2).add(1);

        //+z rectangle
        planes.get(3).add(6);
        planes.get(3).add(7);
        planes.get(3).add(3);
        planes.get(3).add(2);

        //-x rectangle
        planes.get(4).add(7);
        planes.get(4).add(5);
        planes.get(4).add(1);
        planes.get(4).add(3);

        //+x rectangle
        planes.get(5).add(4);
        planes.get(5).add(6);
        planes.get(5).add(2);
        planes.get(5).add(0);

        return new Model3D(vertices, edges, planes);
    }
}
