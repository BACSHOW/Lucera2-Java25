/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import quests._022_TragedyInVonHellmannForest;

public class _023_LidiasHeart
extends Quest
implements ScriptFile {
    private static final int aAb = 31328;
    private static final int azX = 31526;
    private static final int azV = 31524;
    private static final int azU = 31523;
    private static final int aAv = 31386;
    private static final int aAw = 31530;
    private static final int aAx = 7063;
    private static final int aAy = 7149;
    private static final int aAz = 7148;
    private static final int aAA = 7064;
    private static final int aAB = 7150;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _023_LidiasHeart() {
        super(0);
        this.addStartNpc(31328);
        this.addTalkId(31328, 31526, 31524, 31523, 31386, 31530);
        this.addQuestItem(7063, 7149, 7148, 7150, 7064);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        QuestState questState2 = questState.getPlayer().getQuestState(_022_TragedyInVonHellmannForest.class);
        int n = questState.getInt("spawned_rune_ghost1");
        int n2 = questState.getInt("truth_of_ridia");
        int n3 = npcInstance.getNpcId();
        if (n3 == 31328) {
            if (string.equalsIgnoreCase("quest_accept")) {
                if (questState.getPlayer().getLevel() < 64 || questState2 == null) {
                    string2 = "highpriest_innocentin_q0023_02.htm";
                } else {
                    questState.setCond(1);
                    questState.set("truth_of_ridia", String.valueOf(1), true);
                    questState.set("spawned_rune_ghost1", String.valueOf(0), true);
                    questState.giveItems(7063, 1L);
                    questState.giveItems(7149, 1L);
                    questState.setState(2);
                    questState.playSound("ItemSound.quest_accept");
                    string2 = "highpriest_innocentin_q0023_03.htm";
                }
            } else if (string.equalsIgnoreCase("reply_1")) {
                string2 = "highpriest_innocentin_q0023_05.htm";
            } else if (string.equalsIgnoreCase("reply_2")) {
                string2 = "highpriest_innocentin_q0023_06.htm";
            } else if (string.equalsIgnoreCase("reply_3")) {
                if (n2 == 1) {
                    questState.setCond(2);
                    questState.set("truth_of_ridia", String.valueOf(2), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "highpriest_innocentin_q0023_07.htm";
                }
            } else if (string.equalsIgnoreCase("reply_13")) {
                string2 = "highpriest_innocentin_q0023_11.htm";
            } else if (string.equalsIgnoreCase("reply_15")) {
                if (n2 == 5 || n2 == 6) {
                    questState.setCond(5);
                    questState.set("truth_of_ridia", String.valueOf(6), true);
                    string2 = "highpriest_innocentin_q0023_12.htm";
                }
            } else if (string.equalsIgnoreCase("reply_14")) {
                if (n2 == 5 || n2 == 6) {
                    questState.set("truth_of_ridia", String.valueOf(7), true);
                    string2 = "highpriest_innocentin_q0023_13.htm";
                }
            } else if (string.equalsIgnoreCase("reply_17")) {
                string2 = "highpriest_innocentin_q0023_16.htm";
            } else if (string.equalsIgnoreCase("reply_18")) {
                string2 = "highpriest_innocentin_q0023_17.htm";
            } else if (string.equalsIgnoreCase("reply_19")) {
                string2 = "highpriest_innocentin_q0023_18.htm";
            } else if (string.equalsIgnoreCase("reply_20")) {
                string2 = "highpriest_innocentin_q0023_19.htm";
                questState.playSound("AmbSound.mt_creak01");
            } else if (string.equalsIgnoreCase("reply_21")) {
                if (n2 == 7) {
                    questState.setCond(6);
                    questState.set("truth_of_ridia", String.valueOf(8), true);
                    string2 = "highpriest_innocentin_q0023_20.htm";
                }
            } else if (string.equalsIgnoreCase("reply_16")) {
                questState.setCond(5);
                string2 = "highpriest_innocentin_q0023_21.htm";
            }
        } else if (n3 == 31526) {
            if (string.equalsIgnoreCase("reply_4")) {
                if (n2 == 2 && questState.getQuestItemsCount(7149) >= 1L) {
                    questState.set("truth_of_ridia", String.valueOf(3), true);
                    questState.takeItems(7149, -1L);
                    string2 = "broken_desk1_q0023_02.htm";
                }
            } else if (string.equalsIgnoreCase("reply_5")) {
                string2 = "broken_desk1_q0023_04.htm";
            } else if (string.equalsIgnoreCase("reply_7")) {
                string2 = "broken_desk1_q0023_05.htm";
            } else if (string.equalsIgnoreCase("reply_8")) {
                questState.set("truth_of_ridia", String.valueOf(n2 + 1), true);
                questState.giveItems(7148, 1L);
                string2 = "broken_desk1_q0023_06.htm";
                if (questState.getQuestItemsCount(7064) >= 1L) {
                    questState.setCond(4);
                }
            } else if (string.equalsIgnoreCase("reply_6")) {
                string2 = "broken_desk1_q0023_07a.htm";
            } else if (string.equalsIgnoreCase("reply_9")) {
                string2 = "broken_desk1_q0023_08.htm";
                questState.playSound("ItemSound.itemdrop_armor_leather");
            } else if (string.equalsIgnoreCase("reply_10")) {
                string2 = "broken_desk1_q0023_09.htm";
            } else if (string.equalsIgnoreCase("reply_11")) {
                string2 = "broken_desk1_q0023_10.htm";
                questState.playSound("AmbSound.eg_dron_02");
            } else if (string.equalsIgnoreCase("reply_12")) {
                questState.set("truth_of_ridia", String.valueOf(n2 + 1), true);
                questState.giveItems(7064, 1L);
                string2 = "broken_desk1_q0023_11.htm";
                if (questState.getQuestItemsCount(7148) >= 1L) {
                    questState.setCond(4);
                }
            } else if (string.equals("read_book")) {
                string2 = "q_ridia_diary001.htm";
            }
        } else if (n3 == 31524) {
            if (string.equalsIgnoreCase("reply_23")) {
                string2 = "rune_ghost1_q0023_02.htm";
                questState.playSound("ChrSound.MHFighter_cry");
            } else if (string.equalsIgnoreCase("reply_24")) {
                string2 = "rune_ghost1_q0023_03.htm";
            } else if (string.equalsIgnoreCase("reply_25")) {
                if (n2 == 8) {
                    questState.setCond(7);
                    questState.set("truth_of_ridia", String.valueOf(9), true);
                    questState.takeItems(7064, -1L);
                    string2 = "rune_ghost1_q0023_04.htm";
                }
            } else if (string.equalsIgnoreCase("2101")) {
                questState.unset("spawned_rune_ghost1");
                if (npcInstance != null) {
                    npcInstance.deleteMe();
                }
                return null;
            }
        } else if (n3 == 31523) {
            if (string.equalsIgnoreCase("reply_22")) {
                if (n2 == 8 || n2 == 9) {
                    if (n == 0) {
                        questState.set("spawned_rune_ghost1", String.valueOf(1), true);
                        questState.set("rune_ghost1_player_name", questState.getPlayer().getName(), true);
                        questState.playSound("SkillSound5.horror_02");
                        NpcInstance npcInstance2 = questState.addSpawn(31524, 51432, -54570, -3136);
                        Functions.npcSay(npcInstance2, "Who awoke me?");
                        questState.startQuestTimer("2101", 300000L, npcInstance2);
                        string2 = "q_forest_stone1_q0023_02.htm";
                    } else {
                        questState.playSound("SkillSound5.horror_02");
                        string2 = "q_forest_stone1_q0023_03.htm";
                    }
                }
            } else if (string.equalsIgnoreCase("reply_26") && n2 == 9) {
                questState.setCond(8);
                questState.set("truth_of_ridia", String.valueOf(10), true);
                questState.giveItems(7149, 1L);
                string2 = "q_forest_stone1_q0023_06.htm";
            }
        } else if (n3 == 31530 && string.equalsIgnoreCase("reply_27") && n2 == 11 && questState.getQuestItemsCount(7149) >= 1L) {
            questState.setCond(10);
            questState.giveItems(7150, 1L);
            questState.takeItems(7149, -1L);
            questState.playSound("ItemSound.itemdrop_weapon_spear");
            string2 = "rust_box1_q0023_02.htm";
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        QuestState questState2 = questState.getPlayer().getQuestState(_022_TragedyInVonHellmannForest.class);
        int n = questState.getInt("truth_of_ridia");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 31328) break;
                if (questState.getPlayer().getLevel() >= 64 && questState2 != null && questState2.isCompleted()) {
                    string = "highpriest_innocentin_q0023_01.htm";
                    break;
                }
                questState.exitCurrentQuest(true);
                string = "highpriest_innocentin_q0023_01a.htm";
                break;
            }
            case 2: {
                if (n2 == 31328) {
                    if (n == 1) {
                        string = "highpriest_innocentin_q0023_04.htm";
                        break;
                    }
                    if (n == 2) {
                        string = "highpriest_innocentin_q0023_08.htm";
                        break;
                    }
                    if (n == 5) {
                        string = "highpriest_innocentin_q0023_09.htm";
                        break;
                    }
                    if (n == 6) {
                        string = "highpriest_innocentin_q0023_14.htm";
                        break;
                    }
                    if (n == 7) {
                        string = "highpriest_innocentin_q0023_15.htm";
                        break;
                    }
                    if (n != 8) break;
                    questState.setCond(6);
                    questState.playSound("ItemSound.quest_middle");
                    string = "highpriest_innocentin_q0023_22.htm";
                    break;
                }
                if (n2 == 31526) {
                    if (n == 2 && questState.getQuestItemsCount(7149) >= 1L) {
                        questState.setCond(3);
                        questState.playSound("ItemSound.quest_middle");
                        string = "broken_desk1_q0023_01.htm";
                        break;
                    }
                    if (n == 3) {
                        string = "broken_desk1_q0023_03.htm";
                        break;
                    }
                    if (n == 4 && questState.getQuestItemsCount(7148) >= 1L) {
                        string = "broken_desk1_q0023_07.htm";
                        break;
                    }
                    if (n == 4 && questState.getQuestItemsCount(7064) >= 1L) {
                        string = "broken_desk1_q0023_12.htm";
                        break;
                    }
                    if (n != 5 || questState.getQuestItemsCount(7148) < 1L || questState.getQuestItemsCount(7064) < 1L) break;
                    questState.startQuestTimer("read_book", 120000L, npcInstance);
                    string = "broken_desk1_q0023_13.htm";
                    break;
                }
                if (n2 == 31524) {
                    if (n == 8) {
                        string = "rune_ghost1_q0023_01.htm";
                        break;
                    }
                    if (n == 9 && questState.getQuestItemsCount(7149) == 0L) {
                        string = "rune_ghost1_q0023_05.htm";
                        break;
                    }
                    if (n != 9 && n != 10 || questState.getQuestItemsCount(7149) < 1L) break;
                    questState.set("truth_of_ridia", String.valueOf(10), true);
                    string = "rune_ghost1_q0023_06.htm";
                    break;
                }
                if (n2 == 31523) {
                    if (n == 8) {
                        string = "q_forest_stone1_q0023_01.htm";
                        break;
                    }
                    if (n == 9) {
                        string = "q_forest_stone1_q0023_04.htm";
                        break;
                    }
                    if (n != 10) break;
                    string = "q_forest_stone1_q0023_05.htm";
                    break;
                }
                if (n2 == 31386) {
                    if (n == 10 && questState.getQuestItemsCount(7149) >= 1L) {
                        questState.setCond(9);
                        questState.set("truth_of_ridia", String.valueOf(11), true);
                        questState.playSound("ItemSound.quest_middle");
                        string = "day_violet_q0023_01.htm";
                        break;
                    }
                    if (n == 11 && questState.getQuestItemsCount(7150) == 0L) {
                        string = "day_violet_q0023_02.htm";
                        break;
                    }
                    if (n != 11 || questState.getQuestItemsCount(7150) < 1L) break;
                    questState.giveItems(57, 100000L);
                    questState.takeItems(7150, -1L);
                    questState.unset("truth_of_ridia");
                    questState.unset("spawned_rune_ghost1");
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(false);
                    string = "day_violet_q0023_03.htm";
                    break;
                }
                if (n2 != 31530) break;
                if (n == 11 && questState.getQuestItemsCount(7149) >= 1L) {
                    string = "rust_box1_q0023_01.htm";
                    break;
                }
                if (n != 11 || questState.getQuestItemsCount(7150) < 1L) break;
                string = "rust_box1_q0023_03.htm";
                break;
            }
            case 3: {
                if (n2 != 31386) break;
                string = "day_violet_q0023_04.htm";
            }
        }
        return string;
    }
}

