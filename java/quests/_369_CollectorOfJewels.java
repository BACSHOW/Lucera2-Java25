/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _369_CollectorOfJewels
extends Quest
implements ScriptFile {
    private static int bsU = 30376;
    private static final int bsV = 20609;
    private static final int bsW = 20612;
    private static final int bsX = 20616;
    private static final int bsY = 20619;
    private static final int bsZ = 20747;
    private static final int bta = 20749;
    private static int btb = 5882;
    private static int btc = 5883;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _369_CollectorOfJewels() {
        super(0);
        this.addStartNpc(bsU);
        this.addKillId(20609, 20612, 20616, 20619, 20747, 20749);
        this.addQuestItem(btb, btc);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = npcInstance.getNpcId();
        if (n == bsU) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("man_collect_element", String.valueOf(1), true);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "magister_nell_q0369_03.htm";
            } else if (string.equalsIgnoreCase("reply_1")) {
                questState.setCond(3);
                questState.set("man_collect_element", String.valueOf(3), true);
                string2 = "magister_nell_q0369_07.htm";
            } else if (string.equalsIgnoreCase("reply_2")) {
                questState.takeItems(btb, -1L);
                questState.takeItems(btc, -1L);
                questState.unset("man_collect_element");
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                string2 = "magister_nell_q0369_08.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("man_collect_element");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != bsU) break;
                if (questState.getPlayer().getLevel() < 25) {
                    string = "magister_nell_q0369_01.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                string = "magister_nell_q0369_02.htm";
                break;
            }
            case 2: {
                if (n2 != bsU) break;
                if ((questState.getQuestItemsCount(btc) < 50L || questState.getQuestItemsCount(btb) < 50L) && n == 1) {
                    string = "magister_nell_q0369_04.htm";
                    break;
                }
                if (questState.getQuestItemsCount(btc) >= 50L && questState.getQuestItemsCount(btb) >= 50L && n == 1) {
                    questState.giveItems(57, 12500L);
                    questState.takeItems(btb, -1L);
                    questState.takeItems(btc, -1L);
                    questState.set("man_collect_element", String.valueOf(2), true);
                    string = "magister_nell_q0369_05.htm";
                    break;
                }
                if (n == 2) {
                    string = "magister_nell_q0369_09.htm";
                    break;
                }
                if (n == 3 && (questState.getQuestItemsCount(btc) < 200L || questState.getQuestItemsCount(btb) < 200L)) {
                    string = "magister_nell_q0369_10.htm";
                    break;
                }
                if (n != 3 || questState.getQuestItemsCount(btc) < 200L || questState.getQuestItemsCount(btb) < 200L) break;
                questState.giveItems(57, 76000L);
                questState.takeItems(btb, -1L);
                questState.takeItems(btc, -1L);
                questState.unset("man_collect_element");
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                string = "magister_nell_q0369_11.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getInt("man_collect_element");
        int n2 = npcInstance.getNpcId();
        if (n2 == 20609) {
            if (Rnd.get(100) < 75) {
                questState.giveItems(btb, 1L);
                if (n == 1 && questState.getQuestItemsCount(btc) >= 50L && questState.getQuestItemsCount(btb) >= 49L) {
                    questState.setCond(2);
                    questState.playSound("ItemSound.quest_middle");
                } else if (n == 3 && questState.getQuestItemsCount(btc) >= 200L && questState.getQuestItemsCount(btb) >= 199L) {
                    questState.setCond(4);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 20612) {
            if (Rnd.get(100) < 91) {
                questState.giveItems(btb, 1L);
                if (n == 1 && questState.getQuestItemsCount(btc) >= 50L && questState.getQuestItemsCount(btb) >= 49L) {
                    questState.setCond(2);
                    questState.playSound("ItemSound.quest_middle");
                } else if (n == 3 && questState.getQuestItemsCount(btc) >= 200L && questState.getQuestItemsCount(btb) >= 199L) {
                    questState.setCond(4);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 20616) {
            if (Rnd.get(100) < 80) {
                questState.giveItems(btb, 1L);
                if (n == 1 && questState.getQuestItemsCount(btc) >= 50L && questState.getQuestItemsCount(btb) >= 49L) {
                    questState.setCond(2);
                    questState.playSound("ItemSound.quest_middle");
                } else if (n == 3 && questState.getQuestItemsCount(btc) >= 200L && questState.getQuestItemsCount(btb) >= 199L) {
                    questState.setCond(4);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 20619) {
            if (Rnd.get(100) < 87) {
                questState.giveItems(btb, 1L);
                if (n == 1 && questState.getQuestItemsCount(btc) >= 50L && questState.getQuestItemsCount(btb) >= 49L) {
                    questState.setCond(2);
                    questState.playSound("ItemSound.quest_middle");
                } else if (n == 3 && questState.getQuestItemsCount(btc) >= 200L && questState.getQuestItemsCount(btb) >= 199L) {
                    questState.setCond(4);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n2 == 20747 || n2 == 20749) {
            if (Rnd.get(100) < 2) {
                if (n == 1 && questState.getQuestItemsCount(btc) >= 49L) {
                    questState.giveItems(btc, 1L);
                } else if (n == 3 && questState.getQuestItemsCount(btc) >= 199L) {
                    questState.giveItems(btc, 1L);
                } else {
                    questState.giveItems(btc, 2L);
                }
                if (n == 1 && questState.getQuestItemsCount(btc) >= 49L && questState.getQuestItemsCount(btb) >= 50L) {
                    questState.setCond(2);
                    questState.playSound("ItemSound.quest_middle");
                } else if (n == 3 && questState.getQuestItemsCount(btc) >= 199L && questState.getQuestItemsCount(btb) >= 200L) {
                    questState.setCond(4);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else {
                questState.giveItems(btc, 1L);
                if (n == 1 && questState.getQuestItemsCount(btc) >= 49L && questState.getQuestItemsCount(btb) >= 50L) {
                    questState.setCond(2);
                    questState.playSound("ItemSound.quest_middle");
                } else if (n == 3 && questState.getQuestItemsCount(btc) >= 199L && questState.getQuestItemsCount(btb) >= 200L) {
                    questState.setCond(4);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        }
        return null;
    }
}

