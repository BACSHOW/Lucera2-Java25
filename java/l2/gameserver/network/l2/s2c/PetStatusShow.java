/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.Summon;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PetStatusShow
extends L2GameServerPacket {
    private int akU;

    public PetStatusShow(Summon summon) {
        this.akU = summon.getSummonType();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(176);
        this.writeD(this.akU);
    }
}

