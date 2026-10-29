/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.cache.Msg;
import l2.gameserver.model.GameObject;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.AskJoinAlliance;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class RequestJoinAlly
extends L2GameClientPacket {
    private int CS;

    @Override
    protected void readImpl() {
        this.CS = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null || player.getClan() == null || player.getAlliance() == null) {
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
        if (player.getAlliance().getMembersCount() >= Config.MAX_ALLY_SIZE) {
            player.sendPacket((IStaticPacket)Msg.YOU_HAVE_FAILED_TO_INVITE_A_CLAN_INTO_THE_ALLIANCE);
            return;
        }
        GameObject gameObject = player.getVisibleObject(this.CS);
        if (gameObject == null || !gameObject.isPlayer() || gameObject == player) {
            player.sendPacket((IStaticPacket)SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return;
        }
        Player player2 = (Player)gameObject;
        if (!player.isAllyLeader()) {
            player.sendPacket((IStaticPacket)Msg.FEATURE_AVAILABLE_TO_ALLIANCE_LEADERS_ONLY);
            return;
        }
        if (player2.getAlliance() != null || player.getAlliance().isMember(player2.getClan().getClanId())) {
            SystemMessage systemMessage = new SystemMessage(691);
            systemMessage.addString(player2.getClan().getName());
            systemMessage.addString(player2.getAlliance().getAllyName());
            player.sendPacket((IStaticPacket)systemMessage);
            return;
        }
        if (!player2.isClanLeader()) {
            player.sendPacket((IStaticPacket)new SystemMessage(9).addString(player2.getName()));
            return;
        }
        if (player.isAtWarWith(player2.getClanId()) > 0) {
            player.sendPacket((IStaticPacket)Msg.YOU_MAY_NOT_ALLY_WITH_A_CLAN_YOU_ARE_AT_BATTLE_WITH);
            return;
        }
        if (!player2.getClan().canJoinAlly()) {
            SystemMessage systemMessage = new SystemMessage(761);
            systemMessage.addString(player2.getClan().getName());
            player.sendPacket((IStaticPacket)systemMessage);
            return;
        }
        if (!player.getAlliance().canInvite()) {
            player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestJoinAlly.InvitePenalty", player, new Object[0]));
            return;
        }
        if (player2.isBusy()) {
            player.sendPacket((IStaticPacket)new SystemMessage(153).addString(player2.getName()));
            return;
        }
        new Request(Request.L2RequestType.ALLY, player, player2).setTimeout(10000L);
        player2.sendPacket((IStaticPacket)new SystemMessage(527).addString(player.getAlliance().getAllyName()).addName(player));
        player2.sendPacket((IStaticPacket)new AskJoinAlliance(player.getObjectId(), player.getName(), player.getAlliance().getAllyName()));
    }
}

