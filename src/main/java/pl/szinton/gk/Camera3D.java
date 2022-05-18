package pl.szinton.gk;

import org.ejml.simple.SimpleMatrix;

public class Camera3D {

    private final static float MOVE_UNIT = 1f;
    private final static float ROTATE_UNIT = (float) (Math.PI * 0.1f);
    private final static float ZOOM_UNIT = 0.5f;

    private Vector2i frameSize;
    private Vector3f position;
    private Vector3f rotation;
    private float zoom;
    private SimpleMatrix transformationMatrix;

    public Camera3D(Vector2i frameSize, Vector3f position) {
        this.frameSize = frameSize;
        this.position = position;
        this.rotation = new Vector3f();
        this.zoom = 2f;
        updateTransformationMatrix();
    }

    public Vector2i projectPoint(Vector3f point) {
        SimpleMatrix transformedVectorMatrix = Utils.multiplyExtendedVectorByMatrix(point, transformationMatrix);
        Vector3f transformedVector = Utils.normalizeVectorFromMatrix(transformedVectorMatrix);
        int x = (int) (transformedVector.getX() * frameSize.getX() / transformedVector.getZ() + frameSize.getX() / 2);
        int y = (int) (-transformedVector.getY() * frameSize.getY() / transformedVector.getZ() + frameSize.getY() / 2);
        return new Vector2i(x, y);
    }

    public void setFrameSize(Vector2i frameSize) {
        this.frameSize = frameSize;
    }

    public void move(Direction direction) {
        Vector3f directionVector = getDirectionVector(direction);
        SimpleMatrix rotationMatrix = Matrix.rotationZ(-rotation.getZ()).mult(Matrix.rotationY(-rotation.getY()).mult(Matrix.rotationX(-rotation.getX())));
        Vector3f transformedVector = Utils.getVectorFromMatrix(
                Utils.multiplyExtendedVectorByMatrix(directionVector, rotationMatrix));
        move(transformedVector);
    }

    public void rotate(RotationAxis rotationAxis) {
        Vector3f rotationVector = getRotationVector(rotationAxis);
        rotate(rotationVector);
    }

    public void zoom(Zoom zoom) {
        float zoomValue = (zoom == Zoom.IN) ? ZOOM_UNIT : -ZOOM_UNIT;
        zoom(zoomValue);
    }

    public void move(Vector3f vector) {
        position = position.add(vector);
        updateTransformationMatrix();
    }

    public void rotate(Vector3f rotationVector) {
        rotation = rotation.add(rotationVector);
        updateTransformationMatrix();
    }

    public void zoom(float zoomChange) {
        zoom += zoomChange;
        updateTransformationMatrix();
    }

    private void updateTransformationMatrix() {
        transformationMatrix = Matrix.projection(zoom).mult(
                Matrix.rotationZ(rotation.getZ()).mult(
                        Matrix.rotationY(rotation.getY()).mult(
                                Matrix.rotationX(rotation.getX()).mult(
                                        Matrix.translation(position.negative())
                                )
                        )
                )
        );
    }

    private Vector3f getDirectionVector(Direction direction) {
        return switch (direction) {
            case LEFT -> new Vector3f(-MOVE_UNIT, 0f, 0f);
            case RIGHT -> new Vector3f(MOVE_UNIT, 0f, 0f);
            case FORWARD -> new Vector3f(0f, 0f, -MOVE_UNIT);
            case BACKWARD -> new Vector3f(0f, 0f, MOVE_UNIT);
            case UP -> new Vector3f(0f, MOVE_UNIT, 0f);
            case DOWN -> new Vector3f(0f, -MOVE_UNIT, 0f);
        };
    }

    private Vector3f getRotationVector(RotationAxis rotationAxis) {
        return switch (rotationAxis) {
            case POSITIVE_X -> new Vector3f(ROTATE_UNIT, 0f, 0f);
            case POSITIVE_Y -> new Vector3f(0f, ROTATE_UNIT, 0f);
            case POSITIVE_Z -> new Vector3f(0f, 0f, ROTATE_UNIT);
            case NEGATIVE_X -> new Vector3f(-ROTATE_UNIT, 0f, 0f);
            case NEGATIVE_Y -> new Vector3f(0f, -ROTATE_UNIT, 0f);
            case NEGATIVE_Z -> new Vector3f(0f, 0f, -ROTATE_UNIT);
        };
    }

    @Override
    public String toString() {
        return String.format("Camera: [pos: [%.2f, %.2f, %.2f], rot: [%.2f, %.2f, %.2f], zoom: %.1f]",
                position.getX(), position.getY(), position.getZ(),
                rotation.getX(), rotation.getY(), rotation.getZ(), zoom);
    }
}
