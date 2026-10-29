/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.entity.DimensionalRift;
import l2.gameserver.model.entity.Reflection;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.CustomMessage;

public class RequestWithDrawalParty
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
        Party party = player.getParty();
        if (party == null) {
            player.sendActionFailed();
            return;
        }
        if (player.isOlyParticipant()) {
            player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestWithDrawalParty.CantOustNow", player, new Object[0]));
            return;
        }
        Reflection reflection = player.getParty().getReflection();
        if (reflection != null && reflection instanceof DimensionalRift && player.getReflection().equals(reflection)) {
            player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestWithDrawalParty.Rift", player, new Object[0]));
        } else if (reflection != null && player.isInCombat()) {
            player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestWithDrawalParty.CantOustNow", player, new Object[0]));
        } else {
            player.leaveParty();
        }
    }
}

