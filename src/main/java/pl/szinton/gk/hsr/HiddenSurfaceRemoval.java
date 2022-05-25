package pl.szinton.gk.hsr;

import pl.szinton.gk.math.Vector2i;
import pl.szinton.gk.math.Vector3f;
import pl.szinton.gk.view.Camera3D;
import pl.szinton.gk.view.Model3D;
import pl.szinton.gk.view.Plane2D;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HiddenSurfaceRemoval {

    private final static Color DEFAULT_BACKGROUND_COLOR = new Color(150, 150, 150);

    private final static Vector3f cameraVector = new Vector3f(0f, 0f, -1f);

    public static void render(Graphics2D g, Camera3D camera, List<Model3D> objects) {
        List<Plane2D> planes = getProjectedObjects(camera, objects);
        List<Vector2i> planeComparisons = new ArrayList<>(); // stores a pair of plane ids that were already compared (first id belongs to plane closer to camera)
        int viewWidth = camera.getFrameSize().getX();
        int viewHeight = camera.getFrameSize().getY();
        for (int y = 0; y < viewHeight; y++) {
            analyzeScanline(y, g, viewWidth, viewHeight, planes, planeComparisons);
        }
    }

    private static List<Plane2D> getProjectedObjects(Camera3D camera, List<Model3D> objects) {
        List<Plane2D> projectedPlanes = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            Model3D model = objects.get(i);
            List<List<Integer>> planes = model.getPlanes();
            List<Vector3f> projectedVertices = model.getVertices().stream()
                    .map(camera::projectPoint).toList();
            for (List<Integer> plane : planes) {
                List<Integer> planeVerticesOrder = new ArrayList<>(plane);
                List<Vector3f> planeVertices = planeVerticesOrder.stream()
                        .map(projectedVertices::get)
                        .collect(Collectors.toList());
                projectedPlanes.add(new Plane2D(planeVertices, planeVerticesOrder, i));
            }
        }
        return projectedPlanes;
    }

    private static void analyzeScanline(int scanLineY, Graphics2D g, int viewWidth, int viewHeight,
                                        List<Plane2D> planes, List<Vector2i> planeComparisons) {
        List<PlaneIntersection> intersections = findPlaneIntersections(planes, scanLineY);
        sortIntersectionsByX(intersections);
        boolean[] cip = new boolean[planes.size()]; // cip - currently intersecting planes
        fillScanLine(g, 0f, viewWidth, scanLineY, DEFAULT_BACKGROUND_COLOR, viewHeight);
        if (intersections.size() > 0) {
            Vector3f startPoint = new Vector3f();
            for (int i = 0; i < intersections.size(); i++) {
                PlaneIntersection intersection = intersections.get(i);
                int planeId = intersection.planeId();
                Color fillColor = getFillColor(planes, planeComparisons, cip, intersections, i);
                Vector3f endPoint = intersection.point();
                fillScanLine(g, startPoint.getX(), endPoint.getX(), scanLineY, fillColor, viewHeight);
                cip[planeId] = !cip[planeId];
                startPoint = endPoint;

                if ((int) startPoint.getX() == (int) endPoint.getX()) {
                    boolean hasNoMoreIntersections = planeHasNoMoreIntersections(intersections, i);
                    if (cip[planeId] && hasNoMoreIntersections) {
                        cip[planeId] = false;
                    } else if (!cip[planeId] && !hasNoMoreIntersections) {
                        cip[planeId] = true;
                    }
                }
            }
        }
    }

    private static boolean planeHasNoMoreIntersections(List<PlaneIntersection> intersections, int intersectionIndex) {
        int planeId = intersections.get(intersectionIndex).planeId();
        for (int i = intersectionIndex + 1; i < intersections.size(); i++) {
            int otherPlaneId = intersections.get(i).planeId();
            if (planeId == otherPlaneId) {
                return false;
            }
        }
        return true;
    }

    private static Color getFillColor(List<Plane2D> planes, List<Vector2i> planeComparisons,
                                      boolean[] cip, List<PlaneIntersection> intersections, int intersectionIndex) {
        int cipCount = countCurrentlyIntersectingPlanes(cip);
        return switch (cipCount) {
            case 0 -> DEFAULT_BACKGROUND_COLOR;
            case 1 -> planes.get(getIndexOfFirstTrue(cip)).getColor();
            default -> getColorOfMostInFrontPlane(planes, planeComparisons, cip, intersections, intersectionIndex);
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

    private static Color getColorOfMostInFrontPlane(List<Plane2D> planes, List<Vector2i> planeComparisons,
                                                    boolean[] cip, List<PlaneIntersection> intersections, int intersectionIndex) {
        float startX = intersections.get(intersectionIndex).edgeStart().getX();
        float endX = intersections.get(intersectionIndex).edgeEnd().getX();
        ClosestPlaneFound closest = new ClosestPlaneFound();
        for (PlaneIntersection intersection : intersections) {
            int planeId = intersection.planeId();
            if (cip[planeId]) {
                checkPlane(planes, planeComparisons, closest, intersection, startX, endX);
            }
        }
        if (closest.index != -1)
            return planes.get(closest.index).getColor();
        else
            return DEFAULT_BACKGROUND_COLOR;
    }

    private static void checkPlane(List<Plane2D> planes, List<Vector2i> planeComparisons, ClosestPlaneFound closest,
                                   PlaneIntersection intersection, float startX, float endX) {
        int planeId = intersection.planeId();
        int planeOnTop = planesAlreadyCompared(planeComparisons, planeId, closest.index);
        if (planeOnTop == -1) {
            Plane2D plane = planes.get(planeId);
            float planeDotProduct = planeToCameraDotProduct(plane);
            if (planeDotProduct < 1f) {
                float planeMinZ = round(getMinZOfPlaneVertices(plane, startX, endX));
                if (planeMinZ < closest.minZ) {
                    planeComparisons.add(new Vector2i(planeId, closest.index));
                    closest.update(planeMinZ, planeDotProduct, planeId);
                } else if (planeMinZ == closest.minZ) {
                    if (planeDotProduct < closest.dotProduct) {
                        planeComparisons.add(new Vector2i(planeId, closest.index));
                        closest.update(planeMinZ, planeDotProduct, planeId);
                    }
                }
            }
        } else {
            if (planeOnTop != closest.index) {
                Plane2D plane = planes.get(planeId);
                float planeMinZ = round(getMinZOfPlaneVertices(plane, startX, endX));
                float planeDotProduct = planeToCameraDotProduct(plane);
                closest.update(planeMinZ, planeDotProduct, planeId);
            }
        }
    }

    private static float planeToCameraDotProduct(Plane2D plane) {
        Vector3f planeVector = plane.normalVector().unitVector();
        return -Vector3f.dotProduct(cameraVector, planeVector);
    }

    private static int planesAlreadyCompared(List<Vector2i> planeComparisons, int planeId1, int planeId2) {
        for (Vector2i planePair : planeComparisons) {
            int id1 = planePair.getX();
            int id2 = planePair.getY();
            if (id1 == planeId1 && id2 == planeId2) {
                return planeId1;
            } else if (id1 == planeId2 && id2 == planeId1) {
                return planeId2;
            }
        }
        return -1;
    }

    private static float getMinZOfPlaneVertices(Plane2D plane, float x1, float x2) {
        float roundX1 = round(x1);
        float roundX2 = round(x2);
        double minZ = plane.getVertices().stream()
                .filter(v -> {
                    float roundVertexX = round(v.getX());
                    return roundVertexX == roundX1 ||
                            roundVertexX == roundX2;
                })
                .mapToDouble(Vector3f::getZ)
                .min().orElse(Double.NaN);
        if (Double.isNaN(minZ)) {
            return (float) plane.getVertices().stream()
                    .mapToDouble(Vector3f::getZ)
                    .min().orElse(Double.NaN);
        } else {
            return (float) minZ;
        }
    }

    private static float round(float value) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(4, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    private static void fillScanLine(Graphics2D g, float startX, float endX, int scanLineY, Color color, int viewHeight) {
        g.setColor(color);
        g.drawLine((int) startX, scanLineY, (int) endX, scanLineY);
    }

    private static class ClosestPlaneFound {
        float minZ = Float.MAX_VALUE;
        float dotProduct = 0f;
        int index = -1;

        void update(float minZ, float dotProduct, int planeIndex) {
            this.minZ = minZ;
            this.dotProduct = dotProduct;
            this.index = planeIndex;
        }
    }
}
