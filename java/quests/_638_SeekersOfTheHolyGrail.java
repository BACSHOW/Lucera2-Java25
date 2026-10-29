/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _638_SeekersOfTheHolyGrail
extends Quest
implements ScriptFile {
    private static final int aAb = 31328;
    private static final int bQI = 8068;
    private static final int bQJ = 960;
    private static final int bQK = 959;
    private static final int bQL = 22176;
    private static final int bQM = 22146;
    private static final int bQN = 22151;
    private static final int bQO = 22138;
    private static final int bQP = 22141;
    private static final int bQQ = 22175;
    private static final int bQR = 22155;
    private static final int bQS = 22159;
    private static final int bQT = 22163;
    private static final int bQU = 22167;
    private static final int bQV = 22171;
    private static final int bQW = 22143;
    private static final int bQX = 22137;
    private static final int bQY = 22194;
    private static final int bQZ = 22164;
    private static final int bRa = 22156;
    private static final int bRb = 22166;
    private static final int bRc = 22173;
    private static final int bRd = 22170;
    private static final int bRe = 22157;
    private static final int bRf = 22160;
    private static final int bRg = 22165;
    private static final int bRh = 22168;
    private static final int bRi = 22174;
    private static final int bRj = 22158;
    private static final int bRk = 22162;
    private static final int bRl = 22149;
    private static final int bRm = 22147;
    private static final int bRn = 22154;
    private static final int bRo = 22161;
    private static final int bRp = 22169;
    private static final int bRq = 22172;
    private static final int bRr = 22145;
    private static final int bRs = 22152;
    private static final int bRt = 22153;
    private static final int bRu = 22136;
    private static final int bRv = 22150;
    private static final int bRw = 22148;
    private static final int bRx = 22142;
    private static final int bRy = 22144;
    private static final int bRz = 22139;
    private static final int bRA = 22140;

    public _638_SeekersOfTheHolyGrail() {
        super(1);
        this.addStartNpc(31328);
        this.addQuestItem(8068);
        this.addKillId(22176, 22146, 22151, 22138, 22141, 22175, 22155, 22159, 22163, 22167, 22171, 22143, 22137, 22194, 22164, 22156, 22166, 22173, 22170, 22157, 22160, 22165, 22168, 22174, 22158, 22162, 22149, 22147, 22154, 22161, 22169, 22172, 22145, 22152, 22153, 22136, 22150, 22148, 22142, 22144, 22139, 22140);
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

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (npcInstance.getNpcId() == 31328) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "highpriest_innocentin_q0638_03.htm";
            } else if (string.equalsIgnoreCase("reply_1")) {
                string2 = "highpriest_innocentin_q0638_06.htm";
            } else if (string.equalsIgnoreCase("reply_2")) {
                if (questState.getCond() == 1 && questState.getQuestItemsCount(8068) >= 2000L) {
                    if (Rnd.get(100) < 80) {
                        if (Rnd.get(2) == 0) {
                            questState.giveItems(960, 1L, true);
                        } else {
                            questState.giveItems(959, 1L, true);
                        }
                        questState.takeItems(8068, 2000L);
                        string2 = "highpriest_innocentin_q0638_07.htm";
                    } else {
                        questState.giveItems(57, 3576000L);
                        questState.takeItems(8068, 2000L);
                        string2 = "highpriest_innocentin_q0638_08.htm";
                    }
                }
            } else if (string.equalsIgnoreCase("reply_3")) {
                questState.giveItems(57, 1700L * questState.getQuestItemsCount(8068));
                questState.takeItems(8068, -1L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                string2 = "highpriest_innocentin_q0638_09.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getState();
        if (npcInstance.getNpcId() == 31328) {
            if (n == 1) {
                string = questState.getPlayer().getLevel() >= 73 ? "highpriest_innocentin_q0638_01.htm" : "highpriest_innocentin_q0638_02.htm";
            } else if (questState.getQuestItemsCount(8068) >= 2000L && questState.getCond() == 1) {
                string = "highpriest_innocentin_q0638_04.htm";
            } else if (questState.getQuestItemsCount(8068) < 2000L && questState.getCond() == 1) {
                string = "highpriest_innocentin_q0638_05.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (n == 22176 && Rnd.get(100) < 6) {
            questState.rollAndGive(8068, 1, 100.0);
        } else if (n == 22146) {
            if (Rnd.get(100) < 54) {
                questState.rollAndGive(8068, 1, 100.0);
            }
            if (Rnd.get(10) < 1) {
                npcInstance.dropItem(questState.getPlayer(), 8275, 1L);
            }
        } else if (n == 22151) {
            if (Rnd.get(100) < 62) {
                questState.rollAndGive(8068, 1, 100.0);
            }
            if (Rnd.get(10) < 1) {
                npcInstance.dropItem(questState.getPlayer(), 8275, 1L);
            }
        } else if (n == 22149) {
            if (Rnd.get(100) < 54) {
                questState.rollAndGive(8068, 1, 100.0);
            }
            npcInstance.dropItem(questState.getPlayer(), 8273, 6L);
        } else if (n == 22143) {
            if (Rnd.get(100) < 62) {
                questState.rollAndGive(8068, 1, 100.0);
            }
            npcInstance.dropItem(questState.getPlayer(), 8274, 1L);
        } else if (n == 22142) {
            if (Rnd.get(100) < 54) {
                questState.rollAndGive(8068, 1, 100.0);
            }
            npcInstance.dropItem(questState.getPlayer(), 8274, 1L);
        } else if (n == 22140) {
            if (Rnd.get(100) < 54) {
                questState.rollAndGive(8068, 1, 100.0);
            }
            npcInstance.dropItem(questState.getPlayer(), 8273, 1L);
        } else if (n == 22141 || n == 22147 || n == 22152 || n == 22136) {
            if (Rnd.get(100) < 55) {
                questState.rollAndGive(8068, 1, 100.0);
            }
        } else if (n == 22175) {
            if (Rnd.get(100) < 3) {
                questState.rollAndGive(8068, 1, 100.0);
            }
        } else if (n == 22155 || n == 22159 || n == 22163 || n == 22167) {
            if (Rnd.get(100) < 75) {
                questState.rollAndGive(8068, 1, 100.0);
            }
        } else if (n == 22171 && Rnd.get(100) < 87) {
            questState.rollAndGive(8068, 1, 100.0);
        } else if (n == 22137 || n == 22194 || n == 22138) {
            if (Rnd.get(100) < 6) {
                questState.rollAndGive(8068, 1, 100.0);
            }
        } else if (n == 22156 || n == 22164 || n == 22170 || n == 22160 || n == 22174 || n == 22158 || n == 22162) {
            if (Rnd.get(100) < 67) {
                questState.rollAndGive(8068, 1, 100.0);
            }
        } else if (n == 22166 || n == 22173 || n == 22157 || n == 22165 || n == 22168) {
            if (Rnd.get(100) < 66) {
                questState.rollAndGive(8068, 1, 100.0);
            }
        } else if (n == 22154 || n == 22145 || n == 22153) {
            if (Rnd.get(100) < 53) {
                questState.rollAndGive(8068, 1, 100.0);
            }
        } else if (n == 22161 || n == 22169 || n == 22172) {
            if (Rnd.get(100) < 78) {
                questState.rollAndGive(8068, 1, 100.0);
            }
        } else if (n == 22150 || n == 22148) {
            if (Rnd.get(100) < 46) {
                questState.rollAndGive(8068, 1, 100.0);
            }
        } else if ((n == 22144 || n == 22139) && Rnd.get(100) < 54) {
            questState.rollAndGive(8068, 1, 100.0);
        }
        return null;
    }
}

