/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.residence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import l2.commons.dbutils.DbUtils;
import l2.gameserver.Config;
import l2.gameserver.dao.ClanDataDAO;
import l2.gameserver.dao.ClanHallDAO;
import l2.gameserver.database.DatabaseFactory;
import l2.gameserver.instancemanager.PlayerMessageStack;
import l2.gameserver.model.entity.events.impl.ClanHallAuctionEvent;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.entity.residence.ResidenceType;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClanHall
extends Residence {
    private static final Logger _log = LoggerFactory.getLogger(ClanHall.class);
    private int Ti;
    private long Tj;
    private String Tk = "";
    private final int Tl;
    private final long Tm;
    private final long Tn;
    private final long To;

    public ClanHall(StatsSet statsSet) {
        super(statsSet);
        this.Tl = statsSet.getInteger("grade", 0);
        this.Tm = statsSet.getInteger("rental_fee", 0);
        this.Tn = statsSet.getInteger("min_bid", 0);
        this.To = statsSet.getInteger("deposit", 0);
    }

    @Override
    public void init() {
        this.initZone();
        this.initEvent();
        this.loadData();
        this.loadFunctions();
        this.rewardSkills();
        if (this.getSiegeEvent().getClass() == ClanHallAuctionEvent.class && this._owner != null && this.getAuctionLength() == 0) {
            this.startCycleTask();
        }
    }

    @Override
    public void changeOwner(Clan clan) {
        Clan clan2 = this.getOwner();
        if (clan2 != null && (clan == null || clan.getClanId() != clan2.getClanId())) {
            this.removeSkills();
            clan2.setHasHideout(0);
            this.cancelCycleTask();
        }
        this.a(clan);
        this.rewardSkills();
        this.update();
        if (clan == null && this.getSiegeEvent().getClass() == ClanHallAuctionEvent.class) {
            ((SiegeEvent)this.getSiegeEvent()).reCalcNextTime(false);
        }
    }

    @Override
    public ResidenceType getType() {
        return ResidenceType.ClanHall;
    }

    @Override
    protected void loadData() {
        this._owner = ClanDataDAO.getInstance().getOwner(this);
        ClanHallDAO.getInstance().select(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void a(Clan clan) {
        PreparedStatement preparedStatement;
        Connection connection;
        block4: {
            this._owner = clan;
            connection = null;
            preparedStatement = null;
            try {
                connection = DatabaseFactory.getInstance().getConnection();
                preparedStatement = connection.prepareStatement("UPDATE `clan_data` SET `hasHideout`=0 WHERE `hasHideout`=?");
                preparedStatement.setInt(1, this.getId());
                preparedStatement.execute();
                DbUtils.close(preparedStatement);
                preparedStatement = connection.prepareStatement("UPDATE `clan_data` SET `hasHideout`=? WHERE `clan_id`=?");
                preparedStatement.setInt(1, this.getId());
                preparedStatement.setInt(2, this.getOwnerId());
                preparedStatement.execute();
                DbUtils.close(preparedStatement);
                preparedStatement = connection.prepareStatement("DELETE FROM `residence_functions` WHERE `id`=?");
                preparedStatement.setInt(1, this.getId());
                preparedStatement.execute();
                DbUtils.close(preparedStatement);
                if (clan == null) break block4;
                clan.setHasHideout(this.getId());
                clan.broadcastClanStatus(false, true, false);
            }
            catch (Exception exception) {
                try {
                    _log.warn("Exception: updateOwnerInDB(L2Clan clan): " + exception, exception);
                }
                catch (Throwable throwable) {
                    DbUtils.closeQuietly(connection, preparedStatement);
                    throw throwable;
                }
                DbUtils.closeQuietly(connection, preparedStatement);
            }
        }
        DbUtils.closeQuietly(connection, preparedStatement);
    }

    public int getGrade() {
        return this.Tl;
    }

    @Override
    public void update() {
        ClanHallDAO.getInstance().update(this);
    }

    public int getAuctionLength() {
        return this.Ti;
    }

    public void setAuctionLength(int n) {
        this.Ti = n;
    }

    public String getAuctionDescription() {
        return this.Tk;
    }

    public void setAuctionDescription(String string) {
        this.Tk = string == null ? "" : string;
    }

    public long getAuctionMinBid() {
        return this.Tj;
    }

    public void setAuctionMinBid(long l) {
        this.Tj = l;
    }

    public long getRentalFee() {
        return this.Tm;
    }

    public long getBaseMinBid() {
        return this.Tn;
    }

    public long getDeposit() {
        return this.To;
    }

    @Override
    public void chanceCycle() {
        super.chanceCycle();
        this.setPaidCycle(this.getPaidCycle() + 1);
        if (this.getPaidCycle() >= Config.CLNHALL_REWARD_CYCLE) {
            if (this._owner.getWarehouse().getCountOf(Config.CH_BID_CURRENCY_ITEM_ID) > this.Tm) {
                this._owner.getWarehouse().destroyItemByItemId(Config.CH_BID_CURRENCY_ITEM_ID, this.Tm);
                this.setPaidCycle(0);
            } else {
                UnitMember unitMember = this._owner.getLeader();
                if (unitMember.isOnline()) {
                    unitMember.getPlayer().sendPacket((IStaticPacket)SystemMsg.THE_CLAN_HALL_FEE_IS_ONE_WEEK_OVERDUE_THEREFORE_THE_CLAN_HALL_OWNERSHIP_HAS_BEEN_REVOKED);
                } else {
                    PlayerMessageStack.getInstance().mailto(unitMember.getObjectId(), SystemMsg.THE_CLAN_HALL_FEE_IS_ONE_WEEK_OVERDUE_THEREFORE_THE_CLAN_HALL_OWNERSHIP_HAS_BEEN_REVOKED.packet(null));
                }
                this.changeOwner(null);
            }
        }
    }
}

