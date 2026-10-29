/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;
import quests._126_IntheNameofEvilPart2;

public class _641_AttackSailren
extends Quest
implements ScriptFile {
    private static int bSp = 32109;
    private static int bSq = 22196;
    private static int bSr = 22197;
    private static int bSs = 22198;
    private static int bSt = 22218;
    private static int bSu = 22223;
    private static int bSv = 22199;
    private static int bSw = 8782;
    private static int ayh = 8784;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _641_AttackSailren() {
        super(1);
        this.addStartNpc(bSp);
        this.addKillId(bSq);
        this.addKillId(bSr);
        this.addKillId(bSs);
        this.addKillId(bSt);
        this.addKillId(bSu);
        this.addKillId(bSv);
        this.addQuestItem(bSw);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("statue_of_shilen_q0641_05.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("statue_of_shilen_q0641_08.htm")) {
            questState.playSound("ItemSound.quest_finish");
            questState.takeItems(bSw, -1L);
            questState.giveItems(ayh, 1L);
            questState.exitCurrentQuest(true);
            questState.unset("cond");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getCond();
        if (n == 0) {
            QuestState questState2 = questState.getPlayer().getQuestState(_126_IntheNameofEvilPart2.class);
            if (questState2 == null || !questState2.isCompleted()) {
                string = "statue_of_shilen_q0641_02.htm";
            } else if (questState.getPlayer().getLevel() >= 77) {
                string = "statue_of_shilen_q0641_01.htm";
            } else {
                questState.exitCurrentQuest(true);
            }
        } else if (n == 1) {
            string = "statue_of_shilen_q0641_05.htm";
        } else if (n == 2) {
            string = "statue_of_shilen_q0641_07.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getQuestItemsCount(bSw) < 30L) {
            questState.giveItems(bSw, 1L);
            if (questState.getQuestItemsCount(bSw) == 30L) {
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(2);
                questState.setState(2);
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

