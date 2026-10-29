/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _222_TestOfDuelist
extends Quest
implements ScriptFile {
    private static final int aRS = 30623;
    private static final int aRT = 2763;
    private static final int aRU = 2764;
    private static final int aRV = 2765;
    private static final int aRW = 2766;
    private static final int aRX = 2767;
    private static final int aRY = 2768;
    private static final int aRZ = 2769;
    private static final int aSa = 2770;
    private static final int aSb = 2771;
    private static final int aSc = 2772;
    private static final int aSd = 2773;
    private static final int aSe = 2774;
    private static final int aSf = 2775;
    private static final int aSg = 2776;
    private static final int aSh = 2777;
    private static final int aSi = 2778;
    private static final int aSj = 2779;
    private static final int aSk = 2780;
    private static final int aSl = 2781;
    private static final int aSm = 2782;
    private static final int aSn = 2783;
    private static final int aSo = 2762;
    private static final int aSp = 20085;
    private static final int aSq = 20090;
    private static final int aLe = 20234;
    private static final int aPo = 20202;
    private static final int aLf = 20270;
    private static final int FetteredSoul = 20552;
    private static final int aPv = 20582;
    private static final int aSr = 20564;
    private static final int aQD = 20601;
    private static final int aQE = 20602;
    private static final int aSs = 20214;
    private static final int aSt = 20217;
    private static final int aLA = 20554;
    private static final int aQC = 20588;
    private static final int aSu = 20604;
    private static final int[][] DROPLIST_COND = new int[][]{{2, 0, 20085, 0, 2768, 10, 70, 1}, {2, 0, 20090, 0, 2769, 10, 70, 1}, {2, 0, 20234, 0, 2770, 10, 70, 1}, {2, 0, 20202, 0, 2771, 10, 70, 1}, {2, 0, 20270, 0, 2772, 10, 70, 1}, {2, 0, 20552, 0, 2773, 10, 70, 1}, {2, 0, 20582, 0, 2774, 10, 70, 1}, {2, 0, 20564, 0, 2775, 10, 70, 1}, {2, 0, 20601, 0, 2776, 10, 70, 1}, {2, 0, 20602, 0, 2777, 10, 70, 1}, {4, 0, 20214, 0, 2779, 3, 70, 1}, {4, 0, 20217, 0, 2780, 3, 70, 1}, {4, 0, 20554, 0, 2781, 3, 70, 1}, {4, 0, 20588, 0, 2782, 3, 70, 1}, {4, 0, 20604, 0, 2783, 3, 70, 1}};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _222_TestOfDuelist() {
        super(0);
        this.addStartNpc(30623);
        for (int i = 0; i < DROPLIST_COND.length; ++i) {
            this.addKillId(DROPLIST_COND[i][2]);
            this.addQuestItem(DROPLIST_COND[i][4]);
        }
        this.addQuestItem(2763, 2764, 2765, 2766, 2767, 2778);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("30623-04.htm") && questState.getPlayer().getRace() == Race.orc) {
            string2 = "30623-05.htm";
        } else if (string.equalsIgnoreCase("30623-07.htm")) {
            questState.setCond(2);
            questState.setState(2);
            questState.giveItems(2763, 1L);
            questState.giveItems(2764, 1L);
            questState.giveItems(2765, 1L);
            questState.giveItems(2766, 1L);
            questState.giveItems(2767, 1L);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("30623-16.htm")) {
            questState.takeItems(2768, -1L);
            questState.takeItems(2769, -1L);
            questState.takeItems(2770, -1L);
            questState.takeItems(2771, -1L);
            questState.takeItems(2772, -1L);
            questState.takeItems(2773, -1L);
            questState.takeItems(2774, -1L);
            questState.takeItems(2775, -1L);
            questState.takeItems(2776, -1L);
            questState.takeItems(2777, -1L);
            questState.takeItems(2763, -1L);
            questState.takeItems(2764, -1L);
            questState.takeItems(2765, -1L);
            questState.takeItems(2766, -1L);
            questState.takeItems(2767, -1L);
            questState.giveItems(2778, 1L);
            questState.setCond(4);
            questState.setState(2);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getCond();
        if (n == 30623) {
            if (questState.getQuestItemsCount(2762) != 0L) {
                string = "completed";
                questState.exitCurrentQuest(true);
            } else if (n2 == 0) {
                if (questState.getPlayer().getClassId().getId() == 1 || questState.getPlayer().getClassId().getId() == 47 || questState.getPlayer().getClassId().getId() == 19 || questState.getPlayer().getClassId().getId() == 32) {
                    if (questState.getPlayer().getLevel() >= 39) {
                        string = "30623-03.htm";
                    } else {
                        string = "30623-01.htm";
                        questState.exitCurrentQuest(true);
                    }
                } else {
                    string = "30623-02.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 2) {
                string = "30623-14.htm";
            } else if (n2 == 3) {
                string = "30623-13.htm";
            } else if (n2 == 4) {
                string = "30623-17.htm";
            } else if (n2 == 5) {
                questState.giveItems(2762, 1L);
                if (!questState.getPlayer().getVarB("prof2.3")) {
                    questState.giveItems(7562, 8L);
                    questState.addExpAndSp(91457L, 2500L);
                    questState.getPlayer().setVar("prof2.3", "1", -1L);
                }
                string = "30623-18.htm";
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
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
        if (n2 == 2 && questState.getQuestItemsCount(2768) >= 10L && questState.getQuestItemsCount(2769) >= 10L && questState.getQuestItemsCount(2770) >= 10L && questState.getQuestItemsCount(2771) >= 10L && questState.getQuestItemsCount(2772) >= 10L && questState.getQuestItemsCount(2773) >= 10L && questState.getQuestItemsCount(2774) >= 10L && questState.getQuestItemsCount(2775) >= 10L && questState.getQuestItemsCount(2776) >= 10L && questState.getQuestItemsCount(2777) >= 10L) {
            questState.setCond(3);
            questState.setState(2);
        } else if (n2 == 4 && questState.getQuestItemsCount(2779) >= 3L && questState.getQuestItemsCount(2780) >= 3L && questState.getQuestItemsCount(2783) >= 3L && questState.getQuestItemsCount(2781) >= 3L && questState.getQuestItemsCount(2782) >= 3L) {
            questState.setCond(5);
            questState.setState(2);
        }
        return null;
    }
}

