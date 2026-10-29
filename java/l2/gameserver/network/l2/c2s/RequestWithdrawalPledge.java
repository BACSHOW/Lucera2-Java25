/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.events.impl.SiegeEvent;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.model.pledge.SubUnit;
import l2.gameserver.model.pledge.UnitMember;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.PledgeShowMemberListDelete;
import l2.gameserver.network.l2.s2c.PledgeShowMemberListDeleteAll;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class RequestWithdrawalPledge
extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (player.getClanId() == 0) {
            player.sendActionFailed();
            return;
        }
        if (player.isInCombat()) {
            player.sendPacket((IStaticPacket)Msg.ONE_CANNOT_LEAVE_ONES_CLAN_DURING_COMBAT);
            return;
        }
        Clan clan = player.getClan();
        if (clan == null) {
            return;
        }
        UnitMember unitMember = clan.getAnyMember(player.getObjectId());
        if (unitMember == null) {
            player.sendActionFailed();
            return;
        }
        SubUnit subUnit = clan.getSubUnit(0);
        if (unitMember.isClanLeader() || subUnit.getNextLeaderObjectId() == unitMember.getObjectId()) {
            player.sendPacket((IStaticPacket)SystemMsg.A_CLAN_LEADER_CANNOT_WITHDRAW_FROM_THEIR_OWN_CLAN);
            return;
        }
        player.removeEventsByClass(SiegeEvent.class);
        int n = player.getPledgeType();
        clan.removeClanMember(n, player.getObjectId());
        clan.broadcastToOnlineMembers(new SystemMessage(SystemMsg.S1_HAS_WITHDRAWN_FROM_THE_CLAN).addString(player.getName()), new PledgeShowMemberListDelete(player.getName()));
        if (n == -1) {
            player.setLvlJoinedAcademy(0);
        }
        player.setClan(null);
        if (!player.isNoble()) {
            player.setTitle("");
        }
        player.setLeaveClanCurTime();
        player.broadcastCharInfo();
        player.sendPacket(SystemMsg.YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN, PledgeShowMemberListDeleteAll.STATIC);
    }
}

