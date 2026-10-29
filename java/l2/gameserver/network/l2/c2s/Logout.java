/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Player;
import l2.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.components.SystemMsg;

public class Logout
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
        if (player.isInCombat()) {
            player.sendPacket((IStaticPacket)SystemMsg.YOU_CANNOT_EXIT_THE_GAME_WHILE_IN_COMBAT);
            player.sendActionFailed();
            return;
        }
        if (player.isFishing()) {
            player.sendPacket((IStaticPacket)SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
            player.sendActionFailed();
            return;
        }
        if (player.isBlocked() && !player.isFlying()) {
            player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.Logout.OutOfControl", player, new Object[0]));
            player.sendActionFailed();
            return;
        }
        if (player.isFestivalParticipant() && SevenSignsFestival.getInstance().isFestivalInitialized()) {
            player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.Logout.Festival", player, new Object[0]));
            player.sendActionFailed();
            return;
        }
        if (player.isOlyParticipant()) {
            player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.Logout.Olympiad", player, new Object[0]));
            player.sendActionFailed();
            return;
        }
        if (player.isInObserverMode()) {
            player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.Logout.Observer", player, new Object[0]));
            player.sendActionFailed();
            return;
        }
        player.kick();
    }
}

