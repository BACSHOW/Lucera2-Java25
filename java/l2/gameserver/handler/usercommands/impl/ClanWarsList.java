/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.handler.usercommands.impl;

import java.util.Collection;
import java.util.LinkedHashSet;
import l2.gameserver.cache.Msg;
import l2.gameserver.handler.usercommands.IUserCommandHandler;
import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import org.apache.commons.lang3.ArrayUtils;

public class ClanWarsList
implements IUserCommandHandler {
    private static final int zz = 88;
    private static final int zA = 89;
    private static final int zB = 90;
    private static final int[] zy = new int[]{88, 89, 90};

    private static void a(Player player, Clan clan) {
        player.sendPacket((IStaticPacket)Msg._ATTACK_LIST_);
        ClanWarsList.a(player, clan.getEnemyClans());
    }

    private static void b(Player player, Clan clan) {
        player.sendPacket((IStaticPacket)Msg._UNDER_ATTACK_LIST_);
        ClanWarsList.a(player, clan.getAttackerClans());
    }

    private static void c(Player player, Clan clan) {
        LinkedHashSet<Clan> linkedHashSet = new LinkedHashSet<Clan>(clan.getEnemyClans());
        linkedHashSet.retainAll(clan.getAttackerClans());
        player.sendPacket((IStaticPacket)Msg._WAR_LIST_);
        ClanWarsList.a(player, linkedHashSet);
    }

    private static void a(Player player, Collection<Clan> collection) {
        for (Clan clan : collection) {
            Alliance alliance = clan.getAlliance();
            player.sendPacket((IStaticPacket)(alliance != null ? new SystemMessage(1200).addString(clan.getName()).addString(alliance.getAllyName()) : new SystemMessage(1202).addString(clan.getName())));
        }
        player.sendPacket((IStaticPacket)Msg.__EQUALS__);
    }

    @Override
    public boolean useUserCommand(int n, Player player) {
        if (!ArrayUtils.contains(zy, n)) {
            return false;
        }
        Clan clan = player.getClan();
        if (clan == null) {
            player.sendPacket((IStaticPacket)Msg.NOT_JOINED_IN_ANY_CLAN);
            return false;
        }
        switch (n) {
            case 88: {
                ClanWarsList.a(player, clan);
                return true;
            }
            case 89: {
                ClanWarsList.b(player, clan);
                return true;
            }
            case 90: {
                ClanWarsList.c(player, clan);
                return true;
            }
        }
        return false;
    }

    @Override
    public int[] getUserCommandList() {
        return zy;
    }
}

