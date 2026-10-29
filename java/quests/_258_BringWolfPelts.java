/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _258_BringWolfPelts
extends Quest
implements ScriptFile {
    int WOLF_PELT = 702;
    int Cotton_Shirt = 390;
    int Leather_Pants = 29;
    int Leather_Shirt = 22;
    int Short_Leather_Gloves = 1119;
    int Tunic = 426;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _258_BringWolfPelts() {
        super(0);
        this.addStartNpc(30001);
        this.addKillId(20120);
        this.addKillId(20442);
        this.addQuestItem(this.WOLF_PELT);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.intern().equalsIgnoreCase("lector_q0258_03.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getCond();
        if (n == 0) {
            if (questState.getPlayer().getLevel() >= 3) {
                string = "lector_q0258_02.htm";
                return string;
            }
            string = "lector_q0258_01.htm";
            questState.exitCurrentQuest(true);
        } else if (n == 1 && questState.getQuestItemsCount(this.WOLF_PELT) >= 0L && questState.getQuestItemsCount(this.WOLF_PELT) < 40L) {
            string = "lector_q0258_05.htm";
        } else if (n == 2 && questState.getQuestItemsCount(this.WOLF_PELT) >= 40L) {
            questState.takeItems(this.WOLF_PELT, 40L);
            int n2 = Rnd.get(16);
            if (n2 == 0) {
                questState.giveItems(this.Cotton_Shirt, 1L);
                questState.playSound("ItemSound.quest_jackpot");
            } else if (n2 < 6) {
                questState.giveItems(this.Leather_Pants, 1L);
            } else if (n2 < 9) {
                questState.giveItems(this.Leather_Shirt, 1L);
            } else if (n2 < 13) {
                questState.giveItems(this.Short_Leather_Gloves, 1L);
            } else {
                questState.giveItems(this.Tunic, 1L);
            }
            string = "lector_q0258_06.htm";
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        long l = questState.getQuestItemsCount(this.WOLF_PELT);
        if (l < 40L && questState.getCond() == 1) {
            questState.giveItems(this.WOLF_PELT, 1L);
            if (l == 39L) {
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(2);
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

