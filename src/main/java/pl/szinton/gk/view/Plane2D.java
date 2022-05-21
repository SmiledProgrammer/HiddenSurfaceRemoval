package pl.szinton.gk.view;

import pl.szinton.gk.math.Vector3f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record Plane2D(List<Vector3f> vertices,
                      Set<Integer> verticesOrder) {

    public Plane2D(List<Vector3f> vertices, Set<Integer> verticesOrder) {
        this.vertices = new ArrayList<>(vertices);
        this.verticesOrder = new HashSet<>(verticesOrder);
    }


    public Vector3f[] getSortedVertices(){
        Vector3f[] sortedVertices = new Vector3f[vertices.size()];
        for(Integer index : verticesOrder) {
            sortedVertices[index] = vertices.get(index);
        }
        return sortedVertices;
    }
}
