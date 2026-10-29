/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.geometry;

import l2.commons.geometry.Point3D;
import l2.commons.geometry.Shape;

public abstract class AbstractShape
implements Shape {
    protected final Point3D max = new Point3D();
    protected final Point3D min = new Point3D();

    @Override
    public boolean isInside(int n, int n2, int n3) {
        return this.min.z <= n3 && this.max.z >= n3 && this.isInside(n, n2);
    }

    @Override
    public int getXmax() {
        return this.max.x;
    }

    @Override
    public int getXmin() {
        return this.min.x;
    }

    @Override
    public int getYmax() {
        return this.max.y;
    }

    @Override
    public int getYmin() {
        return this.min.y;
    }

    public AbstractShape setZmax(int n) {
        this.max.z = n;
        return this;
    }

    public AbstractShape setZmin(int n) {
        this.min.z = n;
        return this;
    }

    @Override
    public int getZmax() {
        return this.max.z;
    }

    @Override
    public int getZmin() {
        return this.min.z;
    }
}

