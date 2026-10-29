/*
 * Decompiled with CFR 0.152.
 */
package quests;

import java.util.Calendar;
import java.util.HashMap;
import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.model.Party;
import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _620_FourGoblets
extends Quest
implements ScriptFile {
    private static final int aFO = 31453;
    private static final int bOP = 31919;
    private static final int bOQ = 31920;
    private static final int bOR = 31452;
    private static final int bOS = 31454;
    private static final int bOT = 31921;
    private static final int bOU = 31922;
    private static final int bOV = 31923;
    private static final int bOW = 31924;
    private static final int bOX = 55;
    private static final int bOY = 25339;
    private static final int bOZ = 25342;
    private static final int bPa = 25346;
    private static final int bPb = 25349;
    private static final HashMap<Integer, Integer> bPc = new HashMap(61);
    private static final HashMap<Integer, Integer> bPd = new HashMap(10);
    private static final HashMap<Integer, Integer> bPe = new HashMap(8);

    public _620_FourGoblets() {
        super(0);
        this.addStartNpc(31453);
        this.addStartNpc(31452);
        this.addTalkId(31453, 31919, 31452, 31454, 31920);
        this.addTalkId(31921, 31922, 31923, 31924);
        this.addKillId(25339, 25342, 25346, 25349);
        this.addKillId(bPc.keySet());
        this.addKillId(bPd.keySet());
        this.addKillId(bPe.keySet());
        this.addQuestItem(7255, 7256, 7257, 7258, 7259, 7261);
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getInt("four_goblets_ex");
        String string = "noquest";
        Player player = questState.getPlayer();
        int n2 = npcInstance.getNpcId();
        if (n2 == 31453) {
            if (questState.isCreated()) {
                if (player.getLevel() >= 74) {
                    return "printessa_spirit_q0620_01.htm";
                }
                return "printessa_spirit_q0620_12.htm";
            }
            if (questState.isStarted()) {
                if (questState.getQuestItemsCount(7262) == 0L && (questState.getQuestItemsCount(7256) == 0L || questState.getQuestItemsCount(7257) == 0L || questState.getQuestItemsCount(7258) == 0L || questState.getQuestItemsCount(7259) == 0L)) {
                    return "printessa_spirit_q0620_14.htm";
                }
                if (questState.getQuestItemsCount(7262) == 0L && questState.getQuestItemsCount(7256) > 0L && questState.getQuestItemsCount(7257) > 0L && questState.getQuestItemsCount(7258) > 0L && questState.getQuestItemsCount(7259) > 0L) {
                    return "printessa_spirit_q0620_15.htm";
                }
                if (questState.getQuestItemsCount(7262) > 0L) {
                    return "printessa_spirit_q0620_17.htm";
                }
            }
        } else if (n2 == 31919) {
            if (questState.isStarted()) {
                return "el_lord_chamber_ghost_q0620_01.htm";
            }
        } else if (n2 == 31452) {
            if (questState.isCreated()) {
                return "wigoth_ghost_a_q0620_01.htm";
            }
            if (questState.isStarted()) {
                if (questState.getQuestItemsCount(7262) == 0L && (questState.getQuestItemsCount(7256) == 0L || questState.getQuestItemsCount(7257) == 0L || questState.getQuestItemsCount(7258) == 0L || questState.getQuestItemsCount(7259) == 0L) && questState.getQuestItemsCount(7256) + questState.getQuestItemsCount(7257) + questState.getQuestItemsCount(7258) + questState.getQuestItemsCount(7259) < 3L) {
                    return "wigoth_ghost_a_q0620_01.htm";
                }
                if (questState.getQuestItemsCount(7262) == 0L && (questState.getQuestItemsCount(7256) == 0L || questState.getQuestItemsCount(7257) == 0L || questState.getQuestItemsCount(7258) == 0L || questState.getQuestItemsCount(7259) == 0L) && questState.getQuestItemsCount(7256) + questState.getQuestItemsCount(7257) + questState.getQuestItemsCount(7258) + questState.getQuestItemsCount(7259) >= 3L) {
                    return "wigoth_ghost_a_q0620_02.htm";
                }
                if (questState.getQuestItemsCount(7262) == 0L && questState.getQuestItemsCount(7256) >= 1L && questState.getQuestItemsCount(7257) >= 1L && questState.getQuestItemsCount(7258) >= 1L && questState.getQuestItemsCount(7259) >= 1L) {
                    return "wigoth_ghost_a_q0620_04.htm";
                }
                if (questState.getQuestItemsCount(7262) >= 1L) {
                    return "wigoth_ghost_a_q0620_05.htm";
                }
            }
        } else if (n2 == 31454) {
            if (questState.isStarted()) {
                if (n == 2 && questState.getQuestItemsCount(7256) >= 1L && questState.getQuestItemsCount(7257) >= 1L && questState.getQuestItemsCount(7258) >= 1L && questState.getQuestItemsCount(7259) >= 1L) {
                    if (questState.getQuestItemsCount(7255) == 0L && questState.getQuestItemsCount(7254) < 1000L) {
                        questState.set("four_goblets_ex", String.valueOf(3), true);
                        return "wigoth_ghost_b_q0620_01.htm";
                    }
                    if (questState.getQuestItemsCount(7255) >= 1L && questState.getQuestItemsCount(7254) < 1000L) {
                        questState.set("four_goblets_ex", String.valueOf(3), true);
                        return "wigoth_ghost_b_q0620_02.htm";
                    }
                    if (questState.getQuestItemsCount(7255) == 0L && questState.getQuestItemsCount(7254) >= 1000L) {
                        questState.set("four_goblets_ex", String.valueOf(3), true);
                        return "wigoth_ghost_b_q0620_03.htm";
                    }
                    if (questState.getQuestItemsCount(7255) >= 1L && questState.getQuestItemsCount(7254) >= 1000L) {
                        questState.set("four_goblets_ex", String.valueOf(3), true);
                        return "wigoth_ghost_b_q0620_04.htm";
                    }
                }
                if (n == 2 && (questState.getQuestItemsCount(7256) == 0L || questState.getQuestItemsCount(7257) == 0L || questState.getQuestItemsCount(7258) == 0L || questState.getQuestItemsCount(7259) == 0L)) {
                    if (questState.getQuestItemsCount(7255) == 0L && questState.getQuestItemsCount(7254) < 1000L) {
                        questState.set("four_goblets_ex", String.valueOf(3), true);
                        return "wigoth_ghost_b_q0620_05.htm";
                    }
                    if (questState.getQuestItemsCount(7255) >= 1L && questState.getQuestItemsCount(7254) < 1000L) {
                        questState.set("four_goblets_ex", String.valueOf(3), true);
                        return "wigoth_ghost_b_q0620_06.htm";
                    }
                    if (questState.getQuestItemsCount(7255) == 0L && questState.getQuestItemsCount(7254) >= 1000L) {
                        questState.set("four_goblets_ex", String.valueOf(3), true);
                        return "wigoth_ghost_b_q0620_07.htm";
                    }
                    if (questState.getQuestItemsCount(7255) >= 1L && questState.getQuestItemsCount(7254) >= 1000L) {
                        questState.set("four_goblets_ex", String.valueOf(3), true);
                        return "wigoth_ghost_b_q0620_08.htm";
                    }
                }
                if (n == 3) {
                    if (questState.getQuestItemsCount(7255) == 0L && questState.getQuestItemsCount(7254) < 1000L) {
                        return "wigoth_ghost_b_q0620_09.htm";
                    }
                    if (questState.getQuestItemsCount(7255) >= 1L && questState.getQuestItemsCount(7254) < 1000L) {
                        return "wigoth_ghost_b_q0620_10.htm";
                    }
                    if (questState.getQuestItemsCount(7255) == 0L && questState.getQuestItemsCount(7254) >= 1000L) {
                        return "wigoth_ghost_b_q0620_11.htm";
                    }
                    if (questState.getQuestItemsCount(7255) >= 1L && questState.getQuestItemsCount(7254) >= 1000L) {
                        return "wigoth_ghost_b_q0620_12.htm";
                    }
                }
            }
        } else if (n2 == 31921) {
            if (questState.isStarted()) {
                return "conquerors_keeper_q0620_01.htm";
            }
        } else if (n2 == 31922) {
            if (questState.isStarted()) {
                return "lords_keeper_q0620_01.htm";
            }
        } else if (n2 == 31923) {
            if (questState.isStarted()) {
                return "savants_keeper_q0620_01.htm";
            }
        } else if (n2 == 31924 && questState.isStarted()) {
            return "magistrates_keeper_q0620_01.htm";
        }
        return string;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        QuestState questState2;
        Player player = questState.getPlayer();
        String string2 = string;
        int n = questState.getInt("four_goblets_ex");
        int n2 = npcInstance.getNpcId();
        if (n2 == 31453) {
            if (string.equalsIgnoreCase("reply_620")) {
                questState.setCond(1);
                questState.setState(2);
                questState.set("four_goblets", String.valueOf(0), true);
                questState.playSound("ItemSound.quest_accept");
                string2 = "printessa_spirit_q0620_13.htm";
                if (questState.getQuestItemsCount(7262) < 1L) return string2;
                questState.setCond(2);
                return "printessa_spirit_q0620_13.htm";
            }
            if (string.equalsIgnoreCase("reply_5")) {
                return "printessa_spirit_q0620_02.htm";
            }
            if (string.equalsIgnoreCase("reply_6")) {
                return "printessa_spirit_q0620_03.htm";
            }
            if (string.equalsIgnoreCase("reply_7")) {
                return "printessa_spirit_q0620_04.htm";
            }
            if (string.equalsIgnoreCase("reply_8")) {
                return "printessa_spirit_q0620_05.htm";
            }
            if (string.equalsIgnoreCase("reply_9")) {
                return "printessa_spirit_q0620_06.htm";
            }
            if (string.equalsIgnoreCase("reply_10")) {
                return "printessa_spirit_q0620_07.htm";
            }
            if (string.equalsIgnoreCase("reply_11")) {
                return "printessa_spirit_q0620_08.htm";
            }
            if (string.equalsIgnoreCase("reply_12")) {
                return "printessa_spirit_q0620_09.htm";
            }
            if (string.equalsIgnoreCase("reply_13")) {
                return "printessa_spirit_q0620_10.htm";
            }
            if (string.equalsIgnoreCase("reply_14")) {
                return "printessa_spirit_q0620_11.htm";
            }
            if (string.equalsIgnoreCase("reply_4")) {
                if (!questState.isStarted()) return string2;
                if (questState.getQuestItemsCount(7262) != 0L) return string2;
                if (questState.getQuestItemsCount(7256) < 1L) return string2;
                if (questState.getQuestItemsCount(7257) < 1L) return string2;
                if (questState.getQuestItemsCount(7258) < 1L) return string2;
                if (questState.getQuestItemsCount(7259) < 1L) return string2;
                questState.giveItems(7262, 1L);
                questState.setCond(2);
                questState.playSound("ItemSound.quest_middle");
                questState.takeItems(7256, 1L);
                questState.takeItems(7257, 1L);
                questState.takeItems(7258, 1L);
                questState.takeItems(7259, 1L);
                return "printessa_spirit_q0620_16.htm";
            }
            if (string.equalsIgnoreCase("reply_1")) {
                questState.takeItems(7260, -1L);
                questState.takeItems(7261, -1L);
                questState.exitCurrentQuest(true);
                questState.playSound("ItemSound.quest_finish");
                return "printessa_spirit_q0620_18.htm";
            }
            if (string.equalsIgnoreCase("reply_2")) {
                return "printessa_spirit_q0620_19.htm";
            }
            if (!string.equalsIgnoreCase("reply_3")) return string2;
            return "printessa_spirit_q0620_20.htm";
        }
        if (n2 == 31919) {
            if (string.equalsIgnoreCase("reply_1")) {
                if (questState.isStarted() && questState.getQuestItemsCount(7255) >= 1L) {
                    int n3 = 0;
                    while ((long)n3 < questState.getQuestItemsCount(7255)) {
                        if (Rnd.get(100) < 50) {
                            int n4;
                            int n5 = Rnd.get(5);
                            if (n5 == 0) {
                                questState.giveItems(57, 10000L);
                            } else if (n5 == 1) {
                                if (Rnd.get(1000) < 848) {
                                    n4 = Rnd.get(1000);
                                    if (n4 < 43) {
                                        questState.giveItems(1884, 42L);
                                    } else if (n4 < 66) {
                                        questState.giveItems(1895, 36L);
                                    } else if (n4 < 184) {
                                        questState.giveItems(1876, 4L);
                                    } else if (n4 < 250) {
                                        questState.giveItems(1881, 6L);
                                    } else if (n4 < 287) {
                                        questState.giveItems(5549, 8L);
                                    } else if (n4 < 484) {
                                        questState.giveItems(1874, 1L);
                                    } else if (n4 < 681) {
                                        questState.giveItems(1889, 1L);
                                    } else if (n4 < 799) {
                                        questState.giveItems(1877, 1L);
                                    } else if (n4 < 902) {
                                        questState.giveItems(1894, 1L);
                                    } else {
                                        questState.giveItems(4043, 1L);
                                    }
                                }
                                if (Rnd.get(1000) < 323) {
                                    n4 = Rnd.get(1000);
                                    if (n4 < 335) {
                                        questState.giveItems(1888, 1L);
                                    } else if (n4 < 556) {
                                        questState.giveItems(4040, 1L);
                                    } else if (n4 < 725) {
                                        questState.giveItems(1890, 1L);
                                    } else if (n4 < 872) {
                                        questState.giveItems(5550, 1L);
                                    } else if (n4 < 962) {
                                        questState.giveItems(1893, 1L);
                                    } else if (n4 < 986) {
                                        questState.giveItems(4046, 1L);
                                    } else {
                                        questState.giveItems(4048, 1L);
                                    }
                                }
                            } else if (n5 == 2) {
                                if (Rnd.get(1000) < 847) {
                                    n4 = Rnd.get(1000);
                                    if (n4 < 148) {
                                        questState.giveItems(1878, 8L);
                                    } else if (n4 < 175) {
                                        questState.giveItems(1882, 24L);
                                    } else if (n4 < 273) {
                                        questState.giveItems(1879, 4L);
                                    } else if (n4 < 322) {
                                        questState.giveItems(1880, 6L);
                                    } else if (n4 < 357) {
                                        questState.giveItems(1885, 6L);
                                    } else if (n4 < 554) {
                                        questState.giveItems(1875, 1L);
                                    } else if (n4 < 685) {
                                        questState.giveItems(1883, 1L);
                                    } else if (n4 < 803) {
                                        questState.giveItems(5220, 1L);
                                    } else if (n4 < 901) {
                                        questState.giveItems(4039, 1L);
                                    } else {
                                        questState.giveItems(4044, 1L);
                                    }
                                }
                                if (Rnd.get(1000) < 251) {
                                    n4 = Rnd.get(1000);
                                    if (n4 < 350) {
                                        questState.giveItems(1887, 1L);
                                    } else if (n4 < 587) {
                                        questState.giveItems(4042, 1L);
                                    } else if (n4 < 798) {
                                        questState.giveItems(1886, 1L);
                                    } else if (n4 < 922) {
                                        questState.giveItems(4041, 1L);
                                    } else if (n4 < 966) {
                                        questState.giveItems(1892, 1L);
                                    } else if (n4 < 996) {
                                        questState.giveItems(1891, 1L);
                                    } else {
                                        questState.giveItems(4047, 1L);
                                    }
                                }
                            } else if (n5 == 3) {
                                if (Rnd.get(1000) < 31) {
                                    n4 = Rnd.get(1000);
                                    if (n4 < 223) {
                                        questState.giveItems(730, 1L);
                                    } else if (n4 < 893) {
                                        questState.giveItems(948, 1L);
                                    } else {
                                        questState.giveItems(960, 1L);
                                    }
                                }
                                if (Rnd.get(1000) < 5) {
                                    n4 = Rnd.get(1000);
                                    if (n4 < 202) {
                                        questState.giveItems(729, 1L);
                                    } else if (n4 < 928) {
                                        questState.giveItems(947, 1L);
                                    } else {
                                        questState.giveItems(959, 1L);
                                    }
                                }
                            } else if (n5 == 4) {
                                if (Rnd.get(1000) < 329) {
                                    n4 = Rnd.get(1000);
                                    if (n4 < 88) {
                                        questState.giveItems(6698, 1L);
                                    } else if (n4 < 185) {
                                        questState.giveItems(6699, 1L);
                                    } else if (n4 < 238) {
                                        questState.giveItems(6700, 1L);
                                    } else if (n4 < 262) {
                                        questState.giveItems(6701, 1L);
                                    } else if (n4 < 292) {
                                        questState.giveItems(6702, 1L);
                                    } else if (n4 < 356) {
                                        questState.giveItems(6703, 1L);
                                    } else if (n4 < 420) {
                                        questState.giveItems(6704, 1L);
                                    } else if (n4 < 482) {
                                        questState.giveItems(6705, 1L);
                                    } else if (n4 < 554) {
                                        questState.giveItems(6706, 1L);
                                    } else if (n4 < 576) {
                                        questState.giveItems(6707, 1L);
                                    } else if (n4 < 640) {
                                        questState.giveItems(6708, 1L);
                                    } else if (n4 < 704) {
                                        questState.giveItems(6709, 1L);
                                    } else if (n4 < 777) {
                                        questState.giveItems(6710, 1L);
                                    } else if (n4 < 799) {
                                        questState.giveItems(6711, 1L);
                                    } else if (n4 < 863) {
                                        questState.giveItems(6712, 1L);
                                    } else if (n4 < 927) {
                                        questState.giveItems(6713, 1L);
                                    } else {
                                        questState.giveItems(6714, 1L);
                                    }
                                }
                                if (Rnd.get(1000) < 54) {
                                    n4 = Rnd.get(1000);
                                    if (n4 < 100) {
                                        questState.giveItems(6688, 1L);
                                    } else if (n4 < 198) {
                                        questState.giveItems(6689, 1L);
                                    } else if (n4 < 298) {
                                        questState.giveItems(6690, 1L);
                                    } else if (n4 < 398) {
                                        questState.giveItems(6691, 1L);
                                    } else if (n4 < 499) {
                                        questState.giveItems(7579, 1L);
                                    } else if (n4 < 601) {
                                        questState.giveItems(6693, 1L);
                                    } else if (n4 < 703) {
                                        questState.giveItems(6694, 1L);
                                    } else if (n4 < 801) {
                                        questState.giveItems(6695, 1L);
                                    } else if (n4 < 902) {
                                        questState.giveItems(6696, 1L);
                                    } else {
                                        questState.giveItems(6697, 1L);
                                    }
                                }
                            }
                        }
                        questState.takeItems(7255, 1L);
                        if (questState.getQuestItemsCount(7255) == 0L) {
                            string2 = "el_lord_chamber_ghost_q0620_03.htm";
                        }
                        --n3;
                        ++n3;
                    }
                    return string2;
                }
                if (!questState.isStarted()) return string2;
                if (questState.getQuestItemsCount(7255) != 0L) return string2;
                return "el_lord_chamber_ghost_q0620_06.htm";
            }
            if (string.equalsIgnoreCase("reply_101")) {
                if (!questState.isStarted()) return "el_lord_chamber_ghost002.htm";
                if (questState.getQuestItemsCount(7261) < 1L) {
                    return "el_lord_chamber_ghost002.htm";
                }
                questState.takeItems(7261, 1L);
                player.teleToLocation(178127, -84435, -7216);
                return "";
            }
            if (!string.equalsIgnoreCase("reply_102")) return string2;
            if (!questState.isStarted()) return "el_lord_chamber_ghost002.htm";
            if (questState.getQuestItemsCount(7261) < 1L) {
                return "el_lord_chamber_ghost002.htm";
            }
            questState.takeItems(7261, 1L);
            player.teleToLocation(186699, -75915, -2824);
            return "";
        }
        if (n2 == 31920) {
            if (string.equalsIgnoreCase("reply_101")) {
                if (!questState.isStarted()) return "el_lord_chamber_ghost002.htm";
                if (questState.getQuestItemsCount(7261) < 1L) {
                    return "el_lord_chamber_ghost002.htm";
                }
                questState.takeItems(7261, 1L);
                player.teleToLocation(178127, -84435, -7216);
                return "";
            }
            if (!string.equalsIgnoreCase("reply_102")) return string2;
            if (!questState.isStarted()) return "el_lord_chamber_ghost002.htm";
            if (questState.getQuestItemsCount(7261) < 1L) {
                return "el_lord_chamber_ghost002.htm";
            }
            questState.takeItems(7261, 1L);
            player.teleToLocation(186699, -75915, -2824);
            return "";
        }
        if (n2 == 31452) {
            if (string.equalsIgnoreCase("reply_1")) {
                return "wigoth_ghost_a_q0620_03.htm";
            }
            if (!string.equalsIgnoreCase("reply_2")) return string2;
            questState.takeItems(7260, -1L);
            player.teleToLocation(169584, -91008, -2912);
            return "";
        }
        if (n2 == 31454) {
            if (string.equalsIgnoreCase("reply_1")) {
                if (!questState.isStarted()) return string2;
                if (questState.getQuestItemsCount(7255) < 1L) return string2;
                int n6 = 0;
                while ((long)n6 < questState.getQuestItemsCount(7255)) {
                    if (Rnd.get(100) < 50) {
                        int n7;
                        int n8 = Rnd.get(5);
                        boolean bl = false;
                        if (n8 == 0) {
                            bl = true;
                            questState.giveItems(57, 10000L);
                        } else if (n8 == 1) {
                            if (Rnd.get(1000) < 848) {
                                bl = true;
                                n7 = Rnd.get(1000);
                                if (n7 < 43) {
                                    questState.giveItems(1884, 42L);
                                } else if (n7 < 66) {
                                    questState.giveItems(1895, 36L);
                                } else if (n7 < 184) {
                                    questState.giveItems(1876, 4L);
                                } else if (n7 < 250) {
                                    questState.giveItems(1881, 6L);
                                } else if (n7 < 287) {
                                    questState.giveItems(5549, 8L);
                                } else if (n7 < 484) {
                                    questState.giveItems(1874, 1L);
                                } else if (n7 < 681) {
                                    questState.giveItems(1889, 1L);
                                } else if (n7 < 799) {
                                    questState.giveItems(1877, 1L);
                                } else if (n7 < 902) {
                                    questState.giveItems(1894, 1L);
                                } else {
                                    questState.giveItems(4043, 1L);
                                }
                            }
                            if (Rnd.get(1000) < 323) {
                                bl = true;
                                n7 = Rnd.get(1000);
                                if (n7 < 335) {
                                    questState.giveItems(1888, 1L);
                                } else if (n7 < 556) {
                                    questState.giveItems(4040, 1L);
                                } else if (n7 < 725) {
                                    questState.giveItems(1890, 1L);
                                } else if (n7 < 872) {
                                    questState.giveItems(5550, 1L);
                                } else if (n7 < 962) {
                                    questState.giveItems(1893, 1L);
                                } else if (n7 < 986) {
                                    questState.giveItems(4046, 1L);
                                } else {
                                    questState.giveItems(4048, 1L);
                                }
                            }
                        } else if (n8 == 2) {
                            if (Rnd.get(1000) < 847) {
                                bl = true;
                                n7 = Rnd.get(1000);
                                if (n7 < 148) {
                                    questState.giveItems(1878, 8L);
                                } else if (n7 < 175) {
                                    questState.giveItems(1882, 24L);
                                } else if (n7 < 273) {
                                    questState.giveItems(1879, 4L);
                                } else if (n7 < 322) {
                                    questState.giveItems(1880, 6L);
                                } else if (n7 < 357) {
                                    questState.giveItems(1885, 6L);
                                } else if (n7 < 554) {
                                    questState.giveItems(1875, 1L);
                                } else if (n7 < 685) {
                                    questState.giveItems(1883, 1L);
                                } else if (n7 < 803) {
                                    questState.giveItems(5220, 1L);
                                } else if (n7 < 901) {
                                    questState.giveItems(4039, 1L);
                                } else {
                                    questState.giveItems(4044, 1L);
                                }
                            }
                            if (Rnd.get(1000) < 251) {
                                bl = true;
                                n7 = Rnd.get(1000);
                                if (n7 < 350) {
                                    questState.giveItems(1887, 1L);
                                } else if (n7 < 587) {
                                    questState.giveItems(4042, 1L);
                                } else if (n7 < 798) {
                                    questState.giveItems(1886, 1L);
                                } else if (n7 < 922) {
                                    questState.giveItems(4041, 1L);
                                } else if (n7 < 966) {
                                    questState.giveItems(1892, 1L);
                                } else if (n7 < 996) {
                                    questState.giveItems(1891, 1L);
                                } else {
                                    questState.giveItems(4047, 1L);
                                }
                            }
                        } else if (n8 == 3) {
                            if (Rnd.get(1000) < 31) {
                                bl = true;
                                n7 = Rnd.get(1000);
                                if (n7 < 223) {
                                    questState.giveItems(730, 1L);
                                } else if (n7 < 893) {
                                    questState.giveItems(948, 1L);
                                } else {
                                    questState.giveItems(960, 1L);
                                }
                            }
                            if (Rnd.get(1000) < 5) {
                                bl = true;
                                n7 = Rnd.get(1000);
                                if (n7 < 202) {
                                    questState.giveItems(729, 1L);
                                } else if (n7 < 928) {
                                    questState.giveItems(947, 1L);
                                } else {
                                    questState.giveItems(959, 1L);
                                }
                            }
                        } else if (n8 == 4) {
                            if (Rnd.get(1000) < 329) {
                                bl = true;
                                n7 = Rnd.get(1000);
                                if (n7 < 88) {
                                    questState.giveItems(6698, 1L);
                                } else if (n7 < 185) {
                                    questState.giveItems(6699, 1L);
                                } else if (n7 < 238) {
                                    questState.giveItems(6700, 1L);
                                } else if (n7 < 262) {
                                    questState.giveItems(6701, 1L);
                                } else if (n7 < 292) {
                                    questState.giveItems(6702, 1L);
                                } else if (n7 < 356) {
                                    questState.giveItems(6703, 1L);
                                } else if (n7 < 420) {
                                    questState.giveItems(6704, 1L);
                                } else if (n7 < 482) {
                                    questState.giveItems(6705, 1L);
                                } else if (n7 < 554) {
                                    questState.giveItems(6706, 1L);
                                } else if (n7 < 576) {
                                    questState.giveItems(6707, 1L);
                                } else if (n7 < 640) {
                                    questState.giveItems(6708, 1L);
                                } else if (n7 < 704) {
                                    questState.giveItems(6709, 1L);
                                } else if (n7 < 777) {
                                    questState.giveItems(6710, 1L);
                                } else if (n7 < 799) {
                                    questState.giveItems(6711, 1L);
                                } else if (n7 < 863) {
                                    questState.giveItems(6712, 1L);
                                } else if (n7 < 927) {
                                    questState.giveItems(6713, 1L);
                                } else {
                                    questState.giveItems(6714, 1L);
                                }
                            }
                            if (Rnd.get(1000) < 54) {
                                bl = true;
                                n7 = Rnd.get(1000);
                                if (n7 < 100) {
                                    questState.giveItems(6688, 1L);
                                } else if (n7 < 198) {
                                    questState.giveItems(6689, 1L);
                                } else if (n7 < 298) {
                                    questState.giveItems(6690, 1L);
                                } else if (n7 < 398) {
                                    questState.giveItems(6691, 1L);
                                } else if (n7 < 499) {
                                    questState.giveItems(7579, 1L);
                                } else if (n7 < 601) {
                                    questState.giveItems(6693, 1L);
                                } else if (n7 < 703) {
                                    questState.giveItems(6694, 1L);
                                } else if (n7 < 801) {
                                    questState.giveItems(6695, 1L);
                                } else if (n7 < 902) {
                                    questState.giveItems(6696, 1L);
                                } else {
                                    questState.giveItems(6697, 1L);
                                }
                            }
                        }
                    }
                    questState.takeItems(7255, 1L);
                    if (questState.getQuestItemsCount(7255) == 0L) {
                        string2 = "wigoth_ghost_b_q0620_13.htm";
                    }
                    --n6;
                    ++n6;
                }
                return string2;
            }
            if (string.equalsIgnoreCase("reply_2")) {
                if (!questState.isStarted()) return string2;
                if (n != 2) {
                    if (n != 3) return string2;
                }
                if (questState.getQuestItemsCount(7254) < 1000L) return string2;
                return "wigoth_ghost_b_q0620_16.htm";
            }
            if (string.equalsIgnoreCase("reply_3")) {
                if (!questState.isStarted()) return string2;
                if (n != 2) {
                    if (n != 3) return string2;
                }
                if (questState.getQuestItemsCount(7254) < 1000L) return string2;
                questState.giveItems(6881, 1L);
                questState.takeItems(7254, 1000L);
                return "wigoth_ghost_b_q0620_17.htm";
            }
            if (string.equalsIgnoreCase("reply_4")) {
                if (!questState.isStarted()) return string2;
                if (n != 2) {
                    if (n != 3) return string2;
                }
                if (questState.getQuestItemsCount(7254) < 1000L) return string2;
                questState.giveItems(6883, 1L);
                questState.takeItems(7254, 1000L);
                return "wigoth_ghost_b_q0620_18.htm";
            }
            if (string.equalsIgnoreCase("reply_5")) {
                if (!questState.isStarted()) return string2;
                if (n != 2) {
                    if (n != 3) return string2;
                }
                if (questState.getQuestItemsCount(7254) < 1000L) return string2;
                questState.giveItems(6885, 1L);
                questState.takeItems(7254, 1000L);
                return "wigoth_ghost_b_q0620_19.htm";
            }
            if (string.equalsIgnoreCase("reply_6")) {
                if (!questState.isStarted()) return string2;
                if (n != 2) {
                    if (n != 3) return string2;
                }
                if (questState.getQuestItemsCount(7254) < 1000L) return string2;
                questState.giveItems(6887, 1L);
                questState.takeItems(7254, 1000L);
                return "wigoth_ghost_b_q0620_20.htm";
            }
            if (string.equalsIgnoreCase("reply_7")) {
                if (!questState.isStarted()) return string2;
                if (n != 2) {
                    if (n != 3) return string2;
                }
                if (questState.getQuestItemsCount(7254) < 1000L) return string2;
                questState.giveItems(7580, 1L);
                questState.takeItems(7254, 1000L);
                return "wigoth_ghost_b_q0620_21.htm";
            }
            if (string.equalsIgnoreCase("reply_8")) {
                if (!questState.isStarted()) return string2;
                if (n != 2) {
                    if (n != 3) return string2;
                }
                if (questState.getQuestItemsCount(7254) < 1000L) return string2;
                questState.giveItems(6891, 1L);
                questState.takeItems(7254, 1000L);
                return "wigoth_ghost_b_q0620_22.htm";
            }
            if (string.equalsIgnoreCase("reply_9")) {
                if (!questState.isStarted()) return string2;
                if (n != 2) {
                    if (n != 3) return string2;
                }
                if (questState.getQuestItemsCount(7254) < 1000L) return string2;
                questState.giveItems(6893, 1L);
                questState.takeItems(7254, 1000L);
                return "wigoth_ghost_b_q0620_23.htm";
            }
            if (string.equalsIgnoreCase("reply_10")) {
                if (!questState.isStarted()) return string2;
                if (n != 2) {
                    if (n != 3) return string2;
                }
                if (questState.getQuestItemsCount(7254) < 1000L) return string2;
                questState.giveItems(6895, 1L);
                questState.takeItems(7254, 1000L);
                return "wigoth_ghost_b_q0620_24.htm";
            }
            if (string.equalsIgnoreCase("reply_11")) {
                if (!questState.isStarted()) return string2;
                if (n != 2) {
                    if (n != 3) return string2;
                }
                if (questState.getQuestItemsCount(7254) < 1000L) return string2;
                questState.giveItems(6897, 1L);
                questState.takeItems(7254, 1000L);
                return "wigoth_ghost_b_q0620_25.htm";
            }
            if (string.equalsIgnoreCase("reply_12")) {
                if (!questState.isStarted()) return string2;
                if (n != 2) {
                    if (n != 3) return string2;
                }
                if (questState.getQuestItemsCount(7254) < 1000L) return string2;
                questState.giveItems(6899, 1L);
                questState.takeItems(7254, 1000L);
                return "wigoth_ghost_b_q0620_26.htm";
            }
            if (!string.equalsIgnoreCase("reply_13")) return string2;
            if (!questState.isStarted()) return string2;
            player.teleToLocation(170000, -88250, -2912);
            return "wigoth_ghost_b_q0620_01a.htm";
        }
        if (n2 == 31921) {
            QuestState questState3;
            if (!string.equalsIgnoreCase("reply_1")) return string2;
            if (!questState.isStarted()) return string2;
            int n9 = Calendar.getInstance().get(12);
            Party party = player.getParty();
            if (n9 >= 0 && n9 < 55) {
                return "conquerors_keeper_q0620_02.htm";
            }
            if (party == null) return "conquerors_keeper_q0620_04.htm";
            if (party.getMemberCount() < Config.FOUR_SEPULCHER_MIN_PARTY_MEMBERS) {
                return "conquerors_keeper_q0620_04.htm";
            }
            if (player != party.getPartyLeader()) {
                return "conquerors_keeper_q0620_03.htm";
            }
            for (Player player2 : party.getPartyMembers()) {
                questState3 = player2.getQuestState(_620_FourGoblets.class);
                if (player2.getInventory().getCountOf(7075) == 0L) {
                    string2 = HtmCache.getInstance().getNotNull("quests/_620_FourGoblets/conquerors_keeper_q0620_05.htm", questState.getPlayer());
                    return string2.replace("<?member1?>", player2.getName());
                }
                if (questState3 != null && (questState3.isStarted() || questState3.isCompleted())) continue;
                string2 = HtmCache.getInstance().getNotNull("quests/_620_FourGoblets/conquerors_keeper_q0620_06.htm", questState.getPlayer());
                return string2.replace("<?member1?>", player2.getName());
            }
            if (!npcInstance.av_quest0.compareAndSet(0, 1)) {
                return "conquerors_keeper_q0620_07.htm";
            }
            for (Player player2 : party.getPartyMembers()) {
                questState3 = player2.getQuestState(_620_FourGoblets.class);
                if (questState3.getQuestItemsCount(7262) == 0L) {
                    questState3.giveItems(7261, 1L);
                }
                questState3.takeItems(7075, 1L);
                questState3.set("four_goblets_ex", String.valueOf(1), true);
                questState3.takeItems(7260, -1L);
            }
            npcInstance.teleportParty(party, 181528, -85583, -7216, 1000, 0);
            return "";
        }
        if (n2 == 31922) {
            QuestState questState4;
            if (!string.equalsIgnoreCase("reply_1")) return string2;
            if (!questState.isStarted()) return string2;
            int n10 = Calendar.getInstance().get(12);
            Party party = player.getParty();
            if (n10 >= 0 && n10 < 55) {
                return "lords_keeper_q0620_02.htm";
            }
            if (party == null) return "lords_keeper_q0620_04.htm";
            if (party.getMemberCount() < Config.FOUR_SEPULCHER_MIN_PARTY_MEMBERS) {
                return "lords_keeper_q0620_04.htm";
            }
            if (player != party.getPartyLeader()) {
                return "lords_keeper_q0620_03.htm";
            }
            for (Player player3 : party.getPartyMembers()) {
                questState4 = player3.getQuestState(_620_FourGoblets.class);
                if (player3.getInventory().getCountOf(7075) == 0L) {
                    string2 = HtmCache.getInstance().getNotNull("quests/_620_FourGoblets/lords_keeper_q0620_05.htm", questState.getPlayer());
                    return string2.replace("<?member1?>", player3.getName());
                }
                if (questState4 != null && (questState4.isStarted() || questState4.isCompleted())) continue;
                string2 = HtmCache.getInstance().getNotNull("quests/_620_FourGoblets/lords_keeper_q0620_06.htm", questState.getPlayer());
                return string2.replace("<?member1?>", player3.getName());
            }
            if (!npcInstance.av_quest0.compareAndSet(0, 1)) {
                return "lords_keeper_q0620_07.htm";
            }
            for (Player player3 : party.getPartyMembers()) {
                questState4 = player3.getQuestState(_620_FourGoblets.class);
                if (questState4.getQuestItemsCount(7262) == 0L) {
                    questState4.giveItems(7261, 1L);
                }
                questState4.takeItems(7075, 1L);
                questState4.set("four_goblets_ex", String.valueOf(1), true);
                questState4.takeItems(7260, -1L);
            }
            npcInstance.teleportParty(party, 179849, -88990, -7216, 1000, 0);
            return "";
        }
        if (n2 == 31923) {
            QuestState questState5;
            if (!string.equalsIgnoreCase("reply_1")) return string2;
            if (!questState.isStarted()) return string2;
            int n11 = Calendar.getInstance().get(12);
            Party party = player.getParty();
            if (n11 >= 0 && n11 < 55) {
                return "savants_keeper_q0620_02.htm";
            }
            if (party == null) return "savants_keeper_q0620_04.htm";
            if (party.getMemberCount() < Config.FOUR_SEPULCHER_MIN_PARTY_MEMBERS) {
                return "savants_keeper_q0620_04.htm";
            }
            if (player != party.getPartyLeader()) {
                return "savants_keeper_q0620_03.htm";
            }
            for (Player player4 : party.getPartyMembers()) {
                questState5 = player4.getQuestState(_620_FourGoblets.class);
                if (player4.getInventory().getCountOf(7075) == 0L) {
                    string2 = HtmCache.getInstance().getNotNull("quests/_620_FourGoblets/savants_keeper_q0620_05.htm", questState.getPlayer());
                    return string2.replace("<?member1?>", player4.getName());
                }
                if (questState5 != null && (questState5.isStarted() || questState5.isCompleted())) continue;
                string2 = HtmCache.getInstance().getNotNull("quests/_620_FourGoblets/savants_keeper_q0620_06.htm", questState.getPlayer());
                return string2.replace("<?member1?>", player4.getName());
            }
            if (!npcInstance.av_quest0.compareAndSet(0, 1)) {
                return "savants_keeper_q0620_07.htm";
            }
            for (Player player4 : party.getPartyMembers()) {
                questState5 = player4.getQuestState(_620_FourGoblets.class);
                if (questState5.getQuestItemsCount(7262) == 0L) {
                    questState5.giveItems(7261, 1L);
                }
                questState5.takeItems(7075, 1L);
                questState5.set("four_goblets_ex", String.valueOf(1), true);
                questState5.takeItems(7260, -1L);
            }
            npcInstance.teleportParty(party, 173216, -86195, -7216, 1000, 0);
            return "";
        }
        if (n2 != 31924) return string2;
        if (!string.equalsIgnoreCase("reply_1")) return string2;
        if (!questState.isStarted()) return string2;
        int n12 = Calendar.getInstance().get(12);
        Party party = player.getParty();
        if (n12 >= 0 && n12 < 55) {
            return "magistrates_keeper_q0620_02.htm";
        }
        if (party == null) return "magistrates_keeper_q0620_04.htm";
        if (party.getMemberCount() < Config.FOUR_SEPULCHER_MIN_PARTY_MEMBERS) {
            return "magistrates_keeper_q0620_04.htm";
        }
        if (player != party.getPartyLeader()) {
            return "magistrates_keeper_q0620_03.htm";
        }
        for (Player player5 : party.getPartyMembers()) {
            questState2 = player5.getQuestState(_620_FourGoblets.class);
            if (player5.getInventory().getCountOf(7075) == 0L) {
                string2 = HtmCache.getInstance().getNotNull("quests/_620_FourGoblets/magistrates_keeper_q0620_05.htm", questState.getPlayer());
                return string2.replace("<?member1?>", player5.getName());
            }
            if (questState2 != null && (questState2.isStarted() || questState2.isCompleted())) continue;
            string2 = HtmCache.getInstance().getNotNull("quests/_620_FourGoblets/magistrates_keeper_q0620_06.htm", questState.getPlayer());
            return string2.replace("<?member1?>", player5.getName());
        }
        if (!npcInstance.av_quest0.compareAndSet(0, 1)) {
            return "magistrates_keeper_q0620_07.htm";
        }
        for (Player player5 : party.getPartyMembers()) {
            questState2 = player5.getQuestState(_620_FourGoblets.class);
            if (questState2.getQuestItemsCount(7262) == 0L) {
                questState2.giveItems(7261, 1L);
            }
            questState2.takeItems(7075, 1L);
            questState2.set("four_goblets_ex", String.valueOf(1), true);
            questState2.takeItems(7260, -1L);
        }
        npcInstance.teleportParty(party, 175615, -82365, -7216, 1000, 0);
        return "";
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        QuestState questState2;
        Player player = questState.getPlayer();
        int n = npcInstance.getNpcId();
        if (n == 25339 || n == 25346 || n == 25342 || n == 25349) {
            for (QuestState questState3 : this.getPartyMembersWithQuest(player, -1)) {
                if (questState3.getQuestItemsCount(7262) == 0L) {
                    if (n == 25339 && questState3.getQuestItemsCount(7256) == 0L) {
                        questState3.giveItems(7256, 1L);
                    } else if (n == 25342 && questState3.getQuestItemsCount(7257) == 0L) {
                        questState3.giveItems(7257, 1L);
                    } else if (n == 25346 && questState3.getQuestItemsCount(7258) == 0L) {
                        questState3.giveItems(7258, 1L);
                    } else if (n == 25349 && questState3.getQuestItemsCount(7259) == 0L) {
                        questState3.giveItems(7259, 1L);
                    }
                }
                questState3.set("four_goblets_ex", String.valueOf(2), true);
            }
        } else if (bPc.containsKey(n)) {
            QuestState questState4 = this.getRandomPartyMemberWithQuest(player, -1);
            if (questState4 != null && questState4.rollAndGive(7255, Rnd.chance(bPc.get(n)) ? 2 : 1, 100.0)) {
                questState4.playSound("ItemSound.quest_itemget");
            }
        } else if (bPd.containsKey(n)) {
            QuestState questState5 = this.getRandomPartyMemberWithQuest(player, -1);
            if (questState5 != null && questState5.rollAndGive(7255, 1, bPd.get(n).intValue())) {
                questState5.playSound("ItemSound.quest_itemget");
            }
        } else if (bPe.containsKey(n) && (questState2 = this.getRandomPartyMemberWithQuest(player, -1)) != null && questState2.rollAndGive(7255, Rnd.chance(bPe.get(n)) ? 5 : 4, 100.0)) {
            questState2.playSound("ItemSound.quest_itemget");
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

    static {
        bPc.put(18120, 51);
        bPc.put(18122, 10);
        bPc.put(18121, 44);
        bPc.put(18123, 51);
        bPc.put(18125, 10);
        bPc.put(18124, 44);
        bPc.put(18126, 51);
        bPc.put(18128, 10);
        bPc.put(18127, 44);
        bPc.put(18129, 51);
        bPc.put(18131, 10);
        bPc.put(18130, 44);
        bPc.put(18132, 54);
        bPc.put(18133, 42);
        bPc.put(18134, 7);
        bPc.put(18135, 42);
        bPc.put(18136, 42);
        bPc.put(18138, 41);
        bPc.put(18139, 39);
        bPc.put(18137, 6);
        bPc.put(18140, 41);
        bPc.put(18166, 8);
        bPc.put(18167, 7);
        bPc.put(18168, 10);
        bPc.put(18169, 6);
        bPc.put(18171, 11);
        bPc.put(18170, 7);
        bPc.put(18172, 6);
        bPc.put(18173, 17);
        bPc.put(18175, 10);
        bPc.put(18174, 45);
        bPc.put(18176, 17);
        bPc.put(18178, 10);
        bPc.put(18177, 45);
        bPc.put(18179, 17);
        bPc.put(18181, 10);
        bPc.put(18180, 45);
        bPc.put(18182, 17);
        bPc.put(18184, 10);
        bPc.put(18183, 45);
        bPc.put(18195, 8);
        bPc.put(18185, 46);
        bPc.put(18186, 47);
        bPc.put(18187, 42);
        bPc.put(18188, 7);
        bPc.put(18189, 42);
        bPc.put(18190, 42);
        bPc.put(18192, 41);
        bPc.put(18193, 39);
        bPc.put(18191, 6);
        bPc.put(18194, 41);
        bPc.put(18220, 47);
        bPc.put(18221, 51);
        bPc.put(18222, 43);
        bPc.put(18223, 7);
        bPc.put(18224, 44);
        bPc.put(18225, 43);
        bPc.put(18227, 82);
        bPc.put(18228, 36);
        bPc.put(18226, 6);
        bPc.put(18229, 41);
        bPd.put(18141, 90);
        bPd.put(18142, 90);
        bPd.put(18143, 90);
        bPd.put(18144, 90);
        bPd.put(18149, 75);
        bPd.put(18148, 85);
        bPd.put(18146, 78);
        bPd.put(18147, 73);
        bPd.put(18145, 76);
        bPd.put(18230, 58);
        bPe.put(18212, 50);
        bPe.put(18213, 50);
        bPe.put(18214, 50);
        bPe.put(18215, 50);
        bPe.put(18216, 50);
        bPe.put(18217, 50);
        bPe.put(18218, 50);
        bPe.put(18219, 50);
    }
}

