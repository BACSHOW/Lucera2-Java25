/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;

public class PledgeReceiveWarList
extends L2GameServerPacket {
    private List<WarInfo> aji = new ArrayList<WarInfo>();
    private int ald;
    private int _page;

    public PledgeReceiveWarList(Clan clan, int n, int n2) {
        this.ald = n;
        this._page = n2;
        for (Clan clan2 : this.ald == 1 ? clan.getAttackerClans() : clan.getEnemyClans()) {
            if (clan2 == null) continue;
            this.aji.add(new WarInfo(clan2.getName(), this.ald, 0));
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(62);
        this.writeD(this.ald);
        this.writeD(0);
        this.writeD(this.aji.size());
        for (WarInfo warInfo : this.aji) {
            this.writeS(warInfo.clan_name);
            this.writeD(warInfo.unk1);
            this.writeD(warInfo.unk2);
        }
    }

    static class WarInfo {
        public String clan_name;
        public int unk1;
        public int unk2;

        public WarInfo(String string, int n, int n2) {
            this.clan_name = string;
            this.unk1 = n;
            this.unk2 = n2;
        }
    }
}

