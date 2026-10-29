/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _329_CuriosityOfDwarf
extends Quest
implements ScriptFile {
    private static final int bgQ = 1346;
    private static final int bgR = 1365;
    private static final int aIK = 30437;
    private static final int bgS = 20083;
    private static final int bgT = 20085;

    public _329_CuriosityOfDwarf() {
        super(0);
        this.addStartNpc(30437);
        this.addKillId(20083, 20085);
        this.addQuestItem(1365, 1346);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            string2 = "trader_rolento_q0329_03.htm";
        } else if (string.equalsIgnoreCase("reply_1")) {
            string2 = "trader_rolento_q0329_06.htm";
            questState.exitCurrentQuest(true);
            questState.playSound("ItemSound.quest_finish");
        } else if (string.equalsIgnoreCase("reply_2")) {
            string2 = "trader_rolento_q0329_07.htm";
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
                if (n != 30437) break;
                if (questState.getPlayer().getLevel() >= 33) {
                    string = "trader_rolento_q0329_02.htm";
                    break;
                }
                string = "trader_rolento_q0329_01.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n != 30437) break;
                if (questState.getQuestItemsCount(1365) + questState.getQuestItemsCount(1346) > 0L) {
                    if (questState.getQuestItemsCount(1365) + questState.getQuestItemsCount(1346) >= 10L) {
                        questState.giveItems(57, 1183L + 50L * questState.getQuestItemsCount(1365) + 1000L * questState.getQuestItemsCount(1346));
                    } else {
                        questState.giveItems(57, 50L * questState.getQuestItemsCount(1365) + 1000L * questState.getQuestItemsCount(1346));
                    }
                    questState.takeItems(1365, -1L);
                    questState.takeItems(1346, -1L);
                    string = "trader_rolento_q0329_05.htm";
                    break;
                }
                string = "trader_rolento_q0329_04.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = Rnd.get(1, 100);
        if (n == 20085) {
            if (n2 < 5) {
                questState.giveItems(1346, 1L);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 58) {
                questState.giveItems(1365, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20083) {
            if (n2 < 6) {
                questState.giveItems(1346, 1L);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 56) {
                questState.giveItems(1365, 1L);
                questState.playSound("ItemSound.quest_itemget");
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

