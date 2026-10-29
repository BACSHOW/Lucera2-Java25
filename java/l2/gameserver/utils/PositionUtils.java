/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.utils;

import l2.gameserver.model.Creature;
import l2.gameserver.model.GameObject;
import l2.gameserver.utils.Location;

public class PositionUtils {
    private static final int awV = 360;
    private static final double awW = 100.0;
    private static final double awX = 40.0;

    public static TargetDirection getDirectionTo(Creature creature, Creature creature2) {
        if (creature == null || creature2 == null) {
            return TargetDirection.NONE;
        }
        if (PositionUtils.isBehind(creature, creature2)) {
            return TargetDirection.BEHIND;
        }
        if (PositionUtils.isInFrontOf(creature, creature2)) {
            return TargetDirection.FRONT;
        }
        return TargetDirection.SIDE;
    }

    public static boolean isInFrontOf(Creature creature, Creature creature2) {
        if (creature == null) {
            return false;
        }
        double d = PositionUtils.calculateAngleFrom(creature, creature2);
        double d2 = PositionUtils.convertHeadingToDegree(creature.getHeading());
        double d3 = d2 - d;
        if (d3 <= -260.0) {
            d3 += 360.0;
        }
        if (d3 >= 260.0) {
            d3 -= 360.0;
        }
        return Math.abs(d3) <= 100.0;
    }

    public static boolean isBehind(Creature creature, Creature creature2) {
        double d;
        if (creature == null) {
            return false;
        }
        double d2 = PositionUtils.calculateAngleFrom(creature2, creature);
        double d3 = d2 - (d = PositionUtils.convertHeadingToDegree(creature.getHeading()));
        if (d3 <= -320.0) {
            d3 += 360.0;
        }
        if (d3 >= 320.0) {
            d3 -= 360.0;
        }
        return Math.abs(d3) <= 40.0;
    }

    public static boolean isFacing(Creature creature, GameObject gameObject, int n) {
        if (gameObject == null) {
            return false;
        }
        double d = n / 2;
        double d2 = PositionUtils.calculateAngleFrom(creature, gameObject);
        double d3 = PositionUtils.convertHeadingToDegree(creature.getHeading());
        double d4 = d3 - d2;
        if (d4 <= -360.0 + d) {
            d4 += 360.0;
        }
        if (d4 >= 360.0 - d) {
            d4 -= 360.0;
        }
        return Math.abs(d4) <= d;
    }

    public static boolean isFacing(Location location, Location location2, int n) {
        if (location == null) {
            return false;
        }
        double d = n / 2;
        double d2 = PositionUtils.calculateAngleFrom(location.getX(), location.getY(), location2.getX(), location2.getY());
        double d3 = PositionUtils.convertHeadingToDegree(location2.getH());
        double d4 = d3 - d2;
        if (d4 <= -360.0 + d) {
            d4 += 360.0;
        }
        if (d4 >= 360.0 - d) {
            d4 -= 360.0;
        }
        return Math.abs(d4) <= d;
    }

    public static int calculateHeadingFrom(GameObject gameObject, GameObject gameObject2) {
        return PositionUtils.calculateHeadingFrom(gameObject.getX(), gameObject.getY(), gameObject2.getX(), gameObject2.getY());
    }

    public static int calculateHeadingFrom(int n, int n2, int n3, int n4) {
        double d = Math.toDegrees(Math.atan2(n4 - n2, n3 - n));
        if (d < 0.0) {
            d = 360.0 + d;
        }
        return (int)(d * 182.044444444);
    }

    public static double calculateAngleFrom(GameObject gameObject, GameObject gameObject2) {
        return PositionUtils.calculateAngleFrom(gameObject.getX(), gameObject.getY(), gameObject2.getX(), gameObject2.getY());
    }

    public static double calculateAngleFrom(int n, int n2, int n3, int n4) {
        double d = Math.toDegrees(Math.atan2(n4 - n2, n3 - n));
        if (d < 0.0) {
            d = 360.0 + d;
        }
        return d;
    }

