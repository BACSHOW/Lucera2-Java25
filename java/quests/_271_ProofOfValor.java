/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _271_ProofOfValor
extends Quest
implements ScriptFile {
    private static final int bdX = 30577;
    private static final int bdY = 1473;
    private static final int bdZ = 1507;
    private static final int bea = 1506;
    private static final int[][] DROPLIST_COND = new int[][]{{1, 2, 20475, 0, 1473, 50, 25, 2}};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _271_ProofOfValor() {
        super(0);
        this.addStartNpc(30577);
        this.addTalkId(30577);
        for (int i = 0; i < DROPLIST_COND.length; ++i) {
            this.addKillId(DROPLIST_COND[i][2]);
        }
        this.addQuestItem(1473);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("praetorian_rukain_q0271_03.htm")) {
            questState.playSound("ItemSound.quest_accept");
            if (questState.getQuestItemsCount(1506) > 0L || questState.getQuestItemsCount(1507) > 0L) {
                string2 = "praetorian_rukain_q0271_07.htm";
            }
            questState.setCond(1);
            questState.setState(2);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getCond();
        if (n == 30577) {
            if (n2 == 0) {
                if (questState.getPlayer().getRace() != Race.orc) {
                    string = "praetorian_rukain_q0271_00.htm";
                    questState.exitCurrentQuest(true);
                } else if (questState.getPlayer().getLevel() < 4) {
                    string = "praetorian_rukain_q0271_01.htm";
                    questState.exitCurrentQuest(true);
                } else if (questState.getQuestItemsCount(1506) > 0L || questState.getQuestItemsCount(1507) > 0L) {
                    string = "praetorian_rukain_q0271_06.htm";
                    questState.exitCurrentQuest(true);
                } else {
                    string = "praetorian_rukain_q0271_02.htm";
                }
            } else if (n2 == 1) {
                string = "praetorian_rukain_q0271_04.htm";
            } else if (n2 == 2 && questState.getQuestItemsCount(1473) == 50L) {
                questState.takeItems(1473, -1L);
                if (Rnd.chance(14)) {
                    questState.takeItems(1507, -1L);
                    questState.giveItems(1507, 1L);
                } else {
                    questState.takeItems(1506, -1L);
                    questState.giveItems(1506, 1L);
                }
                string = "praetorian_rukain_q0271_05.htm";
                questState.exitCurrentQuest(true);
            } else if (n2 == 2 && questState.getQuestItemsCount(1473) < 50L) {
                string = "praetorian_rukain_q0271_04.htm";
                questState.setCond(1);
                questState.setState(2);
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

