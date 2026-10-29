/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.scripts.ScriptFile;

public class _108_JumbleTumbleDiamondFuss
extends Quest
implements ScriptFile {
    private static final int aEc = 1559;
    private static final int aEd = 1560;
    private static final int aEe = 1561;
    private static final int aEf = 1562;
    private static final int aEg = 1563;
    private static final int aEh = 1564;
    private static final int aEi = 1565;
    private static final int aEj = 1566;
    private static final int aEk = 1567;
    private static final int aEl = 1568;
    private static final int aEm = 1569;
    private static final int aEn = 1570;
    private static final int aEo = 1571;
    private static final int aEp = 1511;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _108_JumbleTumbleDiamondFuss() {
        super(0);
        this.addStartNpc(30523);
        this.addTalkId(30516);
        this.addTalkId(30521);
        this.addTalkId(30522);
        this.addTalkId(30526);
        this.addTalkId(30529);
        this.addTalkId(30555);
        this.addKillId(20323);
        this.addKillId(20324);
        this.addKillId(20480);
        this.addQuestItem(1566, 1571, 1559, 1560, 1561, 1563, 1564, 1565, 1567, 1562, 1568, 1569, 1570);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("collector_gouph_q0108_03.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.giveItems(1559, 1L);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equals("carrier_torocco_q0108_02.htm")) {
            questState.takeItems(1560, 1L);
            questState.giveItems(1561, 1L);
            questState.setCond(3);
        } else if (string.equals("blacksmith_bronp_q0108_02.htm")) {
            questState.takeItems(1562, 1L);
            questState.giveItems(1563, 1L);
            questState.setCond(5);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getCond();
        if (n == 30523) {
            if (n2 == 0) {
                if (questState.getPlayer().getRace() != Race.dwarf) {
                    string = "collector_gouph_q0108_00.htm";
                    questState.exitCurrentQuest(true);
                } else if (questState.getPlayer().getLevel() >= 10) {
                    string = "collector_gouph_q0108_02.htm";
                } else {
                    string = "collector_gouph_q0108_01.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 0 && questState.getQuestItemsCount(1559) > 0L) {
                string = "collector_gouph_q0108_04.htm";
            } else if (n2 > 1 && n2 < 7 && (questState.getQuestItemsCount(1560) > 0L || questState.getQuestItemsCount(1561) > 0L || questState.getQuestItemsCount(1562) > 0L || questState.getQuestItemsCount(1563) > 0L)) {
                string = "collector_gouph_q0108_05.htm";
            } else if (n2 == 7 && questState.getQuestItemsCount(1566) > 0L) {
                string = "collector_gouph_q0108_06.htm";
                questState.takeItems(1566, 1L);
                questState.giveItems(1567, 1L);
                questState.setCond(8);
            } else if (n2 > 7 && n2 < 12 && (questState.getQuestItemsCount(1568) > 0L || questState.getQuestItemsCount(1567) > 0L || questState.getQuestItemsCount(1569) > 0L || questState.getQuestItemsCount(1570) > 0L)) {
                string = "collector_gouph_q0108_07.htm";
            } else if (n2 == 12 && questState.getQuestItemsCount(1571) > 0L) {
                string = "collector_gouph_q0108_08.htm";
                questState.takeItems(1571, 1L);
                questState.giveItems(1511, 1L);
                if (questState.getPlayer().getClassId().getLevel() == 1 && !questState.getPlayer().getVarB("p1q3")) {
                    questState.getPlayer().setVar("p1q3", "1", -1L);
                    questState.getPlayer().sendPacket((IStaticPacket)new ExShowScreenMessage("Now go find the Newbie Guide.", 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                    questState.giveItems(1060, 100L);
                    for (int i = 4412; i <= 4417; ++i) {
                        questState.giveItems(i, 10L);
                    }
                    if (questState.getPlayer().getClassId().isMage()) {
                        questState.playTutorialVoice("tutorial_voice_027");
                        questState.giveItems(5790, 3000L);
                    } else {
                        questState.playTutorialVoice("tutorial_voice_026");
                        questState.giveItems(5789, 7000L);
                    }
                }
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(false);
            }
        } else if (n == 30516) {
            if (n2 == 1 && questState.getQuestItemsCount(1559) > 0L) {
                string = "trader_reep_q0108_01.htm";
                questState.giveItems(1560, 1L);
                questState.takeItems(1559, 1L);
                questState.setCond(2);
            } else if (n2 >= 2) {
                string = "trader_reep_q0108_02.htm";
            }
        } else if (n == 30555) {
            string = n2 == 2 && questState.getQuestItemsCount(1560) == 1L ? "carrier_torocco_q0108_01.htm" : (n2 == 3 && questState.getQuestItemsCount(1561) > 0L ? "carrier_torocco_q0108_03.htm" : (n2 == 7 && questState.getQuestItemsCount(1566) == 1L ? "carrier_torocco_q0108_04.htm" : "carrier_torocco_q0108_05.htm"));
        } else if (n == 30529) {
            if (n2 == 3 && questState.getQuestItemsCount(1561) > 0L) {
                questState.takeItems(1561, 1L);
                questState.giveItems(1562, 1L);
                string = "miner_maron_q0108_01.htm";
                questState.setCond(4);
            } else {
                string = n2 == 4 ? "miner_maron_q0108_02.htm" : "miner_maron_q0108_03.htm";
            }
        } else if (n == 30526) {
            if (n2 == 4 && questState.getQuestItemsCount(1562) > 0L) {
                string = "blacksmith_bronp_q0108_01.htm";
            } else if (n2 == 5 && questState.getQuestItemsCount(1563) > 0L && (questState.getQuestItemsCount(1564) < 10L || questState.getQuestItemsCount(1565) < 10L)) {
                string = "blacksmith_bronp_q0108_03.htm";
            } else if (n2 == 6 && questState.getQuestItemsCount(1563) > 0L && questState.getQuestItemsCount(1564) == 10L && questState.getQuestItemsCount(1565) == 10L) {
                string = "blacksmith_bronp_q0108_04.htm";
                questState.takeItems(1563, -1L);
                questState.takeItems(1564, -1L);
                questState.takeItems(1565, -1L);
                questState.giveItems(1566, 1L);
                questState.setCond(7);
            } else if (n2 == 7 && questState.getQuestItemsCount(1566) > 0L) {
                string = "blacksmith_bronp_q0108_05.htm";
            } else if (n2 == 8 && questState.getQuestItemsCount(1567) > 0L) {
                string = "blacksmith_bronp_q0108_06.htm";
                questState.takeItems(1567, 1L);
                questState.giveItems(1568, 1L);
                questState.setCond(9);
            } else {
                string = n2 == 9 && questState.getQuestItemsCount(1568) > 0L ? "blacksmith_bronp_q0108_07.htm" : "blacksmith_bronp_q0108_08.htm";
            }
        } else if (n == 30521) {
            if (n2 == 9 && questState.getQuestItemsCount(1568) > 0L) {
                string = "warehouse_murphrin_q0108_01.htm";
                questState.takeItems(1568, 1L);
                questState.giveItems(1569, 1L);
                questState.setCond(10);
            } else {
                string = n2 == 10 && questState.getQuestItemsCount(1569) > 0L ? "warehouse_murphrin_q0108_02.htm" : "warehouse_murphrin_q0108_03.htm";
            }
        } else if (n == 30522) {
            if (n2 == 10 && questState.getQuestItemsCount(1569) > 0L) {
                string = "warehouse_airy_q0108_01.htm";
                questState.takeItems(1569, 1L);
                questState.giveItems(1570, 1L);
                questState.setCond(11);
            } else {
                string = n2 == 11 && questState.getQuestItemsCount(1570) > 0L ? "warehouse_airy_q0108_02.htm" : (n2 == 12 && questState.getQuestItemsCount(1571) > 0L ? "warehouse_airy_q0108_03.htm" : "warehouse_airy_q0108_04.htm");
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 20323 || n == 20324) {
            if (n2 == 5 && questState.getQuestItemsCount(1563) > 0L) {
                if (questState.getQuestItemsCount(1564) < 10L && Rnd.chance(80)) {
                    questState.giveItems(1564, 1L);
                    if (questState.getQuestItemsCount(1564) < 10L) {
                        questState.playSound("ItemSound.quest_itemget");
                    } else {
                        questState.playSound("ItemSound.quest_middle");
                        if (questState.getQuestItemsCount(1564) == 10L && questState.getQuestItemsCount(1565) == 10L) {
                            questState.setCond(6);
                        }
                    }
                }
                if (questState.getQuestItemsCount(1565) < 10L && Rnd.chance(80)) {
                    questState.giveItems(1565, 1L);
                    if (questState.getQuestItemsCount(1565) < 10L) {
                        questState.playSound("ItemSound.quest_itemget");
                    } else {
                        questState.playSound("ItemSound.quest_middle");
                        if (questState.getQuestItemsCount(1564) == 10L && questState.getQuestItemsCount(1565) == 10L) {
                            questState.setCond(6);
                        }
                    }
                }
            }
        } else if (n == 20480 && n2 == 11 && questState.getQuestItemsCount(1570) > 0L && questState.getQuestItemsCount(1571) == 0L && Rnd.chance(50)) {
            questState.takeItems(1570, 1L);
            questState.giveItems(1571, 1L);
            questState.setCond(12);
            questState.playSound("ItemSound.quest_middle");
        }
        return null;
    }
}

