/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;
import quests._053_LinnaeusSpecialBait;

public class _030_ChestCaughtWithABaitOfFire
extends Quest
implements ScriptFile {
    int Linnaeus = 31577;
    int Rukal = 30629;
    int RedTreasureChest = 6511;
    int RukalsMusicalScore = 7628;
    int NecklaceOfProtection = 916;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _030_ChestCaughtWithABaitOfFire() {
        super(0);
        this.addStartNpc(this.Linnaeus);
        this.addTalkId(this.Rukal);
        this.addQuestItem(this.RukalsMusicalScore);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("fisher_linneaus_q0030_0104.htm")) {
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equals("fisher_linneaus_q0030_0201.htm")) {
            if (questState.getQuestItemsCount(this.RedTreasureChest) > 0L) {
                questState.takeItems(this.RedTreasureChest, 1L);
                questState.giveItems(this.RukalsMusicalScore, 1L);
                questState.setCond(2);
                questState.playSound("ItemSound.quest_middle");
            } else {
                string2 = "fisher_linneaus_q0030_0202.htm";
            }
        } else if (string.equals("bard_rukal_q0030_0301.htm")) {
            if (questState.getQuestItemsCount(this.RukalsMusicalScore) == 1L) {
                questState.takeItems(this.RukalsMusicalScore, -1L);
                questState.giveItems(this.NecklaceOfProtection, 1L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(false);
            } else {
                string2 = "bard_rukal_q0030_0302.htm";
                questState.exitCurrentQuest(true);
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getState();
        n2 = questState.getState();
        int n3 = questState.getCond();
        if (n == this.Linnaeus) {
            if (n2 == 1) {
                if (questState.getPlayer().getLevel() < 60) {
                    string = "fisher_linneaus_q0030_0102.htm";
                    questState.exitCurrentQuest(true);
                } else {
                    QuestState questState2 = questState.getPlayer().getQuestState(_053_LinnaeusSpecialBait.class);
                    if (questState2 != null) {
                        if (questState2.isCompleted()) {
                            string = "fisher_linneaus_q0030_0101.htm";
                        } else {
                            string = "fisher_linneaus_q0030_0102.htm";
                            questState.exitCurrentQuest(true);
                        }
                    } else {
                        string = "fisher_linneaus_q0030_0103.htm";
                        questState.exitCurrentQuest(true);
                    }
                }
            } else if (n3 == 1) {
                string = "fisher_linneaus_q0030_0105.htm";
                if (questState.getQuestItemsCount(this.RedTreasureChest) == 0L) {
                    string = "fisher_linneaus_q0030_0106.htm";
                }
            } else if (n3 == 2) {
                string = "fisher_linneaus_q0030_0203.htm";
            }
        } else if (n == this.Rukal) {
            string = n3 == 2 ? "bard_rukal_q0030_0201.htm" : "bard_rukal_q0030_0302.htm";
        }
        return string;
    }
}

