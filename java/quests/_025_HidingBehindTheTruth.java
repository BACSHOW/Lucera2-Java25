/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import quests._024_InhabitantsOfTheForestOfTheDead;

public class _025_HidingBehindTheTruth
extends Quest
implements ScriptFile {
    private static final int azY = 31348;
    private static final int azZ = 31349;
    private static final int aAT = 31533;
    private static final int aAU = 31534;
    private static final int aAV = 31535;
    private static final int aAW = 31536;
    private static final int aAE = 31532;
    private static final int azT = 31522;
    private static final int aAD = 31531;
    private static final int aAx = 7063;
    private static final int aAX = 7066;
    private static final int aAY = 874;
    private static final int aAZ = 905;
    private static final int aBa = 936;
    private static final int aBb = 7157;
    private static final int aBc = 7155;
    private static final int aAK = 7156;
    private static final int aBd = 7158;
    private static final int aBe = 27218;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _025_HidingBehindTheTruth() {
        super(0);
        this.addStartNpc(31349);
        this.addTalkId(31348, 31522, 31533, 31534, 31535, 31532, 31531, 31536);
        this.addAttackId(27218);
        this.addKillId(27218);
        this.addQuestItem(7158);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getInt("man_behind_the_truth");
        int n2 = questState.getInt("man_behind_the_ex");
        int n3 = questState.getInt("spawned_triyol_zz");
        String string3 = questState.get("zzolda_player_name");
        if (string.equalsIgnoreCase("quest_accept")) {
            questState.setCond(1);
            questState.set("man_behind_the_truth", String.valueOf(1), true);
            questState.set("spawned_triyol_zz", String.valueOf(0), true);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            string2 = "falsepriest_benedict_q0025_03.htm";
        } else if (string.equalsIgnoreCase("reply_1")) {
            if (n == 1 && questState.getQuestItemsCount(7156) >= 1L) {
                string2 = "falsepriest_benedict_q0025_04.htm";
            } else if (n == 1 && questState.getQuestItemsCount(7156) == 0L) {
                questState.setCond(2);
                questState.playSound("ItemSound.quest_middle");
                string2 = "falsepriest_benedict_q0025_05.htm";
            }
        } else if (string.equalsIgnoreCase("reply_3")) {
            if (n == 1 && questState.getQuestItemsCount(7156) >= 1L) {
                questState.setCond(4);
                questState.set("man_behind_the_truth", String.valueOf(2), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "falsepriest_benedict_q0025_10.htm";
            }
        } else if (string.equalsIgnoreCase("reply_4")) {
            if (n == 2) {
                questState.set("man_behind_the_truth", String.valueOf(3), true);
                questState.takeItems(7156, -1L);
                string2 = "falsepriest_agripel_q0025_02.htm";
            }
        } else if (string.equalsIgnoreCase("reply_6")) {
            if (n == 3) {
                questState.setCond(5);
                questState.set("man_behind_the_truth", String.valueOf(6), true);
                questState.giveItems(7157, 1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "falsepriest_agripel_q0025_08.htm";
            }
        } else if (string.equalsIgnoreCase("reply_25")) {
            if (n == 20 && questState.getQuestItemsCount(7158) >= 1L) {
                questState.set("man_behind_the_truth", String.valueOf(21), true);
                questState.takeItems(7158, -1L);
                string2 = "falsepriest_agripel_q0025_10.htm";
            }
        } else if (string.equalsIgnoreCase("reply_21")) {
            if (n == 21) {
                questState.set("man_behind_the_truth", String.valueOf(22), true);
                string2 = "falsepriest_agripel_q0025_13.htm";
            }
        } else if (string.equalsIgnoreCase("reply_23")) {
            if (n == 22) {
                questState.setCond(17);
                questState.set("man_behind_the_truth", String.valueOf(23), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "falsepriest_agripel_q0025_16.htm";
            }
        } else if (string.equalsIgnoreCase("reply_24")) {
            if (n == 22) {
                questState.setCond(18);
                questState.set("man_behind_the_truth", String.valueOf(24), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "falsepriest_agripel_q0025_17.htm";
            }
        } else if (string.equalsIgnoreCase("reply_7")) {
            if (n == 6 && questState.getQuestItemsCount(7157) >= 1L) {
                questState.setCond(6);
                questState.set("man_behind_the_truth", String.valueOf(7), true);
                questState.set("man_behind_the_ex", String.valueOf(20), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "shadow_hardin_q0025_04.htm";
            }
        } else if (string.equalsIgnoreCase("reply_17")) {
            if (n == 16) {
                questState.set("man_behind_the_truth", String.valueOf(19), true);
                string2 = "shadow_hardin_q0025_10.htm";
            }
        } else if (string.equalsIgnoreCase("reply_19")) {
            if (n == 19) {
                questState.setCond(16);
                questState.set("man_behind_the_truth", String.valueOf(20), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "shadow_hardin_q0025_13.htm";
            }
        } else if (string.equalsIgnoreCase("reply_23a")) {
            if (n == 24) {
                questState.giveItems(874, 1L);
                questState.giveItems(936, 1L);
                questState.takeItems(7063, -1L);
                questState.addExpAndSp(572277L, 53750L);
                questState.unset("man_behind_the_truth");
                questState.unset("man_behind_the_ex");
                questState.exitCurrentQuest(false);
                questState.playSound("ItemSound.quest_finish");
                string2 = "shadow_hardin_q0025_16.htm";
            }
        } else if (string.equalsIgnoreCase("reply_8")) {
            int n4 = n;
            if ((n4 %= 1000) >= 100) {
                string2 = "broken_desk2_q0025_03.htm";
            } else if (Rnd.get(60) > n2) {
                questState.set("man_behind_the_ex", String.valueOf(n2 + 20), true);
                questState.set("man_behind_the_truth", String.valueOf(n + 100), true);
                string2 = "broken_desk2_q0025_04.htm";
            } else {
                questState.set("man_behind_the_truth", String.valueOf(8), true);
                string2 = "broken_desk2_q0025_05.htm";
                questState.playSound("AmdSound.dd_horror_02");
            }
        } else if (string.equalsIgnoreCase("reply_9")) {
            if (n == 8 && questState.getQuestItemsCount(7158) == 0L) {
                if (n3 == 0) {
                    questState.setCond(7);
                    questState.set("spawned_triyol_zz", String.valueOf(1), true);
                    questState.set("zzolda_player_name", questState.getPlayer().getName(), true);
                    NpcInstance npcInstance2 = questState.addSpawn(27218, 47142, -35941, -1623);
                    if (npcInstance2 != null) {
                        npcInstance2.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, questState.getPlayer(), 20000);
                    }
                    questState.startQuestTimer("2501", 500L, npcInstance2);
                    questState.startQuestTimer("2502", 120000L, npcInstance2);
                    string2 = "broken_desk2_q0025_07.htm";
                } else {
                    string2 = string3 == questState.getPlayer().getName() ? "broken_desk2_q0025_08.htm" : "broken_desk2_q0025_09.htm";
                }
            }
            if (n == 8 && questState.getQuestItemsCount(7158) >= 1L) {
                string2 = "broken_desk2_q0025_10.htm";
            }
        } else {
            if (string.equalsIgnoreCase("2501")) {
                Functions.npcSay(npcInstance, "That box was sealed by my master, " + questState.getPlayer().getName() + "! Don't touch it!");
                return null;
            }
            if (string.equalsIgnoreCase("2502")) {
                questState.unset("spawned_triyol_zz");
                if (npcInstance != null) {
                    npcInstance.deleteMe();
                }
                return null;
            }
            if (string.equalsIgnoreCase("reply_10")) {
                if (n == 8 && questState.getQuestItemsCount(7158) >= 1L && questState.getQuestItemsCount(7157) >= 1L) {
                    questState.setCond(9);
                    questState.set("man_behind_the_truth", String.valueOf(9), true);
                    questState.giveItems(7066, 1L);
                    questState.takeItems(7157, -1L);
                    string2 = "broken_desk2_q0025_11.htm";
                }
            } else if (string.equalsIgnoreCase("reply_8a")) {
                int n5 = n;
                if ((n5 %= 10000) >= 1000) {
                    string2 = "broken_desk3_q0025_03.htm";
                } else if (Rnd.get(60) > n2) {
                    questState.set("man_behind_the_ex", String.valueOf(n2 + 20), true);
                    questState.set("man_behind_the_truth", String.valueOf(n + 1000), true);
                    string2 = "broken_desk3_q0025_04.htm";
                } else {
                    questState.set("man_behind_the_truth", String.valueOf(8), true);
                    string2 = "broken_desk3_q0025_05.htm";
                    questState.playSound("AmdSound.dd_horror_02");
                }
            } else if (string.equalsIgnoreCase("reply_9a")) {
                if (n == 8 && questState.getQuestItemsCount(7158) == 0L) {
                    if (n3 == 0) {
                        questState.setCond(7);
                        questState.set("spawned_triyol_zz", String.valueOf(1), true);
                        questState.set("zzolda_player_name", questState.getPlayer().getName(), true);
                        NpcInstance npcInstance3 = questState.addSpawn(27218, 50055, -47020, -3396);
                        if (npcInstance3 != null) {
                            npcInstance3.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, questState.getPlayer(), 20000);
                        }
                        questState.startQuestTimer("2501", 500L, npcInstance3);
                        questState.startQuestTimer("2502", 120000L, npcInstance3);
                        string2 = "broken_desk3_q0025_07.htm";
                    } else {
                        string2 = string3 == questState.getPlayer().getName() ? "broken_desk3_q0025_08.htm" : "broken_desk3_q0025_09.htm";
                    }
                }
                if (n == 8 && questState.getQuestItemsCount(7158) >= 1L) {
                    string2 = "broken_desk3_q0025_10.htm";
                }
            } else if (string.equalsIgnoreCase("reply_10a")) {
                if (n == 8 && questState.getQuestItemsCount(7158) >= 1L && questState.getQuestItemsCount(7157) >= 1L) {
                    questState.setCond(9);
                    questState.set("man_behind_the_truth", String.valueOf(9), true);
                    questState.giveItems(7066, 1L);
                    questState.takeItems(7157, -1L);
                    string2 = "broken_desk3_q0025_11.htm";
                }
            } else if (string.equalsIgnoreCase("reply_8b")) {
                if (n >= 10000) {
                    string2 = "broken_desk4_q0025_03.htm";
                } else if (Rnd.get(60) > n2) {
                    questState.set("man_behind_the_ex", String.valueOf(n2 + 20), true);
                    questState.set("man_behind_the_truth", String.valueOf(n + 10000), true);
                    string2 = "broken_desk4_q0025_04.htm";
                } else {
                    questState.set("man_behind_the_truth", String.valueOf(8), true);
                    string2 = "broken_desk4_q0025_05.htm";
                    questState.playSound("AmdSound.dd_horror_02");
                }
            } else if (string.equalsIgnoreCase("reply_9b")) {
                if (n == 8 && questState.getQuestItemsCount(7158) == 0L) {
                    if (n3 == 0) {
                        questState.setCond(7);
                        questState.set("spawned_triyol_zz", String.valueOf(1), true);
                        questState.set("zzolda_player_name", questState.getPlayer().getName(), true);
                        NpcInstance npcInstance4 = questState.addSpawn(27218, 59712, -47568, -2720);
                        if (npcInstance4 != null) {
                            npcInstance4.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, questState.getPlayer(), 20000);
                        }
                        questState.startQuestTimer("2501", 500L, npcInstance4);
                        questState.startQuestTimer("2502", 120000L, npcInstance4);
                        string2 = "broken_desk4_q0025_07.htm";
                    } else {
                        string2 = string3 == questState.getPlayer().getName() ? "broken_desk4_q0025_08.htm" : "broken_desk4_q0025_09.htm";
                    }
                }
                if (n == 8 && questState.getQuestItemsCount(7158) >= 1L) {
                    string2 = "broken_desk4_q0025_10.htm";
                }
            } else if (string.equalsIgnoreCase("reply_10b")) {
                if (n == 8 && questState.getQuestItemsCount(7158) >= 1L && questState.getQuestItemsCount(7157) >= 1L) {
                    questState.setCond(9);
                    questState.set("man_behind_the_truth", String.valueOf(9), true);
                    questState.giveItems(7066, 1L);
                    questState.takeItems(7157, -1L);
                    string2 = "broken_desk4_q0025_11.htm";
                }
            } else if (string.equalsIgnoreCase("reply_11")) {
                if (n == 9 && questState.getQuestItemsCount(7066) >= 1L) {
                    questState.takeItems(7066, -1L);
                    questState.set("man_behind_the_truth", String.valueOf(10), true);
                    string2 = "maid_of_ridia_q0025_02.htm";
                }
            } else if (string.equalsIgnoreCase("reply_13")) {
                if (n == 10) {
                    questState.setCond(11);
                    questState.set("man_behind_the_truth", String.valueOf(11), true);
                    questState.playSound("SkillSound5.horror_01");
                    string2 = "maid_of_ridia_q0025_07.htm";
                }
            } else if (string.equalsIgnoreCase("reply_14")) {
                if (n == 13) {
                    if (n2 <= 3) {
                        questState.set("man_behind_the_ex", String.valueOf(n2 + 1), true);
                        questState.playSound("ChrSound.FDElf_Cry");
                        string2 = "maid_of_ridia_q0025_11.htm";
                    } else {
                        questState.set("man_behind_the_truth", String.valueOf(14), true);
                        string2 = "maid_of_ridia_q0025_12.htm";
                    }
                }
            } else if (string.equalsIgnoreCase("reply_15")) {
                if (n == 14) {
                    questState.set("man_behind_the_truth", String.valueOf(15), true);
                    string2 = "maid_of_ridia_q0025_17.htm";
                }
            } else if (string.equalsIgnoreCase("reply_16")) {
                if (n == 15) {
                    questState.setCond(15);
                    questState.set("man_behind_the_truth", String.valueOf(16), true);
                    string2 = "maid_of_ridia_q0025_21.htm";
                }
            } else if (string.equalsIgnoreCase("reply_22")) {
                if (n == 23) {
                    questState.giveItems(874, 1L);
                    questState.giveItems(905, 2L);
                    questState.takeItems(7063, -1L);
                    questState.addExpAndSp(572277L, 53750L);
                    questState.playSound("ItemSound.quest_finish");
                    questState.unset("man_behind_the_truth");
                    questState.unset("man_behind_the_ex");
                    string2 = "maid_of_ridia_q0025_25.htm";
                    questState.exitCurrentQuest(false);
                }
            } else if (string.equalsIgnoreCase("reply_1a")) {
                if (n == 11) {
                    questState.setCond(12);
                    NpcInstance npcInstance5 = questState.addSpawn(31536, 60104, -35820, -681);
                    questState.startQuestTimer("2503", 20000L, npcInstance5);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "q_forest_stone2_q0025_02.htm";
                }
            } else {
                if (string.equalsIgnoreCase("2503")) {
                    if (npcInstance != null) {
                        npcInstance.deleteMe();
                    }
                    return null;
                }
                if (string.equalsIgnoreCase("2504")) {
                    if (npcInstance != null) {
                        npcInstance.deleteMe();
                    }
                    return null;
                }
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        QuestState questState2 = questState.getPlayer().getQuestState(_024_InhabitantsOfTheForestOfTheDead.class);
        int n = questState.getInt("man_behind_the_truth");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 31349) break;
                if (questState2 != null && questState2.isCompleted() && questState.getPlayer().getLevel() >= 66) {
                    string = "falsepriest_benedict_q0025_01.htm";
                    break;
                }
                string = "falsepriest_benedict_q0025_02.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n2 == 31349) {
                    if (n == 1) {
                        string = "falsepriest_benedict_q0025_03a.htm";
                        break;
                    }
                    if (n != 2) break;
                    string = "falsepriest_benedict_q0025_11.htm";
                    break;
                }
                if (n2 == 31348) {
                    if (n == 2) {
                        string = "falsepriest_agripel_q0025_01.htm";
                        break;
                    }
                    if (n == 3) {
                        string = "falsepriest_agripel_q0025_03.htm";
                        break;
                    }
                    if (n == 6) {
                        string = "falsepriest_agripel_q0025_08a.htm";
                        break;
                    }
                    if (n == 20 && questState.getQuestItemsCount(7158) >= 1L) {
                        string = "falsepriest_agripel_q0025_09.htm";
                        break;
                    }
                    if (n == 21) {
                        string = "falsepriest_agripel_q0025_10a.htm";
                        break;
                    }
                    if (n == 22) {
                        string = "falsepriest_agripel_q0025_15.htm";
                        break;
                    }
                    if (n == 23) {
                        string = "falsepriest_agripel_q0025_18.htm";
                        break;
                    }
                    if (n != 24) break;
                    string = "falsepriest_agripel_q0025_19.htm";
                    break;
                }
                if (n2 == 31522) {
                    if (n == 1 && questState.getQuestItemsCount(7156) == 0L) {
                        questState.setCond(3);
                        questState.giveItems(7156, 1L);
                        questState.playSound("ItemSound.quest_middle");
                        string = "shadow_hardin_q0025_01.htm";
                        break;
                    }
                    if (n == 1 && questState.getQuestItemsCount(7156) >= 1L) {
                        string = "shadow_hardin_q0025_02.htm";
                        break;
                    }
                    if (n == 6 && questState.getQuestItemsCount(7157) >= 1L) {
                        string = "shadow_hardin_q0025_03.htm";
                        break;
                    }
                    if (n % 100 == 7) {
                        string = "shadow_hardin_q0025_05.htm";
                        break;
                    }
                    if (n == 9 && questState.getQuestItemsCount(7066) >= 1L) {
                        questState.setCond(10);
                        questState.playSound("ItemSound.quest_middle");
                        string = "shadow_hardin_q0025_06.htm";
                        break;
                    }
                    if (n == 16) {
                        string = "shadow_hardin_q0025_06a.htm";
                        break;
                    }
                    if (n == 19) {
                        string = "shadow_hardin_q0025_12.htm";
                        break;
                    }
                    if (n == 20) {
                        string = "shadow_hardin_q0025_14.htm";
                        break;
                    }
                    if (n == 24) {
                        string = "shadow_hardin_q0025_15.htm";
                        break;
                    }
                    if (n != 23) break;
                    string = "shadow_hardin_q0025_15a.htm";
                    break;
                }
                if (n2 == 31533) {
                    if (n % 100 == 7) {
                        string = "broken_desk2_q0025_01.htm";
                        break;
                    }
                    if (n % 100 >= 9) {
                        string = "broken_desk2_q0025_02.htm";
                        break;
                    }
                    if (n != 8) break;
                    string = "broken_desk2_q0025_06.htm";
                    break;
                }
                if (n2 == 31534) {
                    if (n % 100 == 7) {
                        string = "broken_desk3_q0025_01.htm";
                        break;
                    }
                    if (n % 100 >= 9) {
                        string = "broken_desk3_q0025_02.htm";
                        break;
                    }
                    if (n != 8) break;
                    string = "broken_desk3_q0025_06.htm";
                    break;
                }
                if (n2 == 31535) {
                    if (n % 100 == 7) {
                        string = "broken_desk4_q0025_01.htm";
                        break;
                    }
                    if (n % 100 >= 9) {
                        string = "broken_desk4_q0025_02.htm";
                        break;
                    }
                    if (n != 8) break;
                    string = "broken_desk4_q0025_06.htm";
                    break;
                }
                if (n2 == 31532) {
                    if (n == 9 && questState.getQuestItemsCount(7066) >= 1L) {
                        string = "maid_of_ridia_q0025_01.htm";
                        break;
                    }
                    if (n == 10) {
                        string = "maid_of_ridia_q0025_03.htm";
                        break;
                    }
                    if (n == 11) {
                        questState.playSound("SkillSound5.horror_01");
                        string = "maid_of_ridia_q0025_08.htm";
                        break;
                    }
                    if (n == 12 && questState.getQuestItemsCount(7155) >= 1L) {
                        questState.setCond(14);
                        questState.takeItems(7155, -1L);
                        questState.set("man_behind_the_truth", String.valueOf(13), true);
                        questState.playSound("ItemSound.quest_middle");
                        string = "maid_of_ridia_q0025_09.htm";
                        break;
                    }
                    if (n == 13) {
                        questState.set("man_behind_the_ex", String.valueOf(0), true);
                        questState.playSound("ChrSound.FDElf_Cry");
                        string = "maid_of_ridia_q0025_10.htm";
                        break;
                    }
                    if (n == 14) {
                        string = "maid_of_ridia_q0025_13.htm";
                        break;
                    }
                    if (n == 15) {
                        string = "maid_of_ridia_q0025_18.htm";
                        break;
                    }
                    if (n == 16) {
                        string = "maid_of_ridia_q0025_22.htm";
                        break;
                    }
                    if (n == 23) {
                        string = "maid_of_ridia_q0025_23.htm";
                        break;
                    }
                    if (n != 24) break;
                    string = "maid_of_ridia_q0025_24.htm";
                    break;
                }
                if (n2 == 31531) {
                    if (n == 11) {
                        string = "q_forest_stone2_q0025_01.htm";
                        break;
                    }
                    if (n != 12) break;
                    string = "q_forest_stone2_q0025_03.htm";
                    break;
                }
                if (n2 != 31536 || n != 11) break;
                questState.setCond(13);
                questState.set("man_behind_the_truth", String.valueOf(12), true);
                questState.giveItems(7155, 1L);
                questState.playSound("ItemSound.quest_middle");
                questState.startQuestTimer("2503", 3000L, npcInstance);
                string = "q_forest_box1_q0025_01.htm";
            }
        }
        return string;
    }

    @Override
    public String onAttack(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getInt("man_behind_the_truth");
        String string = questState.get("zzolda_player_name");
        double d = npcInstance.getCurrentHpPercents();
        if (n == 27218 && d <= 30.0 && n2 == 8 && questState.getQuestItemsCount(7158) == 0L && string == questState.getPlayer().getName()) {
            questState.setCond(8);
            questState.giveItems(7158, 1L);
            questState.playSound("ItemSound.quest_itemget");
            Functions.npcSay(npcInstance, "You've ended my immortal life! You're protected by the feudal lord, aren't you?");
            questState.unset("spawned_triyol_zz");
            questState.unset("zzolda_player_name");
            if (!questState.isRunningQuestTimer("2502") && npcInstance != null) {
                npcInstance.deleteMe();
            }
        }
        return null;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (n == 27218) {
            questState.unset("spawned_triyol_zz");
        }
        questState.unset("zzolda_player_name");
        return null;
    }
}

