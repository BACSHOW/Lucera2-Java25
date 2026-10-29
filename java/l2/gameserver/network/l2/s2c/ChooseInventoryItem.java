/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ChooseInventoryItem
extends L2GameServerPacket {
    private int afn;

    public ChooseInventoryItem(int n) {
        this.afn = n;
    }

    @Override
    protected final void writeImpl() {
        this.writeC(111);
        this.writeD(this.afn);
    }
}

