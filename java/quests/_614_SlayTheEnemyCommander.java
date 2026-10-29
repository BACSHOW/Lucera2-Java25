/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _614_SlayTheEnemyCommander
extends Quest
implements ScriptFile {
    private static final int bLT = 31377;
    private static final int bLU = 25302;
    private static final int bLV = 7221;
    private static final int bLW = 7222;
    private static final int bLX = 7223;
    private static final int bLY = 7224;
    private static final int bLZ = 7225;
    private static final int bMa = 7241;
    private static final int bMb = 7230;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _614_SlayTheEnemyCommander() {
        super(1);
        this.addStartNpc(31377);
        this.addKillId(25302);
        this.addQuestItem(7241);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            string2 = "elder_ashas_barka_durai_q0614_0104.htm";
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("614_3")) {
            if (questState.getQuestItemsCount(7241) >= 1L) {
                string2 = "elder_ashas_barka_durai_q0614_0201.htm";
                questState.takeItems(7241, -1L);
                questState.giveItems(7230, 1L);
                questState.addExpAndSp(0L, 10000L);
                questState.unset("cond");
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            } else {
                string2 = "elder_ashas_barka_durai_q0614_0106.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getCond();
        if (n == 0) {
            if (questState.getPlayer().getLevel() >= 75) {
                if (questState.getQuestItemsCount(7224) == 1L || questState.getQuestItemsCount(7225) == 1L) {
                    string = "elder_ashas_barka_durai_q0614_0101.htm";
                } else {
                    string = "elder_ashas_barka_durai_q0614_0102.htm";
                    questState.exitCurrentQuest(true);
                }
            } else {
                string = "elder_ashas_barka_durai_q0614_0103.htm";
                questState.exitCurrentQuest(true);
            }
        } else if (n == 1 && questState.getQuestItemsCount(7241) == 0L) {
            string = "elder_ashas_barka_durai_q0614_0106.htm";
        } else if (n == 2 && questState.getQuestItemsCount(7241) >= 1L) {
            string = "elder_ashas_barka_durai_q0614_0105.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getCond() == 1) {
            questState.giveItems(7241, 1L);
            questState.setCond(2);
            questState.playSound("ItemSound.quest_itemget");
        }
        return null;
    }
}

