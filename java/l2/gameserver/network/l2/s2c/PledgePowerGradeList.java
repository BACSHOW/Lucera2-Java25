/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.pledge.RankPrivs;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PledgePowerGradeList
extends L2GameServerPacket {
    private RankPrivs[] akZ;

    public PledgePowerGradeList(RankPrivs[] rankPrivsArray) {
        this.akZ = rankPrivsArray;
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(59);
        this.writeD(this.akZ.length);
        for (RankPrivs rankPrivs : this.akZ) {
            this.writeD(rankPrivs.getRank());
            this.writeD(rankPrivs.getParty());
        }
    }
}

