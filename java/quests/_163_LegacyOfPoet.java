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

public class _163_LegacyOfPoet
extends Quest
implements ScriptFile {
    private static final int aHN = 30220;
    private static final int aHO = 20372;
    private static final int aHP = 20373;
    private static final int aHQ = 1038;
    private static final int aHR = 1039;
    private static final int aHS = 1040;
    private static final int aHT = 1041;

    public _163_LegacyOfPoet() {
        super(0);
        this.addStartNpc(30220);
        this.addKillId(20372, 20373);
        this.addQuestItem(1038, 1039, 1040, 1041);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            string2 = "sentinel_stardyen_q0315_07.htm";
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
                if (player.getRace() != Race.elf && player.getRace() != Race.orc && player.getRace() != Race.dwarf && player.getRace() != Race.human) {
                    string = "sentinel_stardyen_q0315_00.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                if (player.getLevel() >= 11) {
                    string = "sentinel_stardyen_q0315_03.htm";
                    break;
                }
                string = "sentinel_stardyen_q0315_02.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (questState.getQuestItemsCount(1038) == 1L && questState.getQuestItemsCount(1039) == 1L && questState.getQuestItemsCount(1040) == 1L && questState.getQuestItemsCount(1041) == 1L) {
                    string = "sentinel_stardyen_q0315_09.htm";
                    questState.giveItems(57, 13890L);
                    questState.takeItems(1038, 1L);
                    questState.takeItems(1039, 1L);
                    questState.takeItems(1040, 1L);
                    questState.takeItems(1041, 1L);
                    questState.exitCurrentQuest(false);
                    questState.playSound("ItemSound.quest_finish");
                    break;
                }
                string = "sentinel_stardyen_q0315_08.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        boolean bl;
        int n = npcInstance.getNpcId();
        boolean bl2 = bl = questState.getQuestItemsCount(1038) + questState.getQuestItemsCount(1039) + questState.getQuestItemsCount(1040) + questState.getQuestItemsCount(1041) >= 3L;
        if (n == 20372 || n == 20373 && questState.getCond() == 1) {
            if (Rnd.get(10) == 0 && questState.getQuestItemsCount(1038) == 0L) {
                questState.rollAndGive(1038, 1, 100.0);
                if (bl) {
                    questState.playSound("ItemSound.quest_middle");
                    questState.setCond(2);
                }
            }
            if (Rnd.get(10) > 7 && questState.getQuestItemsCount(1039) == 0L) {
                questState.rollAndGive(1039, 1, 100.0);
                if (bl) {
                    questState.playSound("ItemSound.quest_middle");
                    questState.setCond(2);
                }
            }
            if (Rnd.get(10) > 7 && questState.getQuestItemsCount(1040) == 0L) {
                questState.rollAndGive(1040, 1, 100.0);
                if (bl) {
                    questState.playSound("ItemSound.quest_middle");
                    questState.setCond(2);
                }
            }
            if (Rnd.get(10) > 5 && questState.getQuestItemsCount(1041) == 0L) {
                questState.rollAndGive(1041, 1, 100.0);
                if (bl) {
                    questState.playSound("ItemSound.quest_middle");
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

