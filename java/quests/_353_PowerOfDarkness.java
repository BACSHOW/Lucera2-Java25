/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _353_PowerOfDarkness
extends Quest
implements ScriptFile {
    private static int bqA = 31044;
    private static int bqB = 20283;
    private static int bqC = 20284;
    private static int bqD = 20244;
    private static int bqE = 20245;
    private static int bqF = 5862;
    private static int bqG = 57;
    private static int bqH = 50;

    public _353_PowerOfDarkness() {
        super(0);
        this.addStartNpc(bqA);
        this.addKillId(bqB);
        this.addKillId(bqC);
        this.addKillId(bqD);
        this.addKillId(bqE);
        this.addQuestItem(bqF);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        int n = questState.getState();
        if (string.equalsIgnoreCase("31044-04.htm") && n == 1) {
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("31044-08.htm") && n == 2) {
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        }
        return string;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        if (npcInstance.getNpcId() != bqA) {
            return string;
        }
        if (questState.getState() == 1) {
            if (questState.getPlayer().getLevel() >= 55) {
                string = "31044-02.htm";
                questState.setCond(0);
            } else {
                string = "31044-01.htm";
                questState.exitCurrentQuest(true);
            }
        } else {
            long l = questState.getQuestItemsCount(bqF);
            if (l > 0L) {
                string = "31044-06.htm";
                questState.takeItems(bqF, -1L);
                questState.giveItems(bqG, 2500L + 230L * l);
                questState.playSound("ItemSound.quest_middle");
            } else {
                string = "31044-05.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getState() != 2) {
            return null;
        }
        if (Rnd.chance(bqH)) {
            questState.giveItems(bqF, 1L);
            questState.playSound("ItemSound.quest_itemget");
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

