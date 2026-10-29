/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.geodata;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.Config;
import l2.gameserver.geodata.GeoEngine;
import l2.gameserver.geodata.PathFindBuffers;
import l2.gameserver.utils.Location;

public class PathFind {
    private int xT = 0;
    private PathFindBuffers.PathFindBuffer xU;
    private List<Location> xV;
    private final short[] xW = new short[2];
    private final Location xX;
    private final Location xY;
    private PathFindBuffers.GeoNode xZ;
    private PathFindBuffers.GeoNode ya;
    private PathFindBuffers.GeoNode yb;

    public PathFind(int n, int n2, int n3, int n4, int n5, int n6, boolean bl, int n7) {
        this.xT = n7;
        this.xX = Config.PATHFIND_BOOST == 0 ? new Location(n, n2, n3) : GeoEngine.moveCheckWithCollision(n, n2, n3, n4, n5, true, n7);
        this.xY = Config.PATHFIND_BOOST != 2 || Math.abs(n6 - n3) > 200 ? new Location(n4, n5, n6) : GeoEngine.moveCheckBackwardWithCollision(n4, n5, n6, this.xX.x, this.xX.y, true, n7);
        this.xX.world2geo();
        this.xY.world2geo();
        this.xX.z = GeoEngine.NgetHeight(this.xX.x, this.xX.y, this.xX.z, n7);
        this.xY.z = GeoEngine.NgetHeight(this.xY.x, this.xY.y, this.xY.z, n7);
        int n8 = Math.abs(this.xY.x - this.xX.x);
        int n9 = Math.abs(this.xY.y - this.xX.y);
        if (n8 == 0 && n9 == 0) {
            if (Math.abs(this.xY.z - this.xX.z) < 32) {
                this.xV = new ArrayList<Location>();
                this.xV.add(0, this.xX);
            }
            return;
        }
        int n10 = 2 * Math.max(n8, n9);
        this.xU = PathFindBuffers.alloc(n10);
        if (this.xU != null) {
            this.xU.offsetX = this.xX.x - this.xU.mapSize / 2;
            this.xU.offsetY = this.xX.y - this.xU.mapSize / 2;
            ++this.xU.totalUses;
            if (bl) {
                ++this.xU.playableUses;
            }
            this.bv();
            this.xU.free();
            PathFindBuffers.recycle(this.xU);
        }
    }

    private List<Location> bv() {
        this.xZ = this.xU.nodes[this.xX.x - this.xU.offsetX][this.xX.y - this.xU.offsetY].set(this.xX.x, this.xX.y, (short)this.xX.z);
        GeoEngine.NgetHeightAndNSWE(this.xX.x, this.xX.y, (short)this.xX.z, this.xW, this.xT);
        this.xZ.z = this.xW[0];
        this.xZ.nswe = this.xW[1];
        this.xZ.costFromStart = 0.0f;
        this.xZ.state = 1;
        this.xZ.parent = null;
        this.ya = this.xU.nodes[this.xY.x - this.xU.offsetX][this.xY.y - this.xU.offsetY].set(this.xY.x, this.xY.y, (short)this.xY.z);
        this.xZ.costToEnd = this.c(this.xZ);
        this.xZ.totalCost = this.xZ.costFromStart + this.xZ.costToEnd;
        this.xU.open.add(this.xZ);
        long l = System.nanoTime();
        long l2 = 0L;
        int n = 0;
        while ((l2 = System.nanoTime() - l) < Config.PATHFIND_MAX_TIME && (this.yb = this.xU.open.poll()) != null) {
            ++n;
            if (this.yb.x == this.xY.x && this.yb.y == this.xY.y && Math.abs(this.yb.z - this.xY.z) < 64) {
                this.xV = this.a(this.yb);
                break;
            }
            this.b(this.yb);
            this.yb.state = -1;
        }
        this.xU.totalTime += l2;
        this.xU.totalItr += (long)n;
        if (this.xV != null) {
            ++this.xU.successUses;
        } else if (l2 > Config.PATHFIND_MAX_TIME) {
            ++this.xU.overtimeUses;
        }
        return this.xV;
    }

    private List<Location> a(PathFindBuffers.GeoNode geoNode) {
        ArrayList<Location> arrayList = new ArrayList<Location>();
        do {
            arrayList.add(0, geoNode.getLoc());
            geoNode = geoNode.parent;
        } while (geoNode.parent != null);
        return arrayList;
    }

