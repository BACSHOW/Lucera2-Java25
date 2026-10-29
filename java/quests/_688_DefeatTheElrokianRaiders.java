/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _688_DefeatTheElrokianRaiders
extends Quest
implements ScriptFile {
    private static final int bSx = 32105;
    private static final int bVP = 22214;
    private static int bVQ = 8785;

    public _688_DefeatTheElrokianRaiders() {
        super(1);
        this.addStartNpc(32105);
        this.addKillId(22214);
        this.addQuestItem(bVQ);
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = npcInstance.getNpcId();
        if (n == 32105) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("repulse_the_elcroki", String.valueOf(1), true);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "dindin_q0688_04.htm";
            } else if (string.equalsIgnoreCase("reply_1")) {
                string2 = "dindin_q0688_03.htm";
            } else if (string.equalsIgnoreCase("reply_5")) {
                if (questState.getQuestItemsCount(bVQ) >= 1L) {
                    if (questState.getQuestItemsCount(bVQ) >= 10L) {
                        questState.giveItems(57, questState.getQuestItemsCount(bVQ) * 3000L);
                    } else {
                        questState.giveItems(57, questState.getQuestItemsCount(bVQ) * 3000L);
                    }
                    questState.takeItems(bVQ, -1L);
                    string2 = "dindin_q0688_07.htm";
                }
            } else if (string.equalsIgnoreCase("reply_7")) {
                if (questState.getQuestItemsCount(bVQ) >= 1L) {
                    questState.giveItems(57, questState.getQuestItemsCount(bVQ) * 3000L);
                    questState.unset("repulse_the_elcroki");
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(true);
                    string2 = "dindin_q0688_08.htm";
                } else {
                    questState.unset("repulse_the_elcroki");
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(true);
                    string2 = "dindin_q0688_09.htm";
                }
            } else if (string.equalsIgnoreCase("reply_8")) {
                string2 = "dindin_q0688_10.htm";
            } else if (string.equalsIgnoreCase("reply_9")) {
                if (questState.getQuestItemsCount(bVQ) < 100L) {
                    string2 = "dindin_q0688_11.htm";
                }
                if (questState.getQuestItemsCount(bVQ) >= 100L) {
                    if (Rnd.get(1000) < 500) {
                        questState.giveItems(57, 450000L);
                        questState.takeItems(bVQ, 100L);
                        string2 = "dindin_q0688_12.htm";
                    } else {
                        questState.giveItems(57, 150000L);
                        string2 = "dindin_q0688_13.htm";
                        questState.takeItems(bVQ, 100L);
                    }
                }
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("repulse_the_elcroki");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 32105) break;
                if (questState.getPlayer().getLevel() < 75) {
                    string = "dindin_q0688_02.htm";
                    break;
                }
                questState.exitCurrentQuest(true);
                string = "dindin_q0688_01.htm";
                break;
            }
            case 2: {
                if (n2 != 32105 || n != 1) break;
                if (questState.getQuestItemsCount(bVQ) >= 1L) {
                    string = "dindin_q0688_05.htm";
                    break;
                }
                if (questState.getQuestItemsCount(bVQ) >= 1L) break;
                string = "dindin_q0688_06.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n;
        int n2 = questState.getInt("repulse_the_elcroki");
        int n3 = npcInstance.getNpcId();
        if (n2 == 1 && n3 == 22214 && (n = Rnd.get(1000)) < 448) {
            questState.giveItems(bVQ, 1L);
            questState.playSound("ItemSound.quest_itemget");
        }
        return null;
    }
}

