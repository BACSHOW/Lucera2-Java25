/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;
import quests._037_PleaseMakeMeFormalWear;

public class _033_MakeAPairOfDressShoes
extends Quest
implements ScriptFile {
    int LEATHER = 1882;
    int THREAD = 1868;
    int DRESS_SHOES_BOX = 7113;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _033_MakeAPairOfDressShoes() {
        super(0);
        this.addStartNpc(30838);
        this.addTalkId(30838);
        this.addTalkId(30838);
        this.addTalkId(30164);
        this.addTalkId(31520);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("30838-1.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equals("31520-1.htm")) {
            questState.setCond(2);
        } else if (string.equals("30838-3.htm")) {
            questState.setCond(3);
        } else if (string.equals("30838-5.htm")) {
            if (questState.getQuestItemsCount(this.LEATHER) >= 200L && questState.getQuestItemsCount(this.THREAD) >= 600L && questState.getQuestItemsCount(57) >= 200000L) {
                questState.takeItems(this.LEATHER, 200L);
                questState.takeItems(this.THREAD, 600L);
                questState.takeItems(57, 200000L);
                questState.setCond(4);
            } else {
                string2 = "You don't have enough materials";
            }
        } else if (string.equals("30164-1.htm")) {
            if (questState.getQuestItemsCount(57) >= 300000L) {
                questState.takeItems(57, 300000L);
                questState.setCond(5);
            } else {
                string2 = "30164-havent.htm";
            }
        } else if (string.equals("30838-7.htm")) {
            questState.giveItems(this.DRESS_SHOES_BOX, 1L);
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 30838) {
            if (n2 == 0 && questState.getQuestItemsCount(this.DRESS_SHOES_BOX) == 0L) {
                if (questState.getPlayer().getLevel() >= 60) {
                    QuestState questState2 = questState.getPlayer().getQuestState(_037_PleaseMakeMeFormalWear.class);
                    if (questState2 != null && questState2.getCond() == 7) {
                        string = "30838-0.htm";
                    } else {
                        questState.exitCurrentQuest(true);
                    }
                } else {
                    string = "30838-00.htm";
                }
            } else if (n2 == 1) {
                string = "30838-1.htm";
            } else if (n2 == 2) {
                string = "30838-2.htm";
            } else if (n2 == 3 && questState.getQuestItemsCount(this.LEATHER) >= 200L && questState.getQuestItemsCount(this.THREAD) >= 600L && questState.getQuestItemsCount(57) >= 200000L) {
                string = "30838-4.htm";
            } else if (n2 == 3 && (questState.getQuestItemsCount(this.LEATHER) < 200L || questState.getQuestItemsCount(this.THREAD) < 600L || questState.getQuestItemsCount(57) < 200000L)) {
                string = "30838-4r.htm";
            } else if (n2 == 4) {
                string = "30838-5r.htm";
            } else if (n2 == 5) {
                string = "30838-6.htm";
            }
        } else if (n == 31520) {
            if (n2 == 1) {
                string = "31520-0.htm";
            } else if (n2 == 2) {
                string = "31520-1r.htm";
            }
        } else if (n == 30164) {
            if (n2 == 4) {
                string = "30164-0.htm";
            } else if (n2 == 5) {
                string = "30164-2.htm";
            }
        }
        return string;
    }
}

