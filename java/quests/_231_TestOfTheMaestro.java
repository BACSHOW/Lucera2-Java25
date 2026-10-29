/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _231_TestOfTheMaestro
extends Quest
implements ScriptFile {
    private static final int aQO = 30531;
    private static final int aQQ = 30533;
    private static final int Arin = 30536;
    private static final int aJD = 30535;
    private static final int aQP = 30532;
    private static final int aXj = 30671;
    private static final int aXk = 30675;
    private static final int aXl = 30672;
    private static final int Toma = 30556;
    private static final int aXm = 30673;
    private static final int aXn = 2864;
    private static final int aXo = 2865;
    private static final int aXp = 2866;
    private static final int aXq = 2868;
    private static final int aXr = 2869;
    private static final int aXs = 2870;
    private static final int aXt = 2871;
    private static final int aXu = 2872;
    private static final int aXv = 2873;
    private static final int aXw = 2874;
    private static final int aXx = 2875;
    private static final int aXy = 2876;
    private static final int aXz = 2877;
    private static final int aXA = 2878;
    private static final int aXB = 2916;
    private static final int aXC = 7562;
    private static final int aXD = 2867;
    private static final int aXE = 27133;
    private static final int aXF = 20225;
    private static final int aXG = 20229;
    private static final int aPn = 20233;
    private static final int aXH = 20150;
    private static final int[][] DROPLIST_COND = new int[][]{{4, 5, 27133, 0, 2870, 1, 100, 1}, {13, 0, 20225, 0, 2878, 10, 100, 1}, {13, 0, 20229, 0, 2876, 10, 100, 1}, {13, 0, 20233, 0, 2877, 10, 100, 1}};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _231_TestOfTheMaestro() {
        super(0);
        this.addStartNpc(30531);
        this.addTalkId(30533);
        this.addTalkId(30536);
        this.addTalkId(30535);
        this.addTalkId(30532);
        this.addTalkId(30671);
        this.addTalkId(30675);
        this.addTalkId(30672);
        this.addTalkId(30556);
        this.addTalkId(30673);
        for (int i = 0; i < DROPLIST_COND.length; ++i) {
            this.addKillId(DROPLIST_COND[i][2]);
            this.addQuestItem(DROPLIST_COND[i][4]);
        }
        this.addQuestItem(2869, 2868, 2871, 2916, 2872, 2873, 2875, 2864, 2865, 2866, 2874);
    }

    public void recommendationCount(QuestState questState) {
        if (questState.getQuestItemsCount(2866) != 0L && questState.getQuestItemsCount(2865) != 0L && questState.getQuestItemsCount(2864) != 0L) {
            questState.setCond(17);
        }
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("30531-04.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("30533-02.htm")) {
            questState.setCond(2);
            questState.setState(2);
        } else if (string.equalsIgnoreCase("30671-02.htm")) {
            questState.giveItems(2869, 1L);
            questState.setCond(3);
            questState.setState(2);
        } else if (string.equalsIgnoreCase("30556-05.htm")) {
            questState.takeItems(2871, -1L);
            questState.giveItems(2916, 1L);
            questState.getPlayer().teleToLocation(140352, -194133, -3146);
            questState.addSpawn(20150, 140333, -194153, -3138, 30000);
            questState.addSpawn(20150, 140395, -194147, -3146, 30000);
            questState.addSpawn(20150, 140304, -194082, -3157, 30000);
            questState.setCond(9);
            questState.setState(2);
            string2 = "master_toma_q0231_05.htm";
        } else if (string.equalsIgnoreCase("30673-04.htm")) {
            questState.takeItems(2878, -1L);
            questState.takeItems(2876, -1L);
            questState.takeItems(2877, -1L);
            questState.takeItems(2875, -1L);
            questState.giveItems(2874, 1L);
            questState.setCond(15);
            questState.setState(2);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getCond();
        if (n == 30531) {
            if (questState.getQuestItemsCount(2867) != 0L) {
                string = "completed";
                questState.exitCurrentQuest(true);
            } else if (n2 == 0) {
                if (questState.getPlayer().getClassId().getId() == 56) {
                    if (questState.getPlayer().getLevel() >= 39) {
                        string = "30531-03.htm";
                    } else {
                        string = "30531-01.htm";
                        questState.exitCurrentQuest(true);
                    }
                } else {
                    string = "30531-02.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 >= 1 && n2 <= 16) {
                string = "30531-05.htm";
            } else if (n2 == 17) {
                if (!questState.getPlayer().getVarB("prof2.3")) {
                    questState.giveItems(7562, 8L);
                    questState.addExpAndSp(154499L, 37500L);
                    questState.getPlayer().setVar("prof2.3", "1", -1L);
                }
                string = "30531-06.htm";
                questState.takeItems(2864, -1L);
                questState.takeItems(2865, -1L);
                questState.takeItems(2866, -1L);
                questState.giveItems(2867, 1L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            }
        } else if (n == 30533) {
            if ((n2 == 1 || n2 == 11 || n2 == 16) && questState.getQuestItemsCount(2864) == 0L) {
                string = "30533-01.htm";
            } else if (n2 == 2) {
                string = "30533-03.htm";
            } else if (n2 == 6) {
                questState.takeItems(2868, -1L);
                questState.giveItems(2864, 1L);
                string = "30533-04.htm";
                questState.setCond(7);
                this.recommendationCount(questState);
                questState.setState(2);
            } else if (n2 == 7 || n2 == 17) {
                string = "30533-05.htm";
            }
        } else if (n == 30536) {
            if ((n2 == 1 || n2 == 7 || n2 == 16) && questState.getQuestItemsCount(2866) == 0L) {
                questState.giveItems(2871, 1L);
                string = "30536-01.htm";
                questState.setCond(8);
                questState.setState(2);
            } else if (n2 == 8) {
                string = "30536-02.htm";
            } else if (n2 == 10) {
                questState.takeItems(2872, -1L);
                questState.giveItems(2866, 1L);
                string = "30536-03.htm";
                questState.setCond(11);
                this.recommendationCount(questState);
                questState.setState(2);
            } else if (n2 == 11 || n2 == 17) {
                string = "30536-04.htm";
            }
        } else if (n == 30535) {
            if ((n2 == 1 || n2 == 7 || n2 == 11) && questState.getQuestItemsCount(2865) == 0L) {
                questState.giveItems(2873, 1L);
                string = "30535-01.htm";
                questState.setCond(12);
                questState.setState(2);
            } else if (n2 == 12) {
                string = "30535-02.htm";
            } else if (n2 == 15) {
                questState.takeItems(2874, 1L);
                questState.giveItems(2865, 1L);
                questState.setCond(16);
                string = "30535-03.htm";
                this.recommendationCount(questState);
                questState.setState(2);
            } else if (n2 > 15) {
                string = "30535-04.htm";
            }
        } else if (n == 30671) {
            if (n2 == 2) {
                string = "30671-01.htm";
            } else if (n2 == 3) {
                string = "30671-03.htm";
            } else if (n2 == 5) {
                questState.takeItems(2870, -1L);
                questState.takeItems(2869, -1L);
                questState.giveItems(2868, 1L);
                string = "30671-04.htm";
                questState.setCond(6);
                questState.setState(2);
            } else if (n2 == 6) {
                string = "30671-05.htm";
            }
        } else if (n == 30672 && n2 == 3) {
            string = "30672-01.htm";
        } else if (n == 30675 && n2 == 3) {
            string = "30675-01.htm";
            questState.setCond(4);
            questState.setState(2);
        } else if (n == 30556) {
            if (n2 == 8) {
                string = "30556-01.htm";
            } else if (n2 == 9) {
                questState.takeItems(2916, -1L);
                questState.giveItems(2872, 5L);
                string = "30556-06.htm";
                questState.setCond(10);
                questState.setState(2);
            } else if (n2 == 10) {
                string = "30556-07.htm";
            }
        } else if (n == 30673) {
            if (n2 == 12) {
                questState.takeItems(2873, -1L);
                questState.giveItems(2875, 1L);
                questState.setCond(13);
                string = "30673-01.htm";
            } else if (n2 == 13) {
                string = "30673-02.htm";
            } else if (n2 == 14) {
                string = "30673-03.htm";
            } else if (n2 == 15) {
                string = "30673-05.htm";
            }
        } else if (n == 30532 && (n2 == 1 || n2 == 7 || n2 == 11 || n2 == 16)) {
            string = "30532-01.htm";
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
        if (n2 == 13 && questState.getQuestItemsCount(2878) >= 10L && questState.getQuestItemsCount(2876) >= 10L && questState.getQuestItemsCount(2877) >= 10L) {
            questState.setCond(14);
            questState.setState(2);
        }
        return null;
    }
}

