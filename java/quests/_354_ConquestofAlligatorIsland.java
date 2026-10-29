/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _354_ConquestofAlligatorIsland
extends Quest
implements ScriptFile {
    private static final int bqI = 30895;
    private static final int bqJ = 20804;
    private static final int bqK = 20805;
    private static final int bqL = 20806;
    private static final int bqM = 20807;
    private static final int bqN = 20808;
    private static final int bqO = 20991;
    private static final int bqP = 5863;
    private static final int bqQ = 5864;
    private static final int bqR = 5915;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _354_ConquestofAlligatorIsland() {
        super(0);
        this.addStartNpc(30895);
        this.addKillId(20804, 20805, 20806, 20807, 20808, 20991);
        this.addQuestItem(5863, 5864);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = npcInstance.getNpcId();
        if (n == 30895) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "warehouse_keeper_kluck_q0354_03.htm";
            } else if (string.equalsIgnoreCase("reply_1")) {
                if (questState.getQuestItemsCount(5863) >= 100L) {
                    questState.giveItems(57, questState.getQuestItemsCount(5863) * 220L + 10700L);
                    questState.takeItems(5863, -1L);
                    string2 = "warehouse_keeper_kluck_q0354_06b.htm";
                } else if (questState.getQuestItemsCount(5863) > 0L && questState.getQuestItemsCount(5863) < 100L) {
                    questState.giveItems(57, questState.getQuestItemsCount(5863) * 220L + 3100L);
                    questState.takeItems(5863, questState.getQuestItemsCount(5863));
                    string2 = "warehouse_keeper_kluck_q0354_06a.htm";
                } else if (questState.getQuestItemsCount(5863) == 0L) {
                    string2 = "warehouse_keeper_kluck_q0354_06.htm";
                }
            } else if (string.equalsIgnoreCase("reply_2")) {
                string2 = "warehouse_keeper_kluck_q0354_07.htm";
            } else if (string.equalsIgnoreCase("reply_3")) {
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                string2 = "warehouse_keeper_kluck_q0354_08.htm";
            } else if (string.equalsIgnoreCase("reply_4") && questState.getQuestItemsCount(5864) > 0L && questState.getQuestItemsCount(5864) < 10L) {
                string2 = "warehouse_keeper_kluck_q0354_09.htm";
            } else if (string.equalsIgnoreCase("reply_4") && questState.getQuestItemsCount(5864) >= 10L) {
                questState.giveItems(5915, 1L);
                questState.takeItems(5864, 10L);
                string2 = "warehouse_keeper_kluck_q0354_10.htm";
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
                if (n != 30895) break;
                if (questState.getPlayer().getLevel() < 38) {
                    string = "warehouse_keeper_kluck_q0354_01.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                string = "warehouse_keeper_kluck_q0354_02.htm";
                break;
            }
            case 2: {
                if (n != 30895) break;
                if (questState.getQuestItemsCount(5864) == 0L) {
                    string = "warehouse_keeper_kluck_q0354_04.htm";
                    break;
                }
                if (questState.getQuestItemsCount(5864) < 1L) break;
                string = "warehouse_keeper_kluck_q0354_05.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (n == 20804) {
            if (Rnd.get(100) < 84) {
                questState.giveItems(5863, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
            if (Rnd.get(10) == 5) {
                questState.giveItems(5864, 1L);
            }
        } else if (n == 20805) {
            if (Rnd.get(100) < 91) {
                questState.giveItems(5863, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
            if (Rnd.get(10) == 5) {
                questState.giveItems(5864, 1L);
            }
        } else if (n == 20806) {
            if (Rnd.get(100) < 88) {
                questState.giveItems(5863, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
            if (Rnd.get(10) == 5) {
                questState.giveItems(5864, 1L);
            }
        } else if (n == 20807) {
            if (Rnd.get(100) < 92) {
                questState.giveItems(5863, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
            if (Rnd.get(10) == 5) {
                questState.giveItems(5864, 1L);
            }
        } else if (n == 20808) {
            if (Rnd.get(100) < 14) {
                questState.giveItems(5863, 2L);
                questState.playSound("ItemSound.quest_itemget");
            } else {
                questState.giveItems(5863, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
            if (Rnd.get(10) == 5) {
                questState.giveItems(5864, 1L);
            }
        } else if (n == 20991) {
            if (Rnd.get(100) < 69) {
                questState.giveItems(5863, 2L);
                questState.playSound("ItemSound.quest_itemget");
            } else {
                questState.giveItems(5863, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
            if (Rnd.get(10) == 5) {
                questState.giveItems(5864, 1L);
            }
        }
        return null;
    }
}

