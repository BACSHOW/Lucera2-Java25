/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExSubPledgeSkillAdd
extends L2GameServerPacket {
    private int _type;
    private int _id;
    private int _level;

    public ExSubPledgeSkillAdd(int n, int n2, int n3) {
        this._type = n;
        this._id = n2;
        this._level = n3;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(118);
        this.writeD(this._type);
        this.writeD(this._id);
        this.writeD(this._level);
    }
}

