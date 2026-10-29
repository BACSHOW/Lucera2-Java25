/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.pledge.Clan;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.AskJoinPledge;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class RequestJoinPledge
extends L2GameClientPacket {
    public static final String PLEDGE_TYPE_VAR_NAME = "pledgeType";
    private int CS;
    private int Ic;

    @Override
    protected void readImpl() {
        this.CS = this.readD();
        this.Ic = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null || player.getClan() == null) {
            return;
        }
        if (player.isOutOfControl()) {
            player.sendActionFailed();
            return;
        }
        if (player.isProcessingRequest()) {
            player.sendPacket((IStaticPacket)Msg.WAITING_FOR_ANOTHER_REPLY);
            return;
        }
        Clan clan = player.getClan();
        if (clan.isPlacedForDisband()) {
            player.sendPacket((IStaticPacket)SystemMsg.DISPERSION_HAS_ALREADY_BEEN_REQUESTED);
            return;
        }
        if (!clan.canInvite()) {
            player.sendPacket((IStaticPacket)Msg.AFTER_A_CLAN_MEMBER_IS_DISMISSED_FROM_A_CLAN_THE_CLAN_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_ACCEPTING_A_NEW_MEMBER);
            return;
        }
        if (this.CS == player.getObjectId()) {
            player.sendPacket((IStaticPacket)Msg.YOU_CANNOT_ASK_YOURSELF_TO_APPLY_TO_A_CLAN);
            return;
        }
        if ((player.getClanPrivileges() & 2) != 2) {
            player.sendPacket((IStaticPacket)Msg.ONLY_THE_LEADER_CAN_GIVE_OUT_INVITATIONS);
            return;
        }
        GameObject gameObject = player.getVisibleObject(this.CS);
        if (gameObject == null || !gameObject.isPlayer()) {
            player.sendPacket((IStaticPacket)SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return;
        }
        Player player2 = (Player)gameObject;
        if (player2.getClan() == player.getClan()) {
            player.sendPacket((IStaticPacket)SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return;
        }
        if (!player2.getPlayerAccess().CanJoinClan) {
            player.sendPacket((IStaticPacket)new SystemMessage(760).addName(player2));
            return;
        }
        if (player2.getClan() != null) {
            player.sendPacket((IStaticPacket)new SystemMessage(10).addName(player2));
            return;
        }
        if (player2.isBusy()) {
            player.sendPacket((IStaticPacket)new SystemMessage(153).addName(player2));
            return;
        }
        if (this.Ic == -1 && (player2.getLevel() > 40 || player2.getClassId().getLevel() > 2)) {
            player.sendPacket((IStaticPacket)Msg.TO_JOIN_A_CLAN_ACADEMY_CHARACTERS_MUST_BE_LEVEL_40_OR_BELOW_NOT_BELONG_ANOTHER_CLAN_AND_NOT_YET_COMPLETED_THEIR_2ND_CLASS_TRANSFER);
            return;
        }
        if (clan.getUnitMembersSize(this.Ic) >= clan.getSubPledgeLimit(this.Ic)) {
            if (this.Ic == 0) {
                player.sendPacket((IStaticPacket)new SystemMessage(1835).addString(clan.getName()));
            } else {
                player.sendPacket((IStaticPacket)Msg.THE_ACADEMY_ROYAL_GUARD_ORDER_OF_KNIGHTS_IS_FULL_AND_CANNOT_ACCEPT_NEW_MEMBERS_AT_THIS_TIME);
            }
            return;
        }
        Request request = new Request(Request.L2RequestType.CLAN, player, player2).setTimeout(10000L);
        request.set(PLEDGE_TYPE_VAR_NAME, this.Ic);
        player2.sendPacket((IStaticPacket)new AskJoinPledge(player.getObjectId(), player.getClan().getName()));
    }
}

