/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _356_DigUpTheSeaOfSpores
extends Quest
implements ScriptFile {
    private static final int bre = 30717;
    private static final int brf = 20562;
    private static final int brg = 20558;
    private static final int brh = 5865;
    private static final int bri = 5866;

    public _356_DigUpTheSeaOfSpores() {
        super(0);
        this.addStartNpc(30717);
        this.addKillId(20562, 20558);
        this.addQuestItem(5865, 5866);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = npcInstance.getNpcId();
        if (n == 30717) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "magister_gauen_q0356_06.htm";
            } else if (string.equalsIgnoreCase("reply_1")) {
                string2 = "magister_gauen_q0356_05.htm";
            } else if (string.equalsIgnoreCase("reply_2")) {
                questState.setCond(1);
                string2 = "magister_gauen_q0356_11.htm";
            } else if (string.equalsIgnoreCase("reply_3") && questState.getQuestItemsCount(5866) >= 50L) {
                questState.addExpAndSp(31850L, 0L);
                questState.takeItems(5866, -1L);
                string2 = "magister_gauen_q0356_12.htm";
            } else if (string.equalsIgnoreCase("reply_4") && questState.getQuestItemsCount(5865) >= 50L) {
                questState.addExpAndSp(0L, 1820L);
                questState.takeItems(5865, -1L);
                string2 = "magister_gauen_q0356_13.htm";
            } else if (string.equalsIgnoreCase("reply_5") && questState.getQuestItemsCount(5865) >= 50L && questState.getQuestItemsCount(5866) >= 50L) {
                questState.addExpAndSp(45500L, 2600L);
                questState.takeItems(5866, -1L);
                questState.takeItems(5865, -1L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                string2 = "magister_gauen_q0356_14.htm";
            } else if (string.equalsIgnoreCase("reply_6")) {
                string2 = "magister_gauen_q0356_15.htm";
            } else if (string.equalsIgnoreCase("reply_7") && questState.getQuestItemsCount(5865) >= 50L && questState.getQuestItemsCount(5866) >= 50L) {
                int n2 = Rnd.get(100);
                questState.takeItems(5866, -1L);
                questState.takeItems(5865, -1L);
                if (n2 < 20) {
                    questState.giveItems(57, 44000L);
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(true);
                    string2 = "magister_gauen_q0356_16.htm";
                } else if (n2 < 70) {
                    questState.giveItems(57, 20950L);
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(true);
                    string2 = "magister_gauen_q0356_17.htm";
                } else if (n2 >= 70) {
                    questState.giveItems(57, 10400L);
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(true);
                    string2 = "magister_gauen_q0356_18.htm";
                }
            } else if (string.equalsIgnoreCase("reply_8")) {
                questState.takeItems(5866, -1L);
                questState.takeItems(5865, -1L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                string2 = "magister_gauen_q0356_19.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getState();
        switch (n2) {
            case 1: {
                if (n != 30717) break;
                if (questState.getPlayer().getLevel() < 43 || questState.getPlayer().getLevel() > 51) {
                    string = "magister_gauen_q0356_01.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                string = "magister_gauen_q0356_02.htm";
                break;
            }
            case 2: {
                if (n != 30717) break;
                if (questState.getQuestItemsCount(5866) < 50L && questState.getQuestItemsCount(5865) < 50L) {
                    string = "magister_gauen_q0356_07.htm";
                    break;
                }
                if (questState.getQuestItemsCount(5866) >= 50L && questState.getQuestItemsCount(5865) < 50L) {
                    string = "magister_gauen_q0356_08.htm";
                    break;
                }
                if (questState.getQuestItemsCount(5866) < 50L && questState.getQuestItemsCount(5865) >= 50L) {
                    string = "magister_gauen_q0356_09.htm";
                    break;
                }
                if (questState.getQuestItemsCount(5866) < 50L || questState.getQuestItemsCount(5865) < 50L) break;
                string = "magister_gauen_q0356_10.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (n == 20562 && questState.getQuestItemsCount(5865) < 50L) {
            if (Rnd.get(100) < 94) {
                questState.giveItems(5865, 1L);
                questState.playSound("ItemSound.quest_itemget");
                if (questState.getQuestItemsCount(5866) >= 50L && questState.getQuestItemsCount(5865) >= 50L) {
                    questState.setCond(3);
                    questState.playSound("ItemSound.quest_middle");
                } else if (questState.getQuestItemsCount(5865) >= 50L) {
                    questState.setCond(2);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20558 && questState.getQuestItemsCount(5866) < 50L && Rnd.get(100) < 73) {
            questState.giveItems(5866, 1L);
            questState.playSound("ItemSound.quest_itemget");
            if (questState.getQuestItemsCount(5866) >= 50L && questState.getQuestItemsCount(5865) >= 50L) {
                questState.setCond(3);
                questState.playSound("ItemSound.quest_middle");
            } else if (questState.getQuestItemsCount(5866) >= 50L) {
                questState.setCond(2);
                questState.playSound("ItemSound.quest_middle");
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }
}

