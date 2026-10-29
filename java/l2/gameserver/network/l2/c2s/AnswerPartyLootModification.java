/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class AnswerPartyLootModification
extends L2GameClientPacket {
    public int _answer;

    @Override
    protected void readImpl() {
        this._answer = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        Party party = player.getParty();
        if (party != null) {
            party.answerLootChangeRequest(player, this._answer == 1);
        }
    }
}

