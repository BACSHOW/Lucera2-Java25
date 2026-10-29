/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.pledge.RankPrivs;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PledgeReceivePowerInfo
extends L2GameServerPacket {
    private int alb;
    private int ajI;
    private String alc;

    public PledgeReceivePowerInfo(UnitMember unitMember) {
        RankPrivs rankPrivs;
        this.alb = unitMember.getPowerGrade();
        this.alc = unitMember.getName();
        this.ajI = unitMember.isClanLeader() ? 0x7FFFFE : ((rankPrivs = unitMember.getClan().getRankPrivs(unitMember.getPowerGrade())) != null ? rankPrivs.getPrivs() : 0);
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(60);
        this.writeD(this.alb);
        this.writeS(this.alc);
        this.writeD(this.ajI);
    }
}

