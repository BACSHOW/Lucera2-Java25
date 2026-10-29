/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExBlockUpSetState
extends L2GameServerPacket {
    private int ags = 0;

    @Override
    protected void writeImpl() {
        this.writeEx(152);
        this.writeD(this.ags);
        switch (this.ags) {
            case 0: {
                break;
            }
            case 1: {
                break;
            }
        }
    }
}

