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

public class _663_SeductiveWhispers
extends Quest
implements ScriptFile {
    private static final int bVb = 30846;
    private static final int bUw = 20674;
    private static final int bVc = 20678;
    private static final int bVd = 20954;
    private static final int bUy = 20955;
    private static final int bVe = 20956;
    private static final int bVf = 20957;
    private static final int bUz = 20958;
    private static final int bUA = 20959;
    private static final int bVg = 20960;
    private static final int bUB = 20961;
    private static final int bUC = 20962;
    private static final int bVh = 20963;
    private static final int bVi = 20974;
    private static final int bVj = 20975;
    private static final int bVk = 20976;
    private static final int bVl = 20996;
    private static final int bVm = 20997;
    private static final int bVn = 20998;
    private static final int bVo = 20999;
    private static final int bVp = 21000;
    private static final int bVq = 21001;
    private static final int bUH = 21002;
    private static final int bUJ = 21006;
    private static final int bVr = 21007;
    private static final int bUK = 21008;
    private static final int bVs = 21009;
    private static final int bUL = 21010;
    private static final int bVt = 8766;
    private static final int bUo = 955;
    private static final int bUY = 951;
    private static final int bUX = 947;
    private static final int bVu = 948;
    private static final int aGI = 729;
    private static final int bVv = 730;
    private static final int bVw = 4963;
    private static final int bVx = 4964;
    private static final int bVy = 4965;
    private static final int bVz = 4966;
    private static final int bVA = 4967;
    private static final int bVB = 4968;
    private static final int bVC = 4969;
    private static final int bVD = 4970;
    private static final int bVE = 4971;
    private static final int bVF = 4972;
    private static final int bVG = 5000;
    private static final int bVH = 5001;
    private static final int bVI = 5002;
    private static final int bVJ = 5003;
    private static final int bVK = 5004;
    private static final int bVL = 5005;
    private static final int bVM = 5006;
    private static final int bVN = 5007;
    private static final int boL = 4104;
    private static final int boD = 4105;
    private static final int boA = 4106;
    private static final int box = 4107;
    private static final int boN = 4108;
    private static final int boq = 4109;
    private static final int boK = 4110;
    private static final int bou = 4111;
    private static final int boz = 4112;
    private static final int boF = 4113;
    private static final int boy = 4114;
    private static final int boH = 4115;
    private static final int bVO = 4116;
    private static final int boE = 4117;
    private static final int boI = 4118;
    private static final int bos = 4119;
    private static final int bov = 4120;
    private static final int boB = 4121;

    public _663_SeductiveWhispers() {
        super(1);
        this.addStartNpc(30846);
        this.addKillId(20674, 20678, 20954, 20955, 20956, 20957, 20958, 20959, 20960, 20961, 20962, 20963, 20974, 20975, 20976, 20996, 20997, 20998, 20999, 21000, 21001, 21002, 21006, 21007, 21008, 21009, 21010);
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
        String string2 = "no-quest";
        int n = questState.getInt("whispers_of_temptation");
        int n2 = questState.getInt("whispers_of_temptation_ex");
        int n3 = npcInstance.getNpcId();
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        int n7 = 0;
        int n8 = 0;
        int n9 = 0;
        if (n3 == 30846) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("whispers_of_temptation", String.valueOf(1), true);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "blacksmith_wilbert_q0663_03.htm";
            } else if (string.equalsIgnoreCase("reply_1")) {
                string2 = "blacksmith_wilbert_q0663_01a.htm";
            } else if (string.equalsIgnoreCase("reply_4") && n % 10 <= 4) {
                if (n / 10 < 1) {
                    if (questState.getQuestItemsCount(8766) >= 50L) {
                        questState.takeItems(8766, 50L);
                        questState.set("whispers_of_temptation", String.valueOf(5), true);
                        questState.set("whispers_of_temptation_ex", String.valueOf(0), true);
                        string2 = "blacksmith_wilbert_q0663_09.htm";
                    } else {
                        string2 = "blacksmith_wilbert_q0663_10.htm";
                    }
                } else {
                    n4 = n / 10;
                    n5 = n4 * 10 + 5;
                    questState.set("whispers_of_temptation", String.valueOf(n5), true);
                    questState.set("whispers_of_temptation_ex", String.valueOf(0), true);
                    string2 = "blacksmith_wilbert_q0663_09a.htm";
                }
            } else if (string.equalsIgnoreCase("reply_5") && n % 10 == 5 && n / 1000 == 0) {
                n4 = n2;
                if (n4 < 0) {
                    n4 = 0;
                }
                n5 = n4 % 10;
                n6 = (n4 - n5) / 10;
                int n10 = Rnd.get(2) + 1;
                int n11 = Rnd.get(5) + 1;
                n9 = n / 10;
                int n12 = n10 * 10 + n11;
                if (n10 == n6) {
                    n7 = n11 + n5;
                    if (n7 % 5 == 0 && n7 != 10) {
                        if (n % 100 / 10 >= 7) {
                            string2 = HtmCache.getInstance().getNotNull("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_14.htm", questState.getPlayer());
                            string2 = string2.replace("<?card1pic?>", String.valueOf(n4));
                            string2 = string2.replace("<?card2pic?>", String.valueOf(n12));
                            string2 = string2.replace("<?name?>", questState.getPlayer().getName());
                            questState.set("whispers_of_temptation", String.valueOf(4), true);
                            questState.giveItems(57, 2384000L);
                            questState.giveItems(729, 1L);
                            questState.giveItems(730, 2L);
                        } else {
                            string2 = HtmCache.getInstance().getNotNull("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_13.htm", questState.getPlayer());
                            string2 = string2.replace("<?card1pic?>", String.valueOf(n4));
                            string2 = string2.replace("<?card2pic?>", String.valueOf(n12));
                            string2 = string2.replace("<?name?>", questState.getPlayer().getName());
                            string2 = string2.replace("<?wincount?>", String.valueOf(n9 + 1));
                            n8 = n / 10 * 10 + 7;
                            questState.set("whispers_of_temptation", String.valueOf(n8), true);
                        }
                    } else {
                        string2 = HtmCache.getInstance().getNotNull("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_12.htm", questState.getPlayer());
                        string2 = string2.replace("<?card1pic?>", String.valueOf(n4));
                        string2 = string2.replace("<?card2pic?>", String.valueOf(n12));
                        string2 = string2.replace("<?name?>", questState.getPlayer().getName());
                        questState.set("whispers_of_temptation_ex", String.valueOf(n12), true);
                        n8 = n / 10 * 10 + 6;
                        questState.set("whispers_of_temptation", String.valueOf(n8), true);
                    }
                } else if (n10 != n6) {
                    if (n11 == 5 || n5 == 5) {
                        if (n % 100 / 10 >= 7) {
                            string2 = HtmCache.getInstance().getNotNull("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_14.htm", questState.getPlayer());
                            string2 = string2.replace("<?card1pic?>", String.valueOf(n4));
                            string2 = string2.replace("<?card2pic?>", String.valueOf(n12));
                            string2 = string2.replace("<?name?>", questState.getPlayer().getName());
                            questState.giveItems(57, 2384000L);
                            questState.giveItems(729, 1L);
                            questState.giveItems(730, 2L);
                            questState.set("whispers_of_temptation", String.valueOf(4), true);
                        } else {
                            string2 = HtmCache.getInstance().getNotNull("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_13.htm", questState.getPlayer());
                            string2 = string2.replace("<?card1pic?>", String.valueOf(n4));
                            string2 = string2.replace("<?card2pic?>", String.valueOf(n12));
                            string2 = string2.replace("<?name?>", questState.getPlayer().getName());
                            string2 = string2.replace("<?wincount?>", String.valueOf(n9 + 1));
                            n8 = n / 10 * 10 + 7;
                            questState.set("whispers_of_temptation", String.valueOf(n8), true);
                        }
                    } else {
                        string2 = HtmCache.getInstance().getNotNull("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_12.htm", questState.getPlayer());
                        string2 = string2.replace("<?card1pic?>", String.valueOf(n4));
                        string2 = string2.replace("<?card2pic?>", String.valueOf(n12));
                        string2 = string2.replace("<?name?>", questState.getPlayer().getName());
                        n12 = n10 * 10 + n11;
                        questState.set("whispers_of_temptation_ex", String.valueOf(n12), true);
                        n8 = n / 10 * 10 + 6;
                        questState.set("whispers_of_temptation", String.valueOf(n8), true);
                    }
                }
            } else if (string.equalsIgnoreCase("reply_6") && n % 10 == 6 && n / 1000 == 0) {
                n4 = n2;
                if (n4 < 0) {
                    n4 = 0;
                }
                n5 = n4 % 10;
                n6 = (n4 - n5) / 10;
                int n13 = Rnd.get(2) + 1;
                int n14 = Rnd.get(5) + 1;
                int n15 = n13 * 10 + n14;
                if (n13 == n6) {
                    n7 = n14 + n5;
                    if (n7 % 5 == 0 && n7 != 10) {
                        string2 = HtmCache.getInstance().getNotNull("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_19.htm", questState.getPlayer());
                        string2 = string2.replace("<?card1pic?>", String.valueOf(n4));
                        string2 = string2.replace("<?card2pic?>", String.valueOf(n15));
                        string2 = string2.replace("<?name?>", questState.getPlayer().getName());
                        questState.set("whispers_of_temptation", String.valueOf(1), true);
                        questState.set("whispers_of_temptation_ex", String.valueOf(0), true);
                    } else {
                        string2 = HtmCache.getInstance().getNotNull("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_18.htm", questState.getPlayer());
                        string2 = string2.replace("<?card1pic?>", String.valueOf(n4));
                        string2 = string2.replace("<?card2pic?>", String.valueOf(n15));
                        string2 = string2.replace("<?name?>", questState.getPlayer().getName());
                        n15 = n13 * 10 + n14;
                        questState.set("whispers_of_temptation_ex", String.valueOf(n15), true);
                        n8 = n / 10 * 10 + 5;
                        questState.set("whispers_of_temptation", String.valueOf(n8), true);
                    }
                } else if (n13 != n6) {
                    n7 = n13 + n5;
                    n8 = 66310 + n6;
                    n9 = 66310 + n13;
                    if (n14 == 5 || n5 == 5) {
                        string2 = HtmCache.getInstance().getNotNull("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_19.htm", questState.getPlayer());
                        string2 = string2.replace("<?card1pic?>", String.valueOf(n4));
                        string2 = string2.replace("<?card2pic?>", String.valueOf(n15));
                        string2 = string2.replace("<?name?>", questState.getPlayer().getName());
                        questState.set("whispers_of_temptation", String.valueOf(1), true);
                    } else {
                        string2 = HtmCache.getInstance().getNotNull("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_18.htm", questState.getPlayer());
                        string2 = string2.replace("<?card1pic?>", String.valueOf(n4));
                        string2 = string2.replace("<?card2pic?>", String.valueOf(n15));
                        string2 = string2.replace("<?name?>", questState.getPlayer().getName());
                        n15 = n13 * 10 + n14;
                        questState.set("whispers_of_temptation_ex", String.valueOf(n15), true);
                        n8 = n / 10 * 10 + 5;
                        questState.set("whispers_of_temptation", String.valueOf(n8), true);
                    }
                }
            } else if (string.equalsIgnoreCase("reply_8") && n % 10 == 7 && n / 1000 == 0) {
                n4 = n / 10;
                n5 = (n4 + 1) * 10 + 4;
                questState.set("whispers_of_temptation", String.valueOf(n5), true);
                questState.set("whispers_of_temptation_ex", String.valueOf(0), true);
                string2 = "blacksmith_wilbert_q0663_20.htm";
            } else if (string.equalsIgnoreCase("reply_9") && n % 10 == 7 && n / 1000 == 0) {
                n4 = n / 10;
                if (n4 == 0) {
                    questState.giveItems(57, 40000L);
                } else if (n4 == 1) {
                    questState.giveItems(57, 80000L);
                } else if (n4 == 2) {
                    questState.giveItems(57, 110000L);
                    questState.giveItems(955, 1L);
                } else if (n4 == 3) {
                    questState.giveItems(57, 199000L);
                    questState.giveItems(951, 1L);
                } else if (n4 == 4) {
                    questState.giveItems(57, 388000L);
                    n5 = Rnd.get(18) + 1;
                    if (n5 == 1) {
                        questState.giveItems(4963, 1L);
                    } else if (n5 == 2) {
                        questState.giveItems(4964, 1L);
                    } else if (n5 == 3) {
                        questState.giveItems(4965, 1L);
                    } else if (n5 == 4) {
                        questState.giveItems(4966, 1L);
                    } else if (n5 == 5) {
                        questState.giveItems(4967, 1L);
                    } else if (n5 == 6) {
                        questState.giveItems(4968, 1L);
                    } else if (n5 == 7) {
                        questState.giveItems(4969, 1L);
                    } else if (n5 == 8) {
                        questState.giveItems(4970, 1L);
                    } else if (n5 == 9) {
                        questState.giveItems(4971, 1L);
                    } else if (n5 == 10) {
                        questState.giveItems(4972, 1L);
                    } else if (n5 == 11) {
                        questState.giveItems(5000, 1L);
                    } else if (n5 == 12) {
                        questState.giveItems(5001, 1L);
                    } else if (n5 == 13) {
                        questState.giveItems(5002, 1L);
                    } else if (n5 == 14) {
                        questState.giveItems(5003, 1L);
                    } else if (n5 == 15) {
                        questState.giveItems(5004, 1L);
                    } else if (n5 == 16) {
                        questState.giveItems(5005, 1L);
                    } else if (n5 == 17) {
                        questState.giveItems(5006, 1L);
                    } else if (n5 == 18) {
                        questState.giveItems(5007, 1L);
                    }
                } else if (n4 == 5) {
                    questState.giveItems(57, 675000L);
                    n5 = Rnd.get(18) + 1;
                    if (n5 == 1) {
                        questState.giveItems(4104, 12L);
                    } else if (n5 == 2) {
                        questState.giveItems(4113, 12L);
                    } else if (n5 == 3) {
                        questState.giveItems(4112, 12L);
                    } else if (n5 == 4) {
                        questState.giveItems(4108, 12L);
                    } else if (n5 == 5) {
                        questState.giveItems(4111, 12L);
                    } else if (n5 == 6) {
                        questState.giveItems(4106, 12L);
                    } else if (n5 == 7) {
                        questState.giveItems(4109, 12L);
                    } else if (n5 == 8) {
                        questState.giveItems(4107, 12L);
                    } else if (n5 == 9) {
                        questState.giveItems(4105, 12L);
                    } else if (n5 == 10) {
                        questState.giveItems(4110, 12L);
                    } else if (n5 == 11) {
                        questState.giveItems(4114, 13L);
                    } else if (n5 == 12) {
                        questState.giveItems(4115, 13L);
                    } else if (n5 == 13) {
                        questState.giveItems(4120, 13L);
                    } else if (n5 == 14) {
                        questState.giveItems(4118, 13L);
                    } else if (n5 == 15) {
                        questState.giveItems(4116, 13L);
                    } else if (n5 == 16) {
                        questState.giveItems(4117, 13L);
                    } else if (n5 == 17) {
                        questState.giveItems(4119, 13L);
                    } else if (n5 == 18) {
                        questState.giveItems(4121, 13L);
                    }
                } else if (n4 == 6) {
                    questState.giveItems(57, 1284000L);
                    questState.giveItems(947, 2L);
                    questState.giveItems(948, 2L);
                }
                questState.set("whispers_of_temptation", String.valueOf(1), true);
                questState.set("whispers_of_temptation_ex", String.valueOf(0), true);
                string2 = "blacksmith_wilbert_q0663_21.htm";
            } else if (string.equalsIgnoreCase("reply_10") && n == 1 && n / 1000 == 0) {
                string2 = "blacksmith_wilbert_q0663_21a.htm";
            } else if (string.equalsIgnoreCase("reply_14") && n % 10 == 1) {
                if (questState.getQuestItemsCount(8766) >= 1L) {
                    questState.set("whispers_of_temptation", String.valueOf(1005), true);
                    questState.takeItems(8766, 1L);
                    string2 = "blacksmith_wilbert_q0663_22.htm";
                } else {
                    string2 = "blacksmith_wilbert_q0663_22a.htm";
                }
            } else if (string.equalsIgnoreCase("reply_15") && n == 1005) {
                n4 = n2;
                if (n4 < 0) {
                    n4 = 0;
                }
                n5 = n4 % 10;
                n6 = (n4 - n5) / 10;
                int n16 = Rnd.get(2) + 1;
                int n17 = Rnd.get(5) + 1;
                int n18 = n16 * 10 + n17;
                if (n16 == n6) {
                    n7 = n17 + n5;
                    n8 = 66310 + n6;
                    n9 = 66310 + n16;
                    if (n7 % 5 == 0 && n7 != 10) {
                        string2 = HtmCache.getInstance().getNotNull("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_25.htm", questState.getPlayer());
                        string2 = string2.replace("<?card1pic?>", String.valueOf(n4));
                        string2 = string2.replace("<?card2pic?>", String.valueOf(n18));
                        string2 = string2.replace("<?name?>", questState.getPlayer().getName());
                        questState.set("whispers_of_temptation", String.valueOf(1), true);
                        questState.set("whispers_of_temptation_ex", String.valueOf(0), true);
                        questState.giveItems(57, 800L);
                    } else {
                        string2 = HtmCache.getInstance().getNotNull("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_24.htm", questState.getPlayer());
                        string2 = string2.replace("<?card1pic?>", String.valueOf(n4));
                        string2 = string2.replace("<?card2pic?>", String.valueOf(n18));
                        string2 = string2.replace("<?name?>", questState.getPlayer().getName());
                        questState.set("whispers_of_temptation_ex", String.valueOf(n18), true);
                        questState.set("whispers_of_temptation", String.valueOf(1006), true);
                    }
                } else if (n16 != n6) {
                    if (n17 == 5 || n5 == 5) {
                        string2 = HtmCache.getInstance().getNotNull("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_25.htm", questState.getPlayer());
                        string2 = string2.replace("<?card1pic?>", String.valueOf(n4));
                        string2 = string2.replace("<?card2pic?>", String.valueOf(n18));
                        string2 = string2.replace("<?name?>", questState.getPlayer().getName());
                        questState.set("whispers_of_temptation", String.valueOf(1), true);
                        questState.set("whispers_of_temptation_ex", String.valueOf(0), true);
                        questState.giveItems(57, 800L);
                    } else {
                        string2 = HtmCache.getInstance().getNotNull("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_24.htm", questState.getPlayer());
                        string2 = string2.replace("<?card1pic?>", String.valueOf(n4));
                        string2 = string2.replace("<?card2pic?>", String.valueOf(n18));
                        string2 = string2.replace("<?name?>", questState.getPlayer().getName());
                        questState.set("whispers_of_temptation_ex", String.valueOf(n18), true);
                        questState.set("whispers_of_temptation", String.valueOf(1006), true);
                    }
                }
            } else if (string.equalsIgnoreCase("reply_16") && n == 1006) {
                n4 = n2;
                if (n4 < 0) {
                    n4 = 0;
                }
                n5 = n4 % 10;
                n6 = (n4 - n5) / 10;
                int n19 = Rnd.get(2) + 1;
                int n20 = Rnd.get(5) + 1;
                int n21 = n19 * 10 + n20;
                if (n19 == n6) {
                    n7 = n20 + n5;
                    if (n7 % 5 == 0 && n7 != 10) {
                        string2 = HtmCache.getInstance().getNotNull("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_29.htm", questState.getPlayer());
                        string2 = string2.replace("<?card1pic?>", String.valueOf(n4));
                        string2 = string2.replace("<?card2pic?>", String.valueOf(n21));
                        string2 = string2.replace("<?name?>", questState.getPlayer().getName());
                        questState.set("whispers_of_temptation", String.valueOf(1), true);
                        questState.set("whispers_of_temptation_ex", String.valueOf(0), true);
                    } else {
                        string2 = HtmCache.getInstance().getNotNull("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_28.htm", questState.getPlayer());
                        string2 = string2.replace("<?card1pic?>", String.valueOf(n4));
                        string2 = string2.replace("<?card2pic?>", String.valueOf(n21));
                        string2 = string2.replace("<?name?>", questState.getPlayer().getName());
                        n21 = n19 * 10 + n20;
                        questState.set("whispers_of_temptation_ex", String.valueOf(n21), true);
                        questState.set("whispers_of_temptation", String.valueOf(1005), true);
                    }
                } else if (n19 != n6) {
                    n7 = n19 + n5;
                    n8 = 66310 + n6;
                    n9 = 66310 + n19;
                    if (n20 == 5 || n5 == 5) {
                        string2 = HtmCache.getInstance().getNotNull("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_29.htm", questState.getPlayer());
                        string2 = string2.replace("<?card1pic?>", String.valueOf(n4));
                        string2 = string2.replace("<?card2pic?>", String.valueOf(n21));
                        string2 = string2.replace("<?name?>", questState.getPlayer().getName());
                        questState.set("whispers_of_temptation", String.valueOf(1), true);
                        questState.set("whispers_of_temptation_ex", String.valueOf(0), true);
                    } else {
                        string2 = HtmCache.getInstance().getNotNull("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_28.htm", questState.getPlayer());
                        string2 = string2.replace("<?card1pic?>", String.valueOf(n4));
                        string2 = string2.replace("<?card2pic?>", String.valueOf(n21));
                        string2 = string2.replace("<?name?>", questState.getPlayer().getName());
                        n21 = n19 * 10 + n20;
                        questState.set("whispers_of_temptation_ex", String.valueOf(n21), true);
                        questState.set("whispers_of_temptation", String.valueOf(1005), true);
                    }
                }
            } else if (string.equalsIgnoreCase("reply_20")) {
                questState.unset("whispers_of_temptation");
                questState.unset("whispers_of_temptation_ex");
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                string2 = "blacksmith_wilbert_q0663_30.htm";
            } else if (string.equalsIgnoreCase("reply_21")) {
                string2 = "blacksmith_wilbert_q0663_31.htm";
            } else if (string.equalsIgnoreCase("reply_22")) {
                string2 = "blacksmith_wilbert_q0663_32.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("whispers_of_temptation");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 30846) break;
                if (questState.getPlayer().getLevel() < 50) {
                    string = "blacksmith_wilbert_q0663_02.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                string = "blacksmith_wilbert_q0663_01.htm";
                break;
            }
            case 2: {
                if (n2 != 30846) break;
                if (n < 4 && n >= 1 && questState.getQuestItemsCount(8766) == 0L) {
                    string = "blacksmith_wilbert_q0663_04.htm";
                    break;
                }
                if (n < 4 && n >= 1 && questState.getQuestItemsCount(8766) > 0L) {
                    string = "blacksmith_wilbert_q0663_05.htm";
                    break;
                }
                if (n % 10 == 4 && n / 1000 == 0) {
                    string = "blacksmith_wilbert_q0663_05a.htm";
                    break;
                }
                if (n % 10 == 5 && n / 1000 == 0) {
                    string = "blacksmith_wilbert_q0663_11.htm";
                    break;
                }
                if (n % 10 == 6 && n / 1000 == 0) {
                    string = "blacksmith_wilbert_q0663_15.htm";
                    break;
                }
                if (n % 10 == 7 && n / 1000 == 0) {
                    int n4 = n % 100;
                    if (n4 / 10 >= 7) {
                        questState.set("whispers_of_temptation", String.valueOf(1), true);
                        questState.giveItems(57, 2384000L);
                        questState.giveItems(729, 1L);
                        questState.giveItems(730, 2L);
                        string = "blacksmith_wilbert_q0663_17.htm";
                        break;
                    }
                    int n5 = n / 10;
                    string = HtmCache.getInstance().getNotNull("quests/_663_SeductiveWhispers/blacksmith_wilbert_q0663_16.htm", questState.getPlayer());
                    string = string.replace("<?wincount?>", String.valueOf(n5 + 1));
                    break;
                }
                if (n == 1005) {
                    string = "blacksmith_wilbert_q0663_23.htm";
                    break;
                }
                if (n != 1006) break;
                string = "blacksmith_wilbert_q0663_26.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n;
        int n2 = questState.getInt("whispers_of_temptation");
        int n3 = npcInstance.getNpcId();
        if (n3 == 20674) {
            int n4;
            if (n2 >= 1 && n2 <= 4 && (n4 = Rnd.get(1000)) < 807) {
                questState.giveItems(8766, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n3 == 20678 || n3 == 20960) {
            int n5;
            if (n2 >= 1 && n2 <= 4 && (n5 = Rnd.get(1000)) < 372) {
                questState.giveItems(8766, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n3 == 20954) {
            int n6;
            if (n2 >= 1 && n2 <= 4 && (n6 = Rnd.get(1000)) < 460) {
                questState.giveItems(8766, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n3 == 20955) {
            int n7;
            if (n2 >= 1 && n2 <= 4 && (n7 = Rnd.get(1000)) < 537) {
                questState.giveItems(8766, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n3 == 20956 || n3 == 21007) {
            int n8;
            if (n2 >= 1 && n2 <= 4 && (n8 = Rnd.get(1000)) < 540) {
                questState.giveItems(8766, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n3 == 20957) {
            int n9;
            if (n2 >= 1 && n2 <= 4 && (n9 = Rnd.get(1000)) < 565) {
                questState.giveItems(8766, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n3 == 20958) {
            int n10;
            if (n2 >= 1 && n2 <= 4 && (n10 = Rnd.get(1000)) < 425) {
                questState.giveItems(8766, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n3 == 20959) {
            int n11;
            if (n2 >= 1 && n2 <= 4 && (n11 = Rnd.get(1000)) < 682) {
                questState.giveItems(8766, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n3 == 20961) {
            int n12;
            if (n2 >= 1 && n2 <= 4 && (n12 = Rnd.get(1000)) < 547) {
                questState.giveItems(8766, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n3 == 20962) {
            int n13;
            if (n2 >= 1 && n2 <= 4 && (n13 = Rnd.get(1000)) < 522) {
                questState.giveItems(8766, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n3 == 20963) {
            int n14;
            if (n2 >= 1 && n2 <= 4 && (n14 = Rnd.get(1000)) < 498) {
                questState.giveItems(8766, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n3 == 20974) {
            if (n2 >= 1 && n2 <= 4) {
                int n15 = Rnd.get(1000);
                if (n15 < 100) {
                    questState.giveItems(8766, 2L);
                    questState.playSound("ItemSound.quest_itemget");
                } else {
                    questState.giveItems(8766, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n3 == 20975) {
            int n16;
            if (n2 >= 1 && n2 <= 4 && (n16 = Rnd.get(1000)) < 975) {
                questState.giveItems(8766, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n3 == 20976) {
            int n17;
            if (n2 >= 1 && n2 <= 4 && (n17 = Rnd.get(1000)) < 825) {
                questState.giveItems(8766, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n3 == 20996) {
            int n18;
            if (n2 >= 1 && n2 <= 4 && (n18 = Rnd.get(1000)) < 385) {
                questState.giveItems(8766, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n3 == 20997) {
            int n19;
            if (n2 >= 1 && n2 <= 4 && (n19 = Rnd.get(1000)) < 342) {
                questState.giveItems(8766, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n3 == 20998) {
            int n20;
            if (n2 >= 1 && n2 <= 4 && (n20 = Rnd.get(1000)) < 377) {
                questState.giveItems(8766, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n3 == 20999) {
            int n21;
            if (n2 >= 1 && n2 <= 4 && (n21 = Rnd.get(1000)) < 450) {
                questState.giveItems(8766, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n3 == 21000) {
            int n22;
            if (n2 >= 1 && n2 <= 4 && (n22 = Rnd.get(1000)) < 395) {
                questState.giveItems(8766, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n3 == 21001) {
            int n23;
            if (n2 >= 1 && n2 <= 4 && (n23 = Rnd.get(1000)) < 535) {
                questState.giveItems(8766, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n3 == 21002) {
            int n24;
            if (n2 >= 1 && n2 <= 4 && (n24 = Rnd.get(1000)) < 472) {
                questState.giveItems(8766, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n3 == 21006) {
            int n25;
            if (n2 >= 1 && n2 <= 4 && (n25 = Rnd.get(1000)) < 502) {
                questState.giveItems(8766, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n3 == 21008) {
            int n26;
            if (n2 >= 1 && n2 <= 4 && (n26 = Rnd.get(1000)) < 692) {
                questState.giveItems(8766, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n3 == 21009) {
            int n27;
            if (n2 >= 1 && n2 <= 4 && (n27 = Rnd.get(1000)) < 740) {
                questState.giveItems(8766, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n3 == 21010 && n2 >= 1 && n2 <= 4 && (n = Rnd.get(1000)) < 595) {
            questState.giveItems(8766, 1L);
            questState.playSound("ItemSound.quest_itemget");
        }
        return null;
    }
}

