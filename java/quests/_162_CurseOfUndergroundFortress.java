/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _162_CurseOfUndergroundFortress
extends Quest
implements ScriptFile {
    private static final int aHx = 30147;
    private static final int aHE = 20033;
    private static final int aHF = 20345;
    private static final int aHG = 20371;
    private static final int aHH = 20463;
    private static final int aHI = 20464;
    private static final int aHJ = 20504;
    private static final int aHK = 1158;
    private static final int aHL = 1159;
    private static final int aHM = 625;

    public _162_CurseOfUndergroundFortress() {
        super(0);
        this.addStartNpc(30147);
        this.addKillId(20033, 20345, 20371, 20463, 20464, 20504);
        this.addQuestItem(1159, 1158);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            string2 = "uno_q0314_04.htm";
        } else if (string.equalsIgnoreCase("reply_1")) {
            string2 = "uno_q0314_03.htm";
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getState();
        Player player = questState.getPlayer();
        switch (n) {
            case 1: {
                if (player.getRace() == Race.darkelf) {
                    string = "uno_q0314_00.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                if (player.getLevel() >= 12) {
                    string = "uno_q0314_02.htm";
                    break;
                }
                string = "uno_q0314_01.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (questState.getQuestItemsCount(1159) + questState.getQuestItemsCount(1158) < 13L) {
                    string = "uno_q0314_05.htm";
                    break;
                }
                if (questState.getQuestItemsCount(1159) + questState.getQuestItemsCount(1158) < 13L) break;
                string = "uno_q0314_06.htm";
                questState.giveItems(625, 1L);
                questState.giveItems(57, 24000L);
                questState.takeItems(1159, -1L);
                questState.takeItems(1158, -1L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(false);
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (n == 20033 && Rnd.get(100) < 25 && questState.getQuestItemsCount(1159) < 3L && questState.getCond() == 1) {
            questState.rollAndGive(1159, 1, 100.0);
            if (questState.getQuestItemsCount(1159) >= 2L) {
                questState.playSound("ItemSound.quest_middle");
                if (questState.getQuestItemsCount(1158) >= 10L) {
                    questState.setCond(2);
                }
            }
        } else if (n == 20345 && Rnd.get(100) < 26 && questState.getQuestItemsCount(1159) < 3L && questState.getCond() == 1) {
            questState.rollAndGive(1159, 1, 100.0);
            if (questState.getQuestItemsCount(1159) >= 2L) {
                questState.playSound("ItemSound.quest_middle");
                if (questState.getQuestItemsCount(1158) >= 10L) {
                    questState.setCond(2);
                }
            }
        } else if (n == 20371 && Rnd.get(100) < 23 && questState.getQuestItemsCount(1159) < 3L && questState.getCond() == 1) {
            questState.rollAndGive(1159, 1, 100.0);
            if (questState.getQuestItemsCount(1159) >= 2L) {
                questState.playSound("ItemSound.quest_middle");
                if (questState.getQuestItemsCount(1158) >= 10L) {
                    questState.setCond(2);
                }
            }
        } else if (n == 20463 && Rnd.get(4) == 1 && questState.getCond() == 1) {
            questState.rollAndGive(1158, 1, 100.0);
            if (questState.getQuestItemsCount(1158) >= 10L) {
                questState.playSound("ItemSound.quest_middle");
                if (questState.getQuestItemsCount(1159) >= 3L) {
                    questState.setCond(2);
                }
            }
        } else if (n == 20464 && Rnd.get(100) < 23 && questState.getCond() == 1) {
            questState.rollAndGive(1158, 1, 100.0);
            if (questState.getQuestItemsCount(1158) >= 10L) {
                questState.playSound("ItemSound.quest_middle");
                if (questState.getQuestItemsCount(1159) >= 10L) {
                    questState.setCond(2);
                }
            }
        } else if (n == 20504 && Rnd.get(100) < 26 && questState.getCond() == 1) {
            questState.rollAndGive(1158, 1, 100.0);
            if (questState.getQuestItemsCount(1158) >= 10L) {
                questState.playSound("ItemSound.quest_middle");
                if (questState.getQuestItemsCount(1159) >= 3L) {
                    questState.setCond(2);
                }
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

