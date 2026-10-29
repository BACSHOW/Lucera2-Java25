/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PledgeReceiveUpdatePower
extends L2GameServerPacket {
    private int YN;

    public PledgeReceiveUpdatePower(int n) {
        this.YN = n;
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(66);
        this.writeD(this.YN);
    }
}

