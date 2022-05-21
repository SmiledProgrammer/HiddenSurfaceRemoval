package pl.szinton.gk.hsr;

import pl.szinton.gk.math.Vector3f;

class EdgeData extends ActiveEdgeData{
    private float deltaX;
    private float deltaY;
    private float maxY;

    EdgeData(Vector3f startPoint, Vector3f endPoint) {
        super(startPoint, endPoint);
        this.deltaY = endPoint.getY() - startPoint.getY();
        this.deltaX = endPoint.getX() - startPoint.getX();

        this.maxY = endPoint.getY() > startPoint.getY() ? endPoint.getY() : startPoint.getY();
    }
}
