/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PledgeStatusChanged
extends L2GameServerPacket {
    private int agD;
    private int aeC;
    private int level;

    public PledgeStatusChanged(Clan clan) {
        this.agD = clan.getLeaderId();
        this.aeC = clan.getClanId();
        this.level = clan.getLevel();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(205);
        this.writeD(this.agD);
        this.writeD(this.aeC);
        this.writeD(0);
        this.writeD(this.level);
        this.writeD(0);
        this.writeD(0);
        this.writeD(0);
    }
}

