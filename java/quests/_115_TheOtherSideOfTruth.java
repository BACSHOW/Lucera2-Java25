/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _115_TheOtherSideOfTruth
extends Quest
implements ScriptFile {
    private static final int aFc = 32020;
    private static final int aFd = 32018;
    private static final int aFe = 32022;
    private static final int aFf = 32021;
    private static final int aFg = 32077;
    private static final int aFh = 32078;
    private static final int aFi = 32079;
    private static final int aFj = 8079;
    private static final int aFk = 8080;
    private static final int aFl = 8081;
    private static final int aFm = 8082;

    public _115_TheOtherSideOfTruth() {
        super(0);
        this.addStartNpc(32020);
        this.addTalkId(32018, 32022, 32021, 32077, 32078, 32079);
        this.addQuestItem(8079, 8080, 8081, 8082);
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
        int n = questState.getInt("beyond_the_turth");
        int n2 = questState.getInt("beyond_the_turth_ex");
        int n3 = npcInstance.getNpcId();
        if (n3 == 32020) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("beyond_the_turth", String.valueOf(1), true);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "repre_q0115_03.htm";
            } else if (string.equalsIgnoreCase("reply_1") && n == 2) {
                questState.setCond(3);
                questState.set("beyond_the_turth", String.valueOf(3), true);
                questState.takeItems(8079, -1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "repre_q0115_07.htm";
            } else if (string.equalsIgnoreCase("reply_2") && n == 2) {
                questState.takeItems(8079, -1L);
                questState.unset("beyond_the_turth");
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                string2 = "repre_q0115_08.htm";
            } else if (string.equalsIgnoreCase("reply_3") && n == 3) {
                questState.setCond(4);
                questState.set("beyond_the_turth", String.valueOf(4), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "repre_q0115_11.htm";
            } else if (string.equalsIgnoreCase("reply_4") && n == 3) {
                questState.setCond(4);
                questState.set("beyond_the_turth", String.valueOf(4), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "repre_q0115_12.htm";
            } else if (string.equalsIgnoreCase("reply_5") && n == 3) {
                questState.unset("beyond_the_turth");
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                string2 = "repre_q0115_13.htm";
            } else if (string.equalsIgnoreCase("reply_6") && n == 4) {
                questState.setCond(5);
                questState.set("beyond_the_turth", String.valueOf(5), true);
                questState.playSound("AmbSound.t_wingflap_04");
                questState.playSound("ItemSound.quest_middle");
                string2 = "repre_q0115_17.htm";
            } else if (string.equalsIgnoreCase("reply_8") && n == 9 && questState.getQuestItemsCount(8082) >= 1L) {
                questState.setCond(10);
                questState.set("beyond_the_turth", String.valueOf(10), true);
                questState.takeItems(8082, -1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "repre_q0115_23.htm";
            } else if (string.equalsIgnoreCase("reply_9") && n == 10) {
                if (questState.getQuestItemsCount(8081) >= 1L) {
                    questState.takeItems(8081, -1L);
                    questState.giveItems(57, 60044L);
                    questState.unset("beyond_the_turth");
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(false);
                    string2 = "repre_q0115_25.htm";
                } else {
                    questState.setCond(11);
                    questState.set("beyond_the_turth", String.valueOf(11), true);
                    questState.playSound("AmbSound.thunder_02");
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "repre_q0115_27.htm";
                }
            } else if (string.equalsIgnoreCase("reply_10") && n == 10) {
                if (questState.getQuestItemsCount(8081) >= 1L) {
                    questState.takeItems(8081, -1L);
                    questState.giveItems(57, 60040L);
                    questState.unset("beyond_the_turth");
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(false);
                    string2 = "repre_q0115_26.htm";
                } else {
                    questState.setCond(11);
                    questState.set("beyond_the_turth", String.valueOf(11), true);
                    questState.playSound("AmbSound.thunder_02");
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "repre_q0115_28.htm";
                }
            }
        } else if (n3 == 32018) {
            if (string.equalsIgnoreCase("reply_1") && n == 6 && questState.getQuestItemsCount(8080) > 0L) {
                questState.setCond(7);
                questState.set("beyond_the_turth", String.valueOf(7), true);
                questState.takeItems(8080, -1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "misa_q0115_05.htm";
            }
        } else if (n3 == 32022) {
            if (string.equalsIgnoreCase("reply_1") && n == 8) {
                questState.setCond(9);
                questState.set("beyond_the_turth", String.valueOf(9), true);
                questState.giveItems(8082, 1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "keier_q0115_02.htm";
            }
        } else if (n3 == 32021) {
            if (string.equalsIgnoreCase("reply_1") && n == 7 && n2 % 2 < 1) {
                int n4 = n2;
                if (n4 == 6 || n4 == 10 || n4 == 12) {
                    int n5 = n4 + 1;
                    questState.set("beyond_the_turth_ex", String.valueOf(n5), true);
                    questState.giveItems(8081, 1L);
                    string2 = "ice_sculpture_q0115_03.htm";
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (string.equalsIgnoreCase("reply_2") && n == 7 && n2 % 2 < 1) {
                int n6 = n2;
                if (n6 == 6 || n6 == 10 || n6 == 12) {
                    int n7 = n6 + 1;
                    questState.set("beyond_the_turth_ex", String.valueOf(n7), true);
                    string2 = "ice_sculpture_q0115_04.htm";
                }
            } else if (string.equalsIgnoreCase("reply_3") && n == 7 && n2 == 14) {
                questState.setCond(8);
                questState.set("beyond_the_turth", String.valueOf(8), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "ice_sculpture_q0115_06.htm";
            }
        } else if (n3 == 32077) {
            if (string.equalsIgnoreCase("reply_1") && n == 7 && n2 % 4 <= 1) {
                int n8 = n2;
                if (n8 == 5 || n8 == 9 || n8 == 12) {
                    int n9 = n8 + 2;
                    questState.set("beyond_the_turth_ex", String.valueOf(n9), true);
                    questState.giveItems(8081, 1L);
                    string2 = "ice_sculpture_q0115_03.htm";
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (string.equalsIgnoreCase("reply_2") && n == 7 && n2 % 4 <= 1) {
                int n10 = n2;
                if (n10 == 5 || n10 == 9 || n10 == 12) {
                    int n11 = n10 + 2;
                    questState.set("beyond_the_turth_ex", String.valueOf(n11), true);
                    string2 = "ice_sculpture_q0115_04.htm";
                }
            } else if (string.equalsIgnoreCase("reply_3") && n == 7 && n2 == 13) {
                questState.setCond(8);
                questState.set("beyond_the_turth", String.valueOf(8), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "ice_sculpture_q0115_06.htm";
            }
        } else if (n3 == 32078) {
            if (string.equalsIgnoreCase("reply_1") && n == 7 && n2 % 8 <= 3) {
                int n12 = n2;
                if (n12 == 3 || n12 == 9 || n12 == 10) {
                    int n13 = n12 + 4;
                    questState.set("beyond_the_turth_ex", String.valueOf(n13), true);
                    questState.giveItems(8081, 1L);
                    string2 = "ice_sculpture_q0115_03.htm";
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (string.equalsIgnoreCase("reply_2") && n == 7 && n2 % 8 <= 3) {
                int n14 = n2;
                if (n14 == 3 || n14 == 9 || n14 == 10) {
                    int n15 = n14 + 4;
                    questState.set("beyond_the_turth_ex", String.valueOf(n15), true);
                    string2 = "ice_sculpture_q0115_04.htm";
                }
            } else if (string.equalsIgnoreCase("reply_3") && n == 7 && n2 == 11) {
                questState.setCond(8);
                questState.set("beyond_the_turth", String.valueOf(8), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "ice_sculpture_q0115_06.htm";
            }
        } else if (n3 == 32079) {
            if (string.equalsIgnoreCase("reply_1") && n == 7 && n2 <= 7) {
                int n16 = n2;
                if (n16 == 3 || n16 == 5 || n16 == 6) {
                    int n17 = n16 + 8;
                    questState.set("beyond_the_turth_ex", String.valueOf(n17), true);
                    questState.giveItems(8081, 1L);
                    string2 = "ice_sculpture_q0115_03.htm";
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (string.equalsIgnoreCase("reply_2") && n == 7 && n2 <= 7) {
                int n18 = n2;
                if (n18 == 3 || n18 == 5 || n18 == 6) {
                    int n19 = n18 + 8;
                    questState.set("beyond_the_turth_ex", String.valueOf(n19), true);
                    string2 = "ice_sculpture_q0115_04.htm";
                }
            } else if (string.equalsIgnoreCase("reply_3") && n == 7 && n2 == 7) {
                questState.setCond(8);
                questState.set("beyond_the_turth", String.valueOf(8), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "ice_sculpture_q0115_06.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("beyond_the_turth");
        int n2 = questState.getInt("beyond_the_turth_ex");
        int n3 = npcInstance.getNpcId();
        int n4 = questState.getState();
        switch (n4) {
            case 1: {
                if (n3 != 32020) break;
                if (questState.getPlayer().getLevel() < 53 || questState.getPlayer().getLevel() >= 64) {
                    string = "repre_q0115_02.htm";
                    break;
                }
                string = "repre_q0115_01.htm";
                break;
            }
            case 2: {
                if (n3 == 32020) {
                    if (n == 1) {
                        string = "repre_q0115_04.htm";
                        break;
                    }
                    if (n == 2 && questState.getQuestItemsCount(8079) == 0L) {
                        questState.unset("beyond_the_turth");
                        questState.playSound("ItemSound.quest_finish");
                        questState.exitCurrentQuest(true);
                        string = "repre_q0115_05.htm";
                        break;
                    }
                    if (n == 2 && questState.getQuestItemsCount(8079) > 0L) {
                        string = "repre_q0115_06.htm";
                        break;
                    }
                    if (n == 3) {
                        string = "repre_q0115_09.htm";
                        break;
                    }
                    if (n == 4) {
                        string = "repre_q0115_16.htm";
                        break;
                    }
                    if (n == 5) {
                        questState.setCond(6);
                        questState.set("beyond_the_turth", String.valueOf(6), true);
                        questState.giveItems(8080, 1L);
                        questState.playSound("ItemSound.quest_middle");
                        string = "repre_q0115_18.htm";
                        break;
                    }
                    if (n == 6 && questState.getQuestItemsCount(8080) != 0L) {
                        string = "repre_q0115_19.htm";
                        break;
                    }
                    if (n == 6 && questState.getQuestItemsCount(8080) == 0L) {
                        questState.giveItems(8080, 1L);
                        string = "repre_q0115_20.htm";
                        break;
                    }
                    if (n >= 7 && n < 9) {
                        string = "repre_q0115_21.htm";
                        break;
                    }
                    if (n == 9 && questState.getQuestItemsCount(8082) >= 1L) {
                        string = "repre_q0115_22.htm";
                        break;
                    }
                    if (n == 10) {
                        string = "repre_q0115_24.htm";
                        break;
                    }
                    if (n == 11 && questState.getQuestItemsCount(8081) == 0L) {
                        string = "repre_q0115_29.htm";
                        break;
                    }
                    if (n != 11 || questState.getQuestItemsCount(8081) < 1L) break;
                    questState.takeItems(8081, -1L);
                    questState.giveItems(57, 60044L);
                    questState.unset("beyond_the_turth");
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(false);
                    string = "repre_q0115_30.htm";
                    break;
                }
                if (n3 == 32018) {
                    if (n > 2 && n < 5) {
                        string = "misa_q0115_01.htm";
                        break;
                    }
                    if (n == 1) {
                        questState.setCond(2);
                        questState.set("beyond_the_turth", String.valueOf(2), true);
                        questState.giveItems(8079, 1L);
                        questState.playSound("ItemSound.quest_middle");
                        string = "misa_q0115_02.htm";
                        break;
                    }
                    if (n == 2) {
                        string = "misa_q0115_03.htm";
                        break;
                    }
                    if (n == 6 && questState.getQuestItemsCount(8080) > 0L) {
                        string = "misa_q0115_04.htm";
                        break;
                    }
                    if (n != 7) break;
                    string = "misa_q0115_06.htm";
                    break;
                }
                if (n3 == 32022) {
                    if (n == 8) {
                        string = "keier_q0115_01.htm";
                        break;
                    }
                    if (n == 9 && questState.getQuestItemsCount(8082) >= 1L) {
                        string = "keier_q0115_03.htm";
                        break;
                    }
                    if (n == 9 && questState.getQuestItemsCount(8082) == 0L) {
                        questState.giveItems(8082, 1L);
                        string = "keier_q0115_04.htm";
                        break;
                    }
                    if (n != 11 || questState.getQuestItemsCount(8082) != 0L) break;
                    string = "keier_q0115_05.htm";
                    break;
                }
                if (n3 == 32021) {
                    if (n == 7 && n2 % 2 < 1) {
                        int n5 = n2;
                        if (n5 == 6 || n5 == 10 || n5 == 12) {
                            string = "ice_sculpture_q0115_02.htm";
                            break;
                        }
                        if (n5 == 14) {
                            string = "ice_sculpture_q0115_05.htm";
                            break;
                        }
                        int n6 = n5 + 1;
                        questState.set("beyond_the_turth_ex", String.valueOf(n6), true);
                        string = "ice_sculpture_q0115_01.htm";
                        break;
                    }
                    if (n == 7 && n2 % 2 >= 1) {
                        string = "ice_sculpture_q0115_01a.htm";
                        break;
                    }
                    if (n == 8) {
                        string = "ice_sculpture_q0115_07.htm";
                        break;
                    }
                    if (n == 11 && questState.getQuestItemsCount(8081) == 0L) {
                        questState.setCond(12);
                        questState.giveItems(8081, 1L);
                        questState.playSound("ItemSound.quest_middle");
                        string = "ice_sculpture_q0115_08.htm";
                        break;
                    }
                    if (n != 11 || questState.getQuestItemsCount(8081) != 1L) break;
                    string = "ice_sculpture_q0115_09.htm";
                    break;
                }
                if (n3 == 32077) {
                    if (n == 7 && n2 % 4 <= 1) {
                        int n7 = n2;
                        if (n7 == 5 || n7 == 9 || n7 == 12) {
                            string = "ice_sculpture_q0115_02.htm";
                            break;
                        }
                        if (n7 == 13) {
                            string = "ice_sculpture_q0115_05.htm";
                            break;
                        }
                        int n8 = n7 + 2;
                        questState.set("beyond_the_turth_ex", String.valueOf(n8), true);
                        string = "ice_sculpture_q0115_01.htm";
                        break;
                    }
                    if (n == 7 && n2 % 4 > 1) {
                        string = "ice_sculpture_q0115_01a.htm";
                        break;
                    }
                    if (n == 8) {
                        string = "ice_sculpture_q0115_07.htm";
                        break;
                    }
                    if (n == 11 && questState.getQuestItemsCount(8081) == 0L) {
                        questState.setCond(12);
                        questState.giveItems(8081, 1L);
                        questState.playSound("ItemSound.quest_middle");
                        string = "ice_sculpture_q0115_08.htm";
                        break;
                    }
                    if (n != 11 || questState.getQuestItemsCount(8081) != 1L) break;
                    string = "ice_sculpture_q0115_09.htm";
                    break;
                }
                if (n3 == 32078) {
                    if (n == 7 && n2 % 8 <= 3) {
                        int n9 = n2;
                        if (n9 == 3 || n9 == 9 || n9 == 10) {
                            string = "ice_sculpture_q0115_02.htm";
                            break;
                        }
                        if (n9 == 11) {
                            string = "ice_sculpture_q0115_05.htm";
                            break;
                        }
                        int n10 = n9 + 4;
                        questState.set("beyond_the_turth_ex", String.valueOf(n10), true);
                        string = "ice_sculpture_q0115_01.htm";
                        break;
                    }
                    if (n == 7 && n2 % 8 > 3) {
                        string = "ice_sculpture_q0115_01a.htm";
                        break;
                    }
                    if (n == 8) {
                        string = "ice_sculpture_q0115_07.htm";
                        break;
                    }
                    if (n == 11 && questState.getQuestItemsCount(8081) == 0L) {
                        questState.setCond(12);
                        questState.giveItems(8081, 1L);
                        questState.playSound("ItemSound.quest_middle");
                        string = "ice_sculpture_q0115_08.htm";
                        break;
                    }
                    if (n != 11 || questState.getQuestItemsCount(8081) != 1L) break;
                    string = "ice_sculpture_q0115_09.htm";
                    break;
                }
                if (n3 != 32079) break;
                if (n == 7 && n2 <= 7) {
                    int n11 = n2;
                    if (n11 == 3 || n11 == 5 || n11 == 6) {
                        string = "ice_sculpture_q0115_02.htm";
                        break;
                    }
                    if (n11 == 7) {
                        string = "ice_sculpture_q0115_05.htm";
                        break;
                    }
                    int n12 = n11 + 8;
                    questState.set("beyond_the_turth_ex", String.valueOf(n12), true);
                    string = "ice_sculpture_q0115_01.htm";
                    break;
                }
                if (n == 7 && n2 > 7) {
                    string = "ice_sculpture_q0115_01a.htm";
                    break;
                }
                if (n == 8) {
                    string = "ice_sculpture_q0115_07.htm";
                    break;
                }
                if (n == 11 && questState.getQuestItemsCount(8081) == 0L) {
                    questState.setCond(12);
                    questState.giveItems(8081, 1L);
                    questState.playSound("ItemSound.quest_middle");
                    string = "ice_sculpture_q0115_08.htm";
                    break;
                }
                if (n != 11 || questState.getQuestItemsCount(8081) != 1L) break;
                string = "ice_sculpture_q0115_09.htm";
            }
        }
        return string;
    }
}

