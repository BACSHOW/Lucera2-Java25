/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _303_CollectArrowheads
extends Quest
implements ScriptFile {
    int ORCISH_ARROWHEAD = 963;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _303_CollectArrowheads() {
        super(0);
        this.addStartNpc(30029);
        this.addTalkId(30029);
        this.addKillId(20361);
        this.addQuestItem(this.ORCISH_ARROWHEAD);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("minx_q0303_04.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getCond();
        if (n == 0) {
            if (questState.getPlayer().getLevel() >= 10) {
                string = "minx_q0303_03.htm";
            } else {
                string = "minx_q0303_02.htm";
                questState.exitCurrentQuest(true);
            }
        } else if (questState.getQuestItemsCount(this.ORCISH_ARROWHEAD) < 10L) {
            string = "minx_q0303_05.htm";
        } else {
            questState.takeItems(this.ORCISH_ARROWHEAD, -1L);
            questState.giveItems(57, 1000L);
            questState.addExpAndSp(2000L, 0L);
            string = "minx_q0303_06.htm";
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(false);
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getQuestItemsCount(this.ORCISH_ARROWHEAD) < 10L) {
            questState.giveItems(this.ORCISH_ARROWHEAD, 1L);
            if (questState.getQuestItemsCount(this.ORCISH_ARROWHEAD) == 10L) {
                questState.setCond(2);
                questState.playSound("ItemSound.quest_middle");
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

