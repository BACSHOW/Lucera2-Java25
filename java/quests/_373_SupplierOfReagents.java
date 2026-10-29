/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _373_SupplierOfReagents
extends Quest
implements ScriptFile {
    private static final int buF = 30166;
    private static final int bbl = 31149;
    private static final int buG = 20813;
    private static final int buH = 20822;
    private static final int bnR = 20828;
    private static final int buI = 21061;
    private static final int buJ = 21066;
    private static final int buK = 21111;
    private static final int buL = 21115;
    private static final int buM = 6317;
    private static final int buN = 6021;
    private static final int biL = 735;
    private static final int buO = 6022;
    private static final int buP = 737;
    private static final int buQ = 6023;
    private static final int buR = 2508;
    private static final int buS = 6024;
    private static final int boG = 4042;
    private static final int buT = 4043;
    private static final int buU = 4044;
    private static final int buV = 6025;
    private static final int buW = 4953;
    private static final int buX = 4959;
    private static final int buY = 4960;
    private static final int buZ = 4958;
    private static final int bva = 6026;
    private static final int bvb = 4998;
    private static final int bvc = 4992;
    private static final int bvd = 6027;
    private static final int bve = 6028;
    private static final int bvf = 4993;
    private static final int bvg = 4999;
    private static final int bbV = 6029;
    private static final int bvh = 5478;
    private static final int bvi = 5479;
    private static final int bvj = 5480;
    private static final int bvk = 5481;
    private static final int bvl = 5482;
    private static final int bvm = 6030;
    private static final int bvn = 5520;
    private static final int bvo = 5521;
    private static final int bvp = 5522;
    private static final int bvq = 5523;
    private static final int bvr = 5524;
    private static final int bvs = 6031;
    private static final int bvt = 6032;
    private static final int bbW = 6033;
    private static final int bvu = 6034;
    private static final int bvv = 6011;
    private static final int bvw = 6012;
    private static final int bvx = 6013;
    private static final int bvy = 6014;
    private static final int bvz = 6015;
    private static final int bvA = 6016;
    private static final int bvB = 6017;
    private static final int bvC = 6018;
    private static final int bvD = 6019;
    private static final int bvE = 6020;
    private static final int bvF = 6007;
    private static final int bvG = 6008;
    private static final int bvH = 6009;
    private static final int bvI = 6010;
    private static final int bvJ = 103;
    private static final int bvK = 2437;
    private static final int bvL = 630;
    private static final int bvM = 612;
    private static final int bvN = 2464;
    private static final int bvO = 554;
    private static final int bvP = 600;
    private static final int bvQ = 2439;
    private static final int bvR = 601;
    private static final int bvS = 2487;
    private static final int bvT = 2475;
    private static final int bvU = 5904;
    private static final int bbr = 6320;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _373_SupplierOfReagents() {
        super(1);
        this.addStartNpc(30166);
        this.addTalkId(31149);
        this.addKillId(20813, 20822, 20828, 21061, 21066, 21111, 21115);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = npcInstance.getNpcId();
        int n2 = questState.getInt("reagent_supplier");
        int n3 = questState.getInt("reagent_supplier_ex");
        if (n == 30166) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.setState(2);
                questState.giveItems(5904, 1L);
                questState.giveItems(6317, 1L);
                questState.playSound("ItemSound.quest_accept");
                string2 = "bandor_q0373_04.htm";
            } else if (string.equalsIgnoreCase("reply_1")) {
                string2 = "bandor_q0373_03.htm";
            } else if (string.equalsIgnoreCase("reply_2")) {
                string2 = "bandor_q0373_06.htm";
            } else if (string.equalsIgnoreCase("reply_3")) {
                string2 = "bandor_q0373_07.htm";
            } else if (string.equalsIgnoreCase("reply_4")) {
                string2 = "bandor_q0373_08.htm";
            } else if (string.equalsIgnoreCase("reply_5")) {
                questState.takeItems(5904, -1L);
                questState.takeItems(6317, -1L);
                questState.unset("reagent_supplier");
                questState.unset("reagent_supplier_ex");
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                string2 = "bandor_q0373_09.htm";
            } else if (string.equalsIgnoreCase("reply_11")) {
                if (questState.getQuestItemsCount(6021) >= 1L) {
                    questState.giveItems(735, 1L);
                    questState.takeItems(6021, 1L);
                    string2 = "bandor_q0373_10.htm";
                } else {
                    string2 = "bandor_q0373_24.htm";
                }
            } else if (string.equalsIgnoreCase("reply_12")) {
                if (questState.getQuestItemsCount(6022) >= 1L) {
                    questState.giveItems(737, 2L);
                    questState.takeItems(6022, 1L);
                    string2 = "bandor_q0373_11.htm";
                } else {
                    string2 = "bandor_q0373_24.htm";
                }
            } else if (string.equalsIgnoreCase("reply_13")) {
                if (questState.getQuestItemsCount(6023) >= 1L) {
                    questState.giveItems(2508, 25L);
                    questState.takeItems(6023, 1L);
                    string2 = "bandor_q0373_12.htm";
                } else {
                    string2 = "bandor_q0373_24.htm";
                }
            } else if (string.equalsIgnoreCase("reply_14")) {
                if (questState.getQuestItemsCount(6024) >= 1L) {
                    int n4 = Rnd.get(100);
                    if (n4 < 32) {
                        questState.giveItems(4042, 1L);
                    } else if (n4 < 66) {
                        questState.giveItems(4043, 1L);
                    } else {
                        questState.giveItems(4044, 1L);
                    }
                    questState.giveItems(57, 500L);
                    questState.takeItems(6024, 1L);
                    string2 = "bandor_q0373_13.htm";
                } else {
                    string2 = "bandor_q0373_24.htm";
                }
            } else if (string.equalsIgnoreCase("reply_15")) {
                if (questState.getQuestItemsCount(6025) >= 1L) {
                    int n5 = Rnd.get(100);
                    if (n5 < 28) {
                        questState.giveItems(735, 20L);
                    } else if (n5 < 68) {
                        questState.giveItems(4953, 1L);
                    } else {
                        questState.giveItems(4959, 1L);
                    }
                    questState.takeItems(6025, 1L);
                    string2 = "bandor_q0373_14.htm";
                } else {
                    string2 = "bandor_q0373_24.htm";
                }
            } else if (string.equalsIgnoreCase("reply_16")) {
                if (questState.getQuestItemsCount(6026) >= 1L) {
                    int n6 = Rnd.get(100);
                    if (n6 < 40) {
                        questState.giveItems(2508, 200L);
                    } else if (n6 < 70) {
                        questState.giveItems(4960, 1L);
                    } else {
                        questState.giveItems(4958, 1L);
                    }
                    questState.takeItems(6026, 1L);
                    string2 = "bandor_q0373_15.htm";
                } else {
                    string2 = "bandor_q0373_24.htm";
                }
            } else if (string.equalsIgnoreCase("reply_17")) {
                if (questState.getQuestItemsCount(6027) >= 1L) {
                    int n7 = Rnd.get(100);
                    if (n7 < 40) {
                        questState.giveItems(4998, 1L);
                    } else if (n7 < 80) {
                        questState.giveItems(4992, 1L);
                    } else {
                        questState.giveItems(737, 20L);
                    }
                    questState.takeItems(6027, 1L);
                    string2 = "bandor_q0373_16.htm";
                } else {
                    string2 = "bandor_q0373_24.htm";
                }
            } else if (string.equalsIgnoreCase("reply_18")) {
                if (questState.getQuestItemsCount(6028) >= 1L) {
                    int n8 = Rnd.get(100);
                    if (n8 < 90) {
                        questState.giveItems(4993, 1L);
                        questState.giveItems(4999, 1L);
                    } else {
                        questState.giveItems(4042, 2L);
                    }
                    questState.takeItems(6028, 1L);
                    string2 = "bandor_q0373_17.htm";
                } else {
                    string2 = "bandor_q0373_24.htm";
                }
            } else if (string.equalsIgnoreCase("reply_19")) {
                if (questState.getQuestItemsCount(6029) >= 1L) {
                    int n9 = Rnd.get(100);
                    if (n9 < 20) {
                        questState.giveItems(5478, 2L);
                    } else if (n9 < 40) {
                        questState.giveItems(5479, 2L);
                    } else if (n9 < 60) {
                        questState.giveItems(5480, 2L);
                    } else if (n9 < 80) {
                        questState.giveItems(5481, 2L);
                    } else {
                        questState.giveItems(5482, 2L);
                    }
                    questState.giveItems(57, 8000L);
                    questState.takeItems(6029, 1L);
                    string2 = "bandor_q0373_18.htm";
                } else {
                    string2 = "bandor_q0373_24.htm";
                }
            } else if (string.equalsIgnoreCase("reply_20")) {
                if (questState.getQuestItemsCount(6030) >= 1L) {
                    int n10 = Rnd.get(100);
                    if (n10 < 20) {
                        questState.giveItems(5520, 3L);
                    } else if (n10 < 40) {
                        questState.giveItems(5521, 3L);
                    } else if (n10 < 60) {
                        questState.giveItems(5522, 3L);
                    } else if (n10 < 80) {
                        questState.giveItems(5523, 3L);
                    } else {
                        questState.giveItems(5524, 3L);
                    }
                    questState.giveItems(57, 8000L);
                    questState.takeItems(6030, 1L);
                    string2 = "bandor_q0373_19.htm";
                } else {
                    string2 = "bandor_q0373_24.htm";
                }
            } else if (string.equalsIgnoreCase("reply_21")) {
                if (questState.getQuestItemsCount(6031) >= 1L) {
                    int n11 = Rnd.get(100);
                    if (n11 < 40) {
                        questState.giveItems(57, 32000L);
                    } else if (n11 < 80) {
                        questState.giveItems(57, 24500L);
                    } else {
                        questState.giveItems(57, 16000L);
                    }
                    questState.takeItems(6031, 1L);
                    string2 = "bandor_q0373_20.htm";
                } else {
                    string2 = "bandor_q0373_24.htm";
                }
            } else if (string.equalsIgnoreCase("reply_22")) {
                if (questState.getQuestItemsCount(6032) >= 1L) {
                    int n12 = Rnd.get(100);
                    if (n12 < 30) {
                        questState.giveItems(103, 1L);
                    } else if (n12 < 60) {
                        questState.giveItems(2437, 1L);
                    } else {
                        questState.giveItems(630, 1L);
                    }
                    questState.giveItems(57, 5000L);
                    questState.takeItems(6032, 1L);
                    string2 = "bandor_q0373_21.htm";
                } else {
                    string2 = "bandor_q0373_24.htm";
                }
            } else if (string.equalsIgnoreCase("reply_23")) {
                if (questState.getQuestItemsCount(6033) >= 1L) {
                    int n13 = Rnd.get(100);
                    if (n13 < 25) {
                        questState.giveItems(612, 1L);
                    } else if (n13 < 50) {
                        questState.giveItems(2464, 1L);
                    } else if (n13 < 75) {
                        questState.giveItems(554, 1L);
                    } else {
                        questState.giveItems(600, 1L);
                    }
                    questState.takeItems(6033, 1L);
                    string2 = "bandor_q0373_22.htm";
                } else {
                    string2 = "bandor_q0373_24.htm";
                }
            } else if (string.equalsIgnoreCase("reply_24")) {
                if (questState.getQuestItemsCount(6034) >= 1L) {
                    int n14 = Rnd.get(100);
                    if (n14 < 17) {
                        questState.giveItems(2439, 1L);
                    } else if (n14 < 34) {
                        questState.giveItems(601, 1L);
                    } else if (n14 < 51) {
                        questState.giveItems(2487, 1L);
                    } else if (n14 < 68) {
                        questState.giveItems(2475, 1L);
                    } else {
                        questState.giveItems(4992, 1L);
                        questState.giveItems(4998, 1L);
                    }
                    questState.giveItems(57, 19000L);
                    questState.takeItems(6034, 1L);
                    string2 = "bandor_q0373_23.htm";
                } else {
                    string2 = "bandor_q0373_24.htm";
                }
            }
        } else if (n == 31149) {
            if (string.equalsIgnoreCase("reply_1")) {
                questState.set("reagent_supplier", String.valueOf(0), true);
                questState.set("reagent_supplier_ex", String.valueOf(0), true);
                string2 = "alchemical_mixing_jar_q0373_02.htm";
            } else if (string.equalsIgnoreCase("reply_11")) {
                if (questState.getQuestItemsCount(6011) >= 10L) {
                    questState.set("reagent_supplier", String.valueOf(11), true);
                    string2 = "alchemical_mixing_jar_q0373_03.htm";
                    questState.playSound("SkillSound5.liquid_mix_01");
                } else {
                    string2 = "alchemical_mixing_jar_q0373_17.htm";
                }
            } else if (string.equalsIgnoreCase("reply_12")) {
                if (questState.getQuestItemsCount(6012) >= 10L) {
                    questState.set("reagent_supplier", String.valueOf(12), true);
                    string2 = "alchemical_mixing_jar_q0373_04.htm";
                    questState.playSound("SkillSound5.liquid_mix_01");
                } else {
                    string2 = "alchemical_mixing_jar_q0373_17.htm";
                }
            } else if (string.equalsIgnoreCase("reply_13")) {
                if (questState.getQuestItemsCount(6013) >= 10L) {
                    questState.set("reagent_supplier", String.valueOf(13), true);
                    string2 = "alchemical_mixing_jar_q0373_05.htm";
                    questState.playSound("SkillSound5.liquid_mix_01");
                } else {
                    string2 = "alchemical_mixing_jar_q0373_17.htm";
                }
            } else if (string.equalsIgnoreCase("reply_14")) {
                if (questState.getQuestItemsCount(6014) >= 10L) {
                    questState.set("reagent_supplier", String.valueOf(14), true);
                    string2 = "alchemical_mixing_jar_q0373_06.htm";
                    questState.playSound("SkillSound5.liquid_mix_01");
                } else {
                    string2 = "alchemical_mixing_jar_q0373_17.htm";
                }
            } else if (string.equalsIgnoreCase("reply_15")) {
                if (questState.getQuestItemsCount(6015) >= 10L) {
                    questState.set("reagent_supplier", String.valueOf(15), true);
                    string2 = "alchemical_mixing_jar_q0373_07.htm";
                    questState.playSound("SkillSound5.liquid_mix_01");
                } else {
                    string2 = "alchemical_mixing_jar_q0373_17.htm";
                }
            } else if (string.equalsIgnoreCase("reply_16")) {
                if (questState.getQuestItemsCount(6016) >= 10L) {
                    questState.set("reagent_supplier", String.valueOf(16), true);
                    string2 = "alchemical_mixing_jar_q0373_08.htm";
                    questState.playSound("SkillSound5.liquid_mix_01");
                } else {
                    string2 = "alchemical_mixing_jar_q0373_17.htm";
                }
            } else if (string.equalsIgnoreCase("reply_17")) {
                if (questState.getQuestItemsCount(6021) >= 10L) {
                    questState.set("reagent_supplier", String.valueOf(17), true);
                    string2 = "alchemical_mixing_jar_q0373_09.htm";
                    questState.playSound("SkillSound5.liquid_mix_01");
                } else {
                    string2 = "alchemical_mixing_jar_q0373_17.htm";
                }
            } else if (string.equalsIgnoreCase("reply_18")) {
                if (questState.getQuestItemsCount(6022) >= 10L) {
                    questState.set("reagent_supplier", String.valueOf(18), true);
                    string2 = "alchemical_mixing_jar_q0373_10.htm";
                    questState.playSound("SkillSound5.liquid_mix_01");
                } else {
                    string2 = "alchemical_mixing_jar_q0373_17.htm";
                }
            } else if (string.equalsIgnoreCase("reply_19")) {
                if (questState.getQuestItemsCount(6023) >= 10L) {
                    questState.set("reagent_supplier", String.valueOf(19), true);
                    string2 = "alchemical_mixing_jar_q0373_11.htm";
                    questState.playSound("SkillSound5.liquid_mix_01");
                } else {
                    string2 = "alchemical_mixing_jar_q0373_17.htm";
                }
            } else if (string.equalsIgnoreCase("reply_20")) {
                if (questState.getQuestItemsCount(6024) >= 10L) {
                    questState.set("reagent_supplier", String.valueOf(20), true);
                    string2 = "alchemical_mixing_jar_q0373_12.htm";
                    questState.playSound("SkillSound5.liquid_mix_01");
                } else {
                    string2 = "alchemical_mixing_jar_q0373_17.htm";
                }
            } else if (string.equalsIgnoreCase("reply_21")) {
                if (questState.getQuestItemsCount(6025) >= 10L) {
                    questState.set("reagent_supplier", String.valueOf(21), true);
                    string2 = "alchemical_mixing_jar_q0373_13.htm";
                    questState.playSound("SkillSound5.liquid_mix_01");
                } else {
                    string2 = "alchemical_mixing_jar_q0373_17.htm";
                }
            } else if (string.equalsIgnoreCase("reply_22")) {
                if (questState.getQuestItemsCount(6026) >= 10L) {
                    questState.set("reagent_supplier", String.valueOf(22), true);
                    string2 = "alchemical_mixing_jar_q0373_14.htm";
                    questState.playSound("SkillSound5.liquid_mix_01");
                } else {
                    string2 = "alchemical_mixing_jar_q0373_17.htm";
                }
            } else if (string.equalsIgnoreCase("reply_23")) {
                if (questState.getQuestItemsCount(6028) >= 1L) {
                    questState.set("reagent_supplier", String.valueOf(23), true);
                    string2 = "alchemical_mixing_jar_q0373_15.htm";
                    questState.playSound("SkillSound5.liquid_mix_01");
                } else {
                    string2 = "alchemical_mixing_jar_q0373_17.htm";
                }
            } else if (string.equalsIgnoreCase("reply_24")) {
                if (questState.getQuestItemsCount(6029) >= 1L) {
                    questState.set("reagent_supplier", String.valueOf(24), true);
                    string2 = "alchemical_mixing_jar_q0373_16.htm";
                    questState.playSound("SkillSound5.liquid_mix_01");
                } else {
                    string2 = "alchemical_mixing_jar_q0373_17.htm";
                }
            } else if (string.equalsIgnoreCase("reply_2")) {
                string2 = "alchemical_mixing_jar_q0373_18.htm";
            } else if (string.equalsIgnoreCase("reply_31")) {
                if (questState.getQuestItemsCount(6017) >= 1L) {
                    questState.set("reagent_supplier", String.valueOf(n2 + 1100), true);
                    string2 = "alchemical_mixing_jar_q0373_19.htm";
                    questState.playSound("SkillSound5.liquid_mix_01");
                } else {
                    if (n2 == 11) {
                        questState.takeItems(6011, 10L);
                    } else if (n2 == 12) {
                        questState.takeItems(6012, 10L);
                    } else if (n2 == 13) {
                        questState.takeItems(6013, 10L);
                    } else if (n2 == 14) {
                        questState.takeItems(6014, 10L);
                    } else if (n2 == 15) {
                        questState.takeItems(6015, 10L);
                    } else if (n2 == 16) {
                        questState.takeItems(6016, 10L);
                    } else if (n2 == 17) {
                        questState.takeItems(6021, 10L);
                    } else if (n2 == 18) {
                        questState.takeItems(6022, 10L);
                    } else if (n2 == 19) {
                        questState.takeItems(6023, 10L);
                    } else if (n2 == 20) {
                        questState.takeItems(6024, 10L);
                    } else if (n2 == 21) {
                        questState.takeItems(6025, 10L);
                    } else if (n2 == 22) {
                        questState.takeItems(6026, 10L);
                    } else if (n2 == 23) {
                        questState.takeItems(6028, 1L);
                    } else if (n2 == 24) {
                        questState.takeItems(6029, 1L);
                    }
                    string2 = "alchemical_mixing_jar_q0373_25.htm";
                }
            } else if (string.equalsIgnoreCase("reply_32")) {
                if (questState.getQuestItemsCount(6018) >= 1L) {
                    questState.set("reagent_supplier", String.valueOf(n2 + 1200), true);
                    string2 = "alchemical_mixing_jar_q0373_20.htm";
                    questState.playSound("SkillSound5.liquid_mix_01");
                } else {
                    if (n2 == 11) {
                        questState.takeItems(6011, 10L);
                    } else if (n2 == 12) {
                        questState.takeItems(6012, 10L);
                    } else if (n2 == 13) {
                        questState.takeItems(6013, 10L);
                    } else if (n2 == 14) {
                        questState.takeItems(6014, 10L);
                    } else if (n2 == 15) {
                        questState.takeItems(6015, 10L);
                    } else if (n2 == 16) {
                        questState.takeItems(6016, 10L);
                    } else if (n2 == 17) {
                        questState.takeItems(6021, 10L);
                    } else if (n2 == 18) {
                        questState.takeItems(6022, 10L);
                    } else if (n2 == 19) {
                        questState.takeItems(6023, 10L);
                    } else if (n2 == 20) {
                        questState.takeItems(6024, 10L);
                    } else if (n2 == 21) {
                        questState.takeItems(6025, 10L);
                    } else if (n2 == 22) {
                        questState.takeItems(6026, 10L);
                    } else if (n2 == 23) {
                        questState.takeItems(6028, 1L);
                    } else if (n2 == 24) {
                        questState.takeItems(6029, 1L);
                    }
                    string2 = "alchemical_mixing_jar_q0373_17.htm";
                }
            } else if (string.equalsIgnoreCase("reply_33")) {
                if (questState.getQuestItemsCount(6019) >= 1L) {
                    questState.set("reagent_supplier", String.valueOf(n2 + 1300), true);
                    string2 = "alchemical_mixing_jar_q0373_21.htm";
                    questState.playSound("SkillSound5.liquid_mix_01");
                } else {
                    if (n2 == 11) {
                        questState.takeItems(6011, 10L);
                    } else if (n2 == 12) {
                        questState.takeItems(6012, 10L);
                    } else if (n2 == 13) {
                        questState.takeItems(6013, 10L);
                    } else if (n2 == 14) {
                        questState.takeItems(6014, 10L);
                    } else if (n2 == 15) {
                        questState.takeItems(6015, 10L);
                    } else if (n2 == 16) {
                        questState.takeItems(6016, 10L);
                    } else if (n2 == 17) {
                        questState.takeItems(6021, 10L);
                    } else if (n2 == 18) {
                        questState.takeItems(6022, 10L);
                    } else if (n2 == 19) {
                        questState.takeItems(6023, 10L);
                    } else if (n2 == 20) {
                        questState.takeItems(6024, 10L);
                    } else if (n2 == 21) {
                        questState.takeItems(6025, 10L);
                    } else if (n2 == 22) {
                        questState.takeItems(6026, 10L);
                    } else if (n2 == 23) {
                        questState.takeItems(6028, 1L);
                    } else if (n2 == 24) {
                        questState.takeItems(6029, 1L);
                    }
                    string2 = "alchemical_mixing_jar_q0373_17.htm";
                }
            } else if (string.equalsIgnoreCase("reply_34")) {
                if (questState.getQuestItemsCount(6020) >= 1L) {
                    questState.set("reagent_supplier", String.valueOf(n2 + 1400), true);
                    string2 = "alchemical_mixing_jar_q0373_22.htm";
                    questState.playSound("SkillSound5.liquid_mix_01");
                } else {
                    if (n2 == 11) {
                        questState.takeItems(6011, 10L);
                    } else if (n2 == 12) {
                        questState.takeItems(6012, 10L);
                    } else if (n2 == 13) {
                        questState.takeItems(6013, 10L);
                    } else if (n2 == 14) {
                        questState.takeItems(6014, 10L);
                    } else if (n2 == 15) {
                        questState.takeItems(6015, 10L);
                    } else if (n2 == 16) {
                        questState.takeItems(6016, 10L);
                    } else if (n2 == 17) {
                        questState.takeItems(6021, 10L);
                    } else if (n2 == 18) {
                        questState.takeItems(6022, 10L);
                    } else if (n2 == 19) {
                        questState.takeItems(6023, 10L);
                    } else if (n2 == 20) {
                        questState.takeItems(6024, 10L);
                    } else if (n2 == 21) {
                        questState.takeItems(6025, 10L);
                    } else if (n2 == 22) {
                        questState.takeItems(6026, 10L);
                    } else if (n2 == 23) {
                        questState.takeItems(6028, 1L);
                    } else if (n2 == 24) {
                        questState.takeItems(6029, 1L);
                    }
                    string2 = "alchemical_mixing_jar_q0373_17.htm";
                }
            } else if (string.equalsIgnoreCase("reply_35")) {
                if (questState.getQuestItemsCount(6031) >= 1L) {
                    questState.set("reagent_supplier", String.valueOf(n2 + 1500), true);
                    string2 = "alchemical_mixing_jar_q0373_23.htm";
                    questState.playSound("SkillSound5.liquid_mix_01");
                } else {
                    if (n2 == 11) {
                        questState.takeItems(6011, 10L);
                    } else if (n2 == 12) {
                        questState.takeItems(6012, 10L);
                    } else if (n2 == 13) {
                        questState.takeItems(6013, 10L);
                    } else if (n2 == 14) {
                        questState.takeItems(6014, 10L);
                    } else if (n2 == 15) {
                        questState.takeItems(6015, 10L);
                    } else if (n2 == 16) {
                        questState.takeItems(6016, 10L);
                    } else if (n2 == 17) {
                        questState.takeItems(6021, 10L);
                    } else if (n2 == 18) {
                        questState.takeItems(6022, 10L);
                    } else if (n2 == 19) {
                        questState.takeItems(6023, 10L);
                    } else if (n2 == 20) {
                        questState.takeItems(6024, 10L);
                    } else if (n2 == 21) {
                        questState.takeItems(6025, 10L);
                    } else if (n2 == 22) {
                        questState.takeItems(6026, 10L);
                    } else if (n2 == 23) {
                        questState.takeItems(6028, 1L);
                    } else if (n2 == 24) {
                        questState.takeItems(6029, 1L);
                    }
                    string2 = "alchemical_mixing_jar_q0373_17.htm";
                }
            } else if (string.equalsIgnoreCase("reply_36")) {
                if (questState.getQuestItemsCount(6030) >= 1L) {
                    questState.set("reagent_supplier", String.valueOf(n2 + 1600), true);
                    string2 = "alchemical_mixing_jar_q0373_24.htm";
                    questState.playSound("SkillSound5.liquid_mix_01");
                } else {
                    if (n2 == 11) {
                        questState.takeItems(6011, 10L);
                    } else if (n2 == 12) {
                        questState.takeItems(6012, 10L);
                    } else if (n2 == 13) {
                        questState.takeItems(6013, 10L);
                    } else if (n2 == 14) {
                        questState.takeItems(6014, 10L);
                    } else if (n2 == 15) {
                        questState.takeItems(6015, 10L);
                    } else if (n2 == 16) {
                        questState.takeItems(6016, 10L);
                    } else if (n2 == 17) {
                        questState.takeItems(6021, 10L);
                    } else if (n2 == 18) {
                        questState.takeItems(6022, 10L);
                    } else if (n2 == 19) {
                        questState.takeItems(6023, 10L);
                    } else if (n2 == 20) {
                        questState.takeItems(6024, 10L);
                    } else if (n2 == 21) {
                        questState.takeItems(6025, 10L);
                    } else if (n2 == 22) {
                        questState.takeItems(6026, 10L);
                    } else if (n2 == 23) {
                        questState.takeItems(6028, 1L);
                    } else if (n2 == 24) {
                        questState.takeItems(6029, 1L);
                    }
                    string2 = "alchemical_mixing_jar_q0373_17.htm";
                }
            } else if (string.equalsIgnoreCase("reply_3")) {
                string2 = n2 == 1324 ? "alchemical_mixing_jar_q0373_26a.htm" : "alchemical_mixing_jar_q0373_26.htm";
            } else if (string.equalsIgnoreCase("reply_4")) {
                questState.set("reagent_supplier_ex", String.valueOf(1), true);
                string2 = "alchemical_mixing_jar_q0373_27.htm";
            } else if (string.equalsIgnoreCase("reply_5")) {
                if (Rnd.get(100) < 33) {
                    questState.set("reagent_supplier_ex", String.valueOf(3), true);
                    string2 = "alchemical_mixing_jar_q0373_28a.htm";
                } else {
                    questState.set("reagent_supplier_ex", String.valueOf(0), true);
                    string2 = "alchemical_mixing_jar_q0373_28a.htm";
                }
            } else if (string.equalsIgnoreCase("reply_6")) {
                if (Rnd.get(100) < 20) {
                    questState.set("reagent_supplier_ex", String.valueOf(5), true);
                    string2 = "alchemical_mixing_jar_q0373_29a.htm";
                } else {
                    questState.set("reagent_supplier_ex", String.valueOf(0), true);
                    string2 = "alchemical_mixing_jar_q0373_29a.htm";
                }
            } else if (string.equalsIgnoreCase("reply_7") && n3 != 0) {
                if (n2 == 1111) {
                    if (questState.getQuestItemsCount(6011) >= 10L && questState.getQuestItemsCount(6017) >= 1L) {
                        questState.takeItems(6011, 10L);
                        questState.takeItems(6017, 1L);
                        questState.giveItems(6021, n3);
                        questState.set("reagent_supplier", String.valueOf(0), true);
                        questState.set("reagent_supplier_ex", String.valueOf(0), true);
                        string2 = "alchemical_mixing_jar_q0373_30.htm";
                        questState.playSound("SkillSound5.liquid_success_01");
                    } else {
                        string2 = "alchemical_mixing_jar_q0373_44.htm";
                        questState.playSound("SkillSound5.liquid_fail_01");
                    }
                } else if (n2 == 1212) {
                    if (questState.getQuestItemsCount(6012) >= 10L && questState.getQuestItemsCount(6018) >= 1L) {
                        questState.takeItems(6012, 10L);
                        questState.takeItems(6018, 1L);
                        questState.giveItems(6022, n3);
                        questState.set("reagent_supplier", String.valueOf(0), true);
                        questState.set("reagent_supplier_ex", String.valueOf(0), true);
                        string2 = "alchemical_mixing_jar_q0373_31.htm";
                        questState.playSound("SkillSound5.liquid_success_01");
                    } else {
                        string2 = "alchemical_mixing_jar_q0373_44.htm";
                        questState.playSound("SkillSound5.liquid_fail_01");
                    }
                } else if (n2 == 1213) {
                    if (questState.getQuestItemsCount(6013) >= 10L && questState.getQuestItemsCount(6018) >= 1L) {
                        questState.takeItems(6013, 10L);
                        questState.takeItems(6018, 1L);
                        questState.giveItems(6023, n3);
                        questState.set("reagent_supplier", String.valueOf(0), true);
                        questState.set("reagent_supplier_ex", String.valueOf(0), true);
                        string2 = "alchemical_mixing_jar_q0373_32.htm";
                        questState.playSound("SkillSound5.liquid_success_01");
                    } else {
                        string2 = "alchemical_mixing_jar_q0373_44.htm";
                        questState.playSound("SkillSound5.liquid_fail_01");
                    }
                } else if (n2 == 1114) {
                    if (questState.getQuestItemsCount(6014) >= 10L && questState.getQuestItemsCount(6017) >= 1L) {
                        questState.takeItems(6014, 10L);
                        questState.takeItems(6017, 1L);
                        questState.giveItems(6024, n3);
                        questState.set("reagent_supplier", String.valueOf(0), true);
                        questState.set("reagent_supplier_ex", String.valueOf(0), true);
                        string2 = "alchemical_mixing_jar_q0373_33.htm";
                        questState.playSound("SkillSound5.liquid_success_01");
                    } else {
                        string2 = "alchemical_mixing_jar_q0373_44.htm";
                        questState.playSound("SkillSound5.liquid_fail_01");
                    }
                } else if (n2 == 1115) {
                    if (questState.getQuestItemsCount(6015) >= 10L && questState.getQuestItemsCount(6017) >= 1L) {
                        questState.takeItems(6015, 10L);
                        questState.takeItems(6017, 1L);
                        questState.giveItems(6025, n3);
                        questState.set("reagent_supplier", String.valueOf(0), true);
                        questState.set("reagent_supplier_ex", String.valueOf(0), true);
                        string2 = "alchemical_mixing_jar_q0373_34.htm";
                        questState.playSound("SkillSound5.liquid_success_01");
                    } else {
                        string2 = "alchemical_mixing_jar_q0373_44.htm";
                        questState.playSound("SkillSound5.liquid_fail_01");
                    }
                } else if (n2 == 1216) {
                    if (questState.getQuestItemsCount(6016) >= 10L && questState.getQuestItemsCount(6018) >= 1L) {
                        questState.takeItems(6016, 10L);
                        questState.takeItems(6018, 1L);
                        questState.giveItems(6026, n3);
                        questState.set("reagent_supplier", String.valueOf(0), true);
                        questState.set("reagent_supplier_ex", String.valueOf(0), true);
                        string2 = "alchemical_mixing_jar_q0373_35.htm";
                        questState.playSound("SkillSound5.liquid_success_01");
                    } else {
                        string2 = "alchemical_mixing_jar_q0373_44.htm";
                        questState.playSound("SkillSound5.liquid_fail_01");
                    }
                } else if (n2 == 1317) {
                    if (questState.getQuestItemsCount(6021) >= 10L && questState.getQuestItemsCount(6019) >= 1L) {
                        questState.takeItems(6021, 10L);
                        questState.takeItems(6019, 1L);
                        questState.giveItems(6027, n3);
                        questState.set("reagent_supplier", String.valueOf(0), true);
                        questState.set("reagent_supplier_ex", String.valueOf(0), true);
                        string2 = "alchemical_mixing_jar_q0373_36.htm";
                        questState.playSound("SkillSound5.liquid_success_01");
                    } else {
                        string2 = "alchemical_mixing_jar_q0373_44.htm";
                        questState.playSound("SkillSound5.liquid_fail_01");
                    }
                } else if (n2 == 1418) {
                    if (questState.getQuestItemsCount(6022) >= 10L && questState.getQuestItemsCount(6020) >= 1L) {
                        questState.takeItems(6022, 10L);
                        questState.takeItems(6020, 1L);
                        questState.giveItems(6028, n3);
                        questState.set("reagent_supplier", String.valueOf(0), true);
                        questState.set("reagent_supplier_ex", String.valueOf(0), true);
                        string2 = "alchemical_mixing_jar_q0373_37.htm";
                        questState.playSound("SkillSound5.liquid_success_01");
                    } else {
                        string2 = "alchemical_mixing_jar_q0373_44.htm";
                        questState.playSound("SkillSound5.liquid_fail_01");
                    }
                } else if (n2 == 1319) {
                    if (questState.getQuestItemsCount(6023) >= 10L && questState.getQuestItemsCount(6019) >= 1L) {
                        questState.takeItems(6023, 10L);
                        questState.takeItems(6019, 1L);
                        questState.giveItems(6029, n3);
                        questState.set("reagent_supplier", String.valueOf(0), true);
                        questState.set("reagent_supplier_ex", String.valueOf(0), true);
                        string2 = "alchemical_mixing_jar_q0373_38.htm";
                        questState.playSound("SkillSound5.liquid_success_01");
                    } else {
                        string2 = "alchemical_mixing_jar_q0373_44.htm";
                        questState.playSound("SkillSound5.liquid_fail_01");
                    }
                } else if (n2 == 1320) {
                    if (questState.getQuestItemsCount(6024) >= 10L && questState.getQuestItemsCount(6019) >= 1L) {
                        questState.takeItems(6024, 10L);
                        questState.takeItems(6019, 1L);
                        questState.giveItems(6030, n3);
                        questState.set("reagent_supplier", String.valueOf(0), true);
                        questState.set("reagent_supplier_ex", String.valueOf(0), true);
                        string2 = "alchemical_mixing_jar_q0373_39.htm";
                        questState.playSound("SkillSound5.liquid_success_01");
                    } else {
                        string2 = "alchemical_mixing_jar_q0373_44.htm";
                        questState.playSound("SkillSound5.liquid_fail_01");
                    }
                } else if (n2 == 1421) {
                    if (questState.getQuestItemsCount(6025) >= 10L && questState.getQuestItemsCount(6020) >= 1L) {
                        questState.takeItems(6025, 10L);
                        questState.takeItems(6020, 1L);
                        questState.giveItems(6031, n3);
                        questState.set("reagent_supplier", String.valueOf(0), true);
                        questState.set("reagent_supplier_ex", String.valueOf(0), true);
                        string2 = "alchemical_mixing_jar_q0373_40.htm";
                        questState.playSound("SkillSound5.liquid_success_01");
                    } else {
                        string2 = "alchemical_mixing_jar_q0373_44.htm";
                        questState.playSound("SkillSound5.liquid_fail_01");
                    }
                } else if (n2 == 1422) {
                    if (questState.getQuestItemsCount(6026) >= 10L && questState.getQuestItemsCount(6020) >= 1L) {
                        questState.takeItems(6026, 10L);
                        questState.takeItems(6020, 1L);
                        questState.giveItems(6032, n3);
                        questState.set("reagent_supplier", String.valueOf(0), true);
                        questState.set("reagent_supplier_ex", String.valueOf(0), true);
                        string2 = "alchemical_mixing_jar_q0373_41.htm";
                        questState.playSound("SkillSound5.liquid_success_01");
                    } else {
                        string2 = "alchemical_mixing_jar_q0373_44.htm";
                        questState.playSound("SkillSound5.liquid_fail_01");
                    }
                } else if (n2 == 1523) {
                    if (questState.getQuestItemsCount(6028) >= 1L && questState.getQuestItemsCount(6031) >= 1L) {
                        questState.takeItems(6028, 1L);
                        questState.takeItems(6031, 1L);
                        questState.giveItems(6033, n3);
                        questState.set("reagent_supplier", String.valueOf(0), true);
                        questState.set("reagent_supplier_ex", String.valueOf(0), true);
                        string2 = "alchemical_mixing_jar_q0373_42.htm";
                        questState.playSound("SkillSound5.liquid_success_01");
                    } else {
                        string2 = "alchemical_mixing_jar_q0373_44.htm";
                        questState.playSound("SkillSound5.liquid_fail_01");
                    }
                } else if (n2 == 1624) {
                    if (questState.getQuestItemsCount(6029) >= 1L && questState.getQuestItemsCount(6030) >= 1L) {
                        questState.takeItems(6029, 1L);
                        questState.takeItems(6030, 1L);
                        questState.giveItems(6034, n3);
                        questState.set("reagent_supplier", String.valueOf(0), true);
                        questState.set("reagent_supplier_ex", String.valueOf(0), true);
                        string2 = "alchemical_mixing_jar_q0373_43.htm";
                        questState.playSound("SkillSound5.liquid_success_01");
                    } else {
                        string2 = "alchemical_mixing_jar_q0373_44.htm";
                        questState.playSound("SkillSound5.liquid_fail_01");
                    }
                } else if (n2 == 1324) {
                    if (questState.getQuestItemsCount(6320) == 0L) {
                        if (questState.getQuestItemsCount(6029) >= 1L && questState.getQuestItemsCount(6019) >= 1L) {
                            questState.takeItems(6029, 1L);
                            questState.takeItems(6019, 1L);
                            questState.giveItems(6320, 1L);
                            questState.set("reagent_supplier", String.valueOf(0), true);
                            questState.set("reagent_supplier_ex", String.valueOf(0), true);
                            string2 = "alchemical_mixing_jar_q0373_46.htm";
                            questState.playSound("SkillSound5.liquid_success_01");
                        } else {
                            string2 = "alchemical_mixing_jar_q0373_44.htm";
                            questState.playSound("SkillSound5.liquid_fail_01");
                        }
                    } else {
                        string2 = "alchemical_mixing_jar_q0373_44.htm";
                    }
                } else if (n2 == 1324) {
                    string2 = "alchemical_mixing_jar_q0373_44.htm";
                } else {
                    int n15 = n2 / 100;
                    int n16 = n2 % 100;
                    if (n16 == 11) {
                        questState.takeItems(6011, 10L);
                    } else if (n16 == 12) {
                        questState.takeItems(6012, 10L);
                    } else if (n16 == 13) {
                        questState.takeItems(6013, 10L);
                    } else if (n16 == 14) {
                        questState.takeItems(6014, 10L);
                    } else if (n16 == 15) {
                        questState.takeItems(6015, 10L);
                    } else if (n16 == 16) {
                        questState.takeItems(6016, 10L);
                    } else if (n16 == 17) {
                        questState.takeItems(6021, 10L);
                    } else if (n16 == 18) {
                        questState.takeItems(6022, 10L);
                    } else if (n16 == 19) {
                        questState.takeItems(6023, 10L);
                    } else if (n16 == 20) {
                        questState.takeItems(6024, 10L);
                    } else if (n16 == 21) {
                        questState.takeItems(6025, 10L);
                    } else if (n16 == 22) {
                        questState.takeItems(6026, 10L);
                    } else if (n16 == 23) {
                        questState.takeItems(6028, 1L);
                    } else if (n16 == 24) {
                        questState.takeItems(6029, 1L);
                    }
                    if (n15 == 11) {
                        questState.takeItems(6017, 1L);
                    } else if (n15 == 12) {
                        questState.takeItems(6018, 1L);
                    } else if (n15 == 13) {
                        questState.takeItems(6019, 1L);
                    } else if (n15 == 14) {
                        questState.takeItems(6020, 1L);
                    } else if (n15 == 15) {
                        questState.takeItems(6031, 1L);
                    } else if (n15 == 16) {
                        questState.takeItems(6030, 1L);
                    }
                    string2 = "alchemical_mixing_jar_q0373_44.htm";
                    questState.playSound("SkillSound5.liquid_fail_01");
                }
            } else if (string.equalsIgnoreCase("reply_7") && n3 == 0) {
                int n17 = n2 / 100;
                int n18 = n2 % 100;
                if (n18 == 11) {
                    questState.takeItems(6011, 10L);
                } else if (n18 == 12) {
                    questState.takeItems(6012, 10L);
                } else if (n18 == 13) {
                    questState.takeItems(6013, 10L);
                } else if (n18 == 14) {
                    questState.takeItems(6014, 10L);
                } else if (n18 == 15) {
                    questState.takeItems(6015, 10L);
                } else if (n18 == 16) {
                    questState.takeItems(6016, 10L);
                } else if (n18 == 17) {
                    questState.takeItems(6021, 10L);
                } else if (n18 == 18) {
                    questState.takeItems(6022, 10L);
                } else if (n18 == 19) {
                    questState.takeItems(6023, 10L);
                } else if (n18 == 20) {
                    questState.takeItems(6024, 10L);
                } else if (n18 == 21) {
                    questState.takeItems(6025, 10L);
                } else if (n18 == 22) {
                    questState.takeItems(6026, 10L);
                } else if (n18 == 23) {
                    questState.takeItems(6028, 1L);
                } else if (n18 == 24) {
                    questState.takeItems(6029, 1L);
                }
                if (n17 == 11) {
                    questState.takeItems(6017, 1L);
                } else if (n17 == 12) {
                    questState.takeItems(6018, 1L);
                } else if (n17 == 13) {
                    questState.takeItems(6019, 1L);
                } else if (n17 == 14) {
                    questState.takeItems(6020, 1L);
                } else if (n17 == 15) {
                    questState.takeItems(6031, 1L);
                } else if (n17 == 16) {
                    questState.takeItems(6030, 1L);
                }
                string2 = "alchemical_mixing_jar_q0373_45.htm";
                questState.playSound("SkillSound5.liquid_fail_01");
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("reagent_supplier");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 30166) break;
                if (questState.getPlayer().getLevel() < 57) {
                    string = "bandor_q0373_01.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                string = "bandor_q0373_02.htm";
                break;
            }
            case 2: {
                if (n2 == 30166) {
                    if (n != 0) break;
                    string = "bandor_q0373_05.htm";
                    break;
                }
                if (n2 != 31149 || n < 0) break;
                string = "alchemical_mixing_jar_q0373_01.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n;
        int n2 = npcInstance.getNpcId();
        if (n2 == 20813) {
            int n3 = Rnd.get(1000);
            if (n3 < 618) {
                questState.rollAndGive(6014, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n3 < 1000) {
                questState.rollAndGive(6019, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n2 == 20822) {
            int n4 = Rnd.get(100);
            if (n4 < 45) {
                questState.rollAndGive(6007, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n4 < 65) {
                questState.rollAndGive(6018, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n2 == 20828) {
            int n5 = Rnd.get(1000);
            if (n5 < 658) {
                questState.rollAndGive(6008, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n5 < 100) {
                questState.rollAndGive(6019, 2, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n2 == 21061) {
            int n6 = Rnd.get(1000);
            if (n6 < 766) {
                questState.rollAndGive(6015, 3, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n6 < 876) {
                questState.rollAndGive(6013, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n2 == 21066) {
            int n7 = Rnd.get(1000);
            if (n7 < 444) {
                questState.rollAndGive(6010, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n2 == 21111) {
            int n8 = Rnd.get(1000);
            if (n8 < 666) {
                questState.rollAndGive(6011, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n8 < 989) {
                questState.rollAndGive(6012, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n2 == 21115 && (n = Rnd.get(1000)) < 616) {
            questState.rollAndGive(6009, 1, 100.0);
            questState.playSound("ItemSound.quest_itemget");
        }
        return null;
    }
}

