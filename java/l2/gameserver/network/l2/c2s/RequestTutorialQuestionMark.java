/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.instancemanager.QuestManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;

public class RequestTutorialQuestionMark
extends L2GameClientPacket {
    int _number = 0;

    @Override
    protected void readImpl() {
        this._number = this.readD();
    }

    @Override
    protected void runImpl() {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        Quest quest = QuestManager.getQuest(255);
        if (quest != null) {
            player.processQuestEvent(quest.getName(), "QM" + this._number, null);
        }
    }
}

