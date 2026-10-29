/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
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

public class RequestOustPledgeMember
extends L2GameClientPacket {
    private String abZ;

    @Override
    protected void readImpl() {
        this.abZ = this.readS(Config.CNAME_MAXLEN);
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null || (player.getClanPrivileges() & 0x40) != 64) {
            return;
        }
        Clan clan = player.getClan();
        UnitMember unitMember = clan.getAnyMember(this.abZ);
        if (unitMember == null) {
            player.sendPacket((IStaticPacket)SystemMsg.THE_TARGET_MUST_BE_A_CLAN_MEMBER);
            return;
        }
        SubUnit subUnit = clan.getSubUnit(0);
        Player player2 = unitMember.getPlayer();
        if (unitMember.isOnline() && unitMember.getPlayer().isInCombat()) {
            player.sendPacket((IStaticPacket)SystemMsg.A_CLAN_MEMBER_MAY_NOT_BE_DISMISSED_DURING_COMBAT);
            return;
        }
        if (unitMember.isClanLeader() || subUnit.getNextLeaderObjectId() == unitMember.getObjectId()) {
            player.sendPacket((IStaticPacket)SystemMsg.A_CLAN_LEADER_CANNOT_WITHDRAW_FROM_THEIR_OWN_CLAN);
            return;
        }
        int n = unitMember.getPledgeType();
        clan.removeClanMember(n, unitMember.getObjectId());
        clan.broadcastToOnlineMembers(new SystemMessage(191).addString(this.abZ), new PledgeShowMemberListDelete(this.abZ));
        if (n != -1) {
            clan.setExpelledMember();
        }
        if (player2 == null) {
            return;
        }
        player2.removeEventsByClass(SiegeEvent.class);
        if (n == -1) {
            player2.setLvlJoinedAcademy(0);
        }
        player2.setClan(null);
        if (!player2.isNoble()) {
            player2.setTitle("");
        }
        player2.setLeaveClanCurTime();
        player2.broadcastCharInfo();
        player2.broadcastRelationChanged();
        player2.store(true);
        player2.sendPacket(Msg.YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN_YOU_ARE_NOT_ALLOWED_TO_JOIN_ANOTHER_CLAN_FOR_24_HOURS, PledgeShowMemberListDeleteAll.STATIC);
    }
}

