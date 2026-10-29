/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExAttributeEnchantResult
extends L2GameServerPacket {
    private int oJ;

    public ExAttributeEnchantResult(int n) {
        this.oJ = n;
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(97);
        this.writeD(this.oJ);
    }
}

