/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _357_WarehouseKeepersAmbition
extends Quest
implements ScriptFile {
    private static final int brj = 30686;
    private static final int brk = 20594;
    private static final int brl = 20595;
    private static final int brm = 20596;
    private static final int brn = 20597;
    private static final int bro = 5867;

    public _357_WarehouseKeepersAmbition() {
        super(2);
        this.addStartNpc(30686);
        this.addKillId(20594, 20595, 20596, 20597);
        this.addQuestItem(5867);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            string2 = "warehouse_keeper_silva_q0357_05.htm";
        } else if (string.equalsIgnoreCase("reply_1")) {
            string2 = "warehouse_keeper_silva_q0357_03.htm";
        } else if (string.equalsIgnoreCase("reply_2")) {
            string2 = "warehouse_keeper_silva_q0357_04.htm";
        } else if (string.equalsIgnoreCase("reply_3")) {
            if (questState.getQuestItemsCount(5867) < 100L && questState.getQuestItemsCount(5867) > 0L) {
                questState.giveItems(57, questState.getQuestItemsCount(5867) * 425L + 13500L);
                questState.takeItems(5867, questState.getQuestItemsCount(5867));
                string2 = "warehouse_keeper_silva_q0357_08.htm";
            } else if (questState.getQuestItemsCount(5867) >= 100L) {
                questState.giveItems(57, questState.getQuestItemsCount(5867) * 425L + 40500L);
                questState.takeItems(5867, questState.getQuestItemsCount(5867));
                string2 = "warehouse_keeper_silva_q0357_09.htm";
            }
        } else if (string.equalsIgnoreCase("reply_4")) {
            string2 = "warehouse_keeper_silva_q0357_10.htm";
        } else if (string.equalsIgnoreCase("reply_5")) {
            if (questState.getQuestItemsCount(5867) < 100L && questState.getQuestItemsCount(5867) > 0L) {
                questState.giveItems(57, questState.getQuestItemsCount(5867) * 425L);
            } else if (questState.getQuestItemsCount(5867) >= 100L) {
                questState.giveItems(57, questState.getQuestItemsCount(5867) * 425L + 40500L);
            }
            questState.takeItems(5867, questState.getQuestItemsCount(5867));
            string2 = "warehouse_keeper_silva_q0357_11.htm";
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getState();
        switch (n) {
            case 1: {
                if (questState.getPlayer().getLevel() < 47) {
                    string = "warehouse_keeper_silva_q0357_01.htm";
                    break;
                }
                if (questState.getPlayer().getLevel() < 47) break;
                string = "warehouse_keeper_silva_q0357_02.htm";
                break;
            }
            case 2: {
                if (questState.getQuestItemsCount(5867) < 1L) {
                    string = "warehouse_keeper_silva_q0357_06.htm";
                    break;
                }
                if (questState.getQuestItemsCount(5867) < 1L) break;
                string = "warehouse_keeper_silva_q0357_07.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (n == 20594 && Rnd.get(1000) < 577) {
            questState.rollAndGive(5867, 1, 100.0);
        } else if (n == 20595 && Rnd.get(100) < 60) {
            questState.rollAndGive(5867, 1, 100.0);
        } else if (n == 20596 && Rnd.get(1000) < 638) {
            questState.rollAndGive(5867, 1, 100.0);
        } else if (n == 20597) {
            if (Rnd.get(1000) < 62) {
                questState.rollAndGive(5867, 2, 100.0);
            } else {
                questState.rollAndGive(5867, 1, 100.0);
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

