/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _363_SorrowfulSoundofFlute
extends Quest
implements ScriptFile {
    public final int NANARIN = 30956;
    public final int BARBADO = 30959;
    public final int POITAN = 30458;
    public final int HOLVAS = 30058;
    public final int MUSICAL_SCORE = 4420;
    public final int EVENT_CLOTHES = 4318;
    public final int NANARINS_FLUTE = 4319;
    public final int SABRINS_BLACK_BEER = 4320;
    public final int Musical_Score = 4420;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _363_SorrowfulSoundofFlute() {
        super(0);
        this.addStartNpc(30956);
        this.addTalkId(30956);
        this.addTalkId(30458);
        this.addTalkId(30058);
        this.addTalkId(30959);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("30956_2.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            questState.takeItems(4318, -1L);
            questState.takeItems(4319, -1L);
            questState.takeItems(4320, -1L);
        } else if (string.equalsIgnoreCase("30956_4.htm")) {
            questState.giveItems(4319, 1L);
            questState.playSound("ItemSound.quest_middle");
            questState.setCond(3);
        } else if (string.equalsIgnoreCase("answer1")) {
            questState.giveItems(4318, 1L);
            questState.playSound("ItemSound.quest_middle");
            questState.setCond(3);
            string2 = "30956_6.htm";
        } else if (string.equalsIgnoreCase("answer2")) {
            questState.giveItems(4320, 1L);
            questState.playSound("ItemSound.quest_middle");
            questState.setCond(3);
            string2 = "30956_6.htm";
        } else if (string.equalsIgnoreCase("30956_7.htm")) {
            questState.giveItems(4420, 1L);
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getCond();
        if (n == 30956) {
            if (n2 == 0) {
                if (questState.getPlayer().getLevel() < 15) {
                    string = "30956-00.htm";
                    questState.exitCurrentQuest(true);
                } else {
                    string = "30956_1.htm";
                }
            } else if (n2 == 1) {
                string = "30956_8.htm";
            } else if (n2 == 2) {
                string = "30956_3.htm";
            } else if (n2 == 3) {
                string = "30956_6.htm";
            } else if (n2 == 4) {
                string = "30956_5.htm";
            }
        } else if (n == 30959) {
            if (n2 == 3) {
                if (questState.getQuestItemsCount(4318) > 0L) {
                    questState.takeItems(4318, -1L);
                    string = "30959_2.htm";
                    questState.exitCurrentQuest(true);
                } else if (questState.getQuestItemsCount(4320) > 0L) {
                    questState.takeItems(4320, -1L);
                    string = "30959_2.htm";
                    questState.exitCurrentQuest(true);
                } else {
                    questState.takeItems(4319, -1L);
                    questState.setCond(4);
                    questState.playSound("ItemSound.quest_middle");
                    string = "30959_1.htm";
                }
            } else if (n2 == 4) {
                string = "30959_3.htm";
            }
        } else if (n == 30058 && (n2 == 1 || n2 == 2)) {
            questState.setCond(2);
            string = Rnd.chance(60) ? "30058_2.htm" : "30058_1.htm";
        } else if (n == 30458 && (n2 == 1 || n2 == 2)) {
            questState.setCond(2);
            string = Rnd.chance(60) ? "30458_2.htm" : "30458_1.htm";
        }
        return string;
    }
}

