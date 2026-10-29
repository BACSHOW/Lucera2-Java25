/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class GMViewPledgeInfo
extends L2GameServerPacket {
    private String char_name;
    private String clan_name;
    private String leader_name;
    private String ally_name;
    private int aeC;
    private int aeD;
    private int ajd;
    private int rank;
    private int aje;
    private int aeF;
    private int aeG;
    private int ajf;
    private int ajg;
    private int ajh;
    private List<PledgeMemberInfo> aji = new ArrayList<PledgeMemberInfo>();

    public GMViewPledgeInfo(Player player) {
        Clan clan = player.getClan();
        for (UnitMember unitMember : clan) {
            if (unitMember == null) continue;
            this.char_name = unitMember.getName();
            this.ajd = unitMember.getLevel();
            this.aeC = unitMember.getClassId();
            this.aeD = unitMember.isOnline() ? unitMember.getObjectId() : 0;
            this.aje = unitMember.getSponsor() != 0 ? 1 : 0;
            this.aji.add(new PledgeMemberInfo(this.char_name, this.ajd, this.aeC, this.aeD, unitMember.getSex(), 1, this.aje));
        }
        this.char_name = player.getName();
        this.aeC = clan.getClanId();
        this.clan_name = clan.getName();
        this.leader_name = clan.getLeaderName();
        this.aeD = clan.getCrestId();
        this.ajd = clan.getLevel();
        this.ajf = clan.getCastle();
        this.ajg = clan.getHasHideout();
        this.rank = clan.getRank();
        this.aje = clan.getReputationScore();
        this.aeF = clan.getAllyId();
        if (clan.getAlliance() != null) {
            this.ally_name = clan.getAlliance().getAllyName();
            this.aeG = clan.getAlliance().getAllyCrestId();
        } else {
            this.ally_name = "";
            this.aeG = 0;
        }
        this.ajh = clan.isAtWar();
    }

    @Override
    protected final void writeImpl() {
        this.writeC(144);
        this.writeS(this.char_name);
        this.writeD(this.aeC);
        this.writeD(0);
        this.writeS(this.clan_name);
        this.writeS(this.leader_name);
        this.writeD(this.aeD);
        this.writeD(this.ajd);
        this.writeD(this.ajf);
        this.writeD(this.ajg);
        this.writeD(this.rank);
        this.writeD(this.aje);
        this.writeD(0);
        this.writeD(0);
        this.writeD(this.aeF);
        this.writeS(this.ally_name);
        this.writeD(this.aeG);
        this.writeD(this.ajh);
        this.writeD(this.aji.size());
        for (PledgeMemberInfo pledgeMemberInfo : this.aji) {
            this.writeS(pledgeMemberInfo._name);
            this.writeD(pledgeMemberInfo.level);
            this.writeD(pledgeMemberInfo.class_id);
            this.writeD(pledgeMemberInfo.sex);
            this.writeD(pledgeMemberInfo.race);
            this.writeD(pledgeMemberInfo.online);
            this.writeD(pledgeMemberInfo.sponsor);
        }
        this.aji.clear();
    }

    static class PledgeMemberInfo {
        public String _name;
        public int level;
        public int class_id;
        public int online;
        public int sex;
        public int race;
        public int sponsor;

        public PledgeMemberInfo(String string, int n, int n2, int n3, int n4, int n5, int n6) {
            this._name = string;
            this.level = n;
            this.class_id = n2;
            this.online = n3;
            this.sex = n4;
            this.race = n5;
            this.sponsor = n6;
        }
    }
}

