/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExShowScreenMessage
extends L2GameServerPacket {
    public static final int SYSMSG_TYPE = 0;
    public static final int STRING_TYPE = 1;
    private String add;
    private int _type;
    private int aig;
    private boolean aih;
    private boolean aii;
    private ScreenMessageAlign aij;
    private int _time;

    @Deprecated
    public ExShowScreenMessage(String string, int n, ScreenMessageAlign screenMessageAlign, boolean bl) {
        this(string, n, 0, screenMessageAlign, bl, 1, -1, false);
    }

    public ExShowScreenMessage(String string, int n, int n2, ScreenMessageAlign screenMessageAlign, boolean bl, int n3, int n4, boolean bl2) {
        this._type = n3;
        this.aig = n4;
        this._time = n;
        this.aij = screenMessageAlign;
        this.aih = bl;
        this.aii = bl2;
        this._type = n3;
        this.aig = n2;
        this._time = n;
        this.aij = screenMessageAlign;
        this.aih = bl;
        this.aii = bl2;
        this.add = string;
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(56);
        this.writeD(this._type);
        this.writeD(this.aig);
        this.writeD(this.aij.ordinal() + 1);
        this.writeD(0);
        this.writeD(this.aih ? 0 : 1);
        this.writeD(0);
        this.writeD(0);
        this.writeD(this.aii ? 1 : 0);
        this.writeD(this._time);
        this.writeD(1);
        this.writeS(this.add);
    }

    public static enum ScreenMessageAlign {
        TOP_LEFT,
        TOP_CENTER,
        TOP_RIGHT,
        MIDDLE_LEFT,
        MIDDLE_CENTER,
        MIDDLE_RIGHT,
        BOTTOM_CENTER,
        BOTTOM_RIGHT;

    }
}

