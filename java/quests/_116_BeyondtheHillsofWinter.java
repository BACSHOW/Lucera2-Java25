/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _116_BeyondtheHillsofWinter
extends Quest
implements ScriptFile {
    public final int FILAUR = 30535;
    public final int OBI = 32052;
    public final int Supplying_Goods_for_Railroad_Worker = 8098;
    public final int Bandage = 1833;
    public final int Energy_Stone = 5589;
    public final int Thief_Key = 1661;
    public final int SSD = 1463;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _116_BeyondtheHillsofWinter() {
        super(0);
        this.addStartNpc(30535);
        this.addTalkId(32052);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("elder_filaur_q0116_0104.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("elder_filaur_q0116_0201.htm")) {
            if (questState.getQuestItemsCount(1833) >= 20L && questState.getQuestItemsCount(5589) >= 5L && questState.getQuestItemsCount(1661) >= 10L) {
                questState.takeItems(1833, 20L);
                questState.takeItems(5589, 5L);
                questState.takeItems(1661, 10L);
                questState.giveItems(8098, 1L);
                questState.setCond(2);
                questState.setState(2);
            } else {
                string2 = "elder_filaur_q0116_0104.htm";
            }
        } else if (string.equalsIgnoreCase("materials")) {
            string2 = "railman_obi_q0116_0302.htm";
            questState.takeItems(8098, 1L);
            questState.giveItems(1463, 1650L);
            questState.exitCurrentQuest(false);
        } else if (string.equalsIgnoreCase("adena")) {
            string2 = "railman_obi_q0116_0302.htm";
            questState.takeItems(8098, 1L);
            questState.giveItems(57, 16500L);
            questState.exitCurrentQuest(false);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getState();
        int n3 = 0;
        if (n2 != 1) {
            n3 = questState.getCond();
        }
        if (n == 30535) {
            if (n3 == 0) {
                if (questState.getPlayer().getLevel() < 30) {
                    string = "elder_filaur_q0116_0103.htm";
                    questState.exitCurrentQuest(true);
                } else {
                    string = "elder_filaur_q0116_0101.htm";
                }
            } else if (n3 == 1) {
                string = "elder_filaur_q0116_0105.htm";
            } else if (n3 == 2) {
                string = "elder_filaur_q0116_0201.htm";
            }
        } else if (n == 32052 && n3 == 2 && questState.getQuestItemsCount(8098) > 0L) {
            string = "railman_obi_q0116_0201.htm";
        }
        return string;
    }
}

