/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PledgeSkillListAdd
extends L2GameServerPacket {
    private int _skillId;
    private int _skillLevel;

    public PledgeSkillListAdd(int n, int n2) {
        this._skillId = n;
        this._skillLevel = n2;
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(58);
        this.writeD(this._skillId);
        this.writeD(this._skillLevel);
    }
}

