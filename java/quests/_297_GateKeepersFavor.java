/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _297_GateKeepersFavor
extends Quest
implements ScriptFile {
    private static final int beZ = 1573;
    private static final int bfa = 1659;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _297_GateKeepersFavor() {
        super(0);
        this.addStartNpc(30540);
        this.addTalkId(30540);
        this.addKillId(20521);
        this.addQuestItem(1573);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("gatekeeper_wirphy_q0297_03.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 30540) {
            if (n2 == 0) {
                string = questState.getPlayer().getLevel() >= 15 ? "gatekeeper_wirphy_q0297_02.htm" : "gatekeeper_wirphy_q0297_01.htm";
            } else if (n2 == 1 && questState.getQuestItemsCount(1573) < 20L) {
                string = "gatekeeper_wirphy_q0297_04.htm";
            } else if (n2 == 2 && questState.getQuestItemsCount(1573) < 20L) {
                string = "gatekeeper_wirphy_q0297_04.htm";
            } else if (n2 == 2 && questState.getQuestItemsCount(1573) >= 20L) {
                string = "gatekeeper_wirphy_q0297_05.htm";
                questState.takeItems(1573, -1L);
                questState.giveItems(1659, 2L);
                questState.exitCurrentQuest(true);
                questState.playSound("ItemSound.quest_finish");
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        questState.rollAndGive(1573, 1, 1, 20, 33.0);
        if (questState.getQuestItemsCount(1573) >= 20L) {
            questState.setCond(2);
        }
        return null;
    }
}

