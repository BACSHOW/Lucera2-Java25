/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _631_DeliciousTopChoiceMeat
extends Quest
implements ScriptFile {
    public final int TUNATUN = 31537;
    public final int[] MOB_LIST = new int[]{21460, 21461, 21462, 21463, 21464, 21465, 21466, 21467, 21468, 21469, 21479, 21480, 21481, 21482, 21483, 21484, 21485, 21486, 21487, 21488, 21498, 21499, 21500, 21501, 21502, 21503, 21504, 21505, 21506, 21507};
    public final int TOP_QUALITY_MEAT = 7546;
    public final int MOLD_GLUE = 4039;
    public final int MOLD_LUBRICANT = 4040;
    public final int MOLD_HARDENER = 4041;
    public final int ENRIA = 4042;
    public final int ASOFE = 4043;
    public final int THONS = 4044;
    public final int[][] REWARDS = new int[][]{{1, 4039, 15}, {2, 4043, 15}, {3, 4044, 15}, {4, 4040, 10}, {5, 4042, 10}, {6, 4041, 5}};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _631_DeliciousTopChoiceMeat() {
        super(0);
        this.addStartNpc(31537);
        this.addTalkId(31537);
        for (int n : this.MOB_LIST) {
            this.addKillId(n);
        }
        this.addQuestItem(7546);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("31537-03.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("31537-05.htm") && questState.getQuestItemsCount(7546) >= 120L) {
            questState.setCond(3);
        }
        for (int[] nArray : this.REWARDS) {
            if (!string.equalsIgnoreCase(String.valueOf(nArray[0]))) continue;
            if (questState.getCond() == 3 && questState.getQuestItemsCount(7546) >= 120L) {
                string2 = "31537-06.htm";
                questState.takeItems(7546, -1L);
                questState.giveItems(nArray[1], nArray[2] * (int)questState.getRateQuestsReward());
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                continue;
            }
            string2 = "31537-07.htm";
            questState.setCond(1);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getCond();
        if (n < 1) {
            if (questState.getPlayer().getLevel() < 65) {
                string = "31537-02.htm";
                questState.exitCurrentQuest(true);
            } else {
                string = "31537-01.htm";
            }
        } else if (n == 1) {
            string = "31537-01a.htm";
        } else if (n == 2) {
            if (questState.getQuestItemsCount(7546) < 120L) {
                string = "31537-01a.htm";
                questState.setCond(1);
            } else {
                string = "31537-04.htm";
            }
        } else if (n == 3) {
            string = "31537-05.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (questState.getCond() == 1) {
            for (int n2 : this.MOB_LIST) {
                if (!Rnd.chance(80) || n != n2) continue;
                questState.rollAndGive(7546, 1, 100.0);
                if (questState.getQuestItemsCount(7546) < 120L) {
                    questState.playSound("ItemSound.quest_itemget");
                    continue;
                }
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(2);
            }
        }
        return null;
    }
}

