package pl.szinton.gk;

import org.ejml.simple.SimpleMatrix;

public class Matrix {

    public static SimpleMatrix translation(Vector3f vector) {
        return new SimpleMatrix(4, 4, true, new float[] {
                1f, 0f, 0f, vector.getX(),
                0f, 1f, 0f, vector.getY(),
                0f, 0f, 1f, vector.getZ(),
                0f, 0f, 0f, 1f
        });
    }

    public static SimpleMatrix rotationX(float rotation) {
        float sin = (float) Math.sin(rotation);
        float cos = (float) Math.cos(rotation);
        return new SimpleMatrix(4, 4, true, new float[] {
                1f, 0f, 0f, 0f,
                0f, cos, -sin, 0f,
                0f, sin, cos, 0f,
                0f, 0f, 0f, 1f
        });
    }

    public static SimpleMatrix rotationY(float rotation) {
        float sin = (float) Math.sin(rotation);
        float cos = (float) Math.cos(rotation);
        return new SimpleMatrix(4, 4, true, new float[] {
                cos, 0f, sin, 0f,
                0f, 1f, 0f, 0f,
                -sin, 0f, cos, 0f,
                0f, 0f, 0f, 1f
        });
    }

    public static SimpleMatrix rotationZ(float rotation) {
        float sin = (float) Math.sin(rotation);
        float cos = (float) Math.cos(rotation);
        return new SimpleMatrix(4, 4, true, new float[] {
                cos, -sin, 0f, 0f,
                sin, cos, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f
        });
    }

    public static SimpleMatrix projection(float zoom) {
        return new SimpleMatrix(4, 4, true, new float[] {
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f/(1f-zoom), (-zoom)/(1f-zoom),
                0f, 0f, 1f, 0f
        });
    }
}
