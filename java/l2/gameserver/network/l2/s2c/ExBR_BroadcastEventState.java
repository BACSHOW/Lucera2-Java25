/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExBR_BroadcastEventState
extends L2GameServerPacket {
    private int agd;
    private int age;
    private int agf;
    private int agg;
    private int agh;
    private int agi;
    private int agj;
    private String agk;
    private String agl;
    public static final int APRIL_FOOLS = 20090401;
    public static final int EVAS_INFERNO = 20090801;
    public static final int HALLOWEEN_EVENT = 20091031;
    public static final int RAISING_RUDOLPH = 20091225;
    public static final int LOVERS_JUBILEE = 20100214;
    public static final int APRIL_FOOLS_10 = 20100401;

    public ExBR_BroadcastEventState(int n, int n2) {
        this.agd = n;
        this.age = n2;
    }

    public ExBR_BroadcastEventState(int n, int n2, int n3, int n4, int n5, int n6, int n7, String string, String string2) {
        this.agd = n;
        this.age = n2;
        this.agf = n3;
        this.agg = n4;
        this.agh = n5;
        this.agi = n6;
        this.agj = n7;
        this.agk = string;
        this.agl = string2;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(188);
        this.writeD(this.agd);
        this.writeD(this.age);
        this.writeD(this.agf);
        this.writeD(this.agg);
        this.writeD(this.agh);
        this.writeD(this.agi);
        this.writeD(this.agj);
        this.writeS(this.agk);
        this.writeS(this.agl);
    }
}

