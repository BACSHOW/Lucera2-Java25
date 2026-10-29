/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _375_WhisperOfDreams2
extends Quest
implements ScriptFile {
    private static final int bvV = 30515;
    private static final int bwc = 5887;
    private static final int bwd = 5888;
    private static final int bwe = 5889;
    private static final int bwf = 20624;
    private static final int bwg = 20629;
    private static final int bwh = 5348;
    private static final int bwi = 5352;
    private static final int bwj = 5350;

    public _375_WhisperOfDreams2() {
        super(1);
        this.addStartNpc(30515);
        this.addKillId(20624, 20629);
        this.addQuestItem(5888, 5889);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = npcInstance.getNpcId();
        if (n == 30515) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("whisper_of_dreams_1", String.valueOf(1), true);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "seer_manakia_q0375_03.htm";
            }
            if (string.equalsIgnoreCase("menu_select?ask=375&reply=1")) {
                string2 = "seer_manakia_q0375_06.htm";
            } else if (string.equalsIgnoreCase("menu_select?ask=375&reply=2")) {
                questState.unset("whisper_of_dreams_1");
                string2 = "seer_manakia_q0375_07.htm";
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getState();
        int n3 = questState.getInt("whisper_of_dreams_1");
        switch (n2) {
            case 1: {
                if (n != 30515) break;
                if (questState.getPlayer().getLevel() >= 60 && questState.getQuestItemsCount(5887) > 0L) {
                    string = "seer_manakia_q0375_01.htm";
                    break;
                }
                questState.exitCurrentQuest(true);
                string = "seer_manakia_q0375_02.htm";
                break;
            }
            case 2: {
                if (n != 30515) break;
                if (n3 == 1 && (questState.getQuestItemsCount(5888) < 100L && questState.getQuestItemsCount(5889) <= 100L || questState.getQuestItemsCount(5888) <= 100L && questState.getQuestItemsCount(5889) < 100L)) {
                    string = "seer_manakia_q0375_04.htm";
                    break;
                }
                if (n3 != 1 || questState.getQuestItemsCount(5888) < 100L || questState.getQuestItemsCount(5889) < 100L) break;
                int n4 = Rnd.get(100);
                if (n4 < 34) {
                    questState.giveItems(5348, 1L);
                } else if (n4 < 67) {
                    questState.giveItems(5352, 1L);
                } else {
                    questState.giveItems(5350, 1L);
                }
                questState.takeItems(5888, -1L);
                questState.takeItems(5889, -1L);
                string = "seer_manakia_q0375_05.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (n == 20629 && questState.getQuestItemsCount(5888) < 100L) {
            questState.rollAndGive(5888, 1, 100.0);
        } else if (n == 20624 && questState.getQuestItemsCount(5889) < 100L) {
            questState.rollAndGive(5889, 1, 100.0);
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

