/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.utils.Location;

public class ExServerPrimitive
extends L2GameServerPacket {
    private final String _name;
    private final int _x;
    private final int _y;
    private final int Di;
    private final List<Point> ahS = new ArrayList<Point>();
    private final List<Line> ahT = new ArrayList<Line>();

    public ExServerPrimitive(String string, int n, int n2, int n3) {
        this._name = string;
        this._x = n;
        this._y = n2;
        this.Di = n3;
    }

    public ExServerPrimitive(String string, Location location) {
        this(string, location.getX(), location.getY(), location.getZ());
    }

    public void addPoint(String string, int n, boolean bl, int n2, int n3, int n4) {
        this.ahS.add(new Point(string, n, bl, n2, n3, n4));
    }

    public void addGeoPoint(String string, int n, boolean bl, int n2, int n3, int n4) {
        this.addPoint(string, n, bl, new Location(n2, n3, (short)((short)(n4 & 0xFFF0) >> 1)).geo2world());
    }

    public void addPoint(String string, int n, boolean bl, Location location) {
        this.addPoint(string, n, bl, location.getX(), location.getY(), location.getZ());
    }

    public void addPoint(int n, int n2, int n3, int n4) {
        this.addPoint("", n, false, n2, n3, n4);
    }

    public void addPoint(int n, Location location) {
        this.addPoint("", n, false, location);
    }

    public void addPoint(String string, Color color, boolean bl, int n, int n2, int n3) {
        this.addPoint(string, color.getRGB(), bl, n, n2, n3);
    }

    public void addPoint(String string, Color color, boolean bl, Location location) {
        this.addPoint(string, color.getRGB(), bl, location);
    }

    public void addPoint(Color color, int n, int n2, int n3) {
        this.addPoint("", color, false, n, n2, n3);
    }

    public void addPoint(Color color, Location location) {
        this.addPoint("", color, false, location);
    }

    public void addLine(String string, int n, boolean bl, int n2, int n3, int n4, int n5, int n6, int n7) {
        this.ahT.add(new Line(string, n, bl, n2, n3, n4, n5, n6, n7));
    }

    public void addLine(String string, int n, boolean bl, Location location, int n2, int n3, int n4) {
        this.addLine(string, n, bl, location.getX(), location.getY(), location.getZ(), n2, n3, n4);
    }

    public void addLine(String string, int n, boolean bl, int n2, int n3, int n4, Location location) {
        this.addLine(string, n, bl, n2, n3, n4, location.getX(), location.getY(), location.getZ());
    }

    public void addLine(String string, int n, boolean bl, Location location, Location location2) {
        this.addLine(string, n, bl, location, location2.getX(), location2.getY(), location2.getZ());
    }

    public void addLine(int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        this.addLine("", n, false, n2, n3, n4, n5, n6, n7);
    }

    public void addLine(int n, Location location, int n2, int n3, int n4) {
        this.addLine("", n, false, location, n2, n3, n4);
    }

    public void addLine(int n, int n2, int n3, int n4, Location location) {
        this.addLine("", n, false, n2, n3, n4, location);
    }

    public void addLine(int n, Location location, Location location2) {
        this.addLine("", n, false, location, location2);
    }

    public void addLine(String string, Color color, boolean bl, int n, int n2, int n3, int n4, int n5, int n6) {
        this.addLine(string, color.getRGB(), bl, n, n2, n3, n4, n5, n6);
    }

    public void addLine(String string, Color color, boolean bl, Location location, int n, int n2, int n3) {
        this.addLine(string, color.getRGB(), bl, location, n, n2, n3);
    }

    public void addLine(String string, Color color, boolean bl, int n, int n2, int n3, Location location) {
        this.addLine(string, color.getRGB(), bl, n, n2, n3, location);
    }

    public void addLine(String string, Color color, boolean bl, Location location, Location location2) {
        this.addLine(string, color.getRGB(), bl, location, location2);
    }

    public void addLine(Color color, int n, int n2, int n3, int n4, int n5, int n6) {
        this.addLine("", color, false, n, n2, n3, n4, n5, n6);
    }

    public void addLine(Color color, Location location, int n, int n2, int n3) {
        this.addLine("", color, false, location, n, n2, n3);
    }

    public void addLine(Color color, int n, int n2, int n3, Location location) {
        this.addLine("", color, false, n, n2, n3, location);
    }

    public void addLine(Color color, Location location, Location location2) {
        this.addLine("", color, false, location, location2);
    }

    @Override
    protected void writeImpl() {
        int n;
        this.writeC(254);
        this.writeH(36);
        this.writeS(this._name);
        this.writeD(this._x);
        this.writeD(this._y);
        this.writeD(this.Di);
        this.writeD(65535);
        this.writeD(65535);
        this.writeD(this.ahS.size() + this.ahT.size());
        for (Point point : this.ahS) {
            this.writeC(1);
            this.writeS(point.getName());
            n = point.getColor();
            this.writeD(n >> 16 & 0xFF);
            this.writeD(n >> 8 & 0xFF);
            this.writeD(n & 0xFF);
            this.writeD(point.isNameColored() ? 1 : 0);
            this.writeD(point.getX());
            this.writeD(point.getY());
            this.writeD(point.getZ());
        }
        for (Line line : this.ahT) {
            this.writeC(2);
            this.writeS(line.getName());
            n = line.getColor();
            this.writeD(n >> 16 & 0xFF);
            this.writeD(n >> 8 & 0xFF);
            this.writeD(n & 0xFF);
            this.writeD(line.isNameColored() ? 1 : 0);
            this.writeD(line.getX());
            this.writeD(line.getY());
            this.writeD(line.getZ());
            this.writeD(line.getX2());
            this.writeD(line.getY2());
            this.writeD(line.getZ2());
        }
    }

    private static class Line
    extends Point {
        private final int ahU;
        private final int ahV;
        private final int ahW;

        public Line(String string, int n, boolean bl, int n2, int n3, int n4, int n5, int n6, int n7) {
            super(string, n, bl, n2, n3, n4);
            this.ahU = n5;
            this.ahV = n6;
            this.ahW = n7;
        }

        public int getX2() {
            return this.ahU;
        }

        public int getY2() {
            return this.ahV;
        }

        public int getZ2() {
            return this.ahW;
        }
    }

    private static class Point {
        private final String _name;
        private final int ahX;
        private final boolean ahY;
        private final int _x;
        private final int _y;
        private final int Di;

        public Point(String string, int n, boolean bl, int n2, int n3, int n4) {
            this._name = string;
            this.ahX = n;
            this.ahY = bl;
            this._x = n2;
            this._y = n3;
            this.Di = n4;
        }

        public String getName() {
            return this._name;
        }

        public int getColor() {
            return this.ahX;
        }

        public boolean isNameColored() {
            return this.ahY;
        }

        public int getX() {
            return this._x;
        }

        public int getY() {
            return this._y;
        }

        public int getZ() {
            return this.Di;
        }
    }
}

