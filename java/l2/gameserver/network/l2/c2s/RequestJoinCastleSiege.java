/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import java.io.Serializable;
import l2.gameserver.Config;
import l2.gameserver.dao.SiegeClanDAO;
import l2.gameserver.data.xml.holder.ResidenceHolder;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2.gameserver.model.entity.events.impl.ClanHallSiegeEvent;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.entity.events.objects.SiegeClanObject;
import l2.gameserver.model.entity.residence.Castle;
import l2.gameserver.model.entity.residence.ClanHall;
import l2.gameserver.model.entity.residence.Residence;
import l2.gameserver.model.entity.residence.ResidenceType;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.Privilege;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.CastleSiegeAttackerList;
import l2.gameserver.network.l2.s2c.CastleSiegeDefenderList;

public class RequestJoinCastleSiege
extends L2GameClientPacket {
    private int _id;
    private boolean aca;
    private boolean acb;

    @Override
    protected void readImpl() {
        this._id = this.readD();
        this.aca = this.readD() == 1;
        this.acb = this.readD() == 1;
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (!player.hasPrivilege(Privilege.CS_FS_SIEGE_WAR)) {
            player.sendPacket((IStaticPacket)SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }
        Object r = ResidenceHolder.getInstance().getResidence(this._id);
        if (((Residence)r).getType() == ResidenceType.Castle) {
            RequestJoinCastleSiege.a(player, (Castle)r, this.aca, this.acb);
        } else if (((Residence)r).getType() == ResidenceType.ClanHall && this.aca) {
            RequestJoinCastleSiege.a(player, (ClanHall)r, this.acb);
        }
    }

    private static void a(Player player, Castle castle, boolean bl, boolean bl2) {
        CastleSiegeEvent castleSiegeEvent = (CastleSiegeEvent)castle.getSiegeEvent();
        Clan clan = player.getClan();
        if (player.getClan().isPlacedForDisband()) {
            player.sendPacket((IStaticPacket)SystemMsg.DISPERSION_HAS_ALREADY_BEEN_REQUESTED);
            return;
        }
        SiegeClanObject siegeClanObject = null;
        if (bl) {
            siegeClanObject = (SiegeClanObject)castleSiegeEvent.getSiegeClan("attackers", clan);
        } else {
            siegeClanObject = (SiegeClanObject)castleSiegeEvent.getSiegeClan("defenders", clan);
            if (siegeClanObject == null) {
                siegeClanObject = (SiegeClanObject)castleSiegeEvent.getSiegeClan("defenders_waiting", clan);
            }
        }
        if (bl2) {
            Residence clanArray = null;
            for (Residence clanArray2 : ResidenceHolder.getInstance().getResidenceList(Castle.class)) {
                Object s = ((SiegeEvent)clanArray2.getSiegeEvent()).getSiegeClan("attackers", clan);
                if (s == null) {
                    s = ((SiegeEvent)clanArray2.getSiegeEvent()).getSiegeClan("defenders", clan);
                }
                if (s == null) {
                    s = ((SiegeEvent)clanArray2.getSiegeEvent()).getSiegeClan("defenders_waiting", clan);
                }
                if (s == null) continue;
                clanArray = clanArray2;
            }
            if (bl) {
                if (castle.getOwnerId() == clan.getClanId()) {
                    player.sendPacket((IStaticPacket)SystemMsg.CASTLE_OWNING_CLANS_ARE_AUTOMATICALLY_REGISTERED_ON_THE_DEFENDING_SIDE);
                    return;
                }
                Alliance alliance = clan.getAlliance();
                if (alliance != null) {
                    for (Clan clan2 : alliance.getMembers()) {
                        if (clan2.getCastle() != castle.getId()) continue;
                        player.sendPacket((IStaticPacket)SystemMsg.YOU_CANNOT_REGISTER_AS_AN_ATTACKER_BECAUSE_YOU_ARE_IN_AN_ALLIANCE_WITH_THE_CASTLE_OWNING_CLAN);
                        return;
                    }
                }
                if (clan.getCastle() > 0) {
                    player.sendPacket((IStaticPacket)SystemMsg.A_CLAN_THAT_OWNS_A_CASTLE_CANNOT_PARTICIPATE_IN_ANOTHER_SIEGE);
                    return;
                }
                if (siegeClanObject != null) {
                    player.sendPacket((IStaticPacket)SystemMsg.YOU_ARE_ALREADY_REGISTERED_TO_THE_ATTACKER_SIDE_AND_MUST_CANCEL_YOUR_REGISTRATION_BEFORE_SUBMITTING_YOUR_REQUEST);
                    return;
                }
                if (clan.getLevel() < Config.MIN_CLAN_LEVEL_FOR_SIEGE_REGISTRATION) {
                    player.sendPacket((IStaticPacket)SystemMsg.ONLY_CLANS_OF_LEVEL_5_OR_HIGHER_MAY_REGISTER_FOR_A_CASTLE_SIEGE);
                    return;
                }
                if (clanArray != null) {
                    player.sendPacket((IStaticPacket)SystemMsg.YOU_HAVE_ALREADY_REQUESTED_A_CASTLE_SIEGE);
                    return;
                }
                if (castleSiegeEvent.isRegistrationOver()) {
                    player.sendPacket((IStaticPacket)SystemMsg.YOU_ARE_TOO_LATE_THE_REGISTRATION_PERIOD_IS_OVER);
                    return;
                }
                if (castle.getSiegeDate().getTimeInMillis() == 0L) {
                    player.sendPacket((IStaticPacket)SystemMsg.THIS_IS_NOT_THE_TIME_FOR_SIEGE_REGISTRATION_AND_SO_REGISTRATION_AND_CANCELLATION_CANNOT_BE_DONE);
                    return;
                }
                int n = castleSiegeEvent.getObjects("attackers").size();
                if (n >= 20) {
                    player.sendPacket((IStaticPacket)SystemMsg.NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_ATTACKER_SIDE);
                    return;
                }
                siegeClanObject = new SiegeClanObject("attackers", clan, 0L);
                castleSiegeEvent.addObject("attackers", siegeClanObject);
                SiegeClanDAO.getInstance().insert(castle, siegeClanObject);
                player.sendPacket((IStaticPacket)new CastleSiegeAttackerList(castle));
            } else {
                if (castle.getOwnerId() == 0) {
                    return;
                }
                if (castle.getOwnerId() == clan.getClanId()) {
                    player.sendPacket((IStaticPacket)SystemMsg.CASTLE_OWNING_CLANS_ARE_AUTOMATICALLY_REGISTERED_ON_THE_DEFENDING_SIDE);
                    return;
                }
                if (clan.getCastle() > 0) {
                    player.sendPacket((IStaticPacket)SystemMsg.A_CLAN_THAT_OWNS_A_CASTLE_CANNOT_PARTICIPATE_IN_ANOTHER_SIEGE);
                    return;
                }
                if (siegeClanObject != null) {
                    player.sendPacket((IStaticPacket)SystemMsg.YOU_HAVE_ALREADY_REGISTERED_TO_THE_DEFENDER_SIDE_AND_MUST_CANCEL_YOUR_REGISTRATION_BEFORE_SUBMITTING_YOUR_REQUEST);
                    return;
                }
                if (clan.getLevel() < Config.MIN_CLAN_LEVEL_FOR_SIEGE_REGISTRATION) {
                    player.sendPacket((IStaticPacket)SystemMsg.ONLY_CLANS_OF_LEVEL_5_OR_HIGHER_MAY_REGISTER_FOR_A_CASTLE_SIEGE);
                    return;
                }
                if (clanArray != null) {
                    player.sendPacket((IStaticPacket)SystemMsg.YOU_HAVE_ALREADY_REQUESTED_A_CASTLE_SIEGE);
                    return;
                }
                if (castle.getSiegeDate().getTimeInMillis() == 0L) {
                    player.sendPacket((IStaticPacket)SystemMsg.THIS_IS_NOT_THE_TIME_FOR_SIEGE_REGISTRATION_AND_SO_REGISTRATION_AND_CANCELLATION_CANNOT_BE_DONE);
                    return;
                }
                if (castleSiegeEvent.isRegistrationOver()) {
                    player.sendPacket((IStaticPacket)SystemMsg.YOU_ARE_TOO_LATE_THE_REGISTRATION_PERIOD_IS_OVER);
                    return;
                }
                siegeClanObject = new SiegeClanObject("defenders_waiting", clan, 0L);
                castleSiegeEvent.addObject("defenders_waiting", siegeClanObject);
                SiegeClanDAO.getInstance().insert(castle, siegeClanObject);
                player.sendPacket((IStaticPacket)new CastleSiegeDefenderList(castle));
            }
        } else {
            if (siegeClanObject == null) {
                siegeClanObject = (SiegeClanObject)castleSiegeEvent.getSiegeClan("defenders_refused", clan);
            }
            if (siegeClanObject == null) {
                player.sendPacket((IStaticPacket)SystemMsg.YOU_ARE_NOT_YET_REGISTERED_FOR_THE_CASTLE_SIEGE);
                return;
            }
            if (castleSiegeEvent.isRegistrationOver()) {
                player.sendPacket((IStaticPacket)SystemMsg.YOU_ARE_TOO_LATE_THE_REGISTRATION_PERIOD_IS_OVER);
                return;
            }
            castleSiegeEvent.removeObject(siegeClanObject.getType(), siegeClanObject);
            SiegeClanDAO.getInstance().delete(castle, siegeClanObject);
            if (siegeClanObject.getType() == "attackers") {
                player.sendPacket((IStaticPacket)new CastleSiegeAttackerList(castle));
            } else {
                player.sendPacket((IStaticPacket)new CastleSiegeDefenderList(castle));
            }
        }
    }

    private static void a(Player player, ClanHall clanHall, boolean bl) {
        ClanHallSiegeEvent clanHallSiegeEvent = (ClanHallSiegeEvent)clanHall.getSiegeEvent();
        Clan clan = player.getClan();
        Object object = clanHallSiegeEvent.getSiegeClan("attackers", clan);
        if (bl) {
            if (clan.getHasHideout() > 0) {
                player.sendPacket((IStaticPacket)SystemMsg.A_CLAN_THAT_OWNS_A_CLAN_HALL_MAY_NOT_PARTICIPATE_IN_A_CLAN_HALL_SIEGE);
                return;
            }
            if (object != null) {
                player.sendPacket((IStaticPacket)SystemMsg.YOU_ARE_ALREADY_REGISTERED_TO_THE_ATTACKER_SIDE_AND_MUST_CANCEL_YOUR_REGISTRATION_BEFORE_SUBMITTING_YOUR_REQUEST);
                return;
            }
            if (clan.getLevel() < 4) {
                player.sendPacket((IStaticPacket)SystemMsg.ONLY_CLANS_WHO_ARE_LEVEL_4_OR_ABOVE_CAN_REGISTER_FOR_BATTLE_AT_DEVASTATED_CASTLE_AND_FORTRESS_OF_THE_DEAD);
                return;
            }
            if (clanHallSiegeEvent.isRegistrationOver()) {
                player.sendPacket((IStaticPacket)SystemMsg.YOU_ARE_TOO_LATE_THE_REGISTRATION_PERIOD_IS_OVER);
                return;
            }
            int n = clanHallSiegeEvent.getObjects("attackers").size();
            if (n >= 20) {
                player.sendPacket((IStaticPacket)SystemMsg.NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_ATTACKER_SIDE);
                return;
            }
            object = new SiegeClanObject("attackers", clan, 0L);
            clanHallSiegeEvent.addObject("attackers", (Serializable)object);
            SiegeClanDAO.getInstance().insert(clanHall, (SiegeClanObject)object);
        } else {
            if (object == null) {
                player.sendPacket((IStaticPacket)SystemMsg.YOU_ARE_NOT_YET_REGISTERED_FOR_THE_CASTLE_SIEGE);
                return;
            }
            if (clanHallSiegeEvent.isRegistrationOver()) {
                player.sendPacket((IStaticPacket)SystemMsg.YOU_ARE_TOO_LATE_THE_REGISTRATION_PERIOD_IS_OVER);
                return;
            }
            clanHallSiegeEvent.removeObject(((SiegeClanObject)object).getType(), (Serializable)object);
            SiegeClanDAO.getInstance().delete(clanHall, (SiegeClanObject)object);
        }
        player.sendPacket((IStaticPacket)new CastleSiegeAttackerList(clanHall));
    }
}

