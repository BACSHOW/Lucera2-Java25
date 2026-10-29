/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _153_DeliverGoods
extends Quest
implements ScriptFile {
    int DELIVERY_LIST = 1012;
    int HEAVY_WOOD_BOX = 1013;
    int CLOTH_BUNDLE = 1014;
    int CLAY_POT = 1015;
    int JACKSONS_RECEIPT = 1016;
    int SILVIAS_RECEIPT = 1017;
    int RANTS_RECEIPT = 1018;
    int RING_OF_KNOWLEDGE = 875;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _153_DeliverGoods() {
        super(0);
        this.addStartNpc(30041);
        this.addTalkId(30002);
        this.addTalkId(30003);
        this.addTalkId(30054);
        this.addQuestItem(this.HEAVY_WOOD_BOX, this.CLOTH_BUNDLE, this.CLAY_POT, this.DELIVERY_LIST, this.JACKSONS_RECEIPT, this.SILVIAS_RECEIPT, this.RANTS_RECEIPT);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("30041-04.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            if (questState.getQuestItemsCount(this.DELIVERY_LIST) == 0L) {
                questState.giveItems(this.DELIVERY_LIST, 1L);
            }
            if (questState.getQuestItemsCount(this.HEAVY_WOOD_BOX) == 0L) {
                questState.giveItems(this.HEAVY_WOOD_BOX, 1L);
            }
            if (questState.getQuestItemsCount(this.CLOTH_BUNDLE) == 0L) {
                questState.giveItems(this.CLOTH_BUNDLE, 1L);
            }
            if (questState.getQuestItemsCount(this.CLAY_POT) == 0L) {
                questState.giveItems(this.CLAY_POT, 1L);
            }
            string2 = "30041-04.htm";
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getCond();
        if (n == 30041) {
            if (n2 == 0) {
                if (questState.getPlayer().getLevel() >= 2) {
                    string = "30041-03.htm";
                    return string;
                }
                string = "30041-02.htm";
                questState.exitCurrentQuest(true);
            } else if (n2 == 1 && questState.getQuestItemsCount(this.JACKSONS_RECEIPT) + questState.getQuestItemsCount(this.SILVIAS_RECEIPT) + questState.getQuestItemsCount(this.RANTS_RECEIPT) == 0L) {
                string = "30041-05.htm";
            } else if (n2 == 1 && questState.getQuestItemsCount(this.JACKSONS_RECEIPT) + questState.getQuestItemsCount(this.SILVIAS_RECEIPT) + questState.getQuestItemsCount(this.RANTS_RECEIPT) == 3L) {
                questState.giveItems(this.RING_OF_KNOWLEDGE, 1L);
                questState.takeItems(this.DELIVERY_LIST, -1L);
                questState.takeItems(this.JACKSONS_RECEIPT, -1L);
                questState.takeItems(this.SILVIAS_RECEIPT, -1L);
                questState.takeItems(this.RANTS_RECEIPT, -1L);
                questState.addExpAndSp(600L, 0L);
                questState.playSound("ItemSound.quest_finish");
                string = "30041-06.htm";
                questState.exitCurrentQuest(false);
            }
        } else if (n == 30002) {
            if (n2 == 1 && questState.getQuestItemsCount(this.HEAVY_WOOD_BOX) == 1L) {
                questState.takeItems(this.HEAVY_WOOD_BOX, -1L);
                if (questState.getQuestItemsCount(this.JACKSONS_RECEIPT) == 0L) {
                    questState.giveItems(this.JACKSONS_RECEIPT, 1L);
                }
                string = "30002-01.htm";
            } else if (n2 == 1 && questState.getQuestItemsCount(this.JACKSONS_RECEIPT) > 0L) {
                string = "30002-02.htm";
            }
        } else if (n == 30003) {
            if (n2 == 1 && questState.getQuestItemsCount(this.CLOTH_BUNDLE) == 1L) {
                questState.takeItems(this.CLOTH_BUNDLE, -1L);
                if (questState.getQuestItemsCount(this.SILVIAS_RECEIPT) == 0L) {
                    questState.giveItems(this.SILVIAS_RECEIPT, 1L);
                    if (questState.getPlayer().getClassId().isMage()) {
                        questState.giveItems(2509, 3L);
                    } else {
                        questState.giveItems(1835, 6L);
                    }
                }
                string = "30003-01.htm";
            } else if (n2 == 1 && questState.getQuestItemsCount(this.SILVIAS_RECEIPT) > 0L) {
                string = "30003-02.htm";
            }
        } else if (n == 30054) {
            if (n2 == 1 && questState.getQuestItemsCount(this.CLAY_POT) == 1L) {
                questState.takeItems(this.CLAY_POT, -1L);
                if (questState.getQuestItemsCount(this.RANTS_RECEIPT) == 0L) {
                    questState.giveItems(this.RANTS_RECEIPT, 1L);
                }
                string = "30054-01.htm";
            } else if (n2 == 1 && questState.getQuestItemsCount(this.RANTS_RECEIPT) > 0L) {
                string = "30054-02.htm";
            }
        }
        return string;
    }
}

