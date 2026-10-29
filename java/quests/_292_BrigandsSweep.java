/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _292_BrigandsSweep
extends Quest
implements ScriptFile {
    private static int aQP = 30532;
    private static int aQQ = 30533;
    private static int bex = 20322;
    private static int bey = 20323;
    private static int bez = 20324;
    private static int beA = 20327;
    private static int beB = 20528;
    private static int beC = 1483;
    private static int beD = 1484;
    private static int beE = 1485;
    private static int beF = 1486;
    private static int beG = 1487;
    private static int beH = 10;
    private static final int[][] DROPLIST_COND = new int[][]{{1, 0, bex, 0, beC, 0, 40, 1}, {1, 0, bey, 0, beC, 0, 40, 1}, {1, 0, beA, 0, beC, 0, 40, 1}, {1, 0, bez, 0, beD, 0, 40, 1}, {1, 0, beB, 0, beE, 0, 40, 1}};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _292_BrigandsSweep() {
        super(0);
        this.addStartNpc(aQP);
        this.addTalkId(aQQ);
        for (int i = 0; i < DROPLIST_COND.length; ++i) {
            this.addKillId(DROPLIST_COND[i][2]);
        }
        this.addQuestItem(beF);
        this.addQuestItem(beG);
        this.addQuestItem(beC);
        this.addQuestItem(beD);
        this.addQuestItem(beE);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        if (string.equalsIgnoreCase("elder_spiron_q0292_03.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("elder_spiron_q0292_06.htm")) {
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        }
        return string;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getCond();
        if (n == aQP) {
            if (n2 == 0) {
                if (questState.getPlayer().getRace() != Race.dwarf) {
                    string = "elder_spiron_q0292_00.htm";
                    questState.exitCurrentQuest(true);
                } else if (questState.getPlayer().getLevel() < 5) {
                    string = "elder_spiron_q0292_01.htm";
                    questState.exitCurrentQuest(true);
                } else {
                    string = "elder_spiron_q0292_02.htm";
                }
            } else if (n2 == 1) {
                long l = questState.getQuestItemsCount(beC) * 12L + questState.getQuestItemsCount(beD) * 36L + questState.getQuestItemsCount(beE) * 33L + questState.getQuestItemsCount(beG) * 100L;
                if (l == 0L) {
                    return "elder_spiron_q0292_04.htm";
                }
                string = questState.getQuestItemsCount(beG) != 0L ? "elder_spiron_q0292_10.htm" : (questState.getQuestItemsCount(beF) == 0L ? "elder_spiron_q0292_05.htm" : (questState.getQuestItemsCount(beF) == 1L ? "elder_spiron_q0292_08.htm" : "elder_spiron_q0292_09.htm"));
                questState.takeItems(beC, -1L);
                questState.takeItems(beD, -1L);
                questState.takeItems(beE, -1L);
                questState.takeItems(beG, -1L);
                questState.giveItems(57, l);
            }
        } else if (n == aQQ && n2 == 1) {
            if (questState.getQuestItemsCount(beG) == 0L) {
                string = "balanki_q0292_01.htm";
            } else {
                questState.takeItems(beG, -1L);
                questState.giveItems(57, 120L);
                string = "balanki_q0292_02.htm";
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
        if (questState.getQuestItemsCount(beG) == 0L && Rnd.chance(beH)) {
            if (questState.getQuestItemsCount(beF) < 3L) {
                questState.giveItems(beF, 1L);
                questState.playSound("ItemSound.quest_itemget");
            } else {
                questState.takeItems(beF, -1L);
                questState.giveItems(beG, 1L);
                questState.playSound("ItemSound.quest_middle");
            }
        }
        return null;
    }
}

