/*
 * Decompiled with CFR 0.152.
 */
package quests;

import java.util.ArrayList;
import l2.commons.util.Rnd;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _233_TestOfWarspirit
extends Quest
implements ScriptFile {
    private static int aXI = 30510;
    private static int aYE = 30030;
    private static int aYF = 30436;
    private static int aYG = 30507;
    private static int aPz = 30515;
    private static int aWs = 30630;
    private static int aXQ = 30649;
    private static int aYH = 30682;
    private static int aYI = 20213;
    private static int aSs = 20214;
    private static int aYJ = 20215;
    private static int aYK = 20089;
    private static int aYL = 20090;
    private static int aYM = 20581;
    private static int aYN = 20582;
    private static int aLd = 20158;
    private static int aYO = 27108;
    private static int aYP = 20601;
    private static int aYQ = 20602;
    private static int aLD = 7562;
    private static int aYR = 2879;
    private static int aYS = 2880;
    private static int aYT = 2881;
    private static int aYU = 2882;
    private static int aYV = 2883;
    private static int aYW = 2884;
    private static int aYX = 2885;
    private static int aYY = 2886;
    private static int aYZ = 2887;
    private static int aZa = 2888;
    private static int aZb = 2889;
    private static int aZc = 2890;
    private static int aZd = 2891;
    private static int aZe = 2892;
    private static int aZf = 2893;
    private static int aZg = 2894;
    private static int aZh = 2895;
    private static int aZi = 2896;
    private static int aZj = 2897;
    private static int aZk = 2898;
    private static int aZl = 2899;
    private static int aZm = 2900;
    private static int aZn = 2901;
    private static int aZo = 2902;
    private static int aZp = 2903;
    private static int aZq = 2904;
    private static int aZr = 2905;
    private static int aZs = 2906;
    private static int aZt = 2907;
    private static int aZu = 2908;
    private static int aZv = 2909;
    private static int aZw = 2910;
    private static int aZx = 2911;
    private static int aZy = 2912;
    private static int aZz = 2913;
    private static int aZA = 2914;
    private static int[] aZB = new int[]{aZv, aZu, aZt, aZs, aZr};
    private static int[] aZC = new int[]{aZb, aZc, aZd, aZe, aZf};
    private static int[] aZD = new int[]{aZj, aZk, aZm, aZl};

    public _233_TestOfWarspirit() {
        super(0);
        this.addStartNpc(aXI);
        this.addTalkId(aYE);
        this.addTalkId(aYF);
        this.addTalkId(aYG);
        this.addTalkId(aPz);
        this.addTalkId(aWs);
        this.addTalkId(aXQ);
        this.addTalkId(aYH);
        this.addKillId(aYI);
        this.addKillId(aSs);
        this.addKillId(aYJ);
        this.addKillId(aYK);
        this.addKillId(aYL);
        this.addKillId(aYM);
        this.addKillId(aYN);
        this.addKillId(aLd);
        this.addKillId(aYO);
        this.addKillId(aYP);
        this.addKillId(aYQ);
        this.addQuestItem(aYS);
        this.addQuestItem(aYT);
        this.addQuestItem(aYU);
        this.addQuestItem(aYV);
        this.addQuestItem(aYW);
        this.addQuestItem(aYX);
        this.addQuestItem(aYY);
        this.addQuestItem(aYZ);
        this.addQuestItem(aZa);
        this.addQuestItem(aZb);
        this.addQuestItem(aZc);
        this.addQuestItem(aZd);
        this.addQuestItem(aZe);
        this.addQuestItem(aZf);
        this.addQuestItem(aZg);
        this.addQuestItem(aZh);
        this.addQuestItem(aZi);
        this.addQuestItem(aZj);
        this.addQuestItem(aZk);
        this.addQuestItem(aZl);
        this.addQuestItem(aZm);
        this.addQuestItem(aZn);
        this.addQuestItem(aZo);
        this.addQuestItem(aZp);
        this.addQuestItem(aZq);
        this.addQuestItem(aZr);
        this.addQuestItem(aZs);
        this.addQuestItem(aZt);
        this.addQuestItem(aZu);
        this.addQuestItem(aZv);
        this.addQuestItem(aZw);
        this.addQuestItem(aZx);
        this.addQuestItem(aZy);
        this.addQuestItem(aZz);
        this.addQuestItem(aZA);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        int n = questState.getState();
        if (string.equalsIgnoreCase("30510-05.htm") && n == 1) {
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("30630-04.htm") && n == 2) {
            questState.giveItems(aYV, 1L);
        } else if (string.equalsIgnoreCase("30682-02.htm") && n == 2) {
            questState.giveItems(aZa, 1L);
        } else if (string.equalsIgnoreCase("30515-02.htm") && n == 2) {
            questState.giveItems(aZh, 1L);
        } else if (string.equalsIgnoreCase("30507-02.htm") && n == 2) {
            questState.giveItems(aZo, 1L);
        } else if (string.equalsIgnoreCase("30030-04.htm") && n == 2) {
            questState.giveItems(aZp, 1L);
        } else if (string.equalsIgnoreCase("30649-03.htm") && n == 2 && questState.getQuestItemsCount(aYU) > 0L) {
            questState.takeItems(aYU, -1L);
            questState.takeItems(aZx, -1L);
            questState.takeItems(aZz, -1L);
            questState.takeItems(aZA, -1L);
            questState.takeItems(aYT, -1L);
            questState.takeItems(aZy, -1L);
            questState.giveItems(aYR, 1L);
            if (!questState.getPlayer().getVarB("prof2.3")) {
                questState.giveItems(aLD, 8L);
                questState.addExpAndSp(63483L, 17500L);
                questState.getPlayer().setVar("prof2.3", "1", -1L);
            }
            questState.playSound("ItemSound.quest_finish");
            questState.unset("cond");
            questState.exitCurrentQuest(true);
        }
        return string;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        if (questState.getQuestItemsCount(aYR) > 0L) {
            questState.exitCurrentQuest(true);
            return "completed";
        }
        int n = questState.getState();
        int n2 = npcInstance.getNpcId();
        if (n == 1) {
            if (n2 != aXI) {
                return "noquest";
            }
            if (questState.getPlayer().getRace() != Race.orc) {
                questState.exitCurrentQuest(true);
                return "30510-01.htm";
            }
            if (questState.getPlayer().getClassId().getId() != 50) {
                questState.exitCurrentQuest(true);
                return "30510-02.htm";
            }
            if (questState.getPlayer().getLevel() < 39) {
                questState.exitCurrentQuest(true);
                return "30510-03.htm";
            }
            questState.setCond(0);
            return "30510-04.htm";
        }
        if (n != 2 || questState.getCond() != 1) {
            return "noquest";
        }
        if (n2 == aXI) {
            if (questState.getQuestItemsCount(aYS) > 0L) {
                if (questState.getQuestItemsCount(aYT) < 13L) {
                    return "30510-08.htm";
                }
                questState.takeItems(aYS, -1L);
                questState.giveItems(aYU, 1L);
                questState.giveItems(aZx, 1L);
                questState.giveItems(aZz, 1L);
                questState.giveItems(aZA, 1L);
                questState.giveItems(aZy, 1L);
                questState.playSound("ItemSound.quest_middle");
                return "30510-09.htm";
            }
            if (questState.getQuestItemsCount(aYU) > 0L) {
                return "30510-10.htm";
            }
            if (questState.getQuestItemsCount(aYZ) == 0L || questState.getQuestItemsCount(aZn) == 0L || questState.getQuestItemsCount(aZw) == 0L || questState.getQuestItemsCount(aZg) == 0L) {
                return "30510-06.htm";
            }
            questState.takeItems(aYZ, -1L);
            questState.takeItems(aZn, -1L);
            questState.takeItems(aZw, -1L);
            questState.takeItems(aZg, -1L);
            questState.giveItems(aYS, 1L);
            questState.playSound("ItemSound.quest_middle");
            return "30510-07.htm";
        }
        if (n2 == aWs) {
            if (questState.getQuestItemsCount(aYV) > 0L) {
                if (questState.getQuestItemsCount(aYW) < 10L || questState.getQuestItemsCount(aYX) < 10L || questState.getQuestItemsCount(aYY) < 10L) {
                    return "30630-05.htm";
                }
                questState.takeItems(aYV, -1L);
                questState.takeItems(aYW, -1L);
                questState.takeItems(aYX, -1L);
                questState.takeItems(aYY, -1L);
                questState.giveItems(aYZ, 1L);
                questState.playSound("ItemSound.quest_middle");
                return "30630-06.htm";
            }
            if (questState.getQuestItemsCount(aYZ) == 0L && questState.getQuestItemsCount(aZx) == 0L && questState.getQuestItemsCount(aYS) == 0L) {
                return "30630-01.htm";
            }
            return "30630-07.htm";
        }
        if (n2 == aYH) {
            if (questState.getQuestItemsCount(aZa) > 0L) {
                for (int n3 : aZC) {
                    if (questState.getQuestItemsCount(n3) != 0L) continue;
                    return "30682-03.htm";
                }
                questState.takeItems(aZa, -1L);
                for (int n3 : aZC) {
                    if (questState.getQuestItemsCount(n3) != 0L) continue;
                    questState.takeItems(n3, -1L);
                }
                questState.giveItems(aZg, 1L);
                questState.playSound("ItemSound.quest_middle");
                return "30682-04.htm";
            }
            if (questState.getQuestItemsCount(aZg) == 0L && questState.getQuestItemsCount(aZy) == 0L && questState.getQuestItemsCount(aYS) == 0L) {
                return "30682-01.htm";
            }
            return "30682-05.htm";
        }
        if (n2 == aPz) {
            if (questState.getQuestItemsCount(aZh) > 0L) {
                if (questState.getQuestItemsCount(aZi) == 0L) {
                    return "30515-03.htm";
                }
                for (int n4 : aZD) {
                    if (questState.getQuestItemsCount(n4) != 0L) continue;
                    return "30515-03.htm";
                }
                questState.takeItems(aZh, -1L);
                questState.takeItems(aZi, -1L);
                for (int n4 : aZD) {
                    if (questState.getQuestItemsCount(n4) != 0L) continue;
                    questState.takeItems(n4, -1L);
                }
                questState.giveItems(aZn, 1L);
                questState.playSound("ItemSound.quest_middle");
                return "30515-04.htm";
            }
            if (questState.getQuestItemsCount(aZn) == 0L && questState.getQuestItemsCount(aZz) == 0L && questState.getQuestItemsCount(aYS) == 0L) {
                return "30515-01.htm";
            }
            if (questState.getQuestItemsCount(aZo) == 0L && (questState.getQuestItemsCount(aZA) > 0L || questState.getQuestItemsCount(aYU) > 0L || questState.getQuestItemsCount(aZx) > 0L || questState.getQuestItemsCount(aZz) > 0L || questState.getQuestItemsCount(aYT) > 0L || questState.getQuestItemsCount(aZy) > 0L)) {
                return "30515-05.htm";
            }
        }
        if (n2 == aYG) {
            if (questState.getQuestItemsCount(aZo) > 0L) {
                if (questState.getQuestItemsCount(aZq) == 0L) {
                    return questState.getQuestItemsCount(aZp) == 0L ? "30507-03.htm" : "30507-04.htm";
                }
                if (questState.getQuestItemsCount(aZp) == 0L) {
                    for (int n5 : aZB) {
                        if (questState.getQuestItemsCount(n5) != 0L) continue;
                        return "30507-05.htm";
                    }
                    questState.takeItems(aZo, -1L);
                    questState.takeItems(aZq, -1L);
                    for (int n5 : aZB) {
                        if (questState.getQuestItemsCount(n5) != 0L) continue;
                        questState.takeItems(n5, -1L);
                    }
                    questState.giveItems(aZw, 1L);
                    questState.playSound("ItemSound.quest_middle");
                    return "30507-06.htm";
                }
            } else {
                if (questState.getQuestItemsCount(aZw) == 0L && questState.getQuestItemsCount(aZA) == 0L && questState.getQuestItemsCount(aYS) == 0L) {
                    return "30507-01.htm";
                }
                return "30507-07.htm";
            }
        }
        if (n2 == aYE) {
            if (questState.getQuestItemsCount(aZo) > 0L) {
                if (questState.getQuestItemsCount(aZq) == 0L) {
                    return questState.getQuestItemsCount(aZp) == 0L ? "30030-01.htm" : "30030-05.htm";
                }
                if (questState.getQuestItemsCount(aZp) == 0L) {
                    return "30030-06.htm";
                }
            } else if (questState.getQuestItemsCount(aZw) == 0L && questState.getQuestItemsCount(aZA) == 0L && questState.getQuestItemsCount(aYS) == 0L) {
                return "30030-07.htm";
            }
        }
        if (n2 == aYF) {
            if (questState.getQuestItemsCount(aZo) > 0L) {
                if (questState.getQuestItemsCount(aZq) == 0L && questState.getQuestItemsCount(aZp) > 0L) {
                    questState.takeItems(aZp, -1L);
                    questState.giveItems(aZq, 1L);
                    questState.playSound("ItemSound.quest_middle");
                    return "30436-01.htm";
                }
                if (questState.getQuestItemsCount(aZp) == 0L && questState.getQuestItemsCount(aZq) > 0L) {
                    return "30436-02.htm";
                }
            } else if (questState.getQuestItemsCount(aZw) == 0L && questState.getQuestItemsCount(aZA) == 0L && questState.getQuestItemsCount(aYS) == 0L) {
                return "30436-03.htm";
            }
        }
        if (n2 == aXQ && questState.getQuestItemsCount(aYU) > 0L) {
            return "30649-01.htm";
        }
        return "noquest";
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getState() != 2 || questState.getCond() < 1) {
            return null;
        }
        int n = npcInstance.getNpcId();
        if (n == aYI && questState.getQuestItemsCount(aYV) > 0L && questState.getQuestItemsCount(aYW) < 10L) {
            questState.giveItems(aYW, 1L);
            questState.playSound(questState.getQuestItemsCount(aYW) == 10L ? "ItemSound.quest_middle" : "ItemSound.quest_itemget");
        } else if (n == aSs && questState.getQuestItemsCount(aYV) > 0L && questState.getQuestItemsCount(aYX) < 10L) {
            questState.giveItems(aYX, 1L);
            questState.playSound(questState.getQuestItemsCount(aYX) == 10L ? "ItemSound.quest_middle" : "ItemSound.quest_itemget");
        } else if (n == aYJ && questState.getQuestItemsCount(aYV) > 0L && questState.getQuestItemsCount(aYY) < 10L) {
            questState.giveItems(aYY, 1L);
            questState.playSound(questState.getQuestItemsCount(aYY) == 10L ? "ItemSound.quest_middle" : "ItemSound.quest_itemget");
        } else if ((n == aYK || n == aYL) && questState.getQuestItemsCount(aZo) > 0L) {
            ArrayList<Integer> arrayList = new ArrayList<Integer>();
            for (int n2 : aZB) {
                if (questState.getQuestItemsCount(n2) != 0L) continue;
                arrayList.add(n2);
            }
            if (arrayList.size() > 0 && Rnd.chance(30)) {
                int n3 = (Integer)arrayList.get(Rnd.get(arrayList.size()));
                questState.giveItems(n3, 1L);
                questState.playSound(arrayList.size() == 1 ? "ItemSound.quest_middle" : "ItemSound.quest_itemget");
            }
            arrayList.clear();
            arrayList = null;
        } else if ((n == aYM || n == aYN) && questState.getQuestItemsCount(aZa) > 0L) {
            ArrayList<Integer> arrayList = new ArrayList<Integer>();
            for (int n4 : aZC) {
                if (questState.getQuestItemsCount(n4) != 0L) continue;
                arrayList.add(n4);
            }
            if (arrayList.size() > 0 && Rnd.chance(25)) {
                int n5 = (Integer)arrayList.get(Rnd.get(arrayList.size()));
                questState.giveItems(n5, 1L);
                questState.playSound(arrayList.size() == 1 ? "ItemSound.quest_middle" : "ItemSound.quest_itemget");
            }
            arrayList.clear();
            arrayList = null;
        } else if (n == aLd && questState.getQuestItemsCount(aZh) > 0L) {
            ArrayList<Integer> arrayList = new ArrayList<Integer>();
            for (int n6 : aZD) {
                if (questState.getQuestItemsCount(n6) != 0L) continue;
                arrayList.add(n6);
            }
            if (arrayList.size() > 0 && Rnd.chance(30)) {
                int n7 = (Integer)arrayList.get(Rnd.get(arrayList.size()));
                questState.giveItems(n7, 1L);
                questState.playSound(arrayList.size() == 1 && questState.getQuestItemsCount(aZi) > 0L ? "ItemSound.quest_middle" : "ItemSound.quest_itemget");
            }
            arrayList.clear();
            arrayList = null;
        } else if (n == aYO && questState.getQuestItemsCount(aZh) > 0L && questState.getQuestItemsCount(aZi) == 0L && Rnd.chance(30)) {
            questState.giveItems(aZi, 1L);
            boolean bl = true;
            for (int n8 : aZD) {
                if (questState.getQuestItemsCount(n8) != 0L) continue;
                bl = false;
                break;
            }
            questState.playSound(bl ? "ItemSound.quest_middle" : "ItemSound.quest_itemget");
        } else if ((n == aYP || n == aYQ) && questState.getQuestItemsCount(aYS) > 0L && questState.getQuestItemsCount(aYT) < 13L && Rnd.chance(n == aYP ? 30 : 50)) {
            questState.giveItems(aYT, 1L);
            questState.playSound(questState.getQuestItemsCount(aYT) == 13L ? "ItemSound.quest_middle" : "ItemSound.quest_itemget");
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
}

