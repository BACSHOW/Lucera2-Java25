/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.SubUnit;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PledgeShowMemberListAll
extends L2GameServerPacket {
    private int alm;
    private int aln;
    private int _level;
    private int YM;
    private int YG;
    private int alo;
    private int alp;
    private int Yt;
    private int alq;
    private int alr;
    private String als;
    private String aea;
    private String alt;
    private int Ic;
    private List<PledgePacketMember> Go;
    private boolean alj;

    public PledgeShowMemberListAll(Clan clan, SubUnit subUnit) {
        this.Ic = subUnit.getType();
        this.alm = clan.getClanId();
        this.als = subUnit.getName();
        this.aea = subUnit.getLeaderName();
        this.aln = clan.getCrestId();
        this._level = clan.getLevel();
        this.Yt = clan.getCastle();
        this.alq = clan.getHasHideout();
        this.YM = clan.getRank();
        this.YG = clan.getReputationScore();
        this.alr = clan.isAtWarOrUnderAttack();
        this.alj = clan.isPlacedForDisband();
        Alliance alliance = clan.getAlliance();
        if (alliance != null) {
            this.alo = alliance.getAllyId();
            this.alt = alliance.getAllyName();
            this.alp = alliance.getAllyCrestId();
        }
        this.Go = new ArrayList<PledgePacketMember>(subUnit.size());
        for (UnitMember unitMember : subUnit.getUnitMembers()) {
            this.Go.add(new PledgePacketMember(unitMember));
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeC(83);
        this.writeD(this.Ic == 0 ? 0 : 1);
        this.writeD(this.alm);
        this.writeD(this.Ic);
        this.writeS(this.als);
        this.writeS(this.aea);
        this.writeD(this.aln);
        this.writeD(this._level);
        this.writeD(this.Yt);
        this.writeD(this.alq);
        this.writeD(this.YM);
        this.writeD(this.YG);
        this.writeD(this.alj ? 3 : 0);
        this.writeD(0);
        this.writeD(this.alo);
        this.writeS(this.alt);
        this.writeD(this.alp);
        this.writeD(this.alr);
        this.writeD(this.Go.size());
        for (PledgePacketMember pledgePacketMember : this.Go) {
            this.writeS(pledgePacketMember._name);
            this.writeD(pledgePacketMember._level);
            this.writeD(pledgePacketMember.CW);
            this.writeD(pledgePacketMember.Dd);
            this.writeD(pledgePacketMember.CV);
            this.writeD(pledgePacketMember.all);
            this.writeD(pledgePacketMember.alu ? 1 : 0);
        }
    }

    private class PledgePacketMember {
        private String _name;
        private int _level;
        private int CW;
        private int Dd;
        private int CV;
        private int all;
        private boolean alu;

        public PledgePacketMember(UnitMember unitMember) {
            this._name = unitMember.getName();
            this._level = unitMember.getLevel();
            this.CW = unitMember.getClassId();
            this.Dd = unitMember.getSex();
            this.CV = 0;
            this.all = unitMember.isOnline() ? unitMember.getObjectId() : 0;
            this.alu = unitMember.getSponsor() != 0;
        }
    }
}

