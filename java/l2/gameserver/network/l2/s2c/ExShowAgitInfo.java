/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.entity.events.impl.ClanHallAuctionEvent;
import l2.gameserver.model.entity.events.impl.ClanHallMiniGameEvent;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.tables.ClanTable;

public class ExShowAgitInfo
extends L2GameServerPacket {
    private List<AgitInfo> aia = Collections.emptyList();

    public ExShowAgitInfo() {
        List<ClanHall> list = ResidenceHolder.getInstance().getResidenceList(ClanHall.class);
        this.aia = new ArrayList<AgitInfo>(list.size());
        for (ClanHall clanHall : list) {
            int n = clanHall.getId();
            int n2 = clanHall.getSiegeEvent().getClass() == ClanHallAuctionEvent.class ? 0 : (clanHall.getSiegeEvent().getClass() == ClanHallMiniGameEvent.class ? 2 : 1);
            Clan clan = ClanTable.getInstance().getClan(clanHall.getOwnerId());
            String string = clanHall.getOwnerId() == 0 || clan == null ? "" : clan.getName();
            String string2 = clanHall.getOwnerId() == 0 || clan == null ? "" : clan.getLeaderName();
            this.aia.add(new AgitInfo(string, string2, n, n2));
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(22);
        this.writeD(this.aia.size());
        for (AgitInfo agitInfo : this.aia) {
            this.writeD(agitInfo.ch_id);
            this.writeS(agitInfo.clan_name);
            this.writeS(agitInfo.leader_name);
            this.writeD(agitInfo.getType);
        }
    }

    static class AgitInfo {
        public String clan_name;
        public String leader_name;
        public int ch_id;
        public int getType;

        public AgitInfo(String string, String string2, int n, int n2) {
            this.clan_name = string;
            this.leader_name = string2;
            this.ch_id = n;
            this.getType = n2;
        }
    }
}

