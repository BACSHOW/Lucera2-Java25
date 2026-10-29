/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _431_WeddingMarch
extends Quest
implements ScriptFile {
    private static int bHF = 31042;
    private static int bHG = 7540;
    private static int bHH = 7062;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _431_WeddingMarch() {
        super(0);
        this.addStartNpc(bHF);
        this.addKillId(20786);
        this.addKillId(20787);
        this.addQuestItem(bHG);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            string2 = "muzyk_q0431_0104.htm";
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("431_3")) {
            if (questState.getQuestItemsCount(bHG) == 50L) {
                string2 = "muzyk_q0431_0201.htm";
                questState.takeItems(bHG, -1L);
                questState.giveItems(bHH, 25L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            } else {
                string2 = "muzyk_q0431_0202.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getCond();
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        if (n2 == bHF) {
            if (n3 != 2) {
                if (questState.getPlayer().getLevel() < 38) {
                    string = "muzyk_q0431_0103.htm";
                    questState.exitCurrentQuest(true);
                } else {
                    string = "muzyk_q0431_0101.htm";
                }
            } else if (n == 1) {
                string = "muzyk_q0431_0106.htm";
            } else if (n == 2 && questState.getQuestItemsCount(bHG) == 50L) {
                string = "muzyk_q0431_0105.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getState() != 2) {
            return null;
        }
        int n = npcInstance.getNpcId();
        if ((n == 20786 || n == 20787) && questState.getCond() == 1 && questState.getQuestItemsCount(bHG) < 50L) {
            questState.giveItems(bHG, 1L);
            if (questState.getQuestItemsCount(bHG) == 50L) {
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(2);
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

