/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;

public class _348_ArrogantSearch
extends Quest
implements ScriptFile {
    private static final int bnD = 30760;
    private static final int bnE = 30832;
    private static final int bnF = 30864;
    private static final int bnG = 30969;
    private static final int bnH = 30977;
    private static final int bnI = 30978;
    private static final int bnJ = 30979;
    private static final int bnK = 30980;
    private static final int bnL = 30144;
    private static final int bnM = 31001;
    private static final int bnN = 30645;
    private static final int bnO = 27182;
    private static final int bnP = 27183;
    private static final int bnQ = 27184;
    private static final int bnR = 20828;
    private static final int bnS = 20829;
    private static final int bnT = 20657;
    private static final int bnU = 20658;
    private static final int bnV = 20830;
    private static final int bnW = 20831;
    private static final int bnX = 20860;
    private static final int bnY = 4287;
    private static final int bnZ = 4288;
    private static final int boa = 4289;
    private static final int bob = 4290;
    private static final int boc = 4291;
    private static final int bod = 4292;
    private static final int boe = 4293;
    private static final int bof = 4294;
    private static final int bog = 4295;
    private static final int boh = 5232;
    private static final int boi = 4394;
    private static final int boj = 4395;
    private static final int bok = 4396;
    private static final int bol = 4397;
    private static final int bom = 4398;
    private static final int bon = 4399;
    private static final int boo = 4400;
    private static final int bop = 1831;
    private static final int bdp = 1061;
    private static final int boq = 4109;
    private static final int bor = 1888;
    private static final int bos = 4119;
    private static final int bot = 1879;
    private static final int bou = 4111;
    private static final int bov = 4120;
    private static final int bow = 1874;
    private static final int box = 4107;
    private static final int boy = 4114;
    private static final int boz = 4112;
    private static final int boA = 4106;
    private static final int boB = 4121;
    private static final int boC = 1881;
    private static final int boD = 4105;
    private static final int boE = 4117;
    private static final int boF = 4113;
    private static final int boG = 4042;
    private static final int boH = 4115;
    private static final int boI = 4118;
    private static final int boJ = 1872;
    private static final int boK = 4110;
    private static final int boL = 4104;
    private static final int boM = 1887;
    private static final int boN = 4108;
    private static final int boO = 8;
    private static final int boP = 23;
    private static final int boQ = 36;
    private static final int boR = 93;
    private static final int boS = 101;
    private static final int boT = 108;
    private static final int bjq = 48;
    private static final int boU = 114;
    private static final int boV = 5;
    private static final int boW = 6;
    private static final int boX = 17;
    private static final int boY = 20;
    private static final int boZ = 21;
    private static final int bpa = 33;
    private static final int bpb = 34;
    private static final int bpc = 43;
    private static final int bpd = 90;
    private static final int bpe = 91;
    private static final int bpf = 98;
    private static final int bpg = 99;
    private static final int bph = 100;
    private static final int bpi = 106;
    private static final int bpj = 107;
    private static final int bpk = 112;
    private static final int bpl = 9;
    private static final int bpm = 24;
    private static final int bpn = 37;
    private static final int bpo = 92;
    private static final int bpp = 102;
    private static final int bpq = 109;
    private static final int bpr = 2;
    private static final int bps = 16;
    private static final int bpt = 30;
    private static final int bpu = 88;
    private static final int bpv = 97;
    private static final int bpw = 105;
    private static final int bpx = 3;
    private static final int bpy = 55;
    private static final int bpz = 57;
    private static final int bpA = 89;
    private static final int bpB = 117;
    private static final int bpC = 118;
    private static final int bpD = 12;
    private static final int bpE = 27;
    private static final int bpF = 51;
    private static final int bpG = 94;
    private static final int bpH = 103;
    private static final int bpI = 115;
    private static final int bpJ = 13;
    private static final int bpK = 40;
    private static final int bpL = 95;
    private static final int bpM = 110;
    private static final int bpN = 46;
    private static final int bpO = 113;
    private static final int bpP = 28;
    private static final int bpQ = 41;
    private static final int bpR = 104;
    private static final int bpS = 111;
    private static final int bpT = 52;
    private static final int bpU = 116;
    private static final int bpV = 14;
    private static final int bpW = 96;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _348_ArrogantSearch() {
        super(1);
        this.addStartNpc(30864);
        this.addTalkId(31001, 30978, 30645, 30979, 30144, 30980, 30977, 30969, 30832, 30760);
        this.addKillId(27182, 27183, 27184, 20828, 20829, 20657, 20658, 20830, 20831, 20860);
        this.addAttackId(27184, 27182, 27183, 20828, 20829);
        this.addQuestItem(4287, 4288, 4289, 4290, 4394, 4395, 4396, 4397, 4294, 4398, 4399, 4400, 5232, 4291, 4292, 4293);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getInt("arrogant_quest");
        int n2 = questState.getInt("arrogant_quest_ex_1");
        int n3 = npcInstance.getNpcId();
        if (n3 == 30864) {
            if (string.equalsIgnoreCase("quest_accept")) {
                if (questState.getQuestItemsCount(4394) == 1L && questState.getQuestItemsCount(4395) == 0L && questState.getQuestItemsCount(4396) == 0L) {
                    questState.takeItems(4394, 1L);
                    if (questState.getQuestItemsCount(4395) > 0L) {
                        questState.takeItems(4395, 1L);
                    }
                    if (questState.getQuestItemsCount(4396) > 0L) {
                        questState.takeItems(4396, 1L);
                    }
                    questState.setCond(1);
                    questState.set("arrogant_quest", String.valueOf(4), true);
                    questState.set("arrogant_quest_ex_0", String.valueOf(4), true);
                    questState.set("arrogant_quest_ex_1", String.valueOf(1), true);
                    questState.playSound("ItemSound.quest_accept");
                    questState.setState(2);
                    string2 = "magister_hanellin_q0348_15.htm";
                } else if (questState.getQuestItemsCount(4394) == 0L && questState.getQuestItemsCount(4395) == 1L && questState.getQuestItemsCount(4396) == 0L) {
                    questState.takeItems(4395, 1L);
                    if (questState.getQuestItemsCount(4394) > 0L) {
                        questState.takeItems(4394, 1L);
                    }
                    if (questState.getQuestItemsCount(4396) > 0L) {
                        questState.takeItems(4396, 1L);
                    }
                    questState.setCond(1);
                    questState.set("arrogant_quest", String.valueOf(4), true);
                    questState.set("arrogant_quest_ex_0", String.valueOf(4), true);
                    questState.set("arrogant_quest_ex_1", String.valueOf(2), true);
                    questState.playSound("ItemSound.quest_accept");
                    questState.setState(2);
                    string2 = "magister_hanellin_q0348_15.htm";
                } else if (questState.getQuestItemsCount(4394) == 0L && questState.getQuestItemsCount(4395) == 0L && questState.getQuestItemsCount(4396) == 1L) {
                    questState.takeItems(4396, 1L);
                    if (questState.getQuestItemsCount(4394) > 0L) {
                        questState.takeItems(4394, 1L);
                    }
                    if (questState.getQuestItemsCount(4395) > 0L) {
                        questState.takeItems(4395, 1L);
                    }
                    questState.setCond(1);
                    questState.set("arrogant_quest", String.valueOf(4), true);
                    questState.set("arrogant_quest_ex_0", String.valueOf(4), true);
                    questState.set("arrogant_quest_ex_1", String.valueOf(3), true);
                    questState.playSound("ItemSound.quest_accept");
                    questState.setState(2);
                    string2 = "magister_hanellin_q0348_15.htm";
                } else {
                    questState.setCond(2);
                    questState.set("arrogant_quest", String.valueOf(1), true);
                    questState.playSound("ItemSound.quest_accept");
                    questState.setState(2);
                    string2 = "magister_hanellin_q0348_05.htm";
                }
            } else if (string.equalsIgnoreCase("reply_1")) {
                if (n == 1 && questState.getQuestItemsCount(4287) > 0L || n == 2) {
                    questState.setCond(3);
                    questState.set("arrogant_quest", String.valueOf(3), true);
                    questState.giveItems(4394, 1L);
                    questState.giveItems(4395, 1L);
                    questState.giveItems(4396, 1L);
                    string2 = "magister_hanellin_q0348_08.htm";
                }
            } else if (string.equalsIgnoreCase("reply_2")) {
                if (n == 1 && questState.getQuestItemsCount(4287) > 0L || n == 2) {
                    questState.setCond(4);
                    questState.set("arrogant_quest", String.valueOf(4), true);
                    questState.set("arrogant_quest_ex_0", String.valueOf(4), true);
                    questState.set("arrogant_quest_ex_1", String.valueOf(0), true);
                    string2 = "magister_hanellin_q0348_09.htm";
                }
            } else if (string.equalsIgnoreCase("reply_3")) {
                if (n == 4 && n2 == 0) {
                    questState.setCond(5);
                    questState.set("arrogant_quest", String.valueOf(5), true);
                    questState.giveItems(4288, 1L);
                    questState.giveItems(4289, 1L);
                    questState.giveItems(4290, 1L);
                    string2 = "magister_hanellin_q0348_17.htm";
                }
            } else if (string.equalsIgnoreCase("reply_4")) {
                if (n == 4) {
                    if (n2 == 2 && questState.getQuestItemsCount(4289) == 0L) {
                        questState.setCond(7);
                        questState.set("arrogant_quest", String.valueOf(5), true);
                        questState.giveItems(4289, 1L);
                        string2 = "magister_hanellin_q0348_19.htm";
                    } else if (n2 == 1 && questState.getQuestItemsCount(4288) == 0L) {
                        questState.setCond(6);
                        questState.set("arrogant_quest", String.valueOf(5), true);
                        questState.giveItems(4288, 1L);
                        string2 = "magister_hanellin_q0348_18.htm";
                    } else if (n2 == 3 && questState.getQuestItemsCount(4290) == 0L) {
                        questState.setCond(8);
                        questState.set("arrogant_quest", String.valueOf(5), true);
                        questState.giveItems(4290, 1L);
                        string2 = "magister_hanellin_q0348_20.htm";
                    }
                }
            } else if (string.equalsIgnoreCase("reply_5")) {
                string2 = "magister_hanellin_q0348_21.htm";
            } else if (string.equalsIgnoreCase("reply_6")) {
                string2 = "magister_hanellin_q0348_22.htm";
            } else if (string.equalsIgnoreCase("reply_7")) {
                string2 = "magister_hanellin_q0348_23.htm";
            } else if (string.equalsIgnoreCase("reply_8")) {
                string2 = "magister_hanellin_q0348_32.htm";
            } else if (string.equalsIgnoreCase("reply_9")) {
                if (n == 10 && questState.getQuestItemsCount(4294) == 1L) {
                    questState.set("arrogant_quest", String.valueOf(11), true);
                    string2 = "magister_hanellin_q0348_33.htm";
                }
            } else if (string.equalsIgnoreCase("reply_10")) {
                if (n == 11 && questState.getQuestItemsCount(4294) == 1L && n2 > 0) {
                    int n4 = n2;
                    if (n4 == 1) {
                        questState.giveItems(57, 43000L);
                    } else if (n4 == 2) {
                        questState.giveItems(57, 4000L);
                    } else if (n4 == 3) {
                        questState.giveItems(57, 13000L);
                    }
                    questState.setCond(24);
                    questState.set("arrogant_quest", String.valueOf(12), true);
                    questState.set("arrogant_quest_ex_0", String.valueOf(12), true);
                    questState.set("arrogant_quest_ex_1", String.valueOf(100), true);
                    string2 = "magister_hanellin_q0348_34.htm";
                } else {
                    string2 = "magister_hanellin_q0348_35.htm";
                }
            } else if (string.equalsIgnoreCase("reply_11")) {
                if (n == 11 && n2 == 0 && questState.getQuestItemsCount(4294) == 1L) {
                    questState.setCond(24);
                    questState.set("arrogant_quest", String.valueOf(12), true);
                    questState.set("arrogant_quest_ex_0", String.valueOf(12), true);
                    questState.set("arrogant_quest_ex_1", String.valueOf(20000), true);
                    questState.giveItems(57, 49000L);
                    string2 = "magister_hanellin_q0348_36.htm";
                }
            } else if (string.equalsIgnoreCase("reply_12")) {
                if (n == 11 && n2 == 0 && questState.getQuestItemsCount(4294) == 1L) {
                    questState.setCond(25);
                    questState.set("arrogant_quest", String.valueOf(13), true);
                    questState.set("arrogant_quest_ex_0", String.valueOf(13), true);
                    questState.set("arrogant_quest_ex_1", String.valueOf(20000), true);
                    string2 = "magister_hanellin_q0348_37.htm";
                }
            } else if (string.equalsIgnoreCase("reply_13")) {
                if (n == 15) {
                    questState.set("arrogant_quest", String.valueOf(16), true);
                    string2 = "magister_hanellin_q0348_50.htm";
                }
            } else if (string.equalsIgnoreCase("reply_14")) {
                if (n == 15 || n == 16) {
                    if (questState.getQuestItemsCount(4295) > 0L && questState.getQuestItemsCount(4294) == 0L) {
                        questState.giveItems(4294, 9L);
                    } else {
                        questState.giveItems(4294, 10L);
                    }
                    questState.setCond(26);
                    questState.set("arrogant_quest", String.valueOf(17), true);
                    questState.set("arrogant_quest_ex_0", String.valueOf(17), true);
                    questState.set("arrogant_quest_ex_1", String.valueOf(0), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "magister_hanellin_q0348_51.htm";
                }
            } else if (string.equalsIgnoreCase("reply_15")) {
                if (n == 19) {
                    questState.setCond(29);
                    questState.set("arrogant_quest", String.valueOf(17), true);
                    questState.set("arrogant_quest_ex_0", String.valueOf(17), true);
                    questState.set("arrogant_quest_ex_1", String.valueOf(0), true);
                    questState.giveItems(4294, 10L);
                    string2 = "magister_hanellin_q0348_56.htm";
                }
            } else if (string.equalsIgnoreCase("reply_16")) {
                questState.unset("arrogant_quest");
                questState.unset("arrogant_quest_ex_0");
                questState.unset("arrogant_quest_ex_1");
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                string2 = "magister_hanellin_q0348_57.htm";
            } else if (string.equalsIgnoreCase("reply_17") && questState.getQuestItemsCount(4295) >= 10L && questState.getQuestItemsCount(4294) == 0L) {
                if (n == 17) {
                    questState.setCond(27);
                    questState.set("arrogant_quest", String.valueOf(18), true);
                    questState.set("arrogant_quest_ex_0", String.valueOf(18), true);
                    questState.set("arrogant_quest_ex_1", String.valueOf(0), true);
                    string2 = "magister_hanellin_q0348_58.htm";
                }
            } else if (string.equalsIgnoreCase("reply_18")) {
                string2 = "magister_hanellin_q0348_03.htm";
            } else if (string.equalsIgnoreCase("reply_19")) {
                string2 = "magister_hanellin_q0348_04.htm";
            }
        } else if (n3 == 31001) {
            if (string.equalsIgnoreCase("reply_1")) {
                string2 = "claudia_a_q0348_02.htm";
            }
        } else if (n3 == 30645) {
            if (string.equalsIgnoreCase("reply_1")) {
                string2 = "martian_q0348_02.htm";
            }
        } else if (n3 == 30144) {
            if (string.equalsIgnoreCase("reply_1")) {
                string2 = "harne_q0348_02.htm";
            }
        } else {
            if (string.equalsIgnoreCase("34803")) {
                if (npcInstance != null) {
                    npcInstance.deleteMe();
                }
                return null;
            }
            if (string.equalsIgnoreCase("34804")) {
                if (npcInstance != null) {
                    npcInstance.deleteMe();
                }
                return null;
            }
            if (string.equalsIgnoreCase("34801")) {
                if (npcInstance != null) {
                    npcInstance.deleteMe();
                }
                return null;
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getPlayer().getClassId().getId();
        int n2 = questState.getInt("arrogant_quest");
        int n3 = questState.getInt("arrogant_quest_ex_1");
        int n4 = questState.getInt("arrogant_quest_ex_0");
        int n5 = npcInstance.getNpcId();
        int n6 = questState.getState();
        switch (n6) {
            case 1: {
                if (n5 != 30864) break;
                if (questState.getPlayer().getLevel() < 60) {
                    if (questState.getQuestItemsCount(4395) == 0L && questState.getQuestItemsCount(4396) == 0L && questState.getQuestItemsCount(4394) == 0L) {
                        string = "magister_hanellin_q0348_01.htm";
                        questState.exitCurrentQuest(true);
                        break;
                    }
                    if (!(questState.getQuestItemsCount(4394) == 1L && questState.getQuestItemsCount(4395) == 0L && questState.getQuestItemsCount(4396) == 0L || questState.getQuestItemsCount(4394) == 0L && questState.getQuestItemsCount(4395) == 1L && questState.getQuestItemsCount(4396) == 0L) && (questState.getQuestItemsCount(4394) != 0L || questState.getQuestItemsCount(4395) != 0L || questState.getQuestItemsCount(4396) != 1L)) break;
                    string = "magister_hanellin_q0348_13.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                if (questState.getQuestItemsCount(4395) == 0L && questState.getQuestItemsCount(4396) == 0L && questState.getQuestItemsCount(4394) == 0L) {
                    string = "magister_hanellin_q0348_02.htm";
                    break;
                }
                if (!(questState.getQuestItemsCount(4394) == 1L && questState.getQuestItemsCount(4395) == 0L && questState.getQuestItemsCount(4396) == 0L || questState.getQuestItemsCount(4394) == 0L && questState.getQuestItemsCount(4395) == 1L && questState.getQuestItemsCount(4396) == 0L) && (questState.getQuestItemsCount(4394) != 0L || questState.getQuestItemsCount(4395) != 0L || questState.getQuestItemsCount(4396) != 1L)) break;
                string = "magister_hanellin_q0348_14.htm";
                break;
            }
            case 2: {
                if (n5 == 30864) {
                    if (n2 == 1000) {
                        questState.takeItems(4396, 1L);
                        questState.takeItems(4395, 1L);
                        questState.takeItems(4394, 1L);
                        break;
                    }
                    if (n2 == 1 && questState.getQuestItemsCount(4287) == 0L) {
                        string = "magister_hanellin_q0348_06.htm";
                        break;
                    }
                    if (n2 == 1 && questState.getQuestItemsCount(4287) > 0L || n2 == 2) {
                        if (questState.getQuestItemsCount(4287) > 0L) {
                            questState.takeItems(4287, 1L);
                        }
                        questState.set("arrogant_quest", String.valueOf(2), true);
                        string = "magister_hanellin_q0348_07.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(4394) == 1L && questState.getQuestItemsCount(4395) == 1L || questState.getQuestItemsCount(4394) == 1L && questState.getQuestItemsCount(4396) == 1L || questState.getQuestItemsCount(4395) == 1L && questState.getQuestItemsCount(4396) == 1L) {
                        string = "magister_hanellin_q0348_10.htm";
                        break;
                    }
                    if (n2 == 3 && questState.getQuestItemsCount(4394) == 0L && questState.getQuestItemsCount(4395) == 0L && questState.getQuestItemsCount(4396) == 0L) {
                        string = "magister_hanellin_q0348_11.htm";
                        break;
                    }
                    if (n2 == 3 && questState.getQuestItemsCount(4394) == 1L && questState.getQuestItemsCount(4395) == 0L && questState.getQuestItemsCount(4396) == 0L) {
                        questState.takeItems(4394, 1L);
                        questState.set("arrogant_quest", String.valueOf(4), true);
                        questState.set("arrogant_quest_ex_0", String.valueOf(4), true);
                        questState.set("arrogant_quest_ex_1", String.valueOf(1), true);
                        string = "magister_hanellin_q0348_12.htm";
                        break;
                    }
                    if (n2 == 3 && questState.getQuestItemsCount(4394) == 0L && questState.getQuestItemsCount(4395) == 1L && questState.getQuestItemsCount(4396) == 0L) {
                        questState.takeItems(4395, 1L);
                        questState.set("arrogant_quest", String.valueOf(4), true);
                        questState.set("arrogant_quest_ex_0", String.valueOf(4), true);
                        questState.set("arrogant_quest_ex_1", String.valueOf(2), true);
                        string = "magister_hanellin_q0348_12.htm";
                        break;
                    }
                    if (n2 == 3 && questState.getQuestItemsCount(4394) == 0L && questState.getQuestItemsCount(4395) == 0L && questState.getQuestItemsCount(4396) == 1L) {
                        questState.takeItems(4396, 1L);
                        questState.set("arrogant_quest", String.valueOf(4), true);
                        questState.set("arrogant_quest_ex_0", String.valueOf(4), true);
                        questState.set("arrogant_quest_ex_1", String.valueOf(3), true);
                        string = "magister_hanellin_q0348_12.htm";
                        break;
                    }
                    if (n2 == 4 && n3 == 1) {
                        questState.setCond(6);
                        questState.set("arrogant_quest", String.valueOf(5), true);
                        questState.giveItems(4288, 1L);
                        questState.playSound("ItemSound.quest_middle");
                        string = "magister_hanellin_q0348_18.htm";
                        break;
                    }
                    if (n2 == 4 && n3 == 2) {
                        questState.setCond(7);
                        questState.set("arrogant_quest", String.valueOf(5), true);
                        questState.giveItems(4289, 1L);
                        questState.playSound("ItemSound.quest_middle");
                        string = "magister_hanellin_q0348_19.htm";
                        break;
                    }
                    if (n2 == 4 && n3 == 3) {
                        questState.setCond(8);
                        questState.set("arrogant_quest", String.valueOf(5), true);
                        questState.giveItems(4290, 1L);
                        questState.playSound("ItemSound.quest_middle");
                        string = "magister_hanellin_q0348_20.htm";
                        break;
                    }
                    if (n2 == 5 && n3 % 10 == 0) {
                        string = "magister_hanellin_q0348_24.htm";
                        break;
                    }
                    if (n2 == 5 && n3 == 1) {
                        string = "magister_hanellin_q0348_25.htm";
                        break;
                    }
                    if (n2 == 5 && n3 == 2) {
                        string = "magister_hanellin_q0348_26.htm";
                        break;
                    }
                    if (n2 == 5 && n3 == 3) {
                        string = "magister_hanellin_q0348_27.htm";
                        break;
                    }
                    if (n2 == 6 || n2 == 7) {
                        string = "magister_hanellin_q0348_28.htm";
                        break;
                    }
                    if (n2 == 8 && questState.getQuestItemsCount(4397) == 1L && questState.getQuestItemsCount(4398) == 1L && questState.getQuestItemsCount(4399) == 1L) {
                        questState.setCond(22);
                        questState.set("arrogant_quest", String.valueOf(9), true);
                        questState.takeItems(4397, 1L);
                        questState.takeItems(4398, 1L);
                        questState.takeItems(4399, 1L);
                        questState.playSound("ItemSound.quest_middle");
                        string = "magister_hanellin_q0348_29.htm";
                        break;
                    }
                    if (n2 == 8 && n3 == 0 && (questState.getQuestItemsCount(4397) == 0L || questState.getQuestItemsCount(4398) == 0L || questState.getQuestItemsCount(4399) == 0L)) {
                        string = "magister_hanellin_q0348_29t.htm";
                        break;
                    }
                    if (n2 == 9 && (questState.getQuestItemsCount(1831) < 5L || questState.getQuestItemsCount(1061) == 0L)) {
                        string = "magister_hanellin_q0348_30.htm";
                        break;
                    }
                    if (n2 == 9 && n3 == 0 && questState.getQuestItemsCount(1831) >= 5L && questState.getQuestItemsCount(1061) > 0L) {
                        questState.set("arrogant_quest", String.valueOf(10), true);
                        questState.takeItems(1831, 5L);
                        questState.takeItems(1061, 1L);
                        questState.giveItems(4294, 1L);
                        string = "magister_hanellin_q0348_31.htm";
                        break;
                    }
                    if (n2 == 10 && questState.getQuestItemsCount(4294) == 1L) {
                        string = "magister_hanellin_q0348_32.htm";
                        break;
                    }
                    if (n2 == 11 && n3 > 0 && questState.getQuestItemsCount(4294) == 1L) {
                        int n7 = n3;
                        if (n7 == 1) {
                            questState.giveItems(57, 43000L);
                        } else if (n7 == 2) {
                            questState.giveItems(57, 4000L);
                        } else if (n7 == 3) {
                            questState.giveItems(57, 13000L);
                        }
                        questState.setCond(24);
                        questState.set("arrogant_quest", String.valueOf(12), true);
                        questState.set("arrogant_quest_ex_0", String.valueOf(12), true);
                        questState.set("arrogant_quest_ex_1", String.valueOf(100), true);
                        questState.playSound("ItemSound.quest_middle");
                        string = "magister_hanellin_q0348_34.htm";
                        break;
                    }
                    if (n2 == 11 && n3 == 0 && questState.getQuestItemsCount(4294) == 1L) {
                        string = "magister_hanellin_q0348_35.htm";
                        break;
                    }
                    if (n2 == 12 && questState.getQuestItemsCount(4294) == 1L) {
                        string = "magister_hanellin_q0348_38.htm";
                        break;
                    }
                    if (n2 == 13 && questState.getQuestItemsCount(4294) == 1L) {
                        string = "magister_hanellin_q0348_39.htm";
                        break;
                    }
                    if (n2 == 11 && n3 == 1 && questState.getQuestItemsCount(4398) == 1L && (questState.getQuestItemsCount(4397) == 0L || questState.getQuestItemsCount(4399) == 0L)) {
                        string = "magister_hanellin_q0348_40.htm";
                        break;
                    }
                    if (n2 == 8 && n3 == 2 && questState.getQuestItemsCount(4397) == 1L && (questState.getQuestItemsCount(4398) == 0L || questState.getQuestItemsCount(4399) == 0L)) {
                        string = "magister_hanellin_q0348_41.htm";
                        break;
                    }
                    if (n2 == 8 && n3 == 3 && questState.getQuestItemsCount(4397) == 1L && (questState.getQuestItemsCount(4398) == 0L || questState.getQuestItemsCount(4399) == 0L)) {
                        string = "magister_hanellin_q0348_42.htm";
                        break;
                    }
                    if (n2 == 8 && n3 == 1 && questState.getQuestItemsCount(4398) == 0L && questState.getQuestItemsCount(4400) == 0L) {
                        string = "magister_hanellin_q0348_43.htm";
                        break;
                    }
                    if (n2 == 8 && n3 == 2 && questState.getQuestItemsCount(4398) == 0L && questState.getQuestItemsCount(4400) == 0L) {
                        string = "magister_hanellin_q0348_44.htm";
                        break;
                    }
                    if (n2 == 8 && n3 == 3 && questState.getQuestItemsCount(4398) == 0L && questState.getQuestItemsCount(4400) == 0L) {
                        string = "magister_hanellin_q0348_45.htm";
                        break;
                    }
                    if (n2 == 9 && n3 != 0 && questState.getQuestItemsCount(1831) >= 5L && questState.getQuestItemsCount(1061) > 0L) {
                        questState.setCond(23);
                        questState.set("arrogant_quest", String.valueOf(10), true);
                        questState.giveItems(4400, 3L);
                        questState.takeItems(1831, 5L);
                        questState.takeItems(1061, 1L);
                        questState.playSound("ItemSound.quest_middle");
                        string = "magister_hanellin_q0348_46.htm";
                        break;
                    }
                    if ((n2 == 10 || n2 == 8) && n3 > 0 && questState.getQuestItemsCount(4400) > 1L) {
                        string = "magister_hanellin_q0348_47.htm";
                        break;
                    }
                    if ((n2 == 10 || n2 == 8) && n3 > 0 && questState.getQuestItemsCount(4397) == 0L && questState.getQuestItemsCount(4398) == 0L && questState.getQuestItemsCount(4399) == 0L && questState.getQuestItemsCount(4400) == 1L) {
                        questState.set("arrogant_quest", String.valueOf(10), true);
                        questState.giveItems(4294, 1L);
                        questState.takeItems(4400, 1L);
                        string = "magister_hanellin_q0348_48.htm";
                        break;
                    }
                    if (n2 == 14) {
                        if (n == 8 || n == 23 || n == 36 || n == 93 || n == 101 || n == 108) {
                            if (questState.getPlayer().getLevel() < 69) {
                                questState.giveItems(4109, 1L);
                                questState.giveItems(1888, 2L);
                            } else if (questState.getPlayer().getLevel() >= 69) {
                                questState.giveItems(4119, 1L);
                                questState.giveItems(1879, 2L);
                            } else if (n == 48 || n == 114) {
                                if (questState.getPlayer().getLevel() < 69) {
                                    questState.giveItems(4111, 1L);
                                    questState.giveItems(1888, 2L);
                                    questState.giveItems(1879, 1L);
                                } else if (questState.getPlayer().getLevel() >= 69) {
                                    questState.giveItems(4120, 1L);
                                    questState.giveItems(1874, 2L);
                                }
                            } else if (n == 5 || n == 6 || n == 17 || n == 20 || n == 21 || n == 33 || n == 34 || n == 43 || n == 90 || n == 91 || n == 98 || n == 99 || n == 100 || n == 106 || n == 107 || n == 112) {
                                if (questState.getPlayer().getLevel() < 69) {
                                    questState.giveItems(4107, 1L);
                                    questState.giveItems(1888, 2L);
                                } else if (questState.getPlayer().getLevel() >= 69) {
                                    questState.giveItems(4114, 1L);
                                    questState.giveItems(1874, 2L);
                                }
                            } else if (n == 9 || n == 24 || n == 37 || n == 92 || n == 102 || n == 109) {
                                if (questState.getPlayer().getLevel() < 69) {
                                    questState.giveItems(4112, 1L);
                                    questState.giveItems(1888, 2L);
                                } else if (questState.getPlayer().getLevel() >= 69) {
                                    questState.giveItems(4121, 1L);
                                    questState.giveItems(1881, 9L);
                                }
                            } else if (n == 2 || n == 16 || n == 30 || n == 88 || n == 97 || n == 105) {
                                if (questState.getPlayer().getLevel() < 69) {
                                    questState.giveItems(4105, 1L);
                                    questState.giveItems(1888, 2L);
                                    questState.giveItems(1879, 1L);
                                } else if (questState.getPlayer().getLevel() >= 69) {
                                    questState.giveItems(4117, 1L);
                                    questState.giveItems(1874, 2L);
                                }
                            } else if (n == 3 || n == 55 || n == 57 || n == 89 || n == 117 || n == 118) {
                                if (questState.getPlayer().getLevel() < 63) {
                                    questState.giveItems(4113, 1L);
                                    questState.giveItems(4042, 1L);
                                    questState.giveItems(1879, 1L);
                                } else if (questState.getPlayer().getLevel() >= 63) {
                                    questState.giveItems(4115, 1L);
                                    questState.giveItems(1874, 2L);
                                }
                            } else if (n == 12 || n == 27 || n == 51 || n == 94 || n == 103 || n == 115) {
                                if (questState.getPlayer().getLevel() < 63) {
                                    questState.giveItems(4106, 1L);
                                    questState.giveItems(1874, 4L);
                                    questState.giveItems(1881, 1L);
                                } else if (questState.getPlayer().getLevel() >= 63) {
                                    questState.giveItems(4118, 1L);
                                    questState.giveItems(1872, 5L);
                                }
                            } else if (n == 13 || n == 40 || n == 95 || n == 110) {
                                questState.giveItems(4110, 1L);
                                questState.giveItems(1888, 2L);
                                questState.giveItems(1872, 2L);
                            } else if (n == 13 || n == 40 || n == 95 || n == 110) {
                                questState.giveItems(4110, 1L);
                                questState.giveItems(1888, 2L);
                                questState.giveItems(1872, 2L);
                            } else if (n == 46 || n == 113) {
                                questState.giveItems(4104, 1L);
                                questState.giveItems(1887, 2L);
                                questState.giveItems(1888, 2L);
                            } else if (n == 28 || n == 41 || n == 104 || n == 111) {
                                questState.giveItems(4114, 1L);
                                questState.giveItems(4042, 1L);
                            } else if (n == 52 || n == 116) {
                                questState.giveItems(4108, 1L);
                                questState.giveItems(1874, 1L);
                                questState.giveItems(1887, 1L);
                            } else if (n == 14 || n == 96) {
                                questState.giveItems(4117, 1L);
                                questState.giveItems(4042, 1L);
                            } else {
                                questState.giveItems(57, 49000L);
                            }
                        }
                        questState.set("arrogant_quest", String.valueOf(15), true);
                        string = "magister_hanellin_q0348_49.htm";
                        break;
                    }
                    if (n2 == 15) {
                        string = "magister_hanellin_q0348_50.htm";
                        break;
                    }
                    if (n2 == 16) {
                        if (questState.getQuestItemsCount(4295) > 0L && questState.getQuestItemsCount(4294) == 0L) {
                            questState.giveItems(4294, 9L);
                        } else {
                            questState.giveItems(4294, 10L);
                        }
                        questState.setCond(26);
                        questState.set("arrogant_quest", String.valueOf(17), true);
                        questState.set("arrogant_quest_ex_0", String.valueOf(17), true);
                        questState.set("arrogant_quest_ex_1", String.valueOf(0), true);
                        questState.playSound("ItemSound.quest_middle");
                        string = "magister_hanellin_q0348_51.htm";
                        break;
                    }
                    if (n2 == 17 && questState.getQuestItemsCount(4294) > 0L) {
                        string = "magister_hanellin_q0348_52.htm";
                        break;
                    }
                    if (n2 == 17 && questState.getQuestItemsCount(4295) >= 10L && questState.getQuestItemsCount(4294) == 0L) {
                        string = "magister_hanellin_q0348_53.htm";
                        break;
                    }
                    if (n2 == 17 && questState.getQuestItemsCount(4295) < 10L && questState.getQuestItemsCount(4294) == 0L) {
                        questState.giveItems(57, questState.getQuestItemsCount(4295) * 1000L + 4000L);
                        questState.takeItems(4295, -1L);
                        questState.unset("arrogant_quest_ex_0");
                        questState.unset("arrogant_quest_ex_1");
                        questState.unset("arrogant_quest");
                        questState.playSound("ItemSound.quest_finish");
                        questState.exitCurrentQuest(true);
                        string = "magister_hanellin_q0348_54.htm";
                        break;
                    }
                    if (n2 == 19) {
                        string = "magister_hanellin_q0348_55.htm";
                        break;
                    }
                    if (n2 == 18 && n3 % 10 < 7) {
                        int n8 = 0;
                        int n9 = 0;
                        int n10 = n3 % 10;
                        if (n10 >= 4) {
                            n8 += 6;
                            n10 -= 4;
                            ++n9;
                        }
                        if (n10 >= 2) {
                            n10 -= 2;
                            ++n8;
                            ++n9;
                        }
                        if (n10 >= 1) {
                            n8 += 3;
                            ++n9;
                            --n10;
                        }
                        if (n10 == 0 && questState.getQuestItemsCount(4295) + (long)n8 >= 10L) {
                            string = "magister_hanellin_q0348_59.htm";
                            break;
                        }
                        if (n10 != 0 || questState.getQuestItemsCount(4295) + (long)n8 >= 10L) break;
                        string = "magister_hanellin_q0348_61.htm";
                        if (n9 == 2) {
                            questState.giveItems(57, 24000L);
                        } else if (n9 == 1) {
                            questState.giveItems(57, 12000L);
                        }
                        questState.unset("arrogant_quest_ex_0");
                        questState.unset("arrogant_quest_ex_1");
                        questState.unset("arrogant_quest");
                        questState.playSound("ItemSound.quest_finish");
                        questState.exitCurrentQuest(true);
                        break;
                    }
                    if (n2 != 18 || n3 % 10 != 7) break;
                    questState.setCond(28);
                    string = "magister_hanellin_q0348_60.htm";
                    questState.playSound("ItemSound.quest_middle");
                    if (n == 8 || n == 23 || n == 36 || n == 93 || n == 101 || n == 108) {
                        if (questState.getPlayer().getLevel() < 69) {
                            questState.giveItems(4109, 1L);
                            questState.giveItems(1888, 2L);
                        } else if (questState.getPlayer().getLevel() >= 69) {
                            questState.giveItems(4119, 1L);
                            questState.giveItems(1879, 2L);
                        }
                    } else if (n == 48 || n == 114) {
                        if (questState.getPlayer().getLevel() < 69) {
                            questState.giveItems(4111, 1L);
                            questState.giveItems(1888, 2L);
                            questState.giveItems(1879, 1L);
                        } else if (questState.getPlayer().getLevel() >= 69) {
                            questState.giveItems(4120, 1L);
                            questState.giveItems(1874, 2L);
                        }
                    } else if (n == 5 || n == 6 || n == 17 || n == 20 || n == 21 || n == 33 || n == 34 || n == 43 || n == 90 || n == 91 || n == 98 || n == 99 || n == 100 || n == 106 || n == 107 || n == 112) {
                        if (questState.getPlayer().getLevel() < 69) {
                            questState.giveItems(4107, 1L);
                            questState.giveItems(1888, 2L);
                        } else if (questState.getPlayer().getLevel() >= 69) {
                            questState.giveItems(4114, 1L);
                            questState.giveItems(1874, 2L);
                        }
                    } else if (n == 9 || n == 24 || n == 37 || n == 92 || n == 102 || n == 109) {
                        if (questState.getPlayer().getLevel() < 69) {
                            questState.giveItems(4112, 1L);
                            questState.giveItems(1888, 2L);
                        } else if (questState.getPlayer().getLevel() >= 69) {
                            questState.giveItems(4121, 1L);
                            questState.giveItems(1881, 9L);
                        }
                    } else if (n == 2 || n == 16 || n == 30 || n == 88 || n == 97 || n == 105) {
                        if (questState.getPlayer().getLevel() < 69) {
                            questState.giveItems(4105, 1L);
                            questState.giveItems(1888, 2L);
                            questState.giveItems(1879, 1L);
                        } else if (questState.getPlayer().getLevel() >= 69) {
                            questState.giveItems(4117, 1L);
                            questState.giveItems(1874, 2L);
                        }
                    } else if (n == 3 || n == 55 || n == 57 || n == 89 || n == 117 || n == 118) {
                        if (questState.getPlayer().getLevel() < 63) {
                            questState.giveItems(4113, 1L);
                            questState.giveItems(4042, 1L);
                            questState.giveItems(1879, 1L);
                        } else if (questState.getPlayer().getLevel() >= 63) {
                            questState.giveItems(4115, 1L);
                            questState.giveItems(1874, 2L);
                        }
                    } else if (n == 12 || n == 27 || n == 51 || n == 94 || n == 103 || n == 115) {
                        if (questState.getPlayer().getLevel() < 63) {
                            questState.giveItems(4106, 1L);
                            questState.giveItems(1874, 4L);
                            questState.giveItems(1881, 1L);
                        } else if (questState.getPlayer().getLevel() >= 63) {
                            questState.giveItems(4118, 1L);
                            questState.giveItems(1872, 5L);
                        }
                    } else if (n == 13 || n == 40 || n == 95 || n == 110) {
                        questState.giveItems(4110, 1L);
                        questState.giveItems(1888, 2L);
                        questState.giveItems(1872, 2L);
                    } else if (n == 13 || n == 40 || n == 95 || n == 110) {
                        questState.giveItems(4110, 1L);
                        questState.giveItems(1888, 2L);
                        questState.giveItems(1872, 2L);
                    } else if (n == 46 || n == 113) {
                        questState.giveItems(4104, 1L);
                        questState.giveItems(1887, 2L);
                        questState.giveItems(1888, 2L);
                    } else if (n == 28 || n == 41 || n == 104 || n == 111) {
                        questState.giveItems(4114, 1L);
                        questState.giveItems(4042, 1L);
                    } else if (n == 52 || n == 116) {
                        questState.giveItems(4108, 1L);
                        questState.giveItems(1874, 1L);
                        questState.giveItems(1887, 1L);
                    } else if (n == 14 || n == 96) {
                        questState.giveItems(4117, 1L);
                        questState.giveItems(4042, 1L);
                    } else {
                        questState.giveItems(57, 49000L);
                    }
                    questState.set("arrogant_quest", String.valueOf(19), true);
                    break;
                }
                if (n5 == 31001) {
                    if (questState.getQuestItemsCount(4289) > 0L) {
                        int n11 = n3;
                        if ((n11 += 100) % 10 == 0) {
                            questState.getPlayer().addRadarWithMap(181472, 7158, -2725);
                        } else {
                            questState.setCond(9);
                            questState.playSound("ItemSound.quest_middle");
                        }
                        questState.set("arrogant_quest_ex_1", String.valueOf(n11), true);
                        questState.takeItems(4289, 1L);
                        string = "claudia_a_q0348_01.htm";
                        break;
                    }
                    if (n2 < 8 && n3 % 1000 / 100 == 1 && questState.getQuestItemsCount(4292) == 0L) {
                        if (n3 % 10 == 0) {
                            questState.getPlayer().addRadarWithMap(181472, 7158, -2725);
                        }
                        string = "claudia_a_q0348_03.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(4292) > 0L) {
                        if (n3 % 10 == 0) {
                            questState.getPlayer().addRadarWithMap(181472, 7158, -2725);
                        }
                        string = "claudia_a_q0348_04.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(4397) <= 0L) break;
                    string = "claudia_a_q0348_05.htm";
                    break;
                }
                if (n5 == 30978) {
                    if (n2 < 8 && n3 % 1000 / 100 == 1 && questState.getQuestItemsCount(4292) == 0L) {
                        string = "holy_ark_2_q0348_01.htm";
                        int n12 = n3;
                        if (n12 % 10 != 0) {
                            questState.setCond(10);
                            questState.playSound("ItemSound.quest_middle");
                        }
                        questState.playSound("ItemSound.quest_middle");
                        if (questState.isRunningQuestTimer("34804")) break;
                        NpcInstance npcInstance2 = questState.addSpawn(27182, questState.getPlayer().getX(), questState.getPlayer().getY(), questState.getPlayer().getZ());
                        Functions.npcSayCustomMessage(npcInstance2, "NpcString.THAT_DOESNT_BELONG_TO_YOU", new Object[0]);
                        questState.startQuestTimer("34804", 600000L, npcInstance2);
                        break;
                    }
                    if (questState.getQuestItemsCount(4292) > 0L) {
                        questState.giveItems(4397, 1L);
                        questState.takeItems(4292, 1L);
                        questState.getPlayer().addRadar(181472, 7158, -2725);
                        if (n3 % 10 == 0 && questState.getQuestItemsCount(4398) > 0L && questState.getQuestItemsCount(4399) > 0L) {
                            questState.setCond(21);
                            questState.playSound("ItemSound.quest_middle");
                        } else if (n3 % 10 != 0) {
                            questState.setCond(12);
                            questState.playSound("ItemSound.quest_middle");
                        }
                        questState.set("arrogant_quest_ex_1", String.valueOf(n3 - 200), true);
                        if ((n3 - 200) % 1000 / 100 == 0) {
                            questState.set("arrogant_quest", String.valueOf(n2 + 1), true);
                            questState.set("arrogant_quest_ex_0", String.valueOf(n4 + 1), true);
                        }
                        if ((n3 - 200) % 10 == 2) {
                            questState.set("arrogant_quest", String.valueOf(8), true);
                            questState.set("arrogant_quest_ex_0", String.valueOf(8), true);
                        }
                        string = "holy_ark_2_q0348_02.htm";
                        break;
                    }
                    if (n2 > 8 || n3 % 1000 / 100 != 0 || questState.getQuestItemsCount(4292) != 0L || questState.getQuestItemsCount(4397) <= 0L) break;
                    string = "holy_ark_2_q0348_03.htm";
                    break;
                }
                if (n5 == 30645) {
                    if (questState.getQuestItemsCount(4290) > 0L) {
                        int n13 = n3;
                        n13 += 1000;
                        if (n3 % 10 == 0) {
                            questState.getPlayer().addRadar(50693, 158674, 376);
                        } else {
                            questState.setCond(13);
                            questState.playSound("ItemSound.quest_middle");
                        }
                        questState.set("arrogant_quest_ex_1", String.valueOf(n13), true);
                        questState.takeItems(4290, 1L);
                        string = "martian_q0348_01.htm";
                        break;
                    }
                    if (n2 < 8 && n3 % 10000 / 1000 == 1 && questState.getQuestItemsCount(4293) == 0L) {
                        if (n3 % 10 == 0) {
                            questState.getPlayer().addRadarWithMap(50693, 158674, 376);
                        }
                        string = "martian_q0348_03.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(4293) > 0L) {
                        if (n3 % 10 == 0) {
                            questState.getPlayer().addRadarWithMap(50693, 158674, 376);
                        }
                        string = "martian_q0348_04.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(4399) <= 0L) break;
                    string = "martian_q0348_05.htm";
                    break;
                }
                if (n5 == 30979) {
                    if (n2 < 8 && n3 % 10000 / 1000 == 1 && questState.getQuestItemsCount(4293) == 0L) {
                        string = "holy_ark_3_q0348_01.htm";
                        int n14 = n3;
                        if (n14 % 10 != 0) {
                            questState.setCond(14);
                            questState.playSound("ItemSound.quest_middle");
                        }
                        questState.playSound("ItemSound.quest_middle");
                        if (questState.isRunningQuestTimer("34803")) break;
                        NpcInstance npcInstance3 = questState.addSpawn(27183, questState.getPlayer().getX(), questState.getPlayer().getY(), questState.getPlayer().getZ());
                        Functions.npcSayCustomMessage(npcInstance3, "NpcString.GET_OUT_OF_MY_SIGHT_YOU_INFIDELS", new Object[0]);
                        questState.startQuestTimer("34803", 600000L, npcInstance3);
                        break;
                    }
                    if (questState.getQuestItemsCount(4293) > 0L) {
                        questState.giveItems(4399, 1L);
                        questState.takeItems(4293, 1L);
                        questState.getPlayer().addRadar(50693, 158674, 376);
                        if (n3 % 10 == 0 && questState.getQuestItemsCount(4398) > 0L && questState.getQuestItemsCount(4397) > 0L) {
                            questState.setCond(21);
                            questState.playSound("ItemSound.quest_middle");
                        } else if (n3 % 10 != 0) {
                            questState.setCond(16);
                            questState.playSound("ItemSound.quest_middle");
                        }
                        questState.set("arrogant_quest_ex_1", String.valueOf(n3 - 2000), true);
                        if ((n3 - 2000) % 10000 / 1000 == 0) {
                            questState.set("arrogant_quest", String.valueOf(n2 + 1), true);
                            questState.set("arrogant_quest_ex_0", String.valueOf(n4 + 1), true);
                        }
                        if ((n3 - 2000) % 10 == 3) {
                            questState.set("arrogant_quest", String.valueOf(8), true);
                            questState.set("arrogant_quest_ex_0", String.valueOf(8), true);
                        }
                        string = "holy_ark_3_q0348_02.htm";
                        break;
                    }
                    if (n2 > 8 || n3 % 10000 / 1000 != 0 || questState.getQuestItemsCount(4293) != 0L || questState.getQuestItemsCount(4399) <= 0L) break;
                    string = "holy_ark_3_q0348_03.htm";
                    break;
                }
                if (n5 == 30144) {
                    if (questState.getQuestItemsCount(4288) > 0L) {
                        int n15 = n3;
                        n15 += 10;
                        if (questState.getQuestItemsCount(4397) == 0L) {
                            string = "harne_q0348_07.htm";
                            return "harne_q0348_07.htm";
                        }
                        if (questState.getQuestItemsCount(4399) == 0L) {
                            string = "harne_q0348_06.htm";
                            return "harne_q0348_06.htm";
                        }
                        if (n3 % 10 == 0) {
                            questState.getPlayer().addRadarWithMap(2908, 44128, -2712);
                        } else {
                            questState.setCond(17);
                            questState.playSound("ItemSound.quest_middle");
                        }
                        questState.set("arrogant_quest_ex_1", String.valueOf(n15), true);
                        questState.takeItems(4288, 1L);
                        string = "harne_q0348_01.htm";
                        break;
                    }
                    if (n2 < 8 && n3 % 100 / 10 == 1 && questState.getQuestItemsCount(4291) == 0L) {
                        if (n3 % 10 == 0) {
                            questState.getPlayer().addRadarWithMap(2908, 44128, -2712);
                        }
                        string = "harne_q0348_03.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(4291) > 0L) {
                        if (n3 % 10 == 0) {
                            questState.getPlayer().addRadarWithMap(2908, 44128, -2712);
                        }
                        string = "harne_q0348_04.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(4398) <= 0L) break;
                    string = "harne_q0348_05.htm";
                    break;
                }
                if (n5 == 30980) {
                    if (n2 < 8 && n3 % 100 / 10 == 1 && questState.getQuestItemsCount(4291) == 0L && questState.getQuestItemsCount(4398) == 0L) {
                        string = "ark_guardians_corpse_q0348_01.htm";
                        int n16 = n3;
                        questState.getPlayer().addRadar(2908, 44128, -2712);
                        if (n16 % 10 != 0) {
                            questState.setCond(18);
                            questState.playSound("ItemSound.quest_middle");
                        }
                        questState.playSound("ItemSound.quest_middle");
                        if (questState.isRunningQuestTimer("34801")) break;
                        NpcInstance npcInstance4 = questState.addSpawn(27184, questState.getPlayer().getX(), questState.getPlayer().getY(), questState.getPlayer().getZ());
                        Functions.npcSayCustomMessage(npcInstance4, "NpcString.I_HAVE_THE_KEY", new Object[0]);
                        questState.startQuestTimer("34801", 600000L, npcInstance4);
                        break;
                    }
                    if (n2 < 8 && n3 % 100 / 10 == 2 && questState.getQuestItemsCount(4291) == 0L && questState.getQuestItemsCount(4398) == 0L) {
                        questState.giveItems(4291, 1L);
                        questState.getPlayer().addRadarWithMap(-418, 44174, -3568);
                        string = "ark_guardians_corpse_q0348_02.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(4291) <= 0L && questState.getQuestItemsCount(4398) <= 0L) break;
                    string = "ark_guardians_corpse_q0348_03.htm";
                    break;
                }
                if (n5 == 30977) {
                    if (questState.getQuestItemsCount(4291) == 1L) {
                        questState.giveItems(4398, 1L);
                        questState.getPlayer().addRadar(-418, 44174, -3568);
                        if (n3 % 10 == 0 && questState.getQuestItemsCount(4397) > 0L && questState.getQuestItemsCount(4399) > 0L) {
                            questState.setCond(21);
                            questState.playSound("ItemSound.quest_middle");
                        } else if (n3 % 10 != 0) {
                            questState.setCond(20);
                            questState.playSound("ItemSound.quest_middle");
                        }
                        questState.takeItems(4291, 1L);
                        questState.set("arrogant_quest_ex_1", String.valueOf(n3 - 20), true);
                        if ((n3 - 20) % 100 / 10 == 0) {
                            questState.set("arrogant_quest", String.valueOf(n2 + 1), true);
                            questState.set("arrogant_quest_ex_0", String.valueOf(n4 + 1), true);
                        }
                        if ((n3 - 20) % 10 == 1) {
                            questState.set("arrogant_quest_ex_0", String.valueOf(8), true);
                            questState.set("arrogant_quest", String.valueOf(8), true);
                        }
                        string = "holy_ark_1_q0348_02.htm";
                        break;
                    }
                    if (n2 <= 8 && n3 % 100 / 10 == 0 && questState.getQuestItemsCount(4291) == 0L && questState.getQuestItemsCount(4398) > 0L) {
                        string = "holy_ark_1_q0348_03.htm";
                        break;
                    }
                    if (n2 >= 8 || n3 % 100 / 10 != 1 || questState.getQuestItemsCount(4291) != 0L || questState.getQuestItemsCount(4398) != 0L) break;
                    string = "holy_ark_1_q0348_04.htm";
                    break;
                }
                if (n5 == 30969) {
                    if (n4 == 18 && n3 % 8 < 4) {
                        if (questState.getQuestItemsCount(4295) >= 6L) {
                            questState.takeItems(4295, 6L);
                            int n17 = n3 + 4;
                            questState.set("arrogant_quest_ex_1", String.valueOf(n17), true);
                            string = "iason_haine_q0348_01.htm";
                            break;
                        }
                        if (questState.getQuestItemsCount(4295) >= 6L) break;
                        string = "iason_haine_q0348_03.htm";
                        break;
                    }
                    if (n4 != 18 || n3 % 8 < 4) break;
                    string = "iason_haine_q0348_02.htm";
                    break;
                }
                if (n5 == 30832) {
                    if (n4 == 18 && n3 % 4 < 2) {
                        if (questState.getQuestItemsCount(4295) >= 1L) {
                            questState.takeItems(4295, 1L);
                            int n18 = n3 + 2;
                            questState.set("arrogant_quest_ex_1", String.valueOf(n18), true);
                            string = "hardin_q0348_01.htm";
                            break;
                        }
                        if (questState.getQuestItemsCount(4295) >= 3L) break;
                        string = "hardin_q0348_03.htm";
                        break;
                    }
                    if (n4 != 18 || n3 % 4 < 2) break;
                    string = "hardin_q0348_02.htm";
                    break;
                }
                if (n5 != 30760) break;
                if (n4 == 18 && n3 % 2 == 0) {
                    if (questState.getQuestItemsCount(4295) >= 3L) {
                        questState.takeItems(4295, 3L);
                        int n19 = n3 + 1;
                        questState.set("arrogant_quest_ex_1", String.valueOf(n19), true);
                        string = "sir_gustaf_athebaldt_q0348_01.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(4295) >= 3L) break;
                    string = "sir_gustaf_athebaldt_q0348_03.htm";
                    break;
                }
                if (n4 != 18 || n3 % 2 != 1) break;
                string = "sir_gustaf_athebaldt_q0348_02.htm";
            }
        }
        return string;
    }

    @Override
    public String onAttack(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getInt("arrogant_quest_ex_1");
        int n2 = questState.getInt("arrogant_quest_ex_0");
        int n3 = npcInstance.getNpcId();
        if (n3 == 27182 && npcInstance != null) {
            if (Rnd.chance(2)) {
                Functions.npcSayCustomMessage(npcInstance, "NpcString.SORRY_ABOUT_THIS_BUT_I_MUST_KILL_YOU_NOW", new Object[0]);
            }
        } else if (n3 == 27183 && npcInstance != null) {
            if (Rnd.chance(2)) {
                Functions.npcSayCustomMessage(npcInstance, "NpcString.I_SHALL_DRENCH_THIS_MOUNTAIN_WITH_YOUR_BLOOD", new Object[0]);
            }
        } else if (n3 == 27184 && npcInstance != null) {
            if (n2 < 8 && n % 100 / 10 == 1 && questState.getQuestItemsCount(4291) == 0L && questState.getQuestItemsCount(4398) == 0L) {
                if (npcInstance.getCurrentHp() < (double)npcInstance.getMaxHp() * 0.5) {
                    int n4 = n;
                    questState.set("arrogant_quest_ex_1", String.valueOf(n4 += 10), true);
                    if (n4 % 10 == 0) {
                        questState.getPlayer().addRadarWithMap(-2908, 44128, -2712);
                    } else {
                        questState.setCond(19);
                        questState.playSound("ItemSound.quest_middle");
                    }
                    Functions.npcSayCustomMessage(npcInstance, "NpcString.HA_THAT_WAS_FUN_IF_YOU_WISH_TO_FIND_THE_KEY_SEARCH_THE_CORPSE", new Object[0]);
                    if (npcInstance != null) {
                        npcInstance.deleteMe();
                    }
                }
            } else if (n2 < 8 && n % 100 / 10 == 2 && questState.getQuestItemsCount(4291) == 0L && questState.getQuestItemsCount(4398) == 0L) {
                Functions.npcSayCustomMessage(npcInstance, "NpcString.WE_DONT_HAVE_ANY_FURTHER_BUSINESS_TO_DISCUSS", new Object[0]);
                if (npcInstance != null) {
                    npcInstance.deleteMe();
                }
            } else if (questState.getQuestItemsCount(4291) >= 1L || questState.getQuestItemsCount(4398) >= 1L) {
                Functions.npcSayCustomMessage(npcInstance, "NpcString.WE_DONT_HAVE_ANY_FURTHER_BUSINESS_TO_DISCUSS", new Object[0]);
                if (npcInstance != null) {
                    npcInstance.deleteMe();
                }
            }
        } else if (n3 == 20828) {
            if (npcInstance.getCurrentHp() < (double)npcInstance.getMaxHp() * 0.9) {
                if (n2 == 12 && questState.getQuestItemsCount(4294) > 0L) {
                    int n5 = n;
                    questState.set("arrogant_quest_ex_1", String.valueOf(n5 += 60), true);
                    if (n + 60 > 80000) {
                        questState.giveItems(4295, 1L);
                        questState.takeItems(4294, 1L);
                        questState.unset("arrogant_quest");
                        questState.unset("arrogant_quest_ex_0");
                        questState.unset("arrogant_quest_ex_1");
                        questState.playSound("ItemSound.quest_finish");
                        questState.exitCurrentQuest(true);
                    }
                } else if (n2 == 13 && questState.getQuestItemsCount(4294) > 0L) {
                    int n6 = n;
                    questState.set("arrogant_quest_ex_1", String.valueOf(n6 += 60), true);
                    if (n + 60 > 100000) {
                        questState.giveItems(4295, 1L);
                        questState.takeItems(4294, 1L);
                        questState.set("arrogant_quest", String.valueOf(14), true);
                        questState.set("arrogant_quest_ex_0", String.valueOf(14), true);
                        questState.playSound("ItemSound.quest_middle");
                    }
                }
            }
        } else if (n3 == 20829 && npcInstance.getCurrentHp() < (double)npcInstance.getMaxHp() * 0.9) {
            if (n2 == 12 && questState.getQuestItemsCount(4294) > 0L) {
                int n7 = n;
                questState.set("arrogant_quest_ex_1", String.valueOf(n7 += 70), true);
                if (n + 70 > 80000) {
                    questState.giveItems(4295, 1L);
                    questState.takeItems(4294, 1L);
                    questState.unset("arrogant_quest");
                    questState.unset("arrogant_quest_ex_0");
                    questState.unset("arrogant_quest_ex_1");
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 13 && questState.getQuestItemsCount(4294) > 0L) {
                int n8 = n;
                questState.set("arrogant_quest_ex_1", String.valueOf(n8 += 70), true);
                if (n + 70 > 100000) {
                    questState.giveItems(4295, 1L);
                    questState.takeItems(4294, 1L);
                    questState.set("arrogant_quest", String.valueOf(14), true);
                    questState.set("arrogant_quest_ex_0", String.valueOf(14), true);
                    questState.playSound("ItemSound.quest_middle");
                }
            }
        }
        return null;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getInt("arrogant_quest");
        int n2 = questState.getInt("arrogant_quest_ex_1");
        int n3 = questState.getInt("arrogant_quest_ex_0");
        int n4 = npcInstance.getNpcId();
        if (n4 == 27182) {
            if (n3 < 8 && n2 % 1000 / 100 == 1 && questState.getQuestItemsCount(4292) == 0L && questState.getQuestItemsCount(4397) == 0L) {
                int n5 = n2;
                questState.set("arrogant_quest_ex_1", String.valueOf(n5 += 100), true);
                if (n5 % 10 != 0) {
                    questState.setCond(11);
                }
                questState.giveItems(4292, 1L);
                questState.playSound("ItemSound.quest_itemget");
                Functions.npcSayCustomMessage(npcInstance, "NpcString.YOU_FOOLS_WILL_GET_WHATS_COMING_TO_YOU", new Object[0]);
            }
        } else if (n4 == 27183) {
            if (n3 < 8 && n2 % 10000 / 1000 == 1 && questState.getQuestItemsCount(4293) == 0L && questState.getQuestItemsCount(4399) == 0L) {
                int n6 = n2;
                if ((n6 += 1000) % 10 != 0) {
                    questState.setCond(15);
                }
                questState.set("arrogant_quest_ex_1", String.valueOf(n6), true);
                questState.giveItems(4293, 1L);
                questState.playSound("ItemSound.quest_itemget");
                Functions.npcSayCustomMessage(npcInstance, "NpcString.YOU_GUYS_WOULDNT_KNOW", new Object[0]);
            }
        } else if (n4 == 20828) {
            if ((n3 == 12 || n3 == 13) && questState.getQuestItemsCount(4294) > 0L) {
                if (n3 == 12 && questState.getQuestItemsCount(4294) > 0L) {
                    int n7 = n2;
                    questState.set("arrogant_quest_ex_1", String.valueOf(n7 += 600), true);
                    if (n2 + 600 > 80000) {
                        questState.giveItems(4295, 1L);
                        questState.takeItems(4294, 1L);
                        questState.unset("arrogant_quest");
                        questState.unset("arrogant_quest_ex_0");
                        questState.unset("arrogant_quest_ex_1");
                        questState.playSound("ItemSound.quest_finish");
                        questState.exitCurrentQuest(true);
                    }
                } else if (n3 == 13 && questState.getQuestItemsCount(4294) > 0L) {
                    int n8 = n2;
                    questState.set("arrogant_quest_ex_1", String.valueOf(n8 += 600), true);
                    if (n2 + 600 > 100000) {
                        questState.giveItems(4295, 1L);
                        questState.takeItems(4294, 1L);
                        questState.set("arrogant_quest", String.valueOf(14), true);
                        questState.set("arrogant_quest_ex_0", String.valueOf(14), true);
                        questState.playSound("ItemSound.quest_middle");
                    }
                }
            }
        } else if (n4 == 20829) {
            if ((n3 == 12 || n3 == 13) && questState.getQuestItemsCount(4294) > 0L) {
                if (n3 == 12 && questState.getQuestItemsCount(4294) > 0L) {
                    int n9 = n2;
                    questState.set("arrogant_quest_ex_1", String.valueOf(n9 += 700), true);
                    if (n2 + 700 > 80000) {
                        questState.giveItems(4295, 1L);
                        questState.takeItems(4294, 1L);
                        questState.unset("arrogant_quest");
                        questState.unset("arrogant_quest_ex_0");
                        questState.unset("arrogant_quest_ex_1");
                        questState.playSound("ItemSound.quest_finish");
                        questState.exitCurrentQuest(true);
                    }
                } else if (n3 == 13 && questState.getQuestItemsCount(4294) > 0L) {
                    int n10 = n2;
                    questState.set("arrogant_quest_ex_1", String.valueOf(n10 += 700), true);
                    if (n2 + 700 > 100000) {
                        questState.giveItems(4295, 1L);
                        questState.takeItems(4294, 1L);
                        questState.set("arrogant_quest", String.valueOf(14), true);
                        questState.set("arrogant_quest_ex_0", String.valueOf(14), true);
                        questState.playSound("ItemSound.quest_middle");
                    }
                }
            }
        } else if (n4 == 20658 || n4 == 20657) {
            if (n == 1 && questState.getQuestItemsCount(4287) == 0L && Rnd.chance(20)) {
                questState.giveItems(4287, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if ((n4 == 20830 || n4 == 20831 || n4 == 20860) && n3 == 17 && questState.getQuestItemsCount(4294) > 0L) {
            int n11 = n2;
            n11 = n11 + Rnd.get(100) + 100;
            questState.set("arrogant_quest_ex_1", String.valueOf(n11), true);
            if (n2 + n11 > 750) {
                questState.giveItems(4295, 1L);
                questState.takeItems(4294, 1L);
                questState.playSound("ItemSound.quest_middle");
                questState.set("arrogant_quest_ex_1", String.valueOf(0), true);
            }
        }
        return null;
    }
}

