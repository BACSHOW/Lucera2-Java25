/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _272_WrathOfAncestors
extends Quest
implements ScriptFile {
    private static final int aEK = 30572;
    private static final int beb = 1474;
    private static final int bec = 20319;
    private static final int bed = 20320;
    private static final int[][] DROPLIST_COND = new int[][]{{1, 2, 20319, 0, 1474, 50, 100, 1}, {1, 2, 20320, 0, 1474, 50, 100, 1}};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _272_WrathOfAncestors() {
        super(0);
        this.addStartNpc(30572);
        for (int i = 0; i < DROPLIST_COND.length; ++i) {
            this.addKillId(DROPLIST_COND[i][2]);
        }
        this.addQuestItem(1474);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("1")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            string2 = "seer_livina_q0272_03.htm";
        }
        return string2;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getCond();
        if (n != 30572) return string;
        if (n2 == 0) {
            if (questState.getPlayer().getRace() != Race.orc) {
                string = "seer_livina_q0272_00.htm";
                questState.exitCurrentQuest(true);
                return string;
            } else {
                if (questState.getPlayer().getLevel() >= 5) return "seer_livina_q0272_02.htm";
                string = "seer_livina_q0272_01.htm";
                questState.exitCurrentQuest(true);
            }
            return string;
        } else {
            if (n2 == 1) {
                return "seer_livina_q0272_04.htm";
            }
            if (n2 != 2) return string;
            questState.takeItems(1474, -1L);
            questState.giveItems(57, 1500L);
            string = "seer_livina_q0272_05.htm";
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
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

