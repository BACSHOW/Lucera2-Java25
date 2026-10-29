/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class Dice
extends L2GameServerPacket {
    private int afo;
    private int _itemId;
    private int _number;
    private int _x;
    private int _y;
    private int Di;

    public Dice(int n, int n2, int n3, int n4, int n5, int n6) {
        this.afo = n;
        this._itemId = n2;
        this._number = n3;
        this._x = n4;
        this._y = n5;
        this.Di = n6;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(212);
        this.writeD(this.afo);
        this.writeD(this._itemId);
        this.writeD(this._number);
        this.writeD(this._x);
        this.writeD(this._y);
        this.writeD(this.Di);
    }
}

