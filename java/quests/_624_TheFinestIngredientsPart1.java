/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _624_TheFinestIngredientsPart1
extends Quest
implements ScriptFile {
    private static final int bPv = 31521;
    private static final int bPw = 21321;
    private static final int bPx = 21319;
    private static final int bPy = 21317;
    private static final int bHh = 21314;
    private static final int bPz = 7204;
    private static final int bPA = 7202;
    private static final int bPB = 7203;
    private static final int bPC = 7080;
    private static final int bPD = 7205;

    public _624_TheFinestIngredientsPart1() {
        super(1);
        this.addStartNpc(31521);
        this.addKillId(21321, 21319, 21317, 21314);
        this.addQuestItem(7202, 7203, 7204);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            questState.setState(2);
            questState.set("finest_ingredients_part1", String.valueOf(11), true);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
            string2 = "jeremy_q0624_0104.htm";
        } else if (string.equalsIgnoreCase("reply_3")) {
            if (questState.getQuestItemsCount(7202) >= 50L && questState.getQuestItemsCount(7203) >= 50L && questState.getQuestItemsCount(7204) >= 50L) {
                questState.takeItems(7202, -1L);
                questState.takeItems(7203, -1L);
                questState.takeItems(7204, -1L);
                questState.giveItems(7205, 1L);
                questState.giveItems(7080, 1L);
                questState.exitCurrentQuest(true);
                questState.playSound("ItemSound.quest_finish");
                string2 = "jeremy_q0624_0201.htm";
            } else {
                string2 = "jeremy_q0624_0202.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getState();
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getInt("finest_ingredients_part1");
        switch (n) {
            case 1: {
                if (n2 != 31521) break;
                if (questState.getPlayer().getLevel() >= 73) {
                    string = "jeremy_q0624_0101.htm";
                    break;
                }
                string = "jeremy_q0624_0103.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n2 != 31521 || n3 < 11 || n3 > 12) break;
                string = n3 == 12 && questState.getQuestItemsCount(7203) >= 50L && questState.getQuestItemsCount(7202) >= 50L && questState.getQuestItemsCount(7204) >= 50L ? "jeremy_q0624_0105.htm" : "jeremy_q0624_0106.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (n == 21321) {
            if (questState.getQuestItemsCount(7204) + 1L >= 50L) {
                if (questState.getQuestItemsCount(7204) < 50L) {
                    questState.giveItems(7204, 50L - questState.getQuestItemsCount(7204));
                    questState.playSound("ItemSound.quest_middle");
                }
                if (questState.getQuestItemsCount(7203) >= 50L && questState.getQuestItemsCount(7202) >= 50L) {
                    questState.setCond(2);
                    questState.set("finest_ingredients_part1", String.valueOf(12), true);
                }
            } else {
                questState.giveItems(7204, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 21319) {
            if (questState.getQuestItemsCount(7202) + 1L >= 50L) {
                if (questState.getQuestItemsCount(7202) < 50L) {
                    questState.giveItems(7202, 50L - questState.getQuestItemsCount(7202));
                    questState.playSound("ItemSound.quest_middle");
                }
                if (questState.getQuestItemsCount(7204) >= 50L && questState.getQuestItemsCount(7203) >= 50L) {
                    questState.setCond(2);
                    questState.set("finest_ingredients_part1", String.valueOf(12), true);
                }
            } else {
                questState.giveItems(7202, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 21317) {
            if (questState.getQuestItemsCount(7204) + 1L >= 50L) {
                if (questState.getQuestItemsCount(7204) < 50L) {
                    questState.giveItems(7204, 50L - questState.getQuestItemsCount(7204));
                    questState.playSound("ItemSound.quest_middle");
                }
                if (questState.getQuestItemsCount(7203) >= 50L && questState.getQuestItemsCount(7202) >= 50L) {
                    questState.setCond(2);
                    questState.set("finest_ingredients_part1", String.valueOf(12), true);
                }
            } else {
                questState.giveItems(7204, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 21314) {
            if (questState.getQuestItemsCount(7203) + 1L >= 50L) {
                if (questState.getQuestItemsCount(7203) < 50L) {
                    questState.giveItems(7203, 50L - questState.getQuestItemsCount(7203));
                    questState.playSound("ItemSound.quest_middle");
                }
                if (questState.getQuestItemsCount(7204) >= 50L && questState.getQuestItemsCount(7202) >= 50L) {
                    questState.setCond(2);
                    questState.set("finest_ingredients_part1", String.valueOf(12), true);
                }
            } else {
                questState.giveItems(7203, 1L);
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

