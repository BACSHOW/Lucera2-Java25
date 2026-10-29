/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;
import quests._052_WilliesSpecialBait;

public class _029_ChestCaughtWithABaitOfEarth
extends Quest
implements ScriptFile {
    int Willie = 31574;
    int Anabel = 30909;
    int SmallPurpleTreasureChest = 6507;
    int SmallGlassBox = 7627;
    int PlatedLeatherGloves = 2455;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _029_ChestCaughtWithABaitOfEarth() {
        super(0);
        this.addStartNpc(this.Willie);
        this.addTalkId(this.Anabel);
        this.addQuestItem(this.SmallGlassBox);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("fisher_willeri_q0029_0104.htm")) {
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equals("fisher_willeri_q0029_0201.htm")) {
            if (questState.getQuestItemsCount(this.SmallPurpleTreasureChest) > 0L) {
                questState.setCond(2);
                questState.playSound("ItemSound.quest_middle");
                questState.takeItems(this.SmallPurpleTreasureChest, 1L);
                questState.giveItems(this.SmallGlassBox, 1L);
            } else {
                string2 = "fisher_willeri_q0029_0202.htm";
            }
        } else if (string.equals("29_GiveGlassBox")) {
            if (questState.getQuestItemsCount(this.SmallGlassBox) == 1L) {
                string2 = "magister_anabel_q0029_0301.htm";
                questState.takeItems(this.SmallGlassBox, -1L);
                questState.giveItems(this.PlatedLeatherGloves, 1L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(false);
            } else {
                string2 = "magister_anabel_q0029_0302.htm";
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
        int n3 = questState.getCond();
        if (n == this.Willie) {
            if (n2 == 1) {
                if (questState.getPlayer().getLevel() < 48) {
                    string = "fisher_willeri_q0029_0102.htm";
                    questState.exitCurrentQuest(true);
                } else {
                    QuestState questState2 = questState.getPlayer().getQuestState(_052_WilliesSpecialBait.class);
                    if (questState2 != null) {
                        if (questState2.isCompleted()) {
                            string = "fisher_willeri_q0029_0101.htm";
                        } else {
                            string = "fisher_willeri_q0029_0102.htm";
                            questState.exitCurrentQuest(true);
                        }
                    } else {
                        string = "fisher_willeri_q0029_0103.htm";
                        questState.exitCurrentQuest(true);
                    }
                }
            } else if (n3 == 1) {
                string = "fisher_willeri_q0029_0105.htm";
                if (questState.getQuestItemsCount(this.SmallPurpleTreasureChest) == 0L) {
                    string = "fisher_willeri_q0029_0106.htm";
                }
            } else if (n3 == 2) {
                string = "fisher_willeri_q0029_0203.htm";
            }
        } else if (n == this.Anabel) {
            string = n3 == 2 ? "magister_anabel_q0029_0201.htm" : "magister_anabel_q0029_0302.htm";
        }
        return string;
    }
}

