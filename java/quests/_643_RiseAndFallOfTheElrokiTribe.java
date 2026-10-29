/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _643_RiseAndFallOfTheElrokiTribe
extends Quest
implements ScriptFile {
    private static final int bST = 32106;
    private static final int aGl = 32117;
    private static final int bSU = 22209;
    private static final int bSV = 22208;
    private static final int bSW = 22226;
    private static final int bSX = 22221;
    private static final int bSY = 22210;
    private static final int bSZ = 22212;
    private static final int bTa = 22211;
    private static final int bTb = 22227;
    private static final int bTc = 22222;
    private static final int bTd = 22213;
    private static final int bTe = 8776;
    private static final int bTf = 8712;
    private static final int bTg = 8713;
    private static final int bTh = 8714;
    private static final int bTi = 8715;
    private static final int bTj = 8716;
    private static final int bTk = 8717;
    private static final int bTl = 8718;
    private static final int bTm = 8719;
    private static final int bTn = 8720;
    private static final int bTo = 8721;
    private static final int bTp = 8722;

    public _643_RiseAndFallOfTheElrokiTribe() {
        super(1);
        this.addStartNpc(32106);
        this.addTalkId(32117);
        this.addKillId(22209, 22208, 22226, 22221, 22210, 22212, 22211, 22227, 22222, 22213);
        this.addQuestItem(8776);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = npcInstance.getNpcId();
        if (n == 32106) {
            if (string.equalsIgnoreCase("quest_accept")) {
                if (questState.getPlayer().getLevel() >= 75) {
                    questState.setCond(1);
                    questState.setState(2);
                    questState.playSound("ItemSound.quest_accept");
                    string2 = "singsing_q0643_05.htm";
                } else {
                    string2 = "singsing_q0643_05a.htm";
                }
            } else if (string.equalsIgnoreCase("reply_1")) {
                string2 = "singsing_q0643_02.htm";
            } else if (string.equalsIgnoreCase("reply_2")) {
                questState.giveItems(57, 1374L * questState.getQuestItemsCount(8776));
                questState.takeItems(8776, -1L);
                string2 = "singsing_q0643_08.htm";
            } else if (string.equalsIgnoreCase("reply_3")) {
                string2 = "singsing_q0643_09.htm";
            } else if (string.equalsIgnoreCase("reply_4")) {
                if (questState.getQuestItemsCount(8776) <= 0L) {
                    questState.exitCurrentQuest(true);
                    questState.playSound("ItemSound.quest_finish");
                    string2 = "singsing_q0643_10.htm";
                } else {
                    questState.giveItems(57, 1374L * questState.getQuestItemsCount(8776));
                    questState.takeItems(8776, -1L);
                    questState.exitCurrentQuest(true);
                    questState.playSound("ItemSound.quest_finish");
                    string2 = "singsing_q0643_11.htm";
                }
            } else if (string.equalsIgnoreCase("reply_5")) {
                string2 = "singsing_q0643_12.htm";
            }
        } else if (n == 32117) {
            if (string.equalsIgnoreCase("reply_6")) {
                string2 = "shaman_caracawe_q0643_04.htm";
            } else if (string.equalsIgnoreCase("reply_7")) {
                if (questState.getQuestItemsCount(8776) < 300L) {
                    string2 = "shaman_caracawe_q0643_05.htm";
                } else {
                    int n2 = Rnd.get(11);
                    if (n2 == 0) {
                        questState.giveItems(8712, 5L);
                    }
                    if (n2 == 1) {
                        questState.giveItems(8713, 5L);
                    }
                    if (n2 == 2) {
                        questState.giveItems(8714, 5L);
                    }
                    if (n2 == 3) {
                        questState.giveItems(8715, 5L);
                    }
                    if (n2 == 4) {
                        questState.giveItems(8716, 5L);
                    }
                    if (n2 == 5) {
                        questState.giveItems(8717, 5L);
                    }
                    if (n2 == 6) {
                        questState.giveItems(8718, 5L);
                    }
                    if (n2 == 7) {
                        questState.giveItems(8719, 5L);
                    }
                    if (n2 == 8) {
                        questState.giveItems(8720, 5L);
                    }
                    if (n2 == 9) {
                        questState.giveItems(8721, 5L);
                    }
                    if (n2 == 10) {
                        questState.giveItems(8722, 5L);
                    }
                    questState.takeItems(8776, 300L);
                    string2 = "shaman_caracawe_q0643_06.htm";
                    questState.playSound("ItemSound.quest_middle");
                }
            } else if (string.equalsIgnoreCase("reply_8")) {
                string2 = "shaman_caracawe_q0643_08.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getState();
        switch (n2) {
            case 1: {
                if (questState.getPlayer().getLevel() >= 75) {
                    string = "singsing_q0643_01.htm";
                    break;
                }
                string = "singsing_q0643_04.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n == 32106) {
                    if (questState.getQuestItemsCount(8776) > 0L) {
                        string = "singsing_q0643_06.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(8776) > 0L) break;
                    string = "singsing_q0643_07.htm";
                    break;
                }
                if (n != 32117) break;
                string = "shaman_caracawe_q0643_03.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = Rnd.get(1000);
        if (n == 22209) {
            if (n2 < 7) {
                questState.rollAndGive(8776, 2, 100.0);
            } else {
                questState.rollAndGive(8776, 1, 100.0);
            }
        } else if (n == 22208) {
            if (n2 < 95) {
                questState.rollAndGive(8776, 2, 100.0);
            } else {
                questState.rollAndGive(8776, 1, 100.0);
            }
        } else if (n == 22226) {
            if (n2 < 980) {
                questState.rollAndGive(8776, 1, 100.0);
            }
        } else if (n == 22221) {
            if (n2 < 885) {
                questState.rollAndGive(8776, 1, 100.0);
            }
        } else if (n == 22210) {
            if (n2 < 95) {
                questState.rollAndGive(8776, 2, 100.0);
            } else {
                questState.rollAndGive(8776, 1, 100.0);
            }
        } else if (n == 22212) {
            if (n2 < 989) {
                questState.rollAndGive(8776, 1, 100.0);
            }
        } else if (n == 22211) {
            if (n2 < 31) {
                questState.rollAndGive(8776, 2, 100.0);
            } else {
                questState.rollAndGive(8776, 1, 100.0);
            }
        } else if (n == 22227) {
            if (n2 < 966) {
                questState.rollAndGive(8776, 1, 100.0);
            }
        } else if (n == 22222) {
            if (n2 < 858) {
                questState.rollAndGive(8776, 1, 100.0);
            }
        } else if (n == 22213) {
            if (n2 < 31) {
                questState.rollAndGive(8776, 2, 100.0);
            } else {
                questState.rollAndGive(8776, 1, 100.0);
            }
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

