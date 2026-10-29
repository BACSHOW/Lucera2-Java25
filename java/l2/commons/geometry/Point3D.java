/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.geometry;

import l2.commons.geometry.Point2D;

public class Point3D
extends Point2D {
    public static final transient Point3D[] EMPTY_ARRAY = new Point3D[0];
    public int z;

    public Point3D() {
    }

    public Point3D(int n, int n2, int n3) {
        super(n, n2);
        this.z = n3;
    }

    public int getZ() {
        return this.z;
    }

    @Override
    public Point3D clone() {
        return new Point3D(this.x, this.y, this.z);
    }

    @Override
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
        return this.equals((Point3D)object);
    }

    public boolean equals(Point3D point3D) {
        return this.equals(point3D.x, point3D.y, point3D.z);
    }

    public boolean equals(int n, int n2, int n3) {
        return this.x == n && this.y == n2 && this.z == n3;
    }

    @Override
    public String toString() {
        return "[x: " + this.x + " y: " + this.y + " z: " + this.z + "]";
    }
}

