/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _223_TestOfChampion
extends Quest
implements ScriptFile {
    private static final int aSv = 1;
    private static final int aSw = 2;
    private static final int aSx = 3;
    private static final int aSy = 4;
    private static final int aSz = 5;
    private static final int aSA = 6;
    private static final int aSB = 7;
    private static final int aSC = 8;
    private static final int aSD = 9;
    private static final int aSE = 10;
    private static final int aSF = 11;
    private static final int aSG = 12;
    private static final int aSH = 13;
    private static final int aSI = 14;
    private static final int aKb = 117454;
    private static final int aKc = 25000;
    private static final int aSJ = 3276;
    private static final int aSK = 3277;
    private static final int aSL = 3278;
    private static final int aSM = 3279;
    private static final int aSN = 3280;
    private static final int aSO = 3281;
    private static final int aSP = 3282;
    private static final int aSQ = 3283;
    private static final int aSR = 3284;
    private static final int aSS = 3285;
    private static final int aST = 3286;
    private static final int aSU = 3287;
    private static final int aSV = 3288;
    private static final int aSW = 3289;
    private static final int aSX = 3290;
    private static final int aSY = 3291;
    private static final int aSZ = 3292;
    private static final int aTa = 30624;
    private static final int aTb = 30093;
    private static final int aTc = 30196;
    private static final int aTd = 30625;
    private static final int aTe = 20145;
    private static final int aTf = 27088;
    private static final int aLd = 20158;
    private static final int aTg = 20553;
    private static final int aTh = 20551;
    private static final int aTi = 20577;
    private static final int aTj = 20578;
    private static final int aTk = 20579;
    private static final int aLh = 20580;
    private static final int aTl = 20581;
    private static final int aPv = 20582;
    private static final int aTm = 20780;
    private static final int[][] DROPLIST = new int[][]{{2, 3, 20780, 3290, 100, 100}, {6, 7, 20145, 3287, 100, 30}, {6, 7, 27088, 3287, 100, 30}, {6, 7, 20158, 3288, 50, 30}, {6, 7, 20553, 3289, 50, 30}, {10, 11, 20551, 3291, 100, 100}, {12, 13, 20577, 3292, 50, 100}, {12, 13, 20578, 3292, 60, 100}, {12, 13, 20579, 3292, 70, 100}, {12, 13, 20580, 3292, 80, 100}, {12, 13, 20581, 3292, 90, 100}, {12, 13, 20582, 3292, 100, 100}};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _223_TestOfChampion() {
        super(0);
        this.addStartNpc(30624);
        this.addTalkId(30093);
        this.addTalkId(30196);
        this.addTalkId(30625);
        this.addKillId(20145, 20158, 27088, 20551, 20553, 20577, 20578, 20579, 20580, 20581, 20582, 20780);
        this.addQuestItem(3278, 3288, 3289, 3281, 3287, 3282, 3286, 3277, 3279, 3290, 3280, 3283, 3284, 3291, 3285, 3292);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("1")) {
            string2 = "30624-06.htm";
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            questState.giveItems(3277, 1L);
        } else if (string.equals("30624_1")) {
            string2 = "30624-05.htm";
        } else if (string.equals("30624_2")) {
            string2 = "30624-10.htm";
            questState.setCond(5);
            questState.takeItems(3278, -1L);
            questState.giveItems(3280, 1L);
        } else if (string.equals("30624_3")) {
            string2 = "30624-14.htm";
            questState.setCond(9);
            questState.takeItems(3282, -1L);
            questState.giveItems(3283, 1L);
        } else if (string.equals("30625_1")) {
            string2 = "30625-02.htm";
        } else if (string.equals("30625_2")) {
            string2 = "30625-03.htm";
            questState.setCond(2);
            questState.takeItems(3277, -1L);
            questState.giveItems(3279, 1L);
        } else if (string.equals("30093_1")) {
            string2 = "30093-02.htm";
            questState.setCond(6);
            questState.takeItems(3280, -1L);
            questState.giveItems(3281, 1L);
        } else if (string.equals("30196_1")) {
            string2 = "30196-02.htm";
        } else if (string.equals("30196_2")) {
            string2 = "30196-03.htm";
            questState.setCond(10);
            questState.takeItems(3283, -1L);
            questState.giveItems(3284, 1L);
        } else if (string.equals("30196_3")) {
            string2 = "30196-06.htm";
            questState.setCond(12);
            questState.takeItems(3284, -1L);
            questState.takeItems(3291, -1L);
            questState.giveItems(3285, 1L);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        if (questState.getQuestItemsCount(3276) > 0L) {
            questState.exitCurrentQuest(true);
            return "completed";
        }
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        String string = "noquest";
        if (n == 30624) {
            if (n2 == 0) {
                ClassId classId = questState.getPlayer().getClassId();
                if (classId != ClassId.warrior && classId != ClassId.orc_raider) {
                    questState.exitCurrentQuest(true);
                    return "30624-01.htm";
                }
                if (questState.getPlayer().getLevel() < 39) {
                    questState.exitCurrentQuest(true);
                    return "30624-02.htm";
                }
                return classId == ClassId.warrior ? "30624-03.htm" : "30624-04.htm";
            }
            if (n2 == 1) {
                string = "30624-07.htm";
            } else if (n2 == 2 || n2 == 3) {
                string = "30624-08.htm";
            } else if (n2 == 4) {
                string = "30624-09.htm";
            } else if (n2 == 5) {
                string = "30624-11.htm";
            } else if (n2 == 6 || n2 == 7) {
                string = "30624-12.htm";
            } else if (n2 == 8) {
                string = "30624-13.htm";
            } else if (n2 == 9) {
                string = "30624-15.htm";
            } else if (n2 > 9 && n2 < 14) {
                string = "30624-16.htm";
            } else if (n2 == 14) {
                string = "30624-17.htm";
                questState.takeItems(3286, -1L);
                questState.giveItems(3276, 1L);
                if (!questState.getPlayer().getVarB("prof2.3")) {
                    questState.giveItems(7562, 8L);
                    questState.addExpAndSp(117454L, 25000L);
                    questState.getPlayer().setVar("prof2.3", "1", -1L);
                }
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(false);
            }
        } else {
            if (n2 == 0) {
                return "noquest";
            }
            if (n == 30625) {
                if (n2 == 1) {
                    string = "30625-01.htm";
                } else if (n2 == 2) {
                    string = "30625-04.htm";
                } else if (n2 == 3) {
                    string = "30625-05.htm";
                    questState.takeItems(3290, -1L);
                    questState.takeItems(3279, -1L);
                    questState.giveItems(3278, 1L);
                    questState.setCond(4);
                } else {
                    string = n2 == 4 ? "30625-06.htm" : "30625-07.htm";
                }
            } else if (n == 30093) {
                if (n2 == 5) {
                    string = "30093-01.htm";
                } else if (n2 == 6) {
                    string = "30093-03.htm";
                } else if (n2 == 7) {
                    string = "30093-04.htm";
                    questState.takeItems(3281, -1L);
                    questState.takeItems(3287, -1L);
                    questState.takeItems(3288, -1L);
                    questState.takeItems(3289, -1L);
                    questState.giveItems(3282, 1L);
                    questState.setCond(8);
                } else if (n2 == 8) {
                    string = "30093-05.htm";
                } else if (n2 > 8) {
                    string = "30093-06.htm";
                }
            } else if (n == 30196) {
                if (n2 == 9) {
                    string = "30196-01.htm";
                } else if (n2 == 10) {
                    string = "30196-04.htm";
                } else if (n2 == 11) {
                    string = "30196-05.htm";
                } else if (n2 == 12) {
                    string = "30196-07.htm";
                } else if (n2 == 13) {
                    string = "30196-08.htm";
                    questState.takeItems(3285, -1L);
                    questState.takeItems(3292, -1L);
                    questState.giveItems(3286, 1L);
                    questState.setCond(14);
                } else if (n2 == 13) {
                    string = "30196-09.htm";
                }
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getCond();
        if (n == 0) {
            return null;
        }
        int n2 = npcInstance.getNpcId();
        for (int[] nArray : DROPLIST) {
            if (nArray[2] != n2 || nArray[0] != n) continue;
            questState.rollAndGive(nArray[3], 1, 1, nArray[5], nArray[4]);
            for (int[] nArray2 : DROPLIST) {
                if (nArray2[0] != n || questState.getQuestItemsCount(nArray2[3]) >= (long)nArray2[5]) continue;
                return null;
            }
            questState.setCond(n + 1);
            return null;
        }
        return null;
    }
}

