/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _636_TruthBeyond
extends Quest
implements ScriptFile {
    private static final int bQB = 31329;
    private static final int bQC = 32010;
    private static final int bQD = 8067;
    private static final int bQE = 8064;
    private static final int bQF = 8065;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _636_TruthBeyond() {
        super(0);
        this.addStartNpc(31329);
        this.addTalkId(32010);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = npcInstance.getNpcId();
        if (n == 31329) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("truth_behind_door", String.valueOf(1), true);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "priest_eliyah_q0636_05.htm";
            } else if (string.equalsIgnoreCase("reply_1")) {
                string2 = "priest_eliyah_q0636_04.htm";
            }
        } else if (n == 32010 && string.equalsIgnoreCase("reply_1")) {
            questState.giveItems(8064, 1L);
            questState.unset("truth_behind_door");
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
            string2 = "falsepriest_flauron_q0636_02.htm";
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("truth_behind_door");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 31329) break;
                if (questState.getPlayer().getLevel() >= 73 && questState.getQuestItemsCount(8064) == 0L && questState.getQuestItemsCount(8065) == 0L && questState.getQuestItemsCount(8067) == 0L) {
                    string = "priest_eliyah_q0636_01.htm";
                    break;
                }
                if (questState.getPlayer().getLevel() >= 73 && (questState.getQuestItemsCount(8064) >= 1L || questState.getQuestItemsCount(8065) >= 1L || questState.getQuestItemsCount(8067) >= 1L)) {
                    string = "priest_eliyah_q0636_02.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                if (questState.getPlayer().getLevel() >= 73) break;
                string = "priest_eliyah_q0636_03.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n2 == 31329) {
                    if (n != 1) break;
                    string = "priest_eliyah_q0636_06.htm";
                    break;
                }
                if (n2 != 32010 || n != 1) break;
                string = "falsepriest_flauron_q0636_01.htm";
            }
        }
        return string;
    }
}

