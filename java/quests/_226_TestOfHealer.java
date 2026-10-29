/*
 * Decompiled with CFR 0.152.
 */
package quests;

import java.util.HashMap;
import java.util.Map;
import l2.commons.util.Rnd;
import l2.gameserver.model.base.ClassId;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _226_TestOfHealer
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
    private static final int aUp = 15;
    private static final int aUq = 16;
    private static final int aUr = 17;
    private static final int aUs = 18;
    private static final int aUt = 19;
    private static final int aUu = 20;
    private static final int aUv = 21;
    private static final int aUw = 22;
    private static final int aUx = 23;
    private static final int aUy = 30473;
    private static final int aUz = 30428;
    private static final int aUA = 30659;
    private static final int aUB = 30424;
    private static final int aUC = 30658;
    private static final int aUD = 30660;
    private static final int aUE = 30327;
    private static final int aUF = 30674;
    private static final int aUG = 30662;
    private static final int aUH = 30663;
    private static final int aUI = 30664;
    private static final int aUJ = 30661;
    private static final int aUK = 30665;
    private static final int aUL = 2810;
    private static final int aUM = 2811;
    private static final int aUN = 2812;
    private static final int aUO = 2813;
    private static final int aUP = 2814;
    private static final int aUQ = 2815;
    private static final int aUR = 2816;
    private static final int aUS = 2817;
    private static final int aUT = 2818;
    private static final int aUU = 2819;
    private static final int aUV = 2820;
    private static Map<Integer, Integer[]> aUW = new HashMap<Integer, Integer[]>();

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _226_TestOfHealer() {
        super(0);
        this.addStartNpc(30473);
        this.addTalkId(30327, 30424, 30428, 30473, 30658, 30659, 30660, 30661, 30662, 30663, 30664, 30665, 30674);
        this.addKillId(20150, 27123, 27124, 27125, 27127, 27134);
        this.addQuestItem(2810, 2811, 2812, 2813, 2814, 2815, 2816, 2817, 2818, 2819);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("1")) {
            string2 = "30473-04.htm";
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            questState.giveItems(2810, 1L);
        } else if (string.equalsIgnoreCase("30473_1")) {
            string2 = "30473-08.htm";
        } else if (string.equalsIgnoreCase("30473_2")) {
            string2 = "30473-09.htm";
            questState.takeItems(2813, -1L);
            questState.giveItems(2820, 1L);
            questState.addExpAndSp(37831L, 18750L);
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(false);
        } else if (string.equalsIgnoreCase("30428_1")) {
            string2 = "30428-02.htm";
            questState.setCond(2);
            questState.addSpawn(27134, -93254, 147559, -2679);
        } else if (string.equalsIgnoreCase("30658_1")) {
            if (questState.getQuestItemsCount(57) >= 100000L) {
                string2 = "30658-02.htm";
                questState.takeItems(57, 100000L);
                questState.giveItems(2812, 1L);
                questState.setCond(7);
            } else {
                string2 = "30658-05.htm";
            }
        } else if (string.equalsIgnoreCase("30658_2")) {
            questState.setCond(6);
            string2 = "30658-03.htm";
        } else if (string.equalsIgnoreCase("30660-03.htm")) {
            questState.takeItems(2812, 1L);
            questState.giveItems(2814, 1L);
            questState.setCond(8);
        } else if (string.equalsIgnoreCase("30674_1")) {
            string2 = "30674-02.htm";
            questState.setCond(11);
            questState.takeItems(2815, 1L);
            questState.addSpawn(27122);
            questState.addSpawn(27122);
            questState.addSpawn(27123);
            questState.playSound("Itemsound.quest_before_battle");
        } else if (string.equalsIgnoreCase("30665_1")) {
            string2 = "30665-02.htm";
            questState.takeItems(2816, 1L);
            questState.takeItems(2817, 1L);
            questState.takeItems(2818, 1L);
            questState.takeItems(2819, 1L);
            questState.giveItems(2811, 1L);
            questState.setCond(22);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        if (questState.getQuestItemsCount(2820) > 0L) {
            questState.exitCurrentQuest(true);
            return "completed";
        }
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 30473) {
            if (n2 == 0) {
                if (questState.getPlayer().getClassId() == ClassId.knight || questState.getPlayer().getClassId() == ClassId.cleric || questState.getPlayer().getClassId() == ClassId.oracle || questState.getPlayer().getClassId() == ClassId.elven_knight) {
                    string = questState.getPlayer().getLevel() >= 39 ? "30473-03.htm" : "30473-01.htm";
                } else {
                    string = "30473-02.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 23) {
                if (questState.getQuestItemsCount(2813) == 0L) {
                    string = "30473-06.htm";
                    questState.giveItems(2820, 1L);
                    string = "30690-08.htm";
                    if (!questState.getPlayer().getVarB("prof2.3")) {
                        questState.giveItems(7562, 8L);
                        questState.addExpAndSp(738283L, 50662L);
                        questState.giveItems(57, 133490L);
                        questState.getPlayer().setVar("prof2.3", "1", -1L);
                    }
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(false);
                } else {
                    string = "30473-07.htm";
                }
            } else {
                string = "30473-05.htm";
            }
        } else if (n == 30428) {
            if (n2 == 1) {
                string = "30428-01.htm";
            } else if (n2 == 3) {
                string = "30428-03.htm";
                questState.takeItems(2810, 1L);
                questState.setCond(4);
            } else if (n2 != 2) {
                string = "30428-04.htm";
            }
        } else if (n == 30659) {
            int n3 = Rnd.get(5);
            if (n3 == 0) {
                string = "30659-01.htm";
            } else if (n3 == 1) {
                string = "30659-02.htm";
            } else if (n3 == 2) {
                string = "30659-03.htm";
            } else if (n3 == 3) {
                string = "30659-04.htm";
            } else if (n3 == 4) {
                string = "30659-05.htm";
            }
        } else if (n == 30424) {
            if (n2 == 4) {
                string = "30424-01.htm";
                questState.setCond(5);
            } else {
                string = "30424-02.htm";
            }
        } else if (n == 30658) {
            if (n2 == 5) {
                string = "30658-01.htm";
            } else if (n2 == 7) {
                string = "30658-04.htm";
            } else if (n2 == 8) {
                string = "30658-06.htm";
                questState.giveItems(2813, 1L);
                questState.takeItems(2814, 1L);
                questState.setCond(9);
            } else if (n2 == 6) {
                questState.setCond(9);
                string = "30658-07.htm";
            } else if (n2 == 9) {
                string = "30658-07.htm";
            }
        } else if (n == 30660) {
            if (n2 == 7) {
                string = "30660-01.htm";
            } else if (n2 == 8) {
                string = "30660-04.htm";
            }
        } else if (n == 30327) {
            if (n2 == 9) {
                string = "30327-01.htm";
                questState.giveItems(2815, 1L);
                questState.setCond(10);
            } else if (n2 > 9 && n2 < 22) {
                string = "30327-02.htm";
            } else if (n2 == 22) {
                string = "30327-03.htm";
                questState.takeItems(2811, 1L);
                questState.setCond(23);
            }
        } else if (n == 30674) {
            if (n2 == 10 && questState.getQuestItemsCount(2815) > 0L) {
                string = "30674-01.htm";
            } else if (n2 == 12 && questState.getQuestItemsCount(2816) > 0L) {
                string = "30674-03.htm";
                questState.setCond(13);
            }
        } else if (n == 30662 || n == 30663 || n == 30664) {
            if (n2 == 13) {
                string = n + "-01.htm";
            } else if (n2 == 15) {
                string = n + "-02.htm";
            } else if (n2 == 20) {
                questState.setCond(21);
                string = n + "-03.htm";
            } else if (n2 == 21) {
                string = n + "-04.htm";
            }
        } else if (n == 30661) {
            if (n2 == 13) {
                string = "30661-01.htm";
                questState.addSpawn(27124);
                questState.addSpawn(27124);
                questState.addSpawn(27124);
                questState.playSound("Itemsound.quest_before_battle");
                questState.setCond(14);
            } else if (n2 == 15) {
                string = "30661-02.htm";
                questState.addSpawn(27125);
                questState.addSpawn(27125);
                questState.addSpawn(27125);
                questState.playSound("Itemsound.quest_before_battle");
                questState.setCond(16);
            } else if (n2 == 17) {
                string = "30661-03.htm";
                questState.addSpawn(27126);
                questState.addSpawn(27126);
                questState.addSpawn(27127);
                questState.playSound("Itemsound.quest_before_battle");
                questState.setCond(18);
            } else if (n2 == 19) {
                string = "30661-04.htm";
                questState.setCond(20);
            }
        } else if (n == 30665) {
            string = n2 == 20 || n2 == 21 ? "30665-01.htm" : "30665-03.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        Integer[] integerArray = aUW.get(npcInstance.getNpcId());
        if (questState.getCond() == integerArray[0].intValue() && (integerArray[2] == 0 || questState.getQuestItemsCount((int)integerArray[2]) == 0L)) {
            if (integerArray[2] != 0) {
                questState.giveItems(integerArray[2], 1L);
            }
            questState.setCond(integerArray[1]);
        }
        return null;
    }

    static {
        aUW.put(27134, new Integer[]{2, 3, 0});
        aUW.put(27123, new Integer[]{11, 12, 2816});
        aUW.put(27124, new Integer[]{14, 15, 2817});
        aUW.put(27125, new Integer[]{16, 17, 2818});
        aUW.put(27127, new Integer[]{18, 19, 2819});
    }
}