    public static boolean checkIfInRange(int n, int n2, int n3, int n4, int n5) {
        return PositionUtils.checkIfInRange(n, n2, n3, 0, n4, n5, 0, false);
    }

    public static boolean checkIfInRange(int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean bl) {
        long l = n2 - n5;
        long l2 = n3 - n6;
        if (bl) {
            long l3 = n4 - n7;
            return l * l + l2 * l2 + l3 * l3 <= (long)(n * n);
        }
        return l * l + l2 * l2 <= (long)(n * n);
    }

    public static boolean checkIfInRange(int n, GameObject gameObject, GameObject gameObject2, boolean bl) {
        if (gameObject == null || gameObject2 == null) {
            return false;
        }
        return PositionUtils.checkIfInRange(n, gameObject.getX(), gameObject.getY(), gameObject.getZ(), gameObject2.getX(), gameObject2.getY(), gameObject2.getZ(), bl);
    }

    public static double convertHeadingToDegree(int n) {
        return (double)n / 182.044444444;
    }

    public static double convertHeadingToRadian(int n) {
        return Math.toRadians(PositionUtils.convertHeadingToDegree(n) - 90.0);
    }

    public static int convertDegreeToClientHeading(double d) {
        if (d < 0.0) {
            d = 360.0 + d;
        }
        return (int)(d * 182.044444444);
    }

    public static double calculateDistance(int n, int n2, int n3, int n4, int n5) {
        return PositionUtils.calculateDistance(n, n2, 0, n4, n5, 0, false);
    }

    public static double calculateDistance(Location location, Location location2, boolean bl) {
        return PositionUtils.calculateDistance(location.getX(), location.getY(), location.getZ(), location2.getX(), location2.getY(), location2.getZ(), bl);
    }

    public static double calculateDistance(int n, int n2, int n3, int n4, int n5, int n6, boolean bl) {
        long l = n - n4;
        long l2 = n2 - n5;
        if (bl) {
            long l3 = n3 - n6;
            return Math.sqrt(l * l + l2 * l2 + l3 * l3);
        }
        return Math.sqrt(l * l + l2 * l2);
    }

    public static double calculateDistance(GameObject gameObject, GameObject gameObject2, boolean bl) {
        if (gameObject == null || gameObject2 == null) {
            return 2.147483647E9;
        }
        return PositionUtils.calculateDistance(gameObject.getX(), gameObject.getY(), gameObject.getZ(), gameObject2.getX(), gameObject2.getY(), gameObject2.getZ(), bl);
    }

    public static double getDistance(GameObject gameObject, GameObject gameObject2) {
        return PositionUtils.getDistance(gameObject.getX(), gameObject2.getY(), gameObject2.getX(), gameObject2.getY());
    }

    public static double getDistance(Location location, Location location2) {
        return PositionUtils.getDistance(location.getX(), location.getY(), location2.getX(), location2.getY());
    }

    public static double getDistance(int n, int n2, int n3, int n4) {
        return Math.hypot(n - n3, n2 - n4);
    }

    public static int getHeadingTo(GameObject gameObject, GameObject gameObject2) {
        if (gameObject == null || gameObject2 == null || gameObject2 == gameObject) {
            return -1;
        }
        return PositionUtils.getHeadingTo(gameObject.getLoc(), gameObject2.getLoc());
    }

    public static int getHeadingTo(Location location, Location location2) {
        if (location == null || location2 == null || location2.equals(location)) {
            return -1;
        }
        int n = location2.y - location.y;
        int n2 = location2.x - location.x;
        int n3 = location2.h - (int)(Math.atan2(-n, -n2) * 10430.378350470453 + 32768.0);
        if (n3 < 0) {
            n3 = n3 + 1 + Integer.MAX_VALUE & 0xFFFF;
        } else if (n3 > 65535) {
            n3 &= 0xFFFF;
        }
        return n3;
    }

    public static enum TargetDirection {
        NONE,
        FRONT,
        SIDE,
        BEHIND;

    }
}

