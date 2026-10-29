/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class TutorialEnableClientEvent
extends L2GameServerPacket {
    private int anu = 0;

    public TutorialEnableClientEvent(int n) {
        this.anu = n;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(162);
        this.writeD(this.anu);
    }
}

