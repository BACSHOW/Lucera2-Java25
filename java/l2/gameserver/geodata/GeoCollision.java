/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.geodata;

import l2.commons.geometry.Shape;

public interface GeoCollision {
    public Shape getShape();

    public byte[][] getGeoAround();

    public void setGeoAround(byte[][] var1);

    public boolean isConcrete();
}

