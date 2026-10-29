/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExRegenMax
extends L2GameServerPacket {
    private double ahK;
    private int _count;
    private int _time;
    public static final int POTION_HEALING_GREATER = 16457;
    public static final int POTION_HEALING_MEDIUM = 16440;
    public static final int POTION_HEALING_LESSER = 16416;

    public ExRegenMax(double d, int n, int n2) {
        this.ahK = d * 0.66;
        this._count = n;
        this._time = n2;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(1);
        this.writeD(1);
        this.writeD(this._count);
        this.writeD(this._time);
        this.writeF(this.ahK);
    }
}

