/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.data.htm.HtmCache;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _662_AGameOfCards
extends Quest
implements ScriptFile {
    private static final int bUv = 30845;
    private static final int brp = 20672;
    private static final int brq = 20673;
    private static final int bUw = 20674;
    private static final int bUx = 20677;
    private static final int bUy = 20955;
    private static final int bUz = 20958;
    private static final int bUA = 20959;
    private static final int bUB = 20961;
    private static final int bUC = 20962;
    private static final int bbm = 20965;
    private static final int bUD = 20966;
    private static final int bUE = 20968;
    private static final int bUF = 20972;
    private static final int bUG = 20973;
    private static final int bUH = 21002;
    private static final int bUI = 21004;
    private static final int bUJ = 21006;
    private static final int bUK = 21008;
    private static final int bUL = 21010;
    private static final int bUM = 21109;
    private static final int bUN = 21112;
    private static final int bUO = 21114;
    private static final int bUP = 21116;
    private static final int bUQ = 21278;
    private static final int bUR = 21279;
    private static final int bUS = 21280;
    private static final int bUT = 21286;
    private static final int bUU = 21287;
    private static final int bUV = 21288;
    private static final int bbK = 21508;
    private static final int bbM = 21510;
    private static final int bQg = 21515;
    private static final int bDX = 21520;
    private static final int bEc = 21526;
    private static final int bDY = 21530;
    private static final int bPY = 21535;
    private static final int bUW = 18001;
    private static final int bQK = 959;
    private static final int aGI = 729;
    private static final int bUX = 947;
    private static final int bUY = 951;
    private static final int bUo = 955;
    private static final int azF = 956;
    private static final int bUZ = 8765;
    private static final int bVa = 8868;

    public _662_AGameOfCards() {
        super(1);
        this.addStartNpc(30845);
        this.addKillId(20672, 20673, 20674, 20677, 20955, 20958, 20959, 20961, 20962, 20965, 20966, 20968, 20972, 20973, 21002, 21004, 21006, 21008, 21010, 21109, 21112, 21114, 21116, 21278, 21279, 21280, 21286, 21287, 21288, 21508, 21510, 21515, 21520, 21526, 21530, 21535, 18001);
        this.addQuestItem(8765);
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
        int n = questState.getInt("lets_do_card_game");
        int n2 = questState.getInt("lets_do_card_game_ex");
        int n3 = npcInstance.getNpcId();
        if (n3 == 30845) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "warehouse_chief_klump_q0662_03.htm";
            } else if (string.equalsIgnoreCase("reply_1")) {
                string2 = "warehouse_chief_klump_q0662_06.htm";
            } else if (string.equalsIgnoreCase("reply_2")) {
                questState.unset("lets_do_card_game");
                questState.unset("lets_do_card_game_ex");
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                string2 = "warehouse_chief_klump_q0662_07.htm";
            } else if (string.equalsIgnoreCase("reply_3")) {
                string2 = "warehouse_chief_klump_q0662_08.htm";
            } else if (string.equalsIgnoreCase("reply_4")) {
                string2 = "warehouse_chief_klump_q0662_09.htm";
            } else if (string.equalsIgnoreCase("reply_6")) {
                string2 = "warehouse_chief_klump_q0662_09a.htm";
            } else if (string.equalsIgnoreCase("reply_8")) {
                string2 = "warehouse_chief_klump_q0662_09b.htm";
            } else if (string.equalsIgnoreCase("reply_7")) {
                if (n == 0 && n2 == 0 && questState.getQuestItemsCount(8765) < 50L) {
                    string2 = "warehouse_chief_klump_q0662_04.htm";
                } else if (n == 0 && n2 == 0 && questState.getQuestItemsCount(8765) >= 50L) {
                    string2 = "warehouse_chief_klump_q0662_05.htm";
                }
            } else if (string.equalsIgnoreCase("reply_5")) {
                string2 = "warehouse_chief_klump_q0662_10.htm";
            } else if (string.equalsIgnoreCase("reply_10")) {
                if (n == 0 && n2 == 0 && questState.getQuestItemsCount(8765) >= 50L) {
                    int n4 = 0;
                    int n5 = 0;
                    int n6 = 0;
                    int n7 = 0;
                    int n8 = 0;
                    while (n4 == n5 || n4 == n6 || n4 == n7 || n4 == n8 || n5 == n6 || n5 == n7 || n5 == n8 || n6 == n7 || n6 == n8 || n7 == n8) {
                        n4 = Rnd.get(70) + 1;
                        n5 = Rnd.get(70) + 1;
                        n6 = Rnd.get(70) + 1;
                        n7 = Rnd.get(70) + 1;
                        n8 = Rnd.get(70) + 1;
                    }
                    if (n4 >= 57) {
                        n4 -= 56;
                    } else if (n4 >= 43) {
                        n4 -= 42;
                    } else if (n4 >= 29) {
                        n4 -= 28;
                    } else if (n4 >= 15) {
                        n4 -= 14;
                    }
                    if (n5 >= 57) {
                        n5 -= 56;
                    } else if (n5 >= 43) {
                        n5 -= 42;
                    } else if (n5 >= 29) {
                        n5 -= 28;
                    } else if (n5 >= 15) {
                        n5 -= 14;
                    }
                    if (n6 >= 57) {
                        n6 -= 56;
                    } else if (n6 >= 43) {
                        n6 -= 42;
                    } else if (n6 >= 29) {
                        n6 -= 28;
                    } else if (n6 >= 15) {
                        n6 -= 14;
                    }
                    if (n7 >= 57) {
                        n7 -= 56;
                    } else if (n7 >= 43) {
                        n7 -= 42;
                    } else if (n7 >= 29) {
                        n7 -= 28;
                    } else if (n7 >= 15) {
                        n7 -= 14;
                    }
                    if (n8 >= 57) {
                        n8 -= 56;
                    } else if (n8 >= 43) {
                        n8 -= 42;
                    } else if (n8 >= 29) {
                        n8 -= 28;
                    } else if (n8 >= 15) {
                        n8 -= 14;
                    }
                    questState.set("lets_do_card_game", String.valueOf(n7 * 1000000 + n6 * 10000 + n5 * 100 + n4), true);
                    questState.set("lets_do_card_game_ex", String.valueOf(n8), true);
                    questState.takeItems(8765, 50L);
                    string2 = "warehouse_chief_klump_q0662_11.htm";
                }
            } else if (string.equalsIgnoreCase("reply_11") || string.equalsIgnoreCase("reply_12") || string.equalsIgnoreCase("reply_13") || string.equalsIgnoreCase("reply_14") || string.equalsIgnoreCase("reply_15")) {
                int n9 = n;
                int n10 = n2;
                int n11 = n10 % 100;
                int n12 = n10 / 100;
                n10 = n9 % 100;
                int n13 = n9 % 10000 / 100;
                int n14 = n9 % 1000000 / 10000;
                int n15 = n9 % 100000000 / 1000000;
                if (string.equalsIgnoreCase("reply_11")) {
                    if (n12 % 2 < 1) {
                        ++n12;
                    }
                    if (n12 % 32 < 31) {
                        questState.set("lets_do_card_game_ex", String.valueOf(n12 * 100 + n11), true);
                    }
                } else if (string.equalsIgnoreCase("reply_12")) {
                    if (n12 % 4 < 2) {
                        n12 += 2;
                    }
                    if (n12 % 32 < 31) {
                        questState.set("lets_do_card_game_ex", String.valueOf(n12 * 100 + n11), true);
                    }
                } else if (string.equalsIgnoreCase("reply_13")) {
                    if (n12 % 8 < 4) {
                        n12 += 4;
                    }
                    if (n12 % 32 < 31) {
                        questState.set("lets_do_card_game_ex", String.valueOf(n12 * 100 + n11), true);
                    }
                } else if (string.equalsIgnoreCase("reply_14")) {
                    if (n12 % 16 < 8) {
                        n12 += 8;
                    }
                    if (n12 % 32 < 31) {
                        questState.set("lets_do_card_game_ex", String.valueOf(n12 * 100 + n11), true);
                    }
                } else if (string.equalsIgnoreCase("reply_15")) {
                    if (n12 % 32 < 16) {
                        n12 += 16;
                    }
                    if (n12 % 32 < 31) {
                        questState.set("lets_do_card_game_ex", String.valueOf(n12 * 100 + n11), true);
                    }
                }
                if (n12 % 32 < 31) {
                    string2 = HtmCache.getInstance().getNotNull("quests/_662_AGameOfCards/warehouse_chief_klump_q0662_12.htm", questState.getPlayer());
                } else if (n12 % 32 == 31) {
                    int n16 = 0;
                    int n17 = 0;
                    if (n10 >= 1 && n10 <= 14 && n13 >= 1 && n13 <= 14 && n14 >= 1 && n14 <= 14 && n15 >= 1 && n15 <= 14 && n11 >= 1 && n11 <= 14) {
                        if (n10 == n13) {
                            n16 += 10;
                            n17 += 8;
                        }
                        if (n10 == n14) {
                            n16 += 10;
                            n17 += 4;
                        }
                        if (n10 == n15) {
                            n16 += 10;
                            n17 += 2;
                        }
                        if (n10 == n11) {
                            n16 += 10;
                            ++n17;
                        }
                        if (n16 % 100 < 10) {
                            if (n17 % 16 < 8) {
                                if (n17 % 8 < 4 && n13 == n14) {
                                    n16 += 10;
                                    n17 += 4;
                                }
                                if (n17 % 4 < 2 && n13 == n15) {
                                    n16 += 10;
                                    n17 += 2;
                                }
                                if (n17 % 2 < 1 && n13 == n11) {
                                    n16 += 10;
                                    ++n17;
                                }
                            }
                        } else if (n16 % 10 == 0 && n17 % 16 < 8) {
                            if (n17 % 8 < 4 && n13 == n14) {
                                ++n16;
                                n17 += 4;
                            }
                            if (n17 % 4 < 2 && n13 == n15) {
                                ++n16;
                                n17 += 2;
                            }
                            if (n17 % 2 < 1 && n13 == n11) {
                                ++n16;
                                ++n17;
                            }
                        }
                        if (n16 % 100 < 10) {
                            if (n17 % 8 < 4) {
                                if (n17 % 4 < 2 && n14 == n15) {
                                    n16 += 10;
                                    n17 += 2;
                                }
                                if (n17 % 2 < 1 && n14 == n11) {
                                    n16 += 10;
                                    ++n17;
                                }
                            }
                        } else if (n16 % 10 == 0 && n17 % 8 < 4) {
                            if (n17 % 4 < 2 && n14 == n15) {
                                ++n16;
                                n17 += 2;
                            }
                            if (n17 % 2 < 1 && n14 == n11) {
                                ++n16;
                                ++n17;
                            }
                        }
                        if (n16 % 100 < 10) {
                            if (n17 % 4 < 2 && n17 % 2 < 1 && n15 == n11) {
                                n16 += 10;
                                ++n17;
                            }
                        } else if (n16 % 10 == 0 && n17 % 4 < 2 && n17 % 2 < 1 && n15 == n11) {
                            ++n16;
                            ++n17;
                        }
                    }
                    if (n16 == 40) {
                        string2 = HtmCache.getInstance().getNotNull("quests/_662_AGameOfCards/warehouse_chief_klump_q0662_13.htm", questState.getPlayer());
                        questState.set("lets_do_card_game", String.valueOf(0), true);
                        questState.set("lets_do_card_game_ex", String.valueOf(0), true);
                        questState.giveItems(8868, 43L);
                        questState.giveItems(959, 3L);
                        questState.giveItems(729, 1L);
                    } else if (n16 == 30) {
                        string2 = HtmCache.getInstance().getNotNull("quests/_662_AGameOfCards/warehouse_chief_klump_q0662_14.htm", questState.getPlayer());
                        questState.set("lets_do_card_game", String.valueOf(0), true);
                        questState.set("lets_do_card_game_ex", String.valueOf(0), true);
                        questState.giveItems(959, 2L);
                        questState.giveItems(951, 2L);
                    } else if (n16 == 21 || n16 == 12) {
                        string2 = HtmCache.getInstance().getNotNull("quests/_662_AGameOfCards/warehouse_chief_klump_q0662_15.htm", questState.getPlayer());
                        questState.set("lets_do_card_game", String.valueOf(0), true);
                        questState.set("lets_do_card_game_ex", String.valueOf(0), true);
                        questState.giveItems(729, 1L);
                        questState.giveItems(947, 2L);
                        questState.giveItems(955, 1L);
                    } else if (n16 == 20) {
                        string2 = HtmCache.getInstance().getNotNull("quests/_662_AGameOfCards/warehouse_chief_klump_q0662_16.htm", questState.getPlayer());
                        questState.set("lets_do_card_game", String.valueOf(0), true);
                        questState.set("lets_do_card_game_ex", String.valueOf(0), true);
                        questState.giveItems(951, 2L);
                    } else if (n16 == 11) {
                        string2 = HtmCache.getInstance().getNotNull("quests/_662_AGameOfCards/warehouse_chief_klump_q0662_17.htm", questState.getPlayer());
                        questState.set("lets_do_card_game", String.valueOf(0), true);
                        questState.set("lets_do_card_game_ex", String.valueOf(0), true);
                        questState.giveItems(951, 1L);
                    } else if (n16 == 10) {
                        string2 = HtmCache.getInstance().getNotNull("quests/_662_AGameOfCards/warehouse_chief_klump_q0662_18.htm", questState.getPlayer());
                        questState.set("lets_do_card_game", String.valueOf(0), true);
                        questState.set("lets_do_card_game_ex", String.valueOf(0), true);
                        questState.giveItems(956, 2L);
                    } else if (n16 == 0) {
                        string2 = HtmCache.getInstance().getNotNull("quests/_662_AGameOfCards/warehouse_chief_klump_q0662_19.htm", questState.getPlayer());
                        questState.set("lets_do_card_game", String.valueOf(0), true);
                        questState.set("lets_do_card_game_ex", String.valueOf(0), true);
                    }
                }
                if (n12 % 2 < 1) {
                    string2 = string2.replace("<?FontColor1?>", "ffff00");
                    string2 = string2.replace("<?Cell1?>", "?");
                } else {
                    string2 = string2.replace("<?FontColor1?>", "ff6f6f");
                    if (n10 == 1) {
                        string2 = string2.replace("<?Cell1?>", "!");
                    } else if (n10 == 2) {
                        string2 = string2.replace("<?Cell1?>", "=");
                    } else if (n10 == 3) {
                        string2 = string2.replace("<?Cell1?>", "T");
                    } else if (n10 == 4) {
                        string2 = string2.replace("<?Cell1?>", "V");
                    } else if (n10 == 5) {
                        string2 = string2.replace("<?Cell1?>", "O");
                    } else if (n10 == 6) {
                        string2 = string2.replace("<?Cell1?>", "P");
                    } else if (n10 == 7) {
                        string2 = string2.replace("<?Cell1?>", "S");
                    } else if (n10 == 8) {
                        string2 = string2.replace("<?Cell1?>", "E");
                    } else if (n10 == 9) {
                        string2 = string2.replace("<?Cell1?>", "H");
                    } else if (n10 == 10) {
                        string2 = string2.replace("<?Cell1?>", "A");
                    } else if (n10 == 11) {
                        string2 = string2.replace("<?Cell1?>", "R");
                    } else if (n10 == 12) {
                        string2 = string2.replace("<?Cell1?>", "D");
                    } else if (n10 == 13) {
                        string2 = string2.replace("<?Cell1?>", "I");
                    } else if (n10 == 14) {
                        string2 = string2.replace("<?Cell1?>", "N");
                    }
                }
                if (n12 % 4 < 2) {
                    string2 = string2.replace("<?FontColor2?>", "ffff00");
                    string2 = string2.replace("<?Cell2?>", "?");
                } else {
                    string2 = string2.replace("<?FontColor2?>", "ff6f6f");
                    if (n13 == 1) {
                        string2 = string2.replace("<?Cell2?>", "!");
                    } else if (n13 == 2) {
                        string2 = string2.replace("<?Cell2?>", "=");
                    } else if (n13 == 3) {
                        string2 = string2.replace("<?Cell2?>", "T");
                    } else if (n13 == 4) {
                        string2 = string2.replace("<?Cell2?>", "V");
                    } else if (n13 == 5) {
                        string2 = string2.replace("<?Cell2?>", "O");
                    } else if (n13 == 6) {
                        string2 = string2.replace("<?Cell2?>", "P");
                    } else if (n13 == 7) {
                        string2 = string2.replace("<?Cell2?>", "S");
                    } else if (n13 == 8) {
                        string2 = string2.replace("<?Cell2?>", "E");
                    } else if (n13 == 9) {
                        string2 = string2.replace("<?Cell2?>", "H");
                    } else if (n13 == 10) {
                        string2 = string2.replace("<?Cell2?>", "A");
                    } else if (n13 == 11) {
                        string2 = string2.replace("<?Cell2?>", "R");
                    } else if (n13 == 12) {
                        string2 = string2.replace("<?Cell2?>", "D");
                    } else if (n13 == 13) {
                        string2 = string2.replace("<?Cell2?>", "I");
                    } else if (n13 == 14) {
                        string2 = string2.replace("<?Cell2?>", "N");
                    }
                }
                if (n12 % 8 < 4) {
                    string2 = string2.replace("<?FontColor3?>", "ffff00");
                    string2 = string2.replace("<?Cell3?>", "?");
                } else {
                    string2 = string2.replace("<?FontColor3?>", "ff6f6f");
                    if (n14 == 1) {
                        string2 = string2.replace("<?Cell3?>", "!");
                    } else if (n14 == 2) {
                        string2 = string2.replace("<?Cell3?>", "=");
                    } else if (n14 == 3) {
                        string2 = string2.replace("<?Cell3?>", "T");
                    } else if (n14 == 4) {
                        string2 = string2.replace("<?Cell3?>", "V");
                    } else if (n14 == 5) {
                        string2 = string2.replace("<?Cell3?>", "O");
                    } else if (n14 == 6) {
                        string2 = string2.replace("<?Cell3?>", "P");
                    } else if (n14 == 7) {
                        string2 = string2.replace("<?Cell3?>", "S");
                    } else if (n14 == 8) {
                        string2 = string2.replace("<?Cell3?>", "E");
                    } else if (n14 == 9) {
                        string2 = string2.replace("<?Cell3?>", "H");
                    } else if (n14 == 10) {
                        string2 = string2.replace("<?Cell3?>", "A");
                    } else if (n14 == 11) {
                        string2 = string2.replace("<?Cell3?>", "R");
                    } else if (n14 == 12) {
                        string2 = string2.replace("<?Cell3?>", "D");
                    } else if (n14 == 13) {
                        string2 = string2.replace("<?Cell3?>", "I");
                    } else if (n14 == 14) {
                        string2 = string2.replace("<?Cell3?>", "N");
                    }
                }
                if (n12 % 16 < 8) {
                    string2 = string2.replace("<?FontColor4?>", "ffff00");
                    string2 = string2.replace("<?Cell4?>", "?");
                } else {
                    string2 = string2.replace("<?FontColor4?>", "ff6f6f");
                    if (n15 == 1) {
                        string2 = string2.replace("<?Cell4?>", "!");
                    } else if (n15 == 2) {
                        string2 = string2.replace("<?Cell4?>", "=");
                    } else if (n15 == 3) {
                        string2 = string2.replace("<?Cell4?>", "T");
                    } else if (n15 == 4) {
                        string2 = string2.replace("<?Cell4?>", "V");
                    } else if (n15 == 5) {
                        string2 = string2.replace("<?Cell4?>", "O");
                    } else if (n15 == 6) {
                        string2 = string2.replace("<?Cell4?>", "P");
                    } else if (n15 == 7) {
                        string2 = string2.replace("<?Cell4?>", "S");
                    } else if (n15 == 8) {
                        string2 = string2.replace("<?Cell4?>", "E");
                    } else if (n15 == 9) {
                        string2 = string2.replace("<?Cell4?>", "H");
                    } else if (n15 == 10) {
                        string2 = string2.replace("<?Cell4?>", "A");
                    } else if (n15 == 11) {
                        string2 = string2.replace("<?Cell4?>", "R");
                    } else if (n15 == 12) {
                        string2 = string2.replace("<?Cell4?>", "D");
                    } else if (n15 == 13) {
                        string2 = string2.replace("<?Cell4?>", "I");
                    } else if (n15 == 14) {
                        string2 = string2.replace("<?Cell4?>", "N");
                    }
                }
                if (n12 % 32 < 16) {
                    string2 = string2.replace("<?FontColor5?>", "ffff00");
                    string2 = string2.replace("<?Cell5?>", "?");
                } else {
                    string2 = string2.replace("<?FontColor5?>", "ff6f6f");
                    if (n11 == 1) {
                        string2 = string2.replace("<?Cell5?>", "!");
                    } else if (n11 == 2) {
                        string2 = string2.replace("<?Cell5?>", "=");
                    } else if (n11 == 3) {
                        string2 = string2.replace("<?Cell5?>", "T");
                    } else if (n11 == 4) {
                        string2 = string2.replace("<?Cell5?>", "V");
                    } else if (n11 == 5) {
                        string2 = string2.replace("<?Cell5?>", "O");
                    } else if (n11 == 6) {
                        string2 = string2.replace("<?Cell5?>", "P");
                    } else if (n11 == 7) {
                        string2 = string2.replace("<?Cell5?>", "S");
                    } else if (n11 == 8) {
                        string2 = string2.replace("<?Cell5?>", "E");
                    } else if (n11 == 9) {
                        string2 = string2.replace("<?Cell5?>", "H");
                    } else if (n11 == 10) {
                        string2 = string2.replace("<?Cell5?>", "A");
                    } else if (n11 == 11) {
                        string2 = string2.replace("<?Cell5?>", "R");
                    } else if (n11 == 12) {
                        string2 = string2.replace("<?Cell5?>", "D");
                    } else if (n11 == 13) {
                        string2 = string2.replace("<?Cell5?>", "I");
                    } else if (n11 == 14) {
                        string2 = string2.replace("<?Cell5?>", "N");
                    }
                }
            } else if (string.equalsIgnoreCase("reply_20")) {
                if (questState.getQuestItemsCount(8765) >= 50L) {
                    string2 = "warehouse_chief_klump_q0662_20.htm";
                } else if (questState.getQuestItemsCount(8765) < 50L) {
                    string2 = "warehouse_chief_klump_q0662_21.htm";
                }
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("lets_do_card_game");
        int n2 = questState.getInt("lets_do_card_game_ex");
        int n3 = npcInstance.getNpcId();
        int n4 = questState.getState();
        switch (n4) {
            case 1: {
                if (n3 != 30845) break;
                if (questState.getPlayer().getLevel() < 61) {
                    string = "warehouse_chief_klump_q0662_02.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                string = "warehouse_chief_klump_q0662_01.htm";
                break;
            }
            case 2: {
                if (n3 != 30845) break;
                if (n == 0 && n2 == 0 && questState.getQuestItemsCount(8765) < 50L) {
                    string = "warehouse_chief_klump_q0662_04.htm";
                    break;
                }
                if (n == 0 && n2 == 0 && questState.getQuestItemsCount(8765) >= 50L) {
                    string = "warehouse_chief_klump_q0662_05.htm";
                    break;
                }
                if (n == 0 && n2 == 0) break;
                int n5 = n;
                int n6 = n2;
                int n7 = n6 % 100;
                int n8 = n6 / 100;
                n6 = n5 % 100;
                int n9 = n5 % 10000 / 100;
                int n10 = n5 % 1000000 / 10000;
                int n11 = n5 % 100000000 / 1000000;
                string = HtmCache.getInstance().getNotNull("quests/_662_AGameOfCards/warehouse_chief_klump_q0662_11a.htm", questState.getPlayer());
                if (n8 % 2 < 1) {
                    string = string.replace("<?FontColor1?>", "ffff00");
                    string = string.replace("<?Cell1?>", "?");
                } else {
                    string = string.replace("<?FontColor1?>", "ff6f6f");
                    if (n6 == 1) {
                        string = string.replace("<?Cell1?>", "!");
                    } else if (n6 == 2) {
                        string = string.replace("<?Cell1?>", "=");
                    } else if (n6 == 3) {
                        string = string.replace("<?Cell1?>", "T");
                    } else if (n6 == 4) {
                        string = string.replace("<?Cell1?>", "V");
                    } else if (n6 == 5) {
                        string = string.replace("<?Cell1?>", "O");
                    } else if (n6 == 6) {
                        string = string.replace("<?Cell1?>", "P");
                    } else if (n6 == 7) {
                        string = string.replace("<?Cell1?>", "S");
                    } else if (n6 == 8) {
                        string = string.replace("<?Cell1?>", "E");
                    } else if (n6 == 9) {
                        string = string.replace("<?Cell1?>", "H");
                    } else if (n6 == 10) {
                        string = string.replace("<?Cell1?>", "A");
                    } else if (n6 == 11) {
                        string = string.replace("<?Cell1?>", "R");
                    } else if (n6 == 12) {
                        string = string.replace("<?Cell1?>", "D");
                    } else if (n6 == 13) {
                        string = string.replace("<?Cell1?>", "I");
                    } else if (n6 == 14) {
                        string = string.replace("<?Cell1?>", "N");
                    }
                }
                if (n8 % 4 < 2) {
                    string = string.replace("<?FontColor2?>", "ffff00");
                    string = string.replace("<?Cell2?>", "?");
                } else {
                    string = string.replace("<?FontColor2?>", "ff6f6f");
                    if (n9 == 1) {
                        string = string.replace("<?Cell2?>", "!");
                    } else if (n9 == 2) {
                        string = string.replace("<?Cell2?>", "=");
                    } else if (n9 == 3) {
                        string = string.replace("<?Cell2?>", "T");
                    } else if (n9 == 4) {
                        string = string.replace("<?Cell2?>", "V");
                    } else if (n9 == 5) {
                        string = string.replace("<?Cell2?>", "O");
                    } else if (n9 == 6) {
                        string = string.replace("<?Cell2?>", "P");
                    } else if (n9 == 7) {
                        string = string.replace("<?Cell2?>", "S");
                    } else if (n9 == 8) {
                        string = string.replace("<?Cell2?>", "E");
                    } else if (n9 == 9) {
                        string = string.replace("<?Cell2?>", "H");
                    } else if (n9 == 10) {
                        string = string.replace("<?Cell2?>", "A");
                    } else if (n9 == 11) {
                        string = string.replace("<?Cell2?>", "R");
                    } else if (n9 == 12) {
                        string = string.replace("<?Cell2?>", "D");
                    } else if (n9 == 13) {
                        string = string.replace("<?Cell2?>", "I");
                    } else if (n9 == 14) {
                        string = string.replace("<?Cell2?>", "N");
                    }
                }
                if (n8 % 8 < 4) {
                    string = string.replace("<?FontColor3?>", "ffff00");
                    string = string.replace("<?Cell3?>", "?");
                } else {
                    string = string.replace("<?FontColor3?>", "ff6f6f");
                    if (n10 == 1) {
                        string = string.replace("<?Cell3?>", "!");
                    } else if (n10 == 2) {
                        string = string.replace("<?Cell3?>", "=");
                    } else if (n10 == 3) {
                        string = string.replace("<?Cell3?>", "T");
                    } else if (n10 == 4) {
                        string = string.replace("<?Cell3?>", "V");
                    } else if (n10 == 5) {
                        string = string.replace("<?Cell3?>", "O");
                    } else if (n10 == 6) {
                        string = string.replace("<?Cell3?>", "P");
                    } else if (n10 == 7) {
                        string = string.replace("<?Cell3?>", "S");
                    } else if (n10 == 8) {
                        string = string.replace("<?Cell3?>", "E");
                    } else if (n10 == 9) {
                        string = string.replace("<?Cell3?>", "H");
                    } else if (n10 == 10) {
                        string = string.replace("<?Cell3?>", "A");
                    } else if (n10 == 11) {
                        string = string.replace("<?Cell3?>", "R");
                    } else if (n10 == 12) {
                        string = string.replace("<?Cell3?>", "D");
                    } else if (n10 == 13) {
                        string = string.replace("<?Cell3?>", "I");
                    } else if (n10 == 14) {
                        string = string.replace("<?Cell3?>", "N");
                    }
                }
                if (n8 % 16 < 8) {
                    string = string.replace("<?FontColor4?>", "ffff00");
                    string = string.replace("<?Cell4?>", "?");
                } else {
                    string = string.replace("<?FontColor4?>", "ff6f6f");
                    if (n11 == 1) {
                        string = string.replace("<?Cell4?>", "!");
                    } else if (n11 == 2) {
                        string = string.replace("<?Cell4?>", "=");
                    } else if (n11 == 3) {
                        string = string.replace("<?Cell4?>", "T");
                    } else if (n11 == 4) {
                        string = string.replace("<?Cell4?>", "V");
                    } else if (n11 == 5) {
                        string = string.replace("<?Cell4?>", "O");
                    } else if (n11 == 6) {
                        string = string.replace("<?Cell4?>", "P");
                    } else if (n11 == 7) {
                        string = string.replace("<?Cell4?>", "S");
                    } else if (n11 == 8) {
                        string = string.replace("<?Cell4?>", "E");
                    } else if (n11 == 9) {
                        string = string.replace("<?Cell4?>", "H");
                    } else if (n11 == 10) {
                        string = string.replace("<?Cell4?>", "A");
                    } else if (n11 == 11) {
                        string = string.replace("<?Cell4?>", "R");
                    } else if (n11 == 12) {
                        string = string.replace("<?Cell4?>", "D");
                    } else if (n11 == 13) {
                        string = string.replace("<?Cell4?>", "I");
                    } else if (n11 == 14) {
                        string = string.replace("<?Cell4?>", "N");
                    }
                }
                if (n8 % 32 < 16) {
                    string = string.replace("<?FontColor5?>", "ffff00");
                    string = string.replace("<?Cell5?>", "?");
                    break;
                }
                string = string.replace("<?FontColor5?>", "ff6f6f");
                if (n7 == 1) {
                    string = string.replace("<?Cell5?>", "!");
                    break;
                }
                if (n7 == 2) {
                    string = string.replace("<?Cell5?>", "=");
                    break;
                }
                if (n7 == 3) {
                    string = string.replace("<?Cell5?>", "T");
                    break;
                }
                if (n7 == 4) {
                    string = string.replace("<?Cell5?>", "V");
                    break;
                }
                if (n7 == 5) {
                    string = string.replace("<?Cell5?>", "O");
                    break;
                }
                if (n7 == 6) {
                    string = string.replace("<?Cell5?>", "P");
                    break;
                }
                if (n7 == 7) {
                    string = string.replace("<?Cell5?>", "S");
                    break;
                }
                if (n7 == 8) {
                    string = string.replace("<?Cell5?>", "E");
                    break;
                }
                if (n7 == 9) {
                    string = string.replace("<?Cell5?>", "H");
                    break;
                }
                if (n7 == 10) {
                    string = string.replace("<?Cell5?>", "A");
                    break;
                }
                if (n7 == 11) {
                    string = string.replace("<?Cell5?>", "R");
                    break;
                }
                if (n7 == 12) {
                    string = string.replace("<?Cell5?>", "D");
                    break;
                }
                if (n7 == 13) {
                    string = string.replace("<?Cell5?>", "I");
                    break;
                }
                if (n7 != 14) break;
                string = string.replace("<?Cell5?>", "N");
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (n == 20672) {
            if (Rnd.get(1000) < 357) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20673) {
            if (Rnd.get(1000) < 373) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20674) {
            if (Rnd.get(1000) < 583) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20677) {
            if (Rnd.get(1000) < 435) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20677 || n == 20955) {
            if (Rnd.get(1000) < 358) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20958) {
            if (Rnd.get(1000) < 283) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20959) {
            if (Rnd.get(1000) < 455) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20961) {
            if (Rnd.get(1000) < 365) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20962) {
            if (Rnd.get(1000) < 348) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20965) {
            if (Rnd.get(1000) < 457) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20966) {
            if (Rnd.get(1000) < 493) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20968) {
            if (Rnd.get(1000) < 418) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20972) {
            if (Rnd.get(1000) < 35) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20973) {
            if (Rnd.get(1000) < 453) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 21002) {
            if (Rnd.get(1000) < 315) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 21004) {
            if (Rnd.get(1000) < 32) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 21006) {
            if (Rnd.get(1000) < 335) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 21008) {
            if (Rnd.get(1000) < 462) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 21010) {
            if (Rnd.get(1000) < 397) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 21109) {
            if (Rnd.get(1000) < 507) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 21112) {
            if (Rnd.get(1000) < 552) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 21114) {
            if (Rnd.get(1000) < 587) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 21116) {
            if (Rnd.get(1000) < 812) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 21278 || n == 21279 || n == 21280) {
            if (Rnd.get(1000) < 483) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 21286 || n == 21287 || n == 21288) {
            if (Rnd.get(1000) < 515) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 21508) {
            if (Rnd.get(1000) < 493) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 21510) {
            if (Rnd.get(1000) < 527) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 21515) {
            if (Rnd.get(1000) < 598) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 21520) {
            if (Rnd.get(1000) < 458) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 21526) {
            if (Rnd.get(1000) < 552) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 21530) {
            if (Rnd.get(1000) < 488) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 21535) {
            if (Rnd.get(1000) < 573) {
                questState.giveItems(8765, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 18001 && Rnd.get(1000) < 232) {
            questState.giveItems(8765, 1L);
            questState.playSound("ItemSound.quest_itemget");
        }
        return null;
    }
}

