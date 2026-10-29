/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _158_SeedOfEvil
extends Quest
implements ScriptFile {
    int CLAY_TABLET_ID = 1025;
    int ENCHANT_ARMOR_D = 956;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _158_SeedOfEvil() {
        super(0);
        this.addStartNpc(30031);
        this.addKillId(27016);
        this.addQuestItem(this.CLAY_TABLET_ID);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("1")) {
            questState.set("id", "0");
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            string2 = "30031-04.htm";
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getState();
        if (n2 == 1) {
            questState.setState(2);
            questState.set("id", "0");
        }
        if (n == 30031 && questState.getCond() == 0) {
            if (questState.getCond() < 15) {
                if (questState.getPlayer().getLevel() >= 21) {
                    string = "30031-03.htm";
                    return string;
                }
                string = "30031-02.htm";
                questState.exitCurrentQuest(true);
            } else {
                string = "30031-02.htm";
                questState.exitCurrentQuest(true);
            }
        } else if (n == 30031 && questState.getCond() == 0) {
            string = "completed";
        } else if (n == 30031 && questState.getCond() != 0 && questState.getQuestItemsCount(this.CLAY_TABLET_ID) == 0L) {
            string = "30031-05.htm";
        } else if (n == 30031 && questState.getCond() != 0 && questState.getQuestItemsCount(this.CLAY_TABLET_ID) != 0L) {
            questState.takeItems(this.CLAY_TABLET_ID, questState.getQuestItemsCount(this.CLAY_TABLET_ID));
            questState.playSound("ItemSound.quest_finish");
            questState.giveItems(this.ENCHANT_ARMOR_D, 1L);
            string = "30031-06.htm";
            questState.exitCurrentQuest(false);
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getQuestItemsCount(this.CLAY_TABLET_ID) == 0L) {
            questState.giveItems(this.CLAY_TABLET_ID, 1L);
            questState.playSound("ItemSound.quest_middle");
            questState.setCond(2);
        }
        return null;
    }
}

