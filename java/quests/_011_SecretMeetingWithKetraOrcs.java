/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _011_SecretMeetingWithKetraOrcs
extends Quest
implements ScriptFile {
    int CADMON = 31296;
    int LEON = 31256;
    int WAHKAN = 31371;
    int MUNITIONS_BOX = 7231;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _011_SecretMeetingWithKetraOrcs() {
        super(0);
        this.addStartNpc(this.CADMON);
        this.addTalkId(this.LEON);
        this.addTalkId(this.WAHKAN);
        this.addQuestItem(this.MUNITIONS_BOX);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("guard_cadmon_q0011_0104.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("trader_leon_q0011_0201.htm")) {
            questState.giveItems(this.MUNITIONS_BOX, 1L);
            questState.setCond(2);
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equalsIgnoreCase("herald_wakan_q0011_0301.htm") && questState.getQuestItemsCount(this.MUNITIONS_BOX) > 0L) {
            questState.takeItems(this.MUNITIONS_BOX, 1L);
            questState.addExpAndSp(82045L, 6047L);
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(false);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == this.CADMON) {
            if (n2 == 0) {
                if (questState.getPlayer().getLevel() >= 74) {
                    string = "guard_cadmon_q0011_0101.htm";
                } else {
                    string = "guard_cadmon_q0011_0103.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 1) {
                string = "guard_cadmon_q0011_0105.htm";
            }
        } else if (n == this.LEON) {
            if (n2 == 1) {
                string = "trader_leon_q0011_0101.htm";
            } else if (n2 == 2) {
                string = "trader_leon_q0011_0202.htm";
            }
        } else if (n == this.WAHKAN && n2 == 2 && questState.getQuestItemsCount(this.MUNITIONS_BOX) > 0L) {
            string = "herald_wakan_q0011_0201.htm";
        }
        return string;
    }
}

