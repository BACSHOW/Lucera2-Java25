/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _038_DragonFangs
extends Quest
implements ScriptFile {
    private static final int aBO = 30344;
    private static final int aBP = 30386;
    private static final int aBQ = 30034;
    private static final int aBR = 20357;
    private static final int aBS = 21100;
    private static final int aBT = 20356;
    private static final int aBU = 21101;
    private static final int aBV = 7173;
    private static final int aBW = 7174;
    private static final int aBX = 7175;
    private static final int aBY = 7176;
    private static final int aBZ = 7177;
    private static final int aCa = 45;
    private static final int aCb = 627;
    private static final int aCc = 605;
    private static final int aCd = 1123;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _038_DragonFangs() {
        super(0);
        this.addStartNpc(30386);
        this.addTalkId(30034, 30344);
        this.addKillId(20357, 21100, 20356, 21101);
        this.addQuestItem(7173, 7174, 7175, 7176, 7177);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getInt("tooth_dragon_cookie");
        int n2 = npcInstance.getNpcId();
        if (n2 == 30386) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("tooth_of_dragon", String.valueOf(11), true);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "guard_luis_q0038_0104.htm";
            } else if (string.equalsIgnoreCase("reply_1") && n == 1) {
                if (questState.getQuestItemsCount(7173) >= 100L) {
                    questState.setCond(3);
                    questState.set("tooth_of_dragon", String.valueOf(21), true);
                    questState.takeItems(7173, 100L);
                    questState.giveItems(7174, 1L);
                    string2 = "guard_luis_q0038_0201.htm";
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    string2 = "guard_luis_q0038_0202.htm";
                }
            }
        } else if (n2 == 30034) {
            if (string.equalsIgnoreCase("reply_1") && n == 2) {
                if (questState.getQuestItemsCount(7174) >= 1L) {
                    questState.setCond(4);
                    questState.set("tooth_of_dragon", String.valueOf(31), true);
                    questState.takeItems(7174, 1L);
                    questState.giveItems(7176, 1L);
                    string2 = "iris_q0038_0301.htm";
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    string2 = "iris_q0038_0302.htm";
                }
            } else if (string.equalsIgnoreCase("reply_1") && n == 4) {
                if (questState.getQuestItemsCount(7177) >= 1L) {
                    questState.setCond(6);
                    questState.set("tooth_of_dragon", String.valueOf(51), true);
                    questState.takeItems(7177, 1L);
                    string2 = "iris_q0038_0501.htm";
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    string2 = "iris_q0038_0502.htm";
                }
            } else if (string.equalsIgnoreCase("reply_3") && n == 5) {
                if (questState.getQuestItemsCount(7175) >= 50L) {
                    int n3 = Rnd.get(1000);
                    questState.takeItems(7175, -1L);
                    questState.addExpAndSp(435117L, 23977L);
                    if (n3 < 250) {
                        questState.giveItems(45, 1L);
                        questState.giveItems(57, 5200L);
                    } else if (n3 < 500) {
                        questState.giveItems(627, 1L);
                        questState.giveItems(57, 1500L);
                    } else if (n3 < 750) {
                        questState.giveItems(1123, 1L);
                        questState.giveItems(57, 3200L);
                    } else if (n3 < 1000) {
                        questState.giveItems(605, 1L);
                        questState.giveItems(57, 3200L);
                    }
                    questState.unset("tooth_of_dragon");
                    questState.unset("tooth_dragon_cookie");
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(false);
                    string2 = "iris_q0038_0601.htm";
                } else {
                    string2 = "iris_q0038_0602.htm";
                }
            }
        } else if (n2 == 30344 && string.equalsIgnoreCase("reply_1") && n == 3) {
            if (questState.getQuestItemsCount(7176) >= 1L) {
                questState.setCond(5);
                questState.set("tooth_of_dragon", String.valueOf(41), true);
                questState.takeItems(7176, 1L);
                questState.giveItems(7177, 1L);
                string2 = "magister_rohmer_q0038_0401.htm";
                questState.playSound("ItemSound.quest_middle");
            } else {
                string2 = "magister_rohmer_q0038_0402.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("tooth_of_dragon");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 30386) break;
                if (questState.getPlayer().getLevel() >= 19) {
                    string = "guard_luis_q0038_0101.htm";
                    break;
                }
                string = "guard_luis_q0038_0103.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n2 == 30386) {
                    if (n >= 11 && n <= 12) {
                        if (n == 12 && questState.getQuestItemsCount(7173) >= 100L) {
                            questState.set("tooth_dragon_cookie", String.valueOf(1), true);
                            string = "guard_luis_q0038_0105.htm";
                            break;
                        }
                        string = "guard_luis_q0038_0106.htm";
                        break;
                    }
                    if (n != 21) break;
                    string = "guard_luis_q0038_0203.htm";
                    break;
                }
                if (n2 == 30034) {
                    if (questState.getQuestItemsCount(7174) >= 1L && n == 21) {
                        questState.set("tooth_dragon_cookie", String.valueOf(2), true);
                        string = "iris_q0038_0201.htm";
                        break;
                    }
                    if (n == 31) {
                        string = "iris_q0038_0303.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(7177) >= 1L && n == 41) {
                        questState.set("tooth_dragon_cookie", String.valueOf(4), true);
                        string = "iris_q0038_0401.htm";
                        break;
                    }
                    if (n > 52 || n < 51) break;
                    if (n == 52 && questState.getQuestItemsCount(7175) >= 50L) {
                        questState.set("tooth_dragon_cookie", String.valueOf(5), true);
                        string = "iris_q0038_0503.htm";
                        break;
                    }
                    string = "iris_q0038_0504.htm";
                    break;
                }
                if (n2 != 30344) break;
                if (questState.getQuestItemsCount(7176) >= 1L && n == 31) {
                    questState.set("tooth_dragon_cookie", String.valueOf(3), true);
                    string = "magister_rohmer_q0038_0301.htm";
                    break;
                }
                if (n != 41) break;
                string = "magister_rohmer_q0038_0403.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n;
        int n2 = questState.getInt("tooth_of_dragon");
        int n3 = npcInstance.getNpcId();
        if (n2 == 11) {
            if (n3 == 20357 || n3 == 21100) {
                if (questState.getQuestItemsCount(7173) + 1L >= 100L) {
                    if (questState.getQuestItemsCount(7173) < 100L) {
                        questState.giveItems(7173, 100L - questState.getQuestItemsCount(7173));
                        questState.playSound("ItemSound.quest_middle");
                    }
                    questState.setCond(2);
                    questState.set("tooth_of_dragon", String.valueOf(12), true);
                } else {
                    questState.giveItems(7173, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 51 && (n3 == 20356 || n3 == 21101) && (n = Rnd.get(1000)) < 500) {
            if (questState.getQuestItemsCount(7175) + 1L >= 50L) {
                if (questState.getQuestItemsCount(7175) < 50L) {
                    questState.giveItems(7175, 50L - questState.getQuestItemsCount(7175));
                    questState.playSound("ItemSound.quest_middle");
                }
                questState.setCond(7);
                questState.set("tooth_of_dragon", String.valueOf(52), true);
            } else {
                questState.giveItems(7175, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

