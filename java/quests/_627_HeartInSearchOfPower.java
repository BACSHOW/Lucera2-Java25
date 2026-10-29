/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _627_HeartInSearchOfPower
extends Quest
implements ScriptFile {
    private static final int bPR = 31518;
    private static final int bPS = 31519;
    private static final int bDX = 21520;
    private static final int bDZ = 21523;
    private static final int bPT = 21524;
    private static final int bPU = 21525;
    private static final int bEc = 21526;
    private static final int bPV = 21529;
    private static final int bDY = 21530;
    private static final int bPW = 21531;
    private static final int bPX = 21532;
    private static final int bPY = 21535;
    private static final int bDW = 21536;
    private static final int bDU = 21539;
    private static final int bDV = 21540;
    private static final int bPZ = 21658;
    private static final int bQa = 7170;
    private static final int bQb = 7171;
    private static final int bQc = 7172;
    private static final int bQd = 4041;
    private static final int boG = 4042;
    private static final int buT = 4043;
    private static final int buU = 4044;

    public _627_HeartInSearchOfPower() {
        super(1);
        this.addStartNpc(31518);
        this.addTalkId(31519);
        this.addQuestItem(7171, 7170, 7172);
        this.addKillId(21520, 21523, 21524, 21525, 21526, 21529, 21530, 21531, 21532, 21535, 21536, 21539, 21540, 21658);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getInt("temptation_of_power_cookie");
        int n2 = npcInstance.getNpcId();
        if (n2 == 31518) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("temptation_of_power", String.valueOf(11), true);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "dark_necromancer_q0627_0104.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=627&reply=1") && n == 1) {
                if (questState.getQuestItemsCount(7171) >= 300L) {
                    questState.setCond(3);
                    questState.set("temptation_of_power", String.valueOf(21), true);
                    questState.takeItems(7171, 300L);
                    questState.giveItems(7170, 1L, false);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "dark_necromancer_q0627_0201.htm";
                } else {
                    string2 = "dark_necromancer_q0627_0202.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=627&reply=3") && n == 3) {
                string2 = "dark_necromancer_q0627_0401.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=627&reply=11") && n == 3) {
                if (questState.getQuestItemsCount(7172) >= 1L) {
                    questState.takeItems(7172, -1L);
                    questState.giveItems(57, 100000L, true);
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(true);
                    string2 = "dark_necromancer_q0627_0402.htm";
                } else {
                    string2 = "dark_necromancer_q0627_0403.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=627&reply=12") && n == 3) {
                if (questState.getQuestItemsCount(7172) >= 1L) {
                    questState.takeItems(7172, -1L);
                    questState.giveItems(4043, 13L, true);
                    questState.giveItems(57, 6400L, true);
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(true);
                    string2 = "dark_necromancer_q0627_0402.htm";
                } else {
                    string2 = "dark_necromancer_q0627_0403.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=627&reply=13") && n == 3) {
                if (questState.getQuestItemsCount(7172) >= 1L) {
                    questState.takeItems(7172, -1L);
                    questState.giveItems(4044, 13L, true);
                    questState.giveItems(57, 6400L, true);
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(true);
                    string2 = "dark_necromancer_q0627_0402.htm";
                } else {
                    string2 = "dark_necromancer_q0627_0403.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=627&reply=14") && n == 3) {
                if (questState.getQuestItemsCount(7172) >= 1L) {
                    questState.takeItems(7172, -1L);
                    questState.giveItems(4042, 6L, true);
                    questState.giveItems(57, 13600L, true);
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(true);
                    string2 = "dark_necromancer_q0627_0402.htm";
                } else {
                    string2 = "dark_necromancer_q0627_0403.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=627&reply=15") && n == 3) {
                if (questState.getQuestItemsCount(7172) >= 1L) {
                    questState.takeItems(7172, -1L);
                    questState.giveItems(4041, 3L, true);
                    questState.giveItems(57, 17200L, true);
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(true);
                    string2 = "dark_necromancer_q0627_0402.htm";
                } else {
                    string2 = "dark_necromancer_q0627_0403.htm";
                }
            }
        } else if (n2 == 31519 && string.equalsIgnoreCase("menu_select?ask=627&reply=1") && n == 2) {
            if (questState.getQuestItemsCount(7170) >= 1L) {
                questState.setCond(4);
                questState.set("temptation_of_power", String.valueOf(31), true);
                questState.takeItems(7170, 1L);
                questState.giveItems(7172, 1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "enfeux_q0627_0301.htm";
            } else {
                string2 = "enfeux_q0627_0302.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("temptation_of_power");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 31518) break;
                if (questState.getPlayer().getLevel() >= 60) {
                    string = "dark_necromancer_q0627_0101.htm";
                    break;
                }
                questState.exitCurrentQuest(true);
                string = "dark_necromancer_q0627_0103.htm";
                break;
            }
            case 2: {
                if (n2 == 31518) {
                    if (n >= 11 && n <= 12) {
                        if (n == 12 && questState.getQuestItemsCount(7171) >= 300L) {
                            questState.set("temptation_of_power_cookie", String.valueOf(1), true);
                            string = "dark_necromancer_q0627_0105.htm";
                            break;
                        }
                        string = "dark_necromancer_q0627_0106.htm";
                        break;
                    }
                    if (n == 21) {
                        string = "dark_necromancer_q0627_0203.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(7172) < 1L || n != 31) break;
                    questState.set("temptation_of_power_cookie", String.valueOf(3), true);
                    string = "dark_necromancer_q0627_0301.htm";
                    break;
                }
                if (n2 != 31519) break;
                if (questState.getQuestItemsCount(7170) >= 1L && n == 21) {
                    questState.set("temptation_of_power_cookie", String.valueOf(2), true);
                    string = "enfeux_q0627_0201.htm";
                    break;
                }
                if (n != 31) break;
                string = "enfeux_q0627_0303.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n;
        int n2 = npcInstance.getNpcId();
        if (n2 == 21520) {
            int n3 = Rnd.get(1000);
            if (n3 < 661) {
                if (questState.getQuestItemsCount(7171) + 1L >= 300L) {
                    if (questState.getQuestItemsCount(7171) <= 300L) {
                        questState.setCond(2);
                        questState.set("temptation_of_power", String.valueOf(12), true);
                        questState.giveItems(7171, 300L - questState.getQuestItemsCount(7171));
                        questState.playSound("ItemSound.quest_middle");
                    }
                } else {
                    questState.giveItems(7171, 1L, true);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 21523) {
            int n4 = Rnd.get(1000);
            if (n4 < 668) {
                if (questState.getQuestItemsCount(7171) + 1L >= 300L) {
                    if (questState.getQuestItemsCount(7171) <= 300L) {
                        questState.setCond(2);
                        questState.set("temptation_of_power", String.valueOf(12), true);
                        questState.giveItems(7171, 300L - questState.getQuestItemsCount(7171));
                        questState.playSound("ItemSound.quest_middle");
                    }
                } else {
                    questState.giveItems(7171, 1L, true);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 21524 || n2 == 21525) {
            int n5 = Rnd.get(1000);
            if (n5 < 714) {
                if (questState.getQuestItemsCount(7171) + 1L >= 300L) {
                    if (questState.getQuestItemsCount(7171) <= 300L) {
                        questState.setCond(2);
                        questState.set("temptation_of_power", String.valueOf(12), true);
                        questState.giveItems(7171, 300L - questState.getQuestItemsCount(7171));
                        questState.playSound("ItemSound.quest_middle");
                    }
                } else {
                    questState.giveItems(7171, 1L, true);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 21526) {
            int n6 = Rnd.get(1000);
            if (n6 < 796) {
                if (questState.getQuestItemsCount(7171) + 1L >= 300L) {
                    if (questState.getQuestItemsCount(7171) <= 300L) {
                        questState.setCond(2);
                        questState.set("temptation_of_power", String.valueOf(12), true);
                        questState.giveItems(7171, 300L - questState.getQuestItemsCount(7171));
                        questState.playSound("ItemSound.quest_middle");
                    }
                } else {
                    questState.giveItems(7171, 1L, true);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 21529) {
            int n7 = Rnd.get(1000);
            if (n7 < 659) {
                if (questState.getQuestItemsCount(7171) + 1L >= 300L) {
                    if (questState.getQuestItemsCount(7171) <= 300L) {
                        questState.setCond(2);
                        questState.set("temptation_of_power", String.valueOf(12), true);
                        questState.giveItems(7171, 300L - questState.getQuestItemsCount(7171));
                        questState.playSound("ItemSound.quest_middle");
                    }
                } else {
                    questState.giveItems(7171, 1L, true);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 21530) {
            int n8 = Rnd.get(1000);
            if (n8 < 704) {
                if (questState.getQuestItemsCount(7171) + 1L >= 300L) {
                    if (questState.getQuestItemsCount(7171) <= 300L) {
                        questState.setCond(2);
                        questState.set("temptation_of_power", String.valueOf(12), true);
                        questState.giveItems(7171, 300L - questState.getQuestItemsCount(7171));
                        questState.playSound("ItemSound.quest_middle");
                    }
                } else {
                    questState.giveItems(7171, 1L, true);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 21531 || n2 == 21658) {
            int n9 = Rnd.get(1000);
            if (n9 < 791) {
                if (questState.getQuestItemsCount(7171) + 1L >= 300L) {
                    if (questState.getQuestItemsCount(7171) <= 300L) {
                        questState.setCond(2);
                        questState.set("temptation_of_power", String.valueOf(12), true);
                        questState.giveItems(7171, 300L - questState.getQuestItemsCount(7171));
                        questState.playSound("ItemSound.quest_middle");
                    }
                } else {
                    questState.giveItems(7171, 1L, true);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 21532) {
            int n10 = Rnd.get(1000);
            if (n10 < 820) {
                if (questState.getQuestItemsCount(7171) + 1L >= 300L) {
                    if (questState.getQuestItemsCount(7171) <= 300L) {
                        questState.setCond(2);
                        questState.set("temptation_of_power", String.valueOf(12), true);
                        questState.giveItems(7171, 300L - questState.getQuestItemsCount(7171));
                        questState.playSound("ItemSound.quest_middle");
                    }
                } else {
                    questState.giveItems(7171, 1L, true);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 21535) {
            int n11 = Rnd.get(1000);
            if (n11 < 827) {
                if (questState.getQuestItemsCount(7171) + 1L >= 300L) {
                    if (questState.getQuestItemsCount(7171) <= 300L) {
                        questState.setCond(2);
                        questState.set("temptation_of_power", String.valueOf(12), true);
                        questState.giveItems(7171, 300L - questState.getQuestItemsCount(7171));
                        questState.playSound("ItemSound.quest_middle");
                    }
                } else {
                    questState.giveItems(7171, 1L, true);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 21536) {
            int n12 = Rnd.get(1000);
            if (n12 < 798) {
                if (questState.getQuestItemsCount(7171) + 1L >= 300L) {
                    if (questState.getQuestItemsCount(7171) <= 300L) {
                        questState.setCond(2);
                        questState.set("temptation_of_power", String.valueOf(12), true);
                        questState.giveItems(7171, 300L - questState.getQuestItemsCount(7171));
                        questState.playSound("ItemSound.quest_middle");
                    }
                } else {
                    questState.giveItems(7171, 1L, true);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if ((n2 == 21539 || n2 == 21540) && (n = Rnd.get(1000)) < 875) {
            if (questState.getQuestItemsCount(7171) + 1L >= 300L) {
                if (questState.getQuestItemsCount(7171) <= 300L) {
                    questState.setCond(2);
                    questState.set("temptation_of_power", String.valueOf(12), true);
                    questState.giveItems(7171, 300L - questState.getQuestItemsCount(7171));
                    questState.playSound("ItemSound.quest_middle");
                }
            } else {
                questState.giveItems(7171, 1L, true);
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

