/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.cache.Msg;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.s2c.ActionFail;
import l2.gameserver.network.l2.s2c.CharacterSelectionInfo;
import l2.gameserver.network.l2.s2c.RestartResponse;

public class RequestRestart
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
        if (player.isInObserverMode()) {
            player.sendPacket(Msg.OBSERVERS_CANNOT_PARTICIPATE, RestartResponse.FAIL, ActionFail.STATIC);
            return;
        }
        if (player.isInCombat()) {
            player.sendPacket(Msg.YOU_CANNOT_RESTART_WHILE_IN_COMBAT, RestartResponse.FAIL, ActionFail.STATIC);
            return;
        }
        if (player.isFishing()) {
            player.sendPacket(Msg.YOU_CANNOT_DO_ANYTHING_ELSE_WHILE_FISHING, RestartResponse.FAIL, ActionFail.STATIC);
            return;
        }
        if (player.isBlocked() && !player.isFlying()) {
            player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestRestart.OutOfControl", player, new Object[0]));
            player.sendPacket(RestartResponse.FAIL, ActionFail.STATIC);
            return;
        }
        if (player.isFestivalParticipant() && SevenSignsFestival.getInstance().isFestivalInitialized()) {
            player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestRestart.Festival", player, new Object[0]));
            player.sendPacket(RestartResponse.FAIL, ActionFail.STATIC);
            return;
        }
        if (this.getClient() != null) {
            ((GameClient)this.getClient()).setState(GameClient.GameClientState.AUTHED);
        }
        player.restart();
        CharacterSelectionInfo characterSelectionInfo = new CharacterSelectionInfo(((GameClient)this.getClient()).getLogin(), ((GameClient)this.getClient()).getSessionKey().playOkID1);
        this.sendPacket(RestartResponse.OK, characterSelectionInfo);
        ((GameClient)this.getClient()).setCharSelection(characterSelectionInfo.getCharInfo());
    }
}

