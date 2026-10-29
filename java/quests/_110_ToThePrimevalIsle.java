/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _110_ToThePrimevalIsle
extends Quest
implements ScriptFile {
    int ANTON = 31338;
    int MARQUEZ = 32113;
    int ANCIENT_BOOK = 8777;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _110_ToThePrimevalIsle() {
        super(0);
        this.addStartNpc(this.ANTON);
        this.addTalkId(this.ANTON);
        this.addTalkId(this.MARQUEZ);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("1")) {
            string2 = "scroll_seller_anton_q0110_05.htm";
            questState.setCond(1);
            questState.giveItems(this.ANCIENT_BOOK, 1L);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equals("2") && questState.getQuestItemsCount(this.ANCIENT_BOOK) > 0L) {
            string2 = "marquez_q0110_05.htm";
            questState.playSound("ItemSound.quest_finish");
            questState.giveItems(57, 169380L);
            questState.takeItems(this.ANCIENT_BOOK, -1L);
            questState.exitCurrentQuest(false);
        } else if (string.equals("3")) {
            string2 = "marquez_q0110_06.htm";
            questState.exitCurrentQuest(true);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getState();
        int n3 = questState.getCond();
        if (n2 == 1) {
            if (questState.getPlayer().getLevel() >= 75) {
                string = "scroll_seller_anton_q0110_01.htm";
            } else {
                questState.exitCurrentQuest(true);
                string = "scroll_seller_anton_q0110_02.htm";
            }
        } else if (n == this.ANTON) {
            if (n3 == 1) {
                string = "scroll_seller_anton_q0110_07.htm";
            }
        } else if (n2 == 2 && n == this.MARQUEZ && n3 == 1) {
            string = questState.getQuestItemsCount(this.ANCIENT_BOOK) == 0L ? "marquez_q0110_07.htm" : "marquez_q0110_01.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        return null;
    }
}

