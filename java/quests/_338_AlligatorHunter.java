/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _338_AlligatorHunter
extends Quest
implements ScriptFile {
    private static final int bmL = 30892;
    private static final int aBt = 20135;
    private static final int bmM = 4337;

    public _338_AlligatorHunter() {
        super(0);
        this.addStartNpc(30892);
        this.addKillId(20135);
        this.addQuestItem(4337);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getInt("crocodile_hunter_ex");
        int n2 = npcInstance.getNpcId();
        if (n2 == 30892) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("crocodile_hunter", String.valueOf(1), true);
                questState.set("crocodile_hunter_ex", String.valueOf(0), true);
                questState.playSound("ItemSound.quest_accept");
                questState.setState(2);
                string2 = "trader_enverun_q0338_03.htm";
            } else if (string.equalsIgnoreCase("reply_1")) {
                if (questState.getQuestItemsCount(4337) == 0L) {
                    string2 = "trader_enverun_q0338_05.htm";
                } else if (questState.getQuestItemsCount(4337) > 0L) {
                    if (questState.getQuestItemsCount(4337) >= 10L) {
                        questState.giveItems(57, 3430L + questState.getQuestItemsCount(4337) * 60L);
                    } else {
                        questState.giveItems(57, questState.getQuestItemsCount(4337) * 60L);
                    }
                    questState.set("crocodile_hunter_ex", String.valueOf((long)n + questState.getQuestItemsCount(4337)), true);
                    questState.takeItems(4337, -1L);
                    string2 = "trader_enverun_q0338_06.htm";
                }
            } else if (string.equalsIgnoreCase("reply_2")) {
                string2 = "trader_enverun_q0338_11.htm";
            } else if (string.equalsIgnoreCase("reply_3")) {
                string2 = "trader_enverun_q0338_12.htm";
            } else if (string.equalsIgnoreCase("reply_4")) {
                string2 = "trader_enverun_q0338_15.htm";
            } else if (string.equalsIgnoreCase("reply_5")) {
                if (n >= 10) {
                    questState.unset("crocodile_hunter");
                    questState.unset("crocodile_hunter_ex");
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(true);
                    string2 = "trader_enverun_q0338_16.htm";
                } else {
                    questState.unset("crocodile_hunter");
                    questState.unset("crocodile_hunter_ex");
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(true);
                    string2 = "trader_enverun_q0338_16t.htm";
                }
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("crocodile_hunter");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 30892) break;
                if (questState.getPlayer().getLevel() < 40 || questState.getPlayer().getLevel() > 47) {
                    string = "trader_enverun_q0338_01.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                string = "trader_enverun_q0338_02.htm";
                break;
            }
            case 2: {
                if (n2 != 30892 || n != 1) break;
                string = "trader_enverun_q0338_04.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getInt("crocodile_hunter");
        int n2 = npcInstance.getNpcId();
        if (n2 == 20135 && n == 1) {
            questState.giveItems(4337, 1L);
            questState.playSound("ItemSound.quest_itemget");
            if (Rnd.get(100) < 19) {
                questState.giveItems(4337, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
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
}

