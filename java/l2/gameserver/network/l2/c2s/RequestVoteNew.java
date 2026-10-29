/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.SystemMessage;

public class RequestVoteNew
extends L2GameClientPacket {
    private int acX;

    @Override
    protected void readImpl() {
        this.acX = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (player.getTarget() == null) {
            player.sendPacket((IStaticPacket)Msg.SELECT_TARGET);
            return;
        }
        if (!player.getTarget().isPlayer() || player.getTarget().getObjectId() != this.acX) {
            player.sendPacket((IStaticPacket)SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return;
        }
        Player player2 = player.getTarget().getPlayer();
        if (player2 == player) {
            player.sendPacket((IStaticPacket)Msg.SELECT_TARGET);
            return;
        }
        if (player.getLevel() < 10) {
            player.sendPacket((IStaticPacket)Msg.ONLY_LEVEL_SUP_10_CAN_RECOMMEND);
            return;
        }
        if (player.getGivableRec() <= 0) {
            player.sendPacket((IStaticPacket)Msg.NO_MORE_RECOMMENDATIONS_TO_HAVE);
            return;
        }
        if (player.isRecommended(player2)) {
            player.sendPacket((IStaticPacket)Msg.THAT_CHARACTER_HAS_ALREADY_BEEN_RECOMMENDED);
            return;
        }
        if (player2.getReceivedRec() >= 255) {
            player.sendPacket((IStaticPacket)Msg.YOU_NO_LONGER_RECIVE_A_RECOMMENDATION);
            return;
        }
        player.giveRecommendation(player2);
        player.sendPacket((IStaticPacket)new SystemMessage(830).addName(player2).addNumber(player.getGivableRec()));
        player2.sendPacket((IStaticPacket)new SystemMessage(831).addName(player));
        player.sendUserInfo(false);
        player2.broadcastUserInfo(true);
    }
}

