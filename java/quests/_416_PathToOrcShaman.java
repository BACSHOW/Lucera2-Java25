/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.GameObjectsStorage;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.CustomMessage;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;

public class _416_PathToOrcShaman
extends Quest
implements ScriptFile {
    private static final int bzT = 30585;
    private static final int bzU = 30592;
    private static final int bzV = 30502;
    private static final int bzW = 30593;
    private static final int bzX = 31979;
    private static final int bzY = 32057;
    private static final int bzZ = 32090;
    private static final int bAa = 1616;
    private static final int bAb = 1617;
    private static final int bAc = 1618;
    private static final int bAd = 1619;
    private static final int bAe = 1620;
    private static final int bAf = 1621;
    private static final int bAg = 1622;
    private static final int bAh = 1623;
    private static final int bAi = 1624;
    private static final int bAj = 1625;
    private static final int bAk = 1626;
    private static final int bAl = 1627;
    private static final int bAm = 1628;
    private static final int bAn = 1629;
    private static final int bAo = 1630;
    private static final int bAp = 1631;
    private static final int bzM = 20479;
    private static final int bzN = 20478;
    private static final int bzO = 20415;
    private static final int bAq = 20335;
    private static final int bAr = 20038;
    private static final int bAs = 20043;
    private static final int bAt = 27056;
    private static final int bAu = 27319;
    private static final int[][] DROPLIST_COND = new int[][]{{1, 0, 20479, 1616, 1617, 1, 70, 1}, {1, 0, 20478, 1616, 1618, 1, 70, 1}, {1, 0, 20415, 1616, 1619, 1, 70, 1}, {6, 7, 20335, 1624, 1625, 3, 70, 1}};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _416_PathToOrcShaman() {
        super(0);
        this.addStartNpc(30585);
        this.addTalkId(30592, 30502, 30593, 31979, 32057, 32090);
        for (int i = 0; i < DROPLIST_COND.length; ++i) {
            this.addKillId(DROPLIST_COND[i][2]);
            this.addQuestItem(DROPLIST_COND[i][4]);
        }
        this.addKillId(20038, 20043, 27056, 27319);
        this.addQuestItem(1616, 1620, 1621, 1622, 1623, 1624, 1626, 1627, 1628, 1629, 1630);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("tataru_zu_hestui_q0416_06.htm")) {
            questState.giveItems(1616, 1L);
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("hestui_totem_spirit_q0416_03.htm")) {
            questState.takeItems(1620, -1L);
            questState.takeItems(1621, -1L);
            questState.giveItems(1622, 1L);
            questState.setCond(4);
        } else if (string.equalsIgnoreCase("tataru_zu_hestui_q0416_11.htm")) {
            questState.takeItems(1622, -1L);
            questState.giveItems(1623, 1L);
            questState.setCond(5);
        } else if (string.equalsIgnoreCase("tataru_zu_hestui_q0416_11c.htm")) {
            questState.takeItems(1622, -1L);
            questState.setCond(12);
        } else if (string.equalsIgnoreCase("dudamara_totem_spirit_q0416_03.htm")) {
            questState.takeItems(1626, -1L);
            questState.giveItems(1627, 1L);
            questState.setCond(9);
        } else if (string.equalsIgnoreCase("seer_umos_q0416_07.htm")) {
            questState.takeItems(1630, -1L);
            if (questState.getPlayer().getClassId().getLevel() == 1) {
                questState.giveItems(1631, 1L);
                if (!questState.getPlayer().getVarB("prof1")) {
                    questState.getPlayer().setVar("prof1", "1", -1L);
                    questState.addExpAndSp(3200L, 2600L);
                }
            }
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        } else if (string.equalsIgnoreCase("totem_spirit_gandi_q0416_02.htm")) {
            questState.setCond(14);
        } else if (string.equalsIgnoreCase("dead_leopard_q0416_04.htm")) {
            questState.setCond(18);
        } else if (string.equalsIgnoreCase("totem_spirit_gandi_q0416_05.htm")) {
            questState.setCond(21);
        }
        if (string.equalsIgnoreCase("QuestMonsterDurkaSpirit_Fail")) {
            for (NpcInstance npcInstance2 : GameObjectsStorage.getAllByNpcId(27056, false)) {
                npcInstance2.deleteMe();
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getCond();
        if (n == 30585) {
            if (questState.getQuestItemsCount(1631) != 0L) {
                string = "seer_umos_q0416_04.htm";
                questState.exitCurrentQuest(true);
            } else if (n2 == 0) {
                if (questState.getPlayer().getClassId().getId() != 49) {
                    string = questState.getPlayer().getClassId().getId() == 50 ? "tataru_zu_hestui_q0416_02a.htm" : "tataru_zu_hestui_q0416_02.htm";
                    questState.exitCurrentQuest(true);
                } else if (questState.getPlayer().getLevel() < 18) {
                    string = "tataru_zu_hestui_q0416_03.htm";
                    questState.exitCurrentQuest(true);
                } else {
                    string = "tataru_zu_hestui_q0416_01.htm";
                }
            } else if (n2 == 1) {
                string = "tataru_zu_hestui_q0416_07.htm";
            } else if (n2 == 2) {
                string = "tataru_zu_hestui_q0416_08.htm";
                questState.takeItems(1617, -1L);
                questState.takeItems(1618, -1L);
                questState.takeItems(1619, -1L);
                questState.takeItems(1616, -1L);
                questState.giveItems(1620, 1L);
                questState.giveItems(1621, 1L);
                questState.setCond(3);
            } else if (n2 == 3) {
                string = "tataru_zu_hestui_q0416_09.htm";
            } else if (n2 == 4) {
                string = "tataru_zu_hestui_q0416_10.htm";
            } else if (n2 == 5) {
                string = "tataru_zu_hestui_q0416_12.htm";
            } else if (n2 > 5) {
                string = "tataru_zu_hestui_q0416_13.htm";
            }
        } else if (n == 30592) {
            if (n2 == 3) {
                string = "hestui_totem_spirit_q0416_01.htm";
            } else if (n2 == 4) {
                string = "hestui_totem_spirit_q0416_04.htm";
            }
        } else if (n == 30592 && questState.getCond() > 0 && (questState.getQuestItemsCount(1625) > 0L || questState.getQuestItemsCount(1624) > 0L || questState.getQuestItemsCount(1626) > 0L || questState.getQuestItemsCount(1627) > 0L || questState.getQuestItemsCount(1628) > 0L || questState.getQuestItemsCount(1630) > 0L || questState.getQuestItemsCount(1623) > 0L)) {
            string = "hestui_totem_spirit_q0416_05.htm";
        } else if (n == 30502) {
            if (n2 == 5) {
                questState.takeItems(1623, -1L);
                questState.giveItems(1624, 1L);
                string = "seer_umos_q0416_01.htm";
                questState.setCond(6);
            } else if (n2 == 6) {
                string = "seer_umos_q0416_02.htm";
            } else if (n2 == 7) {
                questState.takeItems(1625, -1L);
                questState.takeItems(1624, -1L);
                questState.giveItems(1626, 1L);
                string = "seer_umos_q0416_03.htm";
                questState.setCond(8);
            } else if (n2 == 8) {
                string = "seer_umos_q0416_04.htm";
            } else if (n2 == 9 || n2 == 10) {
                string = "seer_umos_q0416_05.htm";
            } else if (n2 == 11) {
                string = "seer_umos_q0416_06.htm";
            }
        } else if (n == 31979) {
            if (n2 == 12) {
                string = "seer_moirase_q0416_01.htm";
                questState.setCond(13);
            } else if (n2 > 12 && n2 < 21) {
                string = "seer_moirase_q0416_02.htm";
            } else if (n2 == 21) {
                string = "seer_moirase_q0416_03.htm";
                if (questState.getPlayer().getClassId().getLevel() == 1) {
                    questState.giveItems(1631, 1L);
                    if (!questState.getPlayer().getVarB("prof1")) {
                        questState.getPlayer().setVar("prof1", "1", -1L);
                        questState.addExpAndSp(295862L, 18194L);
                    }
                }
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            }
        } else if (n == 32057) {
            if (n2 == 13) {
                string = "totem_spirit_gandi_q0416_01.htm";
            } else if (n2 > 13 && n2 < 20) {
                string = "totem_spirit_gandi_q0416_03.htm";
            } else if (n2 == 20) {
                string = "totem_spirit_gandi_q0416_04.htm";
            }
        } else if (n == 32090) {
            if (n2 <= 14) {
                string = "dead_leopard_q0416_01a.htm";
            } else if (n2 == 15) {
                string = "dead_leopard_q0416_01.htm";
                questState.setCond(16);
            } else if (n2 == 16) {
                string = "dead_leopard_q0416_01.htm";
            } else if (n2 == 17) {
                string = "dead_leopard_q0416_02.htm";
            } else if (n2 == 18) {
                string = "dead_leopard_q0416_05.htm";
            } else if (n2 == 19) {
                string = "dead_leopard_q0416_06.htm";
                questState.setCond(20);
            } else {
                string = "dead_leopard_q0416_06.htm";
            }
        } else if (n == 30593) {
            if (n2 == 8) {
                string = "dudamara_totem_spirit_q0416_01.htm";
            } else if (n2 == 9) {
                string = "dudamara_totem_spirit_q0416_04.htm";
            } else if (n2 == 10) {
                questState.takeItems(1628, -1L);
                questState.giveItems(1630, 1L);
                string = "dudamara_totem_spirit_q0416_05.htm";
                questState.setCond(11);
            } else if (n2 == 11) {
                string = "dudamara_totem_spirit_q0416_06.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        for (int i = 0; i < DROPLIST_COND.length; ++i) {
            if (n2 != DROPLIST_COND[i][0] || n != DROPLIST_COND[i][2] || DROPLIST_COND[i][3] != 0 && questState.getQuestItemsCount(DROPLIST_COND[i][3]) <= 0L) continue;
            if (DROPLIST_COND[i][5] == 0) {
                questState.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][6]);
                continue;
            }
            if (!questState.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][7], DROPLIST_COND[i][5], DROPLIST_COND[i][6]) || DROPLIST_COND[i][1] == n2 || DROPLIST_COND[i][1] == 0) continue;
            questState.setCond(DROPLIST_COND[i][1]);
        }
        if (questState.getQuestItemsCount(1617) != 0L && questState.getQuestItemsCount(1618) != 0L && questState.getQuestItemsCount(1619) != 0L) {
            questState.setCond(2);
        } else if (n2 == 9 && (n == 20038 || n == 20043)) {
            if (questState.getQuestItemsCount(1629) < 8L) {
                questState.giveItems(1629, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
            if ((questState.getQuestItemsCount(1629) == 8L || questState.getQuestItemsCount(1629) >= 5L && Rnd.chance(questState.getQuestItemsCount(1629) * 10L)) && GameObjectsStorage.getByNpcId(27056) == null) {
                questState.takeItems(1629, -1L);
                questState.addSpawn(27056);
                questState.startQuestTimer("QuestMonsterDurkaSpirit_Fail", 300000L);
            }
        } else if (n == 27056) {
            questState.cancelQuestTimer("QuestMonsterDurkaSpirit_Fail");
            for (NpcInstance npcInstance2 : GameObjectsStorage.getAllByNpcId(27056, false)) {
                npcInstance2.deleteMe();
            }
            if (n2 == 9) {
                questState.takeItems(1627, -1L);
                questState.takeItems(1629, -1L);
                questState.giveItems(1628, 1L);
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(10);
            }
        } else if (n == 27319) {
            if (n2 == 14 && Rnd.chance(50)) {
                Functions.npcSayCustomMessage(GameObjectsStorage.getByNpcId(32090), new CustomMessage("quests._416_PathToOrcShaman.LeopardCarcass", questState.getPlayer(), new Object[0]).toString(), questState.getPlayer());
                questState.setCond(15);
            } else if (n2 == 16 && Rnd.chance(50)) {
                questState.setCond(17);
            } else if (n2 == 18 && Rnd.chance(50)) {
                questState.setCond(19);
            }
        }
        return null;
    }
}

