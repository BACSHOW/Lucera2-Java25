/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _159_ProtectHeadsprings
extends Quest
implements ScriptFile {
    int PLAGUE_DUST_ID = 1035;
    int HYACINTH_CHARM1_ID = 1071;
    int HYACINTH_CHARM2_ID = 1072;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _159_ProtectHeadsprings() {
        super(0);
        this.addStartNpc(30154);
        this.addKillId(27017);
        this.addQuestItem(this.PLAGUE_DUST_ID, this.HYACINTH_CHARM1_ID, this.HYACINTH_CHARM2_ID);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("1")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            if (questState.getQuestItemsCount(this.HYACINTH_CHARM1_ID) == 0L) {
                questState.giveItems(this.HYACINTH_CHARM1_ID, 1L);
                string2 = "30154-04.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getCond();
        if (n == 0) {
            if (questState.getPlayer().getRace() != Race.elf) {
                string = "30154-00.htm";
                questState.exitCurrentQuest(true);
            } else {
                if (questState.getPlayer().getLevel() >= 12) {
                    string = "30154-03.htm";
                    return string;
                }
                string = "30154-02.htm";
                questState.exitCurrentQuest(true);
            }
        } else if (n == 1) {
            string = "30154-05.htm";
        } else if (n == 2) {
            questState.takeItems(this.PLAGUE_DUST_ID, -1L);
            questState.takeItems(this.HYACINTH_CHARM1_ID, -1L);
            questState.giveItems(this.HYACINTH_CHARM2_ID, 1L);
            questState.setCond(3);
            string = "30154-06.htm";
        } else if (n == 3) {
            string = "30154-07.htm";
        } else if (n == 4) {
            questState.takeItems(this.PLAGUE_DUST_ID, -1L);
            questState.takeItems(this.HYACINTH_CHARM2_ID, -1L);
            questState.giveItems(57, 18250L);
            questState.playSound("ItemSound.quest_finish");
            string = "30154-08.htm";
            questState.exitCurrentQuest(false);
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getCond();
        if (n == 1 && Rnd.chance(60)) {
            questState.giveItems(this.PLAGUE_DUST_ID, 1L);
            questState.setCond(2);
            questState.playSound("ItemSound.quest_middle");
        } else if (n == 3 && Rnd.chance(60)) {
            if (questState.getQuestItemsCount(this.PLAGUE_DUST_ID) == 4L) {
                questState.giveItems(this.PLAGUE_DUST_ID, 1L);
                questState.setCond(4);
                questState.playSound("ItemSound.quest_middle");
            } else {
                questState.giveItems(this.PLAGUE_DUST_ID, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

