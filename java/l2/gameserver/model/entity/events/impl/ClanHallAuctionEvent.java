/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.impl;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import l2.commons.collections.MultiValueSet;
import l2.commons.dao.JdbcEntityState;
import l2.gameserver.Config;
import l2.gameserver.dao.SiegeClanDAO;
import l2.gameserver.instancemanager.PlayerMessageStack;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.actions.StartStopAction;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.events.objects.AuctionSiegeClanObject;
import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.tables.ClanTable;

public class ClanHallAuctionEvent
extends SiegeEvent<ClanHall, AuctionSiegeClanObject> {
    private Calendar QM = Calendar.getInstance();

    public ClanHallAuctionEvent(MultiValueSet<String> multiValueSet) {
        super(multiValueSet);
    }

    @Override
    public void reCalcNextTime(boolean bl) {
        this.clearActions();
        this._onTimeActions.clear();
        Clan clan = ((ClanHall)this.getResidence()).getOwner();
        this.QM.setTimeInMillis(0L);
        if (((ClanHall)this.getResidence()).getAuctionLength() == 0 && clan == null) {
            ((ClanHall)this.getResidence()).getSiegeDate().setTimeInMillis(System.currentTimeMillis());
            ((ClanHall)this.getResidence()).getSiegeDate().set(7, 2);
            ((ClanHall)this.getResidence()).getSiegeDate().set(11, 15);
            ((ClanHall)this.getResidence()).getSiegeDate().set(12, 0);
            ((ClanHall)this.getResidence()).getSiegeDate().set(13, 0);
            ((ClanHall)this.getResidence()).getSiegeDate().set(14, 0);
            ((ClanHall)this.getResidence()).setAuctionLength(Config.CLNHALL_REWARD_CYCLE / 24);
            ((ClanHall)this.getResidence()).setAuctionMinBid(((ClanHall)this.getResidence()).getBaseMinBid());
            ((ClanHall)this.getResidence()).setJdbcState(JdbcEntityState.UPDATED);
            ((ClanHall)this.getResidence()).update();
            this._onTimeActions.clear();
            this.addOnTimeAction(0, new StartStopAction("event", true));
            this.addOnTimeAction(((ClanHall)this.getResidence()).getAuctionLength() * 86400, new StartStopAction("event", false));
            this.QM.setTimeInMillis(((ClanHall)this.getResidence()).getSiegeDate().getTimeInMillis() + (long)((ClanHall)this.getResidence()).getAuctionLength() * 86400000L);
            this.registerActions();
        } else if (((ClanHall)this.getResidence()).getAuctionLength() != 0 || clan == null) {
            long l = ((ClanHall)this.getResidence()).getSiegeDate().getTimeInMillis() + TimeUnit.DAYS.toMillis(((ClanHall)this.getResidence()).getAuctionLength());
            if (l <= System.currentTimeMillis() && !bl) {
                ((ClanHall)this.getResidence()).getSiegeDate().setTimeInMillis(System.currentTimeMillis());
            }
            this.QM.setTimeInMillis(((ClanHall)this.getResidence()).getSiegeDate().getTimeInMillis() + TimeUnit.DAYS.toMillis(((ClanHall)this.getResidence()).getAuctionLength()));
            this._onTimeActions.clear();
            this.addOnTimeAction(0, new StartStopAction("event", true));
            this.addOnTimeAction((int)TimeUnit.DAYS.toSeconds(((ClanHall)this.getResidence()).getAuctionLength()), new StartStopAction("event", false));
            this.registerActions();
        }
    }

    @Override
    public void stopEvent(boolean bl) {
        AuctionSiegeClanObject auctionSiegeClanObject;
        List list = this.removeObjects("attackers");
        AuctionSiegeClanObject[] auctionSiegeClanObjectArray = (AuctionSiegeClanObject[])list.toArray(new AuctionSiegeClanObject[list.size()]);
        Arrays.sort(auctionSiegeClanObjectArray, SiegeClanObject.SiegeClanComparatorImpl.getInstance());
        Clan clan = ((ClanHall)this.getResidence()).getOwner();
        AuctionSiegeClanObject auctionSiegeClanObject2 = auctionSiegeClanObject = auctionSiegeClanObjectArray.length > 0 ? auctionSiegeClanObjectArray[0] : null;
        if (auctionSiegeClanObject != null) {
            SystemMessage2 systemMessage2 = (SystemMessage2)new SystemMessage2(SystemMsg.THE_CLAN_HALL_WHICH_WAS_PUT_UP_FOR_AUCTION_HAS_BEEN_AWARDED_TO_S1_CLAN).addString(auctionSiegeClanObject.getClan().getName());
            for (Object __raw_auctionSiegeClanObject3_81 : (Iterable<?>)(Object)(list)) {
               AuctionSiegeClanObject auctionSiegeClanObject3 = (AuctionSiegeClanObject)__raw_auctionSiegeClanObject3_81;
                try {
                    Player player = auctionSiegeClanObject3.getClan().getLeader().getPlayer();
                    if (player != null) {
                        player.sendPacket((IStaticPacket)systemMessage2);
                    } else {
                        PlayerMessageStack.getInstance().mailto(auctionSiegeClanObject3.getClan().getLeaderId(), systemMessage2);
                    }
                }
                catch (Exception exception) {
                    exception.printStackTrace();
                }
                if (auctionSiegeClanObject3 == auctionSiegeClanObject) continue;
                long l = auctionSiegeClanObject3.getParam() - (long)((double)auctionSiegeClanObject3.getParam() * 0.1);
                auctionSiegeClanObject3.getClan().getWarehouse().addItem(Config.CH_BID_CURRENCY_ITEM_ID, l);
            }
            SiegeClanDAO.getInstance().delete((Residence)this.getResidence());
            if (clan != null) {
                clan.getWarehouse().addItem(Config.CH_BID_CURRENCY_ITEM_ID, ((ClanHall)this.getResidence()).getDeposit());
            }
            ((ClanHall)this.getResidence()).setAuctionLength(0);
            ((ClanHall)this.getResidence()).setAuctionMinBid(0L);
            ((ClanHall)this.getResidence()).setAuctionDescription("");
            ((ClanHall)this.getResidence()).getSiegeDate().setTimeInMillis(0L);
            ((ClanHall)this.getResidence()).getLastSiegeDate().setTimeInMillis(0L);
            ((ClanHall)this.getResidence()).getOwnDate().setTimeInMillis(System.currentTimeMillis());
            ((ClanHall)this.getResidence()).setJdbcState(JdbcEntityState.UPDATED);
            ((ClanHall)this.getResidence()).changeOwner(auctionSiegeClanObject.getClan());
            ((ClanHall)this.getResidence()).startCycleTask();
        } else if (clan != null) {
            Player player = clan.getLeader().getPlayer();
            if (player != null) {
                player.sendPacket((IStaticPacket)SystemMsg.THE_CLAN_HALL_WHICH_HAD_BEEN_PUT_UP_FOR_AUCTION_WAS_NOT_SOLD_AND_THEREFORE_HAS_BEEN_RELISTED);
            } else {
                PlayerMessageStack.getInstance().mailto(clan.getLeaderId(), SystemMsg.THE_CLAN_HALL_WHICH_HAD_BEEN_PUT_UP_FOR_AUCTION_WAS_NOT_SOLD_AND_THEREFORE_HAS_BEEN_RELISTED.packet(null));
            }
        } else {
            ((ClanHall)this.getResidence()).setAuctionLength(0);
            ((ClanHall)this.getResidence()).setAuctionMinBid(0L);
            ((ClanHall)this.getResidence()).setAuctionDescription("");
            ((ClanHall)this.getResidence()).getSiegeDate().setTimeInMillis(0L);
            ((ClanHall)this.getResidence()).getLastSiegeDate().setTimeInMillis(0L);
            ((ClanHall)this.getResidence()).getOwnDate().setTimeInMillis(0L);
            ((ClanHall)this.getResidence()).setJdbcState(JdbcEntityState.UPDATED);
        }
        super.stopEvent(bl);
    }

    @Override
    public boolean isParticle(Player player) {
        return false;
    }

    @Override
    public AuctionSiegeClanObject newSiegeClan(String string, int n, long l, long l2) {
        Clan clan = ClanTable.getInstance().getClan(n);
        return clan == null ? null : new AuctionSiegeClanObject(string, clan, l, l2);
    }

    public long getEndSiegeForCH() {
        long l = ((ClanHall)this.getResidence()).getSiegeDate().getTimeInMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(((ClanHall)this.getResidence()).getSiegeDate().getTimeInMillis());
        calendar.set(7, 2);
        calendar.set(11, 15);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        long l2 = calendar.getTimeInMillis() + (long)((ClanHall)this.getResidence()).getAuctionLength() * 86400000L;
        return (l2 - l) / 1000L;
    }

        @Override
    protected void printInfo() {
        this.debug(this.getName() + " time - " + l2.gameserver.utils.TimeUtils.toSimpleFormat(this.startTimeMillis()));
    }

    public Calendar getEndSiegeDate() {
        return this.QM;
    }
}

