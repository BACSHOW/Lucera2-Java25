/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _016_TheComingDarkness
extends Quest
implements ScriptFile {
    public final int dark_presbyter = 31517;
    public final int vicious_altar1 = 31512;
    public final int vicious_altar2 = 31513;
    public final int vicious_altar3 = 31514;
    public final int vicious_altar4 = 31515;
    public final int vicious_altar5 = 31516;
    public final int q_crystal_of_sealing = 7167;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _016_TheComingDarkness() {
        super(0);
        this.addStartNpc(31517);
        this.addTalkId(31512, 31513, 31514, 31515, 31516);
        this.addQuestItem(7167);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getInt("draw_to_darkness");
        if (string.equalsIgnoreCase("quest_accept")) {
            questState.setCond(1);
            questState.set("draw_to_darkness", String.valueOf(1), true);
            questState.giveItems(7167, 5L);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            string2 = "dark_presbyter_q0016_04.htm";
        } else if (string.equalsIgnoreCase("reply_1") && n == 1) {
            if (questState.getQuestItemsCount(7167) >= 1L) {
                questState.setCond(2);
                questState.set("draw_to_darkness", String.valueOf(2), true);
                questState.takeItems(7167, 1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "vicious_altar1_q0016_02.htm";
            } else {
                string2 = "vicious_altar1_q0016_03.htm";
            }
        } else if (string.equalsIgnoreCase("reply_1a") && n == 2) {
            if (questState.getQuestItemsCount(7167) >= 1L) {
                questState.setCond(3);
                questState.set("draw_to_darkness", String.valueOf(3), true);
                questState.takeItems(7167, 1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "vicious_altar2_q0016_02.htm";
            } else {
                string2 = "vicious_altar2_q0016_03.htm";
            }
        } else if (string.equalsIgnoreCase("reply_1b") && n == 3) {
            if (questState.getQuestItemsCount(7167) >= 1L) {
                questState.setCond(4);
                questState.set("draw_to_darkness", String.valueOf(4), true);
                questState.takeItems(7167, 1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "vicious_altar3_q0016_02.htm";
            } else {
                string2 = "vicious_altar3_q0016_03.htm";
            }
        } else if (string.equalsIgnoreCase("reply_1c") && n == 4) {
            if (questState.getQuestItemsCount(7167) >= 1L) {
                questState.setCond(5);
                questState.set("draw_to_darkness", String.valueOf(5), true);
                questState.takeItems(7167, 1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "vicious_altar4_q0016_02.htm";
            } else {
                string2 = "vicious_altar4_q0016_03.htm";
            }
        } else if (string.equalsIgnoreCase("reply_1d") && n == 5) {
            if (questState.getQuestItemsCount(7167) >= 1L) {
                questState.setCond(6);
                questState.set("draw_to_darkness", String.valueOf(6), true);
                questState.takeItems(7167, 1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "vicious_altar5_q0016_02.htm";
            } else {
                string2 = "vicious_altar5_q0016_03.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getInt("draw_to_darkness");
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n != 31517) break;
                if (questState.getPlayer().getLevel() < 61) {
                    string = "dark_presbyter_q0016_03.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                questState.exitCurrentQuest(true);
                string = "dark_presbyter_q0016_01.htm";
                break;
            }
            case 2: {
                if (n == 31517) {
                    if (n2 >= 1 && n2 <= 5 && questState.getQuestItemsCount(7167) >= 6L) {
                        string = "dark_presbyter_q0016_05.htm";
                        break;
                    }
                    if (n2 >= 1 && n2 <= 5 && questState.getQuestItemsCount(7167) < 6L) {
                        questState.takeItems(7167, -1L);
                        questState.playSound("ItemSound.quest_finish");
                        questState.unset("draw_to_darkness");
                        string = "dark_presbyter_q0016_06.htm";
                        questState.exitCurrentQuest(false);
                        break;
                    }
                    if (n2 != 6) break;
                    questState.addExpAndSp(221958L, 0L);
                    questState.takeItems(7167, -1L);
                    questState.playSound("ItemSound.quest_finish");
                    questState.unset("draw_to_darkness");
                    string = "dark_presbyter_q0016_07.htm";
                    questState.exitCurrentQuest(false);
                    break;
                }
                if (n == 31512) {
                    if (n2 == 1 && questState.getQuestItemsCount(7167) >= 1L) {
                        string = "vicious_altar1_q0016_01.htm";
                        break;
                    }
                    if (n2 == 1 && questState.getQuestItemsCount(7167) < 1L) {
                        string = "vicious_altar1_q0016_04.htm";
                        break;
                    }
                    if (n2 != 2) break;
                    string = "vicious_altar1_q0016_05.htm";
                    break;
                }
                if (n == 31513) {
                    if (n2 == 2 && questState.getQuestItemsCount(7167) >= 1L) {
                        string = "vicious_altar2_q0016_01.htm";
                        break;
                    }
                    if (n2 == 2 && questState.getQuestItemsCount(7167) < 1L) {
                        string = "vicious_altar2_q0016_04.htm";
                        break;
                    }
                    if (n2 != 3) break;
                    string = "vicious_altar2_q0016_05.htm";
                    break;
                }
                if (n == 31514) {
                    if (n2 == 3 && questState.getQuestItemsCount(7167) >= 1L) {
                        string = "vicious_altar3_q0016_01.htm";
                        break;
                    }
                    if (n2 == 3 && questState.getQuestItemsCount(7167) < 1L) {
                        string = "vicious_altar3_q0016_04.htm";
                        break;
                    }
                    if (n2 != 4) break;
                    string = "vicious_altar3_q0016_05.htm";
                    break;
                }
                if (n == 31515) {
                    if (n2 == 4 && questState.getQuestItemsCount(7167) >= 1L) {
                        string = "vicious_altar4_q0016_01.htm";
                        break;
                    }
                    if (n2 == 4 && questState.getQuestItemsCount(7167) < 1L) {
                        string = "vicious_altar4_q0016_04.htm";
                        break;
                    }
                    if (n2 != 5) break;
                    string = "vicious_altar4_q0016_05.htm";
                    break;
                }
                if (n != 31516) break;
                if (n2 == 5 && questState.getQuestItemsCount(7167) >= 1L) {
                    string = "vicious_altar5_q0016_01.htm";
                    break;
                }
                if (n2 == 5 && questState.getQuestItemsCount(7167) < 1L) {
                    string = "vicious_altar5_q0016_04.htm";
                    break;
                }
                if (n2 != 6) break;
                string = "vicious_altar5_q0016_05.htm";
            }
        }
        return string;
    }
}

