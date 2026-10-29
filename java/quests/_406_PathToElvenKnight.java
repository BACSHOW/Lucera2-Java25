/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _406_PathToElvenKnight
extends Quest
implements ScriptFile {
    private static final int aUE = 30327;
    private static final int byC = 30317;
    private static final int byD = 1202;
    private static final int byE = 1203;
    private static final int byF = 1205;
    private static final int byG = 1206;
    private static final int byH = 1276;
    private static final int byI = 1204;
    private static final int byJ = 20035;
    private static final int byK = 20042;
    private static final int byL = 20045;
    private static final int byM = 20051;
    private static final int byN = 20060;
    private static final int byO = 20782;
    private static final int[][] DROPLIST_COND = new int[][]{{1, 2, 20035, 0, 1205, 20, 70, 1}, {1, 2, 20042, 0, 1205, 20, 70, 1}, {1, 2, 20045, 0, 1205, 20, 70, 1}, {1, 2, 20051, 0, 1205, 20, 70, 1}, {1, 2, 20060, 0, 1205, 20, 70, 1}, {4, 5, 20782, 0, 1206, 20, 50, 1}};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _406_PathToElvenKnight() {
        super(0);
        this.addStartNpc(30327);
        this.addTalkId(30317);
        for (int i = 0; i < DROPLIST_COND.length; ++i) {
            this.addKillId(DROPLIST_COND[i][2]);
        }
        this.addQuestItem(1205, 1206, 1202, 1276, 1203);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("master_sorius_q0406_05.htm")) {
            if (questState.getPlayer().getClassId().getId() == 18) {
                if (questState.getQuestItemsCount(1204) > 0L) {
                    string2 = "master_sorius_q0406_04.htm";
                    questState.exitCurrentQuest(true);
                } else if (questState.getPlayer().getLevel() < 18) {
                    string2 = "master_sorius_q0406_03.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (questState.getPlayer().getClassId().getId() == 19) {
                string2 = "master_sorius_q0406_02a.htm";
                questState.exitCurrentQuest(true);
            } else {
                string2 = "master_sorius_q0406_02.htm";
                questState.exitCurrentQuest(true);
            }
        } else if (string.equalsIgnoreCase("master_sorius_q0406_06.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("blacksmith_kluto_q0406_02.htm")) {
            questState.takeItems(1202, -1L);
            questState.giveItems(1276, 1L);
            questState.setCond(4);
            questState.setState(2);
        } else {
            string2 = "noquest";
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getCond();
        if (n == 30327) {
            if (n2 == 0) {
                string = "master_sorius_q0406_01.htm";
            } else if (n2 == 1) {
                string = questState.getQuestItemsCount(1205) == 0L ? "master_sorius_q0406_07.htm" : "master_sorius_q0406_08.htm";
            } else if (n2 == 2) {
                questState.takeItems(1205, -1L);
                questState.giveItems(1202, 1L);
                string = "master_sorius_q0406_09.htm";
                questState.setCond(3);
                questState.setState(2);
            } else if (n2 == 3 || n2 == 4 || n2 == 5) {
                string = "master_sorius_q0406_11.htm";
            } else if (n2 == 6) {
                questState.takeItems(1203, -1L);
                if (questState.getPlayer().getClassId().getLevel() == 1) {
                    questState.giveItems(1204, 1L);
                    if (!questState.getPlayer().getVarB("prof1")) {
                        questState.getPlayer().setVar("prof1", "1", -1L);
                        questState.addExpAndSp(3200L, 2280L);
                    }
                }
                questState.exitCurrentQuest(true);
                questState.playSound("ItemSound.quest_finish");
                string = "master_sorius_q0406_10.htm";
            }
        } else if (n == 30317) {
            if (n2 == 3) {
                string = "blacksmith_kluto_q0406_01.htm";
            } else if (n2 == 4) {
                string = questState.getQuestItemsCount(1206) == 0L ? "blacksmith_kluto_q0406_03.htm" : "blacksmith_kluto_q0406_04.htm";
            } else if (n2 == 5) {
                questState.takeItems(1206, -1L);
                questState.takeItems(1276, -1L);
                questState.giveItems(1203, 1L);
                string = "blacksmith_kluto_q0406_05.htm";
                questState.setCond(6);
                questState.setState(2);
            } else if (n2 == 6) {
                string = "blacksmith_kluto_q0406_06.htm";
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
        return null;
    }
}

