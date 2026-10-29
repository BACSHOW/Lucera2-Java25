/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.geometry;

import l2.commons.geometry.AbstractShape;
import l2.commons.geometry.Point2D;

public class Circle
extends AbstractShape {
    protected final Point2D c;
    protected final int r;

    public Circle(Point2D point2D, int n) {
        this.c = point2D;
        this.r = n;
        this.min.x = this.c.x - this.r;
        this.max.x = this.c.x + this.r;
        this.min.y = this.c.y - this.r;
        this.max.y = this.c.y + this.r;
    }

    public Circle(int n, int n2, int n3) {
        this(new Point2D(n, n2), n3);
    }

    @Override
    public Circle setZmax(int n) {
        this.max.z = n;
        return this;
    }

    @Override
    public Circle setZmin(int n) {
        this.min.z = n;
        return this;
    }

    @Override
    public boolean isInside(int n, int n2) {
        return (n - this.c.x) * (this.c.x - n) + (n2 - this.c.y) * (this.c.y - n2) <= this.r * this.r;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        stringBuilder.append(this.c).append("{ radius: ").append(this.r).append("}");
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}

