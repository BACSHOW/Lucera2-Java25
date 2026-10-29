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

public class _021_HiddenTruth
extends Quest
implements ScriptFile {
    private static final int azT = 31522;
    private static final int azU = 31523;
    private static final int azV = 31524;
    private static final int azW = 31525;
    private static final int azX = 31526;
    private static final int azY = 31348;
    private static final int azZ = 31349;
    private static final int aAa = 31350;
    private static final int aAb = 31328;
    private static final int aAc = 7140;
    private static final int aAd = 7141;
    private int aAe = 0;

    public _021_HiddenTruth() {
        super(0);
        this.addStartNpc(31522);
        this.addTalkId(31523, 31524, 31525, 31526, 31348, 31349, 31350, 31328);
        this.addQuestItem(7140);
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

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getInt("secret_truth");
        int n2 = questState.getInt("secret_truth_ex");
        int n3 = questState.getInt("spawned_rune_ghost1");
        int n4 = questState.getInt("spawned_rune_ghost1b");
        int n5 = npcInstance.getNpcId();
        if (n5 == 31522) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("secret_truth", String.valueOf(1), true);
                questState.set("spawned_rune_ghost1", String.valueOf(0), true);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "shadow_hardin_q0021_02.htm";
            }
        } else if (n5 == 31523) {
            if (string.equalsIgnoreCase("reply_1")) {
                string2 = "q_forest_stone1_q0021_02.htm";
            } else if (string.equalsIgnoreCase("reply_2") && (n == 1 || n == 3)) {
                if (n3 == 0) {
                    questState.setCond(2);
                    questState.playSound("SkillSound5.horror_02");
                    questState.set("spawned_rune_ghost1", String.valueOf(1), true);
                    string2 = "q_forest_stone1_q0021_03.htm";
                    NpcInstance npcInstance2 = questState.addSpawn(31524, 51432, -54570, -3136);
                    Functions.npcSayCustomMessage(npcInstance2, "NpcString.WHO_AWOKE_ME", new Object[0]);
                    questState.startQuestTimer("2101", 300000L, npcInstance2);
                } else {
                    questState.playSound("SkillSound5.horror_02");
                    string2 = "q_forest_stone1_q0021_04.htm";
                }
            }
        } else if (n5 == 31524) {
            if (string.equalsIgnoreCase("reply_3")) {
                if (this.aAe <= 5) {
                    questState.setCond(3);
                    questState.set("secret_truth", String.valueOf(3), true);
                    questState.set("secret_truth_ex", String.valueOf(n2 + 1), true);
                    questState.set("spawned_rune_ghost1b", String.valueOf(0), true);
                    questState.playSound("ItemSound.quest_middle");
                    NpcInstance npcInstance3 = questState.addSpawn(31525, 51816, -54744, -3160);
                    ++this.aAe;
                    questState.startQuestTimer("2102", 500L, npcInstance3);
                    questState.startQuestTimer("2103", 120000L, npcInstance3);
                    return null;
                }
                string2 = "rune_ghost1_q0021_06a.htm";
            } else if (string.equalsIgnoreCase("2101")) {
                questState.set("spawned_rune_ghost1", String.valueOf(0), true);
                if (n3 == 1) {
                    npcInstance.deleteMe();
                }
                return null;
            }
        } else if (n5 == 31525) {
            if (string.equalsIgnoreCase("2102")) {
                if (n4 == 0) {
                    Functions.npcSayCustomMessage(npcInstance, "NpcString.MY_MASTER_HAS_INSTRUCTED_ME_TO_BE_YOUR_GUIDE_S1", questState.getPlayer().getName());
                }
                return null;
            }
            if (string.equalsIgnoreCase("2103")) {
                --this.aAe;
                if (this.aAe < 0) {
                    this.aAe = 0;
                }
                if (npcInstance != null) {
                    npcInstance.deleteMe();
                }
                return null;
            }
            if (string.equalsIgnoreCase("2105")) {
                --this.aAe;
                if (this.aAe < 0) {
                    this.aAe = 0;
                }
                if (npcInstance != null) {
                    npcInstance.deleteMe();
                }
                return null;
            }
        } else if (n5 == 31526) {
            if (string.equalsIgnoreCase("reply_4")) {
                questState.playSound("ItemSound.item_drop_equip_armor_cloth");
                string2 = "broken_desk1_q0021_03.htm";
            } else if (string.equalsIgnoreCase("reply_5")) {
                string2 = "broken_desk1_q0021_04.htm";
            } else if (string.equalsIgnoreCase("reply_6")) {
                string2 = "broken_desk1_q0021_05.htm";
            } else if (string.equalsIgnoreCase("reply_7")) {
                questState.set("secret_truth", String.valueOf(5), true);
                string2 = "broken_desk1_q0021_07.htm";
            } else if (string.equalsIgnoreCase("reply_8")) {
                if (n == 5) {
                    questState.setCond(5);
                    questState.set("secret_truth", String.valueOf(6), true);
                    questState.playSound("AmdSound.ed_chimes_05");
                    return null;
                }
                if (n == 6) {
                    string2 = "broken_desk1_q0021_09.htm";
                }
            } else if (string.equalsIgnoreCase("reply_9")) {
                string2 = "broken_desk1_q0021_12.htm";
            } else if (string.equalsIgnoreCase("reply_10")) {
                questState.setCond(6);
                questState.set("secret_truth", String.valueOf(7), true);
                questState.set("secret_truth_ex", String.valueOf(0), true);
                questState.giveItems(7140, 1L);
                string2 = "broken_desk1_q0021_14.htm";
            } else if (string.equalsIgnoreCase("reply_11")) {
                string2 = "broken_desk1_q0021_13.htm";
            }
        } else if (n5 == 31328) {
            if (string.equalsIgnoreCase("reply_11")) {
                string2 = "highpriest_innocentin_q0021_02.htm";
            } else if (string.equalsIgnoreCase("reply_12")) {
                string2 = "highpriest_innocentin_q0021_03.htm";
            } else if (string.equalsIgnoreCase("reply_13")) {
                string2 = "highpriest_innocentin_q0021_04.htm";
            } else if (string.equalsIgnoreCase("reply_14")) {
                questState.takeItems(7140, 1L);
                questState.giveItems(7141, 1L);
                questState.playSound("ItemSound.quest_finish");
                questState.unset("secret_truth");
                questState.unset("secret_truth_ex");
                questState.unset("spawned_rune_ghost1");
                questState.unset("spawned_rune_ghost1b");
                questState.exitCurrentQuest(false);
                string2 = "highpriest_innocentin_q0021_05.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("secret_truth");
        int n2 = questState.getInt("secret_truth_ex");
        int n3 = questState.getInt("spawned_rune_ghost1b");
        int n4 = npcInstance.getNpcId();
        int n5 = questState.getState();
        switch (n5) {
            case 1: {
                if (n4 != 31522) break;
                if (questState.getPlayer().getLevel() < 63) {
                    string = "shadow_hardin_q0021_03.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                string = "shadow_hardin_q0021_01.htm";
                break;
            }
            case 2: {
                if (n4 == 31522) {
                    if (n != 1) break;
                    string = "shadow_hardin_q0021_05.htm";
                    break;
                }
                if (n4 == 31523) {
                    if (n != 1 && n != 3) break;
                    string = "q_forest_stone1_q0021_01.htm";
                    break;
                }
                if (n4 == 31524) {
                    if (n == 1) {
                        string = "rune_ghost1_q0021_01.htm";
                        break;
                    }
                    if (n == 3 && n2 <= 20) {
                        if (this.aAe <= 5) {
                            questState.setCond(3);
                            questState.playSound("ItemSound.quest_middle");
                            questState.set("secret_truth_ex", String.valueOf(n2 + 1), true);
                            questState.set("spawned_rune_ghost1b", String.valueOf(0), true);
                            string = "rune_ghost1_q0021_07.htm";
                            NpcInstance npcInstance2 = questState.addSpawn(31525, 51816, -54744, -3160);
                            ++this.aAe;
                            questState.startQuestTimer("2102", 500L, npcInstance2);
                            questState.startQuestTimer("2103", 120000L, npcInstance2);
                            return null;
                        }
                        string = "rune_ghost1_q0021_07a.htm";
                        break;
                    }
                    if (n == 3 && n2 > 20) {
                        questState.setCond(4);
                        questState.playSound("ItemSound.quest_middle");
                        string = "rune_ghost1_q0021_07b.htm";
                        break;
                    }
                    if (n < 4) break;
                    string = "rune_ghost1_q0021_07c.htm";
                    break;
                }
                if (n4 == 31525) {
                    if (n != 3) break;
                    if (n3 == 0) {
                        string = "rune_ghost1b_q0021_01.htm";
                        break;
                    }
                    string = "rune_ghost1b_q0021_02.htm";
                    questState.startQuestTimer("2105", 3000L);
                    break;
                }
                if (n4 == 31526) {
                    if (n == 3) {
                        string = "broken_desk1_q0021_01.htm";
                        break;
                    }
                    if (n == 5) {
                        questState.setCond(5);
                        questState.set("secret_truth", String.valueOf(6), true);
                        questState.playSound("AmdSound.ed_chimes_05");
                        string = "broken_desk1_q0021_10.htm";
                        break;
                    }
                    if (n == 6) {
                        string = "broken_desk1_q0021_11.htm";
                        break;
                    }
                    if (n != 7) break;
                    string = "broken_desk1_q0021_15.htm";
                    break;
                }
                if (n4 == 31348) {
                    if (n == 7 && questState.getQuestItemsCount(7140) >= 1L) {
                        questState.set("secret_truth", String.valueOf(n + 1), true);
                        questState.set("secret_truth_ex", String.valueOf(n2 + 1), true);
                        string = "falsepriest_agripel_q0021_01.htm";
                        break;
                    }
                    if (n == 8 && questState.getQuestItemsCount(7140) >= 1L && (n2 == 2 || n2 == 4)) {
                        questState.set("secret_truth", String.valueOf(n + 1), true);
                        questState.set("secret_truth_ex", String.valueOf(n2 + 1), true);
                        string = "falsepriest_agripel_q0021_02.htm";
                        break;
                    }
                    if (n == 8 && questState.getQuestItemsCount(7140) >= 1L && n2 == 1) {
                        string = "falsepriest_agripel_q0021_01.htm";
                        break;
                    }
                    if (n == 9 && questState.getQuestItemsCount(7140) >= 1L && n2 == 6) {
                        questState.setCond(7);
                        questState.set("secret_truth", String.valueOf(10), true);
                        questState.playSound("ItemSound.quest_middle");
                        string = "falsepriest_agripel_q0021_03.htm";
                        break;
                    }
                    if (n == 9 && questState.getQuestItemsCount(7140) >= 1L && n2 != 6) {
                        string = "falsepriest_agripel_q0021_02.htm";
                        break;
                    }
                    if (n != 10 || questState.getQuestItemsCount(7140) < 1L) break;
                    string = "falsepriest_agripel_q0021_03.htm";
                    break;
                }
                if (n4 == 31349) {
                    if (n == 7 && questState.getQuestItemsCount(7140) >= 1L) {
                        questState.set("secret_truth", String.valueOf(n + 1), true);
                        questState.set("secret_truth_ex", String.valueOf(n2 + 2), true);
                        string = "falsepriest_benedict_q0021_01.htm";
                        break;
                    }
                    if (n == 8 && questState.getQuestItemsCount(7140) >= 1L && (n2 == 1 || n2 == 4)) {
                        questState.set("secret_truth", String.valueOf(n + 1), true);
                        questState.set("secret_truth_ex", String.valueOf(n2 + 2), true);
                        string = "falsepriest_benedict_q0021_02.htm";
                        break;
                    }
                    if (n == 8 && questState.getQuestItemsCount(7140) >= 1L && n2 == 2) {
                        string = "falsepriest_benedict_q0021_01.htm";
                        break;
                    }
                    if (n == 9 && questState.getQuestItemsCount(7140) >= 1L && n2 == 5) {
                        questState.setCond(7);
                        questState.set("secret_truth", String.valueOf(10), true);
                        questState.playSound("ItemSound.quest_middle");
                        string = "falsepriest_benedict_q0021_03.htm";
                        break;
                    }
                    if (n == 9 && questState.getQuestItemsCount(7140) >= 1L && n2 != 5) {
                        string = "falsepriest_benedict_q0021_02.htm";
                        break;
                    }
                    if (n != 10 || questState.getQuestItemsCount(7140) < 1L) break;
                    string = "falsepriest_benedict_q0021_03.htm";
                    break;
                }
                if (n4 == 31350) {
                    if (n == 7 && questState.getQuestItemsCount(7140) >= 1L) {
                        questState.set("secret_truth", String.valueOf(n + 1), true);
                        questState.set("secret_truth_ex", String.valueOf(n2 + 4), true);
                        string = "falsepriest_dominic_q0021_01.htm";
                        break;
                    }
                    if (n == 8 && questState.getQuestItemsCount(7140) >= 1L && (n2 == 1 || n2 == 2)) {
                        questState.set("secret_truth", String.valueOf(n + 1), true);
                        questState.set("secret_truth_ex", String.valueOf(n2 + 4), true);
                        string = "falsepriest_dominic_q0021_02.htm";
                        break;
                    }
                    if (n == 8 && questState.getQuestItemsCount(7140) >= 1L && n2 == 4) {
                        string = "falsepriest_dominic_q0021_01.htm";
                        break;
                    }
                    if (n == 9 && questState.getQuestItemsCount(7140) >= 1L && n2 == 3) {
                        questState.setCond(7);
                        questState.set("secret_truth", String.valueOf(10), true);
                        questState.playSound("ItemSound.quest_middle");
                        string = "falsepriest_dominic_q0021_03.htm";
                        break;
                    }
                    if (n == 9 && questState.getQuestItemsCount(7140) >= 1L && n2 != 3) {
                        string = "falsepriest_dominic_q0021_02.htm";
                        break;
                    }
                    if (n != 10 || questState.getQuestItemsCount(7140) < 1L) break;
                    string = "falsepriest_dominic_q0021_03.htm";
                    break;
                }
                if (n4 != 31328 || n != 10 || questState.getQuestItemsCount(7140) < 1L) break;
                string = "highpriest_innocentin_q0021_01.htm";
                break;
            }
            case 3: {
                if (questState.getPlayer().getQuestState(_022_TragedyInVonHellmannForest.class).isCreated()) break;
                string = "highpriest_innocentin_q0021_06.htm";
            }
        }
        return string;
    }
}

