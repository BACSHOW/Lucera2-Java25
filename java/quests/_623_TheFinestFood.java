/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _623_TheFinestFood
extends Quest
implements ScriptFile {
    public final int JEREMY = 31521;
    public static final int HOT_SPRINGS_BUFFALO = 21315;
    public static final int HOT_SPRINGS_FLAVA = 21316;
    public static final int HOT_SPRINGS_ANTELOPE = 21318;
    public static final int LEAF_OF_FLAVA = 7199;
    public static final int BUFFALO_MEAT = 7200;
    public static final int ANTELOPE_HORN = 7201;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _623_TheFinestFood() {
        super(1);
        this.addStartNpc(31521);
        this.addTalkId(31521);
        this.addKillId(21315);
        this.addKillId(21316);
        this.addKillId(21318);
        this.addQuestItem(7200);
        this.addQuestItem(7199);
        this.addQuestItem(7201);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            string2 = "jeremy_q0623_0104.htm";
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("623_3")) {
            string2 = "jeremy_q0623_0201.htm";
            questState.takeItems(7199, -1L);
            questState.takeItems(7200, -1L);
            questState.takeItems(7201, -1L);
            int n = Rnd.get(100);
            if (n < 12) {
                questState.giveItems(57, 25000L);
                questState.giveItems(6849, 1L, true);
            } else if (n < 24) {
                questState.giveItems(57, 65000L);
                questState.giveItems(6847, 1L, true);
            } else if (n < 34) {
                questState.giveItems(57, 25000L);
                questState.giveItems(6851, 1L, true);
            } else {
                questState.giveItems(57, 73000L);
                questState.addExpAndSp(230000L, 18250L);
            }
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getState();
        if (n2 == 1) {
            questState.setCond(0);
        }
        if (this.l(questState) >= 300L) {
            questState.setCond(2);
        }
        int n3 = questState.getCond();
        if (n == 31521) {
            if (n3 == 0) {
                if (questState.getPlayer().getLevel() >= 71) {
                    string = "jeremy_q0623_0101.htm";
                } else {
                    string = "jeremy_q0623_0103.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n3 == 1 && this.l(questState) < 300L) {
                string = "jeremy_q0623_0106.htm";
            } else if (n3 == 2 && this.l(questState) >= 300L) {
                string = "jeremy_q0623_0105.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getCond();
        int n2 = npcInstance.getNpcId();
        if (n == 1) {
            if (n2 == 21315) {
                if (questState.getQuestItemsCount(7200) < 100L) {
                    questState.rollAndGive(7200, 1, 100.0);
                    if (questState.getQuestItemsCount(7200) == 100L) {
                        if (this.l(questState) >= 300L) {
                            questState.setCond(2);
                        }
                        questState.playSound("ItemSound.quest_middle");
                    } else {
                        questState.playSound("ItemSound.quest_itemget");
                    }
                }
            } else if (n2 == 21316) {
                if (questState.getQuestItemsCount(7199) < 100L) {
                    questState.rollAndGive(7199, 1, 100.0);
                    if (questState.getQuestItemsCount(7199) == 100L) {
                        if (this.l(questState) >= 300L) {
                            questState.setCond(2);
                        }
                        questState.playSound("ItemSound.quest_middle");
                    } else {
                        questState.playSound("ItemSound.quest_itemget");
                    }
                }
            } else if (n2 == 21318 && questState.getQuestItemsCount(7201) < 100L) {
                questState.rollAndGive(7201, 1, 100.0);
                if (questState.getQuestItemsCount(7201) == 100L) {
                    if (this.l(questState) >= 300L) {
                        questState.setCond(2);
                    }
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        }
        return null;
    }

    private long l(QuestState questState) {
        return questState.getQuestItemsCount(7199) + questState.getQuestItemsCount(7200) + questState.getQuestItemsCount(7201);
    }
}

