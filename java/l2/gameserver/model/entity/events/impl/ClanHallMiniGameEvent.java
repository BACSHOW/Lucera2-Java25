/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import l2.commons.collections.CollectionUtils;
import l2.commons.collections.MultiValueSet;
import l2.gameserver.dao.SiegeClanDAO;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.events.objects.CMGSiegeClanObject;
import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.items.ItemInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.network.l2.s2c.SystemMessage2;
import l2.gameserver.tables.ClanTable;

public class ClanHallMiniGameEvent
extends SiegeEvent<ClanHall, CMGSiegeClanObject> {
    public static final String NEXT_STEP = "next_step";
    public static final String REFUND = "refund";
    private boolean QN = true;

    public ClanHallMiniGameEvent(MultiValueSet<String> multiValueSet) {
        super(multiValueSet);
    }

    @Override
    public void startEvent() {
        this._oldOwner = ((ClanHall)this.getResidence()).getOwner();
        List list = this.getObjects("attackers");
        if (list.size() < 2) {
            CMGSiegeClanObject cMGSiegeClanObject = (CMGSiegeClanObject)CollectionUtils.safeGet(list, 0);
            if (cMGSiegeClanObject != null) {
                CMGSiegeClanObject cMGSiegeClanObject2 = (CMGSiegeClanObject)this.getSiegeClan(REFUND, cMGSiegeClanObject.getObjectId());
                if (cMGSiegeClanObject2 != null) {
                    SiegeClanDAO.getInstance().delete((Residence)this.getResidence(), cMGSiegeClanObject);
                    cMGSiegeClanObject2.setParam(cMGSiegeClanObject2.getParam() + cMGSiegeClanObject.getParam());
                    SiegeClanDAO.getInstance().update((Residence)this.getResidence(), cMGSiegeClanObject2);
                } else {
                    cMGSiegeClanObject.setType(REFUND);
                    list.remove(cMGSiegeClanObject);
                    this.addObject(REFUND, cMGSiegeClanObject);
                    SiegeClanDAO.getInstance().update((Residence)this.getResidence(), cMGSiegeClanObject);
                }
            }
            list.clear();
            this.broadcastTo(SystemMsg.THIS_CLAN_HALL_WAR_HAS_BEEN_CANCELLED, "attackers");
            this.broadcastInZone2(new L2GameServerPacket[]{new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW).addResidenceName((Residence)this.getResidence())});
            this.reCalcNextTime(false);
            return;
        }
        CMGSiegeClanObject[] cMGSiegeClanObjectArray = (CMGSiegeClanObject[])list.toArray(new CMGSiegeClanObject[list.size()]);
        Arrays.sort(cMGSiegeClanObjectArray, SiegeClanObject.SiegeClanComparatorImpl.getInstance());
        ArrayList<CMGSiegeClanObject> arrayList = new ArrayList<CMGSiegeClanObject>(4);
        for (int i = 0; i < cMGSiegeClanObjectArray.length; ++i) {
            CMGSiegeClanObject cMGSiegeClanObject = cMGSiegeClanObjectArray[i];
            SiegeClanDAO.getInstance().delete((Residence)this.getResidence(), cMGSiegeClanObject);
            if (arrayList.size() == 4) {
                list.remove(cMGSiegeClanObject);
                cMGSiegeClanObject.broadcast(SystemMsg.YOU_HAVE_FAILED_IN_YOUR_ATTEMPT_TO_REGISTER_FOR_THE_CLAN_HALL_WAR);
                continue;
            }
            arrayList.add(cMGSiegeClanObject);
            cMGSiegeClanObject.broadcast(SystemMsg.YOU_HAVE_BEEN_REGISTERED_FOR_A_CLAN_HALL_WAR);
        }
        this.QN = false;
        super.startEvent();
    }

    @Override
    public void stopEvent(boolean bl) {
        this.removeBanishItems();
        Clan clan = ((ClanHall)this.getResidence()).getOwner();
        if (clan != null) {
            if (this._oldOwner != clan) {
                clan.broadcastToOnlineMembers(PlaySound.SIEGE_VICTORY);
                clan.incReputation(1700, false, this.toString());
            }
            this.broadcastTo((L2GameServerPacket)((SystemMessage2)new SystemMessage2(SystemMsg.S1_CLAN_HAS_DEFEATED_S2).addString(clan.getName())).addResidenceName((Residence)this.getResidence()), "attackers", "defenders");
            this.broadcastTo((L2GameServerPacket)new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_IS_FINISHED).addResidenceName((Residence)this.getResidence()), "attackers", "defenders");
        } else {
            this.broadcastTo((L2GameServerPacket)new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW).addResidenceName((Residence)this.getResidence()), "attackers");
        }
        this.updateParticles(false, "attackers");
        this.removeObjects("attackers");
        super.stopEvent(bl);
        this._oldOwner = null;
    }

    public void nextStep() {
        List list = this.getObjects("attackers");
        for (int i = 0; i < list.size(); ++i) {
            this.spawnAction("arena_" + i, true);
        }
        this.QN = true;
        this.updateParticles(true, "attackers");
        this.broadcastTo((L2GameServerPacket)new SystemMessage2(SystemMsg.THE_SIEGE_TO_CONQUER_S1_HAS_BEGUN).addResidenceName((Residence)this.getResidence()), "attackers");
    }

    @Override
    public void setRegistrationOver(boolean bl) {
        if (bl) {
            this.broadcastTo(SystemMsg.THE_REGISTRATION_PERIOD_FOR_A_CLAN_HALL_WAR_HAS_ENDED, "attackers");
        }
        super.setRegistrationOver(bl);
    }

    @Override
    public CMGSiegeClanObject newSiegeClan(String string, int n, long l, long l2) {
        Clan clan = ClanTable.getInstance().getClan(n);
        return clan == null ? null : new CMGSiegeClanObject(string, clan, l, l2);
    }

    @Override
    public void announce(int n) {
        int n2 = n % 60;
        int n3 = n / 60;
        if (n3 > 0) {
            SystemMsg systemMsg = n3 > 10 ? SystemMsg.IN_S1_MINUTES_THE_GAME_WILL_BEGIN_ALL_PLAYERS_MUST_HURRY_AND_MOVE_TO_THE_LEFT_SIDE_OF_THE_CLAN_HALLS_ARENA : SystemMsg.IN_S1_MINUTES_THE_GAME_WILL_BEGIN_ALL_PLAYERS_PLEASE_ENTER_THE_ARENA_NOW;
            this.broadcastTo((L2GameServerPacket)new SystemMessage2(systemMsg).addInteger(n3), "attackers");
        } else {
            this.broadcastTo((L2GameServerPacket)new SystemMessage2(SystemMsg.IN_S1_SECONDS_THE_GAME_WILL_BEGIN).addInteger(n2), "attackers");
        }
    }

    @Override
    public void processStep(Clan clan) {
        if (clan != null) {
            ((ClanHall)this.getResidence()).changeOwner(clan);
        }
        this.stopEvent(true);
    }

    @Override
    public void loadSiegeClans() {
        this.addObjects("attackers", SiegeClanDAO.getInstance().load((Residence)this.getResidence(), "attackers"));
        this.addObjects(REFUND, SiegeClanDAO.getInstance().load((Residence)this.getResidence(), REFUND));
    }

    @Override
    public void action(String string, boolean bl) {
        if (string.equalsIgnoreCase(NEXT_STEP)) {
            this.nextStep();
        } else {
            super.action(string, bl);
        }
    }

    @Override
    public int getUserRelation(Player player, int n) {
        return n;
    }

    @Override
    public int getRelation(Player player, Player player2, int n) {
        return n;
    }

    public boolean isArenaClosed() {
        return this.QN;
    }

    @Override
    public void onAddEvent(GameObject gameObject) {
        if (gameObject.isItem()) {
            this.addBanishItem((ItemInstance)gameObject);
        }
    }
}

