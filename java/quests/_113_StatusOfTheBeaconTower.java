/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _113_StatusOfTheBeaconTower
extends Quest
implements ScriptFile {
    private static final int MOIRA = 31979;
    private static final int aEN = 32016;
    private static final int aEO = 8086;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _113_StatusOfTheBeaconTower() {
        super(0);
        this.addStartNpc(31979);
        this.addTalkId(32016);
        this.addQuestItem(8086);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("seer_moirase_q0113_0104.htm")) {
            questState.setCond(1);
            questState.giveItems(8086, 1L);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("torant_q0113_0201.htm")) {
            questState.giveItems(57, 12020L);
            questState.takeItems(8086, 1L);
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(false);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getState();
        int n3 = questState.getCond();
        if (n2 == 3) {
            string = "completed";
        } else if (n == 31979) {
            if (n2 == 1) {
                if (questState.getPlayer().getLevel() >= 40) {
                    string = "seer_moirase_q0113_0101.htm";
                } else {
                    string = "seer_moirase_q0113_0103.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n3 == 1) {
                string = "seer_moirase_q0113_0105.htm";
            }
        } else if (n == 32016 && questState.getQuestItemsCount(8086) == 1L) {
            string = "torant_q0113_0101.htm";
        }
        return string;
    }
}

