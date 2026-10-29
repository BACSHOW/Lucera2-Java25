/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.model.entity.events.impl;

import l2.commons.collections.MultiValueSet;
import l2.gameserver.dao.SiegeClanDAO;
import l2.gameserver.model.Creature;
import l2.gameserver.model.Player;
import l2.gameserver.model.Zone;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.L2GameServerPacket;
import l2.gameserver.network.l2.s2c.PlaySound;
import l2.gameserver.network.l2.s2c.SystemMessage2;

public class ClanHallSiegeEvent
extends SiegeEvent<ClanHall, SiegeClanObject> {
    public static final String BOSS = "boss";

    public ClanHallSiegeEvent(MultiValueSet<String> multiValueSet) {
        super(multiValueSet);
    }

    @Override
    public void startEvent() {
        this._oldOwner = ((ClanHall)this.getResidence()).getOwner();
        if (this._oldOwner != null) {
            ((ClanHall)this.getResidence()).changeOwner(null);
            this.addObject("attackers", new SiegeClanObject("attackers", this._oldOwner, 0L));
        }
        if (this.getObjects("attackers").size() == 0) {
            this.broadcastInZone2(new L2GameServerPacket[]{new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST).addResidenceName((Residence)this.getResidence())});
            this.reCalcNextTime(false);
            return;
        }
        SiegeClanDAO.getInstance().delete((Residence)this.getResidence());
        this.updateParticles(true, "attackers");
        this.broadcastTo((L2GameServerPacket)new SystemMessage2(SystemMsg.THE_SIEGE_TO_CONQUER_S1_HAS_BEGUN).addResidenceName((Residence)this.getResidence()), "attackers");
        super.startEvent();
    }

    @Override
    public void stopEvent(boolean bl) {
        Clan clan = ((ClanHall)this.getResidence()).getOwner();
        if (clan != null) {
            clan.broadcastToOnlineMembers(PlaySound.SIEGE_VICTORY);
            clan.incReputation(1700, false, this.toString());
            this.broadcastTo((L2GameServerPacket)((SystemMessage2)new SystemMessage2(SystemMsg.S1_CLAN_HAS_DEFEATED_S2).addString(clan.getName())).addResidenceName((Residence)this.getResidence()), "attackers");
            this.broadcastTo((L2GameServerPacket)new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_IS_FINISHED).addResidenceName((Residence)this.getResidence()), "attackers");
        } else {
            this.broadcastTo((L2GameServerPacket)new SystemMessage2(SystemMsg.THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW).addResidenceName((Residence)this.getResidence()), "attackers");
        }
        this.updateParticles(false, "attackers");
        this.removeObjects("attackers");
        super.stopEvent(bl);
        this._oldOwner = null;
    }

    @Override
    public void setRegistrationOver(boolean bl) {
        if (bl) {
            this.broadcastTo((L2GameServerPacket)new SystemMessage2(SystemMsg.THE_DEADLINE_TO_REGISTER_FOR_THE_SIEGE_OF_S1_HAS_PASSED).addResidenceName((Residence)this.getResidence()), "attackers");
        }
        super.setRegistrationOver(bl);
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
    }

    @Override
    public int getUserRelation(Player player, int n) {
        return n;
    }

    @Override
    public int getRelation(Player player, Player player2, int n) {
        return n;
    }

    @Override
    public boolean canResurrect(Player player, Creature creature, boolean bl) {
        boolean bl2 = player.isInZone(Zone.ZoneType.SIEGE);
        boolean bl3 = creature.isInZone(Zone.ZoneType.SIEGE);
        if (!bl2 && !bl3) {
            return true;
        }
        if (!bl3) {
            return false;
        }
        Player player2 = creature.getPlayer();
        ClanHallSiegeEvent clanHallSiegeEvent = creature.getEvent(ClanHallSiegeEvent.class);
        if (clanHallSiegeEvent != this) {
            if (bl) {
                player2.sendPacket((IStaticPacket)SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE);
            }
            player.sendPacket((IStaticPacket)(bl ? SystemMsg.IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEFIELDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE : SystemMsg.INVALID_TARGET));
            return false;
        }
        Object s = clanHallSiegeEvent.getSiegeClan("attackers", player2.getClan());
        if (((SiegeClanObject)s).getFlag() == null) {
            if (bl) {
                player2.sendPacket((IStaticPacket)SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE);
            }
            player.sendPacket((IStaticPacket)(bl ? SystemMsg.IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE : SystemMsg.INVALID_TARGET));
            return false;
        }
        if (bl) {
            return true;
        }
        player.sendPacket((IStaticPacket)SystemMsg.INVALID_TARGET);
        return false;
    }
}

