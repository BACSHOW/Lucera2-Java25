/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.Player;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _161_FruitsOfMothertree
extends Quest
implements ScriptFile {
    private static final int aHA = 1036;
    private static final int aHB = 1037;
    private static final int aHC = 30362;
    private static final int aHD = 30371;

    public _161_FruitsOfMothertree() {
        super(0);
        this.addStartNpc(30362);
        this.addTalkId(30371);
        this.addQuestItem(1037, 1036);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            string2 = "andellria_q0312_04.htm";
            questState.giveItems(1036, 1L);
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        Player player = questState.getPlayer();
        int n = npcInstance.getNpcId();
        int n2 = questState.getState();
        switch (n2) {
            case 1: {
                if (player.getRace() != Race.elf) {
                    string = "andellria_q0312_00.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                if (player.getLevel() >= 3) {
                    string = "andellria_q0312_03.htm";
                    break;
                }
                string = "andellria_q0312_02.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n == 30362) {
                    if (questState.getQuestItemsCount(1036) == 1L && questState.getQuestItemsCount(1037) == 0L) {
                        string = "andellria_q0312_05.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(1037) != 1L) break;
                    string = "andellria_q0312_06.htm";
                    questState.giveItems(57, 1000L);
                    questState.addExpAndSp(1000L, 0L);
                    questState.takeItems(1037, 1L);
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(false);
                    break;
                }
                if (n != 30371) break;
                if (questState.getQuestItemsCount(1036) == 1L) {
                    string = "thalya_q0312_01.htm";
                    questState.giveItems(1037, 1L);
                    questState.takeItems(1036, 1L);
                    questState.setCond(2);
                    questState.playSound("ItemSound.quest_middle");
                    break;
                }
                if (questState.getQuestItemsCount(1037) != 1L) break;
                string = "thalya_q0312_02.htm";
            }
        }
        return string;
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

