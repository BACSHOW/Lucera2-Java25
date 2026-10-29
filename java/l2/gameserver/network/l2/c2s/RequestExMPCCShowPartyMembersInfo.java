/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExMPCCShowPartyMemberInfo;

public class RequestExMPCCShowPartyMembersInfo
extends L2GameClientPacket {
    private int CS;

    @Override
    protected void readImpl() {
        this.CS = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null || !player.isInParty() || !player.getParty().isInCommandChannel()) {
            return;
        }
        for (Party party : player.getParty().getCommandChannel().getParties()) {
            Player player2 = party.getPartyLeader();
            if (player2 == null || player2.getObjectId() != this.CS) continue;
            player.sendPacket((IStaticPacket)new ExMPCCShowPartyMemberInfo(party));
            break;
        }
    }
}

