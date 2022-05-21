package pl.szinton.gk.hsr;

import pl.szinton.gk.math.Vector3f;

public class ActiveEdgeData {
    protected Vector3f startPoint;
    protected Vector3f endPoint;

    ActiveEdgeData(Vector3f startPoint, Vector3f endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }
}
