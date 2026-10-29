/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.geometry;

import l2.commons.geometry.AbstractShape;

public class Rectangle
extends AbstractShape {
    public Rectangle(int n, int n2, int n3, int n4) {
        this.min.x = Math.min(n, n3);
        this.min.y = Math.min(n2, n4);
        this.max.x = Math.max(n, n3);
        this.max.y = Math.max(n2, n4);
    }

    @Override
    public Rectangle setZmax(int n) {
        this.max.z = n;
        return this;
    }

    @Override
    public Rectangle setZmin(int n) {
        this.min.z = n;
        return this;
    }

    @Override
    public boolean isInside(int n, int n2) {
        return n >= this.min.x && n <= this.max.x && n2 >= this.min.y && n2 <= this.max.y;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        stringBuilder.append(this.min).append(", ").append(this.max);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}

