/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _124_MeetingTheElroki
extends Quest
implements ScriptFile {
    private static final int aGi = 32113;
    private static final int aGj = 32114;
    private static final int aGk = 32115;
    private static final int aGl = 32117;
    private static final int aGm = 32118;
    private static final int aGn = 8778;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _124_MeetingTheElroki() {
        super(0);
        this.addStartNpc(32113);
        this.addTalkId(32114, 32115, 32117, 32118);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getInt("encounter_with_crok");
        if (string.equals("quest_accept")) {
            questState.setCond(1);
            questState.set("encounter_with_crok", String.valueOf(0), true);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            string2 = "marquez_q0124_04.htm";
        }
        if (string.equals("reply_2") && n == 0) {
            questState.setCond(2);
            questState.set("encounter_with_crok", String.valueOf(1), true);
            questState.playSound("ItemSound.quest_middle");
            string2 = "marquez_q0124_06.htm";
        }
        if (string.equals("reply_2a") && n == 1) {
            questState.setCond(3);
            questState.set("encounter_with_crok", String.valueOf(2), true);
            questState.playSound("ItemSound.quest_middle");
            string2 = "mushika_q0124_03.htm";
        }
        if (string.equals("reply_6") && n == 2) {
            questState.setCond(4);
            questState.set("encounter_with_crok", String.valueOf(3), true);
            questState.playSound("ItemSound.quest_middle");
            string2 = "asama_q0124_06.htm";
        }
        if (string.equals("reply_9") && n == 3) {
            questState.setCond(5);
            questState.set("encounter_with_crok", String.valueOf(4), true);
            questState.playSound("ItemSound.quest_middle");
            string2 = "shaman_caracawe_q0124_05.htm";
        }
        if (string.equals("reply_10") && n == 4 && questState.getQuestItemsCount(8778) < 1L) {
            questState.setCond(6);
            questState.giveItems(8778, 1L);
            questState.playSound("ItemSound.quest_middle");
            string2 = "egg_of_mantarasa_q0124_02.htm";
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("encounter_with_crok");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 32113) break;
                if (questState.getPlayer().getLevel() >= 75) {
                    string = "marquez_q0124_01.htm";
                    break;
                }
                string = "marquez_q0124_02.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n2 == 32113) {
                    if (n == 0) {
                        string = "marquez_q0124_05.htm";
                        break;
                    }
                    if (n == 1) {
                        string = "marquez_q0124_07.htm";
                        break;
                    }
                    if (n < 2 || n > 5) break;
                    string = "marquez_q0124_08.htm";
                    break;
                }
                if (n2 == 32114) {
                    if (n == 1) {
                        string = "mushika_q0124_01.htm";
                        break;
                    }
                    if (n < 1) {
                        string = "mushika_q0124_02.htm";
                        break;
                    }
                    if (n < 2) break;
                    string = "mushika_q0124_04.htm";
                    break;
                }
                if (n2 == 32115) {
                    if (n == 2) {
                        string = "asama_q0124_01.htm";
                        break;
                    }
                    if (n < 2) {
                        string = "asama_q0124_02.htm";
                        break;
                    }
                    if (n == 3) {
                        string = "asama_q0124_07.htm";
                        break;
                    }
                    if (n == 4 && questState.getQuestItemsCount(8778) >= 1L) {
                        questState.giveItems(57, 71318L);
                        questState.takeItems(8778, -1L);
                        questState.playSound("ItemSound.quest_finish");
                        questState.unset("encounter_with_crok");
                        string = "asama_q0124_08.htm";
                        questState.exitCurrentQuest(false);
                        break;
                    }
                    if (n != 4 || questState.getQuestItemsCount(8778) >= 1L) break;
                    string = "asama_q0124_09.htm";
                    break;
                }
                if (n2 == 32117) {
                    if (n == 3) {
                        string = "shaman_caracawe_q0124_01.htm";
                        break;
                    }
                    if (n < 3) {
                        string = "shaman_caracawe_q0124_02.htm";
                        break;
                    }
                    if (n == 4 && questState.getQuestItemsCount(8778) < 1L) {
                        string = "shaman_caracawe_q0124_06.htm";
                        break;
                    }
                    if (n != 4 || questState.getQuestItemsCount(8778) < 1L) break;
                    string = "shaman_caracawe_q0124_07.htm";
                    break;
                }
                if (n2 != 32118) break;
                if (n == 4 && questState.getQuestItemsCount(8778) < 1L) {
                    string = "egg_of_mantarasa_q0124_01.htm";
                    break;
                }
                if (n == 4 && questState.getQuestItemsCount(8778) >= 1L) {
                    string = "egg_of_mantarasa_q0124_03.htm";
                    break;
                }
                if (n >= 4) break;
                string = "egg_of_mantarasa_q0124_04.htm";
            }
        }
        return string;
    }
}

