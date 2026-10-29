/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.Player;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _167_DwarvenKinship
extends Quest
implements ScriptFile {
    private static final int aIk = 30350;
    private static final int aIl = 30255;
    private static final int aIm = 30210;
    private static final int aIn = 1076;
    private static final int aIo = 1106;

    public _167_DwarvenKinship() {
        super(0);
        this.addStartNpc(30350);
        this.addTalkId(30255, 30210);
        this.addQuestItem(1076, 1106);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = npcInstance.getNpcId();
        if (n == 30350) {
            if (string.equalsIgnoreCase("quest_accept")) {
                string2 = "calculain_q0323_04.htm";
                questState.giveItems(1076, 1L);
                questState.playSound("ItemSound.quest_accept");
                questState.setCond(1);
                questState.setState(2);
            }
        } else if (n == 30255) {
            if (string.equalsIgnoreCase("reply_1") && questState.getCond() == 1) {
                string2 = "harprock_q0323_03.htm";
                questState.takeItems(1076, 1L);
                questState.giveItems(1106, 1L);
                questState.giveItems(57, 2000L);
                questState.setCond(2);
            } else if (string.equalsIgnoreCase("reply_2") && questState.getQuestItemsCount(1076) == 1L) {
                questState.takeItems(1076, 1L);
                string2 = "harprock_q0323_04.htm";
                questState.giveItems(57, 15000L);
                questState.exitCurrentQuest(false);
                questState.playSound("ItemSound.quest_finish");
            }
        } else if (n == 30210 && string.equalsIgnoreCase("reply_1") && questState.getQuestItemsCount(1106) == 1L) {
            string2 = "warehouse_keeper_norman_q0323_02.htm";
            questState.takeItems(1106, 1L);
            questState.giveItems(57, 20000L);
            questState.exitCurrentQuest(false);
            questState.playSound("ItemSound.quest_finish");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getState();
        Player player = questState.getPlayer();
        int n2 = npcInstance.getNpcId();
        switch (n) {
            case 1: {
                if (n2 == 30350 && player.getLevel() >= 15) {
                    string = "calculain_q0323_03.htm";
                    break;
                }
                string = "calculain_q0323_02.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n2 == 30350) {
                    if (questState.getQuestItemsCount(1076) != 1L) break;
                    string = "calculain_q0323_05.htm";
                    break;
                }
                if (n2 == 30255) {
                    if (questState.getQuestItemsCount(1076) == 1L) {
                        string = "harprock_q0323_01.htm";
                    }
                    if (questState.getQuestItemsCount(1106) != 1L) break;
                    string = "harprock_q0323_05.htm";
                    break;
                }
                if (n2 != 30210 || questState.getQuestItemsCount(1106) != 1L) break;
                string = "warehouse_keeper_norman_q0323_01.htm";
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

