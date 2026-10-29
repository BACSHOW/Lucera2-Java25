/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.pledge.Alliance;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.SystemMessage;
import l2.gameserver.tables.ClanTable;

public class RequestOustAlly
extends L2GameClientPacket {
    private String aci;

    @Override
    protected void readImpl() {
        this.aci = this.readS(32);
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        Clan clan = player.getClan();
        if (clan == null) {
            player.sendActionFailed();
            return;
        }
        Alliance alliance = clan.getAlliance();
        if (alliance == null) {
            player.sendPacket((IStaticPacket)Msg.YOU_ARE_NOT_CURRENTLY_ALLIED_WITH_ANY_CLANS);
            return;
        }
        if (!player.isAllyLeader()) {
            player.sendPacket((IStaticPacket)Msg.FEATURE_AVAILABLE_TO_ALLIANCE_LEADERS_ONLY);
            return;
        }
        if (this.aci == null) {
            return;
        }
        Clan clan2 = ClanTable.getInstance().getClanByName(this.aci);
        if (clan2 != null) {
            if (!alliance.isMember(clan2.getClanId())) {
                player.sendActionFailed();
                return;
            }
            if (alliance.getLeader().equals(clan2)) {
                player.sendPacket((IStaticPacket)Msg.YOU_HAVE_FAILED_TO_WITHDRAW_FROM_THE_ALLIANCE);
                return;
            }
            clan2.broadcastToOnlineMembers(new SystemMessage("Your clan has been expelled from " + alliance.getAllyName() + " alliance."), new SystemMessage(468));
            clan2.setAllyId(0);
            clan2.setLeavedAlly();
            alliance.broadcastAllyStatus();
            alliance.removeAllyMember(clan2.getClanId());
            alliance.setExpelledMember();
            player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestOustAlly.ClanDismissed", player, new Object[0]).addString(clan2.getName()).addString(alliance.getAllyName()));
        }
    }
}

