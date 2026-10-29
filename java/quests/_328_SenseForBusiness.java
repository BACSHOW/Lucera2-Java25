/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _328_SenseForBusiness
extends Quest
implements ScriptFile {
    private final int bgM = 30436;
    private final int bgN = 1347;
    private final int bgO = 1366;
    private final int bgP = 1348;

    public _328_SenseForBusiness() {
        super(0);
        this.addStartNpc(30436);
        this.addKillId(20055, 20059, 20067, 20068, 20070, 20072);
        this.addQuestItem(1347, 1366, 1348);
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

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            string2 = "trader_salient_q0328_03.htm";
        } else if (string.equalsIgnoreCase("reply_1")) {
            string2 = "trader_salient_q0328_06.htm";
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        } else if (string.equalsIgnoreCase("reply_2")) {
            string2 = "trader_salient_q0328_07.htm";
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
                if (n != 30436) break;
                if (questState.getPlayer().getLevel() >= 21) {
                    string = "trader_salient_q0328_02.htm";
                    break;
                }
                string = "trader_salient_q0328_01.htm";
                break;
            }
            case 2: {
                if (n != 30436) break;
                if (questState.getQuestItemsCount(1347) + questState.getQuestItemsCount(1366) + questState.getQuestItemsCount(1348) > 0L) {
                    if (questState.getQuestItemsCount(1347) + questState.getQuestItemsCount(1366) + questState.getQuestItemsCount(1348) >= 10L) {
                        questState.giveItems(57, 618L + 25L * questState.getQuestItemsCount(1347) + 1000L * questState.getQuestItemsCount(1366) + 60L * questState.getQuestItemsCount(1348));
                    } else {
                        questState.giveItems(57, 25L * questState.getQuestItemsCount(1347) + 1000L * questState.getQuestItemsCount(1366) + 60L * questState.getQuestItemsCount(1348));
                    }
                    questState.takeItems(1347, -1L);
                    questState.takeItems(1366, -1L);
                    questState.takeItems(1348, -1L);
                    string = "trader_salient_q0328_05.htm";
                    break;
                }
                string = "trader_salient_q0328_04.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = Rnd.get(1, 100);
        if (n == 20055) {
            if (n2 < 47) {
                questState.giveItems(1347, 1L);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 49) {
                questState.giveItems(1366, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20059) {
            if (n2 < 51) {
                questState.giveItems(1347, 1L);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 53) {
                questState.giveItems(1366, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20067) {
            if (n2 < 67) {
                questState.giveItems(1347, 1L);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 69) {
                questState.giveItems(1366, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20068) {
            if (n2 < 75) {
                questState.giveItems(1347, 1L);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 77) {
                questState.giveItems(1366, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20070) {
            if (n2 < 50) {
                questState.giveItems(1348, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20072 && n2 < 51) {
            questState.giveItems(1348, 1L);
            questState.playSound("ItemSound.quest_itemget");
        }
        return null;
    }
}

