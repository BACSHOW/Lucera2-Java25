/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;
import quests._119_LastImperialPrince;

public class _654_JourneytoaSettlement
extends Quest
implements ScriptFile {
    private static final int aFO = 31453;
    private static final int bUe = 21294;
    private static final int bUf = 21295;
    private static final int bUg = 8072;
    private static final int bUh = 8073;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _654_JourneytoaSettlement() {
        super(0);
        this.addStartNpc(31453);
        this.addKillId(21294, 21295);
        this.addQuestItem(8072);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getInt("to_reach_an_ending");
        int n2 = npcInstance.getNpcId();
        if (n2 == 31453) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("to_reach_an_ending", String.valueOf(1), true);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "printessa_spirit_q0654_03.htm";
            } else if (string.equalsIgnoreCase("reply_1")) {
                if (n == 1) {
                    questState.setCond(2);
                    questState.set("to_reach_an_ending", String.valueOf(2), true);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "printessa_spirit_q0654_04.htm";
                }
            } else if (string.equalsIgnoreCase("reply_2") && n == 2 && questState.getQuestItemsCount(8072) >= 1L) {
                questState.giveItems(8073, 1L);
                questState.takeItems(8072, -1L);
                questState.unset("to_reach_an_ending");
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                string2 = "printessa_spirit_q0654_07.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        QuestState questState2 = questState.getPlayer().getQuestState(_119_LastImperialPrince.class);
        int n = questState.getInt("to_reach_an_ending");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 31453) break;
                if (questState.getPlayer().getLevel() >= 74 && questState2 != null && questState2.isCompleted()) {
                    string = "printessa_spirit_q0654_01.htm";
                    break;
                }
                questState.exitCurrentQuest(true);
                string = "printessa_spirit_q0654_02.htm";
                break;
            }
            case 2: {
                if (n2 != 31453) break;
                if (n == 1) {
                    questState.setCond(2);
                    questState.set("to_reach_an_ending", String.valueOf(2), true);
                    questState.playSound("ItemSound.quest_middle");
                    string = "printessa_spirit_q0654_04.htm";
                    break;
                }
                if (n == 2 && questState.getQuestItemsCount(8072) == 0L) {
                    string = "printessa_spirit_q0654_05.htm";
                    break;
                }
                if (n != 2 || questState.getQuestItemsCount(8072) < 1L) break;
                string = "printessa_spirit_q0654_06.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getInt("to_reach_an_ending");
        int n2 = npcInstance.getNpcId();
        if (n == 2 && questState.getQuestItemsCount(8072) == 0L) {
            if (n2 == 21294) {
                if (Rnd.get(100) < 84) {
                    questState.setCond(3);
                    questState.giveItems(8072, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n2 == 21295 && Rnd.get(1000) < 893) {
                questState.setCond(3);
                questState.giveItems(8072, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

