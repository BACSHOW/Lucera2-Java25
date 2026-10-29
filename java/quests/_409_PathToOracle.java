/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _409_PathToOracle
extends Quest
implements ScriptFile {
    public final int MANUEL = 30293;
    public final int ALLANA = 30424;
    public final int PERRIN = 30428;
    public final int LIZARDMAN_WARRIOR = 27032;
    public final int LIZARDMAN_SCOUT = 27033;
    public final int LIZARDMAN = 27034;
    public final int TAMIL = 27035;
    public final int CRYSTAL_MEDALLION_ID = 1231;
    public final int MONEY_OF_SWINDLER_ID = 1232;
    public final int DAIRY_OF_ALLANA_ID = 1233;
    public final int LIZARD_CAPTAIN_ORDER_ID = 1234;
    public final int LEAF_OF_ORACLE_ID = 1235;
    public final int HALF_OF_DAIRY_ID = 1236;
    public final int TAMATOS_NECKLACE_ID = 1275;
    public final int weapon_exchange_coupon_d_grade = 8593;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _409_PathToOracle() {
        super(0);
        this.addStartNpc(30293);
        this.addTalkId(30424);
        this.addTalkId(30428);
        this.addKillId(27032);
        this.addKillId(27033);
        this.addKillId(27034);
        this.addKillId(27035);
        this.addQuestItem(1232, 1233, 1234, 1231, 1236, 1275);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("1")) {
            if (string.equalsIgnoreCase("1")) {
                if (questState.getPlayer().getClassId().getId() != 25) {
                    string2 = questState.getPlayer().getClassId().getId() == 29 ? "father_manuell_q0409_02a.htm" : "father_manuell_q0409_02.htm";
                } else if (questState.getPlayer().getLevel() < 18) {
                    string2 = "father_manuell_q0409_03.htm";
                } else if (questState.getQuestItemsCount(1235) > 0L) {
                    string2 = "father_manuell_q0409_04.htm";
                } else {
                    questState.setCond(1);
                    questState.setState(2);
                    questState.playSound("ItemSound.quest_accept");
                    questState.giveItems(1231, 1L);
                    string2 = "father_manuell_q0409_05.htm";
                }
            }
        } else if (string.equalsIgnoreCase("allana_q0409_08.htm")) {
            questState.addSpawn(27032);
            questState.addSpawn(27033);
            questState.addSpawn(27034);
            questState.setCond(2);
        } else if (string.equalsIgnoreCase("30424_1")) {
            string2 = "";
        } else if (string.equalsIgnoreCase("30428_1")) {
            string2 = "perrin_q0409_02.htm";
        } else if (string.equalsIgnoreCase("30428_2")) {
            string2 = "perrin_q0409_03.htm";
        } else if (string.equalsIgnoreCase("30428_3")) {
            questState.addSpawn(27035);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 30293) {
            if (n2 < 1) {
                string = "father_manuell_q0409_01.htm";
            } else if (questState.getQuestItemsCount(1231) > 0L) {
                if (questState.getQuestItemsCount(1232) < 1L && questState.getQuestItemsCount(1233) < 1L && questState.getQuestItemsCount(1234) < 1L && questState.getQuestItemsCount(1236) < 1L) {
                    string = "father_manuell_q0409_09.htm";
                } else if (questState.getQuestItemsCount(1232) > 0L && questState.getQuestItemsCount(1233) > 0L && questState.getQuestItemsCount(1234) > 0L && questState.getQuestItemsCount(1236) < 1L) {
                    string = "father_manuell_q0409_08.htm";
                    questState.takeItems(1232, 1L);
                    questState.takeItems(1233, -1L);
                    questState.takeItems(1234, -1L);
                    questState.takeItems(1231, -1L);
                    if (questState.getPlayer().getClassId().getLevel() == 1) {
                        questState.giveItems(1235, 1L);
                        if (!questState.getPlayer().getVarB("prof1")) {
                            questState.getPlayer().setVar("prof1", "1", -1L);
                            questState.addExpAndSp(3200L, 1890L);
                        }
                    }
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(true);
                } else {
                    string = "father_manuell_q0409_07.htm";
                }
            }
        } else if (n == 30424) {
            if (questState.getQuestItemsCount(1231) > 0L) {
                if (questState.getQuestItemsCount(1232) < 1L && questState.getQuestItemsCount(1233) < 1L && questState.getQuestItemsCount(1234) < 1L && questState.getQuestItemsCount(1236) < 1L) {
                    string = n2 > 2 ? "allana_q0409_05.htm" : "allana_q0409_01.htm";
                } else if (questState.getQuestItemsCount(1232) < 1L && questState.getQuestItemsCount(1233) < 1L && questState.getQuestItemsCount(1234) > 0L && questState.getQuestItemsCount(1236) < 1L) {
                    string = "allana_q0409_02.htm";
                    questState.giveItems(1236, 1L);
                    questState.setCond(4);
                } else if (questState.getQuestItemsCount(1232) < 1L && questState.getQuestItemsCount(1233) < 1L && questState.getQuestItemsCount(1234) > 0L && questState.getQuestItemsCount(1236) > 0L) {
                    string = questState.getQuestItemsCount(1275) < 1L ? "allana_q0409_06.htm" : "allana_q0409_03.htm";
                } else if (questState.getQuestItemsCount(1232) > 0L && questState.getQuestItemsCount(1233) < 1L && questState.getQuestItemsCount(1234) > 0L && questState.getQuestItemsCount(1236) > 0L) {
                    string = "allana_q0409_04.htm";
                    questState.takeItems(1236, -1L);
                    questState.giveItems(1233, 1L);
                    questState.setCond(7);
                } else if (questState.getQuestItemsCount(1232) > 0L && questState.getQuestItemsCount(1234) > 0L && questState.getQuestItemsCount(1236) < 1L && questState.getQuestItemsCount(1233) > 0L) {
                    string = "allana_q0409_05.htm";
                }
            }
        } else if (n == 30428 && questState.getQuestItemsCount(1231) > 0L && questState.getQuestItemsCount(1234) > 0L) {
            if (questState.getQuestItemsCount(1275) > 0L) {
                string = "perrin_q0409_04.htm";
                questState.takeItems(1275, -1L);
                questState.giveItems(1232, 1L);
                questState.setCond(6);
            } else {
                string = questState.getQuestItemsCount(1232) > 0L ? "perrin_q0409_05.htm" : (n2 > 4 ? "perrin_q0409_06.htm" : "perrin_q0409_01.htm");
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 27032 | n == 27033 | n == 27034) {
            if (n2 == 2 && questState.getQuestItemsCount(1234) < 1L) {
                questState.giveItems(1234, 1L);
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(3);
            }
        } else if (n == 27035 && n2 == 4 && questState.getQuestItemsCount(1275) < 1L) {
            questState.giveItems(1275, 1L);
            questState.playSound("ItemSound.quest_middle");
            questState.setCond(5);
        }
        return null;
    }
}

