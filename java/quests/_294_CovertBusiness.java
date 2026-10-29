/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _294_CovertBusiness
extends Quest
implements ScriptFile {
    public static int BatFang = 1491;
    public static int RingOfRaccoon = 1508;
    public static int BarbedBat = 20370;
    public static int BladeBat = 20480;
    public static int Keef = 30534;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _294_CovertBusiness() {
        super(0);
        this.addStartNpc(Keef);
        this.addTalkId(Keef);
        this.addKillId(BarbedBat);
        this.addKillId(BladeBat);
        this.addQuestItem(BatFang);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("elder_keef_q0294_03.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getState();
        if (n == 1) {
            if (questState.getPlayer().getRace() != Race.dwarf) {
                string = "elder_keef_q0294_00.htm";
                questState.exitCurrentQuest(true);
            } else {
                if (questState.getPlayer().getLevel() >= 10) {
                    string = "elder_keef_q0294_02.htm";
                    return string;
                }
                string = "elder_keef_q0294_01.htm";
                questState.exitCurrentQuest(true);
            }
        } else if (questState.getQuestItemsCount(BatFang) < 100L) {
            string = "elder_keef_q0294_04.htm";
        } else {
            if (questState.getQuestItemsCount(RingOfRaccoon) < 1L) {
                questState.giveItems(RingOfRaccoon, 1L);
                string = "elder_keef_q0294_05.htm";
            } else {
                questState.giveItems(57, 2400L);
                string = "elder_keef_q0294_06.htm";
            }
            questState.addExpAndSp(0L, 600L);
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getCond() == 1) {
            questState.rollAndGive(BatFang, 1, 2, 100, 100.0);
        }
        return null;
    }
}

