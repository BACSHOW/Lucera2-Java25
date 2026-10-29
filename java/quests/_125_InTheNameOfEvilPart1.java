/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;
import l2.gameserver.tables.SkillTable;
import quests._124_MeetingTheElroki;

public class _125_InTheNameOfEvilPart1
extends Quest
implements ScriptFile {
    private static final int aGj = 32114;
    private static final int aGl = 32117;
    private static final int aGo = 32119;
    private static final int aGp = 32120;
    private static final int aGq = 32121;
    private static final int aGr = 22200;
    private static final int aGs = 22201;
    private static final int aGt = 22202;
    private static final int aGu = 22203;
    private static final int aGv = 22204;
    private static final int aGw = 22205;
    private static final int aGx = 22219;
    private static final int aGy = 22220;
    private static final int aGz = 22224;
    private static final int aGA = 22225;
    private static final int aGB = 8779;
    private static final int aGC = 8780;
    private static final int aGD = 8781;
    private static final int aGE = 8782;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _125_InTheNameOfEvilPart1() {
        super(0);
        this.addStartNpc(32114);
        this.addTalkId(32117, 32119, 32120, 32121);
        this.addKillId(22200, 22201, 22202, 22203, 22204, 22205, 22219, 22220, 22224, 22225);
        this.addQuestItem(8779, 8780);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getInt("name_of_cruel_god_one");
        int n2 = questState.getInt("name_of_cruel_god_one_ex");
        if (string.equalsIgnoreCase("quest_accept")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            string2 = "mushika_q0125_08.htm";
        } else if (string.equalsIgnoreCase("reply_5")) {
            questState.set("name_of_cruel_god_one", String.valueOf(1), true);
            string2 = "mushika_q0125_11.htm";
        } else if (string.equalsIgnoreCase("reply_6") && n == 1) {
            questState.setCond(2);
            questState.set("name_of_cruel_god_one", String.valueOf(2), true);
            questState.giveItems(8782, 1L);
            questState.playSound("ItemSound.quest_middle");
            string2 = "mushika_q0125_12.htm";
        } else if (string.equalsIgnoreCase("reply_13") && n == 2) {
            questState.setCond(3);
            questState.set("name_of_cruel_god_one", String.valueOf(3), true);
            questState.playSound("ItemSound.quest_middle");
            string2 = "shaman_caracawe_q0125_09.htm";
        } else if (string.equalsIgnoreCase("reply_17") && n == 4) {
            questState.setCond(5);
            questState.set("name_of_cruel_god_one", String.valueOf(5), true);
            questState.playSound("ItemSound.quest_middle");
            string2 = "shaman_caracawe_q0125_17.htm";
        } else if (string.equalsIgnoreCase("reply_19") && n == 5) {
            questState.set("name_of_cruel_god_one_ex", String.valueOf(0), true);
            string2 = "ulu_kaimu_stone_q0125_04.htm";
        } else if (string.equalsIgnoreCase("reply_1") && n == 5) {
            questState.set("name_of_cruel_god_one_ex", String.valueOf(n2 + 1), true);
            string2 = "ulu_kaimu_stone_q0125_05.htm";
        } else if (string.equalsIgnoreCase("reply_2") && n == 5) {
            questState.set("name_of_cruel_god_one_ex", String.valueOf(n2 + 10), true);
            string2 = "ulu_kaimu_stone_q0125_06.htm";
        } else if (string.equalsIgnoreCase("reply_3") && n == 5) {
            questState.set("name_of_cruel_god_one_ex", String.valueOf(n2 + 100), true);
            string2 = "ulu_kaimu_stone_q0125_07.htm";
        } else if (string.equalsIgnoreCase("reply_4") && n == 5) {
            if (n2 != 111) {
                string2 = "ulu_kaimu_stone_q0125_08.htm";
            } else {
                questState.set("name_of_cruel_god_one_ex", String.valueOf(0), true);
                questState.set("name_of_cruel_god_one", String.valueOf(6), true);
                string2 = "ulu_kaimu_stone_q0125_09.htm";
            }
        } else if (string.equalsIgnoreCase("reply_26") && n == 6) {
            questState.setCond(6);
            questState.set("name_of_cruel_god_one", String.valueOf(7), true);
            questState.playSound("ItemSound.quest_middle");
            string2 = "ulu_kaimu_stone_q0125_20.htm";
        } else if (string.equalsIgnoreCase("reply_27") && n == 7) {
            questState.set("name_of_cruel_god_one_ex", String.valueOf(0), true);
            string2 = "balu_kaimu_stone_q0125_04.htm";
        } else if (string.equalsIgnoreCase("reply_1a") && n == 7) {
            questState.set("name_of_cruel_god_one_ex", String.valueOf(n2 + 1), true);
            string2 = "balu_kaimu_stone_q0125_05.htm";
        } else if (string.equalsIgnoreCase("reply_2a") && n == 7) {
            questState.set("name_of_cruel_god_one_ex", String.valueOf(n2 + 10), true);
            string2 = "balu_kaimu_stone_q0125_06.htm";
        } else if (string.equalsIgnoreCase("reply_3a") && n == 7) {
            questState.set("name_of_cruel_god_one_ex", String.valueOf(n2 + 100), true);
            string2 = "balu_kaimu_stone_q0125_07.htm";
        } else if (string.equalsIgnoreCase("reply_4a") && n == 7) {
            if (n2 != 111) {
                string2 = "balu_kaimu_stone_q0125_08.htm";
            } else {
                questState.set("name_of_cruel_god_one_ex", String.valueOf(0), true);
                questState.set("name_of_cruel_god_one", String.valueOf(8), true);
                string2 = "balu_kaimu_stone_q0125_09.htm";
            }
        } else if (string.equalsIgnoreCase("reply_34") && n == 8) {
            questState.setCond(7);
            questState.set("name_of_cruel_god_one", String.valueOf(9), true);
            questState.playSound("ItemSound.quest_middle");
            string2 = "balu_kaimu_stone_q0125_19.htm";
        } else if (string.equalsIgnoreCase("reply_35") && n == 9) {
            questState.set("name_of_cruel_god_one_ex", String.valueOf(0), true);
            string2 = "jiuta_kaimu_stone_q0125_04.htm";
        } else if (string.equalsIgnoreCase("reply_1b") && n == 9) {
            questState.set("name_of_cruel_god_one_ex", String.valueOf(n2 + 1), true);
            string2 = "jiuta_kaimu_stone_q0125_05.htm";
        } else if (string.equalsIgnoreCase("reply_2b") && n == 9) {
            questState.set("name_of_cruel_god_one_ex", String.valueOf(n2 + 10), true);
            string2 = "jiuta_kaimu_stone_q0125_06.htm";
        } else if (string.equalsIgnoreCase("reply_3b") && n == 9) {
            questState.set("name_of_cruel_god_one_ex", String.valueOf(n2 + 100), true);
            string2 = "jiuta_kaimu_stone_q0125_07.htm";
        } else if (string.equalsIgnoreCase("reply_4b") && n == 9) {
            if (n2 != 111) {
                string2 = "jiuta_kaimu_stone_q0125_08.htm";
            } else {
                questState.set("name_of_cruel_god_one_ex", String.valueOf(0), true);
                questState.set("name_of_cruel_god_one", String.valueOf(10), true);
                string2 = "jiuta_kaimu_stone_q0125_09.htm";
            }
        } else if (string.equalsIgnoreCase("reply_38") && n == 10) {
            questState.set("name_of_cruel_god_one", String.valueOf(11), true);
            string2 = "jiuta_kaimu_stone_q0125_13.htm";
        } else if (string.equalsIgnoreCase("reply_41") && n == 11 && questState.getQuestItemsCount(8782) >= 1L) {
            questState.set("name_of_cruel_god_one", String.valueOf(12), true);
            string2 = "jiuta_kaimu_stone_q0125_19.htm";
        } else if (string.equalsIgnoreCase("reply_42")) {
            questState.set("name_of_cruel_god_one", String.valueOf(13), true);
            string2 = "jiuta_kaimu_stone_q0125_21.htm";
        } else if (string.equalsIgnoreCase("reply_43") && n == 13 && questState.getQuestItemsCount(8782) >= 1L) {
            questState.setCond(8);
            questState.set("name_of_cruel_god_one", String.valueOf(14), true);
            questState.giveItems(8781, 1L);
            questState.takeItems(8782, -1L);
            questState.playSound("ItemSound.quest_middle");
            string2 = "jiuta_kaimu_stone_q0125_23.htm";
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        QuestState questState2 = questState.getPlayer().getQuestState(_124_MeetingTheElroki.class);
        int n = questState.getInt("name_of_cruel_god_one");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 32114) break;
                if (questState2 != null && questState2.isCompleted()) {
                    if (questState.getPlayer().getLevel() >= 76) {
                        string = "mushika_q0125_01.htm";
                        break;
                    }
                    string = "mushika_q0125_02.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                string = "mushika_q0125_04.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n2 == 32114) {
                    if (n < 1) {
                        string = "mushika_q0125_09.htm";
                        break;
                    }
                    if (n == 1) {
                        string = "mushika_q0125_11a.htm";
                        break;
                    }
                    if (n == 2) {
                        string = "mushika_q0125_13.htm";
                        break;
                    }
                    if (n >= 3 && n <= 13) {
                        string = "mushika_q0125_14.htm";
                        break;
                    }
                    if (n != 14 || questState.getQuestItemsCount(8781) < 1L) break;
                    questState.takeItems(8781, -1L);
                    questState.unset("name_of_cruel_god_one");
                    questState.unset("name_of_cruel_god_one_ex");
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(false);
                    string = "mushika_q0125_15.htm";
                    break;
                }
                if (n2 == 32117) {
                    if (n == 2) {
                        string = "shaman_caracawe_q0125_01.htm";
                        break;
                    }
                    if (n < 2) {
                        string = "shaman_caracawe_q0125_02.htm";
                        break;
                    }
                    if (n == 3 && questState.getQuestItemsCount(8779) >= 2L && questState.getQuestItemsCount(8780) >= 2L) {
                        questState.takeItems(8779, -1L);
                        questState.takeItems(8780, -1L);
                        questState.set("name_of_cruel_god_one", String.valueOf(4), true);
                        string = "shaman_caracawe_q0125_11.htm";
                        break;
                    }
                    if (n == 3 && (questState.getQuestItemsCount(8779) < 2L || questState.getQuestItemsCount(8780) < 2L)) {
                        string = "shaman_caracawe_q0125_12.htm";
                        break;
                    }
                    if (n == 4) {
                        string = "shaman_caracawe_q0125_14.htm";
                        break;
                    }
                    if (n == 5) {
                        string = "shaman_caracawe_q0125_18.htm";
                        break;
                    }
                    if (n >= 6 && n < 13) {
                        string = "shaman_caracawe_q0125_19.htm";
                        break;
                    }
                    if (n != 14 || questState.getQuestItemsCount(8781) < 1L) break;
                    string = "shaman_caracawe_q0125_20.htm";
                    break;
                }
                if (n2 == 32119) {
                    if (n == 5) {
                        npcInstance.doCast(SkillTable.getInstance().getInfo(5089, 1), questState.getPlayer(), true);
                        string = "ulu_kaimu_stone_q0125_01.htm";
                        break;
                    }
                    if (n < 5) {
                        string = "ulu_kaimu_stone_q0125_02.htm";
                        break;
                    }
                    if (n > 7) {
                        string = "ulu_kaimu_stone_q0125_03.htm";
                        break;
                    }
                    if (n == 6) {
                        string = "ulu_kaimu_stone_q0125_11.htm";
                        break;
                    }
                    if (n != 7) break;
                    string = "ulu_kaimu_stone_q0125_21.htm";
                    break;
                }
                if (n2 == 32120) {
                    if (n == 7) {
                        npcInstance.doCast(SkillTable.getInstance().getInfo(5089, 1), questState.getPlayer(), true);
                        string = "balu_kaimu_stone_q0125_01.htm";
                        break;
                    }
                    if (n < 7) {
                        string = "balu_kaimu_stone_q0125_02.htm";
                        break;
                    }
                    if (n > 9) {
                        string = "balu_kaimu_stone_q0125_03.htm";
                        break;
                    }
                    if (n == 8) {
                        string = "balu_kaimu_stone_q0125_11.htm";
                        break;
                    }
                    if (n != 9) break;
                    string = "balu_kaimu_stone_q0125_20.htm";
                    break;
                }
                if (n2 != 32121) break;
                if (n == 9) {
                    npcInstance.doCast(SkillTable.getInstance().getInfo(5089, 1), questState.getPlayer(), true);
                    string = "jiuta_kaimu_stone_q0125_01.htm";
                    break;
                }
                if (n < 9) {
                    string = "jiuta_kaimu_stone_q0125_02.htm";
                    break;
                }
                if (n > 14) {
                    string = "jiuta_kaimu_stone_q0125_03.htm";
                    break;
                }
                if (n == 10) {
                    string = "jiuta_kaimu_stone_q0125_11.htm";
                    break;
                }
                if (n == 11) {
                    string = "jiuta_kaimu_stone_q0125_14.htm";
                    break;
                }
                if (n == 12 && questState.getQuestItemsCount(8782) >= 1L) {
                    string = "jiuta_kaimu_stone_q0125_20.htm";
                    break;
                }
                if (n == 13 && questState.getQuestItemsCount(8782) >= 1L) {
                    string = "jiuta_kaimu_stone_q0125_22.htm";
                    break;
                }
                if (n != 14 || questState.getQuestItemsCount(8781) < 1L) break;
                string = "jiuta_kaimu_stone_q0125_24.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getInt("name_of_cruel_god_one");
        int n2 = npcInstance.getNpcId();
        if (n == 3) {
            int n3;
            if (n2 == 22200 || n2 == 22202) {
                int n4 = Rnd.get(1000);
                if (n4 < 661 && questState.getQuestItemsCount(8779) <= 1L) {
                    if (questState.getQuestItemsCount(8779) < 1L) {
                        questState.giveItems(8779, 1L);
                        questState.playSound("ItemSound.quest_itemget");
                    } else if (questState.getQuestItemsCount(8779) >= 1L) {
                        questState.giveItems(8779, 1L);
                        if (questState.getQuestItemsCount(8780) >= 2L) {
                            questState.setCond(4);
                            questState.playSound("ItemSound.quest_middle");
                        }
                    }
                }
            } else if (n2 == 22201) {
                int n5 = Rnd.get(1000);
                if (n5 < 330 && questState.getQuestItemsCount(8779) <= 1L) {
                    if (questState.getQuestItemsCount(8779) < 1L) {
                        questState.giveItems(8779, 1L);
                        questState.playSound("ItemSound.quest_itemget");
                    } else if (questState.getQuestItemsCount(8779) >= 1L) {
                        questState.giveItems(8779, 1L);
                        if (questState.getQuestItemsCount(8780) >= 2L) {
                            questState.setCond(4);
                            questState.playSound("ItemSound.quest_middle");
                        }
                    }
                }
            } else if (n2 == 22203 || n2 == 22205) {
                int n6 = Rnd.get(1000);
                if (n6 < 651 && questState.getQuestItemsCount(8780) <= 1L) {
                    if (questState.getQuestItemsCount(8780) < 1L) {
                        questState.giveItems(8780, 1L);
                        questState.playSound("ItemSound.quest_itemget");
                    } else if (questState.getQuestItemsCount(8780) >= 1L) {
                        questState.giveItems(8780, 1L);
                        if (questState.getQuestItemsCount(8779) >= 2L) {
                            questState.setCond(4);
                            questState.playSound("ItemSound.quest_middle");
                        }
                    }
                }
            } else if (n2 == 22204) {
                int n7 = Rnd.get(1000);
                if (n7 < 326 && questState.getQuestItemsCount(8780) <= 1L) {
                    if (questState.getQuestItemsCount(8780) < 1L) {
                        questState.giveItems(8780, 1L);
                        questState.playSound("ItemSound.quest_itemget");
                    } else if (questState.getQuestItemsCount(8780) >= 1L) {
                        questState.giveItems(8780, 1L);
                        if (questState.getQuestItemsCount(8779) >= 2L) {
                            questState.setCond(4);
                            questState.playSound("ItemSound.quest_middle");
                        }
                    }
                }
            } else if (n2 == 22219 || n2 == 22224) {
                int n8 = Rnd.get(1000);
                if (n8 < 327 && questState.getQuestItemsCount(8779) <= 1L) {
                    if (questState.getQuestItemsCount(8779) < 1L) {
                        questState.giveItems(8779, 1L);
                        questState.playSound("ItemSound.quest_itemget");
                    } else if (questState.getQuestItemsCount(8779) >= 1L) {
                        questState.giveItems(8779, 1L);
                        if (questState.getQuestItemsCount(8780) >= 2L) {
                            questState.setCond(4);
                            questState.playSound("ItemSound.quest_middle");
                        }
                    }
                }
            } else if ((n2 == 22220 || n2 == 22225) && (n3 = Rnd.get(1000)) < 319 && questState.getQuestItemsCount(8780) <= 1L) {
                if (questState.getQuestItemsCount(8780) < 1L) {
                    questState.giveItems(8780, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                } else if (questState.getQuestItemsCount(8780) >= 1L) {
                    questState.giveItems(8780, 1L);
                    if (questState.getQuestItemsCount(8779) >= 2L) {
                        questState.setCond(4);
                        questState.playSound("ItemSound.quest_middle");
                    }
                }
            }
        }
        return null;
    }
}

