/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.base.Experience;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _385_YokeOfThePast
extends Quest
implements ScriptFile {
    final int ANCIENT_SCROLL = 5902;
    final int BLANK_SCROLL = 5965;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _385_YokeOfThePast() {
        super(1);
        int n;
        for (n = 31095; n <= 31126; ++n) {
            if (n == 31111 || n == 31112 || n == 31113) continue;
            this.addStartNpc(n);
        }
        n = 21208;
        while (n < 21256) {
            this.addKillId(n++);
        }
        this.addQuestItem(5902);
    }

    public boolean checkNPC(int n) {
        return n >= 31095 && n <= 31126 && n != 31100 && n != 31111 && n != 31112 && n != 31113;
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("enter_necropolis1_q0385_05.htm")) {
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            questState.setCond(1);
        } else if (string.equalsIgnoreCase("enter_necropolis1_q0385_09.htm")) {
            string2 = "enter_necropolis1_q0385_10.htm";
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        }
        return string2;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        double d = 60.0 * Experience.penaltyModifier(questState.calculateLevelDiffForDrop(npcInstance.getLevel(), questState.getPlayer().getLevel()), 9.0) * npcInstance.getTemplate().rateHp / 4.0;
        questState.rollAndGive(5902, 1, d);
        return null;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        if (this.checkNPC(n) && questState.getCond() == 0) {
            if (questState.getPlayer().getLevel() < 20) {
                string = "enter_necropolis1_q0385_02.htm";
                questState.exitCurrentQuest(true);
            } else {
                string = "enter_necropolis1_q0385_01.htm";
            }
        } else if (questState.getCond() == 1 && questState.getQuestItemsCount(5902) == 0L) {
            string = "enter_necropolis1_q0385_11.htm";
        } else if (questState.getCond() == 1 && questState.getQuestItemsCount(5902) > 0L) {
            string = "enter_necropolis1_q0385_09.htm";
            questState.giveItems(5965, questState.getQuestItemsCount(5902));
            questState.takeItems(5902, -1L);
        } else {
            questState.exitCurrentQuest(true);
        }
        return string;
    }
}

