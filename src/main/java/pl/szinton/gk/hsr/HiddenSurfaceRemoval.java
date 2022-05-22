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
        render(g, camera, objects, DEFAULT_BACKGROUND_COLOR);
    }

    public static void render(Graphics2D g, Camera3D camera, List<Model3D> objects, Color backgroundColor) {
        List<Plane2D> planes = getProjectedObjects(camera, objects);
        int viewWidth = camera.getFrameSize().getX();
        int viewHeight = camera.getFrameSize().getY();
        for (int y = 0; y < viewHeight; y++) {
            analyzeScanline(y, g, viewWidth, viewHeight, planes, backgroundColor);
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

    private static void analyzeScanline(int scanLineY, Graphics2D g, int viewWidth, int viewHeight, List<Plane2D> planes, Color backgroundColor) {
        List<PlaneIntersection> intersections = findPlaneIntersections(planes, scanLineY);
        sortIntersectionsByX(intersections);
        boolean[] cip = new boolean[planes.size()]; // cip - currently intersecting planes
        fillScanLine(g, new Vector3f(), new Vector3f(viewWidth, 0f, 0f), scanLineY, backgroundColor, viewHeight);
        if (intersections.size() > 0) {
            Vector3f startPoint = intersections.get(0).point();
            int firstPlaneId = intersections.get(0).planeId();
            cip[firstPlaneId] = !cip[firstPlaneId];
//            Color color = planes.get(firstPlaneId).getColor();
//            fillScanLine(g, startPoint, intersections.get(0).point(), scanLineY, color, viewHeight);
//            startPoint = intersections.get(0).point();
            for (int i = 1; i < intersections.size(); i++) {
                if (scanLineY == 300) {
                    System.out.print("");
                }
                PlaneIntersection intersection = intersections.get(i);
                int planeId = intersection.planeId();
                int cipCount = countCurrentlyIntersectingPlanes(cip);
                Color fillColor = switch (cipCount) {
                    case 0 -> backgroundColor; // unneeded imo
                    case 1 -> planes.get(getIndexOfFirstTrue(cip)).getColor();
                    default -> getColorOfMostInFrontPlane(planes, cip, scanLineY, intersections, i);
                };
                Vector3f endPoint = intersection.point();
                fillScanLine(g, startPoint, endPoint, scanLineY, fillColor, viewHeight);
                cip[planeId] = !cip[planeId];
                startPoint = endPoint;
            }
        }
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

    private static Color getColorOfMostInFrontPlane(List<Plane2D> planes, boolean[] cip, int scanLineY,
                                                    List<PlaneIntersection> intersections, int intersectionIndex) {
        float startX = intersections.get(intersectionIndex - 1).point().getX();
        float endX = intersections.get(intersectionIndex).point().getX();

        float minZ = Float.MAX_VALUE; // TODO: check if shouldn't be min instead
        int minZIndex = 0;
        for (PlaneIntersection intersection : intersections) {
            int planeId = intersection.planeId();
            if (cip[planeId]) {
                float startZ = findZOfPlane(planes.get(planeId), startX, scanLineY);
                float endZ = findZOfPlane(planes.get(planeId), endX, scanLineY);
                float planeMinZ = Math.min(startZ, endZ);
                float intersectionZ = intersection.point().getZ(); // TODO: remove
//                System.out.println("intersection: " + intersectionZ + "; start: " + startZ + "; end: " + endZ);
                if (planeMinZ < minZ) {
                    minZ = planeMinZ;
                    minZIndex = planeId;
                }
            }
        }
        return planes.get(minZIndex).getColor();
    }

    private static float findZOfPlane(Plane2D plane, float x, int y) {
        Vector3f point1 = plane.getVertices().get(0);
        Vector3f point2 = plane.getVertices().get(1);
        Vector3f point3 = plane.getVertices().get(2);
        float x1 = point1.getX();
        float y1 = point1.getY();
        float z1 = point1.getZ();
        float x2 = point2.getX();
        float y2 = point2.getY();
        float z2 = point2.getZ();
        float x3 = point3.getX();
        float y3 = point3.getY();
        float z3 = point3.getZ();

        float a1 = x2 - x1;
        float b1 = y2 - y1;
        float c1 = z2 - z1;
        float a2 = x3 - x1;
        float b2 = y3 - y1;
        float c2 = z3 - z1;
        float a = b1 * c2 - b2 * c1;
        float b = a2 * c1 - a1 * c2;
        float c = a1 * b2 - b1 * a2;
        float d = (-a * x1 - b * y1 - c * z1);

        return (-a * x - b * y - d) / c;
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

    private static Color getRandomColor() {
        Random rand = new Random();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        return new Color(r, g, b);
    }
}
