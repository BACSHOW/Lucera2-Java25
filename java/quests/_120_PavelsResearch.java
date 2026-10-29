/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.s2c.MagicSkillUse;
import l2.gameserver.scripts.ScriptFile;
import quests._114_ResurrectionOfAnOldManager;

public class _120_PavelsResearch
extends Quest
implements ScriptFile {
    private static final int aEQ = 32041;
    private static final int aFR = 32042;
    private static final int aFS = 32043;
    private static final int aFT = 32044;
    private static final int aFU = 32045;
    private static final int aER = 32046;
    private static final int aES = 32047;
    private static final int aFV = 854;
    private static final int aFW = 8058;
    private static final int aFX = 8059;
    private static final int aFY = 8060;
    private static final int aFZ = 8290;
    private static final int aGa = 8291;
    private static final int aGb = 8292;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _120_PavelsResearch() {
        super(0);
        this.addStartNpc(32046);
        this.addTalkId(32046, 32041, 32047, 32045, 32042, 32043, 32044);
        this.addQuestItem(8058, 8059, 8060, 8290, 8291, 8292);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        Player player = questState.getPlayer();
        int n = questState.getInt("last_research_pavel");
        int n2 = questState.getInt("last_research_ex");
        int n3 = npcInstance.getNpcId();
        if (n3 == 32046) {
            if (string.equalsIgnoreCase("quest_accept")) {
                if (questState.getPlayer().getLevel() >= 50) {
                    questState.setCond(1);
                    questState.set("last_research_pavel", String.valueOf(1), true);
                    questState.setState(2);
                    questState.playSound("ItemSound.quest_accept");
                    string2 = "pavel_atlanta_q0120_09.htm";
                } else {
                    string2 = "pavel_atlanta_q0120_08.htm";
                }
            } else if (string.equalsIgnoreCase("reply_1")) {
                string2 = "pavel_atlanta_q0120_05.htm";
            } else if (string.equalsIgnoreCase("reply_2")) {
                if (n == 1) {
                    questState.setCond(2);
                    questState.set("last_research_pavel", String.valueOf(2), true);
                    questState.set("last_research_ex", String.valueOf(0), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "pavel_atlanta_q0120_12.htm";
                }
            } else if (string.equalsIgnoreCase("reply_3")) {
                if (n == 3) {
                    questState.setCond(6);
                    questState.set("last_research_pavel", String.valueOf(4), true);
                    questState.giveItems(8290, 1L);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "pavel_atlanta_q0120_16.htm";
                }
            } else if (string.equalsIgnoreCase("reply_5")) {
                if (n == 7) {
                    questState.setCond(10);
                    questState.set("last_research_pavel", String.valueOf(8), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "pavel_atlanta_q0120_25.htm";
                }
            } else if (string.equalsIgnoreCase("reply_6")) {
                if (n == 11) {
                    questState.setCond(13);
                    questState.set("last_research_pavel", String.valueOf(12), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "pavel_atlanta_q0120_32.htm";
                }
            } else if (string.equalsIgnoreCase("reply_7")) {
                if (n == 19) {
                    questState.setCond(20);
                    questState.set("last_research_pavel", String.valueOf(20), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "pavel_atlanta_q0120_38.htm";
                }
            } else if (string.equalsIgnoreCase("reply_8") && n == 22) {
                questState.setCond(23);
                questState.set("last_research_pavel", String.valueOf(23), true);
                questState.giveItems(8291, 1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "pavel_atlanta_q0120_41.htm";
            }
        } else if (n3 == 32041) {
            if (string.equalsIgnoreCase("reply_1")) {
                string2 = "collecter_yumi_q0120_02.htm";
            } else if (string.equalsIgnoreCase("reply_2")) {
                if (n == 2 && n2 == 0) {
                    questState.setCond(3);
                    questState.set("last_research_ex", String.valueOf(1), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "collecter_yumi_q0120_03.htm";
                }
            } else if (string.equalsIgnoreCase("reply_3")) {
                if (n == 2 && n2 == 0) {
                    questState.setCond(4);
                    questState.set("last_research_ex", String.valueOf(2), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "collecter_yumi_q0120_05.htm";
                }
            } else if (string.equalsIgnoreCase("reply_5")) {
                if (n == 5) {
                    questState.setCond(8);
                    questState.set("last_research_pavel", String.valueOf(6), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "collecter_yumi_q0120_13.htm";
                }
            } else if (string.equalsIgnoreCase("reply_6")) {
                if (n == 14) {
                    questState.setCond(16);
                    questState.set("last_research_pavel", String.valueOf(15), true);
                    questState.giveItems(8060, 1L);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "collecter_yumi_q0120_17.htm";
                }
            } else if (string.equalsIgnoreCase("reply_7")) {
                if (n == 15 && questState.getQuestItemsCount(8059) >= 1L) {
                    questState.setCond(17);
                    questState.set("last_research_pavel", String.valueOf(16), true);
                    questState.takeItems(8060, -1L);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "collecter_yumi_q0120_23.htm";
                }
            } else if (string.equalsIgnoreCase("reply_8")) {
                string2 = "collecter_yumi_q0120_30.htm";
            } else if (string.equalsIgnoreCase("reply_9")) {
                string2 = "collecter_yumi_q0120_31.htm";
            } else if (string.equalsIgnoreCase("reply_10") && n == 26) {
                questState.giveItems(854, 1L);
                questState.giveItems(57, 783720L);
                questState.addExpAndSp(3447315L, 272615L);
                questState.takeItems(8292, -1L);
                questState.unset("last_research_pavel");
                questState.unset("last_research_ex");
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(false);
                string2 = "collecter_yumi_q0120_33.htm";
            }
        } else if (n3 == 32047) {
            if (string.equalsIgnoreCase("reply_1")) {
                string2 = "chaos_secretary_wendy_q0120_02.htm";
            } else if (string.equalsIgnoreCase("reply_2")) {
                if (n == 2) {
                    questState.setCond(5);
                    questState.set("last_research_pavel", String.valueOf(3), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "chaos_secretary_wendy_q0120_06.htm";
                }
            } else if (string.equalsIgnoreCase("reply_4")) {
                string2 = "chaos_secretary_wendy_q0120_09.htm";
            } else if (string.equalsIgnoreCase("reply_5")) {
                if (n == 4) {
                    questState.setCond(7);
                    questState.set("last_research_pavel", String.valueOf(5), true);
                    questState.playSound("ItemSound.quest_middle");
                    questState.takeItems(8290, -1L);
                    string2 = "chaos_secretary_wendy_q0120_10.htm";
                }
            } else if (string.equalsIgnoreCase("reply_6")) {
                if (n == 6) {
                    questState.setCond(9);
                    questState.set("last_research_pavel", String.valueOf(7), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "chaos_secretary_wendy_q0120_14.htm";
                }
            } else if (string.equalsIgnoreCase("reply_7")) {
                if (n == 12) {
                    questState.setCond(14);
                    questState.set("last_research_pavel", String.valueOf(13), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "chaos_secretary_wendy_q0120_18.htm";
                }
            } else if (string.equalsIgnoreCase("reply_8")) {
                if (n == 23) {
                    questState.setCond(24);
                    questState.set("last_research_pavel", String.valueOf(24), true);
                    questState.takeItems(8291, -1L);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "chaos_secretary_wendy_q0120_26.htm";
                }
            } else if (string.equalsIgnoreCase("reply_9")) {
                if (n == 24) {
                    questState.set("last_research_pavel", String.valueOf(25), true);
                    string2 = "chaos_secretary_wendy_q0120_29.htm";
                }
            } else if (string.equalsIgnoreCase("reply_10") && n == 25) {
                questState.setCond(25);
                questState.set("last_research_pavel", String.valueOf(26), true);
                questState.giveItems(8292, 1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "chaos_secretary_wendy_q0120_32.htm";
            }
        } else if (n3 == 32045) {
            if (string.equalsIgnoreCase("reply_1") && n == 13) {
                questState.setCond(15);
                questState.set("last_research_pavel", String.valueOf(14), true);
                questState.giveItems(8058, 1L);
                if (player != null) {
                    npcInstance.broadcastPacket(new MagicSkillUse(npcInstance, player, 5073, 5, 1500, 0L));
                }
                questState.playSound("ItemSound.quest_middle");
                string2 = "drchaos_box_q0120_02.htm";
            }
        } else if (n3 == 32042) {
            if (string.equalsIgnoreCase("reply_90")) {
                if (n == 8) {
                    questState.set("last_research_ex", String.valueOf(0), true);
                    string2 = "weather_controller1_q0120_02.htm";
                }
            } else if (string.equalsIgnoreCase("reply_1")) {
                if (n == 8) {
                    questState.set("last_research_ex", String.valueOf(1), true);
                    string2 = "weather_controller1_q0120_03.htm";
                }
            } else if (string.equalsIgnoreCase("reply_2")) {
                if (n == 8) {
                    int n4 = n2;
                    questState.set("last_research_ex", String.valueOf(10 + (n4 %= 10)), true);
                    string2 = "weather_controller1_q0120_04.htm";
                }
            } else if (string.equalsIgnoreCase("reply_3")) {
                if (n == 8 && n2 != 11) {
                    string2 = "weather_controller1_q0120_05.htm";
                }
                if (n == 8 && n2 == 11) {
                    questState.setCond(11);
                    questState.set("last_research_pavel", String.valueOf(9), true);
                    questState.set("last_research_ex", String.valueOf(0), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "weather_controller1_q0120_06.htm";
                }
            } else if (string.equalsIgnoreCase("reply_4")) {
                string2 = "weather_controller1_q0120_07.htm";
            } else if (string.equalsIgnoreCase("reply_5")) {
                if (n == 9) {
                    questState.set("last_research_pavel", String.valueOf(10), true);
                    questState.playSound("AmbSound.dt_percussion_01");
                    string2 = "weather_controller1_q0120_08.htm";
                }
            } else if (string.equalsIgnoreCase("reply_6")) {
                if (n == 10 && n2 != 10101) {
                    string2 = "weather_controller1_q0120_09.htm";
                }
                if (n == 10 && n2 == 10101) {
                    string2 = "weather_controller1_q0120_13.htm";
                }
            } else if (string.equalsIgnoreCase("reply_7")) {
                if (n == 10) {
                    int n5 = n2;
                    questState.set("last_research_ex", String.valueOf((n5 /= 10) * 10 + 1), true);
                    string2 = "weather_controller1_q0120_10.htm";
                }
            } else if (string.equalsIgnoreCase("reply_8")) {
                if (n == 10) {
                    int n6 = n2;
                    int n7 = n6 / 1000;
                    int n8 = n6 % 100;
                    questState.set("last_research_ex", String.valueOf(n7 * 1000 + 100 + n8), true);
                    string2 = "weather_controller1_q0120_11.htm";
                }
            } else if (string.equalsIgnoreCase("reply_9")) {
                if (n == 10) {
                    int n9 = n2;
                    int n10 = n9 % 10000;
                    questState.set("last_research_ex", String.valueOf(10000 + n10), true);
                    string2 = "weather_controller1_q0120_12.htm";
                }
            } else if (string.equalsIgnoreCase("reply_10") && n == 10 && n2 == 10101) {
                questState.setCond(12);
                questState.set("last_research_pavel", String.valueOf(11), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "weather_controller1_q0120_13a.htm";
            }
        } else if (n3 == 32043) {
            if (string.equalsIgnoreCase("reply_90")) {
                if (n == 16) {
                    questState.set("last_research_ex", String.valueOf(0), true);
                    string2 = "weather_controller2_q0120_02.htm";
                }
            } else if (string.equalsIgnoreCase("reply_1")) {
                if (n == 16) {
                    questState.set("last_research_ex", String.valueOf(1), true);
                    string2 = "weather_controller2_q0120_03.htm";
                }
            } else if (string.equalsIgnoreCase("reply_2")) {
                if (n == 16) {
                    int n11 = n2;
                    questState.set("last_research_ex", String.valueOf(10 + (n11 %= 10)), true);
                    string2 = "weather_controller2_q0120_04.htm";
                }
            } else if (string.equalsIgnoreCase("reply_3")) {
                if (n == 16 && n2 != 11) {
                    string2 = "weather_controller2_q0120_05.htm";
                }
                if (n == 16 && n2 == 11) {
                    questState.setCond(18);
                    questState.set("last_research_pavel", String.valueOf(17), true);
                    questState.set("last_research_ex", String.valueOf(0), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "weather_controller2_q0120_06.htm";
                }
            } else if (string.equalsIgnoreCase("reply_4")) {
                string2 = "weather_controller2_q0120_07.htm";
            } else if (string.equalsIgnoreCase("reply_5")) {
                if (n == 17) {
                    questState.set("last_research_pavel", String.valueOf(18), true);
                    string2 = "weather_controller2_q0120_09.htm";
                }
            } else if (string.equalsIgnoreCase("reply_6")) {
                if (n == 18 && n2 != 1111) {
                    string2 = "weather_controller2_q0120_11.htm";
                }
                if (n == 18 && n2 == 1111) {
                    string2 = "weather_controller2_q0120_11b.htm";
                }
            } else if (string.equalsIgnoreCase("reply_7")) {
                if (n == 18) {
                    int n12 = n2;
                    questState.set("last_research_ex", String.valueOf((n12 /= 10) * 10 + 1), true);
                    string2 = "weather_controller2_q0120_12.htm";
                }
            } else if (string.equalsIgnoreCase("reply_10")) {
                string2 = "weather_controller2_q0120_13.htm";
            } else if (string.equalsIgnoreCase("reply_8")) {
                if (n == 18 && n2 < 1000) {
                    string2 = "weather_controller2_q0120_14.htm";
                }
                if (n == 18 && n2 >= 1000) {
                    string2 = "weather_controller2_q0120_17.htm";
                }
            } else if (string.equalsIgnoreCase("reply_71")) {
                string2 = "weather_controller2_q0120_15.htm";
            } else if (string.equalsIgnoreCase("reply_11")) {
                if (n == 18) {
                    int n13 = n2;
                    int n14 = n13 / 10000;
                    int n15 = n13 % 1000;
                    questState.set("last_research_ex", String.valueOf(n14 * 10000 + 1000 + n15), true);
                    questState.playSound("AmbSound.ed_drone_02");
                    string2 = "weather_controller2_q0120_16.htm";
                }
            } else if (string.equalsIgnoreCase("reply_9")) {
                string2 = "weather_controller2_q0120_19.htm";
            } else if (string.equalsIgnoreCase("reply_12")) {
                if (n == 18) {
                    int n16 = n2;
                    int n17 = n16 / 100;
                    int n18 = n16 % 10;
                    questState.set("last_research_ex", String.valueOf(n17 * 100 + 10 + n18), true);
                    string2 = "weather_controller2_q0120_20.htm";
                }
            } else if (string.equalsIgnoreCase("reply_14")) {
                string2 = "weather_controller2_q0120_23.htm";
            } else if (string.equalsIgnoreCase("reply_72")) {
                if (n == 18) {
                    int n19 = n2;
                    int n20 = n19 / 1000;
                    int n21 = n19 % 100;
                    questState.set("last_research_ex", String.valueOf(n20 * 1000 + 100 + n21), true);
                    string2 = "weather_controller2_q0120_25.htm";
                }
            } else if (string.equalsIgnoreCase("reply_13") && n == 18 && n2 == 1111) {
                questState.setCond(19);
                questState.set("last_research_pavel", String.valueOf(19), true);
                questState.set("last_research_ex", String.valueOf(0), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "weather_controller2_q0120_21.htm";
            }
        } else if (n3 == 32044) {
            if (string.equalsIgnoreCase("reply_90")) {
                if (n == 20) {
                    questState.set("last_research_ex", String.valueOf(0), true);
                    string2 = "weather_controller3_q0120_02.htm";
                }
            } else if (string.equalsIgnoreCase("reply_1")) {
                if (n == 20) {
                    questState.set("last_research_ex", String.valueOf(1), true);
                    string2 = "weather_controller3_q0120_03.htm";
                }
            } else if (string.equalsIgnoreCase("reply_2")) {
                if (n == 20) {
                    int n22 = n2;
                    questState.set("last_research_ex", String.valueOf(10 + (n22 %= 10)), true);
                    string2 = "weather_controller3_q0120_04.htm";
                }
            } else if (string.equalsIgnoreCase("reply_3")) {
                if (n == 20 && n2 != 11) {
                    string2 = "weather_controller3_q0120_05.htm";
                }
                if (n == 20 && n2 == 11) {
                    questState.setCond(21);
                    questState.set("last_research_pavel", String.valueOf(21), true);
                    questState.set("last_research_ex", String.valueOf(0), true);
                    questState.playSound("AmbSound.ac_percussion_02");
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "weather_controller3_q0120_06.htm";
                }
            } else if (string.equalsIgnoreCase("reply_4")) {
                string2 = "weather_controller3_q0120_07.htm";
            } else if (string.equalsIgnoreCase("reply_100")) {
                if (n == 21 && n2 % 100 != 11) {
                    string2 = "weather_controller3_q0120_09.htm";
                }
                if (n == 21 && n2 % 100 == 11) {
                    string2 = "weather_controller3_q0120_09a.htm";
                }
            } else if (string.equalsIgnoreCase("reply_5")) {
                if (n == 21) {
                    int n23 = n2;
                    int n24 = n23 / 100;
                    int n25 = n23 % 10;
                    questState.set("last_research_ex", String.valueOf(n24 * 100 + 10 + n25), true);
                    string2 = "weather_controller3_q0120_10.htm";
                }
            } else if (string.equalsIgnoreCase("reply_6")) {
                if (n == 21 && n2 / 100 != 1) {
                    int n26 = n2;
                    int n27 = n26 / 10;
                    questState.set("last_research_ex", String.valueOf(n27 * 10 + 1), true);
                    string2 = "weather_controller3_q0120_11.htm";
                }
                if (n == 21 && n2 / 100 == 1) {
                    string2 = "weather_controller3_q0120_11a.htm";
                }
            } else if (string.equalsIgnoreCase("reply_7")) {
                if (n == 21 && n2 / 100 != 1) {
                    string2 = "weather_controller3_q0120_12.htm";
                }
                if (n == 21 && n2 / 100 == 1) {
                    string2 = "weather_controller3_q0120_12a.htm";
                }
            } else if (string.equalsIgnoreCase("reply_8")) {
                if (n == 21 && n2 / 100 != 1) {
                    int n28 = n2;
                    questState.set("last_research_ex", String.valueOf(100 + (n28 %= 100)), true);
                    string2 = "weather_controller3_q0120_13.htm";
                }
            } else if (string.equalsIgnoreCase("reply_9")) {
                string2 = "weather_controller3_q0120_14.htm";
            } else if (string.equalsIgnoreCase("reply_10")) {
                string2 = "weather_controller3_q0120_15.htm";
            } else if (string.equalsIgnoreCase("reply_11")) {
                string2 = "weather_controller3_q0120_16.htm";
            } else if (string.equalsIgnoreCase("reply_12") && n == 21 && n2 / 100 == 1) {
                questState.setCond(22);
                questState.set("last_research_pavel", String.valueOf(22), true);
                questState.playSound("AmbSound.ed_drone_02");
                if (player != null) {
                    npcInstance.broadcastPacket(new MagicSkillUse(npcInstance, player, 5073, 5, 1500, 0L));
                }
                questState.playSound("ItemSound.quest_middle");
                string2 = "weather_controller3_q0120_17.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        QuestState questState2 = questState.getPlayer().getQuestState(_114_ResurrectionOfAnOldManager.class);
        int n = questState.getInt("last_research_pavel");
        int n2 = questState.getInt("last_research_ex");
        int n3 = npcInstance.getNpcId();
        int n4 = questState.getState();
        switch (n4) {
            case 1: {
                if (n3 != 32046) break;
                if (questState2 != null && questState2.isCompleted() && questState.getPlayer().getLevel() >= 50) {
                    string = "pavel_atlanta_q0120_01.htm";
                    break;
                }
                string = "pavel_atlanta_q0120_03.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n3 == 32046) {
                    if (n == 1) {
                        string = "pavel_atlanta_q0120_10.htm";
                        break;
                    }
                    if (n == 2) {
                        string = "pavel_atlanta_q0120_13.htm";
                        break;
                    }
                    if (n == 3) {
                        string = "pavel_atlanta_q0120_14.htm";
                        break;
                    }
                    if (n == 4) {
                        string = "pavel_atlanta_q0120_16a.htm";
                        break;
                    }
                    if (n == 7) {
                        string = "pavel_atlanta_q0120_17.htm";
                        break;
                    }
                    if (n == 8) {
                        string = "pavel_atlanta_q0120_28.htm";
                        break;
                    }
                    if (n == 11) {
                        string = "pavel_atlanta_q0120_29.htm";
                        break;
                    }
                    if (n == 12) {
                        string = "pavel_atlanta_q0120_33.htm";
                        break;
                    }
                    if (n == 19) {
                        string = "pavel_atlanta_q0120_34.htm";
                        break;
                    }
                    if (n == 20) {
                        string = "pavel_atlanta_q0120_39.htm";
                        break;
                    }
                    if (n == 22) {
                        string = "pavel_atlanta_q0120_40.htm";
                        break;
                    }
                    if (n != 23) break;
                    string = "pavel_atlanta_q0120_42.htm";
                    break;
                }
                if (n3 == 32041) {
                    if (n == 2 && n2 == 0) {
                        string = "collecter_yumi_q0120_01.htm";
                        break;
                    }
                    if (n == 2 && n2 == 1) {
                        string = "collecter_yumi_q0120_04.htm";
                        break;
                    }
                    if (n == 2 && n2 == 2) {
                        string = "collecter_yumi_q0120_06.htm";
                        break;
                    }
                    if (n == 5 && n2 > 0) {
                        string = "collecter_yumi_q0120_07.htm";
                        break;
                    }
                    if (n == 5 && n2 == 0) {
                        string = "collecter_yumi_q0120_08.htm";
                        break;
                    }
                    if (n == 6) {
                        string = "collecter_yumi_q0120_14.htm";
                        break;
                    }
                    if (n == 14) {
                        string = "collecter_yumi_q0120_15.htm";
                        break;
                    }
                    if (n == 15 && questState.getQuestItemsCount(8059) == 0L) {
                        string = "collecter_yumi_q0120_18.htm";
                        break;
                    }
                    if (n == 15 && questState.getQuestItemsCount(8059) >= 1L) {
                        string = "collecter_yumi_q0120_19.htm";
                        break;
                    }
                    if (n == 16) {
                        string = "collecter_yumi_q0120_26.htm";
                        break;
                    }
                    if (n != 26) break;
                    string = "collecter_yumi_q0120_27.htm";
                    break;
                }
                if (n3 == 32047) {
                    if (n == 2) {
                        string = "chaos_secretary_wendy_q0120_01.htm";
                        break;
                    }
                    if (n == 3) {
                        string = "chaos_secretary_wendy_q0120_07.htm";
                        break;
                    }
                    if (n == 4) {
                        string = "chaos_secretary_wendy_q0120_08.htm";
                        break;
                    }
                    if (n == 5) {
                        string = "chaos_secretary_wendy_q0120_11.htm";
                        break;
                    }
                    if (n == 6) {
                        string = "chaos_secretary_wendy_q0120_11z.htm";
                        break;
                    }
                    if (n == 7) {
                        string = "chaos_secretary_wendy_q0120_15.htm";
                        break;
                    }
                    if (n == 12) {
                        string = "chaos_secretary_wendy_q0120_16.htm";
                        break;
                    }
                    if (n == 13) {
                        string = "chaos_secretary_wendy_q0120_19.htm";
                        break;
                    }
                    if (n == 14) {
                        string = "chaos_secretary_wendy_q0120_20.htm";
                        break;
                    }
                    if (n == 23) {
                        string = "chaos_secretary_wendy_q0120_21.htm";
                        break;
                    }
                    if (n == 24) {
                        string = "chaos_secretary_wendy_q0120_26.htm";
                        break;
                    }
                    if (n == 25) {
                        string = "chaos_secretary_wendy_q0120_29.htm";
                        break;
                    }
                    if (n != 26) break;
                    string = "chaos_secretary_wendy_q0120_33.htm";
                    break;
                }
                if (n3 == 32045) {
                    if (n == 13) {
                        string = "drchaos_box_q0120_01.htm";
                        break;
                    }
                    if (n != 14) break;
                    string = "drchaos_box_q0120_03.htm";
                    break;
                }
                if (n3 == 32042) {
                    if (n == 8) {
                        questState.playSound("AmbSound.cd_crystal_loop");
                        string = "weather_controller1_q0120_01.htm";
                        break;
                    }
                    if (n == 9) {
                        string = "weather_controller1_q0120_06.htm";
                        break;
                    }
                    if (n == 10 && n2 != 10101) {
                        string = "weather_controller1_q0120_09.htm";
                        break;
                    }
                    if (n == 10 && n2 == 10101) {
                        string = "weather_controller1_q0120_13.htm";
                        break;
                    }
                    if (n != 11) break;
                    string = "weather_controller1_q0120_13a.htm";
                    break;
                }
                if (n3 == 32043) {
                    if (n == 16) {
                        string = "weather_controller2_q0120_01.htm";
                        break;
                    }
                    if (n == 17) {
                        string = "weather_controller2_q0120_06.htm";
                        break;
                    }
                    if (n == 18) {
                        string = "weather_controller2_q0120_09.htm";
                        break;
                    }
                    if (n != 19) break;
                    string = "weather_controller2_q0120_22.htm";
                    break;
                }
                if (n3 != 32044) break;
                if (n == 20) {
                    string = "weather_controller3_q0120_01.htm";
                    break;
                }
                if (n == 21) {
                    string = "weather_controller3_q0120_08.htm";
                    break;
                }
                if (n != 22) break;
                string = "weather_controller3_q0120_19.htm";
            }
        }
        return string;
    }
}

