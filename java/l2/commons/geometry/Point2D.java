/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.geometry;

import java.io.Serializable;

public class Point2D
implements Serializable,
Cloneable {
    public static final transient Point2D[] EMPTY_ARRAY = new Point2D[0];
    public int x;
    public int y;

    public Point2D() {
    }

    public Point2D(int n, int n2) {
        this.x = n;
        this.y = n2;
    }

    public Point2D clone() {
        return new Point2D(this.x, this.y);
    }

    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (object.getClass() != this.getClass()) {
            return false;
        }
        return this.equals((Point2D)object);
    }

    public boolean equals(Point2D point2D) {
        return this.equals(point2D.x, point2D.y);
    }

    public boolean equals(int n, int n2) {
        return this.x == n && this.y == n2;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public String toString() {
        return "[x: " + this.x + " y: " + this.y + "]";
    }
}

