/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _037_PleaseMakeMeFormalWear
extends Quest
implements ScriptFile {
    private static final int aBK = 7076;
    private static final int JEWEL_BOX = 7077;
    private static final int SEWING_KIT = 7078;
    private static final int DRESS_SHOES_BOX = 7113;
    private static final int aBL = 7164;
    private static final int aBM = 7160;
    private static final int aBN = 7159;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _037_PleaseMakeMeFormalWear() {
        super(0);
        this.addStartNpc(30842);
        this.addTalkId(30842);
        this.addTalkId(31520);
        this.addTalkId(31521);
        this.addTalkId(31627);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("30842-1.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equals("31520-1.htm")) {
            questState.giveItems(7164, 1L);
            questState.setCond(2);
        } else if (string.equals("31521-1.htm")) {
            questState.takeItems(7164, 1L);
            questState.giveItems(7160, 1L);
            questState.setCond(3);
        } else if (string.equals("31627-1.htm")) {
            if (questState.getQuestItemsCount(7160) > 0L) {
                questState.takeItems(7160, 1L);
                questState.setCond(4);
            } else {
                string2 = "You don't have enough materials";
            }
        } else if (string.equals("31521-3.htm")) {
            questState.giveItems(7159, 1L);
            questState.setCond(5);
        } else if (string.equals("31520-3.htm")) {
            questState.takeItems(7159, 1L);
            questState.setCond(6);
        } else if (string.equals("31520-5.htm")) {
            questState.takeItems(7076, 1L);
            questState.takeItems(7077, 1L);
            questState.takeItems(7078, 1L);
            questState.setCond(7);
        } else if (string.equals("31520-7.htm")) {
            if (questState.getQuestItemsCount(7113) > 0L) {
                questState.takeItems(7113, 1L);
                questState.giveItems(6408, 1L);
                questState.unset("cond");
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(false);
            } else {
                string2 = "You don't have enough materials";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 30842) {
            if (n2 == 0) {
                if (questState.getPlayer().getLevel() >= 60) {
                    string = "30842-0.htm";
                } else {
                    string = "30842-2.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 1) {
                string = "30842-1.htm";
            }
        } else if (n == 31520) {
            if (n2 == 1) {
                string = "31520-0.htm";
            } else if (n2 == 2) {
                string = "31520-1.htm";
            } else if (n2 == 5 || n2 == 6) {
                string = questState.getQuestItemsCount(7076) > 0L && questState.getQuestItemsCount(7077) > 0L && questState.getQuestItemsCount(7078) > 0L ? "31520-4.htm" : (questState.getQuestItemsCount(7159) > 0L ? "31520-2.htm" : "31520-3.htm");
            } else if (n2 == 7) {
                string = questState.getQuestItemsCount(7113) > 0L ? "31520-6.htm" : "31520-5.htm";
            }
        } else if (n == 31521) {
            if (questState.getQuestItemsCount(7164) > 0L) {
                string = "31521-0.htm";
            } else if (n2 == 3) {
                string = "31521-1.htm";
            } else if (n2 == 4) {
                string = "31521-2.htm";
            } else if (n2 == 5) {
                string = "31521-3.htm";
            }
        } else if (n == 31627) {
            string = "31627-0.htm";
        }
        return string;
    }
}

