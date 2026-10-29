/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

@Deprecated
public class TradePressOwnOk
extends L2GameServerPacket {
    @Override
    protected final void writeImpl() {
        this.writeC(34);
        this.writeD(0);
    }
}

