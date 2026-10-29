/*
 * Decompiled with CFR 0.152.
 */
package l2.commons.geometry;

import l2.commons.geometry.Point2D;

public class GeometryUtils {
    private GeometryUtils() {
    }

    public static boolean checkIfLinesIntersects(Point2D point2D, Point2D point2D2, Point2D point2D3, Point2D point2D4) {
        return GeometryUtils.checkIfLinesIntersects(point2D, point2D2, point2D3, point2D4, null);
    }

    public static boolean checkIfLinesIntersects(Point2D point2D, Point2D point2D2, Point2D point2D3, Point2D point2D4, Point2D point2D5) {
        if (point2D.x == point2D2.x && point2D.y == point2D2.y || point2D3.x == point2D4.x && point2D3.y == point2D4.y) {
            return false;
        }
        double d = point2D2.x - point2D.x;
        double d2 = point2D2.y - point2D.y;
        double d3 = point2D3.x - point2D.x;
        double d4 = point2D3.y - point2D.y;
        double d5 = point2D4.x - point2D.x;
        double d6 = point2D4.y - point2D.y;
        double d7 = Math.sqrt(d * d + d2 * d2);
        double d8 = d / d7;
        double d9 = d2 / d7;
        double d10 = d3 * d8 + d4 * d9;
        d4 = (int)(d4 * d8 - d3 * d9);
        d3 = d10;
        d10 = d5 * d8 + d6 * d9;
        d6 = (int)(d6 * d8 - d5 * d9);
        d5 = d10;
        if (d4 == d6) {
            return false;
        }
        double d11 = d5 + (d3 - d5) * d6 / (d6 - d4);
        if (point2D5 != null) {
            point2D5.x = (int)((double)point2D.x + d11 * d8);
            point2D5.y = (int)((double)point2D.y + d11 * d9);
        }
        return true;
    }

    public static boolean checkIfLineSegementsIntersects(Point2D point2D, Point2D point2D2, Point2D point2D3, Point2D point2D4) {
        return GeometryUtils.checkIfLineSegementsIntersects(point2D, point2D2, point2D3, point2D4, null);
    }

    public static boolean checkIfLineSegementsIntersects(Point2D point2D, Point2D point2D2, Point2D point2D3, Point2D point2D4, Point2D point2D5) {
        if (point2D.x == point2D2.x && point2D.y == point2D2.y || point2D3.x == point2D4.x && point2D3.y == point2D4.y) {
            return false;
        }
        if (point2D.x == point2D3.x && point2D.y == point2D3.y || point2D2.x == point2D3.x && point2D2.y == point2D3.y || point2D.x == point2D4.x && point2D.y == point2D4.y || point2D2.x == point2D4.x && point2D2.y == point2D4.y) {
            return false;
        }
        double d = point2D2.x - point2D.x;
        double d2 = point2D2.y - point2D.y;
        double d3 = point2D3.x - point2D.x;
        double d4 = point2D3.y - point2D.y;
        double d5 = point2D4.x - point2D.x;
        double d6 = point2D4.y - point2D.y;
        double d7 = Math.sqrt(d * d + d2 * d2);
        double d8 = d / d7;
        double d9 = d2 / d7;
        double d10 = d3 * d8 + d4 * d9;
        d4 = (int)(d4 * d8 - d3 * d9);
        d3 = d10;
        d10 = d5 * d8 + d6 * d9;
        d6 = (int)(d6 * d8 - d5 * d9);
        d5 = d10;
        if (d4 < 0.0 && d6 < 0.0 || d4 >= 0.0 && d6 >= 0.0) {
            return false;
        }
        double d11 = d5 + (d3 - d5) * d6 / (d6 - d4);
        if (d11 < 0.0 || d11 > d7) {
            return false;
        }
        if (point2D5 != null) {
            point2D5.x = (int)((double)point2D.x + d11 * d8);
            point2D5.y = (int)((double)point2D.y + d11 * d9);
        }
        return true;
    }
}

