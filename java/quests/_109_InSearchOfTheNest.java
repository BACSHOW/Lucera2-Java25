/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _109_InSearchOfTheNest
extends Quest
implements ScriptFile {
    private static final int aEq = 31553;
    private static final int aEr = 32015;
    private static final int aEs = 31554;
    private static final int aEt = 8083;
    private static final int aEu = 7246;
    private static final int aEv = 7247;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _109_InSearchOfTheNest() {
        super(0);
        this.addStartNpc(31553);
        this.addTalkId(32015);
        this.addTalkId(31554);
        this.addQuestItem(8083);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getCond();
        if (string.equalsIgnoreCase("Memo") && n == 1) {
            questState.giveItems(8083, 1L);
            questState.setCond(2);
            questState.playSound("ItemSound.quest_itemget");
            string2 = "You've find something...";
        } else if (string.equalsIgnoreCase("merc_cap_peace_q0109_0301.htm") && n == 2) {
            questState.takeItems(8083, -1L);
            questState.setCond(3);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getState();
        if (n2 == 3) {
            return "completed";
        }
        int n3 = questState.getCond();
        String string = "noquest";
        if (n2 == 1) {
            if (questState.getPlayer().getLevel() >= 66 && n == 31553 && (questState.getQuestItemsCount(7246) > 0L || questState.getQuestItemsCount(7247) > 0L)) {
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                questState.setCond(1);
                string = "merc_cap_peace_q0109_0105.htm";
            } else {
                string = "merc_cap_peace_q0109_0103.htm";
                questState.exitCurrentQuest(true);
            }
        } else if (n2 == 2) {
            if (n == 32015) {
                if (n3 == 1) {
                    string = "corpse_of_scout_q0109_0101.htm";
                } else if (n3 == 2) {
                    string = "corpse_of_scout_q0109_0203.htm";
                }
            } else if (n == 31553) {
                if (n3 == 1) {
                    string = "merc_cap_peace_q0109_0304.htm";
                } else if (n3 == 2) {
                    string = "merc_cap_peace_q0109_0201.htm";
                } else if (n3 == 3) {
                    string = "merc_cap_peace_q0109_0303.htm";
                }
            } else if (n == 31554 && n3 == 3) {
                string = "merc_kahmun_q0109_0401.htm";
                questState.giveItems(57, 5168L);
                questState.exitCurrentQuest(false);
                questState.playSound("ItemSound.quest_finish");
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        return null;
    }
}

