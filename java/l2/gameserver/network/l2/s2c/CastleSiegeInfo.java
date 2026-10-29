/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.s2c;

import java.util.Calendar;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import org.apache.commons.lang3.ArrayUtils;

public class CastleSiegeInfo
extends L2GameServerPacket {
    private long adX;
    private int _id;
    private int adY;
    private int Yo;
    private boolean adZ;
    private String _ownerName = "NPC";
    private String aea = "";
    private String Yn = "";
    private int[] aeb = ArrayUtils.EMPTY_INT_ARRAY;

    public CastleSiegeInfo(Castle castle, Player player) {
        this((Residence)castle, player);
        CastleSiegeEvent castleSiegeEvent = (CastleSiegeEvent)castle.getSiegeEvent();
        long l = castle.getSiegeDate().getTimeInMillis();
        if (l == 0L) {
            this.aeb = castleSiegeEvent.getNextSiegeTimes();
        } else {
            this.adX = (int)(l / 1000L);
        }
    }

    public CastleSiegeInfo(ClanHall clanHall, Player player) {
        this((Residence)clanHall, player);
        this.adX = (int)(clanHall.getSiegeDate().getTimeInMillis() / 1000L);
    }

    protected CastleSiegeInfo(Residence residence, Player player) {
        this._id = residence.getId();
        this.adY = residence.getOwnerId();
        Clan clan = residence.getOwner();
        if (clan != null) {
            this.adZ = clan.getLeaderId(0) == player.getObjectId();
            this._ownerName = clan.getName();
            this.aea = clan.getLeaderName(0);
            Alliance alliance = clan.getAlliance();
            if (alliance != null) {
                this.Yo = alliance.getAllyId();
                this.Yn = alliance.getAllyName();
            }
        }
    }

    @Override
    protected void writeImpl() {
        this.writeC(201);
        this.writeD(this._id);
        this.writeD(this.adZ ? 1 : 0);
        this.writeD(this.adY);
        this.writeS(this._ownerName);
        this.writeS(this.aea);
        this.writeD(this.Yo);
        this.writeS(this.Yn);
        this.writeD((int)(Calendar.getInstance().getTimeInMillis() / 1000L));
        this.writeD((int)this.adX);
        if (this.adX == 0L) {
            this.writeDD(this.aeb, true);
        }
    }
}

