package pl.szinton.gk.hsr;

import pl.szinton.gk.math.Vector3f;
import pl.szinton.gk.view.Camera3D;
import pl.szinton.gk.view.Model3D;
import pl.szinton.gk.view.Plane2D;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class HiddenSurfaceRemoval {

    private final static Color DEFAULT_BACKGROUND_COLOR = new Color(150, 150, 150);

    public static void render(Graphics2D g, Camera3D camera, List<Model3D> objects) {
        List<Plane2D> planes = getProjectedObjects(camera, objects);
        int viewWidth = camera.getFrameSize().getX();
        int viewHeight = camera.getFrameSize().getY();
        for (int y = 0; y < viewHeight; y++) {
            analyzeScanline(y, g, viewWidth, viewHeight, planes);
        }
    }

    private static List<Plane2D> getProjectedObjects(Camera3D camera, List<Model3D> objects) {
        List<Plane2D> projectedPlanes = new ArrayList<>();
        for (Model3D model : objects) {
            List<List<Integer>> planes = model.getPlanes();
            List<Vector3f> projectedVertices = model.getVertices().stream()
                    .map(camera::projectPoint).toList();
            for (List<Integer> plane : planes) {
                List<Integer> planeVerticesOrder = new ArrayList<>(plane);
                List<Vector3f> planeVertices = planeVerticesOrder.stream()
                        .map(projectedVertices::get)
                        .collect(Collectors.toList());
                projectedPlanes.add(new Plane2D(planeVertices, planeVerticesOrder));
            }
        }
        return projectedPlanes;
    }

    private static void analyzeScanline(int scanLineY, Graphics2D g, int viewWidth, int viewHeight, List<Plane2D> planes) {
        List<PlaneIntersection> intersections = findPlaneIntersections(planes, scanLineY);
        sortIntersectionsByX(intersections);
        boolean[] cip = new boolean[planes.size()]; // cip - currently intersecting planes
        fillScanLine(g, new Vector3f(), new Vector3f(viewWidth, 0f, 0f), scanLineY, DEFAULT_BACKGROUND_COLOR, viewHeight);
        if (intersections.size() > 0) {
            if (scanLineY == 300) {
                System.out.print("");
            }
            Vector3f startPoint = intersections.get(0).point();
            int firstPlaneId = intersections.get(0).planeId();
            cip[firstPlaneId] = !cip[firstPlaneId];
            for (int i = 1; i < intersections.size(); i++) {
                PlaneIntersection intersection = intersections.get(i);
                int planeId = intersection.planeId();
                Color fillColor = getFillColor(planes, cip);
                Vector3f endPoint = intersection.point();
                fillScanLine(g, startPoint, endPoint, scanLineY, fillColor, viewHeight);
                cip[planeId] = !cip[planeId];
                startPoint = endPoint;
            }
        }
    }

    private static Color getFillColor(List<Plane2D> planes, boolean[] cip) {
        int cipCount = countCurrentlyIntersectingPlanes(cip);
        return switch (cipCount) {
            case 0 -> DEFAULT_BACKGROUND_COLOR; // unneeded imo
            case 1 -> planes.get(getIndexOfFirstTrue(cip)).getColor();
            default -> getColorOfMostInFrontPlane(planes, cip);
        };
    }

    private static int getIndexOfFirstTrue(boolean[] arr) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i]) {
                return i;
            }
        }
        return -1;
    }

    private static List<PlaneIntersection> findPlaneIntersections(List<Plane2D> planes, int scanLineY) {
        List<PlaneIntersection> intersections = new ArrayList<>();
        for (int i = 0; i < planes.size(); i++) {
            Plane2D plane = planes.get(i);
            List<Vector3f> vertices = plane.getVertices();
            List<Integer> order = plane.getVerticesOrder();
            for (int j = 0; j < order.size(); j++) {
                Vector3f edgeStart = vertices.get(j);
                int endIndex = (j + 1 < order.size()) ? j + 1 : 0;
                Vector3f edgeEnd = vertices.get(endIndex);
                Vector3f intersectionPoint = findIntersectionPoint(edgeStart, edgeEnd, scanLineY);
                if (intersectionPoint != null) {
                    intersections.add(new PlaneIntersection(intersectionPoint, i, edgeStart, edgeEnd));
                }
            }
        }
        return intersections;
    }

    private static Vector3f findIntersectionPoint(Vector3f edgeStartPoint, Vector3f edgeEndPoint, int scanLineY) {
        if (edgeEndPoint.getY() == edgeStartPoint.getY())
            return null;
        float maxY = Math.max(edgeEndPoint.getY(), edgeStartPoint.getY());
        float minY = maxY == edgeEndPoint.getY() ? edgeStartPoint.getY() : edgeEndPoint.getY();
        if (maxY < scanLineY || minY > scanLineY)
            return null;
        if (edgeEndPoint.getX() == edgeStartPoint.getX())
            return new Vector3f(edgeEndPoint.getX(), scanLineY, edgeEndPoint.getZ());
        float invertedSlope = (edgeEndPoint.getX() - edgeStartPoint.getX()) / (edgeEndPoint.getY() - edgeStartPoint.getY());
        float b = (edgeEndPoint.getY() - edgeEndPoint.getX() / invertedSlope);
        float x = (scanLineY - b) * invertedSlope;
        return new Vector3f(x, scanLineY, edgeEndPoint.getZ());
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
        float minZ = Float.MAX_VALUE;
        int i = 0;
        int minZIndex = 0;
        for (Plane2D plane : planes) {
            if (cip[i]) {
                float gravityCenterZ = gravityCenter(plane).getZ();
                if (gravityCenterZ < minZ) {
                    minZ = gravityCenterZ;
                    minZIndex = i;
                }
            }
            i++;
        }
        return planes.get(minZIndex).getColor();
    }

    private static void fillScanLine(Graphics2D g, Vector3f startPoint, Vector3f endPoint, int scanLineY, Color color, int viewHeight) {
        g.setColor(color);
        g.drawLine((int) startPoint.getX(), viewHeight - scanLineY, (int) endPoint.getX(), viewHeight - scanLineY);
    }

    private static Vector3f gravityCenter(Plane2D polygon) {
        float x = 0f, y = 0f, z = 0f;
        for (Vector3f vertice : polygon.getVertices()) {
            x += vertice.getX();
            y += vertice.getY();
            z += vertice.getZ();
        }
        x = x / polygon.getVertices().size();
        y = y / polygon.getVertices().size();
        z = z / polygon.getVertices().size();

        return new Vector3f(x, y, z);
    }
}
