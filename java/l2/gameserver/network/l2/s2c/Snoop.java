/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class Snoop
extends L2GameServerPacket {
    private int amD;
    private String _name;
    private int _type;
    private int amE;
    private String amF;
    private String[] _params;

    public Snoop(int n, String string, int n2, String string2, String string3, int n3, String ... stringArray) {
        this.amD = n;
        this._name = string;
        this._type = n2;
        this.amF = string2;
        this.amE = n3;
        this._params = stringArray;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(213);
        this.writeD(this.amD);
        this.writeS(this._name);
        this.writeD(0);
        this.writeD(this._type);
        this.writeS(this.amF);
        for (String string : this._params) {
            this.writeS(string);
        }
    }
}

