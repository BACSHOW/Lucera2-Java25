/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _358_IllegitimateChildOfAGoddess
extends Quest
implements ScriptFile {
    private static final int aGN = 30862;
    private static final int brp = 20672;
    private static final int brq = 20673;
    private static final int brr = 5868;
    private static final int brs = 6329;
    private static final int brt = 6331;
    private static final int bru = 6333;
    private static final int brv = 6335;
    private static final int brw = 6337;
    private static final int brx = 6339;
    private static final int bry = 5364;
    private static final int brz = 5366;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _358_IllegitimateChildOfAGoddess() {
        super(0);
        this.addStartNpc(30862);
        this.addKillId(20672, 20673);
        this.addQuestItem(5868);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = npcInstance.getNpcId();
        if (n == 30862) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("illegitimate_child", String.valueOf(1), true);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "grandmaster_oltlin_q0358_05.htm";
            } else if (string.equalsIgnoreCase("reply_1")) {
                string2 = "grandmaster_oltlin_q0358_04.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("illegitimate_child");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 30862) break;
                if (questState.getPlayer().getLevel() < 63) {
                    string = "grandmaster_oltlin_q0358_01.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                if (questState.getPlayer().getLevel() < 63) break;
                string = "grandmaster_oltlin_q0358_02.htm";
                break;
            }
            case 2: {
                if (n2 != 30862) break;
                if (n == 1 && questState.getQuestItemsCount(5868) < 108L) {
                    string = "grandmaster_oltlin_q0358_06.htm";
                    break;
                }
                if (n != 1 || questState.getQuestItemsCount(5868) < 108L) break;
                int n4 = Rnd.get(1000);
                if (n4 < 125) {
                    questState.giveItems(6331, 1L);
                } else if (n4 < 250) {
                    questState.giveItems(6337, 1L);
                } else if (n4 < 375) {
                    questState.giveItems(6329, 1L);
                } else if (n4 < 500) {
                    questState.giveItems(6335, 1L);
                } else if (n4 < 625) {
                    questState.giveItems(6333, 1L);
                } else if (n4 < 750) {
                    questState.giveItems(6339, 1L);
                } else if (n4 < 875) {
                    questState.giveItems(5366, 1L);
                } else {
                    questState.giveItems(5364, 1L);
                }
                questState.takeItems(5868, -1L);
                questState.unset("illegitimate_child");
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                string = "grandmaster_oltlin_q0358_07.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getInt("illegitimate_child");
        int n2 = npcInstance.getNpcId();
        if (n == 1) {
            if (n2 == 20672) {
                if (questState.getQuestItemsCount(5868) < 108L && Rnd.get(100) < 71) {
                    questState.giveItems(5868, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                    if (questState.getQuestItemsCount(5868) >= 107L) {
                        questState.setCond(2);
                        questState.playSound("ItemSound.quest_middle");
                    } else {
                        questState.playSound("ItemSound.quest_itemget");
                    }
                }
            } else if (n2 == 20673 && questState.getQuestItemsCount(5868) < 108L && Rnd.get(100) < 74) {
                questState.giveItems(5868, 1L);
                if (questState.getQuestItemsCount(5868) >= 107L) {
                    questState.setCond(2);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        }
        return null;
    }
}

