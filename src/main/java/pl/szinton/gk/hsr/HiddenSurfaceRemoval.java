package pl.szinton.gk.hsr;

import pl.szinton.gk.math.Vector3f;
import pl.szinton.gk.view.Camera3D;
import pl.szinton.gk.view.Model3D;
import pl.szinton.gk.view.Plane2D;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

public class HiddenSurfaceRemoval {

    @Deprecated
    public static void deprecated_render(Graphics2D g, Camera3D camera, List<Model3D> objects) {
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

    public static void render(Graphics2D g, Camera3D camera, List<Model3D> objects) {
        Color defaultBackgroundColor = new Color(200, 230, 255);
        render(g, camera, objects, defaultBackgroundColor);
    }

    public static void render(Graphics2D g, Camera3D camera, List<Model3D> objects, Color backgroundColor) {
        List<Plane2D> planes = getProjectedObjects(camera, objects);
        int viewWidth = camera.getFrameSize().getX();
        int viewHeight = camera.getFrameSize().getY();
        for (int y = 0; y < viewHeight; y++) {
            List<PlaneIntersection> intersections = findPlaneIntersections(planes, y);
            sortIntersectionsByX(intersections);
            boolean[] cip = new boolean[planes.size()]; // cip - currently intersecting planes
            float startX = 0f;
            for (PlaneIntersection intersection : intersections) {
                int planeId = intersection.planeId();
                cip[planeId] = !cip[planeId];
                int cipCount = countCurrentlyIntersectingPlanes(cip);
                float endX = intersection.point().getX();
                Color fillColor = switch (cipCount) {
                    case 0 -> backgroundColor;
                    case 1 -> planes.get(intersection.planeId()).getColor();
                    default -> getColorOfMostInFrontPlane(planes, cip);
                };
                fillScanLine(g, startX, endX, y, fillColor);
                startX = endX;
            }
            fillScanLine(g, startX, viewWidth, y, backgroundColor);
        }
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

    private static List<PlaneIntersection> findPlaneIntersections(List<Plane2D> planes, int scanLineY) {
        List<PlaneIntersection> intersections = new ArrayList<>();
        for (int i = 0; i < planes.size(); i++) {
            Plane2D plane = planes.get(i);
            List<Vector3f> vertices = plane.getVertices();
            Set<Integer> order = plane.getVerticesOrder();
            for (int j = 0; j < order.size(); j++) {
                Vector3f edgeStart = vertices.get(j);
                int endIndex = (j + 1 < order.size()) ? j + 1 : 0;
                Vector3f edgeEnd = vertices.get(endIndex);
                Vector3f intersectionPoint = findIntersectionPoint(edgeStart, edgeEnd, scanLineY);
                if (intersectionPoint != null) {
                    intersections.add(new PlaneIntersection(intersectionPoint, i));
                }
            }
        }
        return intersections;
    }

    private static Vector3f findIntersectionPoint(Vector3f edgeStartPoint, Vector3f edgeEndPoint, int scanLineY) {
        throw new UnsupportedOperationException(); // TODO
        // note: treat scanLine as infinite line (prosta) not as segment line (odcinek) (this way we'll omit mistakes later in the algorithm)
    }

    private static void sortIntersectionsByX(List<PlaneIntersection> intersections) {
        intersections.sort((pi1, pi2) -> {
            float x1 = pi1.point().getX();
            float x2 = pi2.point().getX();
            return Float.compare(x1, x2);
        });
    }

    private static int countCurrentlyIntersectingPlanes(boolean[] cip) {
        int count = 0;
        for (boolean b : cip) {
            if (b) {
                count++;
            }
        }
        return count;
    }

    private static Color getColorOfMostInFrontPlane(List<Plane2D> planes, boolean[] cip) {
        float minZ = Float.MIN_VALUE; // TODO: check if shouldn't be max instead
        throw new UnsupportedOperationException(); // TODO
    }

    private static void fillScanLine(Graphics2D g, float startX, float endX, int scanLineY, Color color) {
        g.setColor(color);
        g.drawLine((int) startX, -scanLineY, (int) endX, -scanLineY);
    }

    @Deprecated
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

    @Deprecated
    private static List<ActiveEdgeData> initActiveEdgesList(List<EdgeData> edgesList, int scanline) {
        //List<EdgeData>
        throw new UnsupportedOperationException();
    }
}
