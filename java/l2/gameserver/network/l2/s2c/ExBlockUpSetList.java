/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExBlockUpSetList
extends L2GameServerPacket {
    private int agr = 0;

    @Override
    protected void writeImpl() {
        this.writeEx(151);
        this.writeD(this.agr);
        switch (this.agr) {
            case 0: {
                break;
            }
            case 1: {
                break;
            }
            case 2: {
                break;
            }
            case 3: {
                break;
            }
            case 4: {
                break;
            }
            case 5: {
                break;
            }
        }
    }
}

