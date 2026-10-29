/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Summon;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class SetSummonRemainTime
extends L2GameServerPacket {
    private final int amk;
    private final int Vw;

    public SetSummonRemainTime(Summon summon) {
        this.Vw = summon.getCurrentFed();
        this.amk = summon.getMaxFed();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(209);
        this.writeD(this.amk);
        this.writeD(this.Vw);
    }
}

