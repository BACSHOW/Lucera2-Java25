/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _642_APowerfulPrimevalCreature
extends Quest
implements ScriptFile {
    private static final int bSx = 32105;
    private static final int bSy = 18344;
    private static final int aGv = 22204;
    private static final int aGu = 22203;
    private static final int aGA = 22225;
    private static final int aGy = 22220;
    private static final int aGw = 22205;
    private static final int aGs = 22201;
    private static final int aGr = 22200;
    private static final int bSz = 22224;
    private static final int aGx = 22219;
    private static final int aGt = 22202;
    private static final int bSA = 22199;
    private static final int bSB = 22197;
    private static final int bSC = 22196;
    private static final int bSD = 22223;
    private static final int bSE = 22218;
    private static final int bSF = 22198;
    private static final int bSG = 8774;
    private static final int bSH = 8775;
    private static final int bSI = 8690;
    private static final int bSJ = 8692;
    private static final int bSK = 8694;
    private static final int bSL = 8696;
    private static final int bSM = 8698;
    private static final int bSN = 8700;
    private static final int bSO = 8702;
    private static final int bSP = 8704;
    private static final int bSQ = 8706;
    private static final int bSR = 8708;
    private static final int bSS = 8710;

    public _642_APowerfulPrimevalCreature() {
        super(1);
        this.addStartNpc(32105);
        this.addKillId(18344, 22204, 22203, 22225, 22220, 22205, 22201, 22200, 22224, 22219, 22202, 22199, 22197, 22196, 22223, 22218, 22198);
        this.addQuestItem(8774, 8775);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            if (questState.getPlayer().getLevel() >= 75) {
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "dindin_q0642_04.htm";
            } else {
                string2 = "dindin_q0642_01a.htm";
                questState.exitCurrentQuest(true);
            }
        } else if (string.equalsIgnoreCase("reply_1")) {
            string2 = "dindin_q0642_02.htm";
        } else if (string.equalsIgnoreCase("reply_2")) {
            string2 = "dindin_q0642_03.htm";
        } else if (string.equalsIgnoreCase("reply_3")) {
            if (questState.getQuestItemsCount(8774) >= 1L) {
                questState.giveItems(57, 5000L * questState.getQuestItemsCount(8774));
                questState.takeItems(8774, -1L);
                string2 = "dindin_q0642_08.htm";
            } else {
                string2 = "dindin_q0642_08a.htm";
            }
        } else if (string.equalsIgnoreCase("reply_5")) {
            string2 = questState.getQuestItemsCount(8774) >= 150L && questState.getQuestItemsCount(8775) >= 1L ? "dindin_q0642_10.htm" : "dindin_q0642_11a.htm";
        } else if (string.equalsIgnoreCase("reply_100") || string.equalsIgnoreCase("reply_101") || string.equalsIgnoreCase("reply_102") || string.equalsIgnoreCase("reply_103") || string.equalsIgnoreCase("reply_104") || string.equalsIgnoreCase("reply_105") || string.equalsIgnoreCase("reply_106") || string.equalsIgnoreCase("reply_107") || string.equalsIgnoreCase("reply_108") || string.equalsIgnoreCase("reply_109") || string.equalsIgnoreCase("reply_110")) {
            if (string.equalsIgnoreCase("reply_100")) {
                questState.giveItems(8690, 1L);
            } else if (string.equalsIgnoreCase("reply_101")) {
                questState.giveItems(8692, 1L);
            } else if (string.equalsIgnoreCase("reply_102")) {
                questState.giveItems(8694, 1L);
            } else if (string.equalsIgnoreCase("reply_103")) {
                questState.giveItems(8696, 1L);
            } else if (string.equalsIgnoreCase("reply_104")) {
                questState.giveItems(8698, 1L);
            } else if (string.equalsIgnoreCase("reply_105")) {
                questState.giveItems(8700, 1L);
            } else if (string.equalsIgnoreCase("reply_106")) {
                questState.giveItems(8702, 1L);
            } else if (string.equalsIgnoreCase("reply_107")) {
                questState.giveItems(8704, 1L);
            } else if (string.equalsIgnoreCase("reply_108")) {
                questState.giveItems(8706, 1L);
            } else if (string.equalsIgnoreCase("reply_109")) {
                questState.giveItems(8708, 1L);
            } else if (string.equalsIgnoreCase("reply_110")) {
                questState.giveItems(8710, 1L);
            }
            questState.giveItems(57, 44000L);
            questState.takeItems(8775, 1L);
            questState.takeItems(8774, 150L);
            string2 = "dindin_q0642_10a.htm";
        } else if (string.equalsIgnoreCase("reply_200")) {
            string2 = "dindin_q0642_11.htm";
        } else if (string.equalsIgnoreCase("reply_6")) {
            string2 = "dindin_q0642_12.htm";
        } else if (string.equalsIgnoreCase("reply_7")) {
            if (questState.getQuestItemsCount(8775) > 0L || questState.getQuestItemsCount(8774) > 0L) {
                if (questState.getQuestItemsCount(8775) > 0L) {
                    questState.giveItems(57, questState.getQuestItemsCount(8775));
                } else if (questState.getQuestItemsCount(8774) > 0L) {
                    questState.giveItems(57, questState.getQuestItemsCount(8774) * 5000L);
                }
                questState.takeItems(8775, -1L);
                questState.takeItems(8774, -1L);
                questState.exitCurrentQuest(true);
                questState.playSound("ItemSound.quest_finish");
                string2 = "dindin_q0642_13.htm";
            } else {
                questState.exitCurrentQuest(true);
                questState.playSound("ItemSound.quest_finish");
                string2 = "dindin_q0642_14.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getState();
        switch (n) {
            case 1: {
                if (questState.getPlayer().getLevel() >= 75) {
                    string = "dindin_q0642_01.htm";
                    break;
                }
                string = "dindin_q0642_01a.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (questState.getQuestItemsCount(8774) <= 0L && questState.getQuestItemsCount(8775) <= 0L) {
                    string = "dindin_q0642_06.htm";
                    break;
                }
                if (questState.getQuestItemsCount(8774) <= 0L && questState.getQuestItemsCount(8775) <= 0L) break;
                string = "dindin_q0642_07.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = Rnd.get(1000);
        if (n == 18344) {
            questState.rollAndGive(8775, 1, 100.0);
        } else if (n == 22204 && n2 < 313) {
            questState.rollAndGive(8774, 1, 100.0);
        } else if (n == 22203) {
            if (n2 < 321) {
                questState.rollAndGive(8774, 1, 100.0);
            }
        } else if (n == 22225) {
            if (n2 < 289) {
                questState.rollAndGive(8774, 1, 100.0);
            }
        } else if (n == 22220) {
            if (n2 < 264) {
                questState.rollAndGive(8774, 1, 100.0);
            }
        } else if (n == 22205) {
            if (n2 < 321) {
                questState.rollAndGive(8774, 1, 100.0);
            }
        } else if (n == 22201) {
            if (n2 < 326) {
                questState.rollAndGive(8774, 1, 100.0);
            }
        } else if (n == 22200) {
            if (n2 < 326) {
                questState.rollAndGive(8774, 1, 100.0);
            }
        } else if (n == 22224) {
            if (n2 < 305) {
                questState.rollAndGive(8774, 1, 100.0);
            }
        } else if (n == 22219) {
            if (n2 < 297) {
                questState.rollAndGive(8774, 1, 100.0);
            }
        } else if (n == 22202) {
            if (n2 < 326) {
                questState.rollAndGive(8774, 1, 100.0);
            }
        } else if (n == 22199) {
            if (n2 < 230) {
                questState.rollAndGive(8774, 2, 100.0);
            } else {
                questState.rollAndGive(8774, 1, 100.0);
            }
        } else if (n == 22197) {
            if (n2 < 314) {
                questState.rollAndGive(8774, 1, 100.0);
            }
        } else if (n == 22196) {
            if (n2 < 720) {
                questState.rollAndGive(8774, 1, 100.0);
            }
        } else if (n == 22223) {
            if (n2 < 308) {
                questState.rollAndGive(8774, 1, 100.0);
            }
        } else if (n == 22218) {
            if (n2 < 296) {
                questState.rollAndGive(8774, 1, 100.0);
            }
        } else if (n == 22198 && n2 < 720) {
            questState.rollAndGive(8774, 1, 100.0);
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

