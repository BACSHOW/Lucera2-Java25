/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ShowTownMap
extends L2GameServerPacket {
    String _texture;
    int _x;
    int _y;

    public ShowTownMap(String string, int n, int n2) {
        this._texture = string;
        this._x = n;
        this._y = n2;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(222);
        this.writeS(this._texture);
        this.writeD(this._x);
        this.writeD(this._y);
    }
}

