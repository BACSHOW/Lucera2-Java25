/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _651_RunawayYouth
extends Quest
implements ScriptFile {
    private static int bTY = 32014;
    private static int bTZ = 31989;
    protected NpcInstance _npc;
    private static int SOE = 736;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _651_RunawayYouth() {
        super(0);
        this.addStartNpc(bTY);
        this.addTalkId(bTZ);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("runaway_boy_ivan_q0651_03.htm")) {
            if (questState.getQuestItemsCount(SOE) > 0L) {
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                questState.takeItems(SOE, 1L);
                string2 = "runaway_boy_ivan_q0651_04.htm";
                questState.startQuestTimer("ivan_timer", 20000L);
            }
        } else if (string.equalsIgnoreCase("runaway_boy_ivan_q0651_05.htm")) {
            questState.exitCurrentQuest(true);
            questState.playSound("ItemSound.quest_giveup");
        } else if (string.equalsIgnoreCase("ivan_timer")) {
            this._npc.deleteMe();
            string2 = null;
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == bTY && n2 == 0) {
            if (questState.getPlayer().getLevel() >= 26) {
                string = "runaway_boy_ivan_q0651_01.htm";
            } else {
                string = "runaway_boy_ivan_q0651_01a.htm";
                questState.exitCurrentQuest(true);
            }
        } else if (n == bTZ && n2 == 1) {
            string = "fisher_batidae_q0651_01.htm";
            questState.giveItems(57, Math.round(2883.0 * questState.getRateQuestsAdenaReward()));
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        }
        return string;
    }
}

