package pl.szinton.gk.utils;

import org.ejml.simple.SimpleMatrix;
import pl.szinton.gk.math.Vector2i;
import pl.szinton.gk.math.Vector3f;

public class MatrixUtils {

    public static SimpleMatrix extendedVector(Vector3f vector) {
        return new SimpleMatrix(4, 1, true, new float[]{
                vector.getX(), vector.getY(), vector.getZ(), 1f
        });
    }

    public static SimpleMatrix multiplyExtendedVectorByMatrix(Vector3f vector, SimpleMatrix matrix) {
        return matrix.mult(extendedVector(vector));
    }

    public static Vector3f getVectorFromMatrix(SimpleMatrix vectorMatrix) {
        float x = (float) vectorMatrix.get(0, 0);
        float y = (float) vectorMatrix.get(1, 0);
        float z = (float) vectorMatrix.get(2, 0);
        return new Vector3f(x, y, z);
    }

    public static Vector3f normalizeVectorFromMatrix(SimpleMatrix vectorMatrix) {
        float divider = (float) vectorMatrix.get(3, 0);
        float x = (float) (vectorMatrix.get(0, 0) / divider);
        float y = (float) (vectorMatrix.get(1, 0) / divider);
        float z = (float) (vectorMatrix.get(2, 0) / divider);
        return new Vector3f(x, y, z);
    }

    public static Vector2i convertVector3fToVector2i(Vector3f vec) {
        return new Vector2i((int) vec.getX(), (int) vec.getY());
    }
}
