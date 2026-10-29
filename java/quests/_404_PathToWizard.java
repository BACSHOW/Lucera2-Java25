/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _404_PathToWizard
extends Quest
implements ScriptFile {
    public final int PARINA = 30391;
    public final int EARTH_SNAKE = 30409;
    public final int WASTELAND_LIZARDMAN = 30410;
    public final int FLAME_SALAMANDER = 30411;
    public final int WIND_SYLPH = 30412;
    public final int WATER_UNDINE = 30413;
    public final int RED_BEAR = 20021;
    public final int RATMAN_WARRIOR = 20359;
    public final int WATER_SEER = 27030;
    public final int MAP_OF_LUSTER_ID = 1280;
    public final int KEY_OF_FLAME_ID = 1281;
    public final int FLAME_EARING_ID = 1282;
    public final int BROKEN_BRONZE_MIRROR_ID = 1283;
    public final int WIND_FEATHER_ID = 1284;
    public final int WIND_BANGEL_ID = 1285;
    public final int RAMAS_DIARY_ID = 1286;
    public final int SPARKLE_PEBBLE_ID = 1287;
    public final int WATER_NECKLACE_ID = 1288;
    public final int RUST_GOLD_COIN_ID = 1289;
    public final int RED_SOIL_ID = 1290;
    public final int EARTH_RING_ID = 1291;
    public final int BEAD_OF_SEASON_ID = 1292;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _404_PathToWizard() {
        super(0);
        this.addStartNpc(30391);
        this.addTalkId(30409);
        this.addTalkId(30410);
        this.addTalkId(30411);
        this.addTalkId(30412);
        this.addTalkId(30413);
        this.addKillId(20021);
        this.addKillId(20359);
        this.addKillId(27030);
        this.addQuestItem(1281, 1280, 1284, 1283, 1287, 1286, 1290, 1289, 1282, 1285, 1288, 1291);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("1")) {
            if (questState.getPlayer().getClassId().getId() == 10) {
                if (questState.getPlayer().getLevel() >= 18) {
                    if (questState.getQuestItemsCount(1292) > 0L) {
                        string2 = "parina_q0404_03.htm";
                    } else {
                        string2 = "parina_q0404_08.htm";
                        questState.setCond(1);
                        questState.setState(2);
                        questState.playSound("ItemSound.quest_accept");
                    }
                } else {
                    string2 = "parina_q0404_02.htm";
                }
            } else {
                string2 = questState.getPlayer().getClassId().getId() == 11 ? "parina_q0404_02a.htm" : "parina_q0404_01.htm";
            }
        } else if (string.equalsIgnoreCase("30410_1") && questState.getQuestItemsCount(1284) < 1L) {
            string2 = "lizardman_of_wasteland_q0404_03.htm";
            questState.giveItems(1284, 1L);
            questState.setCond(6);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 30391) {
            if (n2 == 0) {
                string = "parina_q0404_04.htm";
            } else if (n2 > 0 && questState.getQuestItemsCount(1282) < 1L | questState.getQuestItemsCount(1285) < 1L | questState.getQuestItemsCount(1288) < 1L | questState.getQuestItemsCount(1291) < 1L) {
                string = "parina_q0404_05.htm";
            } else if (n2 > 0 && questState.getQuestItemsCount(1282) > 0L && questState.getQuestItemsCount(1285) > 0L && questState.getQuestItemsCount(1288) > 0L && questState.getQuestItemsCount(1291) > 0L) {
                string = "parina_q0404_06.htm";
                questState.takeItems(1282, questState.getQuestItemsCount(1282));
                questState.takeItems(1285, questState.getQuestItemsCount(1285));
                questState.takeItems(1288, questState.getQuestItemsCount(1288));
                questState.takeItems(1291, questState.getQuestItemsCount(1291));
                if (questState.getPlayer().getClassId().getLevel() == 1) {
                    if (questState.getQuestItemsCount(1292) < 1L) {
                        questState.giveItems(1292, 1L);
                    }
                    if (!questState.getPlayer().getVarB("prof1")) {
                        questState.getPlayer().setVar("prof1", "1", -1L);
                        questState.addExpAndSp(3200L, 2020L);
                    }
                }
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            }
        } else if (n == 30411) {
            if (n2 > 0 && questState.getQuestItemsCount(1280) < 1L && questState.getQuestItemsCount(1282) < 1L) {
                questState.giveItems(1280, 1L);
                string = "flame_salamander_q0404_01.htm";
                questState.setCond(2);
            } else if (n2 > 0 && questState.getQuestItemsCount(1280) > 0L && questState.getQuestItemsCount(1281) < 1L) {
                string = "flame_salamander_q0404_02.htm";
            } else if (n2 == 3 && questState.getQuestItemsCount(1280) > 0L && questState.getQuestItemsCount(1281) > 0L) {
                questState.takeItems(1281, -1L);
                questState.takeItems(1280, -1L);
                if (questState.getQuestItemsCount(1282) < 1L) {
                    questState.giveItems(1282, 1L);
                }
                string = "flame_salamander_q0404_03.htm";
                questState.setCond(4);
            } else if (n2 > 0 && questState.getQuestItemsCount(1282) > 0L) {
                string = "flame_salamander_q0404_04.htm";
            }
        } else if (n == 30412) {
            if (n2 == 4 && questState.getQuestItemsCount(1282) > 0L && questState.getQuestItemsCount(1283) < 1L && questState.getQuestItemsCount(1285) < 1L) {
                questState.giveItems(1283, 1L);
                string = "wind_sylph_q0404_01.htm";
                questState.setCond(5);
            } else if (n2 > 0 && questState.getQuestItemsCount(1283) > 0L && questState.getQuestItemsCount(1284) < 1L) {
                string = "wind_sylph_q0404_02.htm";
            } else if (n2 > 0 && questState.getQuestItemsCount(1283) > 0L && questState.getQuestItemsCount(1284) > 0L) {
                questState.takeItems(1284, questState.getQuestItemsCount(1284));
                questState.takeItems(1283, questState.getQuestItemsCount(1283));
                if (questState.getQuestItemsCount(1285) < 1L) {
                    questState.giveItems(1285, 1L);
                }
                string = "wind_sylph_q0404_03.htm";
                questState.setCond(7);
            } else if (n2 > 0 && questState.getQuestItemsCount(1285) > 0L) {
                string = "wind_sylph_q0404_04.htm";
            }
        } else if (n == 30410) {
            if (n2 > 0 && questState.getQuestItemsCount(1283) > 0L && questState.getQuestItemsCount(1284) < 1L) {
                string = "lizardman_of_wasteland_q0404_01.htm";
            } else if (n2 > 0 && questState.getQuestItemsCount(1283) > 0L && questState.getQuestItemsCount(1284) > 0L) {
                string = "lizardman_of_wasteland_q0404_04.htm";
            }
        } else if (n == 30413) {
            if (n2 == 7 && questState.getQuestItemsCount(1285) > 0L && questState.getQuestItemsCount(1286) < 1L && questState.getQuestItemsCount(1288) < 1L) {
                questState.giveItems(1286, 1L);
                string = "water_undine_q0404_01.htm";
                questState.setCond(8);
            } else if (n2 > 0 && questState.getQuestItemsCount(1286) > 0L && questState.getQuestItemsCount(1287) < 2L) {
                string = "water_undine_q0404_02.htm";
            } else if (n2 == 9 && questState.getQuestItemsCount(1286) > 0L && questState.getQuestItemsCount(1287) > 1L) {
                questState.takeItems(1287, -1L);
                questState.takeItems(1286, -1L);
                if (questState.getQuestItemsCount(1288) < 1L) {
                    questState.giveItems(1288, 1L);
                }
                string = "water_undine_q0404_03.htm";
                questState.setCond(10);
            } else if (n2 > 0 && questState.getQuestItemsCount(1288) > 0L) {
                string = "water_undine_q0404_04.htm";
            }
        } else if (n == 30409) {
            if (n2 > 0 && questState.getQuestItemsCount(1288) > 0L && questState.getQuestItemsCount(1289) < 1L && questState.getQuestItemsCount(1291) < 1L) {
                questState.giveItems(1289, 1L);
                string = "earth_snake_q0404_01.htm";
                questState.setCond(11);
            } else if (n2 > 0 && questState.getQuestItemsCount(1289) > 0L && questState.getQuestItemsCount(1290) < 1L) {
                string = "earth_snake_q0404_02.htm";
            } else if (n2 == 12 && questState.getQuestItemsCount(1289) > 0L && questState.getQuestItemsCount(1290) > 0L) {
                questState.takeItems(1290, questState.getQuestItemsCount(1290));
                questState.takeItems(1289, questState.getQuestItemsCount(1289));
                if (questState.getQuestItemsCount(1291) < 1L) {
                    questState.giveItems(1291, 1L);
                }
                string = "earth_snake_q0404_04.htm";
                questState.setCond(13);
            } else if (n2 > 0 && questState.getQuestItemsCount(1291) > 0L) {
                string = "earth_snake_q0404_04.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 20359) {
            if (n2 == 2) {
                questState.giveItems(1281, 1L);
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(3);
            }
        } else if (n == 27030) {
            if (n2 == 8 && questState.getQuestItemsCount(1287) < 2L) {
                questState.giveItems(1287, 1L);
                if (questState.getQuestItemsCount(1287) == 2L) {
                    questState.playSound("ItemSound.quest_middle");
                    questState.setCond(9);
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20021 && n2 == 11) {
            questState.giveItems(1290, 1L);
            questState.playSound("ItemSound.quest_middle");
            questState.setCond(12);
        }
        return null;
    }
}

