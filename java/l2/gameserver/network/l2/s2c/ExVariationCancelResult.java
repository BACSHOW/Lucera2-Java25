/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class ExVariationCancelResult
extends L2GameServerPacket {
    public static final ExVariationCancelResult FAIL_PACKET = new ExVariationCancelResult(0);
    private int aiu = 1;
    private int _unk1;

    public ExVariationCancelResult(int n) {
        this._unk1 = n;
    }

    @Override
    protected void writeImpl() {
        this.writeEx(87);
        this.writeD(this.aiu);
        this.writeD(this._unk1);
    }
}

