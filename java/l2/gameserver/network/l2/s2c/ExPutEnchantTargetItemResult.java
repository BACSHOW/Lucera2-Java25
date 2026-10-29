/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExPutEnchantTargetItemResult
extends L2GameServerPacket {
    public static final L2GameServerPacket FAIL = new ExPutEnchantTargetItemResult(0);
    public static final L2GameServerPacket SUCCESS = new ExPutEnchantTargetItemResult(1);
    private int oJ;

    private ExPutEnchantTargetItemResult(int n) {
        this.oJ = n;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(129);
        this.writeD(this.oJ);
    }
}

