package pl.szinton.gk.hsr;

import pl.szinton.gk.math.Vector3f;
import pl.szinton.gk.view.Camera3D;
import pl.szinton.gk.view.Model3D;
import pl.szinton.gk.view.Plane2D;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class HiddenSurfaceRemoval {

    private final static Color DEFAULT_BACKGROUND_COLOR = new Color(150, 150, 150);

    public static void render(Graphics2D g, Camera3D camera, List<Model3D> objects) {
        render(g, camera, objects, DEFAULT_BACKGROUND_COLOR);
    }

    public static void render(Graphics2D g, Camera3D camera, List<Model3D> objects, Color backgroundColor) {
        List<Plane2D> planes = getProjectedObjects(camera, objects);
        int viewWidth = camera.getFrameSize().getX();
        int viewHeight = camera.getFrameSize().getY();
        for (int y = 0; y < viewHeight; y++) {
            List<PlaneIntersection> intersections = findPlaneIntersections(planes, y);
            sortIntersectionsByX(intersections);
            boolean[] cip = new boolean[planes.size()]; // cip - currently intersecting planes
            fillScanLine(g, 0f, viewWidth, y, backgroundColor, viewHeight);
            float startX = intersections.size() > 0 ? intersections.get(0).point().getX() : 0f;
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
                fillScanLine(g, startX, endX, y, fillColor, viewHeight);
                startX = endX;
            }
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
                    intersections.add(new PlaneIntersection(intersectionPoint, i));
                }
            }
        }
        return intersections;
    }

    private static Vector3f findIntersectionPoint(Vector3f edgeStartPoint, Vector3f edgeEndPoint, int scanLineY) {
        // note: treat scanLine as infinite line (prosta) not as segment line (odcinek) (this way we'll omit mistakes later in the algorithm)
        if (edgeEndPoint.getY() == edgeStartPoint.getY())
            return null;        // jeśli linia jest pozioma zwracam null
        float maxY = Math.max(edgeEndPoint.getY(), edgeStartPoint.getY());
        float minY = maxY == edgeEndPoint.getY() ? edgeStartPoint.getY() : edgeEndPoint.getY();
        if (maxY < scanLineY || minY > scanLineY)
            return null;
        if (edgeEndPoint.getX() == edgeStartPoint.getX())
            return new Vector3f(edgeEndPoint.getX(), scanLineY, edgeEndPoint.getZ());
        // pobieram Z z punktu końcowego, w przypadku prostopadłościanu na pewno będzie ok, nwm jak z innymi bryłami
        // gdzie ściany nie są prostopadłe
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
        float minZ = Float.MIN_VALUE; // TODO: check if shouldn't be min instead
        int i = 0;
        int minZIndex = 0;
        for (Plane2D plane : planes) {
            if (cip[i]) {
                float zOfTheFirstVertice = plane.getVertices().get(0).getZ();
                if (zOfTheFirstVertice < minZ) {
                    minZ = zOfTheFirstVertice;
                    minZIndex = i;
                }
            }
            i++;
        }
        return planes.get(minZIndex).getColor();
    }

    private static void fillScanLine(Graphics2D g, float startX, float endX, int scanLineY, Color color, int viewHeight) {
        g.setColor(color);
        g.drawLine((int) startX, viewHeight - scanLineY, (int) endX, viewHeight - scanLineY);
    }

    private static Color getRandomColor() {
        Random rand = new Random();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        return new Color(r, g, b);
    }
}
