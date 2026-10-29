/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.tables.ClanTable;

public class ExShowCastleInfo
extends L2GameServerPacket {
    private List<CastleInfo> aib = Collections.emptyList();

    public ExShowCastleInfo() {
        List<Castle> list = ResidenceHolder.getInstance().getResidenceList(Castle.class);
        this.aib = new ArrayList<CastleInfo>(list.size());
        for (Castle castle : list) {
            String string = ClanTable.getInstance().getClanName(castle.getOwnerId());
            int n = castle.getId();
            int n2 = castle.getTaxPercent();
            int n3 = (int)(castle.getSiegeDate().getTimeInMillis() / 1000L);
            this.aib.add(new CastleInfo(string, n, n2, n3));
        }
    }

    @Override
    protected final void writeImpl() {
        this.writeEx(20);
        this.writeD(this.aib.size());
        for (CastleInfo castleInfo : this.aib) {
            this.writeD(castleInfo._id);
            this.writeS(castleInfo._ownerName);
            this.writeD(castleInfo._tax);
            this.writeD(castleInfo._nextSiege);
        }
        this.aib.clear();
    }

    private static class CastleInfo {
        public String _ownerName;
        public int _id;
        public int _tax;
        public int _nextSiege;

        public CastleInfo(String string, int n, int n2, int n3) {
            this._ownerName = string;
            this._id = n;
            this._tax = n2;
            this._nextSiege = n3;
        }
    }
}

