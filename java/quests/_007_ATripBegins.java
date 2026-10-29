/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _007_ATripBegins
extends Quest
implements ScriptFile {
    int MIRABEL = 30146;
    int ARIEL = 30148;
    int ASTERIOS = 30154;
    int ARIELS_RECOMMENDATION = 7572;
    int SCROLL_OF_ESCAPE_GIRAN = 7126;
    int MARK_OF_TRAVELER = 7570;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _007_ATripBegins() {
        super(0);
        this.addStartNpc(this.MIRABEL);
        this.addTalkId(this.MIRABEL);
        this.addTalkId(this.ARIEL);
        this.addTalkId(this.ASTERIOS);
        this.addQuestItem(this.ARIELS_RECOMMENDATION);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("mint_q0007_0104.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("ariel_q0007_0201.htm")) {
            questState.giveItems(this.ARIELS_RECOMMENDATION, 1L);
            questState.setCond(2);
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equalsIgnoreCase("ozzy_q0007_0301.htm")) {
            questState.takeItems(this.ARIELS_RECOMMENDATION, -1L);
            questState.setCond(3);
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equalsIgnoreCase("mint_q0007_0401.htm")) {
            questState.giveItems(this.SCROLL_OF_ESCAPE_GIRAN, 1L);
            questState.giveItems(this.MARK_OF_TRAVELER, 1L);
            questState.setCond(0);
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
        if (n == this.MIRABEL) {
            if (n2 == 0) {
                if (questState.getPlayer().getRace() == Race.elf && questState.getPlayer().getLevel() >= 3) {
                    string = "mint_q0007_0101.htm";
                } else {
                    string = "mint_q0007_0102.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 1) {
                string = "mint_q0007_0105.htm";
            } else if (n2 == 3) {
                string = "mint_q0007_0301.htm";
            }
        } else if (n == this.ARIEL) {
            if (n2 == 1 && questState.getQuestItemsCount(this.ARIELS_RECOMMENDATION) == 0L) {
                string = "ariel_q0007_0101.htm";
            } else if (n2 == 2) {
                string = "ariel_q0007_0202.htm";
            }
        } else if (n == this.ASTERIOS) {
            if (n2 == 2 && questState.getQuestItemsCount(this.ARIELS_RECOMMENDATION) > 0L) {
                string = "ozzy_q0007_0201.htm";
            } else if (n2 == 2 && questState.getQuestItemsCount(this.ARIELS_RECOMMENDATION) == 0L) {
                string = "ozzy_q0007_0302.htm";
            } else if (n2 == 3) {
                string = "ozzy_q0007_0303.htm";
            }
        }
        return string;
    }
}

