/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;

public class _334_TheWishingPotion
extends Quest
implements ScriptFile {
    private static final int bbY = 30738;
    private static final int bjj = 30742;
    private static final int bjk = 30557;
    private static final int bjl = 30743;
    private static final int bjm = 20078;
    private static final int bjn = 20087;
    private static final int bjo = 20088;
    private static final int bjp = 20168;
    private static final int bjq = 20192;
    private static final int bjr = 20193;
    private static final int bjs = 20199;
    private static final int bjt = 20227;
    private static final int bju = 20248;
    private static final int bjv = 20249;
    private static final int bjw = 20250;
    private static final int bjx = 27135;
    private static final int bjy = 27136;
    private static final int bjz = 27138;
    private static final int bjA = 27139;
    private static final int bjB = 27153;
    private static final int bjC = 27154;
    private static final int bjD = 27155;
    private static final int bjE = 3684;
    private static final int bjF = 3685;
    private static final int bjG = 3686;
    private static final int bjH = 3687;
    private static final int bjI = 3688;
    private static final int bjJ = 3689;
    private static final int bjK = 3690;
    private static final int bjL = 3691;
    private static final int bjM = 931;
    private static final int bjN = 3467;
    private static final int bjO = 3468;
    private static final int bjP = 3469;
    private static final int bjQ = 3678;
    private static final int bjR = 3679;
    private static final int bjS = 3680;
    private static final int bjT = 3681;
    private static final int bjU = 3682;
    private static final int bjV = 3683;
    private static final int bjW = 1979;
    private static final int bjX = 1980;
    private static final int bjY = 2952;
    private static final int bjZ = 2953;
    private static final int bka = 4408;
    private static final int bkb = 4409;
    private static int[] bkc = new int[]{3081, 3076, 3075, 3074, 4917, 3077, 3080, 3079, 3078, 4928, 4931, 4932, 5013, 3067, 3064, 3061, 3062, 3058, 4206, 3065, 3060, 3063, 4208, 3057, 3059, 3066, 4911, 4918, 3092, 3039, 4922, 3091, 3093, 3431};
    private static int[] bkd = new int[]{3430, 3429, 3073, 3941, 3071, 3069, 3072, 4200, 3068, 3070, 4912, 3100, 3101, 3098, 3094, 3102, 4913, 3095, 3096, 3097, 3099, 3085, 3086, 3082, 4907, 3088, 4207, 3087, 3084, 3083, 4929, 4933, 4919, 3045};
    private static int[] bke = new int[]{4923, 4201, 4914, 3942, 3090, 4909, 3089, 4930, 4934, 4920, 3041, 4924, 3114, 3105, 3110, 3104, 3113, 3103, 4204, 3108, 4926, 3112, 3107, 4205, 3109, 3111, 3106, 4925, 3117, 3115, 3118, 3116, 4927};
    private int bkf = 0;
    private int bkg = 0;
    private int aFa = 0;
    private int bkh = 0;
    private int bki = 0;

    public _334_TheWishingPotion() {
        super(0);
        this.addStartNpc(30738);
        this.addTalkId(30742, 30557, 30743);
        this.addKillId(20078, 20087, 20088, 20168, 20192, 20193, 20199, 20227, 20248, 20249, 20250, 27135, 27136, 27138, 27139, 27153, 27154, 27155);
        this.addQuestItem(3684, 3685, 3686, 3687, 3688, 3689, 3690, 3691);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        NpcInstance npcInstance2 = GameObjectsStorage.getByNpcId(30738);
        int n = npcInstance.getNpcId();
        if (n == 30738) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("wish_potion", String.valueOf(1), true);
                if (questState.getQuestItemsCount(3678) == 0L) {
                    questState.giveItems(3678, 1L);
                }
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "alchemist_matild_q0334_04.htm";
            } else if (string.equalsIgnoreCase("reply_1")) {
                string2 = "alchemist_matild_q0334_03.htm";
            } else if (string.equalsIgnoreCase("reply_2")) {
                questState.setCond(3);
                questState.set("wish_potion", String.valueOf(2), true);
                questState.takeItems(3679, -1L);
                questState.takeItems(3678, -1L);
                questState.giveItems(3680, 1L);
                questState.giveItems(3681, 1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "alchemist_matild_q0334_07.htm";
            } else if (string.equalsIgnoreCase("reply_3")) {
                string2 = "alchemist_matild_q0334_10.htm";
            } else if (string.equalsIgnoreCase("reply_4") && questState.getQuestItemsCount(3684) > 0L && questState.getQuestItemsCount(3686) > 0L && questState.getQuestItemsCount(3687) > 0L && questState.getQuestItemsCount(3688) > 0L && questState.getQuestItemsCount(3689) > 0L && questState.getQuestItemsCount(3690) > 0L && questState.getQuestItemsCount(3691) > 0L && questState.getQuestItemsCount(3685) > 0L && questState.getQuestItemsCount(3680) > 0L && questState.getQuestItemsCount(3681) > 0L) {
                questState.setCond(5);
                questState.set("wish_potion", String.valueOf(2), true);
                questState.giveItems(3467, 1L);
                if (questState.getQuestItemsCount(3682) == 0L) {
                    questState.giveItems(3682, 1L);
                }
                questState.takeItems(3684, 1L);
                questState.takeItems(3686, 1L);
                questState.takeItems(3687, 1L);
                questState.takeItems(3688, 1L);
                questState.takeItems(3689, 1L);
                questState.takeItems(3690, 1L);
                questState.takeItems(3691, 1L);
                questState.takeItems(3685, 1L);
                questState.takeItems(3680, -1L);
                questState.takeItems(3681, -1L);
                questState.playSound("ItemSound.quest_itemget");
                string2 = "alchemist_matild_q0334_11.htm";
            } else if (string.equalsIgnoreCase("reply_5")) {
                string2 = questState.getQuestItemsCount(3467) > 0L ? "alchemist_matild_q0334_13.htm" : "alchemist_matild_q0334_14.htm";
            } else if (string.equalsIgnoreCase("reply_6")) {
                if (questState.getQuestItemsCount(3467) > 0L) {
                    string2 = "alchemist_matild_q0334_15a.htm";
                } else {
                    string2 = "alchemist_matild_q0334_15.htm";
                    questState.giveItems(3680, 1L);
                    questState.giveItems(3681, 1L);
                }
            } else if (string.equalsIgnoreCase("reply_7")) {
                if (questState.getQuestItemsCount(3467) > 0L) {
                    if (this.bkf == 0) {
                        string2 = "alchemist_matild_q0334_16.htm";
                        questState.takeItems(3467, 1L);
                        this.aFa = 1;
                        this.bkg = 1;
                        questState.startQuestTimer("2336008", 3000L, npcInstance2);
                    } else {
                        string2 = "alchemist_matild_q0334_20.htm";
                    }
                } else {
                    string2 = "alchemist_matild_q0334_14.htm";
                }
            } else if (string.equalsIgnoreCase("reply_8")) {
                if (questState.getQuestItemsCount(3467) > 0L) {
                    if (this.bkf == 0) {
                        string2 = "alchemist_matild_q0334_17.htm";
                        questState.takeItems(3467, 1L);
                        this.aFa = 2;
                        this.bkg = 2;
                        questState.startQuestTimer("2336008", 3000L, npcInstance2);
                    } else {
                        string2 = "alchemist_matild_q0334_20.htm";
                    }
                } else {
                    string2 = "alchemist_matild_q0334_14.htm";
                }
            } else if (string.equalsIgnoreCase("reply_9")) {
                if (questState.getQuestItemsCount(3467) > 0L) {
                    if (this.bkf == 0) {
                        string2 = "alchemist_matild_q0334_18.htm";
                        questState.takeItems(3467, 1L);
                        this.aFa = 3;
                        this.bkg = 3;
                        questState.startQuestTimer("2336008", 3000L, npcInstance2);
                    } else {
                        string2 = "alchemist_matild_q0334_20.htm";
                    }
                } else {
                    string2 = "alchemist_matild_q0334_14.htm";
                }
            } else if (string.equalsIgnoreCase("reply_10")) {
                if (questState.getQuestItemsCount(3467) > 0L) {
                    if (this.bkf == 0) {
                        string2 = "alchemist_matild_q0334_18.htm";
                        questState.takeItems(3467, 1L);
                        this.aFa = 4;
                        this.bkg = 4;
                        questState.startQuestTimer("2336008", 3000L, npcInstance2);
                    } else {
                        string2 = "alchemist_matild_q0334_20.htm";
                    }
                } else {
                    string2 = "alchemist_matild_q0334_14.htm";
                }
            } else {
                if (string.equalsIgnoreCase("2336008")) {
                    Functions.npcSayCustomMessage(npcInstance2, "OK_EVERYBODY_PRAY_FERVENTLY", new Object[0]);
                    questState.startQuestTimer("2336009", 4000L, npcInstance2);
                    return null;
                }
                if (string.equalsIgnoreCase("2336009")) {
                    Functions.npcSayCustomMessage(npcInstance2, "BOTH_HANDS_TO_HEAVEN_EVERYBODY_YELL_TOGETHER", new Object[0]);
                    questState.startQuestTimer("2336010", 4000L, npcInstance2);
                    return null;
                }
                if (string.equalsIgnoreCase("2336010")) {
                    int n2 = 0;
                    Functions.npcSayCustomMessage(npcInstance2, "ONE_TWO_MAY_YOUR_DREAMS_COME_TRUE", new Object[0]);
                    if (this.aFa == 1) {
                        n2 = Rnd.get(2);
                    } else if (this.aFa == 3 || this.aFa == 4 || this.aFa == 2) {
                        n2 = Rnd.get(3);
                    }
                    switch (n2) {
                        case 0: {
                            if (this.aFa == 1) {
                                NpcInstance npcInstance3 = questState.addSpawn(30742);
                                Functions.npcSayCustomMessage(npcInstance3, "I_WILL_MAKE_YOUR_LOVE_COME_TRUE_LOVE_LOVE_LOVE", new Object[0]);
                                questState.startQuestTimer("2336001", 120000L, npcInstance3);
                                this.bkf = 1;
                                break;
                            }
                            if (this.aFa == 2) {
                                NpcInstance npcInstance4 = questState.addSpawn(27135, 120000);
                                NpcInstance npcInstance5 = questState.addSpawn(27135, 120000);
                                NpcInstance npcInstance6 = questState.addSpawn(27135, 120000);
                                Functions.npcSayCustomMessage(npcInstance4, "OH_OH_OH", new Object[0]);
                                Functions.npcSayCustomMessage(npcInstance5, "OH_OH_OH", new Object[0]);
                                Functions.npcSayCustomMessage(npcInstance6, "OH_OH_OH", new Object[0]);
                                questState.startQuestTimer("2336002", 120000L, npcInstance2);
                                this.bkf = 1;
                                break;
                            }
                            if (this.aFa == 3) {
                                questState.playSound("ItemSound.quest_itemget");
                                questState.giveItems(3469, 1L);
                                break;
                            }
                            if (this.aFa != 4) break;
                            NpcInstance npcInstance7 = questState.addSpawn(30743);
                            Functions.npcSayCustomMessage(npcInstance7, "I_HAVE_WISDOM_IN_ME_I_AM_THE_BOX_OF_WISDOM", new Object[0]);
                            questState.startQuestTimer("2336007", 120000L, npcInstance7);
                            this.bkf = 1;
                            break;
                        }
                        case 1: {
                            if (this.aFa == 1) {
                                NpcInstance npcInstance8 = questState.addSpawn(27136, 200000);
                                NpcInstance npcInstance9 = questState.addSpawn(27136, 200000);
                                NpcInstance npcInstance10 = questState.addSpawn(27136, 200000);
                                Functions.npcSayCustomMessage(npcInstance8, "DO_YOU_WANT_US_TO_LOVE_YOU_OH", new Object[0]);
                                Functions.npcSayCustomMessage(npcInstance9, "DO_YOU_WANT_US_TO_LOVE_YOU_OH", new Object[0]);
                                Functions.npcSayCustomMessage(npcInstance10, "DO_YOU_WANT_US_TO_LOVE_YOU_OH", new Object[0]);
                                questState.startQuestTimer("2336003", 200000L, npcInstance2);
                                this.bkf = 1;
                                break;
                            }
                            if (this.aFa == 2) {
                                questState.playSound("ItemSound.quest_itemget");
                                questState.giveItems(57, 10000L);
                                break;
                            }
                            if (this.aFa == 3) {
                                NpcInstance npcInstance11 = questState.addSpawn(27153);
                                Functions.npcSayCustomMessage(npcInstance11, "WHO_IS_CALLING_THE_LORD_OF_DARKNESS", new Object[0]);
                                questState.startQuestTimer("2336004", 200000L, npcInstance11);
                                this.bkf = 1;
                                break;
                            }
                            if (this.aFa != 4) break;
                            NpcInstance npcInstance12 = questState.addSpawn(30743);
                            Functions.npcSayCustomMessage(npcInstance12, "I_HAVE_WISDOM_IN_ME_I_AM_THE_BOX_OF_WISDOM", new Object[0]);
                            questState.startQuestTimer("2336007", 120000L, npcInstance12);
                            this.bkf = 1;
                            break;
                        }
                        case 2: {
                            if (this.aFa == 2) {
                                questState.playSound("ItemSound.quest_itemget");
                                questState.giveItems(57, 10000L);
                                break;
                            }
                            if (this.aFa == 3) {
                                questState.playSound("ItemSound.quest_itemget");
                                questState.giveItems(3468, 1L);
                                break;
                            }
                            if (this.aFa != 4) break;
                            NpcInstance npcInstance13 = questState.addSpawn(30743);
                            Functions.npcSayCustomMessage(npcInstance13, "I_HAVE_WISDOM_IN_ME_I_AM_THE_BOX_OF_WISDOM", new Object[0]);
                            questState.startQuestTimer("2336007", 120000L, npcInstance13);
                            this.bkf = 1;
                        }
                    }
                    return null;
                }
                if (string.equalsIgnoreCase("2336003")) {
                    this.bkf = 0;
                    return null;
                }
                if (string.equalsIgnoreCase("2336002")) {
                    this.bkf = 0;
                    return null;
                }
            }
        } else {
            if (string.equalsIgnoreCase("2336001")) {
                if (npcInstance != null) {
                    this.bkf = 0;
                }
                npcInstance.deleteMe();
                return null;
            }
            if (string.equalsIgnoreCase("2336007")) {
                if (npcInstance != null) {
                    this.bkf = 0;
                }
                npcInstance.deleteMe();
                return null;
            }
            if (string.equalsIgnoreCase("2336004")) {
                if (npcInstance != null) {
                    this.bkf = 0;
                }
                npcInstance.deleteMe();
                return null;
            }
            if (string.equalsIgnoreCase("2336107")) {
                if (npcInstance != null) {
                    Functions.npcSayCustomMessage(npcInstance, "DONT_INTERRUPT_MY_REST_AGAIN", new Object[0]);
                    Functions.npcSayCustomMessage(npcInstance, "YOURE_A_GREAT_DEVIL_NOW", new Object[0]);
                }
                this.bkf = 0;
                npcInstance.deleteMe();
                return null;
            }
            if (string.equalsIgnoreCase("2336005")) {
                if (npcInstance != null) {
                    Functions.npcSayCustomMessage(npcInstance, "OH_ITS_NOT_AN_OPPONENT_OF_MINE_HA_HA_HA", new Object[0]);
                }
                this.bkf = 0;
                npcInstance.deleteMe();
                return null;
            }
            if (string.equalsIgnoreCase("2336006")) {
                if (npcInstance != null) {
                    Functions.npcSayCustomMessage(npcInstance, "OH_ITS_NOT_AN_OPPONENT_OF_MINE_HA_HA_HA", new Object[0]);
                }
                this.bkf = 0;
                npcInstance.deleteMe();
                return null;
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
                if (n != 30738) break;
                if (questState.getPlayer().getLevel() < 30) {
                    string = "alchemist_matild_q0334_01.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                string = "alchemist_matild_q0334_02.htm";
                break;
            }
            case 2: {
                if (n == 30738) {
                    if (questState.getQuestItemsCount(3679) == 0L && questState.getQuestItemsCount(3678) == 1L) {
                        string = "alchemist_matild_q0334_05.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(3679) == 1L && questState.getQuestItemsCount(3678) == 1L) {
                        string = "alchemist_matild_q0334_06.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(3680) == 1L && questState.getQuestItemsCount(3681) == 1L && (questState.getQuestItemsCount(3684) == 0L || questState.getQuestItemsCount(3685) > 0L && questState.getQuestItemsCount(3686) == 0L || questState.getQuestItemsCount(3687) == 0L || questState.getQuestItemsCount(3688) == 0L || questState.getQuestItemsCount(3689) == 0L || questState.getQuestItemsCount(3690) == 0L || questState.getQuestItemsCount(3691) == 0L)) {
                        string = "alchemist_matild_q0334_08.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(3680) == 1L && questState.getQuestItemsCount(3681) == 1L && questState.getQuestItemsCount(3684) > 0L && questState.getQuestItemsCount(3685) > 0L && questState.getQuestItemsCount(3686) > 0L && questState.getQuestItemsCount(3687) > 0L && questState.getQuestItemsCount(3688) > 0L && questState.getQuestItemsCount(3689) > 0L && questState.getQuestItemsCount(3690) > 0L && questState.getQuestItemsCount(3691) > 0L) {
                        string = "alchemist_matild_q0334_09.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(3682) != 1L || questState.getQuestItemsCount(3680) != 0L || questState.getQuestItemsCount(3681) != 0L || questState.getQuestItemsCount(3684) != 0L && (questState.getQuestItemsCount(3685) <= 0L || questState.getQuestItemsCount(3686) != 0L) && questState.getQuestItemsCount(3687) != 0L && questState.getQuestItemsCount(3688) != 0L && questState.getQuestItemsCount(3689) != 0L && questState.getQuestItemsCount(3690) != 0L && questState.getQuestItemsCount(3691) != 0L) break;
                    string = "alchemist_matild_q0334_12.htm";
                    break;
                }
                if (n == 30742) {
                    if (this.bkg != 1) break;
                    if (Rnd.get(100) < 4) {
                        string = "fairy_rupina_q0334_01.htm";
                        questState.giveItems(931, 1L);
                        this.bkg = 0;
                        if (questState.isRunningQuestTimer("2336001")) {
                            questState.cancelQuestTimer("2336001");
                        }
                        this.bkf = 0;
                        npcInstance.deleteMe();
                        break;
                    }
                    string = "fairy_rupina_q0334_02.htm";
                    int n3 = Rnd.get(4);
                    if (n3 == 0) {
                        questState.giveItems(1979, 1L);
                    } else if (n3 == 1) {
                        questState.giveItems(1980, 1L);
                    } else if (n3 == 2) {
                        questState.giveItems(2952, 1L);
                    } else if (n3 == 3) {
                        questState.giveItems(2953, 1L);
                    }
                    this.bkg = 0;
                    if (questState.isRunningQuestTimer("2336001")) {
                        questState.cancelQuestTimer("2336001");
                    }
                    this.bkf = 0;
                    npcInstance.deleteMe();
                    break;
                }
                if (n == 30557) {
                    if (questState.getQuestItemsCount(3683) < 1L) break;
                    questState.giveItems(57, 500000L);
                    questState.takeItems(3683, 1L);
                    questState.playSound("ItemSound.quest_middle");
                    string = "torai_q0334_01.htm";
                    break;
                }
                if (n != 30743 || this.bkg != 4) break;
                int n4 = Rnd.get(10);
                if (n4 < 2) {
                    string = "wisdom_chest_q0334_01.htm";
                } else if (n4 >= 2 && n4 < 4) {
                    string = "wisdom_chest_q0334_02.htm";
                } else if (n4 >= 4 && n4 < 6) {
                    string = "wisdom_chest_q0334_03.htm";
                } else if (n4 == 6) {
                    string = "wisdom_chest_q0334_04.htm";
                } else if (n4 >= 7 && n4 < 9) {
                    string = "wisdom_chest_q0334_05.htm";
                } else if (n4 == 9) {
                    string = "wisdom_chest_q0334_06.htm";
                }
                int n5 = Rnd.get(bkc);
                questState.giveItems(n5, 1L);
                int n6 = Rnd.get(bkd);
                questState.giveItems(n6, 1L);
                int n7 = Rnd.get(bke);
                questState.giveItems(n7, 1L);
                if (Rnd.get(3) == 0) {
                    questState.giveItems(3943, 1L);
                }
                questState.giveItems(4408, 1L);
                questState.giveItems(4409, 1L);
                this.bkg = 0;
                this.bkf = 0;
                if (questState.isRunningQuestTimer("2336007")) {
                    questState.cancelQuestTimer("2336007");
                }
                npcInstance.deleteMe();
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getInt("wish_potion");
        int n2 = npcInstance.getNpcId();
        if (n2 == 20078) {
            if (questState.getQuestItemsCount(3680) > 0L && questState.getQuestItemsCount(3681) > 0L && questState.getQuestItemsCount(3685) == 0L && Rnd.get(10) == 0) {
                questState.giveItems(3685, 1L);
                if (questState.getQuestItemsCount(3684) >= 1L && questState.getQuestItemsCount(3686) >= 1L && questState.getQuestItemsCount(3687) >= 1L && questState.getQuestItemsCount(3688) >= 1L && questState.getQuestItemsCount(3689) >= 1L && questState.getQuestItemsCount(3689) >= 1L && questState.getQuestItemsCount(3690) >= 1L && questState.getQuestItemsCount(3691) >= 1L) {
                    questState.setCond(4);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 20087 || n2 == 20088) {
            if (questState.getQuestItemsCount(3680) > 0L && questState.getQuestItemsCount(3681) > 0L && questState.getQuestItemsCount(3689) == 0L && Rnd.get(10) == 0) {
                questState.giveItems(3689, 1L);
                if (questState.getQuestItemsCount(3684) >= 1L && questState.getQuestItemsCount(3685) >= 1L && questState.getQuestItemsCount(3686) >= 1L && questState.getQuestItemsCount(3687) >= 1L && questState.getQuestItemsCount(3688) >= 1L && questState.getQuestItemsCount(3689) >= 1L && questState.getQuestItemsCount(3690) >= 1L && questState.getQuestItemsCount(3691) >= 1L) {
                    questState.setCond(4);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 20168) {
            if (questState.getQuestItemsCount(3680) > 0L && questState.getQuestItemsCount(3681) > 0L && questState.getQuestItemsCount(3688) == 0L && Rnd.get(10) == 0) {
                questState.giveItems(3688, 1L);
                if (questState.getQuestItemsCount(3684) >= 1L && questState.getQuestItemsCount(3685) >= 1L && questState.getQuestItemsCount(3686) >= 1L && questState.getQuestItemsCount(3687) >= 1L && questState.getQuestItemsCount(3689) >= 1L && questState.getQuestItemsCount(3689) >= 1L && questState.getQuestItemsCount(3690) >= 1L && questState.getQuestItemsCount(3691) >= 1L) {
                    questState.setCond(4);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 20192 || n2 == 20193) {
            if (questState.getQuestItemsCount(3680) > 0L && questState.getQuestItemsCount(3681) > 0L && questState.getQuestItemsCount(3690) == 0L && Rnd.get(10) == 0) {
                questState.giveItems(3690, 1L);
                if (questState.getQuestItemsCount(3684) >= 1L && questState.getQuestItemsCount(3685) >= 1L && questState.getQuestItemsCount(3686) >= 1L && questState.getQuestItemsCount(3687) >= 1L && questState.getQuestItemsCount(3688) >= 1L && questState.getQuestItemsCount(3689) >= 1L && questState.getQuestItemsCount(3689) >= 1L && questState.getQuestItemsCount(3691) >= 1L) {
                    questState.setCond(4);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 20199) {
            if (questState.getQuestItemsCount(3680) > 0L && questState.getQuestItemsCount(3681) > 0L && questState.getQuestItemsCount(3684) == 0L && Rnd.get(10) == 0) {
                questState.giveItems(3684, 1L);
                if (questState.getQuestItemsCount(3685) >= 1L && questState.getQuestItemsCount(3686) >= 1L && questState.getQuestItemsCount(3687) >= 1L && questState.getQuestItemsCount(3688) >= 1L && questState.getQuestItemsCount(3689) >= 1L && questState.getQuestItemsCount(3689) >= 1L && questState.getQuestItemsCount(3690) >= 1L && questState.getQuestItemsCount(3691) >= 1L) {
                    questState.setCond(4);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 20227) {
            if (questState.getQuestItemsCount(3680) > 0L && questState.getQuestItemsCount(3681) > 0L && questState.getQuestItemsCount(3687) == 0L && Rnd.get(10) == 0) {
                questState.giveItems(3687, 1L);
                if (questState.getQuestItemsCount(3684) >= 1L && questState.getQuestItemsCount(3685) >= 1L && questState.getQuestItemsCount(3686) >= 1L && questState.getQuestItemsCount(3688) >= 1L && questState.getQuestItemsCount(3689) >= 1L && questState.getQuestItemsCount(3689) >= 1L && questState.getQuestItemsCount(3690) >= 1L && questState.getQuestItemsCount(3691) >= 1L) {
                    questState.setCond(4);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 20248 || n2 == 20249) {
            if (questState.getQuestItemsCount(3680) > 0L && questState.getQuestItemsCount(3681) > 0L && questState.getQuestItemsCount(3691) == 0L && Rnd.get(10) == 0) {
                questState.giveItems(3691, 1L);
                if (questState.getQuestItemsCount(3684) >= 1L && questState.getQuestItemsCount(3685) >= 1L && questState.getQuestItemsCount(3686) >= 1L && questState.getQuestItemsCount(3687) >= 1L && questState.getQuestItemsCount(3688) >= 1L && questState.getQuestItemsCount(3689) >= 1L && questState.getQuestItemsCount(3689) >= 1L && questState.getQuestItemsCount(3690) >= 1L) {
                    questState.setCond(4);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 20250) {
            if (questState.getQuestItemsCount(3680) > 0L && questState.getQuestItemsCount(3681) > 0L && questState.getQuestItemsCount(3686) == 0L && Rnd.get(10) == 0) {
                questState.giveItems(3686, 1L);
                if (questState.getQuestItemsCount(3684) >= 1L && questState.getQuestItemsCount(3685) >= 1L && questState.getQuestItemsCount(3687) >= 1L && questState.getQuestItemsCount(3688) >= 1L && questState.getQuestItemsCount(3689) >= 1L && questState.getQuestItemsCount(3689) >= 1L && questState.getQuestItemsCount(3690) >= 1L && questState.getQuestItemsCount(3691) >= 1L) {
                    questState.setCond(4);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 27135) {
            if (n == 2 && this.bkg == 2) {
                if (Rnd.get(1000) < 33) {
                    int n3 = Rnd.get(1000);
                    if (n3 == 0) {
                        questState.giveItems(57, 100000000L);
                    } else {
                        questState.giveItems(57, 900000L);
                    }
                    questState.playSound("ItemSound.quest_itemget");
                }
                ++this.bki;
                if (this.bki == 3) {
                    this.bkg = 0;
                    this.bkf = 0;
                    this.bki = 0;
                    if (questState.isRunningQuestTimer("2336002")) {
                        questState.cancelQuestTimer("2336002");
                    }
                }
            }
        } else if (n2 == 27136) {
            if (n == 2 && this.bkg == 1 && questState.getQuestItemsCount(3683) == 0L) {
                if (Rnd.get(1000) < 28) {
                    questState.giveItems(3683, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                    this.bkg = 0;
                    this.bkf = 0;
                    this.bkh = 0;
                    if (questState.isRunningQuestTimer("2336003")) {
                        questState.cancelQuestTimer("2336003");
                    }
                }
                ++this.bkh;
                if (this.bkh == 3) {
                    this.bkg = 0;
                    this.bkf = 0;
                    this.bkh = 0;
                    if (questState.isRunningQuestTimer("2336003")) {
                        questState.cancelQuestTimer("2336003");
                    }
                }
            }
        } else if (n2 == 27138) {
            if (n == 2 && this.bkg == 3) {
                questState.giveItems(57, 1406956L);
                questState.playSound("ItemSound.quest_itemget");
                this.bkg = 0;
                this.bkf = 0;
                if (questState.isRunningQuestTimer("2336107")) {
                    questState.cancelQuestTimer("2336107");
                }
            }
        } else if (n2 == 27139) {
            if (n == 1 && questState.getQuestItemsCount(3679) == 0L) {
                questState.setCond(2);
                questState.giveItems(3679, 1L);
                questState.playSound("ItemSound.quest_middle");
            }
        } else if (n2 == 27153) {
            if (n == 2 && this.bkg == 3) {
                Functions.npcSayCustomMessage(npcInstance, "BONAPARTERIUS_ABYSS_KING_WILL_PUNISH_YOU", new Object[0]);
                if (Rnd.get(2) == 0) {
                    NpcInstance npcInstance2 = questState.addSpawn(27154);
                    Functions.npcSayCustomMessage(npcInstance2, "I_AM_A_GREAT_EMPIRE_BONAPARTERIUS", new Object[0]);
                    questState.startQuestTimer("2336005", 200000L, npcInstance2);
                } else {
                    int n4 = Rnd.get(4);
                    if (n4 == 0) {
                        questState.giveItems(1979, 1L);
                    } else if (n4 == 1) {
                        questState.giveItems(1980, 1L);
                    } else if (n4 == 2) {
                        questState.giveItems(2952, 1L);
                    } else if (n4 == 3) {
                        questState.giveItems(2953, 1L);
                    }
                    this.bkf = 0;
                    this.bkg = 0;
                    if (questState.isRunningQuestTimer("2336004")) {
                        questState.cancelQuestTimer("2336004");
                    }
                }
            }
        } else if (n2 == 27154) {
            if (n == 2 && this.bkg == 3) {
                Functions.npcSayCustomMessage(npcInstance, "REVENGE_IS_OVERLORD_RAMSEBALIUS_OF_THE_EVIL_WORLD", new Object[0]);
                if (Rnd.get(2) == 0) {
                    NpcInstance npcInstance3 = questState.addSpawn(27155);
                    Functions.npcSayCustomMessage(npcInstance3, "LET_YOUR_HEAD_DOWN_BEFORE_THE_LORD", new Object[0]);
                    questState.startQuestTimer("2336006", 200000L, npcInstance3);
                } else {
                    int n5 = Rnd.get(4);
                    if (n5 == 0) {
                        questState.giveItems(1979, 1L);
                    } else if (n5 == 1) {
                        questState.giveItems(1980, 1L);
                    } else if (n5 == 2) {
                        questState.giveItems(2952, 1L);
                    } else if (n5 == 3) {
                        questState.giveItems(2953, 1L);
                    }
                    this.bkf = 0;
                    this.bkg = 0;
                    if (questState.isRunningQuestTimer("2336005")) {
                        questState.cancelQuestTimer("2336005");
                    }
                }
            }
        } else if (n2 == 27155 && n == 2 && this.bkg == 3) {
            Functions.npcSayCustomMessage(npcInstance, "OH_GREAT_DEMON_KING", new Object[0]);
            if (Rnd.get(2) == 0) {
                NpcInstance npcInstance4 = questState.addSpawn(27138);
                Functions.npcSayCustomMessage(npcInstance4, "WHO_KILLED_MY_UNDERLING_DEVIL", new Object[0]);
                questState.startQuestTimer("2336107", 600000L, npcInstance4);
            } else {
                int n6 = Rnd.get(4);
                if (n6 == 0) {
                    questState.giveItems(1979, 1L);
                } else if (n6 == 1) {
                    questState.giveItems(1980, 1L);
                } else if (n6 == 2) {
                    questState.giveItems(2952, 1L);
                } else if (n6 == 3) {
                    questState.giveItems(2953, 1L);
                }
                this.bkf = 0;
                this.bkg = 0;
                if (questState.isRunningQuestTimer("2336006")) {
                    questState.cancelQuestTimer("2336006");
                }
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

