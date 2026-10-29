/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _380_BringOutTheFlavorOfIngredients
extends Quest
implements ScriptFile {
    private static final int bgW = 30069;
    private static final int bxw = 5895;
    private static final int bxx = 5896;
    private static final int bxy = 5897;
    private static final int bxz = 1831;
    private static final int bxA = 5959;
    private static final int bxB = 5960;
    private static final int bxC = 55;
    private static final int bxD = 20205;
    private static final int bxE = 20206;
    private static final int aXF = 20225;
    private static final int[][] DROPLIST_COND = new int[][]{{1, 0, 20205, 0, 5895, 4, 10, 1}, {1, 0, 20206, 0, 5896, 20, 50, 1}, {1, 0, 20225, 0, 5897, 10, 50, 1}};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _380_BringOutTheFlavorOfIngredients() {
        super(0);
        this.addStartNpc(30069);
        for (int i = 0; i < DROPLIST_COND.length; ++i) {
            this.addKillId(DROPLIST_COND[i][2]);
            this.addQuestItem(DROPLIST_COND[i][4]);
        }
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        if (string.equalsIgnoreCase("rollant_q0380_05.htm")) {
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("rollant_q0380_12.htm")) {
            questState.giveItems(5959, 1L);
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
        if (n == 30069) {
            if (n2 == 0) {
                if (questState.getPlayer().getLevel() >= 24) {
                    string = "rollant_q0380_02.htm";
                } else {
                    string = "rollant_q0380_01.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 1) {
                string = "rollant_q0380_06.htm";
            } else if (n2 == 2 && questState.getQuestItemsCount(1831) >= 2L) {
                questState.takeItems(1831, 2L);
                questState.takeItems(5895, -1L);
                questState.takeItems(5896, -1L);
                questState.takeItems(5897, -1L);
                string = "rollant_q0380_07.htm";
                questState.setCond(3);
                questState.setState(2);
            } else if (n2 == 2) {
                string = "rollant_q0380_06.htm";
            } else if (n2 == 3) {
                string = "rollant_q0380_08.htm";
                questState.setCond(4);
            } else if (n2 == 4) {
                string = "rollant_q0380_09.htm";
                questState.setCond(5);
            }
            if (n2 == 5) {
                string = "rollant_q0380_10.htm";
                questState.setCond(6);
            }
            if (n2 == 6) {
                questState.giveItems(5960, 1L);
                if (Rnd.chance(55)) {
                    string = "rollant_q0380_11.htm";
                } else {
                    string = "rollant_q0380_14.htm";
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(true);
                }
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
        if (n2 == 1 && questState.getQuestItemsCount(5895) >= 4L && questState.getQuestItemsCount(5896) >= 20L && questState.getQuestItemsCount(5897) >= 10L) {
            questState.setCond(2);
            questState.setState(2);
        }
        return null;
    }
}

