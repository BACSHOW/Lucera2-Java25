/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.utils;

import java.io.Serializable;
import l2.commons.geometry.Point2D;
import l2.commons.geometry.Point3D;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.World;
import l2.gameserver.templates.spawn.SpawnRange;
import l2.gameserver.utils.PositionUtils;
import org.dom4j.Element;

public class Location
extends Point3D
implements Serializable,
SpawnRange {
    public int h;

    public Location() {
    }

    public Location(int n, int n2, int n3, int n4) {
        super(n, n2, n3);
        this.h = n4;
    }

    public Location(int n, int n2, int n3) {
        this(n, n2, n3, 0);
    }

    public Location(GameObject gameObject) {
        this(gameObject.getX(), gameObject.getY(), gameObject.getZ(), gameObject.getHeading());
    }

    public int getHeading() {
        return this.h;
    }

    public int getH() {
        return this.h;
    }

    public Location changeZ(int n) {
        this.z += n;
        return this;
    }

    public Location correctGeoZ() {
        this.z = GeoEngine.getHeight(this.x, this.y, this.z, 0);
        return this;
    }

    public Location correctGeoZ(int n) {
        this.z = GeoEngine.getHeight(this.x, this.y, this.z, n);
        return this;
    }

    public Location setX(int n) {
        this.x = n;
        return this;
    }

    public Location setY(int n) {
        this.y = n;
        return this;
    }

    public Location setZ(int n) {
        this.z = n;
        return this;
    }

    public Location setH(int n) {
        this.h = n;
        return this;
    }

    public Location set(int n, int n2, int n3) {
        this.x = n;
        this.y = n2;
        this.z = n3;
        return this;
    }

    public Location set(int n, int n2, int n3, int n4) {
        this.set(n, n2, n3);
        this.h = n4;
        return this;
    }

    public Location set(Location location) {
        this.x = location.x;
        this.y = location.y;
        this.z = location.z;
        this.h = location.h;
        return this;
    }

    public Location world2geo() {
        this.x = this.x - World.MAP_MIN_X >> 4;
        this.y = this.y - World.MAP_MIN_Y >> 4;
        return this;
    }

    public Location geo2world() {
        this.x = (this.x << 4) + World.MAP_MIN_X + 8;
        this.y = (this.y << 4) + World.MAP_MIN_Y + 8;
        return this;
    }

    public double distance(Location location) {
        return this.distance(location.x, location.y);
    }

    public double distance(int n, int n2) {
        long l = this.x - n;
        long l2 = this.y - n2;
        return Math.sqrt(l * l + l2 * l2);
    }

    public double distance3D(Location location) {
        return this.distance3D(location.x, location.y, location.z);
    }

    public double distance3D(int n, int n2, int n3) {
        long l = this.x - n;
        long l2 = this.y - n2;
        long l3 = this.z - n3;
        return Math.sqrt(l * l + l2 * l2 + l3 * l3);
    }

    @Override
    public Location clone() {
        return new Location(this.x, this.y, this.z, this.h);
    }

    @Override
    public final String toString() {
        return this.x + "," + this.y + "," + this.z + "," + this.h;
    }

    public boolean isNull() {
        return this.x == 0 || this.y == 0 || this.z == 0;
    }

    public final String toXYZString() {
        return this.x + " " + this.y + " " + this.z;
    }

    public final String toXYZHString() {
        return this.x + " " + this.y + " " + this.z + " " + this.h;
    }

    public static Location parseLoc(String string) throws IllegalArgumentException {
        String[] stringArray = string.split("[\\s,;]+");
        if (stringArray.length < 3) {
            throw new IllegalArgumentException("Can't parse location from string: " + string);
        }
        int n = Integer.parseInt(stringArray[0]);
        int n2 = Integer.parseInt(stringArray[1]);
        int n3 = Integer.parseInt(stringArray[2]);
        int n4 = stringArray.length < 4 ? 0 : Integer.parseInt(stringArray[3]);
        return new Location(n, n2, n3, n4);
    }

    public static Location parse(Element element) {
        int n = Integer.parseInt(element.attributeValue("x"));
        int n2 = Integer.parseInt(element.attributeValue("y"));
        int n3 = Integer.parseInt(element.attributeValue("z"));
        int n4 = element.attributeValue("h") == null ? 0 : Integer.parseInt(element.attributeValue("h"));
        return new Location(n, n2, n3, n4);
    }

    public Location indent(Location location, int n, boolean bl) {
        double d;
        if (n <= 0) {
            return this;
        }
        long l = this.getX() - location.getX();
        long l2 = this.getY() - location.getY();
        long l3 = this.getZ() - location.getZ();
        double d2 = d = bl ? Math.sqrt(l * l + l2 * l2 + l3 * l3) : Math.sqrt(l * l + l2 * l2);
        if (d <= (double)n) {
            this.set(location.getX(), location.getY(), location.getZ());
            return this;
        }
        if (d >= 1.0) {
            double d3 = (double)n / d;
            this.setX(this.getX() - (int)((double)l * d3 + 0.5));
            this.setY(this.getY() - (int)((double)l2 * d3 + 0.5));
            this.setZ(this.getZ() - (int)((double)l3 * d3 + 0.5));
        }
        return this;
    }

    public boolean equalsGeo(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (object instanceof Point2D) {
            Point2D point2D = (Point2D)object;
            if (point2D.x - World.MAP_MIN_X >> 4 != this.x - World.MAP_MIN_X >> 4) {
                return false;
            }
            if (point2D.y - World.MAP_MIN_Y >> 4 != this.y - World.MAP_MIN_Y >> 4) {
                return false;
            }
            if (object instanceof Point3D) {
                return Math.abs(((Point3D)object).z - this.z) < Config.MAX_Z_DIFF;
            }
            return true;
        }
        return false;
    }

    public Location indent(Location location, int n) {
        return this.indent(location, n, true);
    }

    public static Location findFrontPosition(GameObject gameObject, GameObject gameObject2, int n, int n2) {
        if (n2 == 0 || n2 < n) {
            return new Location(gameObject);
        }
        double d = gameObject.getColRadius() + gameObject2.getColRadius();
        int n3 = 0;
        int n4 = 360;
        if (!gameObject.equals(gameObject2)) {
            double d2 = PositionUtils.calculateAngleFrom(gameObject, gameObject2);
            n3 = (int)d2 - 45;
            n4 = (int)d2 + 45;
        }
        Location location = new Location();
        for (int i = 0; i < 100; ++i) {
            int n5 = Rnd.get(n, n2);
            int n6 = Rnd.get(n3, n4);
            location.x = gameObject.getX() + (int)((d + (double)n5) * Math.cos(Math.toRadians(n6)));
            location.y = gameObject.getY() + (int)((d + (double)n5) * Math.sin(Math.toRadians(n6)));
            location.z = gameObject.getZ();
            int n7 = GeoEngine.getHeight(location.x, location.y, location.z, gameObject.getGeoIndex());
            if (Math.abs(location.z - n7) >= 200 || GeoEngine.getNSWE(location.x, location.y, n7, gameObject.getGeoIndex()) != 15) continue;
            location.z = n7;
            location.h = !gameObject.equals(gameObject2) ? PositionUtils.getHeadingTo(location, gameObject2.getLoc()) : gameObject.getHeading();
            return location;
        }
        return new Location(gameObject);
    }

    public static Location findAroundPosition(int n, int n2, int n3, int n4, int n5, int n6) {
        for (int i = 0; i < 100; ++i) {
            Location location = Location.coordsRandomize(n, n2, n3, 0, n4, n5);
            int n7 = GeoEngine.getHeight(location.x, location.y, location.z, n6);
            if (!GeoEngine.canMoveToCoord(n, n2, n3, location.x, location.y, n7, n6) || !GeoEngine.canMoveToCoord(location.x, location.y, n7, n, n2, n3, n6)) continue;
            location.z = n7;
            return location;
        }
        return new Location(n, n2, n3);
    }

    public static Location findAroundPosition(Location location, int n, int n2) {
        return Location.findAroundPosition(location.x, location.y, location.z, 0, n, n2);
    }

    public static Location findAroundPosition(Location location, int n, int n2, int n3) {
        return Location.findAroundPosition(location.x, location.y, location.z, n, n2, n3);
    }

    public static Location findAroundPosition(GameObject gameObject, Location location, int n, int n2) {
        return Location.findAroundPosition(location.x, location.y, location.z, n, n2, gameObject.getGeoIndex());
    }

    public static Location findAroundPosition(GameObject gameObject, int n, int n2) {
        return Location.findAroundPosition(gameObject, gameObject.getLoc(), n, n2);
    }

    public static Location findAroundPosition(GameObject gameObject, int n) {
        return Location.findAroundPosition(gameObject, 0, n);
    }

    public static Location findPointToStay(int n, int n2, int n3, int n4, int n5, int n6) {
        for (int i = 0; i < 100; ++i) {
            Location location = Location.coordsRandomize(n, n2, n3, 0, n4, n5);
            int n7 = GeoEngine.getHeight(location.x, location.y, location.z, n6);
            if (Math.abs(location.z - n7) >= 200 || GeoEngine.getNSWE(location.x, location.y, n7, n6) != 15) continue;
            location.z = n7;
            return location;
        }
        return new Location(n, n2, n3);
    }

    public static Location findPointToStay(Location location, int n, int n2) {
        return Location.findPointToStay(location.x, location.y, location.z, 0, n, n2);
    }

    public static Location findPointToStay(Location location, int n, int n2, int n3) {
        return Location.findPointToStay(location.x, location.y, location.z, n, n2, n3);
    }

    public static Location findPointToStay(GameObject gameObject, Location location, int n, int n2) {
        return Location.findPointToStay(location.x, location.y, location.z, n, n2, gameObject.getGeoIndex());
    }

    public static Location findPointToStay(GameObject gameObject, int n, int n2) {
        return Location.findPointToStay(gameObject, gameObject.getLoc(), n, n2);
    }

    public static Location findPointToStay(GameObject gameObject, int n) {
        return Location.findPointToStay(gameObject, 0, n);
    }

    public static Location coordsRandomize(Location location, int n, int n2) {
        return Location.coordsRandomize(location.x, location.y, location.z, location.h, n, n2);
    }

    public static Location coordsRandomize(int n, int n2, int n3, int n4, int n5, int n6) {
        if (n6 == 0 || n6 < n5) {
            return new Location(n, n2, n3, n4);
        }
        int n7 = Rnd.get(n5, n6);
        double d = Rnd.nextDouble() * 2.0 * Math.PI;
        return new Location((int)((double)n + (double)n7 * Math.cos(d)), (int)((double)n2 + (double)n7 * Math.sin(d)), n3, n4);
    }

    public static Location findNearest(Creature creature, Location[] locationArray) {
        Location location = null;
        for (Location location2 : locationArray) {
            if (location == null) {
                location = location2;
                continue;
            }
            if (!(creature.getDistance(location2) < creature.getDistance(location))) continue;
            location = location2;
        }
        return location;
    }

    public static int getRandomHeading() {
        return Rnd.get(65535);
    }

    @Override
    public Location getRandomLoc(int n) {
        return this;
    }
}

