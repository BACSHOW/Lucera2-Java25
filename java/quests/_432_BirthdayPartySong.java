/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _432_BirthdayPartySong
extends Quest
implements ScriptFile {
    private static int bHI = 31043;
    private static int bHJ = 21103;
    private static int bHK = 7541;
    private static int bHL = 7061;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _432_BirthdayPartySong() {
        super(0);
        this.addStartNpc(bHI);
        this.addKillId(bHJ);
        this.addQuestItem(bHK);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("muzyko_q0432_0104.htm")) {
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("muzyko_q0432_0201.htm")) {
            if (questState.getQuestItemsCount(bHK) == 50L) {
                questState.takeItems(bHK, -1L);
                questState.giveItems(bHL, 25L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            } else {
                string2 = "muzyko_q0432_0202.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getCond();
        int n2 = npcInstance.getNpcId();
        if (n2 == bHI) {
            if (n == 0) {
                if (questState.getPlayer().getLevel() >= 31) {
                    string = "muzyko_q0432_0101.htm";
                } else {
                    string = "muzyko_q0432_0103.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n == 1) {
                string = "muzyko_q0432_0106.htm";
            } else if (n == 2 && questState.getQuestItemsCount(bHK) == 50L) {
                string = "muzyko_q0432_0105.htm";
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
        if (n == bHJ && questState.getCond() == 1 && questState.getQuestItemsCount(bHK) < 50L) {
            questState.giveItems(bHK, 1L);
            if (questState.getQuestItemsCount(bHK) == 50L) {
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(2);
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

