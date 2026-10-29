/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _264_KeenClaws
extends Quest
implements ScriptFile {
    private static final int bdz = 30136;
    private static final int bdA = 1367;
    private static final int bdB = 36;
    private static final int bdC = 43;
    private static final int bdD = 462;
    private static final int bdE = 1061;
    private static final int bdF = 48;
    private static final int bdG = 35;
    private static final int bdH = 20003;
    private static final int bdI = 20456;
    private static final int[][] DROPLIST_COND = new int[][]{{1, 2, 20003, 0, 1367, 50, 50, 2}, {1, 2, 20456, 0, 1367, 50, 50, 2}};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _264_KeenClaws() {
        super(0);
        this.addStartNpc(30136);
        this.addKillId(20003);
        this.addKillId(20456);
        this.addQuestItem(1367);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("paint_q0264_03.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        }
        return string2;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n != 30136) return string;
        if (n2 == 0) {
            if (questState.getPlayer().getLevel() >= 3) {
                return "paint_q0264_02.htm";
            }
            questState.exitCurrentQuest(true);
            return "paint_q0264_01.htm";
        }
        if (n2 == 1) {
            return "paint_q0264_04.htm";
        }
        if (n2 != 2) return string;
        questState.takeItems(1367, -1L);
        int n3 = Rnd.get(17);
        if (n3 == 0) {
            questState.giveItems(43, 1L);
            questState.playSound("ItemSound.quest_jackpot");
        } else if (n3 < 2) {
            questState.giveItems(57, 1000L);
        } else if (n3 < 5) {
            questState.giveItems(36, 1L);
        } else if (n3 < 8) {
            questState.giveItems(462, 1L);
            questState.giveItems(57, 50L);
        } else if (n3 < 11) {
            questState.giveItems(1061, 1L);
        } else if (n3 < 14) {
            questState.giveItems(48, 1L);
        } else {
            questState.giveItems(35, 1L);
        }
        string = "paint_q0264_05.htm";
        questState.playSound("ItemSound.quest_finish");
        questState.exitCurrentQuest(true);
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

