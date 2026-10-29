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

public class RequestOustPartyMember
extends L2GameClientPacket {
    private String _name;

    @Override
    protected void readImpl() {
        this._name = this.readS(16);
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        Party party = player.getParty();
        if (party == null || !player.getParty().isLeader(player)) {
            player.sendActionFailed();
            return;
        }
        if (player.isOlyParticipant()) {
            player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestOustPartyMember.CantOustNow", player, new Object[0]));
            return;
        }
        Player player2 = party.getPlayerByName(this._name);
        if (player2 == player) {
            player.sendActionFailed();
            return;
        }
        if (player2 == null) {
            player.sendActionFailed();
            return;
        }
        Reflection reflection = party.getReflection();
        if (reflection != null && reflection instanceof DimensionalRift && player2.getReflection().equals(reflection)) {
            player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestOustPartyMember.CantOustInRift", player, new Object[0]));
        } else if (reflection != null && !(reflection instanceof DimensionalRift)) {
            player.sendMessage(new CustomMessage("l2p.gameserver.clientpackets.RequestOustPartyMember.CantOustInDungeon", player, new Object[0]));
        } else {
            party.removePartyMember(player2, true);
        }
    }
}

