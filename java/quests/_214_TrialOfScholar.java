/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _214_TrialOfScholar
extends Quest
implements ScriptFile {
    private static final int aLi = 30070;
    private static final int aLj = 30071;
    private static final int aLk = 30103;
    private static final int aLl = 30111;
    private static final int aLm = 30115;
    private static final int aLn = 30230;
    private static final int Raut = 30316;
    private static final int aLo = 30458;
    private static final int aLp = 30461;
    private static final int aLq = 30608;
    private static final int aLr = 30609;
    private static final int aLs = 30610;
    private static final int aLt = 30611;
    private static final int aLu = 30612;
    private static final int aLv = 20068;
    private static final int aLd = 20158;
    private static final int aLw = 20201;
    private static final int aLx = 20235;
    private static final int aLy = 20269;
    private static final int aLz = 20552;
    private static final int aLA = 20554;
    private static final int aLB = 20567;
    private static final int aLC = 20580;
    private static final int aLD = 7562;
    private static final int aLE = 2674;
    private static final int aLF = 2675;
    private static final int aLG = 2676;
    private static final int aLH = 2677;
    private static final int aLI = 2678;
    private static final int aLJ = 2679;
    private static final int aLK = 2680;
    private static final int aLL = 2681;
    private static final int aLM = 2682;
    private static final int aLN = 2683;
    private static final int aLO = 2684;
    private static final int aLP = 2685;
    private static final int aLQ = 2686;
    private static final int aLR = 2687;
    private static final int aLS = 2688;
    private static final int aLT = 2689;
    private static final int aLU = 2690;
    private static final int aLV = 2691;
    private static final int aLW = 2692;
    private static final int aLX = 2693;
    private static final int aLY = 2694;
    private static final int aLZ = 2695;
    private static final int aMa = 2696;
    private static final int aMb = 2697;
    private static final int aMc = 2698;
    private static final int aMd = 2699;
    private static final int aMe = 2700;
    private static final int aMf = 2701;
    private static final int aMg = 2702;
    private static final int aMh = 2703;
    private static final int aMi = 2704;
    private static final int aMj = 2705;
    private static final int aMk = 2706;
    private static final int aMl = 2707;
    private static final int aMm = 2708;
    private static final int aMn = 2709;
    private static final int aMo = 2710;
    private static final int aMp = 2711;
    private static final int aMq = 2713;
    private static final int aMr = 2714;
    private static final int aMs = 2715;
    private static final int aMt = 2716;
    private static final int aMu = 2717;
    private static final int aMv = 2718;
    private static final int aMw = 2719;
    private static final int aMx = 2720;

    public _214_TrialOfScholar() {
        super(0);
        this.addStartNpc(30461);
        this.addTalkId(30070);
        this.addTalkId(30071);
        this.addTalkId(30103);
        this.addTalkId(30111);
        this.addTalkId(30115);
        this.addTalkId(30230);
        this.addTalkId(30316);
        this.addTalkId(30458);
        this.addTalkId(30608);
        this.addTalkId(30609);
        this.addTalkId(30610);
        this.addTalkId(30611);
        this.addTalkId(30612);
        this.addKillId(20068);
        this.addKillId(20158);
        this.addKillId(20201);
        this.addKillId(20235);
        this.addKillId(20269);
        this.addKillId(20552);
        this.addKillId(20554);
        this.addKillId(20567);
        this.addKillId(20580);
        this.addQuestItem(2708);
        this.addQuestItem(2687);
        this.addQuestItem(2695);
        this.addQuestItem(2696);
        this.addQuestItem(2697);
        this.addQuestItem(2716);
        this.addQuestItem(2717);
        this.addQuestItem(2718);
        this.addQuestItem(2719);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        int n = questState.getState();
        if (string.equalsIgnoreCase("magister_mirien_q0214_04.htm") && n == 1) {
            questState.giveItems(2675, 1L);
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("sylvain_q0214_02.htm") && n == 2) {
            questState.playSound("ItemSound.quest_middle");
            questState.giveItems(2689, 1L);
            questState.giveItems(2692, 1L);
            questState.setCond(2);
        } else if (string.equalsIgnoreCase("marya_q0214_02.htm") && n == 2) {
            if (questState.getQuestItemsCount(2692) == 0L) {
                return null;
            }
            questState.takeItems(2692, -1L);
            questState.playSound("ItemSound.quest_middle");
            questState.giveItems(2679, 1L);
            questState.setCond(3);
        } else if (string.equalsIgnoreCase("astrologer_creta_q0214_05.htm") && n == 2) {
            if (questState.getQuestItemsCount(2680) == 0L) {
                return null;
            }
            questState.takeItems(2680, -1L);
            questState.playSound("ItemSound.quest_middle");
            questState.giveItems(2683, 1L);
            questState.setCond(6);
        } else if (string.equalsIgnoreCase("marya_q0214_08.htm") && n == 2) {
            if (questState.getQuestItemsCount(2683) == 0L) {
                return null;
            }
            questState.takeItems(2683, -1L);
            questState.playSound("ItemSound.quest_middle");
            questState.giveItems(2682, 1L);
            questState.setCond(7);
        } else if (string.equalsIgnoreCase("astrologer_creta_q0214_09.htm") && n == 2) {
            if (questState.getQuestItemsCount(2682) == 0L) {
                return null;
            }
            questState.takeItems(2682, -1L);
            questState.playSound("ItemSound.quest_middle");
            questState.giveItems(2684, 1L);
            questState.setCond(8);
        } else if (string.equalsIgnoreCase("lucas_q0214_04.htm") && n == 2) {
            if (questState.getQuestItemsCount(2685) == 0L) {
                return null;
            }
            questState.takeItems(2685, -1L);
            questState.playSound("ItemSound.quest_middle");
            questState.giveItems(2686, 1L);
            questState.setCond(10);
        } else if (string.equalsIgnoreCase("marya_q0214_14.htm") && n == 2) {
            if (questState.getQuestItemsCount(2686) == 0L || questState.getQuestItemsCount(2687) < 5L) {
                return null;
            }
            questState.takeItems(2686, -1L);
            questState.takeItems(2687, -1L);
            questState.playSound("ItemSound.quest_middle");
            questState.giveItems(2688, 1L);
            questState.setCond(13);
        } else if (string.equalsIgnoreCase("valkon_q0214_04.htm") && n == 2 && questState.getQuestItemsCount(2710) == 0L) {
            questState.playSound("ItemSound.quest_middle");
            questState.giveItems(2710, 1L);
        } else if (string.equalsIgnoreCase("jurek_q0214_03.htm") && n == 2 && questState.getQuestItemsCount(2690) == 0L && questState.getQuestItemsCount(2698) == 0L) {
            questState.playSound("ItemSound.quest_middle");
            questState.giveItems(2690, 1L);
            questState.giveItems(2694, 1L);
            questState.setCond(16);
        } else if (string.equalsIgnoreCase("magister_mirien_q0214_10.htm") && n == 2) {
            if (questState.getQuestItemsCount(2676) == 0L || questState.getQuestItemsCount(2698) == 0L) {
                return null;
            }
            questState.takeItems(2676, -1L);
            questState.takeItems(2698, -1L);
            if (questState.getPlayer().getLevel() < 36) {
                questState.giveItems(2678, 1L);
                return "magister_mirien_q0214_09.htm";
            }
            questState.playSound("ItemSound.quest_middle");
            questState.giveItems(2677, 1L);
            questState.setCond(19);
        } else if (string.equalsIgnoreCase("sage_cronos_q0214_10.htm") && n == 2) {
            questState.playSound("ItemSound.quest_middle");
            questState.giveItems(2691, 1L);
            questState.giveItems(2699, 1L);
            questState.setCond(20);
        } else if (string.equalsIgnoreCase("dieter_q0214_05.htm") && n == 2) {
            if (questState.getQuestItemsCount(2699) == 0L) {
                return null;
            }
            questState.takeItems(2699, -1L);
            questState.playSound("ItemSound.quest_middle");
            questState.giveItems(2700, 1L);
            questState.setCond(21);
        } else if (string.equalsIgnoreCase("astrologer_creta_q0214_14.htm") && n == 2) {
            if (questState.getQuestItemsCount(2700) == 0L) {
                return null;
            }
            questState.takeItems(2700, -1L);
            questState.playSound("ItemSound.quest_middle");
            questState.giveItems(2701, 1L);
            questState.setCond(22);
        } else if (string.equalsIgnoreCase("dieter_q0214_09.htm") && n == 2) {
            if (questState.getQuestItemsCount(2701) == 0L) {
                return null;
            }
            questState.takeItems(2701, -1L);
            questState.playSound("ItemSound.quest_middle");
            questState.giveItems(2702, 1L);
            questState.giveItems(2703, 1L);
            questState.setCond(23);
        } else if (string.equalsIgnoreCase("trader_edroc_q0214_02.htm") && n == 2) {
            if (questState.getQuestItemsCount(2702) == 0L) {
                return null;
            }
            questState.takeItems(2702, -1L);
            questState.playSound("ItemSound.quest_middle");
            questState.giveItems(2704, 1L);
            questState.setCond(24);
        } else if (string.equalsIgnoreCase("warehouse_keeper_raut_q0214_02.htm") && n == 2) {
            if (questState.getQuestItemsCount(2704) == 0L) {
                return null;
            }
            questState.takeItems(2704, -1L);
            questState.playSound("ItemSound.quest_middle");
            questState.giveItems(2706, 1L);
            questState.giveItems(2713, 1L);
            questState.setCond(25);
        } else if (string.equalsIgnoreCase("drunkard_treaf_q0214_04.htm") && n == 2) {
            if (questState.getQuestItemsCount(2713) == 0L) {
                return null;
            }
            questState.takeItems(2713, -1L);
            questState.playSound("ItemSound.quest_middle");
            questState.giveItems(2705, 1L);
            questState.setCond(26);
        } else if (string.equalsIgnoreCase("sage_kasian_q0214_04.htm") && n == 2) {
            if (questState.getQuestItemsCount(2715) == 0L) {
                questState.playSound("ItemSound.quest_middle");
                questState.giveItems(2715, 1L);
            }
            questState.setCond(28);
        } else if (string.equalsIgnoreCase("sage_kasian_q0214_07.htm") && n == 2) {
            questState.takeItems(2715, -1L);
            questState.takeItems(2716, -1L);
            questState.takeItems(2717, -1L);
            questState.takeItems(2718, -1L);
            questState.takeItems(2719, -1L);
            questState.takeItems(2711, -1L);
            questState.playSound("ItemSound.quest_middle");
            questState.giveItems(2709, 1L);
            questState.setCond(30);
        } else if (string.equalsIgnoreCase("sage_cronos_q0214_14.htm") && n == 2) {
            if (questState.getQuestItemsCount(2706) == 0L) {
                return null;
            }
            if (questState.getQuestItemsCount(2707) == 0L) {
                return null;
            }
            if (questState.getQuestItemsCount(2708) == 0L) {
                return null;
            }
            if (questState.getQuestItemsCount(2709) == 0L) {
                return null;
            }
            if (questState.getQuestItemsCount(2691) == 0L) {
                return null;
            }
            questState.takeItems(2706, -1L);
            questState.takeItems(2707, -1L);
            questState.takeItems(2708, -1L);
            questState.takeItems(2709, -1L);
            questState.takeItems(2691, -1L);
            questState.takeItems(2705, -1L);
            questState.takeItems(2703, 1L);
            questState.playSound("ItemSound.quest_middle");
            questState.giveItems(2720, 1L);
            questState.setCond(31);
        }
        return string;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        long l;
        long l2;
        if (questState.getQuestItemsCount(2674) > 0L) {
            questState.exitCurrentQuest(true);
            return "completed";
        }
        int n = questState.getState();
        int n2 = npcInstance.getNpcId();
        if (n == 1) {
            if (n2 != 30461) {
                return "noquest";
            }
            int n3 = questState.getPlayer().getClassId().getId();
            if (n3 != 11 && n3 != 26 && n3 != 39) {
                questState.exitCurrentQuest(true);
                return "magister_mirien_q0214_01.htm";
            }
            if (questState.getPlayer().getLevel() < 35) {
                questState.exitCurrentQuest(true);
                return "magister_mirien_q0214_02.htm";
            }
            questState.setCond(0);
            return "magister_mirien_q0214_03.htm";
        }
        if (n != 2) {
            return "noquest";
        }
        if (n2 == 30461) {
            if (questState.getQuestItemsCount(2675) > 0L) {
                if (questState.getQuestItemsCount(2693) == 0L) {
                    return "magister_mirien_q0214_05.htm";
                }
                questState.takeItems(2675, -1L);
                questState.takeItems(2693, -1L);
                questState.playSound("ItemSound.quest_middle");
                questState.giveItems(2676, 1L);
                questState.setCond(15);
                return "magister_mirien_q0214_06.htm";
            }
            if (questState.getQuestItemsCount(2676) > 0L) {
                return questState.getQuestItemsCount(2698) > 0L ? "magister_mirien_q0214_08.htm" : "magister_mirien_q0214_07.htm";
            }
            if (questState.getQuestItemsCount(2678) > 0L) {
                if (questState.getPlayer().getLevel() < 36) {
                    return "magister_mirien_q0214_11.htm";
                }
                questState.takeItems(2678, -1L);
                questState.playSound("ItemSound.quest_middle");
                questState.giveItems(2677, 1L);
                questState.setCond(19);
                return "magister_mirien_q0214_12.htm";
            }
            if (questState.getQuestItemsCount(2677) > 0L) {
                if (questState.getQuestItemsCount(2720) == 0L) {
                    return "magister_mirien_q0214_13.htm";
                }
                questState.takeItems(2720, -1L);
                questState.takeItems(2677, -1L);
                if (!questState.getPlayer().getVarB("prof2.1")) {
                    questState.giveItems(7562, 8L);
                    questState.addExpAndSp(80265L, 30000L);
                    questState.getPlayer().setVar("prof2.1", "1", -1L);
                }
                questState.giveItems(2674, 1L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                return "magister_mirien_q0214_14.htm";
            }
        }
        if (n2 == 30070) {
            if (questState.getQuestItemsCount(2675) > 0L) {
                if (questState.getQuestItemsCount(2689) < 1L) {
                    return questState.getQuestItemsCount(2693) > 0L ? "sylvain_q0214_05.htm" : "sylvain_q0214_01.htm";
                }
                if (questState.getQuestItemsCount(2688) < 1L) {
                    return "sylvain_q0214_03.htm";
                }
                questState.takeItems(2689, -1L);
                questState.takeItems(2688, -1L);
                questState.playSound("ItemSound.quest_middle");
                questState.giveItems(2693, 1L);
                questState.setCond(14);
                return "sylvain_q0214_04.htm";
            }
            if (questState.getQuestItemsCount(2676) > 0L || questState.getQuestItemsCount(2677) > 0L) {
                return "sylvain_q0214_06.htm";
            }
        }
        if (n2 == 30071) {
            if (questState.getQuestItemsCount(2675) > 0L && questState.getQuestItemsCount(2689) > 0L) {
                if (questState.getQuestItemsCount(2679) > 0L) {
                    questState.takeItems(2679, -1L);
                    questState.playSound("ItemSound.quest_middle");
                    questState.giveItems(2681, 1L);
                    questState.setCond(4);
                    return "lucas_q0214_01.htm";
                }
                if (questState.getQuestItemsCount(2680) > 0L) {
                    return "lucas_q0214_02.htm";
                }
                if (questState.getQuestItemsCount(2683) > 0L) {
                    return "lucas_q0214_02.htm";
                }
                if (questState.getQuestItemsCount(2682) > 0L) {
                    return "lucas_q0214_02.htm";
                }
                if (questState.getQuestItemsCount(2684) > 0L) {
                    return "lucas_q0214_02.htm";
                }
                if (questState.getQuestItemsCount(2681) > 0L) {
                    return "lucas_q0214_02.htm";
                }
                if (questState.getQuestItemsCount(2685) > 0L) {
                    return "lucas_q0214_03.htm";
                }
                if (questState.getQuestItemsCount(2686) > 0L) {
                    return questState.getQuestItemsCount(2687) < 5L ? "lucas_q0214_05.htm" : "lucas_q0214_06.htm";
                }
            } else {
                if (questState.getQuestItemsCount(2693) > 0L) {
                    return "lucas_q0214_07.htm";
                }
                if (questState.getQuestItemsCount(2676) > 0L) {
                    return "lucas_q0214_07.htm";
                }
                if (questState.getQuestItemsCount(2677) > 0L) {
                    return "lucas_q0214_07.htm";
                }
                if (questState.getQuestItemsCount(2688) > 0L) {
                    return "lucas_q0214_07.htm";
                }
            }
        }
        if (n2 == 30103 && questState.getQuestItemsCount(2705) > 0L) {
            l2 = questState.getQuestItemsCount(2710);
            l = questState.getQuestItemsCount(2707);
            if (questState.getQuestItemsCount(2714) == 0L) {
                if (l == 0L) {
                    return l2 > 0L ? "valkon_q0214_05.htm" : "valkon_q0214_01.htm";
                }
                if (l2 == 0L) {
                    return "valkon_q0214_07.htm";
                }
            } else if (l2 == 0L && l == 0L) {
                questState.takeItems(2714, -1L);
                questState.playSound("ItemSound.quest_middle");
                questState.giveItems(2707, 1L);
                return "valkon_q0214_06.htm";
            }
        }
        if (n2 == 30111) {
            if (questState.getQuestItemsCount(2677) > 0L && questState.getQuestItemsCount(2691) > 0L) {
                if (questState.getQuestItemsCount(2699) > 0L) {
                    return "dieter_q0214_01.htm";
                }
                if (questState.getQuestItemsCount(2700) > 0L) {
                    return "dieter_q0214_06.htm";
                }
                if (questState.getQuestItemsCount(2701) > 0L) {
                    return "dieter_q0214_07.htm";
                }
                if (questState.getQuestItemsCount(2703) > 0L) {
                    if (questState.getQuestItemsCount(2702) > 0L) {
                        return "dieter_q0214_10.htm";
                    }
                    if (questState.getQuestItemsCount(2704) > 0L) {
                        return "dieter_q0214_11.htm";
                    }
                    if (questState.getQuestItemsCount(2706) == 0L) {
                        return "dieter_q0214_12.htm";
                    }
                    if (questState.getQuestItemsCount(2707) == 0L) {
                        return "dieter_q0214_12.htm";
                    }
                    if (questState.getQuestItemsCount(2708) == 0L) {
                        return "dieter_q0214_12.htm";
                    }
                    if (questState.getQuestItemsCount(2709) == 0L) {
                        return "dieter_q0214_12.htm";
                    }
                    return "dieter_q0214_13.htm";
                }
            }
            if (questState.getQuestItemsCount(2720) > 0L) {
                return "dieter_q0214_15.htm";
            }
        }
        if (n2 == 30115) {
            if (questState.getQuestItemsCount(2676) > 0L) {
                l2 = questState.getQuestItemsCount(2690);
                l = questState.getQuestItemsCount(2698);
                if (l2 == 0L && l == 0L) {
                    return "jurek_q0214_01.htm";
                }
                if (questState.getQuestItemsCount(2694) > 0L) {
                    if (!_214_TrialOfScholar.c(questState)) {
                        return "jurek_q0214_04.htm";
                    }
                    if (l2 > 0L) {
                        questState.takeItems(2694, -1L);
                        questState.takeItems(2695, -1L);
                        questState.takeItems(2696, -1L);
                        questState.takeItems(2697, -1L);
                        questState.takeItems(2690, -1L);
                        questState.playSound("ItemSound.quest_middle");
                        questState.giveItems(2698, 1L);
                        questState.setCond(18);
                        return "jurek_q0214_05.htm";
                    }
                }
                if (l > 0L && l2 == 0L) {
                    return "jurek_q0214_06.htm";
                }
            }
            if (questState.getQuestItemsCount(2675) > 0L || questState.getQuestItemsCount(2677) > 0L) {
                return "jurek_q0214_07.htm";
            }
        }
        if (n2 == 30230 && questState.getQuestItemsCount(2703) > 0L) {
            if (questState.getQuestItemsCount(2702) > 0L) {
                return "trader_edroc_q0214_01.htm";
            }
            if (questState.getQuestItemsCount(2704) > 0L) {
                return "trader_edroc_q0214_03.htm";
            }
            if (questState.getQuestItemsCount(2713) > 0L || questState.getQuestItemsCount(2705) > 0L) {
                return "trader_edroc_q0214_04.htm";
            }
        }
        if (n2 == 30316 && questState.getQuestItemsCount(2703) > 0L) {
            if (questState.getQuestItemsCount(2704) > 0L) {
                return "warehouse_keeper_raut_q0214_01.htm";
            }
            if (questState.getQuestItemsCount(2706) > 0L) {
                if (questState.getQuestItemsCount(2713) > 0L) {
                    return "warehouse_keeper_raut_q0214_04.htm";
                }
                if (questState.getQuestItemsCount(2705) > 0L) {
                    return "warehouse_keeper_raut_q0214_05.htm";
                }
            }
        }
        if (n2 == 30458 && questState.getQuestItemsCount(2705) > 0L) {
            l2 = questState.getQuestItemsCount(2711);
            l = questState.getQuestItemsCount(2715);
            if (questState.getQuestItemsCount(2709) == 0L) {
                if (l2 > 0L) {
                    return l > 0L ? "blacksmith_poitan_q0214_03.htm" : "valkon_q0214_02.htm";
                }
                if (l == 0L) {
                    questState.playSound("ItemSound.quest_middle");
                    questState.giveItems(2711, 1L);
                    return "blacksmith_poitan_q0214_01.htm";
                }
            } else if (l2 == 0L && l == 0L) {
                return "blacksmith_poitan_q0214_04.htm";
            }
        }
        if (n2 == 30608) {
            if (questState.getQuestItemsCount(2675) > 0L && questState.getQuestItemsCount(2689) > 0L) {
                if (questState.getQuestItemsCount(2692) > 0L) {
                    return "marya_q0214_01.htm";
                }
                if (questState.getQuestItemsCount(2679) > 0L) {
                    return "marya_q0214_03.htm";
                }
                if (questState.getQuestItemsCount(2681) > 0L) {
                    questState.takeItems(2681, -1L);
                    questState.playSound("ItemSound.quest_middle");
                    questState.giveItems(2680, 1L);
                    questState.setCond(5);
                    return "marya_q0214_04.htm";
                }
                if (questState.getQuestItemsCount(2680) > 0L) {
                    return "marya_q0214_05.htm";
                }
                if (questState.getQuestItemsCount(2683) > 0L) {
                    return "marya_q0214_06.htm";
                }
                if (questState.getQuestItemsCount(2682) > 0L) {
                    return "marya_q0214_09.htm";
                }
                if (questState.getQuestItemsCount(2684) > 0L) {
                    questState.takeItems(2684, 1L);
                    questState.playSound("ItemSound.quest_middle");
                    questState.giveItems(2685, 1L);
                    questState.setCond(9);
                    return "marya_q0214_10.htm";
                }
                if (questState.getQuestItemsCount(2685) > 0L) {
                    return "marya_q0214_11.htm";
                }
                if (questState.getQuestItemsCount(2686) > 0L) {
                    if (questState.getQuestItemsCount(2687) < 5L) {
                        questState.setCond(11);
                        return "marya_q0214_12.htm";
                    }
                    return "marya_q0214_13.htm";
                }
                if (questState.getQuestItemsCount(2688) > 0L) {
                    return "marya_q0214_15.htm";
                }
            }
            if (questState.getQuestItemsCount(2693) > 0L || questState.getQuestItemsCount(2676) > 0L) {
                return "marya_q0214_16.htm";
            }
            if (questState.getQuestItemsCount(2677) > 0L) {
                if (questState.getQuestItemsCount(2710) == 0L) {
                    return "marya_q0214_17.htm";
                }
                questState.takeItems(2710, 1L);
                questState.playSound("ItemSound.quest_middle");
                questState.giveItems(2714, 1L);
                return "marya_q0214_18.htm";
            }
        }
        if (n2 == 30609) {
            if (questState.getQuestItemsCount(2675) > 0L && questState.getQuestItemsCount(2689) > 0L) {
                if (questState.getQuestItemsCount(2680) > 0L) {
                    return "astrologer_creta_q0214_01.htm";
                }
                if (questState.getQuestItemsCount(2683) > 0L) {
                    return "astrologer_creta_q0214_06.htm";
                }
                if (questState.getQuestItemsCount(2682) > 0L) {
                    return "astrologer_creta_q0214_07.htm";
                }
                if (questState.getQuestItemsCount(2684) > 0L) {
                    return "astrologer_creta_q0214_10.htm";
                }
                if (questState.getQuestItemsCount(2685) > 0L) {
                    return "astrologer_creta_q0214_10.htm";
                }
                if (questState.getQuestItemsCount(2686) > 0L) {
                    return "astrologer_creta_q0214_10.htm";
                }
            }
            if (questState.getQuestItemsCount(2688) > 0L) {
                return "astrologer_creta_q0214_11.htm";
            }
            if (questState.getQuestItemsCount(2693) > 0L) {
                return "astrologer_creta_q0214_11.htm";
            }
            if (questState.getQuestItemsCount(2676) > 0L) {
                return "astrologer_creta_q0214_11.htm";
            }
            if (questState.getQuestItemsCount(2677) > 0L) {
                return questState.getQuestItemsCount(2700) > 0L ? "astrologer_creta_q0214_12.htm" : "astrologer_creta_q0214_15.htm";
            }
        }
        if (n2 == 30610 && questState.getQuestItemsCount(2677) > 0L) {
            if (questState.getQuestItemsCount(2691) > 0L) {
                if (questState.getQuestItemsCount(2706) == 0L) {
                    return "sage_cronos_q0214_11.htm";
                }
                if (questState.getQuestItemsCount(2707) == 0L) {
                    return "sage_cronos_q0214_11.htm";
                }
                if (questState.getQuestItemsCount(2708) == 0L) {
                    return "sage_cronos_q0214_11.htm";
                }
                if (questState.getQuestItemsCount(2709) == 0L) {
                    return "sage_cronos_q0214_11.htm";
                }
                return "sage_cronos_q0214_12.htm";
            }
            return questState.getQuestItemsCount(2720) > 0L ? "sage_cronos_q0214_15.htm" : "sage_cronos_q0214_01.htm";
        }
        if (n2 == 30611) {
            if (questState.getQuestItemsCount(2703) > 0L && questState.getQuestItemsCount(2706) > 0L && questState.getQuestItemsCount(2713) > 0L) {
                return "drunkard_treaf_q0214_01.htm";
            }
            if (questState.getQuestItemsCount(2705) > 0L) {
                return "drunkard_treaf_q0214_05.htm";
            }
            if (questState.getQuestItemsCount(2720) > 0L) {
                return "drunkard_treaf_q0214_05.htm";
            }
        }
        if (n2 == 30612 && questState.getQuestItemsCount(2705) > 0L) {
            l2 = questState.getQuestItemsCount(2715);
            if (questState.getQuestItemsCount(2711) > 0L) {
                if (l2 > 0L) {
                    return _214_TrialOfScholar.d(questState) ? "sage_kasian_q0214_06.htm" : "sage_kasian_q0214_05.htm";
                }
                if (questState.getQuestItemsCount(2706) > 0L && questState.getQuestItemsCount(2707) > 0L && questState.getQuestItemsCount(2708) > 0L) {
                    return "sage_kasian_q0214_02.htm";
                }
                questState.setCond(27);
                return "sage_kasian_q0214_01.htm";
            }
            if (l2 == 0L && questState.getQuestItemsCount(2706) > 0L && questState.getQuestItemsCount(2707) > 0L && questState.getQuestItemsCount(2708) > 0L && questState.getQuestItemsCount(2709) > 0L) {
                return "sage_kasian_q0214_08.htm";
            }
        }
        return "noquest";
    }

    private static boolean c(QuestState questState) {
        if (questState.getQuestItemsCount(2695) < 5L) {
            return false;
        }
        if (questState.getQuestItemsCount(2696) < 5L) {
            return false;
        }
        return questState.getQuestItemsCount(2697) >= 2L;
    }

    private static boolean d(QuestState questState) {
        if (questState.getQuestItemsCount(2716) < 10L) {
            return false;
        }
        if (questState.getQuestItemsCount(2717) < 12L) {
            return false;
        }
        if (questState.getQuestItemsCount(2718) < 5L) {
            return false;
        }
        return questState.getQuestItemsCount(2719) >= 5L;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getState() != 2) {
            return null;
        }
        int n = npcInstance.getNpcId();
        if (n == 20554 && questState.getQuestItemsCount(2677) > 0L && questState.getQuestItemsCount(2691) > 0L && questState.getQuestItemsCount(2705) > 0L) {
            questState.rollAndGive(2708, 1, 1, 1, 30.0);
        }
        if (n == 20580 && questState.getQuestItemsCount(2675) > 0L && questState.getQuestItemsCount(2689) > 0L && questState.getQuestItemsCount(2686) > 0L && questState.rollAndGive(2687, 1, 1, 5, 50.0)) {
            questState.setCond(12);
        }
        if (n == 20068 && questState.getQuestItemsCount(2676) > 0L && questState.getQuestItemsCount(2690) > 0L && questState.getQuestItemsCount(2694) > 0L && questState.rollAndGive(2695, 1, 1, 5, 50.0) && _214_TrialOfScholar.c(questState)) {
            questState.setCond(17);
        }
        if (n == 20269 && questState.getQuestItemsCount(2676) > 0L && questState.getQuestItemsCount(2690) > 0L && questState.getQuestItemsCount(2694) > 0L && questState.rollAndGive(2696, 1, 1, 5, 50.0) && _214_TrialOfScholar.c(questState)) {
            questState.setCond(17);
        }
        if (n == 20235 && questState.getQuestItemsCount(2676) > 0L && questState.getQuestItemsCount(2690) > 0L && questState.getQuestItemsCount(2694) > 0L && questState.rollAndGive(2697, 1, 1, 2, 50.0) && _214_TrialOfScholar.c(questState)) {
            questState.setCond(17);
        }
        if (n == 20201 && questState.getQuestItemsCount(2705) > 0L && questState.getQuestItemsCount(2711) > 0L && questState.getQuestItemsCount(2715) > 0L && questState.rollAndGive(2716, 1, 1, 10, 100.0) && _214_TrialOfScholar.d(questState)) {
            questState.setCond(29);
        }
        if (n == 20158 && questState.getQuestItemsCount(2705) > 0L && questState.getQuestItemsCount(2711) > 0L && questState.getQuestItemsCount(2715) > 0L && questState.rollAndGive(2717, 1, 1, 12, 100.0) && _214_TrialOfScholar.d(questState)) {
            questState.setCond(29);
        }
        if (n == 20552 && questState.getQuestItemsCount(2705) > 0L && questState.getQuestItemsCount(2711) > 0L && questState.getQuestItemsCount(2715) > 0L && questState.rollAndGive(2718, 1, 1, 5, 100.0) && _214_TrialOfScholar.d(questState)) {
            questState.setCond(29);
        }
        if (n == 20567 && questState.getQuestItemsCount(2705) > 0L && questState.getQuestItemsCount(2711) > 0L && questState.getQuestItemsCount(2715) > 0L && questState.rollAndGive(2719, 1, 1, 5, 100.0) && _214_TrialOfScholar.d(questState)) {
            questState.setCond(29);
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

