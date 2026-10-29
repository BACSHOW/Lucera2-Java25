/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _213_TrialOfSeeker
extends Quest
implements ScriptFile {
    private static final int aKs = 30106;
    private static final int aKt = 30064;
    private static final int aKu = 30684;
    private static final int aKv = 30715;
    private static final int aKw = 30526;
    private static final int aKx = 2647;
    private static final int aKy = 2648;
    private static final int aKz = 2649;
    private static final int aKA = 2650;
    private static final int aKB = 2651;
    private static final int aKC = 2652;
    private static final int aKD = 2653;
    private static final int aKE = 2654;
    private static final int aKF = 2655;
    private static final int aKG = 2656;
    private static final int aKH = 2657;
    private static final int aKI = 2658;
    private static final int aKJ = 2659;
    private static final int aKK = 2660;
    private static final int aKL = 2661;
    private static final int aKM = 2662;
    private static final int aKN = 2663;
    private static final int aKO = 2664;
    private static final int aKP = 2665;
    private static final int aKQ = 2666;
    private static final int aKR = 2667;
    private static final int aKS = 2668;
    private static final int aKT = 2669;
    private static final int aKU = 2670;
    private static final int aKV = 2671;
    private static final int aKW = 2672;
    private static final int aKX = 2673;
    private static final int aKY = 20198;
    private static final int aKZ = 20211;
    private static final int aLa = 20495;
    private static final int aLb = 20080;
    private static final int aLc = 20249;
    private static final int aLd = 20158;
    private static final int aLe = 20234;
    private static final int aLf = 20270;
    private static final int aLg = 20088;
    private static final int aLh = 20580;
    private static final int[][] DROPLIST_COND = new int[][]{{2, 3, 20198, 2648, 2653, 1, 10, 1}, {4, 0, 20211, 2649, 2654, 1, 20, 1}, {4, 0, 20495, 2649, 2655, 1, 20, 1}, {4, 0, 20080, 2649, 2656, 1, 20, 1}, {4, 0, 20249, 2649, 2657, 1, 20, 1}, {9, 10, 20158, 2659, 2660, 10, 30, 1}, {16, 0, 20234, 2667, 2668, 1, 25, 1}, {16, 0, 20270, 2667, 2669, 1, 25, 1}, {16, 0, 20088, 2667, 2670, 1, 25, 1}, {16, 0, 20580, 2667, 2671, 1, 25, 1}};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _213_TrialOfSeeker() {
        super(0);
        this.addStartNpc(30106);
        this.addTalkId(30106);
        this.addTalkId(30064);
        this.addTalkId(30684);
        this.addTalkId(30715);
        this.addTalkId(30526);
        for (int i = 0; i < DROPLIST_COND.length; ++i) {
            this.addKillId(DROPLIST_COND[i][2]);
        }
        this.addQuestItem(2647, 2648, 2649, 2650, 2658, 2651, 2659, 2652, 2661, 2662, 2663, 2664, 2665, 2667, 2666, 2672, 2653, 2654, 2655, 2656, 2657, 2660, 2668, 2669, 2670, 2671);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("dufner_q0213_05a.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            questState.giveItems(2647, 1L);
        } else if (string.equalsIgnoreCase("terry_q0213_03.htm")) {
            questState.giveItems(2648, 1L);
            questState.takeItems(2647, -1L);
            questState.setCond(2);
            questState.setState(2);
        } else if (string.equalsIgnoreCase("terry_q0213_07.htm")) {
            questState.takeItems(2648, -1L);
            questState.takeItems(2653, -1L);
            questState.giveItems(2649, 1L);
            questState.setCond(4);
            questState.setState(2);
        } else if (string.equalsIgnoreCase("terry_q0213_10.htm")) {
            questState.takeItems(2649, -1L);
            questState.takeItems(2654, -1L);
            questState.takeItems(2655, -1L);
            questState.takeItems(2656, -1L);
            questState.takeItems(2657, -1L);
            questState.giveItems(2650, 1L);
            questState.giveItems(2658, 1L);
            questState.setCond(6);
            questState.setState(2);
        } else if (string.equalsIgnoreCase("trader_viktor_q0213_05.htm")) {
            questState.takeItems(2650, -1L);
            questState.giveItems(2651, 1L);
            questState.setCond(7);
            questState.setState(2);
        } else if (string.equalsIgnoreCase("trader_viktor_q0213_11.htm")) {
            questState.takeItems(2650, -1L);
            questState.takeItems(2658, -1L);
            questState.takeItems(2652, -1L);
            questState.giveItems(2659, 1L);
            questState.setCond(9);
            questState.setState(2);
        } else if (string.equalsIgnoreCase("trader_viktor_q0213_15.htm")) {
            questState.takeItems(2659, -1L);
            questState.takeItems(2660, -1L);
            questState.giveItems(2661, 1L);
            questState.giveItems(2662, 1L);
            questState.setCond(11);
            questState.setState(2);
        } else if (string.equalsIgnoreCase("magister_marina_q0213_02.htm")) {
            questState.takeItems(2661, -1L);
            questState.takeItems(2662, -1L);
            questState.giveItems(2663, 1L);
            questState.setCond(12);
            questState.setState(2);
        } else if (string.equalsIgnoreCase("magister_marina_q0213_05.htm")) {
            questState.takeItems(2664, 1L);
            questState.giveItems(2665, 1L);
            questState.setCond(14);
            questState.setState(2);
        } else if (string.equalsIgnoreCase("terry_q0213_18.htm")) {
            if (questState.getPlayer().getLevel() < 36) {
                string2 = "terry_q0213_17.htm";
                questState.takeItems(2665, -1L);
                questState.giveItems(2666, 1L);
            } else {
                string2 = "terry_q0213_18.htm";
                questState.giveItems(2667, 1L);
                questState.takeItems(2665, -1L);
                questState.setCond(16);
                questState.setState(2);
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getCond();
        if (n == 30106) {
            if (questState.getQuestItemsCount(2673) != 0L) {
                string = "completed";
                questState.exitCurrentQuest(true);
            } else if (n2 == 0 && questState.getQuestItemsCount(2672) == 0L) {
                if (questState.getPlayer().getClassId().ordinal() == 7 || questState.getPlayer().getClassId().ordinal() == 22 || questState.getPlayer().getClassId().ordinal() == 35) {
                    if (questState.getPlayer().getLevel() < 35) {
                        string = "dufner_q0213_02.htm";
                        questState.exitCurrentQuest(true);
                    } else {
                        string = "dufner_q0213_03.htm";
                    }
                } else {
                    string = "dufner_q0213_00.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (questState.getQuestItemsCount(2647) == 1L && questState.getQuestItemsCount(2672) == 0L) {
                string = "dufner_q0213_06.htm";
            } else if (questState.getQuestItemsCount(2647) == 0L && questState.getQuestItemsCount(2672) == 0L) {
                string = "dufner_q0213_07.htm";
            } else if (questState.getQuestItemsCount(2672) != 0L) {
                if (!questState.getPlayer().getVarB("prof2.1")) {
                    questState.giveItems(7562, 8L);
                    questState.addExpAndSp(72126L, 11000L);
                    questState.getPlayer().setVar("prof2.1", "1", -1L);
                }
                string = "dufner_q0213_08.htm";
                questState.playSound("ItemSound.quest_finish");
                questState.takeItems(2672, -1L);
                questState.giveItems(2673, 1L);
                questState.exitCurrentQuest(true);
            }
        } else if (n == 30064) {
            if (n2 == 1) {
                string = "terry_q0213_01.htm";
            } else if (n2 == 2) {
                string = "terry_q0213_04.htm";
            } else if (n2 == 3 && questState.getQuestItemsCount(2653) == 0L) {
                string = "terry_q0213_04.htm";
                questState.setCond(2);
            } else if (n2 == 3) {
                string = "terry_q0213_05.htm";
            } else if (n2 == 4) {
                string = "terry_q0213_07.htm";
            } else if (n2 == 5 && questState.getQuestItemsCount(2654) != 0L && questState.getQuestItemsCount(2655) != 0L && questState.getQuestItemsCount(2656) != 0L && questState.getQuestItemsCount(2657) != 0L) {
                string = "terry_q0213_09.htm";
            } else if (n2 == 5) {
                string = "terry_q0213_07.htm";
                questState.setCond(4);
            } else if (n2 == 6) {
                string = "terry_q0213_11.htm";
            } else if (n2 == 7) {
                questState.takeItems(2651, -1L);
                questState.giveItems(2652, 1L);
                string = "terry_q0213_12.htm";
                questState.setCond(8);
                questState.setState(2);
            } else if (n2 == 8) {
                string = "terry_q0213_13.htm";
            } else if (n2 > 8 && n2 < 14) {
                string = "terry_q0213_14.htm";
            } else if (n2 == 14 && questState.getQuestItemsCount(2665) > 0L) {
                string = "terry_q0213_15.htm";
            } else if ((n2 == 14 || n2 == 15) && questState.getQuestItemsCount(2666) > 0L) {
                if (questState.getPlayer().getLevel() < 36) {
                    string = "terry_q0213_20.htm";
                } else {
                    string = "terry_q0213_21.htm";
                    questState.takeItems(2666, -1L);
                    questState.giveItems(2667, 1L);
                    questState.setCond(16);
                    questState.setState(2);
                }
            } else if (n2 == 15 || n2 == 16) {
                string = "terry_q0213_22.htm";
            } else if (n2 == 17) {
                if (questState.getQuestItemsCount(2668) != 0L && questState.getQuestItemsCount(2669) != 0L && questState.getQuestItemsCount(2670) != 0L && questState.getQuestItemsCount(2671) != 0L) {
                    string = "terry_q0213_23.htm";
                    questState.takeItems(2667, -1L);
                    questState.takeItems(2668, -1L);
                    questState.takeItems(2669, -1L);
                    questState.takeItems(2670, -1L);
                    questState.takeItems(2671, -1L);
                    questState.giveItems(2672, 1L);
                    questState.setCond(0);
                } else {
                    string = "terry_q0213_22.htm";
                    questState.setCond(16);
                }
            }
        } else if (n == 30684) {
            if (n2 == 6) {
                string = "trader_viktor_q0213_01.htm";
            } else if (n2 == 8) {
                string = "trader_viktor_q0213_12.htm";
            } else if (n2 == 9) {
                string = "trader_viktor_q0213_13.htm";
            } else if (n2 == 10 && questState.getQuestItemsCount(2660) >= 10L) {
                string = "trader_viktor_q0213_14.htm";
            } else if (n2 == 10) {
                questState.setCond(9);
                string = "trader_viktor_q0213_13.htm";
            }
        } else if (n == 30715) {
            if (n2 == 11) {
                string = "magister_marina_q0213_01.htm";
            } else if (n2 == 12) {
                string = "magister_marina_q0213_03.htm";
            } else if (n2 == 13) {
                string = "magister_marina_q0213_04.htm";
            } else if (n2 > 13) {
                string = "magister_marina_q0213_06.htm";
            }
        } else if (n == 30526) {
            if (n2 == 12) {
                string = "blacksmith_bronp_q0213_01.htm";
                questState.takeItems(2663, 1L);
                questState.giveItems(2664, 1L);
                questState.setCond(13);
                questState.setState(2);
            } else if (n2 == 13) {
                string = "blacksmith_bronp_q0213_02.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        for (int i = 0; i < DROPLIST_COND.length; ++i) {
            if (n2 != DROPLIST_COND[i][0] || n != DROPLIST_COND[i][2] || DROPLIST_COND[i][3] != 0 && questState.getQuestItemsCount(DROPLIST_COND[i][3]) <= 0L) continue;
            if (DROPLIST_COND[i][5] == 0) {
                questState.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][6]);
                continue;
            }
            if (!questState.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][7], DROPLIST_COND[i][5], DROPLIST_COND[i][6]) || DROPLIST_COND[i][1] == n2 || DROPLIST_COND[i][1] == 0) continue;
            questState.setCond(DROPLIST_COND[i][1]);
            questState.setState(2);
        }
        if (n2 == 4 && questState.getQuestItemsCount(2654) != 0L && questState.getQuestItemsCount(2655) != 0L && questState.getQuestItemsCount(2656) != 0L && questState.getQuestItemsCount(2657) != 0L) {
            questState.setCond(5);
            questState.setState(2);
        } else if (n2 == 16 && questState.getQuestItemsCount(2668) != 0L && questState.getQuestItemsCount(2669) != 0L && questState.getQuestItemsCount(2670) != 0L && questState.getQuestItemsCount(2671) != 0L) {
            questState.setCond(17);
            questState.setState(2);
        }
        return null;
    }
}

