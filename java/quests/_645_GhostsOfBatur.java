/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _645_GhostsOfBatur
extends Quest
implements ScriptFile {
    private static final int aEL = 32017;
    private static final int bTy = 8089;
    private static final int[][] REWARDS = new int[][]{{1878, 18}, {1879, 7}, {1880, 4}, {1881, 6}, {1882, 10}, {1883, 2}};
    private static final int[] MOBS = new int[]{22007, 22009, 22010, 22011, 22012, 22013, 22014, 22015, 22016};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _645_GhostsOfBatur() {
        super(0);
        this.addStartNpc(32017);
        this.addTalkId(32017);
        for (int n : MOBS) {
            this.addKillId(n);
        }
        this.addQuestItem(8089);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("32017-03.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (questState.getCond() == 2) {
            if (questState.getQuestItemsCount(8089) >= 180L) {
                for (int i = 0; i < REWARDS.length; ++i) {
                    if (!string.equalsIgnoreCase(String.valueOf(REWARDS[i][0]))) continue;
                    questState.takeItems(8089, -1L);
                    questState.giveItems(REWARDS[i][0], REWARDS[i][1], true);
                    string2 = "32017-07.htm";
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(true);
                }
            } else {
                string2 = "32017-04.htm";
                questState.setCond(1);
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getCond();
        if (n == 0) {
            if (questState.getPlayer().getLevel() < 21 || questState.getPlayer().getLevel() > 32) {
                string = "32017-02.htm";
                questState.exitCurrentQuest(true);
            } else {
                string = "32017-01.htm";
            }
        } else if (n == 1) {
            string = "32017-04.htm";
        } else if (n == 2) {
            string = questState.getQuestItemsCount(8089) >= 180L ? "32017-05.htm" : "32017-01.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getCond() == 1 && questState.getQuestItemsCount(8089) < 180L && questState.rollAndGive(8089, 1, 2, 180, 70.0)) {
            questState.setCond(2);
            questState.setState(2);
        }
        return null;
    }
}

