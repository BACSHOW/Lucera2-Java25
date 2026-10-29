/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.ai.CtrlEvent;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;
import quests._021_HiddenTruth;

public class _022_TragedyInVonHellmannForest
extends Quest
implements ScriptFile {
    private static final int aAf = 31527;
    private static final int aAg = 31334;
    private static final int aAb = 31328;
    private static final int aAh = 31528;
    private static final int aAi = 31529;
    private static final int aAj = 27217;
    private static final int aAd = 7141;
    private static final int aAk = 7142;
    private static final int aAl = 7143;
    private static final int aAm = 7144;
    private static final int aAn = 7145;
    private static final int aAo = 7146;
    private static final int aAp = 7147;
    private static final int aAq = 21553;
    private static final int aAr = 21554;
    private static final int aAs = 21555;
    private static final int aAt = 21556;
    private static final int aAu = 21561;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _022_TragedyInVonHellmannForest() {
        super(0);
        this.addStartNpc(31334);
        this.addTalkId(31334, 31528, 31328, 31529, 31527);
        this.addAttackId(27217);
        this.addKillId(27217);
        this.addKillId(21553, 21554, 21555, 21556, 21561);
        this.addQuestItem(7142);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getInt("tragedy_of_helman_forest");
        int n2 = questState.getInt("spawned_rune_ghost2");
        int n3 = questState.getInt("spawned_ghost_of_umul");
        if (string.equalsIgnoreCase("quest_accept")) {
            questState.setCond(1);
            questState.set("tragedy_of_helman_forest", String.valueOf(1), true);
            questState.set("spawned_rune_ghost2", String.valueOf(0), true);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            string2 = "grandmagister_tifaren_q0022_04.htm";
        } else if (string.equalsIgnoreCase("reply_2") && n == 1) {
            if (questState.getQuestItemsCount(7141) >= 1L) {
                string2 = "grandmagister_tifaren_q0022_06.htm";
            } else {
                questState.setCond(2);
                questState.playSound("ItemSound.quest_middle");
                string2 = "grandmagister_tifaren_q0022_07.htm";
            }
        } else if (string.equalsIgnoreCase("reply_3") && n == 1) {
            if (questState.getQuestItemsCount(7141) >= 1L) {
                questState.setCond(4);
                questState.set("tragedy_of_helman_forest", String.valueOf(2), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "grandmagister_tifaren_q0022_08.htm";
            }
        } else if (string.equalsIgnoreCase("reply_5")) {
            if (n == 2 && questState.getQuestItemsCount(7141) >= 1L && questState.getQuestItemsCount(7142) >= 1L) {
                if (n2 == 0) {
                    questState.setCond(7);
                    questState.set("spawned_rune_ghost2", String.valueOf(1), true);
                    questState.set("tragedy_of_helman_forest", String.valueOf(4), true);
                    questState.set("rune_ghost2_player_name", questState.getPlayer().getName(), true);
                    questState.takeItems(7142, 1L);
                    questState.playSound("ItemSound.quest_middle");
                    NpcInstance npcInstance2 = questState.addSpawn(31528, 38354, -49777, -1128);
                    Functions.npcSay(npcInstance2, "Did you call me, " + questState.getPlayer().getName());
                    questState.startQuestTimer("despawn_rune_ghost2", 120000L, npcInstance2);
                    string2 = "grandmagister_tifaren_q0022_13.htm";
                } else {
                    questState.setCond(6);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "grandmagister_tifaren_q0022_14.htm";
                }
            } else if (n == 4 && questState.getQuestItemsCount(7141) >= 1L) {
                if (n2 == 0) {
                    NpcInstance npcInstance3 = questState.addSpawn(31528, 38354, -49777, -1128);
                    questState.startQuestTimer("despawn_rune_ghost2", 120000L, npcInstance3);
                    string2 = "grandmagister_tifaren_q0022_13.htm";
                } else {
                    questState.setCond(6);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "grandmagister_tifaren_q0022_14.htm";
                }
            }
        } else {
            if (string.equalsIgnoreCase("despawn_rune_ghost2")) {
                Functions.npcSay(npcInstance, "I'm confused! Maybe it's time to go back.");
                questState.unset("spawned_rune_ghost2");
                if (npcInstance != null) {
                    npcInstance.deleteMe();
                }
                if (!questState.isRunningQuestTimer("despawn_rune_ghost2_2")) {
                    questState.cancelQuestTimer("despawn_rune_ghost2_2");
                }
                return null;
            }
            if (string.equalsIgnoreCase("despawn_rune_ghost2_2")) {
                Functions.npcSay(npcInstance, "My train of thought is chaotic. It goes back to the beginning of time...");
                questState.unset("spawned_rune_ghost2");
                if (npcInstance != null) {
                    npcInstance.deleteMe();
                }
                if (!questState.isRunningQuestTimer("despawn_rune_ghost2")) {
                    questState.cancelQuestTimer("despawn_rune_ghost2");
                }
                return null;
            }
            if (string.equalsIgnoreCase("reply_6")) {
                questState.playSound("AmbSound.d_horror_03");
                string2 = "rune_ghost2_q0022_04.htm";
            } else if (string.equalsIgnoreCase("reply_7")) {
                questState.setCond(8);
                questState.set("tragedy_of_helman_forest", String.valueOf(5), true);
                questState.startQuestTimer("despawn_rune_ghost2_2", 3000L, npcInstance);
                string2 = "rune_ghost2_q0022_08.htm";
            } else if (string.equalsIgnoreCase("reply_8") && n == 5) {
                questState.takeItems(7141, -1L);
                questState.set("tragedy_of_helman_forest", String.valueOf(6), true);
                string2 = "highpriest_innocentin_q0022_03.htm";
            } else if (string.equalsIgnoreCase("reply_10") && n == 6) {
                questState.setCond(9);
                questState.set("tragedy_of_helman_forest", String.valueOf(7), true);
                questState.giveItems(7143, 1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "highpriest_innocentin_q0022_09.htm";
            } else if (string.equalsIgnoreCase("reply_17") && n == 12 && questState.getQuestItemsCount(7147) >= 1L) {
                questState.setCond(15);
                questState.set("tragedy_of_helman_forest", String.valueOf(13), true);
                questState.takeItems(7147, -1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "highpriest_innocentin_q0022_11.htm";
            } else if (string.equalsIgnoreCase("reply_19") && n == 13) {
                questState.setCond(16);
                questState.set("tragedy_of_helman_forest", String.valueOf(14), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "highpriest_innocentin_q0022_19.htm";
            } else if (string.equalsIgnoreCase("reply_12") && n == 7 && questState.getQuestItemsCount(7143) >= 1L) {
                questState.takeItems(7143, -1L);
                questState.set("tragedy_of_helman_forest", String.valueOf(8), true);
                string2 = "rune_ghost3_q0022_03.htm";
            } else if (string.equalsIgnoreCase("reply_14") && n == 8) {
                questState.set("tragedy_of_helman_forest", String.valueOf(9), true);
                string2 = "rune_ghost3_q0022_08.htm";
            } else if (string.equalsIgnoreCase("reply_15") && n == 9) {
                questState.setCond(10);
                questState.set("tragedy_of_helman_forest", String.valueOf(10), true);
                questState.giveItems(7144, 1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "rune_ghost3_q0022_11.htm";
            } else if (string.equalsIgnoreCase("reply_16")) {
                if (n3 == 0) {
                    questState.set("spawned_ghost_of_umul", String.valueOf(1), true);
                    questState.set("umul", String.valueOf(0), true);
                    questState.playSound("SkillSound3.antaras_fear");
                    NpcInstance npcInstance4 = questState.addSpawn(27217, 34706, -54590, -2054);
                    if (npcInstance4 != null) {
                        npcInstance4.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, questState.getPlayer(), 20000);
                    }
                    questState.startQuestTimer("ghost_of_umul_1", 90000L, npcInstance4);
                    questState.startQuestTimer("despawn_ghost_of_umul", 120000L, npcInstance4);
                    string2 = "umul_q0022_02.htm";
                } else {
                    string2 = "umul_q0022_03.htm";
                }
            } else {
                if (string.equalsIgnoreCase("ghost_of_umul_1")) {
                    questState.set("umul", String.valueOf(1), true);
                    return null;
                }
                if (string.equalsIgnoreCase("despawn_ghost_of_umul")) {
                    questState.unset("spawned_ghost_of_umul");
                    if (npcInstance != null) {
                        npcInstance.deleteMe();
                    }
                    return null;
                }
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        QuestState questState2 = questState.getPlayer().getQuestState(_021_HiddenTruth.class);
        int n = questState.getInt("tragedy_of_helman_forest");
        int n2 = questState.getInt("spawned_rune_ghost2");
        String string2 = questState.get("rune_ghost2_player_name");
        int n3 = npcInstance.getNpcId();
        int n4 = questState.getState();
        switch (n4) {
            case 1: {
                if (n3 != 31334) break;
                if (questState2 != null && questState2.isCompleted() && questState.getPlayer().getLevel() >= 63) {
                    string = "grandmagister_tifaren_q0022_01.htm";
                    break;
                }
                string = "grandmagister_tifaren_q0022_03.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n3 == 31334) {
                    if (n == 1) {
                        string = "grandmagister_tifaren_q0022_05.htm";
                        break;
                    }
                    if (n == 2) {
                        if (questState.getQuestItemsCount(7141) >= 1L && questState.getQuestItemsCount(7142) > 0L) {
                            if (n2 == 0) {
                                string = "grandmagister_tifaren_q0022_10.htm";
                                break;
                            }
                            string = "grandmagister_tifaren_q0022_11.htm";
                            break;
                        }
                        string = "grandmagister_tifaren_q0022_09.htm";
                        break;
                    }
                    if (n == 4 && questState.getQuestItemsCount(7141) >= 1L) {
                        if (n2 == 1) {
                            if (string2 == questState.getPlayer().getName()) {
                                string = "grandmagister_tifaren_q0022_15.htm";
                                break;
                            }
                            questState.setCond(6);
                            string = "grandmagister_tifaren_q0022_16.htm";
                            questState.playSound("ItemSound.quest_middle");
                            break;
                        }
                        string = "grandmagister_tifaren_q0022_17.htm";
                        break;
                    }
                    if (n != 5 || questState.getQuestItemsCount(7141) < 1L) break;
                    string = "grandmagister_tifaren_q0022_19.htm";
                    break;
                }
                if (n3 == 31528) {
                    if (string2 != questState.getPlayer().getName()) {
                        string = "rune_ghost2_q0022_01a.htm";
                        questState.playSound("AmbSound.d_horror_15");
                        break;
                    }
                    if (string2 != questState.getPlayer().getName()) break;
                    string = "rune_ghost2_q0022_01.htm";
                    questState.playSound("AmbSound.d_horror_15");
                    break;
                }
                if (n3 == 31328) {
                    if (n < 5 && questState.getQuestItemsCount(7141) == 0L) {
                        questState.setCond(3);
                        questState.giveItems(7141, 1L);
                        string = "highpriest_innocentin_q0022_01.htm";
                        questState.playSound("ItemSound.quest_middle");
                        break;
                    }
                    if (n < 5 && questState.getQuestItemsCount(7141) >= 1L) {
                        string = "highpriest_innocentin_q0022_01b.htm";
                        break;
                    }
                    if (n == 5) {
                        string = "highpriest_innocentin_q0022_02.htm";
                        break;
                    }
                    if (n == 6) {
                        string = "highpriest_innocentin_q0022_04.htm";
                        break;
                    }
                    if (n == 7) {
                        string = "highpriest_innocentin_q0022_09a.htm";
                        break;
                    }
                    if (n == 12 && questState.getQuestItemsCount(7147) >= 1L) {
                        string = "highpriest_innocentin_q0022_10.htm";
                        break;
                    }
                    if (n == 13) {
                        string = "highpriest_innocentin_q0022_12.htm";
                        break;
                    }
                    if (n == 14 && questState.getPlayer().getLevel() >= 64) {
                        questState.unset("rune_ghost2_player_name");
                        questState.unset("tragedy_of_helman_forest");
                        questState.addExpAndSp(345966L, 31578L);
                        questState.exitCurrentQuest(false);
                        questState.playSound("ItemSound.quest_finish");
                        string = "highpriest_innocentin_q0022_20.htm";
                        break;
                    }
                    if (n != 14 || questState.getPlayer().getLevel() >= 64) break;
                    questState.unset("rune_ghost2_player_name");
                    questState.unset("tragedy_of_helman_forest");
                    questState.addExpAndSp(345966L, 31578L);
                    questState.exitCurrentQuest(false);
                    questState.playSound("ItemSound.quest_finish");
                    string = "highpriest_innocentin_q0022_21.htm";
                    break;
                }
                if (n3 == 31529) {
                    if (n == 7 && questState.getQuestItemsCount(7143) >= 1L) {
                        string = "rune_ghost3_q0022_01.htm";
                        break;
                    }
                    if (n == 8) {
                        string = "rune_ghost3_q0022_03a.htm";
                        break;
                    }
                    if (n == 9) {
                        string = "rune_ghost3_q0022_10.htm";
                        break;
                    }
                    if (n == 11 && questState.getQuestItemsCount(7144) >= 1L) {
                        string = "rune_ghost3_q0022_14.htm";
                        break;
                    }
                    if (n == 11 && questState.getQuestItemsCount(7145) >= 1L && questState.getQuestItemsCount(7146) == 0L) {
                        questState.setCond(12);
                        questState.playSound("ItemSound.quest_middle");
                        string = "rune_ghost3_q0022_15.htm";
                        break;
                    }
                    if (n == 11 && questState.getQuestItemsCount(7145) >= 1L && questState.getQuestItemsCount(7146) >= 1L) {
                        questState.setCond(14);
                        questState.set("tragedy_of_helman_forest", String.valueOf(12), true);
                        questState.giveItems(7147, 1L);
                        questState.takeItems(7146, -1L);
                        questState.takeItems(7145, -1L);
                        questState.playSound("ItemSound.quest_middle");
                        string = "rune_ghost3_q0022_16.htm";
                        break;
                    }
                    if (n != 12 || questState.getQuestItemsCount(7147) < 1L) break;
                    string = "rune_ghost3_q0022_17.htm";
                    break;
                }
                if (n3 != 31527) break;
                if ((n == 10 || n == 11) && questState.getQuestItemsCount(7144) >= 1L) {
                    questState.playSound("AmbSound.dd_horror_01");
                    string = "umul_q0022_01.htm";
                    break;
                }
                if (n == 11 && questState.getQuestItemsCount(7145) >= 1L && questState.getQuestItemsCount(7146) == 0L) {
                    questState.setCond(13);
                    questState.giveItems(7146, 1L);
                    questState.playSound("ItemSound.quest_middle");
                    string = "umul_q0022_04.htm";
                    break;
                }
                if (n != 11 || questState.getQuestItemsCount(7145) < 1L || questState.getQuestItemsCount(7146) < 1L) break;
                string = "umul_q0022_05.htm";
            }
        }
        return string;
    }

    @Override
    public String onAttack(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getInt("tragedy_of_helman_forest");
        int n3 = questState.getInt("spawned_ghost_of_umul");
        if (n == 27217) {
            if (n2 == 10 && questState.getQuestItemsCount(7144) >= 1L) {
                questState.set("tragedy_of_helman_forest", String.valueOf(11), true);
            } else if (n2 == 11 && questState.getQuestItemsCount(7144) >= 1L && Rnd.get(100) < 5 && n3 == 1) {
                questState.setCond(11);
                questState.takeItems(7144, -1L);
                questState.giveItems(7145, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getInt("tragedy_of_helman_forest");
        if (n == 27217) {
            questState.unset("spawned_ghost_of_umul");
        } else if ((n == 21553 || n == 21554 || n == 21555 || n == 21556 || n == 21561) && n2 == 2 && questState.getQuestItemsCount(7142) == 0L && Rnd.get(100) < 10) {
            questState.giveItems(7142, 1L);
            questState.playSound("ItemSound.quest_itemget");
            questState.setCond(5);
        }
        return null;
    }
}

