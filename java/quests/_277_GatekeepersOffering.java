/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _277_GatekeepersOffering
extends Quest
implements ScriptFile {
    private static final int bev = 1572;
    private static final int bew = 1658;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _277_GatekeepersOffering() {
        super(0);
        this.addStartNpc(30576);
        this.addKillId(20333);
        this.addQuestItem(1572);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("1")) {
            if (questState.getPlayer().getLevel() >= 15) {
                string2 = "gatekeeper_tamil_q0277_03.htm";
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
            } else {
                string2 = "gatekeeper_tamil_q0277_01.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 30576 && n2 == 0) {
            string = "gatekeeper_tamil_q0277_02.htm";
        } else if (n == 30576 && n2 == 1 && questState.getQuestItemsCount(1572) < 20L) {
            string = "gatekeeper_tamil_q0277_04.htm";
        } else if (n == 30576 && n2 == 2 && questState.getQuestItemsCount(1572) < 20L) {
            string = "gatekeeper_tamil_q0277_04.htm";
        } else if (n == 30576 && n2 == 2 && questState.getQuestItemsCount(1572) >= 20L) {
            string = "gatekeeper_tamil_q0277_05.htm";
            questState.takeItems(1572, -1L);
            questState.giveItems(1658, 2L);
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        questState.rollAndGive(1572, 1, 1, 20, 33.0);
        if (questState.getQuestItemsCount(1572) >= 20L) {
            questState.setCond(2);
        }
        return null;
    }
}

