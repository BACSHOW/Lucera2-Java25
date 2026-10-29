/*
 * Decompiled with CFR 0.152.
 */
package l2.gameserver.network.l2.c2s;

import l2.gameserver.instancemanager.QuestManager;
import l2.gameserver.model.Player;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.GameClient;
import l2.gameserver.network.l2.c2s.L2GameClientPacket;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExQuestNpcLogList;

public class RequestAddExpandQuestAlarm
extends L2GameClientPacket {
    private int _questId;

    @Override
    protected void readImpl() throws Exception {
        this._questId = this.readD();
    }

    @Override
    protected void runImpl() throws Exception {
        Player player = ((GameClient)this.getClient()).getActiveChar();
        if (player == null) {
            return;
        }
        Quest quest = QuestManager.getQuest(this._questId);
        if (quest == null) {
            return;
        }
        QuestState questState = player.getQuestState(quest.getClass());
        if (questState == null) {
            return;
        }
        player.sendPacket((IStaticPacket)new ExQuestNpcLogList(questState));
    }
}

