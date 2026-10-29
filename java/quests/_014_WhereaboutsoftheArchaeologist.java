/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _014_WhereaboutsoftheArchaeologist
extends Quest
implements ScriptFile {
    private static final int azP = 7253;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _014_WhereaboutsoftheArchaeologist() {
        super(0);
        this.addStartNpc(31263);
        this.addTalkId(31538);
        this.addQuestItem(7253);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("trader_liesel_q0014_0104.htm")) {
            questState.setCond(1);
            questState.giveItems(7253, 1L);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("explorer_ghost_a_q0014_0201.htm") && questState.getQuestItemsCount(7253) > 0L) {
            questState.takeItems(7253, -1L);
            questState.giveItems(57, 113228L);
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(false);
            return "explorer_ghost_a_q0014_0201.htm";
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 31263) {
            if (n2 == 0) {
                if (questState.getPlayer().getLevel() >= 74) {
                    string = "trader_liesel_q0014_0101.htm";
                } else {
                    string = "trader_liesel_q0014_0103.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 1) {
                string = "trader_liesel_q0014_0104.htm";
            }
        } else if (n == 31538 && n2 == 1 && questState.getQuestItemsCount(7253) == 1L) {
            string = "explorer_ghost_a_q0014_0101.htm";
        }
        return string;
    }
}

