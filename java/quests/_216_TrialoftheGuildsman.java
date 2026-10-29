/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.MonsterInstance;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _216_TrialoftheGuildsman
extends Quest
implements ScriptFile {
    private static final int aML = 30103;
    private static final int aMM = 30210;
    private static final int aMN = 30283;
    private static final int aMO = 30298;
    private static final int aMP = 30688;
    private static final int aMQ = 3119;
    private static final int aMR = 3120;
    private static final int aMS = 3121;
    private static final int aMT = 3122;
    private static final int aMU = 3123;
    private static final int aMV = 3124;
    private static final int aMW = 3125;
    private static final int aMX = 3126;
    private static final int aMY = 3127;
    private static final int aMZ = 3128;
    private static final int aNa = 3129;
    private static final int aNb = 3130;
    private static final int aNc = 3131;
    private static final int aNd = 3132;
    private static final int aNe = 3133;
    private static final int aNf = 3134;
    private static final int aNg = 3135;
    private static final int aNh = 3136;
    private static final int aNi = 3137;
    private static final int aNj = 3138;
    private static final int aNk = 3139;
    private static final int aNl = 3024;
    private static final int aNm = 7562;
    private static final int aNn = 3025;
    private static final int[][] DROPLIST_COND = new int[][]{{3, 4, 20223, 3120, 3121, 1, 20, 1}, {3, 4, 20154, 3120, 3121, 1, 50, 1}, {3, 4, 20155, 3120, 3121, 1, 50, 1}, {3, 4, 20156, 3120, 3121, 1, 50, 1}, {5, 0, 20267, 3127, 3128, 30, 100, 1}, {5, 0, 20268, 3127, 3128, 30, 100, 1}, {5, 0, 20269, 3127, 3128, 30, 100, 1}, {5, 0, 20270, 3127, 3128, 30, 100, 1}, {5, 0, 20271, 3127, 3128, 30, 100, 1}, {5, 0, 20200, 3129, 3130, 70, 100, 2}, {5, 0, 20201, 3129, 3130, 70, 100, 2}, {5, 0, 20202, 3129, 3132, 70, 100, 2}, {5, 0, 20083, 3129, 3131, 70, 100, 2}, {5, 0, 20168, 3129, 3133, 70, 100, 2}};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _216_TrialoftheGuildsman() {
        super(0);
        this.addStartNpc(30103);
        this.addTalkId(30103);
        this.addTalkId(30210);
        this.addTalkId(30283);
        this.addTalkId(30298);
        this.addTalkId(30688);
        this.addKillId(20079);
        this.addKillId(20080);
        this.addKillId(20081);
        for (int i = 0; i < DROPLIST_COND.length; ++i) {
            this.addKillId(DROPLIST_COND[i][2]);
        }
        this.addQuestItem(3122, 3120, 3123, 3125, 3129, 3126, 3124, 3135, 3127, 3134, 3138, 3139, 3136, 3137, 3121, 3128, 3130, 3132, 3131, 3133);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("valkon_q0216_06.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            questState.giveItems(3120, 1L);
            questState.takeItems(57, 2000L);
        } else if (string.equalsIgnoreCase("valkon_q0216_07c.htm")) {
            questState.setCond(3);
        } else if (string.equalsIgnoreCase("valkon_q0216_05.htm") && questState.getQuestItemsCount(57) < 2000L) {
            string2 = "valkon_q0216_05a.htm";
        } else if (string.equalsIgnoreCase("30103_3") || string.equalsIgnoreCase("30103_4")) {
            string2 = string.equalsIgnoreCase("30103_3") ? "valkon_q0216_09a.htm" : "valkon_q0216_09b.htm";
            questState.takeItems(3139, -1L);
            questState.takeItems(3122, -1L);
            questState.takeItems(3024, -1L);
            questState.giveItems(3119, 1L);
            if (!questState.getPlayer().getVarB("prof2.1")) {
                questState.giveItems(7562, 8L);
                questState.addExpAndSp(80993L, 12250L);
                questState.getPlayer().setVar("prof2.1", "1", -1L);
            }
            questState.exitCurrentQuest(true);
            questState.playSound("ItemSound.quest_finish");
        } else if (string.equalsIgnoreCase("blacksmith_alltran_q0216_03.htm")) {
            questState.takeItems(3120, -1L);
            questState.takeItems(3121, -1L);
            questState.giveItems(3122, 1L);
            questState.giveItems(3024, 1L);
            questState.giveItems(3123, 1L);
            questState.giveItems(3124, 1L);
            questState.setCond(5);
        } else if (string.equalsIgnoreCase("warehouse_keeper_norman_q0216_04.htm")) {
            questState.takeItems(3123, -1L);
            questState.giveItems(3125, 1L);
            questState.giveItems(3126, 1L);
        } else if (string.equalsIgnoreCase("warehouse_keeper_norman_q0216_10.htm")) {
            questState.takeItems(3128, -1L);
            questState.takeItems(3125, -1L);
            questState.giveItems(3129, 1L);
        } else if (string.equalsIgnoreCase("blacksmith_duning_q0216_02.htm")) {
            questState.takeItems(3126, -1L);
            questState.giveItems(3127, 1L);
        } else if (string.equalsIgnoreCase("blacksmith_pinter_q0216_04.htm")) {
            questState.takeItems(3124, -1L);
            questState.giveItems(3135, 1L);
            if (questState.getPlayer().getClassId().getId() == 56) {
                string2 = "blacksmith_pinter_q0216_05.htm";
                questState.giveItems(3025, 1L);
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getState();
        int n3 = 0;
        if (n2 != 1) {
            n3 = questState.getCond();
        }
        if (n == 30103) {
            if (questState.getQuestItemsCount(3119) > 0L) {
                string = "completed";
                questState.exitCurrentQuest(true);
            } else if (n3 == 0) {
                if (questState.getPlayer().getClassId().getId() == 54 || questState.getPlayer().getClassId().getId() == 56) {
                    if (questState.getPlayer().getLevel() >= 35) {
                        string = "valkon_q0216_03.htm";
                    } else {
                        string = "valkon_q0216_02.htm";
                        questState.exitCurrentQuest(true);
                    }
                } else {
                    string = "valkon_q0216_01.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n3 == 2 && questState.getQuestItemsCount(3120) > 0L) {
                string = "valkon_q0216_07.htm";
            } else if (questState.getQuestItemsCount(3122) > 0L) {
                string = questState.getQuestItemsCount(3139) < 7L ? "valkon_q0216_08.htm" : "valkon_q0216_09.htm";
            }
        } else if (n == 30283) {
            if (n3 == 1 && questState.getQuestItemsCount(3120) > 0L) {
                string = "blacksmith_alltran_q0216_01.htm";
                questState.setCond(2);
            } else if (n3 == 4 && questState.getQuestItemsCount(3120) > 0L && questState.getQuestItemsCount(3121) == 1L) {
                string = "blacksmith_alltran_q0216_02.htm";
            } else if (n3 < 6 && questState.getQuestItemsCount(3122) == 1L && questState.getQuestItemsCount(3139) < 7L) {
                string = "blacksmith_alltran_q0216_04.htm";
            } else if (n3 == 6 && questState.getQuestItemsCount(3139) == 7L) {
                string = "blacksmith_alltran_q0216_05.htm";
            }
        } else if (n == 30210 && n3 >= 5) {
            if (questState.getQuestItemsCount(3122) == 1L && questState.getQuestItemsCount(3123) == 1L) {
                string = "warehouse_keeper_norman_q0216_01.htm";
            } else if (questState.getQuestItemsCount(3122) > 0L && questState.getQuestItemsCount(3125) > 0L && questState.getQuestItemsCount(3126) > 0L) {
                string = "warehouse_keeper_norman_q0216_05.htm";
            } else if (questState.getQuestItemsCount(3122) > 0L && questState.getQuestItemsCount(3125) > 0L && questState.getQuestItemsCount(3127) > 0L) {
                string = "warehouse_keeper_norman_q0216_06.htm";
            } else if (questState.getQuestItemsCount(3122) > 0L && questState.getQuestItemsCount(3125) > 0L && questState.getQuestItemsCount(3128) >= 30L) {
                string = "warehouse_keeper_norman_q0216_07.htm";
            } else if (questState.getQuestItemsCount(3122) > 0L && questState.getQuestItemsCount(3129) > 0L) {
                if (questState.getQuestItemsCount(3130) >= 70L && questState.getQuestItemsCount(3131) >= 70L && questState.getQuestItemsCount(3132) >= 70L && questState.getQuestItemsCount(3133) >= 70L) {
                    string = "warehouse_keeper_norman_q0216_12.htm";
                    questState.takeItems(3129, -1L);
                    questState.takeItems(3130, -1L);
                    questState.takeItems(3131, -1L);
                    questState.takeItems(3132, -1L);
                    questState.takeItems(3133, -1L);
                    questState.giveItems(3134, 7L);
                    if (questState.getQuestItemsCount(3138) == 7L && questState.getQuestItemsCount(3134) == 7L) {
                        questState.setCond(6);
                    }
                } else {
                    string = "warehouse_keeper_norman_q0216_11.htm";
                }
            } else if (questState.getQuestItemsCount(3125) == 0L && questState.getQuestItemsCount(3129) == 0L && questState.getQuestItemsCount(3122) == 1L && (questState.getQuestItemsCount(3134) > 0L || questState.getQuestItemsCount(3139) > 0L)) {
                string = "warehouse_keeper_norman_q0216_13.htm";
            }
        } else if (n == 30688 && n3 >= 5) {
            if (questState.getQuestItemsCount(3122) > 0L && questState.getQuestItemsCount(3125) > 0L && questState.getQuestItemsCount(3126) > 0L) {
                string = "blacksmith_duning_q0216_01.htm";
            } else if (questState.getQuestItemsCount(3122) > 0L && questState.getQuestItemsCount(3125) > 0L && questState.getQuestItemsCount(3127) > 0L) {
                string = "blacksmith_duning_q0216_03.htm";
            } else if (questState.getQuestItemsCount(3122) > 0L && questState.getQuestItemsCount(3125) > 0L && questState.getQuestItemsCount(3128) == 30L) {
                string = "blacksmith_duning_q0216_04.htm";
            } else if (questState.getQuestItemsCount(3126) == 0L && questState.getQuestItemsCount(3127) == 0L && questState.getQuestItemsCount(3128) == 0L && questState.getQuestItemsCount(3122) == 1L) {
                string = "blacksmith_duning_q0216_01.htm";
            }
        } else if (n == 30298 && n3 >= 5) {
            if (questState.getQuestItemsCount(3122) > 0L && questState.getQuestItemsCount(3124) > 0L) {
                string = questState.getPlayer().getLevel() < 36 ? "blacksmith_pinter_q0216_01.htm" : "blacksmith_pinter_q0216_02.htm";
            } else if (questState.getQuestItemsCount(3122) > 0L && questState.getQuestItemsCount(3135) > 0L) {
                if (questState.getQuestItemsCount(3136) < 70L) {
                    string = "blacksmith_pinter_q0216_06.htm";
                } else {
                    string = "blacksmith_pinter_q0216_07.htm";
                    questState.takeItems(3135, -1L);
                    questState.takeItems(3136, -1L);
                    questState.takeItems(3025, -1L);
                    questState.takeItems(3137, -1L);
                    questState.giveItems(3138, 7L);
                    if (questState.getQuestItemsCount(3138) == 7L && questState.getQuestItemsCount(3134) == 7L) {
                        questState.setCond(6);
                    }
                }
            } else if (questState.getQuestItemsCount(3122) == 1L && questState.getQuestItemsCount(3135) == 0L && (questState.getQuestItemsCount(3138) > 0L || questState.getQuestItemsCount(3139) > 0L)) {
                string = "blacksmith_pinter_q0216_08.htm";
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
            if (!questState.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][7], DROPLIST_COND[i][5], DROPLIST_COND[i][6])) continue;
            if (DROPLIST_COND[i][4] == 3128) {
                questState.takeItems(3127, -1L);
            }
            if (DROPLIST_COND[i][1] == n2 || DROPLIST_COND[i][1] == 0) continue;
            questState.setCond(DROPLIST_COND[i][1]);
            questState.setState(2);
        }
        if (n2 == 5 && (n == 20079 || n == 20080 || n == 20081) && Rnd.chance(33) && questState.getQuestItemsCount(3122) > 0L && questState.getQuestItemsCount(3135) > 0L) {
            long l = questState.getQuestItemsCount(3136) + questState.getQuestItemsCount(3137) * 5L;
            if (l < 70L && questState.getPlayer().getClassId().getId() == 54) {
                questState.giveItems(3136, 5L);
                if (questState.getQuestItemsCount(3136) == 70L) {
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
            if (l < 70L && questState.getPlayer().getClassId().getId() == 56) {
                questState.giveItems(3137, 5L);
                if (((MonsterInstance)npcInstance).isSpoiled()) {
                    questState.giveItems(3137, 5L);
                }
                if ((l = questState.getQuestItemsCount(3136) + questState.getQuestItemsCount(3137) * 5L) == 70L) {
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        }
        return null;
    }
}

