/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import quests._121_PavelTheGiants;

public class _114_ResurrectionOfAnOldManager
extends Quest
implements ScriptFile {
    private static final int aEP = 31961;
    private static final int aEQ = 32041;
    private static final int aER = 32046;
    private static final int aES = 32047;
    private static final int aET = 32050;
    private static final int aEU = 27318;
    private static final int aEV = 8090;
    private static final int aEW = 8091;
    private static final int aEX = 8287;
    private static final int aEY = 8288;
    private static final int aEZ = 8289;
    private int aFa = 0;
    private String aFb;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _114_ResurrectionOfAnOldManager() {
        super(0);
        this.addStartNpc(32041);
        this.addTalkId(32047, 32050, 32046, 31961);
        this.addKillId(27318);
        this.addQuestItem(8090, 8091, 8287, 8288, 8289);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getInt("return_of_old_susceptor");
        int n2 = questState.getInt("return_of_old_susceptor_ex");
        int n3 = npcInstance.getNpcId();
        if (n3 == 32041) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("return_of_old_susceptor", String.valueOf(0), true);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "collecter_yumi_q0114_04.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=1")) {
                if (n == 0) {
                    questState.set("return_of_old_susceptor", String.valueOf(1), true);
                    string2 = "collecter_yumi_q0114_08.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=2")) {
                if (n == 1) {
                    questState.setCond(2);
                    questState.set("return_of_old_susceptor", String.valueOf(2), true);
                    questState.set("return_of_old_susceptor_ex", String.valueOf(0), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "collecter_yumi_q0114_09.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=7")) {
                if (n == 3 && n2 == 10011) {
                    string2 = "collecter_yumi_q0114_12.htm";
                } else if (n == 3 && n2 == 20011) {
                    string2 = "collecter_yumi_q0114_13.htm";
                } else if (n == 3 && n2 == 30011) {
                    string2 = "collecter_yumi_q0114_14.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=8")) {
                if (n == 3) {
                    questState.set("return_of_old_susceptor", String.valueOf(4), true);
                    string2 = "collecter_yumi_q0114_15.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=10")) {
                if (n == 4) {
                    questState.set("return_of_old_susceptor", String.valueOf(5), true);
                    string2 = "collecter_yumi_q0114_23.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=11")) {
                if (n == 5) {
                    questState.setCond(6);
                    questState.set("return_of_old_susceptor", String.valueOf(6), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "collecter_yumi_q0114_26.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=18")) {
                string2 = "collecter_yumi_q0114_29.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=12")) {
                string2 = "collecter_yumi_q0114_30.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=13")) {
                if (n == 7) {
                    questState.setCond(17);
                    questState.set("return_of_old_susceptor", String.valueOf(8), true);
                    questState.giveItems(8090, 1L);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "collecter_yumi_q0114_31.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=15")) {
                if (n == 9 && questState.getQuestItemsCount(8091) >= 1L) {
                    questState.takeItems(8091, -1L);
                    questState.set("return_of_old_susceptor", String.valueOf(10), true);
                    string2 = "collecter_yumi_q0114_34.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=14")) {
                if (n == 10 && n2 == 20211) {
                    string2 = "collecter_yumi_q0114_37.htm";
                }
                if (n == 10 && (n2 == 10111 || n2 == 30411)) {
                    string2 = "collecter_yumi_q0114_38.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=16")) {
                if (n == 10 && (n2 == 10111 || n2 == 30411)) {
                    questState.setCond(20);
                    questState.set("return_of_old_susceptor", String.valueOf(101), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "collecter_yumi_q0114_39.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=17") && n == 10) {
                questState.setCond(21);
                questState.set("return_of_old_susceptor", String.valueOf(11), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "collecter_yumi_q0114_40.htm";
            }
        } else if (n3 == 32047) {
            if (string.equalsIgnoreCase("menu_select?ask=114&reply=1")) {
                if (n == 2) {
                    if (n2 % 10 == 0) {
                        questState.set("return_of_old_susceptor_ex", String.valueOf(n2 + 1), true);
                    }
                    string2 = "chaos_secretary_wendy_q0114_02.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=2")) {
                if (n == 2) {
                    int n4 = n2 % 100;
                    if ((n4 /= 10) == 0) {
                        questState.set("return_of_old_susceptor_ex", String.valueOf(n2 + 10), true);
                    }
                    string2 = "chaos_secretary_wendy_q0114_03.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=3")) {
                if (n == 2 && n2 < 11) {
                    string2 = "chaos_secretary_wendy_q0114_04.htm";
                } else if (n == 2 && n2 == 11) {
                    string2 = "chaos_secretary_wendy_q0114_05.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=4")) {
                if (n == 2 && n2 == 11) {
                    questState.setCond(3);
                    questState.set("return_of_old_susceptor", String.valueOf(3), true);
                    questState.set("return_of_old_susceptor_ex", String.valueOf(10011), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "chaos_secretary_wendy_q0114_06.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=401")) {
                string2 = "chaos_secretary_wendy_q0114_06a.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=5")) {
                if (n == 2 && n2 == 11) {
                    questState.setCond(4);
                    questState.set("return_of_old_susceptor", String.valueOf(3), true);
                    questState.set("return_of_old_susceptor_ex", String.valueOf(20011), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "chaos_secretary_wendy_q0114_07.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=6")) {
                if (n == 2 && n2 == 11) {
                    questState.setCond(5);
                    questState.set("return_of_old_susceptor", String.valueOf(3), true);
                    questState.set("return_of_old_susceptor_ex", String.valueOf(30011), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "chaos_secretary_wendy_q0114_09.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=402")) {
                string2 = "chaos_secretary_wendy_q0114_12a.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=403")) {
                string2 = "chaos_secretary_wendy_q0114_13a.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=405")) {
                if (n == 6 && n2 == 10011) {
                    questState.setCond(7);
                    questState.set("return_of_old_susceptor_ex", String.valueOf(30011), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "chaos_secretary_wendy_q0114_14ab.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=404")) {
                string2 = "chaos_secretary_wendy_q0114_14a.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=407")) {
                string2 = "chaos_secretary_wendy_q0114_17a.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=408")) {
                if (n == 6 && n2 == 10011) {
                    questState.setCond(8);
                    questState.set("return_of_old_susceptor_ex", String.valueOf(10111), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "chaos_secretary_wendy_q0114_20a.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=409")) {
                if (n == 6 && n2 == 10111) {
                    questState.setCond(9);
                    questState.set("return_of_old_susceptor", String.valueOf(7), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "chaos_secretary_wendy_q0114_21a.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=501")) {
                string2 = "chaos_secretary_wendy_q0114_12b.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=502")) {
                string2 = "chaos_secretary_wendy_q0114_13b.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=503")) {
                if (n == 6 && n2 == 20011) {
                    questState.setCond(10);
                    questState.set("return_of_old_susceptor_ex", String.valueOf(20111), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "chaos_secretary_wendy_q0114_14b.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=504")) {
                if (this.aFa == 0) {
                    this.aFa = 1;
                    this.aFb = questState.getPlayer().getName();
                    NpcInstance npcInstance2 = questState.addSpawn(27318, 96977, -110625, -3322);
                    questState.startQuestTimer("11401", 500L, npcInstance2);
                    questState.startQuestTimer("11402", 300000L, npcInstance2);
                    string2 = "chaos_secretary_wendy_q0114_15b.htm";
                } else {
                    string2 = this.aFb == questState.getPlayer().getName() ? "chaos_secretary_wendy_q0114_17b.htm" : "chaos_secretary_wendy_q0114_16b.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=505")) {
                if (n == 6 && n2 == 20211) {
                    questState.setCond(12);
                    questState.set("return_of_old_susceptor", String.valueOf(7), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "chaos_secretary_wendy_q0114_20b.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=506")) {
                string2 = "chaos_secretary_wendy_q0114_21b.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=601")) {
                string2 = "chaos_secretary_wendy_q0114_12c.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=602")) {
                string2 = "chaos_secretary_wendy_q0114_17c.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=603")) {
                string2 = "chaos_secretary_wendy_q0114_18c.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=604")) {
                string2 = "chaos_secretary_wendy_q0114_19c.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=605")) {
                if (n == 6 && n2 == 30011) {
                    questState.setCond(13);
                    questState.set("return_of_old_susceptor_ex", String.valueOf(30111), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "chaos_secretary_wendy_q0114_20c.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=607")) {
                if (n == 6 && n2 == 30311) {
                    questState.setCond(15);
                    questState.set("return_of_old_susceptor_ex", String.valueOf(30411), true);
                    questState.takeItems(8287, -1L);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "chaos_secretary_wendy_q0114_23c.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=410")) {
                if (n == 101 && n2 == 10111) {
                    questState.setCond(23);
                    questState.set("return_of_old_susceptor", String.valueOf(102), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "chaos_secretary_wendy_q0114_23a.htm";
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=610")) {
                string2 = "chaos_secretary_wendy_q0114_27c.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=611")) {
                string2 = "chaos_secretary_wendy_q0114_28c.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=612")) {
                if ((n == 101 || n == 103) && n2 == 30411) {
                    if (questState.getQuestItemsCount(57) >= 3000L) {
                        questState.setCond(26);
                        questState.set("return_of_old_susceptor", String.valueOf(12), true);
                        questState.giveItems(8289, 1L);
                        questState.takeItems(57, 3000L);
                        questState.playSound("ItemSound.quest_middle");
                        string2 = "chaos_secretary_wendy_q0114_29c.htm";
                    } else {
                        string2 = "chaos_secretary_wendy_q0114_29ca.htm";
                    }
                }
            } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=613") && (n == 101 || n == 103) && n2 == 30411) {
                questState.set("return_of_old_susceptor", String.valueOf(103), true);
                string2 = "chaos_secretary_wendy_q0114_30c.htm";
            }
        } else {
            if (string.equalsIgnoreCase("11401")) {
                Functions.npcSayCustomMessage(npcInstance, "NpcString.YOU_S1_YOU_ATTACKED_WENDY_PREPARE_TO_DIE", questState.getPlayer().getName());
                if (npcInstance != null) {
                    npcInstance.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, questState.getPlayer(), 2000);
                }
                return null;
            }
            if (string.equalsIgnoreCase("11402")) {
                if (npcInstance != null) {
                    this.aFa = 0;
                    this.aFb = null;
                    Functions.npcSayCustomMessage(npcInstance, "NpcString.S1_YOUR_ENEMY_WAS_DRIVEN_OUT_I_WILL_NOW_WITHDRAW_AND_AWAIT_YOUR_NEXT_COMMAND", questState.getPlayer().getName());
                    npcInstance.deleteMe();
                }
                return null;
            }
            if (n3 == 32050) {
                if (string.equalsIgnoreCase("menu_select?ask=114&reply=999")) {
                    if (n == 6 && n2 == 30111) {
                        questState.set("return_of_old_susceptor_ex", String.valueOf(30211), true);
                        questState.playSound("ItemSound.armor_wood_3");
                        string2 = "chaos_box2_q0114_01r.htm";
                    }
                } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=606")) {
                    if (n == 6 && n2 == 30211) {
                        questState.setCond(14);
                        questState.giveItems(8287, 1L);
                        questState.set("return_of_old_susceptor_ex", String.valueOf(30311), true);
                        questState.playSound("ItemSound.quest_middle");
                        string2 = "chaos_box2_q0114_03.htm";
                    }
                } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=411") && n == 102 && n2 == 10111 && questState.getQuestItemsCount(8289) == 0L) {
                    questState.setCond(24);
                    questState.giveItems(8289, 1L);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "chaos_box2_q0114_05.htm";
                }
            } else if (n3 == 32046) {
                if (string.equalsIgnoreCase("menu_select?ask=114&reply=1")) {
                    if (n == 8 && questState.getQuestItemsCount(8090) >= 1L) {
                        questState.setCond(19);
                        questState.set("return_of_old_susceptor", String.valueOf(9), true);
                        questState.giveItems(8091, 1L);
                        questState.playSound("ItemSound.quest_middle");
                        string2 = "pavel_atlanta_q0114_03.htm";
                    }
                } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=2")) {
                    string2 = "pavel_atlanta_q0114_05.htm";
                } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=3")) {
                    string2 = "pavel_atlanta_q0114_06.htm";
                } else if (string.equalsIgnoreCase("menu_select?ask=114&reply=4") && n == 13) {
                    questState.takeItems(8289, -1L);
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(false);
                    string2 = "pavel_atlanta_q0114_07.htm";
                }
            } else if (n3 == 31961 && string.equalsIgnoreCase("menu_select?ask=114&reply=1") && n == 11) {
                questState.setCond(22);
                questState.set("return_of_old_susceptor", String.valueOf(12), true);
                questState.giveItems(8289, 1L);
                questState.takeItems(8288, -1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "head_blacksmith_newyear_q0114_02.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("return_of_old_susceptor");
        int n2 = questState.getInt("return_of_old_susceptor_ex");
        int n3 = npcInstance.getNpcId();
        int n4 = questState.getState();
        switch (n4) {
            case 1: {
                if (n3 != 32041) break;
                QuestState questState2 = questState.getPlayer().getQuestState(_121_PavelTheGiants.class);
                if (questState2 == null) {
                    return "collecter_yumi_q0114_01.htm";
                }
                if (questState.getPlayer().getLevel() >= 49 && questState2.getState() == 3) {
                    string = "collecter_yumi_q0114_02.htm";
                    break;
                }
                string = "collecter_yumi_q0114_03.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n3 == 32041) {
                    if (n == 0) {
                        string = "collecter_yumi_q0114_04.htm";
                        break;
                    }
                    if (n == 1) {
                        string = "collecter_yumi_q0114_08.htm";
                        break;
                    }
                    if (n == 2) {
                        string = "collecter_yumi_q0114_10.htm";
                        break;
                    }
                    if (n == 3) {
                        string = "collecter_yumi_q0114_11.htm";
                        break;
                    }
                    if (n == 4) {
                        string = "collecter_yumi_q0114_16.htm";
                        break;
                    }
                    if (n == 5) {
                        string = "collecter_yumi_q0114_24.htm";
                        break;
                    }
                    if (n == 6) {
                        string = "collecter_yumi_q0114_27.htm";
                        break;
                    }
                    if (n == 7) {
                        string = "collecter_yumi_q0114_28.htm";
                        break;
                    }
                    if (n == 8) {
                        string = "collecter_yumi_q0114_32.htm";
                        break;
                    }
                    if (n == 9 && questState.getQuestItemsCount(8091) >= 1L) {
                        string = "collecter_yumi_q0114_33.htm";
                        break;
                    }
                    if (n == 10) {
                        string = "collecter_yumi_q0114_34z.htm";
                        break;
                    }
                    if (n == 101) {
                        string = "collecter_yumi_q0114_39z.htm";
                        break;
                    }
                    if (n == 11) {
                        string = "collecter_yumi_q0114_40z.htm";
                        break;
                    }
                    if (n == 12) {
                        questState.setCond(27);
                        questState.set("return_of_old_susceptor", String.valueOf(13), true);
                        questState.playSound("ItemSound.quest_middle");
                        string = "collecter_yumi_q0114_41.htm";
                        break;
                    }
                    if (n != 13) break;
                    string = "collecter_yumi_q0114_42.htm";
                    break;
                }
                if (n3 == 32047) {
                    if (n == 2 && n2 < 11) {
                        string = "chaos_secretary_wendy_q0114_01.htm";
                        break;
                    }
                    if (n == 2 && n2 == 11) {
                        string = "chaos_secretary_wendy_q0114_05.htm";
                        break;
                    }
                    if (n == 3 && n2 == 10011) {
                        string = "chaos_secretary_wendy_q0114_06b.htm";
                        break;
                    }
                    if (n == 3 && n2 == 20011) {
                        string = "chaos_secretary_wendy_q0114_08.htm";
                        break;
                    }
                    if (n == 3 && n2 == 30011) {
                        string = "chaos_secretary_wendy_q0114_10.htm";
                        break;
                    }
                    if (n == 6 && n2 == 10011) {
                        string = "chaos_secretary_wendy_q0114_11a.htm";
                        break;
                    }
                    if (n == 6 && n2 == 10111) {
                        string = "chaos_secretary_wendy_q0114_17a.htm";
                        break;
                    }
                    if (n == 6 && n2 == 20011) {
                        string = "chaos_secretary_wendy_q0114_11b.htm";
                        break;
                    }
                    if (n == 6 && n2 == 20111) {
                        string = "chaos_secretary_wendy_q0114_18b.htm";
                        break;
                    }
                    if (n == 6 && n2 == 20211) {
                        string = "chaos_secretary_wendy_q0114_19b.htm";
                        break;
                    }
                    if (n == 6 && n2 == 30011) {
                        string = "chaos_secretary_wendy_q0114_11c.htm";
                        break;
                    }
                    if (n == 6 && (n2 == 30111 || n2 == 30211)) {
                        string = "chaos_secretary_wendy_q0114_21c.htm";
                        break;
                    }
                    if (n == 6 && n2 == 30311) {
                        string = "chaos_secretary_wendy_q0114_22c.htm";
                        break;
                    }
                    if (n == 6 && n2 == 30411) {
                        questState.setCond(16);
                        questState.set("return_of_old_susceptor", String.valueOf(7), true);
                        questState.playSound("ItemSound.quest_middle");
                        string = "chaos_secretary_wendy_q0114_24c.htm";
                        break;
                    }
                    if (n == 7) {
                        string = "chaos_secretary_wendy_q0114_25c.htm";
                        break;
                    }
                    if (n == 101 && n2 == 10111) {
                        string = "chaos_secretary_wendy_q0114_22a.htm";
                        break;
                    }
                    if (n == 102 && n2 == 10111 && questState.getQuestItemsCount(8289) == 0L) {
                        string = "chaos_secretary_wendy_q0114_23z.htm";
                        break;
                    }
                    if (n == 102 && n2 == 10111 && questState.getQuestItemsCount(8289) >= 1L) {
                        questState.setCond(25);
                        questState.set("return_of_old_susceptor", String.valueOf(12), true);
                        questState.playSound("ItemSound.quest_middle");
                        string = "chaos_secretary_wendy_q0114_24a.htm";
                        break;
                    }
                    if (n == 12 && n2 == 10111) {
                        string = "chaos_secretary_wendy_q0114_24a.htm";
                        break;
                    }
                    if (n == 101 && n2 == 30411) {
                        string = "chaos_secretary_wendy_q0114_26c.htm";
                        break;
                    }
                    if (n == 103 && n2 == 30411) {
                        string = "chaos_secretary_wendy_q0114_31c.htm";
                        break;
                    }
                    if (n != 12 || n2 != 30411) break;
                    string = "chaos_secretary_wendy_q0114_32c.htm";
                    break;
                }
                if (n3 == 32050) {
                    if (n == 6 && n2 == 30111) {
                        string = "chaos_box2_q0114_01.htm";
                        break;
                    }
                    if (n == 6 && n2 == 30211) {
                        string = "chaos_box2_q0114_02.htm";
                        break;
                    }
                    if (n == 6 && n2 == 30311) {
                        string = "chaos_box2_q0114_04.htm";
                        break;
                    }
                    if (n == 102 && n2 == 10111 && questState.getQuestItemsCount(8289) == 0L) {
                        string = "chaos_box2_q0114_04b.htm";
                        break;
                    }
                    if (n != 102 || n2 != 10111 || questState.getQuestItemsCount(8289) < 1L) break;
                    string = "chaos_box2_q0114_05z.htm";
                    break;
                }
                if (n3 == 32046) {
                    if (n == 8 && questState.getQuestItemsCount(8090) >= 1L) {
                        questState.getPlayer().sendPacket((IStaticPacket)new ExShowScreenMessage("The radio signal detector is responding. Stones catches your eye.", 5000, 0, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, false));
                        string = "pavel_atlanta_q0114_02.htm";
                        break;
                    }
                    if (n == 9) {
                        string = "pavel_atlanta_q0114_03.htm";
                        break;
                    }
                    if (n != 13) break;
                    string = "pavel_atlanta_q0114_04.htm";
                    break;
                }
                if (n3 != 31961) break;
                if (n == 11) {
                    string = "head_blacksmith_newyear_q0114_01.htm";
                    break;
                }
                if (n != 12) break;
                string = "head_blacksmith_newyear_q0114_03.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getInt("return_of_old_susceptor");
        int n2 = questState.getInt("return_of_old_susceptor_ex");
        int n3 = npcInstance.getNpcId();
        if (n3 == 27318 && n == 6 && n2 == 20111) {
            questState.setCond(11);
            questState.set("return_of_old_susceptor_ex", String.valueOf(20211), true);
            Functions.npcSayCustomMessage(npcInstance, "NpcString.THIS_ENEMY_IS_FAR_TOO_POWERFUL_FOR_ME_TO_FIGHT_I_MUST_WITHDRAW", new Object[0]);
            questState.playSound("ItemSound.quest_middle");
            this.aFa = 0;
            this.aFb = null;
        }
        return null;
    }
}

