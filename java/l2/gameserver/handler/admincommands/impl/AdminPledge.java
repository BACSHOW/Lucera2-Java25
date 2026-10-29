/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.admincommands.impl;

import java.util.StringTokenizer;
import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.handler.admincommands.IAdminCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.VillageMasterInstance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.SubUnit;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.PledgeShowInfoUpdate;
import l2.gameserver.network.l2.s2c.PledgeStatusChanged;
import l2.gameserver.tables.ClanTable;
import l2.gameserver.utils.Util;

public class AdminPledge
implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum enum_, String[] stringArray, String string, Player player) {
        Commands commands = (Commands)enum_;
        if (player.getPlayerAccess() == null || !player.getPlayerAccess().CanEditPledge || player.getTarget() == null || !player.getTarget().isPlayer()) {
            return false;
        }
        Player player2 = (Player)player.getTarget();
        if (string.startsWith("admin_pledge")) {
            StringTokenizer stringTokenizer = new StringTokenizer(string);
            stringTokenizer.nextToken();
            String string2 = stringTokenizer.nextToken();
            if (string2.equals("create")) {
                try {
                    if (player2 == null) {
                        player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
                        return false;
                    }
                    if (player2.getPlayer().getLevel() < 10) {
                        player.sendPacket((IStaticPacket)Msg.YOU_ARE_NOT_QUALIFIED_TO_CREATE_A_CLAN);
                        return false;
                    }
                    String string3 = stringTokenizer.nextToken();
                    if (string3.length() > 16) {
                        player.sendPacket((IStaticPacket)Msg.CLAN_NAMES_LENGTH_IS_INCORRECT);
                        return false;
                    }
                    if (!Util.isMatchingRegexp(string3, Config.CLAN_NAME_TEMPLATE)) {
                        player.sendPacket((IStaticPacket)Msg.CLAN_NAME_IS_INCORRECT);
                        return false;
                    }
                    Clan clan = ClanTable.getInstance().createClan(player2, string3);
                    if (clan != null) {
                        player2.sendPacket(clan.listAll());
                        player2.sendPacket(new PledgeShowInfoUpdate(clan), Msg.CLAN_HAS_BEEN_CREATED);
                        player2.updatePledgeClass();
                        player2.sendUserInfo(true);
                        return true;
                    }
                    player.sendPacket((IStaticPacket)Msg.THIS_NAME_ALREADY_EXISTS);
                    return false;
                }
                catch (Exception exception) {}
            } else if (string2.equals("setlevel")) {
                if (player2.getClan() == null) {
                    player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
                    return false;
                }
                try {
                    int n = Integer.parseInt(stringTokenizer.nextToken());
                    Clan clan = player2.getClan();
                    player.sendMessage("You set level " + n + " for clan " + clan.getName());
                    clan.setLevel(n);
                    clan.updateClanInDB();
                    if (n == 5) {
                        player2.sendPacket((IStaticPacket)Msg.NOW_THAT_YOUR_CLAN_LEVEL_IS_ABOVE_LEVEL_5_IT_CAN_ACCUMULATE_CLAN_REPUTATION_POINTS);
                    }
                    PledgeShowInfoUpdate pledgeShowInfoUpdate = new PledgeShowInfoUpdate(clan);
                    PledgeStatusChanged pledgeStatusChanged = new PledgeStatusChanged(clan);
                    for (Player player3 : clan.getOnlineMembers(0)) {
                        player3.updatePledgeClass();
                        player3.sendPacket(Msg.CLANS_SKILL_LEVEL_HAS_INCREASED, pledgeShowInfoUpdate, pledgeStatusChanged);
                        player3.broadcastUserInfo(true);
                    }
                    return true;
                }
                catch (Exception exception) {}
            } else if (string2.equals("resetcreate")) {
                if (player2.getClan() == null) {
                    player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
                    return false;
                }
                player2.getClan().setExpelledMemberTime(0L);
                player.sendMessage("The penalty for creating a clan has been lifted for " + player2.getName());
            } else if (string2.equals("resetwait")) {
                player2.setLeaveClanTime(0L);
                player.sendMessage("The penalty for leaving a clan has been lifted for " + player2.getName());
            } else if (string2.equals("addrep")) {
                try {
                    int n = Integer.parseInt(stringTokenizer.nextToken());
                    if (player2.getClan() == null || player2.getClan().getLevel() < 5) {
                        player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
                        return false;
                    }
                    player2.getClan().incReputation(n, false, "admin_manual");
                    player.sendMessage("Added " + n + " clan points to clan " + player2.getClan().getName() + ".");
                }
                catch (NumberFormatException numberFormatException) {
                    player.sendMessage("Please specify a number of clan points to add.");
                }
            } else if (string2.equals("setleader")) {
                Clan clan = player2.getClan();
                if (player2.getClan() == null) {
                    player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
                    return false;
                }
                String string4 = null;
                string4 = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : player2.getName();
                SubUnit subUnit = clan.getSubUnit(0);
                UnitMember unitMember = subUnit.getUnitMember(string4);
                if (unitMember == null) {
                    player.sendPacket((IStaticPacket)Msg.INVALID_TARGET);
                    return false;
                }
                VillageMasterInstance.setLeader(player, clan, subUnit, unitMember);
            }
        }
        return false;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private static enum Commands {
        admin_pledge;

    }
}

