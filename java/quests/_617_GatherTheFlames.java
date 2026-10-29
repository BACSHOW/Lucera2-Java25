/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _617_GatherTheFlames
extends Quest
implements ScriptFile {
    private static final int bMm = 31539;
    private static final int bMn = 31271;
    private static final int bMo = 7264;
    private static final int[][] DROPLIST = new int[][]{{21376, 48}, {21377, 48}, {21378, 48}, {21652, 48}, {21380, 48}, {21381, 51}, {21653, 51}, {21383, 51}, {21394, 51}, {21385, 51}, {21386, 51}, {21388, 53}, {21655, 53}, {21387, 53}, {21390, 56}, {21656, 56}, {21395, 56}, {21389, 56}, {21391, 56}, {21392, 56}, {21393, 58}, {21657, 58}, {21382, 60}, {21379, 60}, {21654, 64}, {21384, 64}};
    public static final int[] Recipes = new int[]{6881, 6883, 6885, 6887, 7580, 6891, 6893, 6895, 6897, 6899};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _617_GatherTheFlames() {
        super(1);
        this.addStartNpc(31539);
        this.addStartNpc(31271);
        for (int[] nArray : DROPLIST) {
            this.addKillId(nArray[0]);
        }
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("warsmith_vulcan_q0617_03.htm")) {
            if (questState.getPlayer().getLevel() < 74) {
                return "warsmith_vulcan_q0617_02.htm";
            }
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            questState.setCond(1);
        } else if (string.equalsIgnoreCase("blacksmith_hilda_q0617_03.htm")) {
            if (questState.getPlayer().getLevel() < 74) {
                return "blacksmith_hilda_q0617_02.htm";
            }
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            questState.setCond(1);
        } else if (string.equalsIgnoreCase("warsmith_vulcan_q0617_08.htm")) {
            questState.playSound("ItemSound.quest_finish");
            questState.takeItems(7264, -1L);
            questState.exitCurrentQuest(true);
        } else if (string.equalsIgnoreCase("warsmith_vulcan_q0617_07.htm")) {
            if (questState.getQuestItemsCount(7264) < 1000L) {
                return "warsmith_vulcan_q0617_05.htm";
            }
            questState.takeItems(7264, 1000L);
            questState.giveItems(Recipes[Rnd.get(Recipes.length)], 1L);
            questState.playSound("ItemSound.quest_middle");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 31539) {
            if (n2 == 0) {
                if (questState.getPlayer().getLevel() < 74) {
                    string = "warsmith_vulcan_q0617_02.htm";
                    questState.exitCurrentQuest(true);
                } else {
                    string = "warsmith_vulcan_q0617_01.htm";
                }
            } else {
                string = questState.getQuestItemsCount(7264) < 1000L ? "warsmith_vulcan_q0617_05.htm" : "warsmith_vulcan_q0617_04.htm";
            }
        } else if (n == 31271) {
            string = n2 < 1 ? (questState.getPlayer().getLevel() < 74 ? "blacksmith_hilda_q0617_02.htm" : "blacksmith_hilda_q0617_01.htm") : "blacksmith_hilda_q0617_04.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        for (int[] nArray : DROPLIST) {
            if (npcInstance.getNpcId() != nArray[0]) continue;
            questState.rollAndGive(7264, 1, nArray[1]);
            return null;
        }
        return null;
    }
}

