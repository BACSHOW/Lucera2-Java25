/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _336_CoinOfMagic
extends Quest
implements ScriptFile {
    private static final int blc = 3811;
    private static final int bld = 3812;
    private static final int ble = 3813;
    private static final int blf = 3814;
    private static final int blg = 3815;
    private static final int blh = 3472;
    private static final int bli = 3473;
    private static final int blj = 3474;
    private static final int blk = 3475;
    private static final int bll = 3476;
    private static final int blm = 3477;
    private static final int bln = 3478;
    private static final int blo = 3479;
    private static final int blp = 3480;
    private static final int blq = 3481;
    private static final int blr = 3482;
    private static final int bls = 3483;
    private static final int blt = 3484;
    private static final int blu = 3485;
    private static final int blv = 3486;
    private static final int blw = 3487;
    private static final int blx = 3488;
    private static final int bly = 3489;
    private static final int blz = 3490;
    private static final int blA = 3491;
    private static final int blB = 3492;
    private static final int blC = 3493;
    private static final int blD = 3494;
    private static final int blE = 3495;
    private static final int blF = 3496;
    private static final int blG = 3497;
    private static final int blH = 3498;
    private static final int[] blI = new int[]{3472, 3482, 3490};
    private static final int blJ = 30232;
    private static final int blK = 30702;
    private static final int blL = 30696;
    private static final int blM = 30183;
    private static final int blN = 30200;
    private static final int blO = 30165;
    private static final int blP = 30847;
    private static final int blQ = 30092;
    private static final int blR = 30078;
    private static final int aMP = 30688;
    private static final int blS = 30673;
    private static final int aQy = 20584;
    private static final int aQz = 20585;
    private static final int aQB = 20587;
    private static final int aSu = 20604;
    private static final int blT = 20678;
    private static final int blU = 20663;
    private static final int aLx = 20235;
    private static final int aQx = 20583;
    private static final int blV = 20146;
    private static final int blW = 20240;
    private static final int blX = 20245;
    private static final int blY = 20568;
    private static final int blZ = 20569;
    private static final int bma = 20685;
    private static final int bmb = 20572;
    private static final int bmc = 20161;
    private static final int bmd = 20575;
    private static final int bme = 20645;
    private static final int bmf = 20644;
    private static final int bmg = 20279;
    private static final int bmh = 20280;
    private static final int bmi = 20284;
    private static final int bmj = 20276;
    private static final int bmk = 21003;
    private static final int bml = 21006;
    private static final int bmm = 21008;
    private static final int bmn = 20674;
    private static final int bmo = 21276;
    private static final int bmp = 21275;
    private static final int bmq = 21274;
    private static final int bmr = 21278;
    private static final int bms = 21279;
    private static final int bmt = 21280;
    private static final int bmu = 21282;
    private static final int bmv = 21284;
    private static final int bmw = 21283;
    private static final int bmx = 21287;
    private static final int bmy = 21288;
    private static final int bmz = 21286;
    private static final int bmA = 21521;
    private static final int bmB = 21526;
    private static final int bmC = 21531;
    private static final int bmD = 21539;
    private static final int bmE = 20954;
    private static final int bmF = 20960;
    private static final int bmG = 20957;
    private static final int bmH = 20959;
    private static final int[][] bmI = new int[][]{new int[0], new int[0], {3492, 3474, 3476, 3495, 3484, 3486}, {3473, 3485, 3491, 3475, 3483, 3494}};
    private static final int[][] bmJ = new int[][]{{30696, 3}, {30673, 3}, {30183, 3}, {30165, 2}, {30200, 2}, {30688, 2}, {30847, 1}, {30092, 1}, {30078, 1}};
    private static final int[][] DROPLIST = new int[][]{{20584, 3472}, {20585, 3472}, {20587, 3472}, {20604, 3472}, {20678, 3472}, {20663, 3472}, {20583, 3482}, {20235, 3482}, {20146, 3482}, {20240, 3482}, {20245, 3482}, {20568, 3490}, {20569, 3490}, {20685, 3490}, {20572, 3490}, {20161, 3490}, {20575, 3490}};
    private static final int[] bmK = new int[]{21003, 21006, 21008, 20674, 21276, 21275, 21274, 21278, 21279, 21280, 21282, 21284, 21283, 21287, 21288, 21286, 21521, 21526, 21531, 21539, 20954, 20960, 20957, 20959};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _336_CoinOfMagic() {
        super(1);
        this.addStartNpc(30232);
        this.addTalkId(30232, 30702, 30696, 30183, 30200, 30165, 30847, 30092, 30078, 30688, 30673);
        for (int[] nArray : DROPLIST) {
            this.addKillId(nArray[0]);
        }
        this.addKillId(bmK);
        this.addKillId(20645);
        this.addKillId(20644);
        this.addQuestItem(3811, 3812, 3813, 3814, 3815);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getCond();
        if (string.equalsIgnoreCase("30702-06.htm")) {
            if (n < 7) {
                questState.setCond(7);
                questState.playSound("ItemSound.quest_accept");
            }
        } else if (string.equalsIgnoreCase("30232-22.htm")) {
            if (n < 6) {
                questState.setCond(6);
            }
        } else if (string.equalsIgnoreCase("30232-23.htm")) {
            if (n < 5) {
                questState.setCond(5);
            }
        } else if (string.equalsIgnoreCase("30702-02.htm")) {
            questState.setCond(2);
        } else if (string.equalsIgnoreCase("30232-05.htm")) {
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            questState.giveItems(3811, 1L);
            questState.setCond(1);
        } else if (string.equalsIgnoreCase("30232-04.htm") || string.equalsIgnoreCase("30232-18a.htm")) {
            questState.exitCurrentQuest(true);
            questState.playSound("ItemSound.quest_giveup");
        } else if (string.equalsIgnoreCase("raise")) {
            string2 = this.j(questState);
        }
        return string2;
    }

    private String j(QuestState questState) {
        String string;
        int n = questState.getInt("grade");
        if (n == 1) {
            string = "30232-15.htm";
        } else {
            int n2 = 0;
            for (int n3 : bmI[n]) {
                if (questState.getQuestItemsCount(n3) <= 0L) continue;
                ++n2;
            }
            if (n2 == 6) {
                for (int n3 : bmI[n]) {
                    questState.takeItems(n3, 1L);
                }
                string = "30232-" + this.str(19 - n) + ".htm";
                questState.takeItems(3812 + n, -1L);
                questState.giveItems(3811 + n, 1L);
                questState.set("grade", this.str(n - 1));
                if (n == 3) {
                    questState.setCond(9);
                } else if (n == 2) {
                    questState.setCond(11);
                }
                questState.playSound("ItemSound.quest_fanfare_middle");
            } else {
                string = "30232-" + this.str(16 - n) + ".htm";
                if (n == 3) {
                    questState.setCond(8);
                } else if (n == 2) {
                    questState.setCond(9);
                }
            }
        }
        return string;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string;
        block7: {
            int n;
            int n2;
            block11: {
                block12: {
                    block5: {
                        block10: {
                            block9: {
                                block8: {
                                    block6: {
                                        string = "noquest";
                                        n2 = npcInstance.getNpcId();
                                        int n3 = questState.getState();
                                        n = questState.getInt("grade");
                                        if (n2 != 30232) break block5;
                                        if (n3 != 1) break block6;
                                        if (questState.getPlayer().getLevel() < 40) {
                                            string = "30232-01.htm";
                                            questState.exitCurrentQuest(true);
                                        } else {
                                            string = "30232-02.htm";
                                        }
                                        break block7;
                                    }
                                    if (questState.getQuestItemsCount(3811) <= 0L) break block8;
                                    if (questState.getQuestItemsCount(3812) > 0L) {
                                        questState.takeItems(3812, -1L);
                                        questState.takeItems(3811, -1L);
                                        questState.giveItems(3815, 1L);
                                        questState.set("grade", "3");
                                        questState.setCond(4);
                                        questState.playSound("ItemSound.quest_fanfare_middle");
                                        string = "30232-07.htm";
                                    } else {
                                        string = "30232-06.htm";
                                    }
                                    break block7;
                                }
                                if (n != 3) break block9;
                                string = "30232-12.htm";
                                break block7;
                            }
                            if (n != 2) break block10;
                            string = "30232-11.htm";
                            break block7;
                        }
                        if (n != 1) break block7;
                        string = "30232-10.htm";
                        break block7;
                    }
                    if (n2 != 30702) break block11;
                    if (questState.getQuestItemsCount(3811) <= 0L || n != 0) break block12;
                    string = "30702-01.htm";
                    break block7;
                }
                if (n != 3) break block7;
                string = "30702-05.htm";
                break block7;
            }
            for (int[] nArray : bmJ) {
                if (n2 != nArray[0] || n > nArray[1]) continue;
                string = n2 + "-01.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getCond();
        int n2 = questState.getInt("grade");
        int n3 = npcInstance.getLevel() + n2 * 3 - 20;
        int n4 = npcInstance.getNpcId();
        if (n4 == 20645 || n4 == 20644) {
            if (n == 2 && questState.rollAndGive(3812, 1, 1, 1, 10.0 * npcInstance.getTemplate().rateHp)) {
                questState.setCond(3);
            }
            return null;
        }
        for (int[] nArray : DROPLIST) {
            if (nArray[0] != n4) continue;
            questState.rollAndGive(nArray[1], 1, n3);
            return null;
        }
        for (int n5 : bmK) {
            if (n5 != n4) continue;
            questState.rollAndGive(blI[Rnd.get(blI.length)], 1, (double)n3 * npcInstance.getTemplate().rateHp);
            return null;
        }
        return null;
    }
}

