package pl.szinton.gk.math;

import java.util.Objects;

public class Vector3f {

    private final float x;
    private final float y;
    private final float z;
    private final int hashCode;

    public Vector3f() {
        this.x = 0f;
        this.y = 0f;
        this.z = 0f;
        this.hashCode = Objects.hash(x, y, z);
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.hashCode = Objects.hash(x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Vector3f other)) {
            return false;
        }
        return x == other.x &&
                y == other.y &&
                z == other.z;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    public Vector3f negative() {
        return new Vector3f(-x, -y, -z);
    }

    public Vector3f unitVector() {
        float m = magnitude();
        return new Vector3f(x / m, y / m, z / m);
    }

    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3f add(Vector3f other) {
        return new Vector3f(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    public static Vector3f crossProduct(Vector3f u, Vector3f v) {
        float x = u.getY() * v.getZ() - u.getZ() * v.getY();
        float y = u.getZ() * v.getX() - u.getX() * v.getZ();
        float z = u.getX() * v.getY() - u.getY() * v.getX();
        return new Vector3f(x, y, z);
    }

    public static float dotProduct(Vector3f u, Vector3f v) {
        return u.getX() * v.getX() + u.getY() * v.getY() + u.getZ() + v.getZ();
    }
}
