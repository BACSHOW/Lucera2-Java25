/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;
import quests._037_PleaseMakeMeFormalWear;

public class _035_FindGlitteringJewelry
extends Quest
implements ScriptFile {
    int ROUGH_JEWEL = 7162;
    int ORIHARUKON = 1893;
    int SILVER_NUGGET = 1873;
    int THONS = 4044;
    int JEWEL_BOX = 7077;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _035_FindGlitteringJewelry() {
        super(0);
        this.addStartNpc(30091);
        this.addTalkId(30091);
        this.addTalkId(30879);
        this.addKillId(20135);
        this.addQuestItem(this.ROUGH_JEWEL);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getCond();
        if (string.equals("30091-1.htm") && n == 0) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equals("30879-1.htm") && n == 1) {
            questState.setCond(2);
        } else if (string.equals("30091-3.htm") && n == 3) {
            if (questState.getQuestItemsCount(this.ROUGH_JEWEL) == 10L) {
                questState.takeItems(this.ROUGH_JEWEL, -1L);
                questState.setCond(4);
            } else {
                string2 = "30091-hvnore.htm";
            }
        } else if (string.equals("30091-5.htm") && n == 4) {
            if (questState.getQuestItemsCount(this.ORIHARUKON) >= 5L && questState.getQuestItemsCount(this.SILVER_NUGGET) >= 500L && questState.getQuestItemsCount(this.THONS) >= 150L) {
                questState.takeItems(this.ORIHARUKON, 5L);
                questState.takeItems(this.SILVER_NUGGET, 500L);
                questState.takeItems(this.THONS, 150L);
                questState.giveItems(this.JEWEL_BOX, 1L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            } else {
                string2 = "30091-hvnmat-bug.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 30091) {
            if (n2 == 0 && questState.getQuestItemsCount(this.JEWEL_BOX) == 0L) {
                if (questState.getPlayer().getLevel() >= 60) {
                    QuestState questState2 = questState.getPlayer().getQuestState(_037_PleaseMakeMeFormalWear.class);
                    if (questState2 != null && questState2.getCond() == 6) {
                        string = "30091-0.htm";
                    } else {
                        questState.exitCurrentQuest(true);
                    }
                } else {
                    string = "30091-6.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 1) {
                string = "30091-1r.htm";
            } else if (n2 == 2) {
                string = "30091-1r2.htm";
            } else if (n2 == 3 && questState.getQuestItemsCount(this.ROUGH_JEWEL) == 10L) {
                string = "30091-2.htm";
            } else if (n2 == 4 && (questState.getQuestItemsCount(this.ORIHARUKON) < 5L || questState.getQuestItemsCount(this.SILVER_NUGGET) < 500L || questState.getQuestItemsCount(this.THONS) < 150L)) {
                string = "30091-hvnmat.htm";
            } else if (n2 == 4 && questState.getQuestItemsCount(this.ORIHARUKON) >= 5L && questState.getQuestItemsCount(this.SILVER_NUGGET) >= 500L && questState.getQuestItemsCount(this.THONS) >= 150L) {
                string = "30091-4.htm";
            }
        } else if (n == 30879) {
            if (n2 == 1) {
                string = "30879-0.htm";
            } else if (n2 == 2) {
                string = "30879-1r.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        long l = questState.getQuestItemsCount(this.ROUGH_JEWEL);
        if (l < 10L) {
            questState.giveItems(this.ROUGH_JEWEL, 1L);
            if (questState.getQuestItemsCount(this.ROUGH_JEWEL) == 10L) {
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(3);
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

