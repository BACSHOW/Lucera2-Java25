/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _619_RelicsOfTheOldEmpire
extends Quest
implements ScriptFile {
    public static final int[] Recipes = new int[]{6881, 6883, 6885, 6887, 7580, 6891, 6893, 6895, 6897, 6899};
    private static final int bMs = 7075;
    private static final int bMt = 7254;
    private static final int bMu = 31538;
    private static final int bMv = 21426;
    private static final int bMw = 21427;
    private static final int bMx = 21430;
    private static final int bMy = 21431;
    private static final int bMz = 21424;
    private static final int bMA = 21425;
    private static final int bMB = 21420;
    private static final int bMC = 21421;
    private static final int bMD = 21396;
    private static final int bME = 21397;
    private static final int bMF = 21432;
    private static final int bMG = 21410;
    private static final int bMH = 21411;
    private static final int bMI = 21798;
    private static final int bMJ = 21799;
    private static final int bMK = 21800;
    private static final int bML = 21428;
    private static final int bMM = 21429;
    private static final int bMN = 21400;
    private static final int bMO = 21401;
    private static final int bMP = 21414;
    private static final int bMQ = 21415;
    private static final int bMR = 21422;
    private static final int bMS = 21423;
    private static final int bMT = 21408;
    private static final int bMU = 21409;
    private static final int bMV = 21418;
    private static final int bMW = 21419;
    private static final int bMX = 21406;
    private static final int bMY = 21407;
    private static final int bMZ = 21416;
    private static final int bNa = 21417;
    private static final int bNb = 21433;
    private static final int bNc = 21402;
    private static final int bNd = 21403;
    private static final int bNe = 21434;
    private static final int bNf = 18120;
    private static final int bNg = 18122;
    private static final int bNh = 18121;
    private static final int bNi = 18123;
    private static final int bNj = 18125;
    private static final int bNk = 18124;
    private static final int bNl = 18126;
    private static final int bNm = 18128;
    private static final int bNn = 18127;
    private static final int bNo = 18129;
    private static final int bNp = 18131;
    private static final int bNq = 18130;
    private static final int bNr = 18132;
    private static final int bNs = 18133;
    private static final int bNt = 18134;
    private static final int bNu = 18135;
    private static final int bNv = 18136;
    private static final int bNw = 18138;
    private static final int bNx = 18139;
    private static final int bNy = 18137;
    private static final int bNz = 18140;
    private static final int bNA = 18141;
    private static final int bNB = 18142;
    private static final int bNC = 18143;
    private static final int bND = 18144;
    private static final int bNE = 18149;
    private static final int bNF = 18148;
    private static final int bNG = 18146;
    private static final int bNH = 18147;
    private static final int bNI = 18145;
    private static final int bNJ = 18166;
    private static final int bNK = 18167;
    private static final int bNL = 18168;
    private static final int bNM = 18169;
    private static final int bNN = 18171;
    private static final int bNO = 18170;
    private static final int bNP = 18172;
    private static final int bNQ = 18173;
    private static final int bNR = 18175;
    private static final int bNS = 18174;
    private static final int bNT = 18176;
    private static final int bNU = 18178;
    private static final int bNV = 18177;
    private static final int bNW = 18179;
    private static final int bNX = 18181;
    private static final int bNY = 18180;
    private static final int bNZ = 18182;
    private static final int bOa = 18184;
    private static final int bOb = 18183;
    private static final int bOc = 18195;
    private static final int bOd = 18185;
    private static final int bOe = 18186;
    private static final int bOf = 18187;
    private static final int bOg = 18188;
    private static final int bOh = 18189;
    private static final int bOi = 18190;
    private static final int bOj = 18192;
    private static final int bOk = 18193;
    private static final int bOl = 18191;
    private static final int bOm = 18194;
    private static final int bOn = 18212;
    private static final int bOo = 18213;
    private static final int bOp = 18214;
    private static final int bOq = 18215;
    private static final int bOr = 18216;
    private static final int bOs = 18217;
    private static final int bOt = 18218;
    private static final int bOu = 18219;
    private static final int bOv = 18230;
    private static final int bOw = 18220;
    private static final int bOx = 18221;
    private static final int bOy = 18222;
    private static final int bOz = 18223;
    private static final int bOA = 18224;
    private static final int bOB = 18225;
    private static final int bOC = 18227;
    private static final int bOD = 18228;
    private static final int bOE = 18226;
    private static final int bOF = 18229;
    private static final int bOG = 21436;
    private static final int bOH = 21435;
    private static final int bOI = 21437;
    private static final int bOJ = 21398;
    private static final int bOK = 21399;
    private static final int bOL = 21404;
    private static final int bOM = 21405;
    private static final int bON = 21412;
    private static final int bOO = 21413;

    public _619_RelicsOfTheOldEmpire() {
        super(1);
        this.addStartNpc(31538);
        this.addKillId(21426, 21427, 21430, 21431, 21424, 21425, 21420, 21421, 21396, 21397, 21432, 21410, 21411, 21798, 21799, 21800, 21428, 21429, 21400, 21401, 21414, 21415, 21422, 21423, 21408, 21409, 21418, 21419, 21406, 21407, 21416, 21417, 21433, 21402, 21403, 21434, 18120, 18122, 18121, 18123, 18125, 18124, 18126, 18128, 18127, 18129, 18131, 18130, 18132, 18133, 18134, 18135, 18136, 18138, 18139, 18137, 18140, 18141, 18142, 18143, 18144, 18149, 18148, 18146, 18147, 18145, 18166, 18167, 18168, 18169, 18171, 18170, 18172, 18173, 18175, 18174, 18176, 18178, 18177, 18179, 18181, 18180, 18182, 18184, 18183, 18195, 18185, 18186, 18187, 18188, 18189, 18190, 18192, 18193, 18191, 18194, 18212, 18213, 18214, 18215, 18216, 18217, 18218, 18219, 18230, 18220, 18221, 18222, 18223, 18224, 18225, 18227, 18228, 18226, 18229, 21436, 21435, 21437, 21398, 21399, 21404, 21405, 21412, 21413);
        this.addQuestItem(7254);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        if (string.equals("explorer_ghost_a_q0619_03.htm")) {
            if (questState.getPlayer().getLevel() < 74) {
                questState.exitCurrentQuest(true);
                return "explorer_ghost_a_q0619_02.htm";
            }
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else {
            if (string.equals("explorer_ghost_a_q0619_09.htm")) {
                if (questState.getQuestItemsCount(7254) < 1000L) {
                    return questState.getQuestItemsCount(7075) > 0L ? "explorer_ghost_a_q0619_06.htm" : "explorer_ghost_a_q0619_07.htm";
                }
                questState.takeItems(7254, 1000L);
                questState.giveItems(Recipes[Rnd.get(Recipes.length)], 1L);
                return "explorer_ghost_a_q0619_09.htm";
            }
            if (string.equals("explorer_ghost_a_q0619_10.htm")) {
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            }
        }
        return string;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        if (questState.getState() == 1) {
            if (questState.getPlayer().getLevel() < 74) {
                questState.exitCurrentQuest(true);
                return "explorer_ghost_a_q0619_02.htm";
            }
            questState.setCond(0);
            return "explorer_ghost_a_q0619_01.htm";
        }
        if (questState.getQuestItemsCount(7254) >= 1000L) {
            return "explorer_ghost_a_q0619_04.htm";
        }
        return questState.getQuestItemsCount(7075) > 0L ? "explorer_ghost_a_q0619_06.htm" : "explorer_ghost_a_q0619_07.htm";
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (n == 21426) {
            if (Rnd.get(100) < 8) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21427) {
            if (Rnd.get(100) < 74) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21430) {
            if (Rnd.get(100) < 10) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21431 || n == 21398) {
            if (Rnd.get(100) < 94) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21424) {
            if (Rnd.get(100) < 19) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21425) {
            if (Rnd.get(100) < 21) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21420) {
            if (Rnd.get(100) < 82) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21421) {
            if (Rnd.get(100) < 77) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21396) {
            if (Rnd.get(100) < 51) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 60) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21397) {
            if (Rnd.get(100) < 50) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21432) {
            if (Rnd.get(100) < 34) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21410) {
            if (Rnd.get(100) < 81) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21411) {
            if (Rnd.get(100) < 66) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21798) {
            if (Rnd.get(100) < 33) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21799) {
            if (Rnd.get(100) < 61) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21800) {
            if (Rnd.get(100) < 31) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21428) {
            if (Rnd.get(100) < 76) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21429) {
            if (Rnd.get(100) < 80) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21400) {
            if (Rnd.get(100) < 76) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21401) {
            if (Rnd.get(100) < 67) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21414) {
            if (Rnd.get(100) < 79) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21415) {
            if (Rnd.get(100) < 79) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21422) {
            if (Rnd.get(100) < 88) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21423) {
            if (Rnd.get(100) < 94) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21408) {
            if (Rnd.get(100) < 82) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21409) {
            if (Rnd.get(100) < 92) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21418) {
            if (Rnd.get(100) < 66) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21419) {
            if (Rnd.get(100) < 67) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21406) {
            if (Rnd.get(100) < 87) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21407) {
            if (Rnd.get(100) < 56) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21416) {
            if (Rnd.get(100) < 82) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21417) {
            if (Rnd.get(100) < 27) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21433) {
            if (Rnd.get(100) < 34) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21402) {
            if (Rnd.get(100) < 69) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21403) {
            if (Rnd.get(100) < 80) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21434) {
            if (Rnd.get(100) < 90) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 18120) {
            if (Rnd.get(100) < 28) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18122) {
            if (Rnd.get(100) < 93) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18121) {
            if (Rnd.get(100) < 21) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18123) {
            if (Rnd.get(100) < 28) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18125) {
            if (Rnd.get(100) < 93) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18124) {
            if (Rnd.get(100) < 21) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18126) {
            if (Rnd.get(100) < 28) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18128) {
            if (Rnd.get(100) < 93) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18127) {
            if (Rnd.get(100) < 21) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18129) {
            if (Rnd.get(100) < 28) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18131) {
            if (Rnd.get(100) < 93) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18130) {
            if (Rnd.get(100) < 21) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18132) {
            if (Rnd.get(100) < 30) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18133) {
            if (Rnd.get(100) < 20) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18134) {
            if (Rnd.get(100) < 90) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18135 || n == 18136) {
            if (Rnd.get(100) < 20) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18138) {
            if (Rnd.get(100) < 19) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18139) {
            if (Rnd.get(100) < 17) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18137) {
            if (Rnd.get(100) < 89) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18140) {
            if (Rnd.get(100) < 19) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18141) {
            if (Rnd.get(100) < 76) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18142) {
            if (Rnd.get(100) < 76) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18143 || n == 18144) {
            if (Rnd.get(100) < 76) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18149) {
            if (Rnd.get(100) < 63) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18148) {
            if (Rnd.get(100) < 72) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18146) {
            if (Rnd.get(100) < 66) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18147) {
            if (Rnd.get(100) < 62) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18145) {
            if (Rnd.get(100) < 65) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18166) {
            if (Rnd.get(100) < 92) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18167) {
            if (Rnd.get(100) < 90) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18168) {
            if (Rnd.get(100) < 93) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18171) {
            if (Rnd.get(100) < 94) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18170) {
            if (Rnd.get(100) < 90) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18172) {
            if (Rnd.get(100) < 89) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18175) {
            if (Rnd.get(100) < 93) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18174) {
            if (Rnd.get(100) < 22) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18176) {
            if (Rnd.get(100) < 99) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18178) {
            if (Rnd.get(100) < 93) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18177) {
            if (Rnd.get(100) < 22) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18179) {
            if (Rnd.get(100) < 99) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18181) {
            if (Rnd.get(100) < 93) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18180) {
            if (Rnd.get(100) < 22) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18182) {
            if (Rnd.get(100) < 99) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18184) {
            if (Rnd.get(100) < 93) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18183) {
            if (Rnd.get(100) < 22) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18195) {
            if (Rnd.get(100) < 91) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18185) {
            if (Rnd.get(100) < 23) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18186) {
            if (Rnd.get(100) < 23) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18187) {
            if (Rnd.get(100) < 20) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18188) {
            if (Rnd.get(100) < 90) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18189) {
            if (Rnd.get(100) < 20) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18190) {
            if (Rnd.get(100) < 20) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18192) {
            if (Rnd.get(100) < 19) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18193) {
            if (Rnd.get(100) < 17) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18191) {
            if (Rnd.get(100) < 89) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18194) {
            if (Rnd.get(100) < 19) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18214 || n == 18216 || n == 18215 || n == 18217 || n == 18218 || n == 18219 || n == 18212 || n == 18213) {
            if (Rnd.get(100) < 79) {
                questState.rollAndGive(7254, 4, 100.0);
            } else {
                questState.rollAndGive(7254, 3, 100.0);
            }
        } else if (n == 18230) {
            if (Rnd.get(100) < 49) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18220) {
            if (Rnd.get(100) < 24) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18221) {
            if (Rnd.get(100) < 27) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18222) {
            if (Rnd.get(100) < 21) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18223 || n == 18169 || n == 18173) {
            if (Rnd.get(100) < 90) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18224) {
            if (Rnd.get(100) < 22) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18225) {
            if (Rnd.get(100) < 21) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18227) {
            if (Rnd.get(100) < 53) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18228) {
            if (Rnd.get(100) < 15) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18226) {
            if (Rnd.get(100) < 89) {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 18229) {
            if (Rnd.get(100) < 19) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
        } else if (n == 21436) {
            if (Rnd.get(100) < 66) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21435) {
            if (Rnd.get(100) < 60) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21437) {
            if (Rnd.get(100) < 69) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21399) {
            if (Rnd.get(100) < 84) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21404) {
            if (Rnd.get(100) < 90) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21405) {
            if (Rnd.get(100) < 64) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21412) {
            if (Rnd.get(100) < 6) {
                questState.rollAndGive(7254, 2, 100.0);
            } else {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
            }
        } else if (n == 21413) {
            if (Rnd.get(100) < 81) {
                questState.rollAndGive(7254, 1, 100.0);
            }
            if (Rnd.get(30) == 0) {
                questState.rollAndGive(7075, 1, 100.0);
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

