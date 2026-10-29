/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class AutoAttackStop
extends L2GameServerPacket {
    private int _targetId;

    public AutoAttackStop(int n) {
        this._targetId = n;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(44);
        this.writeD(this._targetId);
    }
}

