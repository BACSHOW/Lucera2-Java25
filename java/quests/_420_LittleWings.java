/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _420_LittleWings
extends Quest
implements ScriptFile {
    private static final int bBt = 30829;
    private static final int aLs = 30610;
    private static final int bBu = 30711;
    private static final int aLq = 30608;
    private static final int bBv = 30747;
    private static final int bBw = 30748;
    private static final int bBx = 30749;
    private static final int bBy = 30750;
    private static final int bBz = 30751;
    private static final int bBA = 30752;
    private static final int bBB = 20589;
    private static final int bBC = 20599;
    private static final int bBD = 20231;
    private static final int aXR = 20233;
    private static final int aLC = 20580;
    private static final int bBE = 20551;
    private static final int aXS = 20270;
    private static final int bBF = 20202;
    private static int bBG = 1870;
    private static int bBH = 1871;
    private static int bBI = 1873;
    private static int bBJ = 1875;
    private static int bBK = 2130;
    private static int bBL = 2131;
    private static int bBM = 3500;
    private static int bBN = 3502;
    private static int bBO = 3912;
    private static int bBP = 3918;
    private static int bBQ = 4038;
    private static int bBR = 3499;
    private static int bBS = 3816;
    private static int bBT = 3817;
    private static int bBU = 3818;
    private static int bBV = 3819;
    private static int bBW = 3820;
    private static int bBX = 3821;
    private static int bBY = 3822;
    private static int bBZ = 3824;
    private static int bCa = 3826;
    private static int bCb = 3828;
    private static int bCc = 3830;
    private static int bCd = 3823;
    private static int bCe = 3825;
    private static int bCf = 3827;
    private static int bCg = 3829;
    private static int bCh = 3831;
    private static final int bCi = 30;
    private static final int bCj = 50;
    private static final int bCk = 35;
    private static int[][] bCl = new int[][]{{bBG, 10}, {bBH, 10}, {bBK, 1}, {bBI, 3}, {bBW, 10}};
    private static int[][] bCm = new int[][]{{bBG, 10}, {bBH, 10}, {bBL, 1}, {bBJ, 1}, {bBI, 5}, {bBW, 20}};
    private static final int[][] bCn = new int[][]{{20580, 30748, bBY, bCd}, {20233, 30749, bBZ, bCe}, {20551, 30750, bCa, bCf}, {20270, 30751, bCb, bCg}, {20202, 30752, bCc, bCh}};

    public _420_LittleWings() {
        super(0);
        this.addStartNpc(30829);
        this.addTalkId(30610);
        this.addTalkId(30747);
        this.addTalkId(30711);
        this.addTalkId(30608);
        this.addKillId(20231);
        int n = 20589;
        while (n <= 20599) {
            this.addKillId(n++);
        }
        for (int[] nArray : bCn) {
            this.addTalkId(nArray[1]);
            this.addKillId(nArray[0]);
        }
        this.addQuestItem(bBR);
        this.addQuestItem(bBS);
        this.addQuestItem(bBT);
        this.addQuestItem(bBU);
        this.addQuestItem(bBV);
        this.addQuestItem(bBW);
        this.addQuestItem(bBX);
        this.addQuestItem(bBY);
        this.addQuestItem(bBZ);
        this.addQuestItem(bCa);
        this.addQuestItem(bCb);
        this.addQuestItem(bCc);
        this.addQuestItem(bCd);
        this.addQuestItem(bCe);
        this.addQuestItem(bCf);
        this.addQuestItem(bCg);
        this.addQuestItem(bCh);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        int n = questState.getState();
        int n2 = questState.getCond();
        if (string.equalsIgnoreCase("30829-02.htm") && n == 1) {
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
            return string;
        } else if ((string.equalsIgnoreCase("30610-05.htm") || string.equalsIgnoreCase("30610-12.htm")) && n == 2 && n2 == 1) {
            questState.setCond(2);
            questState.takeItems(bBS, -1L);
            questState.takeItems(bBT, -1L);
            questState.takeItems(bBU, -1L);
            questState.takeItems(bBV, -1L);
            questState.giveItems(bBU, 1L);
            questState.playSound("ItemSound.quest_middle");
            return string;
        } else if ((string.equalsIgnoreCase("30610-06.htm") || string.equalsIgnoreCase("30610-13.htm")) && n == 2 && n2 == 1) {
            questState.setCond(2);
            questState.takeItems(bBS, -1L);
            questState.takeItems(bBT, -1L);
            questState.takeItems(bBU, -1L);
            questState.takeItems(bBV, -1L);
            questState.giveItems(bBV, 1L);
            questState.playSound("ItemSound.quest_middle");
            return string;
        } else if (string.equalsIgnoreCase("30608-03.htm") && n == 2 && n2 == 2 && questState.getQuestItemsCount(bBU) > 0L) {
            if (!_420_LittleWings.CheckFairyStoneItems(questState, bCl)) return "30608-01.htm";
            questState.setCond(3);
            _420_LittleWings.TakeFairyStoneItems(questState, bCl);
            questState.giveItems(bBS, 1L);
            questState.playSound("ItemSound.quest_middle");
            return string;
        } else if (string.equalsIgnoreCase("30608-03a.htm") && n == 2 && n2 == 2 && questState.getQuestItemsCount(bBV) > 0L) {
            if (!_420_LittleWings.CheckFairyStoneItems(questState, bCm)) return "30608-01a.htm";
            questState.setCond(3);
            _420_LittleWings.TakeFairyStoneItems(questState, bCm);
            questState.giveItems(bBT, 1L);
            questState.playSound("ItemSound.quest_middle");
            return string;
        } else {
            if (string.equalsIgnoreCase("30711-03.htm") && n == 2 && n2 == 3 && questState.getQuestItemsCount(bBS) + questState.getQuestItemsCount(bBT) > 0L) {
                questState.setCond(4);
                questState.playSound("ItemSound.quest_middle");
                if (questState.getQuestItemsCount(bBT) > 0L) {
                    if (questState.getInt("broken") != 1) return "30711-03a.htm";
                    return "30711-04a.htm";
                }
                if (questState.getInt("broken") != 1) return string;
                return "30711-04.htm";
            }
            if (string.equalsIgnoreCase("30747-02.htm") && n == 2 && n2 == 4 && questState.getQuestItemsCount(bBS) > 0L) {
                questState.takeItems(bBS, -1L);
                questState.set("takedStone", "1");
                return string;
            } else if (string.equalsIgnoreCase("30747-02a.htm") && n == 2 && n2 == 4 && questState.getQuestItemsCount(bBT) > 0L) {
                questState.takeItems(bBT, -1L);
                questState.set("takedStone", "2");
                questState.giveItems(bBR, 1L);
                questState.playSound("ItemSound.quest_itemget");
                return string;
            } else if (string.equalsIgnoreCase("30747-04.htm") && n == 2 && n2 == 4 && questState.getInt("takedStone") > 0) {
                questState.setCond(5);
                questState.unset("takedStone");
                questState.giveItems(bBX, 1L);
                questState.playSound("ItemSound.quest_itemget");
                return string;
            } else if (string.equalsIgnoreCase("30748-02.htm") && n2 == 5 && n == 2 && questState.getQuestItemsCount(bBX) > 0L) {
                questState.setCond(6);
                questState.takeItems(bBX, -1L);
                questState.giveItems(3822, 1L);
                questState.playSound("ItemSound.quest_itemget");
                return string;
            } else if (string.equalsIgnoreCase("30749-02.htm") && n2 == 5 && n == 2 && questState.getQuestItemsCount(bBX) > 0L) {
                questState.setCond(6);
                questState.takeItems(bBX, -1L);
                questState.giveItems(3824, 1L);
                questState.playSound("ItemSound.quest_itemget");
                return string;
            } else if (string.equalsIgnoreCase("30750-02.htm") && n2 == 5 && n == 2 && questState.getQuestItemsCount(bBX) > 0L) {
                questState.setCond(6);
                questState.takeItems(bBX, -1L);
                questState.giveItems(3826, 1L);
                questState.playSound("ItemSound.quest_itemget");
                return string;
            } else if (string.equalsIgnoreCase("30751-02.htm") && n2 == 5 && n == 2 && questState.getQuestItemsCount(bBX) > 0L) {
                questState.setCond(6);
                questState.takeItems(bBX, -1L);
                questState.giveItems(3828, 1L);
                questState.playSound("ItemSound.quest_itemget");
                return string;
            } else if (string.equalsIgnoreCase("30752-02.htm") && n2 == 5 && n == 2 && questState.getQuestItemsCount(bBX) > 0L) {
                questState.setCond(6);
                questState.takeItems(bBX, -1L);
                questState.giveItems(3830, 1L);
                questState.playSound("ItemSound.quest_itemget");
                return string;
            } else if (string.equalsIgnoreCase("30747-09.htm") && n == 2 && n2 == 7) {
                int n3 = 0;
                for (int[] nArray : bCn) {
                    if (questState.getQuestItemsCount(nArray[2]) != 0L || questState.getQuestItemsCount(nArray[3]) < 1L) continue;
                    n3 = nArray[3];
                    break;
                }
                if (n3 == 0) {
                    return "noquest";
                }
                questState.takeItems(n3, -1L);
                questState.giveItems(Rnd.get(bBM, bBN), 1L);
                if (questState.getQuestItemsCount(bBR) > 0L) {
                    questState.playSound("ItemSound.quest_middle");
                    return "30747-09a.htm";
                }
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                return string;
            } else if (string.equalsIgnoreCase("30747-10.htm") && n == 2 && n2 == 7) {
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                return string;
            } else {
                if (!string.equalsIgnoreCase("30747-11.htm")) return string;
                if (n != 2) return string;
                if (n2 != 7) return string;
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                if (questState.getQuestItemsCount(bBR) == 0L) {
                    return "30747-10.htm";
                }
                questState.takeItems(bBR, -1L);
                if (Rnd.chance(35)) {
                    int n4 = bBO + Rnd.get((int)questState.getRateQuestsReward());
                    if (n4 > bBP) {
                        n4 = bBP;
                    }
                    questState.giveItems(n4, 1L);
                    return string;
                } else {
                    questState.giveItems(bBQ, 20L, true);
                }
            }
        }
        return string;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getState();
        int n2 = npcInstance.getNpcId();
        if (n == 1) {
            if (n2 != 30829) {
                return "noquest";
            }
            if (questState.getPlayer().getLevel() < 35) {
                questState.exitCurrentQuest(true);
                return "30829-00.htm";
            }
            questState.setCond(0);
            return "30829-01.htm";
        }
        if (n != 2) {
            return "noquest";
        }
        int n3 = questState.getCond();
        int n4 = questState.getInt("broken");
        if (n2 == 30829) {
            if (n3 == 1) {
                return "30829-02.htm";
            }
            return "30829-03.htm";
        }
        if (n2 == 30610) {
            if (n3 == 1) {
                return n4 == 1 ? "30610-10.htm" : "30610-01.htm";
            }
            if (n3 == 2) {
                return "30610-07.htm";
            }
            if (n3 == 3) {
                return n4 == 1 ? "30610-14.htm" : "30610-08.htm";
            }
            if (n3 == 4) {
                return "30610-09.htm";
            }
            if (n3 > 4) {
                return "30610-11.htm";
            }
        }
        if (n2 == 30608) {
            if (n3 == 2) {
                if (questState.getQuestItemsCount(bBV) > 0L) {
                    return _420_LittleWings.CheckFairyStoneItems(questState, bCm) ? "30608-02a.htm" : "30608-01a.htm";
                }
                if (questState.getQuestItemsCount(bBU) > 0L) {
                    return _420_LittleWings.CheckFairyStoneItems(questState, bCl) ? "30608-02.htm" : "30608-01.htm";
                }
            } else if (n3 > 2) {
                return "30608-04.htm";
            }
        }
        if (n2 == 30711) {
            if (n3 == 1 && n4 == 1) {
                return "30711-06.htm";
            }
            if (n3 == 2 && n4 == 1) {
                return "30711-07.htm";
            }
            if (n3 == 3 && questState.getQuestItemsCount(bBS) + questState.getQuestItemsCount(bBT) > 0L) {
                return "30711-01.htm";
            }
            if (n3 >= 4 && questState.getQuestItemsCount(bBT) > 0L) {
                return "30711-05a.htm";
            }
            if (n3 >= 4 && questState.getQuestItemsCount(bBS) > 0L) {
                return "30711-05.htm";
            }
        }
        if (n2 == 30747) {
            if (n3 == 4 && questState.getQuestItemsCount(bBT) > 0L) {
                return "30747-01a.htm";
            }
            if (n3 == 4 && questState.getQuestItemsCount(bBS) > 0L) {
                return "30747-01.htm";
            }
            if (n3 == 5) {
                return "30747-05.htm";
            }
            if (n3 == 6) {
                for (int[] nArray : bCn) {
                    if (questState.getQuestItemsCount(nArray[2]) != 0L || questState.getQuestItemsCount(nArray[3]) < 20L) continue;
                    return "30747-07.htm";
                }
                return "30747-06.htm";
            }
            if (n3 == 7) {
                for (int[] nArray : bCn) {
                    if (questState.getQuestItemsCount(nArray[2]) != 0L || questState.getQuestItemsCount(nArray[3]) < 1L) continue;
                    return "30747-08.htm";
                }
            }
        }
        if (n2 >= 30748 && n2 <= 30752) {
            if (n3 == 5 && questState.getQuestItemsCount(bBX) > 0L) {
                return String.valueOf(n2) + "-01.htm";
            }
            if (n3 == 6 && questState.getQuestItemsCount(_420_LittleWings.Z(n2)) > 0L) {
                int n5 = _420_LittleWings.aa(n2);
                if (questState.getQuestItemsCount(n5) < 20L) {
                    return String.valueOf(n2) + "-03.htm";
                }
                questState.takeItems(_420_LittleWings.Z(n2), -1L);
                questState.takeItems(n5, -1L);
                questState.giveItems(n5, 1L);
                questState.setCond(7);
                return String.valueOf(n2) + "-04.htm";
            }
            if (n3 == 7 && questState.getQuestItemsCount(_420_LittleWings.aa(n2)) == 1L) {
                return String.valueOf(n2) + "-05.htm";
            }
        }
        return "noquest";
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n;
        if (questState.getState() != 2) {
            return null;
        }
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getCond();
        if (n3 == 2 && n2 == 20231) {
            int n4 = _420_LittleWings.getNeededSkins(questState);
            if (questState.getQuestItemsCount(bBW) < (long)n4 && Rnd.chance(30)) {
                questState.giveItems(bBW, 1L);
                questState.playSound(questState.getQuestItemsCount(bBW) < (long)n4 ? "ItemSound.quest_itemget" : "ItemSound.quest_middle");
            }
            return null;
        }
        if (n2 >= 20589 && n2 <= 20599 && questState.getQuestItemsCount(bBT) > 0L) {
            questState.takeItems(bBT, 1L);
            questState.set("broken", "1");
            questState.setCond(1);
            return "You lost fairy stone deluxe!";
        }
        if (n3 == 6 && (n = _420_LittleWings.ab(n2)) > 0 && questState.getQuestItemsCount(_420_LittleWings.Z(n)) > 0L && questState.getQuestItemsCount(_420_LittleWings.aa(n)) < 20L && Rnd.chance(50)) {
            questState.giveItems(_420_LittleWings.aa(n), 1L);
            questState.playSound(questState.getQuestItemsCount(_420_LittleWings.aa(n)) < 20L ? "ItemSound.quest_itemget" : "ItemSound.quest_middle");
        }
        return null;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    private static int Z(int n) {
        for (int[] nArray : bCn) {
            if (n != nArray[1]) continue;
            return nArray[2];
        }
        return 0;
    }

    private static int aa(int n) {
        for (int[] nArray : bCn) {
            if (n != nArray[1]) continue;
            return nArray[3];
        }
        return 0;
    }

    private static int ab(int n) {
        for (int[] nArray : bCn) {
            if (n != nArray[0]) continue;
            return nArray[1];
        }
        return 0;
    }

    public static int getNeededSkins(QuestState questState) {
        if (questState.getQuestItemsCount(bBV) > 0L) {
            return 20;
        }
        if (questState.getQuestItemsCount(bBU) > 0L) {
            return 10;
        }
        return -1;
    }

    public static boolean CheckFairyStoneItems(QuestState questState, int[][] nArray) {
        for (int[] nArray2 : nArray) {
            if (questState.getQuestItemsCount(nArray2[0]) >= (long)nArray2[1]) continue;
            return false;
        }
        return true;
    }

    public static void TakeFairyStoneItems(QuestState questState, int[][] nArray) {
        for (int[] nArray2 : nArray) {
            questState.takeItems(nArray2[0], nArray2[1]);
        }
    }
}

