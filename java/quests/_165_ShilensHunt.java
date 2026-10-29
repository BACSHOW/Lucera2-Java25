/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.Player;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _165_ShilensHunt
extends Quest
implements ScriptFile {
    private static final int aHW = 1160;
    private static final int aHv = 1060;
    private static final int aHX = 30348;
    private static final int aHY = 20456;
    private static final int aHZ = 20529;
    private static final int aIa = 20532;
    private static final int aIb = 20536;

    public _165_ShilensHunt() {
        super(0);
        this.addStartNpc(30348);
        this.addKillId(20456, 20529, 20532, 20536);
        this.addQuestItem(1160);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            string2 = "sentry_nelsya_q0321_03.htm";
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getState();
        Player player = questState.getPlayer();
        switch (n) {
            case 1: {
                if (player.getRace() != Race.darkelf) {
                    string = "sentry_nelsya_q0321_00.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                if (player.getLevel() >= 3) {
                    string = "sentry_nelsya_q0321_02.htm";
                    break;
                }
                string = "sentry_nelsya_q0321_01.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (questState.getQuestItemsCount(1160) < 13L) {
                    string = "sentry_nelsya_q0321_04.htm";
                    break;
                }
                if (questState.getQuestItemsCount(1160) < 13L) break;
                string = "sentry_nelsya_q0321_05.htm";
                questState.takeItems(1160, -1L);
                questState.giveItems(1060, 5L);
                questState.exitCurrentQuest(false);
                questState.playSound("ItemSound.quest_finish");
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (n == 20456 && Rnd.get(3) < 3 && questState.getCond() == 1) {
            questState.rollAndGive(1160, 1, 100.0);
            if (questState.getQuestItemsCount(1160) >= 13L) {
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(2);
            }
        } else if (n == 20529 && Rnd.get(3) < 1 && questState.getCond() == 1) {
            questState.rollAndGive(1160, 1, 100.0);
            if (questState.getQuestItemsCount(1160) >= 13L) {
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(2);
            }
        } else if (n == 20532 && Rnd.get(3) < 1 && questState.getCond() == 1) {
            questState.rollAndGive(1160, 1, 100.0);
            if (questState.getQuestItemsCount(1160) >= 13L) {
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(2);
            }
        } else if (n == 20536 && Rnd.get(3) < 2 && questState.getCond() == 1) {
            questState.rollAndGive(1160, 1, 100.0);
            if (questState.getQuestItemsCount(1160) >= 13L) {
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(2);
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

