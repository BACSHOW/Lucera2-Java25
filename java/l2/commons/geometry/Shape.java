/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.geometry;

public interface Shape {
    public boolean isInside(int var1, int var2);

    public boolean isInside(int var1, int var2, int var3);

    public int getXmax();

    public int getXmin();

    public int getYmax();

    public int getYmin();

    public int getZmax();

    public int getZmin();
}

