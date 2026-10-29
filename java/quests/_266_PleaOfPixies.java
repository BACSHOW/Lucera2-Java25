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

public class _266_PleaOfPixies
extends Quest
implements ScriptFile {
    private static final int bdM = 1334;
    private static final int bdN = 1337;
    private static final int bdO = 1338;
    private static final int bdP = 1339;
    private static final int bdQ = 1336;
    private static final int bdR = 2176;
    private static final int bdS = 3032;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _266_PleaOfPixies() {
        super(0);
        this.addStartNpc(31852);
        this.addKillId(20525, 20530, 20534, 20537);
        this.addQuestItem(1334);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("pixy_murika_q0266_03.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        if (questState.getCond() == 0) {
            if (questState.getPlayer().getRace() != Race.elf) {
                string = "pixy_murika_q0266_00.htm";
                questState.exitCurrentQuest(true);
            } else if (questState.getPlayer().getLevel() < 3) {
                string = "pixy_murika_q0266_01.htm";
                questState.exitCurrentQuest(true);
            } else {
                string = "pixy_murika_q0266_02.htm";
            }
        } else if (questState.getQuestItemsCount(1334) < 100L) {
            string = "pixy_murika_q0266_04.htm";
        } else {
            questState.takeItems(1334, -1L);
            int n = Rnd.get(100);
            if (n < 2) {
                questState.giveItems(1337, 1L);
                questState.giveItems(3032, 1L);
                questState.playSound("ItemSound.quest_jackpot");
            } else if (n < 20) {
                questState.giveItems(1338, 1L);
                questState.giveItems(2176, 1L);
            } else if (n < 45) {
                questState.giveItems(1339, 1L);
            } else {
                questState.giveItems(1336, 1L);
            }
            string = "pixy_murika_q0266_05.htm";
            questState.exitCurrentQuest(true);
            questState.playSound("ItemSound.quest_finish");
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getCond() == 1) {
            questState.rollAndGive(1334, 1, 1, 100, 60 + npcInstance.getLevel() * 5);
        }
        return null;
    }
}

