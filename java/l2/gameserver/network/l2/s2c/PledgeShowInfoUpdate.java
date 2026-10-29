/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PledgeShowInfoUpdate
extends L2GameServerPacket {
    private int aeC;
    private int ajd;
    private int ale;
    private int alf;
    private int crest_id;
    private int aeF;
    private int ally_crest;
    private int alg;
    private String ally_name = "";
    private int alh;
    private int ali;
    private boolean alj;

    public PledgeShowInfoUpdate(Clan clan) {
        this.aeC = clan.getClanId();
        this.ajd = clan.getLevel();
        this.alh = clan.getCastle();
        this.ali = clan.getHasHideout();
        this.ale = clan.getRank();
        this.alf = clan.getReputationScore();
        this.crest_id = clan.getCrestId();
        this.aeF = clan.getAllyId();
        this.alg = clan.isAtWar();
        this.alj = clan.isPlacedForDisband();
        Alliance alliance = clan.getAlliance();
        if (alliance != null) {
            this.ally_name = alliance.getAllyName();
            this.ally_crest = alliance.getAllyCrestId();
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeC(136);
        this.writeD(this.aeC);
        this.writeD(this.crest_id);
        this.writeD(this.ajd);
        this.writeD(this.alh);
        this.writeD(this.ali);
        this.writeD(this.ale);
        this.writeD(this.alf);
        this.writeD(this.alj ? 3 : 0);
        this.writeD(0);
        this.writeD(this.aeF);
        this.writeS(this.ally_name);
        this.writeD(this.ally_crest);
        this.writeD(this.alg);
    }
}

