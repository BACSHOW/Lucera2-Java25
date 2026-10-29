/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _121_PavelTheGiants
extends Quest
implements ScriptFile {
    private static int aGc = 31961;
    private static int aGd = 32041;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _121_PavelTheGiants() {
        super(0);
        this.addStartNpc(aGc);
        this.addTalkId(aGc, aGd);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        if (string.equals("collecter_yumi_q0121_0201.htm")) {
            questState.playSound("ItemSound.quest_finish");
            questState.addExpAndSp(10000L, 0L);
            questState.exitCurrentQuest(false);
        }
        return string;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getState();
        int n3 = questState.getCond();
        if (n2 == 1 && n == aGc) {
            if (questState.getPlayer().getLevel() >= 46) {
                string = "head_blacksmith_newyear_q0121_0101.htm";
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
            } else {
                string = "head_blacksmith_newyear_q0121_0103.htm";
                questState.exitCurrentQuest(false);
            }
        } else if (n2 == 2) {
            string = n == aGd && n3 == 1 ? "collecter_yumi_q0121_0101.htm" : "head_blacksmith_newyear_q0121_0105.htm";
        }
        return string;
    }
}

