/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.Config;
import l2.gameserver.model.Player;
import l2.gameserver.model.Request;
import l2.gameserver.model.World;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;
import l2.gameserver.network.l2.s2c.AskJoinParty;
import l2.gameserver.network.l2.s2c.SystemMessage2;

public class RequestJoinParty
extends L2GameClientPacket {
    private String _name;
    private int Gq;

    @Override
    protected void readImpl() {
        this._name = this.readS(Config.CNAME_MAXLEN);
        this.Gq = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        if (player.isOutOfControl()) {
            player.sendActionFailed();
            return;
        }
        if (player.isProcessingRequest()) {
            player.sendPacket((IStaticPacket)SystemMsg.WAITING_FOR_ANOTHER_REPLY);
            return;
        }
        Player player2 = World.getPlayer(this._name);
        if (player2 == null) {
            player.sendPacket((IStaticPacket)SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
            return;
        }
        if (player2 == player) {
            player.sendPacket((IStaticPacket)SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            player.sendActionFailed();
            return;
        }
        if (player2.isBusy()) {
            player.sendPacket((IStaticPacket)new SystemMessage2(SystemMsg.C1_IS_ON_ANOTHER_TASK).addName(player2));
            return;
        }
        if (player2.getVarB("blockparty@")) {
            player.sendMessage(new CustomMessage("voicecommands.party_invite_blocked", player, new Object[0]).addCharName(player2));
            return;
        }
        IStaticPacket iStaticPacket = player2.canJoinParty(player);
        if (iStaticPacket != null) {
            player.sendPacket(iStaticPacket);
            return;
        }
        if (player.isInParty()) {
            if (player.getParty().getMemberCount() >= Config.ALT_MAX_PARTY_SIZE) {
                player.sendPacket((IStaticPacket)SystemMsg.THE_PARTY_IS_FULL);
                return;
            }
            if (Config.PARTY_LEADER_ONLY_CAN_INVITE && !player.getParty().isLeader(player)) {
                player.sendPacket((IStaticPacket)SystemMsg.ONLY_THE_LEADER_CAN_GIVE_OUT_INVITATIONS);
                return;
            }
            if (player.getParty().isInDimensionalRift()) {
                player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestJoinParty.InDimensionalRift", player, new Object[0]));
                player.sendActionFailed();
                return;
            }
        }
        new Request(Request.L2RequestType.PARTY, player, player2).setTimeout(10000L).set("itemDistribution", this.Gq);
        player2.sendPacket((IStaticPacket)new AskJoinParty(player.getName(), this.Gq));
        player.sendPacket((IStaticPacket)new SystemMessage2(SystemMsg.C1_HAS_BEEN_INVITED_TO_THE_PARTY).addName(player2));
    }
}

