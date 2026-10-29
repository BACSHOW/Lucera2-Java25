/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _647_InfluxOfMachines
extends Quest
implements ScriptFile {
    private static final int bTI = 60;
    private static final int bTJ = 8100;
    private static final int[] bTK = new int[]{4963, 4964, 4965, 4966, 4967, 4968, 4969, 4970, 4971, 4972, 5000, 5001, 5002, 5003, 5004, 5005, 5006, 5007, 8298, 8306, 8310, 8312, 8322, 8324};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _647_InfluxOfMachines() {
        super(1);
        this.addStartNpc(32069);
        this.addTalkId(32069);
        int n = 22052;
        while (n < 22079) {
            this.addKillId(n++);
        }
        this.addQuestItem(8100);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            string2 = "collecter_gutenhagen_q0647_0103.htm";
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("647_3")) {
            if (questState.getQuestItemsCount(8100) >= 500L) {
                questState.takeItems(8100, -1L);
                questState.giveItems(bTK[Rnd.get(bTK.length)], 1L);
                string2 = "collecter_gutenhagen_q0647_0201.htm";
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            } else {
                string2 = "collecter_gutenhagen_q0647_0106.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getCond();
        long l = questState.getQuestItemsCount(8100);
        if (n == 0) {
            if (questState.getPlayer().getLevel() >= 46) {
                string = "collecter_gutenhagen_q0647_0101.htm";
            } else {
                string = "collecter_gutenhagen_q0647_0102.htm";
                questState.exitCurrentQuest(true);
            }
        } else if (n == 1 && l < 500L) {
            string = "collecter_gutenhagen_q0647_0106.htm";
        } else if (n == 2 && l >= 500L) {
            string = "collecter_gutenhagen_q0647_0105.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getCond() == 1 && questState.rollAndGive(8100, 1, 1, 500, 60.0 * npcInstance.getTemplate().rateHp)) {
            questState.setCond(2);
        }
        return null;
    }
}

