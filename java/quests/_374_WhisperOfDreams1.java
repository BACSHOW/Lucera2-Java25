/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _374_WhisperOfDreams1
extends Quest
implements ScriptFile {
    private static final int bvV = 30515;
    private static final int bjk = 30557;
    private static final int bvW = 20620;
    private static final int bvX = 20621;
    private static final int bvY = 5884;
    private static final int bvZ = 5885;
    private static final int bwa = 5886;
    private static final int bwb = 5887;

    public _374_WhisperOfDreams1() {
        super(1);
        this.addStartNpc(30515);
        this.addTalkId(30515, 30557);
        this.addKillId(20620, 20621);
        this.addQuestItem(5884, 5885, 5886, 5887);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = npcInstance.getNpcId();
        if (n == 30515) {
            if (string.equalsIgnoreCase("quest_accept")) {
                string2 = "seer_manakia_q0374_03.htm";
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
            } else if (string.equalsIgnoreCase("reply_1")) {
                string2 = "seer_manakia_q0374_06.htm";
            } else if (string.equalsIgnoreCase("reply_2")) {
                string2 = "seer_manakia_q0374_07.htm";
                questState.exitCurrentQuest(true);
                questState.playSound("ItemSound.quest_finish");
            } else if (string.equalsIgnoreCase("reply_3") && questState.getQuestItemsCount(5884) >= 65L && questState.getQuestItemsCount(5885) >= 65L) {
                questState.giveItems(5486, 3L);
                questState.giveItems(57, 15886L, true);
                questState.takeItems(5884, -1L);
                questState.takeItems(5885, -1L);
                string2 = "seer_manakia_q0374_10.htm";
            } else if (string.equalsIgnoreCase("reply_4") && questState.getQuestItemsCount(5884) >= 65L && questState.getQuestItemsCount(5885) >= 65L) {
                questState.giveItems(5487, 2L);
                questState.giveItems(57, 28458L, true);
                questState.takeItems(5884, -1L);
                questState.takeItems(5885, -1L);
                string2 = "seer_manakia_q0374_10.htm";
            } else if (string.equalsIgnoreCase("reply_5") && questState.getQuestItemsCount(5884) >= 65L && questState.getQuestItemsCount(5885) >= 65L) {
                questState.giveItems(5488, 2L);
                questState.giveItems(57, 28458L, true);
                questState.takeItems(5884, -1L);
                questState.takeItems(5885, -1L);
                string2 = "seer_manakia_q0374_10.htm";
            } else if (string.equalsIgnoreCase("reply_6") && questState.getQuestItemsCount(5884) >= 65L && questState.getQuestItemsCount(5885) >= 65L) {
                questState.giveItems(5485, 4L);
                questState.giveItems(57, 28458L, true);
                questState.takeItems(5884, -1L);
                questState.takeItems(5885, -1L);
                string2 = "seer_manakia_q0374_10.htm";
            } else if (string.equalsIgnoreCase("reply_7") && questState.getQuestItemsCount(5884) >= 65L && questState.getQuestItemsCount(5885) >= 65L) {
                questState.giveItems(5489, 6L);
                questState.giveItems(57, 28458L, true);
                questState.takeItems(5884, -1L);
                questState.takeItems(5885, -1L);
                string2 = "seer_manakia_q0374_10.htm";
            }
        }
        if (n == 30557 && string.equalsIgnoreCase("reply_1") && questState.getQuestItemsCount(5886) > 0L) {
            string2 = "torai_q0374_02.htm";
            questState.setCond(3);
            questState.playSound("ItemSound.quest_middle");
            questState.takeItems(5886, -1L);
            questState.giveItems(5887, 1L);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getState();
        int n3 = questState.getCond();
        switch (n2) {
            case 1: {
                if (n != 30515) break;
                if (questState.getPlayer().getLevel() >= 56) {
                    string = "seer_manakia_q0374_01.htm";
                    break;
                }
                questState.exitCurrentQuest(true);
                string = "seer_manakia_q0374_02.htm";
                break;
            }
            case 2: {
                if (n == 30515) {
                    if ((questState.getQuestItemsCount(5884) <= 65L && questState.getQuestItemsCount(5885) < 65L || questState.getQuestItemsCount(5884) < 65L && questState.getQuestItemsCount(5885) <= 65L) && questState.getQuestItemsCount(5886) == 0L) {
                        string = "seer_manakia_q0374_04.htm";
                    } else if (questState.getQuestItemsCount(5884) >= 65L && questState.getQuestItemsCount(5885) >= 65L && questState.getQuestItemsCount(5886) == 0L) {
                        string = "seer_manakia_q0374_05.htm";
                    } else if (n3 == 1 && questState.getQuestItemsCount(5886) > 0L) {
                        string = "seer_manakia_q0374_08.htm";
                        questState.setCond(2);
                        questState.playSound("ItemSound.quest_middle");
                    } else if (n3 == 2 && questState.getQuestItemsCount(5886) > 0L) {
                        string = "seer_manakia_q0374_09.htm";
                        questState.playSound("ItemSound.quest_middle");
                    }
                }
                if (n != 30557) break;
                if (n3 == 2 && questState.getQuestItemsCount(5886) > 0L) {
                    string = "torai_q0374_01.htm";
                }
                if (n3 != 3 || questState.getQuestItemsCount(5886) <= 0L) break;
                string = "torai_q0374_03.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (Rnd.chance(1) && questState.getState() == 2 && questState.getCond() != 3 && questState.getQuestItemsCount(5886) < 1L) {
            questState.giveItems(5886, 1L);
            questState.playSound("ItemSound.quest_middle");
        } else if (n == 20620 && questState.getState() == 2 && Rnd.chance(60) && questState.getQuestItemsCount(5884) < 65L) {
            questState.rollAndGive(5884, 1, 100.0);
        } else if (n == 20621 && questState.getState() == 2 && Rnd.chance(60) && questState.getQuestItemsCount(5885) < 65L) {
            questState.rollAndGive(5885, 1, 100.0);
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

