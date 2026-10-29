/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _333_BlackLionHunt
extends Quest
implements ScriptFile {
    private static final int bhU = 30735;
    private static final int bhV = 30736;
    private static final int bhW = 30471;
    private static final int aIc = 30130;
    private static final int bhX = 30531;
    private static final int bhY = 30737;
    private static final int bhZ = 20157;
    private static final int bia = 20160;
    private static final int bib = 20171;
    private static final int bic = 20197;
    private static final int bid = 20198;
    private static final int bie = 20200;
    private static final int bif = 20201;
    private static final int big = 20207;
    private static final int bih = 20208;
    private static final int bii = 20209;
    private static final int bij = 20210;
    private static final int bik = 20211;
    private static final int bil = 20230;
    private static final int bim = 20232;
    private static final int bin = 20234;
    private static final int bio = 20251;
    private static final int bip = 20252;
    private static final int biq = 20253;
    private static final int bir = 27151;
    private static final int bis = 27152;
    private static final int bgx = 1369;
    private static final int bit = 3675;
    private static final int biu = 3676;
    private static final int biv = 3677;
    private static final int biw = 3848;
    private static final int bix = 3849;
    private static final int biy = 3850;
    private static final int biz = 3851;
    private static final int biA = 3671;
    private static final int biB = 3672;
    private static final int biC = 3673;
    private static final int biD = 3674;
    private static final int biE = 3440;
    private static final int biF = 3441;
    private static final int biG = 3442;
    private static final int biH = 3443;
    private static final int bdp = 1061;
    private static final int biI = 1463;
    private static final int biJ = 2510;
    private static final int biK = 736;
    private static final int biL = 735;
    private static final int biM = 3444;
    private static final int biN = 3445;
    private static final int biO = 3446;
    private static final int biP = 3447;
    private static final int biQ = 3448;
    private static final int biR = 3449;
    private static final int biS = 3450;
    private static final int biT = 3451;
    private static final int biU = 3452;
    private static final int biV = 3453;
    private static final int biW = 3454;
    private static final int biX = 3455;
    private static final int biY = 3456;
    private static final int biZ = 3457;
    private static final int bja = 3458;
    private static final int bjb = 3459;
    private static final int bjc = 3460;
    private static final int bjd = 3461;
    private static final int bje = 3462;
    private static final int bjf = 3463;
    private static final int bjg = 3464;
    private static final int bjh = 3465;
    private static final int bji = 3466;

    public _333_BlackLionHunt() {
        super(0);
        this.addStartNpc(30735);
        this.addTalkId(30737, 30736, 30471, 30130, 30531);
        this.addKillId(20157, 20160, 20171, 20197, 20198, 20200, 20201, 20207, 20208, 20209, 20210, 20211, 20230, 20232, 20234, 20251, 20252, 20253, 27151, 27152);
        this.addQuestItem(3675, 3676, 3677, 3848, 3849, 3850, 3851, 3671, 3672, 3673, 3674);
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
        int n = questState.getInt("hunt_of_blacklion");
        int n2 = npcInstance.getNpcId();
        if (n2 == 30735) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "sophia_q0333_04.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=333&reply=1")) {
                string2 = "sophia_q0333_05.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=333&reply=2")) {
                string2 = "sophia_q0333_06.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=333&reply=3")) {
                string2 = "sophia_q0333_07.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=333&reply=4")) {
                string2 = "sophia_q0333_08.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=333&reply=5")) {
                string2 = "sophia_q0333_09.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=333&reply=6")) {
                if (questState.getQuestItemsCount(3671) == 0L) {
                    questState.giveItems(3671, 1L);
                    string2 = "sophia_q0333_10.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=333&reply=7")) {
                if (questState.getQuestItemsCount(3672) == 0L) {
                    questState.giveItems(3672, 1L);
                    string2 = "sophia_q0333_11.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=333&reply=8")) {
                if (questState.getQuestItemsCount(3673) == 0L) {
                    questState.giveItems(3673, 1L);
                    string2 = "sophia_q0333_12.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=333&reply=9")) {
                if (questState.getQuestItemsCount(3674) == 0L) {
                    questState.giveItems(3674, 1L);
                    string2 = "sophia_q0333_13.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=333&reply=10")) {
                if (questState.getQuestItemsCount(3675) < 10L) {
                    string2 = "sophia_q0333_16.htm";
                } else if (questState.getQuestItemsCount(3675) >= 10L && questState.getQuestItemsCount(3676) < 4L) {
                    questState.giveItems(3676, 1L);
                    int n3 = Rnd.get(100);
                    if (n3 < 25) {
                        questState.giveItems(1061, 20L);
                    } else if (n3 < 50) {
                        if (!questState.getPlayer().getClassId().isMage()) {
                            questState.giveItems(1463, 100L);
                        } else {
                            questState.giveItems(2510, 50L);
                        }
                    } else if (n3 < 75) {
                        questState.giveItems(736, 20L);
                    } else {
                        questState.giveItems(735, 3L);
                    }
                    questState.takeItems(3675, 10L);
                    string2 = "sophia_q0333_17a.htm";
                } else if (questState.getQuestItemsCount(3675) >= 10L && questState.getQuestItemsCount(3676) >= 4L && questState.getQuestItemsCount(3676) <= 7L) {
                    questState.giveItems(3676, 1L);
                    int n4 = Rnd.get(100);
                    if (n4 < 25) {
                        questState.giveItems(1061, 25L);
                    } else if (n4 < 50) {
                        if (!questState.getPlayer().getClassId().isMage()) {
                            questState.giveItems(1463, 200L);
                        } else {
                            questState.giveItems(2510, 100L);
                        }
                    } else if (n4 < 75) {
                        questState.giveItems(736, 20L);
                    } else {
                        questState.giveItems(735, 3L);
                    }
                    questState.takeItems(3675, 10L);
                    string2 = "sophia_q0333_18b.htm";
                } else if (questState.getQuestItemsCount(3675) >= 10L && questState.getQuestItemsCount(3676) >= 8L) {
                    int n5;
                    if (questState.getQuestItemsCount(3676) > 8L) {
                        questState.takeItems(3676, questState.getQuestItemsCount(3676) - 8L);
                    }
                    if ((n5 = Rnd.get(100)) < 25) {
                        questState.giveItems(1061, 50L);
                    } else if (n5 < 50) {
                        if (!questState.getPlayer().getClassId().isMage()) {
                            questState.giveItems(1463, 400L);
                        } else {
                            questState.giveItems(2510, 200L);
                        }
                    } else if (n5 < 75) {
                        questState.giveItems(736, 30L);
                    } else {
                        questState.giveItems(735, 4L);
                    }
                    questState.takeItems(3675, 10L);
                    string2 = "sophia_q0333_19b.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=333&reply=11")) {
                questState.takeItems(3671, -1L);
                questState.takeItems(3672, -1L);
                questState.takeItems(3673, -1L);
                questState.takeItems(3674, -1L);
                string2 = "sophia_q0333_20.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=333&reply=12")) {
                string2 = "sophia_q0333_21.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=333&reply=13")) {
                string2 = "sophia_q0333_24a.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=333&reply=14")) {
                string2 = "sophia_q0333_25b.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=333&reply=15") && questState.getQuestItemsCount(1369) >= 1L) {
                questState.giveItems(57, 12400L);
                questState.takeItems(1369, -1L);
                string2 = "sophia_q0333_26.htm";
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            }
        } else if (n2 == 30737) {
            if (string.equalsIgnoreCase("menu_select?ask=333&reply=1")) {
                if (questState.getQuestItemsCount(3440) + questState.getQuestItemsCount(3441) + questState.getQuestItemsCount(3442) + questState.getQuestItemsCount(3443) >= 1L) {
                    if (questState.getQuestItemsCount(3440) >= 1L) {
                        questState.takeItems(3440, 1L);
                    } else if (questState.getQuestItemsCount(3441) >= 1L) {
                        questState.takeItems(3441, 1L);
                    } else if (questState.getQuestItemsCount(3442) >= 1L) {
                        questState.takeItems(3442, 1L);
                    } else if (questState.getQuestItemsCount(3443) >= 1L) {
                        questState.takeItems(3443, 1L);
                    }
                    if (questState.getQuestItemsCount(3677) < 80L) {
                        questState.giveItems(3677, 1L);
                    } else if (questState.getQuestItemsCount(3677) > 80L) {
                        questState.takeItems(3677, questState.getQuestItemsCount(3677) - 80L);
                    }
                    if (questState.getQuestItemsCount(3677) < 40L) {
                        questState.giveItems(57, 100L);
                        string2 = "morgan_q0333_03.htm";
                    } else if (questState.getQuestItemsCount(3677) >= 40L && questState.getQuestItemsCount(3677) < 80L) {
                        questState.giveItems(57, 200L);
                        string2 = "morgan_q0333_04.htm";
                    } else {
                        questState.giveItems(57, 300L);
                        string2 = "morgan_q0333_05.htm";
                    }
                } else {
                    string2 = "morgan_q0333_06.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=333&reply=2")) {
                string2 = "morgan_q0333_07.htm";
            }
        } else if (n2 == 30736) {
            if (string.equalsIgnoreCase("menu_select?ask=333&reply=1")) {
                int n6 = Rnd.get(100);
                int n7 = Rnd.get(100);
                if (questState.getQuestItemsCount(57) < 650L && questState.getQuestItemsCount(3440) + questState.getQuestItemsCount(3441) + questState.getQuestItemsCount(3442) + questState.getQuestItemsCount(3443) >= 1L) {
                    string2 = "redfoot_q0333_03.htm";
                } else if (questState.getQuestItemsCount(57) >= 650L && questState.getQuestItemsCount(3440) + questState.getQuestItemsCount(3441) + questState.getQuestItemsCount(3442) + questState.getQuestItemsCount(3443) >= 1L) {
                    if (n6 < 40) {
                        if (n7 < 33) {
                            questState.giveItems(3444, 1L);
                            string2 = "redfoot_q0333_04a.htm";
                        } else if (n7 < 66) {
                            questState.giveItems(3445, 1L);
                            string2 = "redfoot_q0333_04b.htm";
                        } else {
                            questState.giveItems(3446, 1L);
                            string2 = "redfoot_q0333_04c.htm";
                        }
                    } else if (n6 < 60) {
                        if (n7 < 33) {
                            questState.giveItems(3447, 1L);
                            string2 = "redfoot_q0333_04d.htm";
                        } else if (n7 < 66) {
                            questState.giveItems(3448, 1L);
                            string2 = "redfoot_q0333_04e.htm";
                        } else {
                            questState.giveItems(3449, 1L);
                            string2 = "redfoot_q0333_04f.htm";
                        }
                    } else if (n6 < 70) {
                        if (n7 < 33) {
                            questState.giveItems(3450, 1L);
                            string2 = "redfoot_q0333_04g.htm";
                        } else if (n7 < 66) {
                            questState.giveItems(3451, 1L);
                            string2 = "redfoot_q0333_04h.htm";
                        } else {
                            questState.giveItems(3452, 1L);
                            string2 = "redfoot_q0333_04i.htm";
                        }
                    } else if (n6 < 75) {
                        if (n7 < 33) {
                            questState.giveItems(3453, 1L);
                            string2 = "redfoot_q0333_04j.htm";
                        } else if (n7 < 66) {
                            questState.giveItems(3454, 1L);
                            string2 = "redfoot_q0333_04k.htm";
                        } else {
                            questState.giveItems(3455, 1L);
                            string2 = "redfoot_q0333_04l.htm";
                        }
                    } else if (n6 < 76) {
                        questState.giveItems(3456, 1L);
                        string2 = "redfoot_q0333_04m.htm";
                    } else if (Rnd.get(100) < 50) {
                        if (n7 < 25) {
                            questState.giveItems(3457, 1L);
                        } else if (n7 < 50) {
                            questState.giveItems(3458, 1L);
                        } else if (n7 < 75) {
                            questState.giveItems(3459, 1L);
                        } else {
                            questState.giveItems(3460, 1L);
                        }
                        string2 = "redfoot_q0333_04n.htm";
                    } else {
                        if (n7 < 25) {
                            questState.giveItems(3462, 1L);
                        } else if (n7 < 50) {
                            questState.giveItems(3463, 1L);
                        } else if (n7 < 75) {
                            questState.giveItems(3464, 1L);
                        } else {
                            questState.giveItems(3465, 1L);
                        }
                        string2 = "redfoot_q0333_04o.htm";
                    }
                    questState.takeItems(57, 650L);
                    if (questState.getQuestItemsCount(3440) >= 1L) {
                        questState.takeItems(3440, 1L);
                    } else if (questState.getQuestItemsCount(3441) >= 1L) {
                        questState.takeItems(3441, 1L);
                    } else if (questState.getQuestItemsCount(3442) >= 1L) {
                        questState.takeItems(3442, 1L);
                    } else if (questState.getQuestItemsCount(3443) >= 1L) {
                        questState.takeItems(3443, 1L);
                    }
                } else if (questState.getQuestItemsCount(3440) + questState.getQuestItemsCount(3441) + questState.getQuestItemsCount(3442) + questState.getQuestItemsCount(3443) < 1L) {
                    string2 = "redfoot_q0333_05.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=333&reply=2")) {
                string2 = "redfoot_q0333_06.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=333&reply=3")) {
                int n8 = Rnd.get(100);
                if (questState.getQuestItemsCount(57) < (long)(200 + n * 200)) {
                    string2 = "redfoot_q0333_07.htm";
                } else if (n * 100 > 200) {
                    string2 = "redfoot_q0333_08.htm";
                }
                string2 = n8 < 5 ? "redfoot_q0333_08a.htm" : (n8 < 10 ? "redfoot_q0333_08b.htm" : (n8 < 15 ? "redfoot_q0333_08c.htm" : (n8 < 20 ? "redfoot_q0333_08d.htm" : (n8 < 25 ? "redfoot_q0333_08e.htm" : (n8 < 30 ? "redfoot_q0333_08f.htm" : (n8 < 35 ? "redfoot_q0333_08g.htm" : (n8 < 40 ? "redfoot_q0333_08h.htm" : (n8 < 45 ? "redfoot_q0333_08i.htm" : (n8 < 50 ? "redfoot_q0333_08j.htm" : (n8 < 55 ? "redfoot_q0333_08k.htm" : (n8 < 60 ? "redfoot_q0333_08l.htm" : (n8 < 65 ? "redfoot_q0333_08m.htm" : (n8 < 70 ? "redfoot_q0333_08n.htm" : (n8 < 75 ? "redfoot_q0333_08o.htm" : (n8 < 80 ? "redfoot_q0333_08p.htm" : (n8 < 85 ? "redfoot_q0333_08q.htm" : (n8 < 90 ? "redfoot_q0333_08r.htm" : (n8 < 95 ? "redfoot_q0333_08s.htm" : "redfoot_q0333_08t.htm"))))))))))))))))));
                questState.takeItems(57, 200 + n * 200);
                questState.set("hunt_of_blacklion", String.valueOf(n + 1), true);
            } else if (string.equalsIgnoreCase("menu_select?ask=333&reply=4")) {
                string2 = "redfoot_q0333_09.htm";
            }
        } else if (n2 == 30471) {
            if (string.equalsIgnoreCase("menu_select?ask=333&reply=1")) {
                if (questState.getQuestItemsCount(3457) == 0L || questState.getQuestItemsCount(3458) == 0L || questState.getQuestItemsCount(3459) == 0L || questState.getQuestItemsCount(3460) == 0L) {
                    string2 = "blacksmith_rupio_q0333_03.htm";
                } else if (Rnd.get(100) < 50) {
                    questState.giveItems(3461, 1L);
                    questState.takeItems(3457, 1L);
                    questState.takeItems(3458, 1L);
                    questState.takeItems(3459, 1L);
                    questState.takeItems(3460, 1L);
                    string2 = "blacksmith_rupio_q0333_04.htm";
                } else {
                    questState.takeItems(3457, 1L);
                    questState.takeItems(3458, 1L);
                    questState.takeItems(3459, 1L);
                    questState.takeItems(3460, 1L);
                    string2 = "blacksmith_rupio_q0333_05.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=333&reply=2")) {
                if (questState.getQuestItemsCount(3462) == 0L || questState.getQuestItemsCount(3463) == 0L || questState.getQuestItemsCount(3464) == 0L || questState.getQuestItemsCount(3465) == 0L) {
                    string2 = "blacksmith_rupio_q0333_06.htm";
                } else if (Rnd.get(100) < 50) {
                    questState.giveItems(3466, 1L);
                    questState.takeItems(3462, 1L);
                    questState.takeItems(3463, 1L);
                    questState.takeItems(3464, 1L);
                    questState.takeItems(3465, 1L);
                    string2 = "blacksmith_rupio_q0333_07.htm";
                } else {
                    questState.takeItems(3462, 1L);
                    questState.takeItems(3463, 1L);
                    questState.takeItems(3464, 1L);
                    questState.takeItems(3465, 1L);
                    string2 = "blacksmith_rupio_q0333_08.htm";
                }
            }
        } else if (n2 == 30130) {
            if (string.equalsIgnoreCase("menu_select?ask=333&reply=1")) {
                if (questState.getQuestItemsCount(3461) >= 1L) {
                    questState.giveItems(57, 30000L);
                    questState.takeItems(3461, 1L);
                    string2 = "undres_q0333_04.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=333&reply=2")) {
                string2 = "undres_q0333_05.htm";
            }
        } else if (n2 == 30531) {
            if (string.equalsIgnoreCase("menu_select?ask=333&reply=1")) {
                if (questState.getQuestItemsCount(3466) >= 1L) {
                    questState.giveItems(57, 30000L);
                    questState.takeItems(3466, 1L);
                    string2 = "first_elder_lockirin_q0333_04.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=333&reply=2")) {
                string2 = "first_elder_lockirin_q0333_05.htm";
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
                if (n != 30735) break;
                if (questState.getPlayer().getLevel() < 25 || questState.getPlayer().getLevel() > 39) {
                    string = "sophia_q0333_01.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                if (questState.getQuestItemsCount(1369) == 0L) {
                    string = "sophia_q0333_02.htm";
                    break;
                }
                string = "sophia_q0333_03.htm";
                break;
            }
            case 2: {
                if (n == 30735) {
                    if (questState.getQuestItemsCount(3671) + questState.getQuestItemsCount(3672) + questState.getQuestItemsCount(3673) + questState.getQuestItemsCount(3674) == 0L) {
                        string = "sophia_q0333_14.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(3671) + questState.getQuestItemsCount(3672) + questState.getQuestItemsCount(3673) + questState.getQuestItemsCount(3674) == 1L && questState.getQuestItemsCount(3848) + questState.getQuestItemsCount(3849) + questState.getQuestItemsCount(3850) + questState.getQuestItemsCount(3851) < 1L && questState.getQuestItemsCount(3440) + questState.getQuestItemsCount(3441) + questState.getQuestItemsCount(3442) + questState.getQuestItemsCount(3443) < 1L) {
                        string = "sophia_q0333_15.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(3671) + questState.getQuestItemsCount(3672) + questState.getQuestItemsCount(3673) + questState.getQuestItemsCount(3674) == 1L && questState.getQuestItemsCount(3848) + questState.getQuestItemsCount(3849) + questState.getQuestItemsCount(3850) + questState.getQuestItemsCount(3851) < 1L && questState.getQuestItemsCount(3440) + questState.getQuestItemsCount(3441) + questState.getQuestItemsCount(3442) + questState.getQuestItemsCount(3443) >= 1L) {
                        string = "sophia_q0333_15a.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(3671) + questState.getQuestItemsCount(3672) + questState.getQuestItemsCount(3673) + questState.getQuestItemsCount(3674) == 1L && questState.getQuestItemsCount(3848) + questState.getQuestItemsCount(3849) + questState.getQuestItemsCount(3850) + questState.getQuestItemsCount(3851) >= 1L && questState.getQuestItemsCount(3440) + questState.getQuestItemsCount(3441) + questState.getQuestItemsCount(3442) + questState.getQuestItemsCount(3443) == 0L) {
                        long l = questState.getQuestItemsCount(3848) + questState.getQuestItemsCount(3849) + questState.getQuestItemsCount(3850) + questState.getQuestItemsCount(3851);
                        if (l >= 20L) {
                            if (l < 50L) {
                                questState.giveItems(3675, 1L);
                            } else if (l < 100L) {
                                questState.giveItems(3675, 2L);
                            } else {
                                questState.giveItems(3675, 3L);
                            }
                        }
                        questState.giveItems(57, questState.getQuestItemsCount(3848) * 35L + questState.getQuestItemsCount(3851) * 35L + questState.getQuestItemsCount(3850) * 35L + questState.getQuestItemsCount(3849) * 35L);
                        questState.takeItems(3848, -1L);
                        questState.takeItems(3849, -1L);
                        questState.takeItems(3850, -1L);
                        questState.takeItems(3851, -1L);
                        string = "sophia_q0333_22.htm";
                        questState.set("hunt_of_blacklion", String.valueOf(0), true);
                        break;
                    }
                    if (questState.getQuestItemsCount(3671) + questState.getQuestItemsCount(3672) + questState.getQuestItemsCount(3673) + questState.getQuestItemsCount(3674) != 1L || questState.getQuestItemsCount(3848) + questState.getQuestItemsCount(3849) + questState.getQuestItemsCount(3850) + questState.getQuestItemsCount(3851) < 1L || questState.getQuestItemsCount(3440) + questState.getQuestItemsCount(3441) + questState.getQuestItemsCount(3442) + questState.getQuestItemsCount(3443) < 1L) break;
                    long l = questState.getQuestItemsCount(3848) + questState.getQuestItemsCount(3849) + questState.getQuestItemsCount(3850) + questState.getQuestItemsCount(3851);
                    if (l >= 20L) {
                        if (l < 50L) {
                            questState.giveItems(3675, 1L);
                        } else if (l < 100L) {
                            questState.giveItems(3675, 2L);
                        } else {
                            questState.giveItems(3675, 3L);
                        }
                    }
                    if (questState.getQuestItemsCount(3848) > 0L) {
                        questState.giveItems(57, questState.getQuestItemsCount(3848) * 35L);
                    }
                    if (questState.getQuestItemsCount(3849) > 0L) {
                        questState.giveItems(57, questState.getQuestItemsCount(3849) * 35L);
                    }
                    if (questState.getQuestItemsCount(3850) > 0L) {
                        questState.giveItems(57, questState.getQuestItemsCount(3850) * 35L);
                    }
                    if (questState.getQuestItemsCount(3851) > 0L) {
                        questState.giveItems(57, questState.getQuestItemsCount(3851) * 35L);
                    }
                    questState.takeItems(3848, -1L);
                    questState.takeItems(3849, -1L);
                    questState.takeItems(3850, -1L);
                    questState.takeItems(3851, -1L);
                    string = "sophia_q0333_23.htm";
                    questState.set("hunt_of_blacklion", String.valueOf(0), true);
                    break;
                }
                if (n == 30737) {
                    if (questState.getQuestItemsCount(3440) + questState.getQuestItemsCount(3441) + questState.getQuestItemsCount(3442) + questState.getQuestItemsCount(3443) == 0L) {
                        string = "morgan_q0333_01.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(3440) + questState.getQuestItemsCount(3441) + questState.getQuestItemsCount(3442) + questState.getQuestItemsCount(3443) < 1L) break;
                    string = "morgan_q0333_02.htm";
                    break;
                }
                if (n == 30736) {
                    if (questState.getQuestItemsCount(3440) + questState.getQuestItemsCount(3441) + questState.getQuestItemsCount(3442) + questState.getQuestItemsCount(3443) == 0L) {
                        string = "redfoot_q0333_01.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(3440) + questState.getQuestItemsCount(3441) + questState.getQuestItemsCount(3442) + questState.getQuestItemsCount(3443) < 1L) break;
                    string = "redfoot_q0333_02.htm";
                    break;
                }
                if (n == 30471) {
                    if (questState.getQuestItemsCount(3457) + questState.getQuestItemsCount(3458) + questState.getQuestItemsCount(3459) + questState.getQuestItemsCount(3460) < 1L && questState.getQuestItemsCount(3462) + questState.getQuestItemsCount(3463) + questState.getQuestItemsCount(3464) + questState.getQuestItemsCount(3465) < 1L) {
                        string = "blacksmith_rupio_q0333_01.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(3457) + questState.getQuestItemsCount(3458) + questState.getQuestItemsCount(3459) + questState.getQuestItemsCount(3460) < 1L && questState.getQuestItemsCount(3462) + questState.getQuestItemsCount(3463) + questState.getQuestItemsCount(3464) + questState.getQuestItemsCount(3465) < 1L) break;
                    string = "blacksmith_rupio_q0333_02.htm";
                    break;
                }
                if (n == 30130) {
                    if (questState.getQuestItemsCount(3457) + questState.getQuestItemsCount(3458) + questState.getQuestItemsCount(3459) + questState.getQuestItemsCount(3460) < 1L && questState.getQuestItemsCount(3461) == 0L) {
                        string = "undres_q0333_01.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(3457) + questState.getQuestItemsCount(3458) + questState.getQuestItemsCount(3459) + questState.getQuestItemsCount(3460) >= 1L && questState.getQuestItemsCount(3461) == 0L) {
                        string = "undres_q0333_02.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(3461) < 1L) break;
                    string = "undres_q0333_03.htm";
                    break;
                }
                if (n != 30531) break;
                if (questState.getQuestItemsCount(3462) + questState.getQuestItemsCount(3463) + questState.getQuestItemsCount(3464) + questState.getQuestItemsCount(3465) < 1L && questState.getQuestItemsCount(3466) == 0L) {
                    string = "first_elder_lockirin_q0333_01.htm";
                    break;
                }
                if (questState.getQuestItemsCount(3462) + questState.getQuestItemsCount(3463) + questState.getQuestItemsCount(3464) + questState.getQuestItemsCount(3465) >= 1L && questState.getQuestItemsCount(3466) == 0L) {
                    string = "first_elder_lockirin_q0333_02.htm";
                    break;
                }
                if (questState.getQuestItemsCount(3466) < 1L) break;
                string = "first_elder_lockirin_q0333_03.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (n == 20157) {
            if (questState.getQuestItemsCount(3674) >= 1L) {
                if (Rnd.get(100) < 55) {
                    questState.giveItems(3851, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
                if (Rnd.get(100) < 12) {
                    questState.giveItems(3443, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
                if (Rnd.get(100) < 2 && questState.getQuestItemsCount(3674) > 0L) {
                    questState.addSpawn(27152);
                }
            }
        } else if (n == 20160) {
            if (questState.getQuestItemsCount(3671) >= 1L) {
                if (Rnd.get(2) == 0) {
                    questState.giveItems(3848, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
                if (Rnd.get(100) < 11) {
                    questState.giveItems(3440, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20171) {
            if (questState.getQuestItemsCount(3671) >= 1L) {
                if (Rnd.get(100) < 60) {
                    questState.giveItems(3848, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
                if (Rnd.get(100) < 8) {
                    questState.giveItems(3440, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20197) {
            if (questState.getQuestItemsCount(3671) >= 1L) {
                if (Rnd.get(100) < 60) {
                    questState.giveItems(3848, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
                if (Rnd.get(100) < 9) {
                    questState.giveItems(3440, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20198) {
            if (questState.getQuestItemsCount(3671) >= 1L) {
                if (Rnd.get(2) == 0) {
                    questState.giveItems(3848, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
                if (Rnd.get(100) < 12) {
                    questState.giveItems(3440, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20200) {
            if (questState.getQuestItemsCount(3671) >= 1L) {
                if (Rnd.get(2) == 0) {
                    questState.giveItems(3848, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
                if (Rnd.get(100) < 13) {
                    questState.giveItems(3440, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20201) {
            if (questState.getQuestItemsCount(3671) >= 1L) {
                if (Rnd.get(2) == 0) {
                    questState.giveItems(3848, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
                if (Rnd.get(100) < 15) {
                    questState.giveItems(3440, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20207) {
            if (questState.getQuestItemsCount(3672) >= 1L) {
                if (Rnd.get(2) == 0) {
                    questState.giveItems(3849, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
                if (Rnd.get(100) < 9) {
                    questState.giveItems(3441, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20208) {
            if (questState.getQuestItemsCount(3672) >= 1L) {
                if (Rnd.get(2) == 0) {
                    questState.giveItems(3849, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
                if (Rnd.get(100) < 10) {
                    questState.giveItems(3441, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20209) {
            if (questState.getQuestItemsCount(3672) >= 1L) {
                if (Rnd.get(2) == 0) {
                    questState.giveItems(3849, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
                if (Rnd.get(100) < 11) {
                    questState.giveItems(3441, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20210) {
            if (questState.getQuestItemsCount(3672) >= 1L) {
                if (Rnd.get(2) == 0) {
                    questState.giveItems(3849, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
                if (Rnd.get(100) < 12) {
                    questState.giveItems(3441, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20211) {
            if (questState.getQuestItemsCount(3672) >= 1L) {
                if (Rnd.get(2) == 0) {
                    questState.giveItems(3849, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
                if (Rnd.get(100) < 13) {
                    questState.giveItems(3441, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20230) {
            if (questState.getQuestItemsCount(3674) >= 1L) {
                if (Rnd.get(100) < 60) {
                    questState.giveItems(3851, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
                if (Rnd.get(100) < 13) {
                    questState.giveItems(3443, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
                if (Rnd.get(100) < 2 && questState.getQuestItemsCount(3674) > 0L) {
                    questState.addSpawn(27152);
                }
            }
        } else if (n == 20232) {
            if (questState.getQuestItemsCount(3674) >= 1L) {
                if (Rnd.get(100) < 56) {
                    questState.giveItems(3851, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
                if (Rnd.get(100) < 14) {
                    questState.giveItems(3443, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
                if (Rnd.get(100) < 2 && questState.getQuestItemsCount(3674) > 0L) {
                    questState.addSpawn(27152);
                }
            }
        } else if (n == 20234) {
            if (questState.getQuestItemsCount(3674) >= 1L) {
                if (Rnd.get(100) < 60) {
                    questState.giveItems(3851, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
                if (Rnd.get(100) < 15) {
                    questState.giveItems(3443, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
                if (Rnd.get(100) < 2 && questState.getQuestItemsCount(3674) > 0L) {
                    questState.addSpawn(27152);
                }
            }
        } else if (n == 20251 || n == 20252) {
            if (questState.getQuestItemsCount(3673) >= 1L) {
                if (Rnd.get(2) == 0) {
                    questState.giveItems(3850, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
                if (Rnd.get(100) < 14) {
                    questState.giveItems(3442, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
                if (Rnd.get(100) < 3 && questState.getQuestItemsCount(3673) > 0L) {
                    questState.addSpawn(27151);
                    questState.addSpawn(27151);
                }
            }
        } else if (n == 20253) {
            if (questState.getQuestItemsCount(3673) >= 1L) {
                if (Rnd.get(2) == 0) {
                    questState.giveItems(3850, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
                if (Rnd.get(100) < 15) {
                    questState.giveItems(3442, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
                if (Rnd.get(100) < 3 && questState.getQuestItemsCount(3673) > 0L) {
                    questState.addSpawn(27151);
                    questState.addSpawn(27151);
                }
            }
        } else if (n == 27151) {
            if (questState.getQuestItemsCount(3673) >= 1L) {
                questState.giveItems(3850, 4L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 27152 && questState.getQuestItemsCount(3674) >= 1L) {
            questState.giveItems(3851, 8L);
            questState.playSound("ItemSound.quest_itemget");
        }
        return null;
    }
}