    private void b(PathFindBuffers.GeoNode geoNode) {
        int n = geoNode.x;
        int n2 = geoNode.y;
        short s = geoNode.z;
        this.a(n, n2, s);
        short s2 = this.xW[1];
        if (Config.PATHFIND_DIAGONAL) {
            if ((s2 & 4) == 4 && (s2 & 1) == 1) {
                this.a(n + 1, n2, s);
                if ((this.xW[1] & 4) == 4) {
                    this.a(n, n2 + 1, s);
                    if ((this.xW[1] & 1) == 1) {
                        this.a(n + 1, n2 + 1, geoNode, true);
                    }
                }
            }
            if ((s2 & 4) == 4 && (s2 & 2) == 2) {
                this.a(n - 1, n2, s);
                if ((this.xW[1] & 4) == 4) {
                    this.a(n, n2 + 1, s);
                    if ((this.xW[1] & 2) == 2) {
                        this.a(n - 1, n2 + 1, geoNode, true);
                    }
                }
            }
            if ((s2 & 8) == 8 && (s2 & 1) == 1) {
                this.a(n + 1, n2, s);
                if ((this.xW[1] & 8) == 8) {
                    this.a(n, n2 - 1, s);
                    if ((this.xW[1] & 1) == 1) {
                        this.a(n + 1, n2 - 1, geoNode, true);
                    }
                }
            }
            if ((s2 & 8) == 8 && (s2 & 2) == 2) {
                this.a(n - 1, n2, s);
                if ((this.xW[1] & 8) == 8) {
                    this.a(n, n2 - 1, s);
                    if ((this.xW[1] & 2) == 2) {
                        this.a(n - 1, n2 - 1, geoNode, true);
                    }
                }
            }
        }
        if ((s2 & 1) == 1) {
            this.a(n + 1, n2, geoNode, false);
        }
        if ((s2 & 2) == 2) {
            this.a(n - 1, n2, geoNode, false);
        }
        if ((s2 & 4) == 4) {
            this.a(n, n2 + 1, geoNode, false);
        }
        if ((s2 & 8) == 8) {
            this.a(n, n2 - 1, geoNode, false);
        }
    }

    private float c(PathFindBuffers.GeoNode geoNode) {
        int n = this.ya.x - geoNode.x;
        int n2 = this.ya.y - geoNode.y;
        int n3 = this.ya.z - geoNode.z;
        return (float)Math.sqrt(n * n + n2 * n2 + n3 * n3 / 256);
    }

    private float a(PathFindBuffers.GeoNode geoNode, PathFindBuffers.GeoNode geoNode2, boolean bl) {
        if (geoNode2.nswe != 15 || Math.abs(geoNode2.z - geoNode.z) > 16) {
            return 3.0f;
        }
        this.a(geoNode2.x + 1, geoNode2.y, geoNode2.z);
        if (this.xW[1] != 15 || Math.abs(geoNode2.z - this.xW[0]) > 16) {
            return 2.0f;
        }
        this.a(geoNode2.x - 1, geoNode2.y, geoNode2.z);
        if (this.xW[1] != 15 || Math.abs(geoNode2.z - this.xW[0]) > 16) {
            return 2.0f;
        }
        this.a(geoNode2.x, geoNode2.y + 1, geoNode2.z);
        if (this.xW[1] != 15 || Math.abs(geoNode2.z - this.xW[0]) > 16) {
            return 2.0f;
        }
        this.a(geoNode2.x, geoNode2.y - 1, geoNode2.z);
        if (this.xW[1] != 15 || Math.abs(geoNode2.z - this.xW[0]) > 16) {
            return 2.0f;
        }
        return bl ? 1.414f : 1.0f;
    }

    private void a(int n, int n2, PathFindBuffers.GeoNode geoNode, boolean bl) {
        int n3;
        int n4 = n - this.xU.offsetX;
        int n5 = n2 - this.xU.offsetY;
        if (n4 >= this.xU.mapSize || n4 < 0 || n5 >= this.xU.mapSize || n5 < 0) {
            return;
        }
        PathFindBuffers.GeoNode geoNode2 = this.xU.nodes[n4][n5];
        if (!geoNode2.isSet()) {
            geoNode2 = geoNode2.set(n, n2, geoNode.z);
            GeoEngine.NgetHeightAndNSWE(n, n2, geoNode.z, this.xW, this.xT);
            geoNode2.z = this.xW[0];
            geoNode2.nswe = this.xW[1];
        }
        if ((n3 = Math.abs(geoNode2.z - geoNode.z)) > Config.PATHFIND_MAX_Z_DIFF || geoNode2.nswe == 0) {
            return;
        }
        float f = geoNode.costFromStart + this.a(geoNode, geoNode2, bl);
        if ((geoNode2.state == 1 || geoNode2.state == -1) && geoNode2.costFromStart <= f) {
            return;
        }
        if (geoNode2.state == 0) {
            geoNode2.costToEnd = this.c(geoNode2);
        }
        geoNode2.parent = geoNode;
        geoNode2.costFromStart = f;
        geoNode2.totalCost = geoNode2.costFromStart + geoNode2.costToEnd;
        if (geoNode2.state == 1) {
            return;
        }
        geoNode2.state = 1;
        this.xU.open.add(geoNode2);
    }

    private void a(int n, int n2, short s) {
        int n3 = n - this.xU.offsetX;
        int n4 = n2 - this.xU.offsetY;
        if (n3 >= this.xU.mapSize || n3 < 0 || n4 >= this.xU.mapSize || n4 < 0) {
            this.xW[1] = 0;
            return;
        }
        PathFindBuffers.GeoNode geoNode = this.xU.nodes[n3][n4];
        if (!geoNode.isSet()) {
            geoNode = geoNode.set(n, n2, s);
            GeoEngine.NgetHeightAndNSWE(n, n2, s, this.xW, this.xT);
            geoNode.z = this.xW[0];
            geoNode.nswe = this.xW[1];
        } else {
            this.xW[0] = geoNode.z;
            this.xW[1] = geoNode.nswe;
        }
    }

    public List<Location> getPath() {
        return this.xV;
    }
}

