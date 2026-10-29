/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _634_InSearchofDimensionalFragments
extends Quest
implements ScriptFile {
    int DIMENSION_FRAGMENT_ID = 7079;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _634_InSearchofDimensionalFragments() {
        super(1);
        int n;
        for (n = 31494; n < 31508; ++n) {
            this.addTalkId(n);
            this.addStartNpc(n);
        }
        n = 21208;
        while (n < 21256) {
            this.addKillId(n++);
        }
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            string2 = "dimension_keeper_1_q0634_03.htm";
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            questState.setCond(1);
        } else if (string.equalsIgnoreCase("634_2")) {
            string2 = "dimension_keeper_1_q0634_06.htm";
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getState();
        if (n == 1) {
            if (questState.getPlayer().getLevel() > 20) {
                string = "dimension_keeper_1_q0634_01.htm";
            } else {
                string = "dimension_keeper_1_q0634_02.htm";
                questState.exitCurrentQuest(true);
            }
        } else if (n == 2) {
            string = "dimension_keeper_1_q0634_04.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = (int)(1.6 + (double)((float)npcInstance.getLevel() * 0.15f));
        questState.rollAndGive(this.DIMENSION_FRAGMENT_ID, n, 90.0);
        return null;
    }
}

