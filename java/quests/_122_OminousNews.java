/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _122_OminousNews
extends Quest
implements ScriptFile {
    int MOIRA = 31979;
    int KARUDA = 32017;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _122_OminousNews() {
        super(0);
        this.addStartNpc(this.MOIRA);
        this.addTalkId(this.KARUDA);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getCond();
        string2 = string;
        if (string2.equalsIgnoreCase("seer_moirase_q0122_0104.htm") && n == 0) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string2.equalsIgnoreCase("karuda_q0122_0201.htm")) {
            if (n == 1) {
                questState.giveItems(57, 1695L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(false);
            } else {
                string2 = "noquest";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getCond();
        if (n == this.MOIRA) {
            if (n2 == 0) {
                if (questState.getPlayer().getLevel() >= 20) {
                    string = "seer_moirase_q0122_0101.htm";
                } else {
                    string = "seer_moirase_q0122_0103.htm";
                    questState.exitCurrentQuest(true);
                }
            } else {
                string = "seer_moirase_q0122_0104.htm";
            }
        } else if (n == this.KARUDA && n2 == 1) {
            string = "karuda_q0122_0101.htm";
        }
        return string;
    }
}

