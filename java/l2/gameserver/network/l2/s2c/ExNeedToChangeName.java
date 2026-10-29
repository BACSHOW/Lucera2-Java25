/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExNeedToChangeName
extends L2GameServerPacket {
    private int _type;
    private int pE;
    private String aha;

    public ExNeedToChangeName(int n, int n2, String string) {
        this._type = n;
        this.pE = n2;
        this.aha = string;
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(105);
        this.writeD(this._type);
        this.writeD(this.pE);
        this.writeS(this.aha);
    }
}

