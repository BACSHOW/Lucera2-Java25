/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _637_ThroughOnceMore
extends Quest
implements ScriptFile {
    private static final int bQC = 32010;
    private static final int aAQ = 21565;
    private static final int aAR = 21566;
    private static final int aAS = 21567;
    private static final int bQG = 8066;
    private static final int bQE = 8064;
    private static final int bQF = 8065;
    private static final int bQD = 8067;
    private static final int bQH = 8273;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _637_ThroughOnceMore() {
        super(0);
        this.addStartNpc(32010);
        this.addKillId(21565, 21566, 21567);
        this.addQuestItem(8066);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = npcInstance.getNpcId();
        if (n == 32010) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("beyond_the_door_agai", String.valueOf(1), true);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "falsepriest_flauron_q0637_11.htm";
            } else if (string.equalsIgnoreCase("reply_1")) {
                string2 = "falsepriest_flauron_q0637_06.htm";
            } else if (string.equalsIgnoreCase("reply_2")) {
                string2 = "falsepriest_flauron_q0637_07.htm";
            } else if (string.equalsIgnoreCase("reply_3")) {
                string2 = "falsepriest_flauron_q0637_08.htm";
            } else if (string.equalsIgnoreCase("reply_4")) {
                string2 = "falsepriest_flauron_q0637_09.htm";
            } else if (string.equalsIgnoreCase("reply_5")) {
                string2 = "falsepriest_flauron_q0637_10.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("beyond_the_door_agai");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 32010) break;
                if (questState.getPlayer().getLevel() >= 73 && questState.getQuestItemsCount(8065) >= 1L && questState.getQuestItemsCount(8067) == 0L) {
                    string = "falsepriest_flauron_q0637_01.htm";
                    break;
                }
                string = "falsepriest_flauron_q0637_02.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n2 != 32010) break;
                if (n == 1 && questState.getQuestItemsCount(8064) >= 1L && questState.getQuestItemsCount(8065) == 0L && questState.getQuestItemsCount(8067) == 0L) {
                    string = "falsepriest_flauron_q0637_03.htm";
                    break;
                }
                if (n == 1 && questState.getQuestItemsCount(8064) == 0L && questState.getQuestItemsCount(8065) == 0L && questState.getQuestItemsCount(8067) == 0L) {
                    string = "falsepriest_flauron_q0637_04.htm";
                    break;
                }
                if (n == 1 && questState.getQuestItemsCount(8067) >= 1L) {
                    string = "falsepriest_flauron_q0637_05.htm";
                    break;
                }
                if (n == 1 && questState.getQuestItemsCount(8066) < 10L) {
                    string = "falsepriest_flauron_q0637_12.htm";
                    break;
                }
                if (n != 1 || questState.getQuestItemsCount(8066) < 10L) break;
                questState.giveItems(8067, 1L);
                questState.giveItems(8273, 10L);
                questState.takeItems(8064, -1L);
                questState.takeItems(8065, -1L);
                questState.takeItems(8066, -1L);
                questState.unset("beyond_the_door_agai");
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                string = "falsepriest_flauron_q0637_13.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getInt("beyond_the_door_agai");
        int n2 = npcInstance.getNpcId();
        if (n == 1 && questState.getQuestItemsCount(8066) < 10L) {
            if (n2 == 21565) {
                if (Rnd.get(100) < 84) {
                    questState.giveItems(8066, 1L);
                    if (questState.getQuestItemsCount(8066) >= 9L) {
                        questState.setCond(2);
                        questState.playSound("ItemSound.quest_middle");
                    } else {
                        questState.playSound("ItemSound.quest_itemget");
                    }
                }
            } else if (n2 == 21566) {
                if (Rnd.get(100) < 92) {
                    questState.giveItems(8066, 1L);
                    if (questState.getQuestItemsCount(8066) > 9L) {
                        questState.setCond(2);
                        questState.playSound("ItemSound.quest_middle");
                    } else {
                        questState.playSound("ItemSound.quest_itemget");
                    }
                }
            } else if (n2 == 21567) {
                if (Rnd.get(100) < 10) {
                    questState.giveItems(8066, 1L);
                    if (questState.getQuestItemsCount(8066) >= 9L) {
                        questState.setCond(2);
                        questState.playSound("ItemSound.quest_middle");
                    } else {
                        questState.playSound("ItemSound.quest_itemget");
                    }
                } else {
                    questState.giveItems(8066, 1L);
                    if (questState.getQuestItemsCount(8066) >= 9L) {
                        questState.setCond(2);
                        questState.playSound("ItemSound.quest_middle");
                    } else {
                        questState.playSound("ItemSound.quest_itemget");
                    }
                }
            }
        }
        return null;
    }
}

