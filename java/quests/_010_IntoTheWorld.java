/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _010_IntoTheWorld
extends Quest
implements ScriptFile {
    int VERY_EXPENSIVE_NECKLACE = 7574;
    int SCROLL_OF_ESCAPE_GIRAN = 7126;
    int MARK_OF_TRAVELER = 7570;
    int BALANKI = 30533;
    int REED = 30520;
    int GERALD = 30650;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _010_IntoTheWorld() {
        super(0);
        this.addStartNpc(this.BALANKI);
        this.addTalkId(this.BALANKI);
        this.addTalkId(this.REED);
        this.addTalkId(this.GERALD);
        this.addQuestItem(this.VERY_EXPENSIVE_NECKLACE);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("elder_balanki_q0010_0104.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("warehouse_chief_reed_q0010_0201.htm")) {
            questState.giveItems(this.VERY_EXPENSIVE_NECKLACE, 1L);
            questState.setCond(2);
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equalsIgnoreCase("gerald_priest_of_earth_q0010_0301.htm")) {
            questState.takeItems(this.VERY_EXPENSIVE_NECKLACE, -1L);
            questState.setCond(3);
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equalsIgnoreCase("warehouse_chief_reed_q0010_0401.htm")) {
            questState.setCond(4);
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equalsIgnoreCase("elder_balanki_q0010_0501.htm")) {
            questState.giveItems(this.SCROLL_OF_ESCAPE_GIRAN, 1L);
            questState.giveItems(this.MARK_OF_TRAVELER, 1L);
            questState.exitCurrentQuest(false);
            questState.playSound("ItemSound.quest_finish");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == this.BALANKI) {
            if (n2 == 0) {
                if (questState.getPlayer().getRace() == Race.dwarf && questState.getPlayer().getLevel() >= 3) {
                    string = "elder_balanki_q0010_0101.htm";
                } else {
                    string = "elder_balanki_q0010_0102.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 1) {
                string = "elder_balanki_q0010_0105.htm";
            } else if (n2 == 4) {
                string = "elder_balanki_q0010_0401.htm";
            }
        } else if (n == this.REED) {
            if (n2 == 1) {
                string = "warehouse_chief_reed_q0010_0101.htm";
            } else if (n2 == 2) {
                string = "warehouse_chief_reed_q0010_0202.htm";
            } else if (n2 == 3) {
                string = "warehouse_chief_reed_q0010_0301.htm";
            } else if (n2 == 4) {
                string = "warehouse_chief_reed_q0010_0402.htm";
            }
        } else if (n == this.GERALD) {
            string = n2 == 2 && questState.getQuestItemsCount(this.VERY_EXPENSIVE_NECKLACE) > 0L ? "gerald_priest_of_earth_q0010_0201.htm" : (n2 == 3 ? "gerald_priest_of_earth_q0010_0302.htm" : "gerald_priest_of_earth_q0010_0303.htm");
        }
        return string;
    }
}

