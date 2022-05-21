package pl.szinton.gk.hsr;

import pl.szinton.gk.math.Vector3f;
import pl.szinton.gk.view.Camera3D;
import pl.szinton.gk.view.Model3D;
import pl.szinton.gk.view.Plane2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HiddenSurfaceRemoval {

    public static void render(Graphics2D g, Camera3D camera, List<Model3D> objects) {
        // TODO: make this pseudocode actually work
        List<Plane2D> planes = getProjectedObjects(camera, objects);
        List<EdgeData> edgesList = initEdgesList(planes);
//        List<PolygonData> polygonsList = initPolygonsList(planes);
//        int viewWidth = camera.getFrameSize().getX();
//        for (int i = 0; i < viewWidth; i++) {
//            List<ActiveEdgeData> activeEdgesList = initActiveEdgesList(edgesList);
//            sortActiveEdgesList(activeEdgesList, edgesList); //edgesList needed in order to get max-Y values
//            // TODO: go through/iterate over border values of Y coordinates (plane start and plane end)
//            // TODO: fill color based on amount of polygons with "flag on"
//        }
    }

    private static List<Plane2D> getProjectedObjects(Camera3D camera, List<Model3D> objects) {
        List<Plane2D> projectedPlanes = new ArrayList<>();
        for (Model3D model : objects) {
            List<List<Integer>> planes = model.getPlanes();
            List<Vector3f> projectedVertices = model.getVertices().stream()
                    .map(camera::projectPoint).toList();
            for (List<Integer> plane : planes) {
                Set<Integer> planeVerticesOrder = new HashSet<>(plane);
                projectedPlanes.add(new Plane2D(projectedVertices, planeVerticesOrder));
            }
        }
        return projectedPlanes;
    }

    private static List<EdgeData> initEdgesList(List<Plane2D> polygons) {
        List<EdgeData> edgesList = new ArrayList<>();
        for (Plane2D polygon : polygons) {
            Vector3f[] vertices = polygon.getSortedVertices();
            for (int i=1; i < vertices.length; i++) {
                edgesList.add(new EdgeData(vertices[i - 1], vertices[i]));
            }
            edgesList.add(new EdgeData(vertices[vertices.length - 1], vertices[0]));
        }
        return edgesList;
    }

    private static List<ActiveEdgeData> initActiveEdgesList(List<EdgeData> edgesList, int scanline) {
        //List<EdgeData>
        throw new UnsupportedOperationException();
    }
}
