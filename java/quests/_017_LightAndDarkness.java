/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _017_LightAndDarkness
extends Quest
implements ScriptFile {
    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _017_LightAndDarkness() {
        super(0);
        this.addStartNpc(31517);
        this.addTalkId(31508);
        this.addTalkId(31509);
        this.addTalkId(31510);
        this.addTalkId(31511);
        this.addQuestItem(7168);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("dark_presbyter_q0017_04.htm")) {
            questState.setState(2);
            questState.setCond(1);
            questState.giveItems(7168, 4L);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equals("blessed_altar1_q0017_02.htm")) {
            questState.takeItems(7168, 1L);
            questState.setCond(2);
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equals("blessed_altar2_q0017_02.htm")) {
            questState.takeItems(7168, 1L);
            questState.setCond(3);
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equals("blessed_altar3_q0017_02.htm")) {
            questState.takeItems(7168, 1L);
            questState.setCond(4);
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equals("blessed_altar4_q0017_02.htm")) {
            questState.takeItems(7168, 1L);
            questState.setCond(5);
            questState.playSound("ItemSound.quest_middle");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 31517) {
            if (n2 == 0) {
                if (questState.getPlayer().getLevel() >= 61) {
                    string = "dark_presbyter_q0017_01.htm";
                } else {
                    string = "dark_presbyter_q0017_03.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 > 0 && n2 < 5 && questState.getQuestItemsCount(7168) > 0L) {
                string = "dark_presbyter_q0017_05.htm";
            } else if (n2 > 0 && n2 < 5 && questState.getQuestItemsCount(7168) == 0L) {
                string = "dark_presbyter_q0017_06.htm";
                questState.setCond(0);
                questState.exitCurrentQuest(false);
            } else if (n2 == 5 && questState.getQuestItemsCount(7168) == 0L) {
                string = "dark_presbyter_q0017_07.htm";
                questState.addExpAndSp(105527L, 0L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(false);
            }
        } else if (n == 31508) {
            if (n2 == 1) {
                string = questState.getQuestItemsCount(7168) != 0L ? "blessed_altar1_q0017_01.htm" : "blessed_altar1_q0017_03.htm";
            } else if (n2 == 2) {
                string = "blessed_altar1_q0017_05.htm";
            }
        } else if (n == 31509) {
            if (n2 == 2) {
                string = questState.getQuestItemsCount(7168) != 0L ? "blessed_altar2_q0017_01.htm" : "blessed_altar2_q0017_03.htm";
            } else if (n2 == 3) {
                string = "blessed_altar2_q0017_05.htm";
            }
        } else if (n == 31510) {
            if (n2 == 3) {
                string = questState.getQuestItemsCount(7168) != 0L ? "blessed_altar3_q0017_01.htm" : "blessed_altar3_q0017_03.htm";
            } else if (n2 == 4) {
                string = "blessed_altar3_q0017_05.htm";
            }
        } else if (n == 31511) {
            if (n2 == 4) {
                string = questState.getQuestItemsCount(7168) != 0L ? "blessed_altar4_q0017_01.htm" : "blessed_altar4_q0017_03.htm";
            } else if (n2 == 5) {
                string = "blessed_altar4_q0017_05.htm";
            }
        }
        return string;
    }
}

