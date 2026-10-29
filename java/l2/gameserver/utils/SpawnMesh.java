/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.utils;

import l2.commons.geometry.Polygon;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.World;
import l2.gameserver.templates.spawn.SpawnRange;
import l2.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpawnMesh
extends Polygon
implements SpawnRange {
    private static final Logger LOG = LoggerFactory.getLogger(SpawnMesh.class);

    @Override
    public Location getRandomLoc(int n) {
        Location location = new Location(0, 0, 0);
        int n2 = this.getXmax() - this.getXmin();
        int n3 = this.getYmax() - this.getYmin();
        int n4 = this.getZmin() + (this.getZmax() - this.getZmin()) / 2;
        int n5 = Math.max(2048, (n3 >> 4) * (n2 >> 4));
        int n6 = n4;
        int n7 = 0;
        block0: do {
            location.setX(Rnd.get(this.getXmin(), this.getXmax()));
            location.setY(Rnd.get(this.getYmin(), this.getYmax()));
            location.setZ(n4);
            if (!this.isInside(location.getX(), location.getY())) continue;
            n6 = GeoEngine.getHeight(location, n);
            if (this.getZmin() != this.getZmax() ? n6 < this.getZmin() || n6 > this.getZmax() : n6 < this.getZmin() - Config.MAX_Z_DIFF || n6 > this.getZmin() + Config.MAX_Z_DIFF) continue;
            location.setZ(n6);
            int n8 = location.getX() - World.MAP_MIN_X >> 4;
            int n9 = location.getY() - World.MAP_MIN_Y >> 4;
            for (int i = n8 - 1; i <= n8 + 1; ++i) {
                for (int j = n9 - 1; j <= n9 + 1; ++j) {
                    if (GeoEngine.NgetNSWE(i, j, n6, n) != 15) continue block0;
                }
            }
            return location;
        } while (n7++ < n5);
        if (Config.ALT_DEBUG_ENABLED) {
            LOG.warn("Cant find suitable point in " + this.toString() + " z[" + this.getZmin() + " " + this.getZmax() + "] last: " + location);
        }
        return location;
    }
}

