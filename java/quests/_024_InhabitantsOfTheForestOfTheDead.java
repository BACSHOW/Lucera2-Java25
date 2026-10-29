/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import quests._023_LidiasHeart;

public class _024_InhabitantsOfTheForestOfTheDead
extends Quest
implements ScriptFile {
    private static final int aAC = 31389;
    private static final int aAD = 31531;
    private static final int aAE = 31532;
    private static final int azT = 31522;
    private static final int aAF = 7065;
    private static final int aAz = 7148;
    private static final int aAG = 7151;
    private static final int aAH = 7152;
    private static final int aAI = 7153;
    private static final int aAJ = 7154;
    private static final int aAK = 7156;
    private static final int aAL = 21557;
    private static final int aAM = 21558;
    private static final int aAN = 21560;
    private static final int aAO = 21563;
    private static final int aAP = 21564;
    private static final int aAQ = 21565;
    private static final int aAR = 21566;
    private static final int aAS = 21567;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _024_InhabitantsOfTheForestOfTheDead() {
        super(0);
        this.addStartNpc(31389);
        this.addTalkId(31531, 31532, 31522);
        this.addKillId(21557, 21558, 21560, 21563, 21564, 21565, 21566, 21567);
        this.addQuestItem(7151);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getInt("people_of_lost_forest");
        if (string.startsWith("see_creature")) {
            if (questState.getQuestItemsCount(7153) > 0L) {
                questState.setCond(4);
                questState.takeItems(7153, -1L);
                questState.giveItems(7154, 1L);
                Functions.npcSay(npcInstance, "That sign!");
            }
            return null;
        }
        if (string.equalsIgnoreCase("quest_accept")) {
            questState.setCond(1);
            questState.set("people_of_lost_forest", String.valueOf(1), true);
            questState.giveItems(7152, 1L);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            string2 = "day_dorian_q0024_03.htm";
        } else if (string.equalsIgnoreCase("reply_1")) {
            questState.set("people_of_lost_forest", String.valueOf(3), true);
            string2 = "day_dorian_q0024_08.htm";
        } else if (string.equalsIgnoreCase("reply_3")) {
            if (n == 3) {
                questState.setCond(3);
                questState.set("people_of_lost_forest", String.valueOf(4), true);
                questState.giveItems(7153, 1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "day_dorian_q0024_13.htm";
            }
        } else if (string.equalsIgnoreCase("reply_5")) {
            questState.playSound("InterfaceSound.charstat_open_01");
            string2 = "day_dorian_q0024_18.htm";
        } else if (string.equalsIgnoreCase("reply_6")) {
            if (n == 4 && questState.getQuestItemsCount(7154) >= 1L) {
                questState.setCond(5);
                questState.set("people_of_lost_forest", String.valueOf(5), true);
                questState.takeItems(7154, -1L);
                string2 = "day_dorian_q0024_19.htm";
            }
        } else if (string.equalsIgnoreCase("reply_1a")) {
            questState.setCond(2);
            questState.set("people_of_lost_forest", String.valueOf(2), true);
            questState.takeItems(7152, -1L);
            questState.playSound("ItemSound.quest_middle");
            string2 = "q_forest_stone2_q0024_02.htm";
        } else if (string.equalsIgnoreCase("reply_7")) {
            if (n == 5) {
                questState.setCond(6);
                questState.set("people_of_lost_forest", String.valueOf(6), true);
                questState.giveItems(7065, 1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "maid_of_ridia_q0024_04.htm";
            }
        } else if (string.equalsIgnoreCase("reply_8")) {
            if ((n == 6 || n == 7) && questState.getQuestItemsCount(7148) >= 1L) {
                questState.takeItems(7065, -1L);
                questState.takeItems(7148, -1L);
                questState.set("people_of_lost_forest", String.valueOf(8), true);
                string2 = "maid_of_ridia_q0024_06.htm";
            } else if ((n == 6 || n == 7) && questState.getQuestItemsCount(7148) == 0L) {
                questState.setCond(7);
                questState.set("people_of_lost_forest", String.valueOf(7), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "maid_of_ridia_q0024_07.htm";
            }
        } else if (string.equalsIgnoreCase("reply_10")) {
            if (n == 8) {
                questState.set("people_of_lost_forest", String.valueOf(9), true);
                string2 = "maid_of_ridia_q0024_10.htm";
            }
        } else if (string.equalsIgnoreCase("reply_11")) {
            if (n == 9) {
                questState.set("people_of_lost_forest", String.valueOf(10), true);
                string2 = "maid_of_ridia_q0024_14.htm";
            }
        } else if (string.equalsIgnoreCase("reply_12")) {
            if (n == 10) {
                questState.setCond(9);
                questState.set("people_of_lost_forest", String.valueOf(11), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "maid_of_ridia_q0024_19.htm";
            }
        } else if (string.equalsIgnoreCase("reply_14")) {
            if (n == 11 && questState.getQuestItemsCount(7151) >= 1L) {
                questState.set("people_of_lost_forest", String.valueOf(12), true);
                questState.takeItems(7151, -1L);
                string2 = "shadow_hardin_q0024_03.htm";
            }
        } else if (string.equalsIgnoreCase("reply_16")) {
            if (n == 12) {
                questState.setCond(11);
                questState.set("people_of_lost_forest", String.valueOf(13), true);
                string2 = "shadow_hardin_q0024_08.htm";
            }
        } else if (string.equalsIgnoreCase("reply_18")) {
            if (n == 13) {
                questState.set("people_of_lost_forest", String.valueOf(14), true);
                string2 = "shadow_hardin_q0024_17.htm";
            }
        } else if (string.equalsIgnoreCase("reply_19") && n == 14) {
            questState.giveItems(7156, 1L);
            questState.addExpAndSp(242105L, 22529L);
            questState.unset("people_of_lost_forest");
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(false);
            string2 = "shadow_hardin_q0024_21.htm";
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        QuestState questState2 = questState.getPlayer().getQuestState(_023_LidiasHeart.class);
        int n = questState.getInt("people_of_lost_forest");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 31389) break;
                if (questState2 != null && questState2.isCompleted() && questState.getPlayer().getLevel() >= 65) {
                    string = "day_dorian_q0024_01.htm";
                    break;
                }
                string = "day_dorian_q0024_02.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n2 == 31389) {
                    if (n == 1) {
                        string = "day_dorian_q0024_04.htm";
                        break;
                    }
                    if (n == 2) {
                        string = "day_dorian_q0024_05.htm";
                        break;
                    }
                    if (n == 3) {
                        string = "day_dorian_q0024_09.htm";
                        break;
                    }
                    if (n == 4 && questState.getQuestItemsCount(7154) >= 1L) {
                        string = "day_dorian_q0024_15.htm";
                        break;
                    }
                    if (n == 5) {
                        string = "day_dorian_q0024_20.htm";
                        break;
                    }
                    if (n == 7 && questState.getQuestItemsCount(7148) == 0L) {
                        questState.setCond(8);
                        questState.giveItems(7148, 1L);
                        questState.playSound("ItemSound.quest_middle");
                        string = "day_dorian_q0024_21.htm";
                        break;
                    }
                    if ((n != 7 || questState.getQuestItemsCount(7148) < 1L) && n != 6) break;
                    string = "day_dorian_q0024_22.htm";
                    break;
                }
                if (n2 == 31531) {
                    if (n == 1 && questState.getQuestItemsCount(7152) >= 1L) {
                        questState.playSound("AmdSound.d_wind_loot_02");
                        string = "q_forest_stone2_q0024_01.htm";
                        break;
                    }
                    if (n != 2) break;
                    string = "q_forest_stone2_q0024_03.htm";
                    break;
                }
                if (n2 == 31532) {
                    if (n == 5) {
                        string = "maid_of_ridia_q0024_01.htm";
                        break;
                    }
                    if (n == 6 && questState.getQuestItemsCount(7065) >= 1L) {
                        string = "maid_of_ridia_q0024_05.htm";
                        break;
                    }
                    if (n == 7) {
                        string = "maid_of_ridia_q0024_07a.htm";
                        break;
                    }
                    if (n == 8) {
                        string = "maid_of_ridia_q0024_08.htm";
                        break;
                    }
                    if (n == 9) {
                        string = "maid_of_ridia_q0024_11.htm";
                        break;
                    }
                    if (n != 11) break;
                    string = "maid_of_ridia_q0024_20.htm";
                    break;
                }
                if (n2 != 31522) break;
                if (n == 11 && questState.getQuestItemsCount(7151) >= 1L) {
                    string = "shadow_hardin_q0024_01.htm";
                    break;
                }
                if (n == 12) {
                    string = "shadow_hardin_q0024_04.htm";
                    break;
                }
                if (n == 13) {
                    string = "shadow_hardin_q0024_09.htm";
                    break;
                }
                if (n != 14) break;
                string = "shadow_hardin_q0024_18.htm";
                break;
            }
            case 3: {
                if (n2 != 31522) break;
                string = "shadow_hardin_q0024_22.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getInt("people_of_lost_forest");
        if ((n == 21557 || n == 21558 || n == 21560 || n == 21563 || n == 21564 || n == 21565 || n == 21566 || n == 21567) && n2 == 11 && Rnd.get(100) < 10) {
            questState.setCond(10);
            questState.giveItems(7151, 1L);
            questState.playSound("ItemSound.quest_itemget");
        }
        return null;
    }
}

