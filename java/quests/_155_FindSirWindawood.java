/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _155_FindSirWindawood
extends Quest
implements ScriptFile {
    int OFFICIAL_LETTER = 1019;
    int HASTE_POTION = 734;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _155_FindSirWindawood() {
        super(0);
        this.addStartNpc(30042);
        this.addTalkId(30042);
        this.addTalkId(30311);
        this.addQuestItem(this.OFFICIAL_LETTER);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("30042-04.htm")) {
            questState.giveItems(this.OFFICIAL_LETTER, 1L);
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getCond();
        if (n == 30042) {
            if (n2 == 0) {
                if (questState.getPlayer().getLevel() >= 3) {
                    string = "30042-03.htm";
                    return string;
                }
                string = "30042-02.htm";
                questState.exitCurrentQuest(true);
            } else if (n2 == 1 && questState.getQuestItemsCount(this.OFFICIAL_LETTER) == 1L) {
                string = "30042-05.htm";
            }
        } else if (n == 30311 && n2 == 1 && questState.getQuestItemsCount(this.OFFICIAL_LETTER) == 1L) {
            string = "30311-01.htm";
            questState.takeItems(this.OFFICIAL_LETTER, -1L);
            questState.giveItems(this.HASTE_POTION, 1L);
            questState.setCond(0);
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(false);
        }
        return string;
    }
}

