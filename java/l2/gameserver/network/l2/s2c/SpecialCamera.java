/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class SpecialCamera
extends L2GameServerPacket {
    private int _id;
    private int amI;
    private int amJ;
    private int amK;
    private int _time;
    private int _duration;
    private final int amL;
    private final int amM;
    private final int amN;
    private final int aaM;

    public SpecialCamera(int n, int n2, int n3, int n4, int n5, int n6) {
        this._id = n;
        this.amI = n2;
        this.amJ = n3;
        this.amK = n4;
        this._time = n5;
        this._duration = n6;
        this.amL = 0;
        this.amM = 0;
        this.amN = 0;
        this.aaM = 0;
    }

    public SpecialCamera(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9, int n10) {
        this._id = n;
        this.amI = n2;
        this.amJ = n3;
        this.amK = n4;
        this._time = n5;
        this._duration = n6;
        this.amL = n7;
        this.amM = n8;
        this.amN = n9;
        this.aaM = n10;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(199);
        this.writeD(this._id);
        this.writeD(this.amI);
        this.writeD(this.amJ);
        this.writeD(this.amK);
        this.writeD(this._time);
        this.writeD(this._duration);
        this.writeD(this.amL);
        this.writeD(this.amM);
        this.writeD(this.amN);
        this.writeD(this.aaM);
    }
}

