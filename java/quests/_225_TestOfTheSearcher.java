/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _225_TestOfTheSearcher
extends Quest
implements ScriptFile {
    private static final int aTD = 30690;
    private static final int aTE = 30291;
    private static final int aTF = 30420;
    private static final int aTG = 30628;
    private static final int aTH = 30728;
    private static final int aTI = 30729;
    private static final int aTJ = 30730;
    private static final int aTK = 30627;
    private static final int aTL = 2784;
    private static final int aTM = 2785;
    private static final int aTN = 2786;
    private static final int aTO = 2787;
    private static final int aTP = 2788;
    private static final int aTQ = 2789;
    private static final int aTR = 2808;
    private static final int aTS = 2792;
    private static final int aTT = 2790;
    private static final int aTU = 2793;
    private static final int aTV = 2791;
    private static final int aTW = 2794;
    private static final int aTX = 2805;
    private static final int aTY = 2807;
    private static final int aTZ = 2795;
    private static final int aUa = 2799;
    private static final int aUb = 2798;
    private static final int aUc = 2796;
    private static final int aUd = 2797;
    private static final int aUe = 2800;
    private static final int aUf = 2803;
    private static final int aUg = 2804;
    private static final int aUh = 2806;
    private static final int aUi = 2801;
    private static final int aUj = 2802;
    private static final int aUk = 2809;
    private static final int aUl = 20781;
    private static final int aUm = 27094;
    private static final int aUn = 27093;
    private static final int aUo = 20555;
    private static final int aTh = 20551;
    private static final int aPm = 20144;
    private static final int[][] DROPLIST_COND = new int[][]{{3, 4, 20781, 0, 2787, 10, 100, 1}, {3, 4, 27094, 0, 2787, 10, 100, 1}, {10, 11, 20555, 0, 2797, 10, 100, 1}};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _225_TestOfTheSearcher() {
        super(0);
        this.addStartNpc(30690);
        this.addTalkId(30291);
        this.addTalkId(30728);
        this.addTalkId(30729);
        this.addTalkId(30420);
        this.addTalkId(30730);
        this.addTalkId(30627);
        this.addTalkId(30628);
        this.addKillId(27093);
        this.addKillId(20551);
        this.addKillId(20144);
        for (int i = 0; i < DROPLIST_COND.length; ++i) {
            this.addKillId(DROPLIST_COND[i][2]);
        }
        this.addQuestItem(2787, 2797, 2784, 2785, 2786, 2788, 2790, 2789, 2791, 2792, 2793, 2794, 2795, 2796, 2799, 2798, 2800, 2801, 2802, 2803, 2804, 2806, 2805);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("30690-05.htm")) {
            questState.giveItems(2784, 1L);
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("30291-07.htm")) {
            questState.takeItems(2790, -1L);
            questState.takeItems(2791, -1L);
            questState.giveItems(2792, 1L);
            questState.giveItems(2793, 1L);
            questState.giveItems(2794, 1L);
            questState.setCond(8);
            questState.setState(2);
        } else if (string.equalsIgnoreCase("30420-01a.htm")) {
            questState.takeItems(2795, -1L);
            questState.giveItems(2796, 1L);
            questState.setCond(10);
            questState.setState(2);
        } else if (string.equalsIgnoreCase("30730-01d.htm")) {
            questState.takeItems(2799, -1L);
            questState.giveItems(2800, 1L);
            questState.setCond(14);
            questState.setState(2);
        } else if (string.equalsIgnoreCase("30627-01a.htm")) {
            NpcInstance npcInstance2 = GameObjectsStorage.getByNpcId(30628);
            if (npcInstance2 == null) {
                if (questState.getQuestItemsCount(2806) == 0L) {
                    questState.giveItems(2806, 1L);
                }
                questState.addSpawn(30628);
                questState.startQuestTimer("Chest", 300000L);
                questState.setCond(17);
                questState.setState(2);
            } else {
                if (!questState.isRunningQuestTimer("Wait1")) {
                    questState.startQuestTimer("Wait1", 300000L);
                }
                string2 = "<html><head><body>Please wait 5 minutes</body></html>";
            }
        } else if (string.equalsIgnoreCase("30628-01a.htm")) {
            questState.takeItems(2806, -1L);
            questState.giveItems(2807, 20L);
            questState.setCond(18);
        } else if (string.equalsIgnoreCase("Wait1") || string.equalsIgnoreCase("Chest")) {
            NpcInstance npcInstance3 = GameObjectsStorage.getByNpcId(30628);
            if (npcInstance3 != null) {
                npcInstance3.deleteMe();
            }
            questState.cancelQuestTimer("Wait1");
            questState.cancelQuestTimer("Chest");
            if (questState.getCond() == 17) {
                questState.setCond(16);
            }
            return null;
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getCond();
        if (n == 30690) {
            if (questState.getQuestItemsCount(2809) != 0L) {
                string = "completed";
                questState.exitCurrentQuest(true);
            } else if (n2 == 0) {
                if (questState.getPlayer().getClassId().getId() == 7 || questState.getPlayer().getClassId().getId() == 22 || questState.getPlayer().getClassId().getId() == 35 || questState.getPlayer().getClassId().getId() == 54) {
                    if (questState.getPlayer().getLevel() >= 39) {
                        string = questState.getPlayer().getClassId().getId() == 54 ? "30690-04.htm" : "30690-03.htm";
                    } else {
                        string = "30690-02.htm";
                        questState.exitCurrentQuest(true);
                    }
                } else {
                    string = "30690-01.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 1) {
                string = "30690-06.htm";
            } else if (n2 > 1 && n2 < 16) {
                string = "30623-17.htm";
            } else if (n2 == 19) {
                string = "30690-08.htm";
                if (!questState.getPlayer().getVarB("prof2.3")) {
                    questState.giveItems(7562, 8L);
                    questState.addExpAndSp(37831L, 18750L);
                    questState.getPlayer().setVar("prof2.3", "1", -1L);
                }
                questState.takeItems(2808, -1L);
                questState.giveItems(2809, 1L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            }
        } else if (n == 30291) {
            if (n2 == 1) {
                string = "30291-01.htm";
                questState.takeItems(2784, -1L);
                questState.giveItems(2785, 1L);
                questState.setCond(2);
                questState.setState(2);
            } else if (n2 == 2) {
                string = "30291-02.htm";
            } else if (n2 > 2 && n2 < 7) {
                string = "30291-03.htm";
            } else if (n2 == 7) {
                string = "30291-04.htm";
            } else if (n2 == 8) {
                string = "30291-08.htm";
            } else if (n2 == 13 || n2 == 14) {
                string = "30291-09.htm";
            } else if (n2 == 18) {
                questState.takeItems(2794, -1L);
                questState.takeItems(2805, -1L);
                questState.takeItems(2807, -1L);
                questState.giveItems(2808, 1L);
                string = "30291-11.htm";
                questState.setCond(19);
                questState.setState(2);
            } else if (n2 == 19) {
                string = "30291-12.htm";
            }
        } else if (n == 30728) {
            if (n2 == 2) {
                string = "30728-01.htm";
                questState.takeItems(2785, -1L);
                questState.giveItems(2786, 1L);
                questState.setCond(3);
                questState.setState(2);
            } else if (n2 == 3) {
                string = "30728-02.htm";
            } else if (n2 == 4) {
                string = "30728-03.htm";
                questState.takeItems(2787, -1L);
                questState.takeItems(2786, -1L);
                questState.giveItems(2788, 1L);
                questState.setCond(5);
                questState.setState(2);
            } else if (n2 == 5) {
                string = "30728-04.htm";
            } else if (n2 == 6) {
                questState.takeItems(2789, -1L);
                questState.takeItems(2788, -1L);
                questState.giveItems(2790, 1L);
                string = "30728-05.htm";
                questState.setCond(7);
                questState.setState(2);
            } else if (n2 == 7) {
                string = "30728-06.htm";
            } else if (n2 == 8) {
                string = "30728-07.htm";
            }
        } else if (n == 30729) {
            if (n2 == 8) {
                questState.takeItems(2793, -1L);
                questState.giveItems(2795, 1L);
                string = "30729-01.htm";
                questState.setCond(9);
                questState.setState(2);
            } else if (n2 == 9) {
                string = "30729-02.htm";
            } else if (n2 == 12) {
                questState.takeItems(2795, -1L);
                questState.takeItems(2798, -1L);
                questState.giveItems(2799, 1L);
                string = "30729-03.htm";
                questState.setCond(13);
                questState.setState(2);
            } else if (n2 == 13) {
                string = "30729-04.htm";
            } else if (n2 >= 8 && n2 <= 14) {
                string = "30729-05.htm";
            }
        } else if (n == 30420) {
            if (n2 == 9) {
                string = "30420-01.htm";
            } else if (n2 == 10) {
                string = "30420-02.htm";
            } else if (n2 == 11) {
                questState.takeItems(2796, -1L);
                questState.takeItems(2797, -1L);
                questState.giveItems(2798, 1L);
                string = "30420-03.htm";
                questState.setCond(12);
                questState.setState(2);
            } else if (n2 == 12 || n2 == 13) {
                string = "30420-04.htm";
            }
        } else if (n == 30730) {
            if (n2 == 13) {
                string = "30730-01.htm";
            } else if (n2 == 14) {
                string = "30730-02.htm";
            } else if (n2 == 15) {
                questState.takeItems(2803, -1L);
                questState.takeItems(2804, -1L);
                questState.takeItems(2792, -1L);
                questState.takeItems(2800, -1L);
                questState.giveItems(2805, 1L);
                string = "30730-03.htm";
                questState.setCond(16);
                questState.setState(2);
            } else if (n2 == 16) {
                string = "30730-04.htm";
            }
        } else if (n == 30627) {
            if (n2 == 16 || n2 == 17) {
                string = "30627-01.htm";
            }
        } else if (n == 30628) {
            string = n2 == 17 ? "30628-01.htm" : "<html><head><body>You haven't got a Key for this Chest.</body></html>";
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
        if (n2 == 5 && n == 27093) {
            if (questState.getQuestItemsCount(2791) == 0L) {
                questState.giveItems(2791, 1L);
            }
            if (questState.getQuestItemsCount(2789) == 0L) {
                questState.giveItems(2789, 1L);
            }
            questState.playSound("ItemSound.quest_middle");
            questState.setCond(6);
            questState.setState(2);
        } else if (n2 == 14) {
            if (n == 20551 && questState.getQuestItemsCount(2803) == 0L) {
                questState.giveItems(2801, 1L);
                if (questState.getQuestItemsCount(2801) >= 4L) {
                    questState.takeItems(2801, -1L);
                    questState.giveItems(2803, 1L);
                }
            } else if (n == 20144 && questState.getQuestItemsCount(2804) == 0L) {
                questState.giveItems(2802, 1L);
                if (questState.getQuestItemsCount(2802) >= 4L) {
                    questState.takeItems(2802, -1L);
                    questState.giveItems(2804, 1L);
                }
            }
            if (questState.getQuestItemsCount(2803) != 0L && questState.getQuestItemsCount(2804) != 0L) {
                questState.setCond(15);
                questState.setState(2);
            }
        }
        return null;
    }
}

