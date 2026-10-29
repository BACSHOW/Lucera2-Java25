/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _426_QuestforFishingShot
extends Quest
implements ScriptFile {
    private static final int bCY = 31989;
    private static final int bCZ = 31576;
    private static final int bDa = 31567;
    private static final int bDb = 31569;
    private static final int bDc = 31697;
    private static final int bDd = 32007;
    private static final int bDe = 31578;
    private static final int bDf = 31571;
    private static final int bDg = 31579;
    private static final int bDh = 31562;
    private static final int bDi = 31570;
    private static final int bDj = 31577;
    private static final int bDk = 31575;
    private static final int bDl = 31564;
    private static final int bDm = 31573;
    private static final int bDn = 31572;
    private static final int bDo = 31565;
    private static final int bDp = 31568;
    private static final int bDq = 31563;
    private static final int bDr = 31696;
    private static final int bDs = 31566;
    private static final int bDt = 31574;
    private static final int bDu = 20074;
    private static final int bDv = 20077;
    private static final int bDw = 20079;
    private static final int bDx = 20080;
    private static final int bDy = 20081;
    private static final int bDz = 20084;
    private static final int bjn = 20087;
    private static final int bjo = 20088;
    private static final int bDA = 20819;
    private static final int bDB = 20812;
    private static final int aHY = 20456;
    private static final int bDC = 21024;
    private static final int bDD = 20386;
    private static final int bDE = 22046;
    private static final int bDF = 20772;
    private static final int bDG = 21058;
    private static final int bDH = 21060;
    private static final int bDI = 20983;
    private static final int bDJ = 20985;
    private static final int bDK = 20043;
    private static final int bDL = 20317;
    private static final int bDM = 20814;
    private static final int bsP = 20794;
    private static final int bsR = 20796;
    private static final int bDN = 20389;
    private static final int bDO = 20267;
    private static final int bDP = 20268;
    private static final int bDQ = 20270;
    private static final int bDR = 20269;
    private static final int bDS = 20271;
    private static final int bDT = 22022;
    private static final int bDU = 21539;
    private static final int bDV = 21540;
    private static final int bDW = 21536;
    private static final int bDX = 21520;
    private static final int bcm = 21544;
    private static final int bDY = 21530;
    private static final int bDZ = 21523;
    private static final int bEa = 21533;
    private static final int bEb = 21534;
    private static final int bEc = 21526;
    private static final int bEd = 21527;
    private static final int bEe = 21528;
    private static final int bEf = 21269;
    private static final int bEg = 21271;
    private static final int bEh = 20349;
    private static final int bEi = 21273;
    private static final int bEj = 20350;
    private static final int bEk = 21270;
    private static final int bEl = 21272;
    private static final int bEm = 20552;
    private static final int aBt = 20135;
    private static final int bqJ = 20804;
    private static final int bqL = 20806;
    private static final int bEn = 21106;
    private static final int bEo = 20790;
    private static final int bEp = 22048;
    private static final int bEq = 20346;
    private static final int aIC = 20105;
    private static final int aHF = 20345;
    private static final int bEr = 21105;
    private static final int bEs = 21107;
    private static final int bEt = 21104;
    private static final int bEu = 20781;
    private static final int bEv = 21387;
    private static final int bEw = 21655;
    private static final int bEx = 21390;
    private static final int bEy = 21656;
    private static final int bEz = 21382;
    private static final int bEA = 21384;
    private static final int bEB = 21654;
    private static final int bEC = 20557;
    private static final int bED = 20783;
    private static final int bEE = 21638;
    private static final int bEF = 20013;
    private static final int aHH = 20463;
    private static final int aIb = 20536;
    private static final int bEG = 20538;
    private static final int aHl = 20544;
    private static final int bEH = 21395;
    private static final int bEI = 20539;
    private static final int bEJ = 20537;
    private static final int bEK = 20978;
    private static final int bEL = 20979;
    private static final int bEM = 20800;
    private static final int bEN = 20667;
    private static final int bEO = 20792;
    private static final int bEP = 20433;
    private static final int bEQ = 20841;
    private static final int bER = 21617;
    private static final int bES = 21618;
    private static final int bET = 21518;
    private static final int bEU = 20651;
    private static final int bEV = 20811;
    private static final int bEW = 20809;
    private static final int bEX = 20555;
    private static final int bEY = 21025;
    private static final int bEZ = 20324;
    private static final int bFa = 20528;
    private static final int bFb = 20554;
    private static final int bgS = 20083;
    private static final int bFc = 22002;
    private static final int bFd = 20368;
    private static final int bFe = 20659;
    private static final int bFf = 20525;
    private static final int bFg = 20333;
    private static final int bFh = 20859;
    private static final int bFi = 21068;
    private static final int bFj = 20550;
    private static final int bFk = 20994;
    private static final int bFl = 20646;
    private static final int btk = 20824;
    private static final int buI = 21061;
    private static final int btw = 20825;
    private static final int buH = 20822;
    private static final int bFm = 20816;
    private static final int bFn = 20641;
    private static final int bFo = 20644;
    private static final int bFp = 20643;
    private static final int bFq = 20663;
    private static final int bFr = 20661;
    private static final int bFs = 20308;
    private static final int bFt = 20815;
    private static final int bFu = 20241;
    private static final int bFv = 20286;
    private static final int beT = 20403;
    private static final int bFw = 20005;
    private static final int bFx = 20471;
    private static final int bFy = 20470;
    private static final int bFz = 21117;
    private static final int bFA = 20476;
    private static final int bFB = 20475;
    private static final int bwo = 20650;
    private static final int bFC = 20487;
    private static final int bFD = 20030;
    private static final int aBT = 20356;
    private static final int aBS = 21100;
    private static final int aBU = 21101;
    private static final int aBR = 20357;
    private static final int bFE = 20652;
    private static final int bFF = 20070;
    private static final int bnU = 20658;
    private static final int bnT = 20657;
    private static final int bwV = 20656;
    private static final int bFG = 20655;
    private static final int bqw = 20786;
    private static final int bFH = 21644;
    private static final int bFI = 20849;
    private static final int bfF = 20044;
    private static final int bFJ = 21393;
    private static final int bFK = 21657;
    private static final int bFL = 21078;
    private static final int bFM = 20363;
    private static final int bFN = 20834;
    private static final int bhZ = 20157;
    private static final int bin = 20234;
    private static final int bim = 20232;
    private static final int bil = 20230;
    private static final int bFO = 22093;
    private static final int bFP = 22081;
    private static final int bFQ = 22097;
    private static final int bFR = 22057;
    private static final int bFS = 22051;
    private static final int bFT = 20639;
    private static final int bFU = 20636;
    private static final int bFV = 20638;
    private static final int bFW = 20225;
    private static final int bjt = 20227;
    private static final int bFX = 21516;
    private static final int bFY = 21517;
    private static final int bFZ = 21514;
    private static final int bGa = 20943;
    private static final int bGb = 20944;
    private static final int bGc = 20089;
    private static final int bGd = 21125;
    private static final int bqN = 20808;
    private static final int bGe = 21103;
    private static final int bGf = 20575;
    private static final int bGg = 20576;
    private static final int bGh = 20162;
    private static final int bGi = 21034;
    private static final int bGj = 21641;
    private static final int aJe = 20066;
    private static final int bgo = 20058;
    private static final int bgq = 20063;
    private static final int bGk = 20547;
    private static final int bGl = 21261;
    private static final int bGm = 21262;
    private static final int bGn = 21263;
    private static final int bGo = 21264;
    private static final int bGp = 20131;
    private static final int bwm = 20648;
    private static final int bGq = 20836;
    private static final int bGr = 20845;
    private static final int bGs = 21629;
    private static final int bGt = 21630;
    private static final int bGu = 20511;
    private static final int bGv = 21102;
    private static final int bnS = 20829;
    private static final int bnR = 20828;
    private static final int bGw = 21072;
    private static final int buJ = 21066;
    private static final int bGx = 20050;
    private static final int bdw = 20106;
    private static final int bGy = 21081;
    private static final int bGz = 21542;
    private static final int bGA = 21543;
    private static final int bgT = 20085;
    private static final int bGB = 20312;
    private static final int bGC = 20788;
    private static final int bGD = 20551;
    private static final int bGE = 20559;
    private static final int bGF = 21378;
    private static final int bGG = 21652;
    private static final int bGH = 21376;
    private static final int bGI = 21071;
    private static final int bGJ = 20810;
    private static final int bGK = 20404;
    private static final int bGL = 21026;
    private static final int bGM = 22020;
    private static final int bGN = 20100;
    private static final int bGO = 21075;
    private static final int bGP = 21023;
    private static final int bbK = 21508;
    private static final int bbN = 21511;
    private static final int bGQ = 21512;
    private static final int bbM = 21510;
    private static final int brf = 20562;
    private static final int azB = 20046;
    private static final int aGX = 20016;
    private static final int bfz = 20047;
    private static final int bGR = 20630;
    private static final int bGS = 20634;
    private static final int bGT = 20665;
    private static final int bGU = 20632;
    private static final int bGV = 20837;
    private static final int bGW = 20936;
    private static final int bGX = 20941;
    private static final int bGY = 20937;
    private static final int bGZ = 20940;
    private static final int bHa = 20939;
    private static final int bHb = 22089;
    private static final int bHc = 20573;
    private static final int bqg = 20784;
    private static final int bqi = 21639;
    private static final int bHd = 21380;
    private static final int bHe = 20802;
    private static final int bHf = 21318;
    private static final int bHg = 21322;
    private static final int bHh = 21314;
    private static final int bHi = 21316;
    private static final int bHj = 21320;
    private static final int bHk = 22008;
    private static final int bqO = 20991;
    private static final int aBF = 20560;
    private static final int bHl = 20341;
    private static final int bfu = 20115;
    private static final int bHm = 20839;
    private static final int bHn = 21611;
    private static final int bHo = 21612;
    private static final int beM = 20448;
    private static final int bHp = 20847;
    private static final int bHq = 21635;
    private static final int bHr = 21636;
    private static final int bHs = 20017;
    private static final int bHt = 20798;
    private static final int bHu = 20132;
    private static final int bjm = 20078;
    private static final int bHv = 20553;
    private static final int bHw = 20120;
    private static final int bHx = 22025;
    private static final int bhS = 20176;
    private static final int bHy = 20833;
    private static final int bHz = 21605;
    private static final int bHA = 21606;
    private static final int bHB = 21623;
    private static final int bHC = 21623;
    private static final int bHD = 21624;
    private static final int bHE = 7586;

    public _426_QuestforFishingShot() {
        super(1);
        this.addStartNpc(31989, 31576, 31567, 31569, 31697, 32007, 31578, 31571, 31579, 31562, 31570, 31577, 31575, 31564, 31573, 31572, 31565, 31568, 31563, 31696, 31566, 31574);
        this.addKillId(20074, 20077, 20079, 20080, 20081, 20084, 20087, 20088, 20819, 20812, 20456, 21024, 20386, 22046, 20772, 21058, 21060, 20983, 20985, 20043, 20317, 20814, 20794, 20796, 20389, 20267, 20268, 20270, 20269, 20271, 22022, 21539, 21540, 21536, 21520, 21544, 21530, 21523, 21533, 21534, 21526, 21527, 21528, 21269, 21271, 20349, 21273, 20350, 21270, 21272, 20552, 20135, 20804, 20806, 21106, 20790, 22048, 20346, 20105, 20345, 21105, 21107, 21104, 20781, 21387, 21655, 21390, 21656, 21382, 21384, 21654, 20557, 20783, 21638, 20013, 20463, 20536, 20538, 20544, 21395, 20539, 20537, 20978, 20979, 20800, 20667, 20792, 20433, 20841, 21617, 21618, 21518, 20651, 20811, 20809, 20555, 21025, 20324, 20528, 20554, 20083, 22002, 20368, 20659, 20525, 20333, 20859, 21068, 20550, 20994, 20646, 20824, 21061, 20825, 20822, 20816, 20641, 20644, 20643, 20663, 20661, 20308, 20815, 20241, 20286, 20403, 20005, 20471, 20470, 21117, 20476, 20475, 20650, 20487, 20030, 20356, 21100, 21101, 20357, 20652, 20070, 20658, 20657, 20656, 20655, 20786, 21644, 20849, 20044, 21393, 21657, 21078, 20363, 20834, 20157, 20234, 20232, 20230, 22093, 22081, 22097, 22057, 22051, 20639, 20636, 20638, 20225, 20227, 21516, 21517, 21514, 20943, 20944, 20089, 21125, 20808, 21103, 20575, 20576, 20162, 21034, 21641, 20066, 20058, 20063, 20547, 21261, 21262, 21263, 21264, 20131, 20648, 20836, 20845, 21629, 21630, 20511, 21102, 20829, 20828, 21072, 21066, 20050, 20106, 21081, 21542, 21543, 20085, 20312, 20788, 20551, 20559, 21378, 21652, 21376, 21071, 20810, 20404, 21026, 22020, 20100, 21075, 21023, 21508, 21511, 21512, 21510, 20562, 20046, 20016, 20047, 20630, 20634, 20665, 20632, 20837, 20936, 20941, 20937, 20940, 20939, 22089, 20573, 20784, 21639, 21380, 20802, 21318, 21322, 21314, 21316, 21320, 22008, 20991, 20560, 20341, 20115, 20839, 21611, 21612, 20448, 20847, 21635, 21636, 20017, 20798, 20132, 20078, 20553, 20120, 22025, 20176, 20833, 21605, 21606, 21623, 21623, 21624);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            questState.setCond(1);
            string2 = "fisher_berix_q0426_03.htm";
        } else if (string.equalsIgnoreCase("reply_1")) {
            string2 = "fisher_berix_q0426_06.htm";
        } else if (string.equalsIgnoreCase("reply_2")) {
            string2 = "fisher_berix_q0426_07.htm";
        } else if (string.equalsIgnoreCase("reply_3")) {
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
            string2 = "fisher_berix_q0426_08.htm";
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getCond();
        int n2 = questState.getState();
        if (n2 == 1) {
            string = "fisher_berix_q0426_01.htm";
        } else if (n == 1 && questState.getQuestItemsCount(7586) == 0L) {
            string = "fisher_berix_q0426_04.htm";
        } else if (n == 1 && questState.getQuestItemsCount(7586) >= 1L) {
            string = "fisher_berix_q0426_05.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (Rnd.chance(25)) {
            questState.rollAndGive(7586, 1, 100.0);
            questState.playSound("ItemSound.quest_itemget");
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

