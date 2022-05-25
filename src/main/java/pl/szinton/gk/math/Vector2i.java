package pl.szinton.gk.math;

import java.util.Objects;

public class Vector2i {

    private final int x;
    private final int y;
    private final int hashCode;

    public Vector2i(int x, int y) {
        this.x = x;
        this.y = y;
        this.hashCode = Objects.hash(x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Vector2i other)) {
            return false;
        }
        return x == other.x &&
                y == other.y;
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
