package pl.szinton.gk.hsr;

import pl.szinton.gk.view.Camera3D;
import pl.szinton.gk.view.Model3D;

import java.awt.*;
import java.util.List;

public class HiddenSurfaceRemoval {

    public static void render(Graphics2D g, Camera3D camera, List<Model3D> objects) {
//        // TODO: make this pseudocode actually work
//        List<ProjectedModel> projectedObjects = getProjectedObjects(camera, objects);
//        List<EdgeData> edgesList = initEdgesList(projectedObjects);
//        List<PolygonData> polygonsList = initPolygonsList(projectedObjects);
//        int viewWidth = camera.getFrameSize().getX();
//        for (int i = 0; i < viewWidth; i++) {
//            List<ActiveEdgeData> activeEdgesList = initActiveEdgesList(projectedObjects);
//            sortActiveEdgesList(activeEdgesList, edgesList); //edgesList needed in order to get max-Y values
//            // TODO: go through/iterate over border values of Y coordinates (plane start and plane end)
//            // TODO: fill color based on amount of polygons with "flag on"
//        }
    }
}
