/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model;

import java.util.ArrayList;
import java.util.List;
import l2.commons.geometry.Point3D;
import l2.commons.geometry.Shape;
import l2.commons.util.Rnd;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.World;
import l2.gameserver.templates.spawn.SpawnRange;
import l2.gameserver.utils.Location;

public class Territory
implements Shape,
SpawnRange {
    protected final Point3D max = new Point3D();
    protected final Point3D min = new Point3D();
    private final List<Shape> LO = new ArrayList<Shape>(1);
    private final List<Shape> LP = new ArrayList<Shape>(1);

    public Territory add(Shape shape) {
        if (this.LO.isEmpty()) {
            this.max.x = shape.getXmax();
            this.max.y = shape.getYmax();
            this.max.z = shape.getZmax();
            this.min.x = shape.getXmin();
            this.min.y = shape.getYmin();
            this.min.z = shape.getZmin();
        } else {
            this.max.x = Math.max(this.max.x, shape.getXmax());
            this.max.y = Math.max(this.max.y, shape.getYmax());
            this.max.z = Math.max(this.max.z, shape.getZmax());
            this.min.x = Math.min(this.min.x, shape.getXmin());
            this.min.y = Math.min(this.min.y, shape.getYmin());
            this.min.z = Math.min(this.min.z, shape.getZmin());
        }
        this.LO.add(shape);
        return this;
    }

    public Territory addBanned(Shape shape) {
        this.LP.add(shape);
        return this;
    }

    public List<Shape> getTerritories() {
        return this.LO;
    }

    public List<Shape> getBannedTerritories() {
        return this.LP;
    }

    @Override
    public boolean isInside(int n, int n2) {
        for (int i = 0; i < this.LO.size(); ++i) {
            Shape shape = this.LO.get(i);
            if (!shape.isInside(n, n2)) continue;
            return !this.isExcluded(n, n2);
        }
        return false;
    }

    @Override
    public boolean isInside(int n, int n2, int n3) {
        if (n < this.min.x || n > this.max.x || n2 < this.min.y || n2 > this.max.y || n3 < this.min.z || n3 > this.max.z) {
            return false;
        }
        for (int i = 0; i < this.LO.size(); ++i) {
            Shape shape = this.LO.get(i);
            if (!shape.isInside(n, n2, n3)) continue;
            return !this.isExcluded(n, n2, n3);
        }
        return false;
    }

    public boolean isExcluded(int n, int n2) {
        for (int i = 0; i < this.LP.size(); ++i) {
            Shape shape = this.LP.get(i);
            if (!shape.isInside(n, n2)) continue;
            return true;
        }
        return false;
    }

    public boolean isExcluded(int n, int n2, int n3) {
        for (int i = 0; i < this.LP.size(); ++i) {
            Shape shape = this.LP.get(i);
            if (!shape.isInside(n, n2, n3)) continue;
            return true;
        }
        return false;
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

    @Override
    public int getZmax() {
        return this.max.z;
    }

    @Override
    public int getZmin() {
        return this.min.z;
    }

    public static Location getRandomLoc(Territory territory) {
        return Territory.getRandomLoc(territory, 0);
    }

    public static Location getRandomLoc(Territory territory, int n) {
        Location location = new Location();
        List<Shape> list = territory.getTerritories();
        block0: for (int i = 0; i < 100; ++i) {
            Shape shape = list.get(Rnd.get(list.size()));
            location.x = Rnd.get(shape.getXmin(), shape.getXmax());
            location.y = Rnd.get(shape.getYmin(), shape.getYmax());
            location.z = shape.getZmin() + (shape.getZmax() - shape.getZmin()) / 2;
            if (!territory.isInside(location.x, location.y)) continue;
            int n2 = GeoEngine.getHeight(location, n);
            if (shape.getZmin() != shape.getZmax() ? n2 < shape.getZmin() || n2 > shape.getZmax() : n2 < shape.getZmin() - 200 || n2 > shape.getZmin() + 200) continue;
            location.z = n2;
            int n3 = location.x - World.MAP_MIN_X >> 4;
            int n4 = location.y - World.MAP_MIN_Y >> 4;
            for (int j = n3 - 1; j <= n3 + 1; ++j) {
                for (int k = n4 - 1; k <= n4 + 1; ++k) {
                    if (GeoEngine.NgetNSWE(j, k, n2, n) != 15) continue block0;
                }
            }
            return location;
        }
        return location;
    }

    @Override
    public Location getRandomLoc(int n) {
        return Territory.getRandomLoc(this, n);
    }
}

