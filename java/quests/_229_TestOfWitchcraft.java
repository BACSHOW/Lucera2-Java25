/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _229_TestOfWitchcraft
extends Quest
implements ScriptFile {
    private static final int aWs = 30630;
    private static final int aWt = 30098;
    private static final int aWu = 30110;
    private static final int aOx = 30476;
    private static final int Lara = 30063;
    private static final int aWv = 30631;
    private static final int aWw = 30314;
    private static final int aWx = 30435;
    private static final int aWy = 30417;
    private static final int aWz = 30188;
    private static final int aWA = 30633;
    private static final int aWB = 30632;
    private static final int aWC = 3307;
    private static final int aWD = 3308;
    private static final int aWE = 3309;
    private static final int aWF = 3310;
    private static final int aWG = 3311;
    private static final int aWH = 3312;
    private static final int aWI = 3313;
    private static final int aWJ = 3314;
    private static final int aWK = 3315;
    private static final int aWL = 3316;
    private static final int aWM = 3317;
    private static final int aWN = 3318;
    private static final int aWO = 3319;
    private static final int aWP = 3320;
    private static final int aWQ = 3321;
    private static final int aWR = 3322;
    private static final int aWS = 3323;
    private static final int aWT = 3324;
    private static final int aWU = 3325;
    private static final int aWV = 3326;
    private static final int aWW = 3327;
    private static final int aWX = 3328;
    private static final int aWY = 3329;
    private static final int aWZ = 3330;
    private static final int aXa = 3331;
    private static final int aXb = 3332;
    private static final int aXc = 3333;
    private static final int aXd = 3334;
    private static final int aXe = 3335;
    private static final int aXf = 3029;
    private static final int aXg = 20557;
    private static final int aWp = 20565;
    private static final int aTi = 20577;
    private static final int aTj = 20578;
    private static final int aTk = 20579;
    private static final int aLh = 20580;
    private static final int aTl = 20581;
    private static final int aPv = 20582;
    private static final int aVx = 27099;
    private static final int aXh = 27100;
    private static final int aXi = 27101;
    private static final int aQD = 20601;
    private static final int aQE = 20602;
    private static final int[][] DROPLIST_COND = new int[][]{{2, 0, 20557, 3310, 3311, 20, 100, 1}, {2, 0, 20565, 3310, 3313, 20, 80, 1}, {2, 0, 20577, 3310, 3312, 20, 50, 1}, {2, 0, 20578, 3310, 3312, 20, 50, 1}, {2, 0, 20579, 3310, 3312, 20, 60, 1}, {2, 0, 20580, 3310, 3312, 20, 60, 1}, {2, 0, 20581, 3310, 3312, 20, 70, 1}, {2, 0, 20582, 3310, 3312, 20, 70, 1}, {2, 0, 27099, 3314, 3319, 1, 100, 1}, {6, 0, 20601, 3328, 3329, 20, 50, 1}, {6, 0, 20602, 3328, 3329, 20, 55, 1}};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _229_TestOfWitchcraft() {
        super(0);
        this.addStartNpc(30630);
        this.addTalkId(30098);
        this.addTalkId(30110);
        this.addTalkId(30476);
        this.addTalkId(30063);
        this.addTalkId(30631);
        this.addTalkId(30314);
        this.addTalkId(30435);
        this.addTalkId(30417);
        this.addTalkId(30188);
        this.addTalkId(30633);
        this.addTalkId(30632);
        for (int i = 0; i < DROPLIST_COND.length; ++i) {
            this.addKillId(DROPLIST_COND[i][2]);
        }
        this.addKillId(27100);
        this.addKillId(27101);
        this.addQuestItem(3308, 3324, 3325, 3326, 3323, 3309, 3310, 3317, 3332, 3331, 3318, 3314, 3315, 3316, 3320, 3321, 3322, 3327, 3029, 3328, 3330, 3335, 3333, 3334, 3311, 3313, 3312, 3319, 3329);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        NpcInstance npcInstance2;
        String string2 = string;
        if (string.equalsIgnoreCase("30630-08.htm")) {
            questState.playSound("ItemSound.quest_accept");
            questState.giveItems(3308, 1L);
            questState.setCond(1);
            questState.setState(2);
        } else if (string.equalsIgnoreCase("30098-03.htm")) {
            questState.giveItems(3309, 1L);
            questState.takeItems(3308, 1L);
            questState.setCond(2);
            questState.setState(2);
        } else if (string.equalsIgnoreCase("30110-03.htm")) {
            questState.giveItems(3310, 1L);
        } else if (string.equalsIgnoreCase("30476-02.htm")) {
            questState.giveItems(3318, 1L);
        } else if (string.equalsIgnoreCase("30063-02.htm")) {
            questState.giveItems(3314, 1L);
        } else if (string.equalsIgnoreCase("30314-02.htm")) {
            questState.giveItems(3315, 1L);
        } else if (string.equalsIgnoreCase("30435-02.htm")) {
            questState.takeItems(3315, 1L);
            questState.giveItems(3316, 1L);
        } else if (string.equalsIgnoreCase("30630-14.htm")) {
            npcInstance2 = GameObjectsStorage.getByNpcId(27101);
            if (npcInstance2 != null && !npcInstance2.isDead()) {
                string2 = "Drevanul Prince Zeruel is already spawned.";
            } else {
                questState.takeItems(3309, 1L);
                questState.takeItems(3317, 1L);
                questState.takeItems(3318, 1L);
                questState.takeItems(3319, 1L);
                questState.takeItems(3320, 1L);
                questState.takeItems(3321, 1L);
                questState.takeItems(3322, 1L);
                if (questState.getQuestItemsCount(3323) == 0L) {
                    questState.giveItems(3323, 1L);
                }
                questState.setCond(4);
                questState.set("id", "1");
                questState.startQuestTimer("DrevanulPrinceZeruel_Fail", 300000L);
                NpcInstance npcInstance3 = questState.addSpawn(27101);
                if (npcInstance3 != null) {
                    npcInstance3.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, questState.getPlayer(), 1);
                }
            }
        } else if (string.equalsIgnoreCase("30630-16.htm")) {
            string2 = "30630-16.htm";
            questState.takeItems(3323, -1L);
            questState.giveItems(3324, 1L);
            questState.giveItems(3325, 1L);
            questState.giveItems(3326, 1L);
            questState.setCond(6);
        } else if (string.equalsIgnoreCase("30110-08.htm")) {
            questState.takeItems(3326, 1L);
            questState.giveItems(3332, 1L);
            questState.giveItems(3331, 1L);
            if (questState.getQuestItemsCount(3029) > 0L) {
                questState.setCond(7);
                questState.setState(2);
            }
        } else if (string.equalsIgnoreCase("30417-03.htm")) {
            questState.takeItems(3325, 1L);
            questState.giveItems(3327, 1L);
        } else if (string.equalsIgnoreCase("30633-02.htm")) {
            npcInstance2 = GameObjectsStorage.getByNpcId(27101);
            if (npcInstance2 != null) {
                string2 = "30633-fail.htm";
            } else {
                questState.set("id", "2");
                questState.setCond(9);
                if (questState.getQuestItemsCount(3335) == 0L) {
                    questState.giveItems(3335, 1L);
                }
                questState.addSpawn(27101);
                questState.startQuestTimer("DrevanulPrinceZeruel_Fail", 300000L);
                NpcInstance npcInstance4 = GameObjectsStorage.getByNpcId(27101);
                if (npcInstance4 != null) {
                    npcInstance4.getAggroList().addDamageHate(questState.getPlayer(), 0, 1);
                }
            }
        } else if (string.equalsIgnoreCase("30630-20.htm")) {
            questState.takeItems(3334, 1L);
        } else if (string.equalsIgnoreCase("30630-21.htm")) {
            questState.takeItems(3333, 1L);
        } else if (string.equalsIgnoreCase("30630-22.htm")) {
            questState.takeItems(3029, -1L);
            questState.takeItems(3331, -1L);
            questState.takeItems(3324, -1L);
            if (!questState.getPlayer().getVarB("prof2.3")) {
                questState.giveItems(7562, 8L);
                questState.addExpAndSp(139796L, 40000L);
                questState.getPlayer().setVar("prof2.3", "1", -1L);
            }
            questState.giveItems(3307, 1L);
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        }
        if (string.equalsIgnoreCase("DrevanulPrinceZeruel_Fail") && (npcInstance2 = GameObjectsStorage.getByNpcId(27101)) != null) {
            npcInstance2.deleteMe();
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 30630) {
            if (questState.getQuestItemsCount(3307) != 0L) {
                string = "completed";
                questState.exitCurrentQuest(true);
            } else if (n2 == 0) {
                if (questState.getPlayer().getClassId().getId() == 11 || questState.getPlayer().getClassId().getId() == 4 || questState.getPlayer().getClassId().getId() == 32) {
                    if (questState.getPlayer().getLevel() < 39) {
                        string = "30630-02.htm";
                        questState.exitCurrentQuest(true);
                    } else {
                        string = questState.getPlayer().getClassId().getId() == 11 ? "30630-03.htm" : "30630-05.htm";
                    }
                } else {
                    string = "30630-01.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 1) {
                string = "30630-09.htm";
            } else if (n2 == 2) {
                string = "30630-10.htm";
            } else if (n2 == 3 || questState.getInt("id") == 1) {
                string = "30630-11.htm";
            } else if (n2 == 5) {
                string = "30630-15.htm";
            } else if (n2 == 6) {
                string = "30630-17.htm";
            } else if (n2 == 7) {
                string = "30630-18.htm";
                questState.setCond(8);
            } else if (n2 == 10) {
                string = questState.getQuestItemsCount(3334) != 0L ? "30630-19.htm" : (questState.getQuestItemsCount(3333) != 0L ? "30630-20.htm" : "30630-21.htm");
            }
        } else if (n == 30098) {
            string = n2 == 1 ? "30098-01.htm" : (n2 == 2 ? "30098-04.htm" : "30098-05.htm");
        } else if (n == 30110) {
            if (n2 == 2) {
                if (questState.getQuestItemsCount(3317) == 0L && questState.getQuestItemsCount(3310) == 0L) {
                    string = "30110-01.htm";
                } else if (questState.getQuestItemsCount(3310) > 0L && (questState.getQuestItemsCount(3311) < 20L || questState.getQuestItemsCount(3312) < 20L || questState.getQuestItemsCount(3313) < 20L)) {
                    string = "30110-04.htm";
                } else if (questState.getQuestItemsCount(3317) == 0L && questState.getQuestItemsCount(3310) > 0L) {
                    questState.takeItems(3310, -1L);
                    questState.takeItems(3311, -1L);
                    questState.takeItems(3312, -1L);
                    questState.takeItems(3313, -1L);
                    questState.giveItems(3317, 1L);
                    string = "30110-05.htm";
                } else if (questState.getQuestItemsCount(3317) == 1L) {
                    string = "30110-06.htm";
                }
            } else {
                string = n2 == 6 ? "30110-07.htm" : (n2 == 10 ? "30110-10.htm" : "30110-09.htm");
            }
        } else if (n == 30476) {
            if (n2 == 2) {
                string = questState.getQuestItemsCount(3318) == 0L ? "30476-01.htm" : "30476-03.htm";
            } else if (n2 > 2) {
                string = "30476-04.htm";
            }
        } else if (n == 30063) {
            if (n2 == 2) {
                if (questState.getQuestItemsCount(3314) == 0L && questState.getQuestItemsCount(3319) == 0L) {
                    string = "30063-01.htm";
                } else if (questState.getQuestItemsCount(3314) == 1L && questState.getQuestItemsCount(3319) == 0L) {
                    string = "30063-03.htm";
                } else if (questState.getQuestItemsCount(3319) == 1L) {
                    string = "30063-04.htm";
                }
            } else if (n2 > 2) {
                string = "30063-05.htm";
            }
        } else if (n == 30631 && n2 == 2 && questState.getQuestItemsCount(3314) > 0L) {
            string = "30631-01.htm";
        } else if (n == 30314 && n2 == 2) {
            string = questState.getQuestItemsCount(3317) > 0L && questState.getQuestItemsCount(3318) > 0L && questState.getQuestItemsCount(3319) > 0L ? "30314-01.htm" : "30314-04.htm";
        } else if (n == 30435) {
            string = n2 == 2 && questState.getQuestItemsCount(3315) > 0L ? (questState.getQuestItemsCount(3320) + questState.getQuestItemsCount(3321) + questState.getQuestItemsCount(3322) == 0L ? "30435-01.htm" : "30435-04.htm") : "30435-05.htm";
        } else if (n == 30417) {
            if (n2 == 6) {
                if (questState.getQuestItemsCount(3327) > 0L || questState.getQuestItemsCount(3328) > 0L) {
                    string = "30417-04.htm";
                } else if (questState.getQuestItemsCount(3330) == 0L) {
                    string = "30417-01.htm";
                } else if (questState.getQuestItemsCount(3330) != 0L) {
                    string = "30417-05.htm";
                    questState.takeItems(3330, 1L);
                    questState.giveItems(3029, 1L);
                    if (questState.getQuestItemsCount(3332) > 0L) {
                        questState.setCond(7);
                        questState.setState(2);
                    }
                }
            } else if (n2 == 7) {
                string = "30417-06.htm";
            }
        } else if (n == 30188) {
            if (n2 == 6) {
                if (questState.getQuestItemsCount(3327) != 0L) {
                    string = "30188-01.htm";
                    questState.takeItems(3327, 1L);
                    questState.giveItems(3328, 1L);
                } else if (questState.getQuestItemsCount(3328) > 0L && questState.getQuestItemsCount(3329) < 20L) {
                    string = "30188-02.htm";
                } else if (questState.getQuestItemsCount(3329) >= 20L) {
                    string = "30188-03.htm";
                    questState.takeItems(3329, -1L);
                    questState.takeItems(3328, -1L);
                    questState.giveItems(3330, 1L);
                } else if (questState.getQuestItemsCount(3330) > 0L) {
                    string = "30188-04.htm";
                }
            } else if (n2 == 7) {
                string = "30188-05.htm";
            }
        } else if (n == 30633) {
            string = questState.getInt("id") == 2 || n2 == 8 && questState.getQuestItemsCount(3335) == 0L ? "30633-01.htm" : "30633-03.htm";
        } else if (n == 30632 && n2 == 2) {
            string = "30632-01.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        for (int i = 0; i < DROPLIST_COND.length; ++i) {
            if (n2 != DROPLIST_COND[i][0] || n != DROPLIST_COND[i][2] || DROPLIST_COND[i][3] != 0 && questState.getQuestItemsCount(DROPLIST_COND[i][3]) <= 0L) continue;
            if (n == 27099) {
                questState.takeItems(3314, -1L);
            }
            if (DROPLIST_COND[i][5] == 0) {
                questState.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][6]);
                continue;
            }
            if (!questState.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][7], DROPLIST_COND[i][5], DROPLIST_COND[i][6]) || DROPLIST_COND[i][1] == n2 || DROPLIST_COND[i][1] == 0) continue;
            questState.setCond(DROPLIST_COND[i][1]);
            questState.setState(2);
        }
        if (n2 == 2 && questState.getQuestItemsCount(3316) > 0L && n == 27100) {
            if (questState.getQuestItemsCount(3320) == 0L && Rnd.chance(50)) {
                questState.giveItems(3320, 1L);
            }
            if (questState.getQuestItemsCount(3321) == 0L && Rnd.chance(50)) {
                questState.giveItems(3321, 1L);
            }
            if (questState.getQuestItemsCount(3322) == 0L && Rnd.chance(50)) {
                questState.giveItems(3322, 1L);
            }
            if (questState.getQuestItemsCount(3320) != 0L && questState.getQuestItemsCount(3321) != 0L && questState.getQuestItemsCount(3322) != 0L) {
                questState.takeItems(3316, -1L);
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(3);
                questState.setState(2);
            }
        } else if (n2 == 4 && n == 27101) {
            questState.cancelQuestTimer("DrevanulPrinceZeruel_Fail");
            NpcInstance npcInstance2 = GameObjectsStorage.getByNpcId(27101);
            if (npcInstance2 != null) {
                npcInstance2.deleteMe();
            }
            questState.setCond(5);
            questState.unset("id");
            questState.setState(2);
        } else if (n2 == 9 && n == 27101) {
            if (questState.getItemEquipped(7) == 3029) {
                questState.takeItems(3335, 1L);
                questState.takeItems(3332, 1L);
                questState.giveItems(3333, 1L);
                questState.giveItems(3334, 1L);
                questState.playSound("ItemSound.quest_middle");
                questState.unset("id");
                questState.setCond(10);
                questState.setState(2);
                return "You trapped the Seal of Drevanul Prince Zeruel";
            }
            questState.cancelQuestTimer("DrevanulPrinceZeruel_Fail");
            NpcInstance npcInstance3 = GameObjectsStorage.getByNpcId(27101);
            if (npcInstance3 != null) {
                npcInstance3.deleteMe();
            }
        }
        return null;
    }
}

