/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _008_AnAdventureBegins
extends Quest
implements ScriptFile {
    int JASMINE = 30134;
    int ROSELYN = 30355;
    int HARNE = 30144;
    int ROSELYNS_NOTE = 7573;
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

    public _008_AnAdventureBegins() {
        super(0);
        this.addStartNpc(this.JASMINE);
        this.addTalkId(this.JASMINE);
        this.addTalkId(this.ROSELYN);
        this.addTalkId(this.HARNE);
        this.addQuestItem(this.ROSELYNS_NOTE);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("jasmine_q0008_0104.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("sentry_roseline_q0008_0201.htm")) {
            questState.giveItems(this.ROSELYNS_NOTE, 1L);
            questState.setCond(2);
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equalsIgnoreCase("harne_q0008_0301.htm")) {
            questState.takeItems(this.ROSELYNS_NOTE, -1L);
            questState.setCond(3);
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equalsIgnoreCase("jasmine_q0008_0401.htm")) {
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
        if (n == this.JASMINE) {
            if (n2 == 0 && questState.getPlayer().getRace() == Race.darkelf) {
                if (questState.getPlayer().getLevel() >= 3) {
                    string = "jasmine_q0008_0101.htm";
                } else {
                    string = "jasmine_q0008_0102.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 1) {
                string = "jasmine_q0008_0105.htm";
            } else if (n2 == 3) {
                string = "jasmine_q0008_0301.htm";
            }
        } else if (n == this.ROSELYN) {
            string = questState.getQuestItemsCount(this.ROSELYNS_NOTE) == 0L ? "sentry_roseline_q0008_0101.htm" : "sentry_roseline_q0008_0202.htm";
        } else if (n == this.HARNE) {
            if (n2 == 2 && questState.getQuestItemsCount(this.ROSELYNS_NOTE) > 0L) {
                string = "harne_q0008_0201.htm";
            } else if (n2 == 2 && questState.getQuestItemsCount(this.ROSELYNS_NOTE) == 0L) {
                string = "harne_q0008_0302.htm";
            } else if (n2 == 3) {
                string = "harne_q0008_0303.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        return null;
    }
}

