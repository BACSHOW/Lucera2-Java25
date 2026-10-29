/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.Config;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _649_ALooterandaRailroadMan
extends Quest
implements ScriptFile {
    private static final int OBI = 32052;
    private static final int bTT = 8099;
    private static final int[][] DROPLIST_COND = new int[][]{{1, 2, 22017, 0, 8099, 200, 50, 1}, {1, 2, 22018, 0, 8099, 200, 50, 1}, {1, 2, 22019, 0, 8099, 200, 50, 1}, {1, 2, 22021, 0, 8099, 200, 50, 1}, {1, 2, 22022, 0, 8099, 200, 50, 1}, {1, 2, 22023, 0, 8099, 200, 50, 1}, {1, 2, 22024, 0, 8099, 200, 50, 1}, {1, 2, 22026, 0, 8099, 200, 50, 1}};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _649_ALooterandaRailroadMan() {
        super(1);
        this.addStartNpc(32052);
        for (int i = 0; i < DROPLIST_COND.length; ++i) {
            this.addKillId(DROPLIST_COND[i][2]);
        }
        this.addQuestItem(8099);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            string2 = "railman_obi_q0649_0103.htm";
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("649_3")) {
            if (questState.getQuestItemsCount(8099) == 200L) {
                string2 = "railman_obi_q0649_0201.htm";
                questState.takeItems(8099, -1L);
                questState.giveItems(57, 21698L, Config.RATE_QUESTS_ADENA_REWARD < 1.0);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            } else {
                questState.setCond(1);
                string2 = "railman_obi_q0649_0202.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getState();
        int n3 = 0;
        if (n2 != 1) {
            n3 = questState.getCond();
        }
        if (n == 32052) {
            if (n3 == 0) {
                if (questState.getPlayer().getLevel() < 30) {
                    string = "railman_obi_q0649_0102.htm";
                    questState.exitCurrentQuest(true);
                } else {
                    string = "railman_obi_q0649_0101.htm";
                }
            } else if (n3 == 1) {
                string = "railman_obi_q0649_0106.htm";
            } else if (n3 == 2 && questState.getQuestItemsCount(8099) == 200L) {
                string = "railman_obi_q0649_0105.htm";
            } else {
                string = "railman_obi_q0649_0106.htm";
                questState.setCond(1);
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        for (int i = 0; i < DROPLIST_COND.length; ++i) {
            if (n2 != DROPLIST_COND[i][0] || n != DROPLIST_COND[i][2] || DROPLIST_COND[i][3] != 0 && questState.getQuestItemsCount(DROPLIST_COND[i][3]) <= 0L) continue;
            if (DROPLIST_COND[i][5] == 0) {
                questState.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][6]);
                continue;
            }
            if (!questState.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][7], DROPLIST_COND[i][5], DROPLIST_COND[i][6]) || DROPLIST_COND[i][1] == n2 || DROPLIST_COND[i][1] == 0) continue;
            questState.setCond(DROPLIST_COND[i][1]);
            questState.setState(2);
        }
        return null;
    }
}

