/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _324_SweetestVenom
extends Quest
implements ScriptFile {
    private static int bfJ = 30351;
    private static int bfK = 20034;
    private static int bfL = 20038;
    private static int bfM = 20043;
    private static int bfN = 1077;
    private static int bfO = 60;

    public _324_SweetestVenom() {
        super(0);
        this.addStartNpc(bfJ);
        this.addKillId(bfK);
        this.addKillId(bfL);
        this.addKillId(bfM);
        this.addQuestItem(bfN);
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        if (npcInstance.getNpcId() != bfJ) {
            return string;
        }
        int n = questState.getState();
        if (n == 1) {
            if (questState.getPlayer().getLevel() >= 18) {
                string = "astaron_q0324_03.htm";
                questState.setCond(0);
            } else {
                string = "astaron_q0324_02.htm";
                questState.exitCurrentQuest(true);
            }
        } else if (n == 2) {
            long l = questState.getQuestItemsCount(bfN);
            if (l >= 10L) {
                string = "astaron_q0324_06.htm";
                questState.takeItems(bfN, -1L);
                questState.giveItems(57, 5810L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            } else {
                string = "astaron_q0324_05.htm";
            }
        }
        return string;
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        if (string.equalsIgnoreCase("astaron_q0324_04.htm") && questState.getState() == 1) {
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getState() != 2) {
            return null;
        }
        long l = questState.getQuestItemsCount(bfN);
        int n = bfO + (npcInstance.getNpcId() - bfK) / 4 * 12;
        if (l < 10L && Rnd.chance(n)) {
            questState.giveItems(bfN, 1L);
            if (l == 9L) {
                questState.setCond(2);
                questState.playSound("ItemSound.quest_middle");
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
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

