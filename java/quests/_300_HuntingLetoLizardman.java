/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _300_HuntingLetoLizardman
extends Quest
implements ScriptFile {
    private static int bfj = 30126;
    private static int bfk = 7139;
    private static int bfl = 1872;
    private static int bfm = 1867;
    private static int bfn = 70;

    public _300_HuntingLetoLizardman() {
        super(0);
        this.addStartNpc(bfj);
        int n = 20577;
        while (n <= 20582) {
            this.addKillId(n++);
        }
        this.addQuestItem(bfk);
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        if (npcInstance.getNpcId() != bfj) {
            return string;
        }
        if (questState.getState() == 1) {
            if (questState.getPlayer().getLevel() < 34) {
                string = "rarshints_q0300_0103.htm";
                questState.exitCurrentQuest(true);
            } else {
                string = "rarshints_q0300_0101.htm";
                questState.setCond(0);
            }
        } else if (questState.getQuestItemsCount(bfk) < 60L) {
            string = "rarshints_q0300_0106.htm";
            questState.setCond(1);
        } else {
            string = "rarshints_q0300_0105.htm";
        }
        return string;
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getState();
        if (string.equalsIgnoreCase("rarshints_q0300_0104.htm") && n == 1) {
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("rarshints_q0300_0201.htm") && n == 2) {
            if (questState.getQuestItemsCount(bfk) < 60L) {
                string2 = "rarshints_q0300_0202.htm";
                questState.setCond(1);
            } else {
                questState.takeItems(bfk, -1L);
                switch (Rnd.get(3)) {
                    case 0: {
                        questState.giveItems(57, 30000L, true);
                        break;
                    }
                    case 1: {
                        questState.giveItems(bfl, 50L, true);
                        break;
                    }
                    case 2: {
                        questState.giveItems(bfm, 50L, true);
                    }
                }
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            }
        }
        return string2;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getState() != 2) {
            return null;
        }
        long l = questState.getQuestItemsCount(bfk);
        if (l < 60L && Rnd.chance(bfn)) {
            questState.giveItems(bfk, 1L);
            if (l == 59L) {
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

