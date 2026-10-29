/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _325_GrimCollector
extends Quest
implements ScriptFile {
    private static final int bfP = 30336;
    private static final int bfQ = 30342;
    private static final int bfR = 30434;
    private static final int bfS = 20026;
    private static final int bfT = 20029;
    private static final int bfU = 20035;
    private static final int bfV = 20042;
    private static final int bfW = 20045;
    private static final int bfX = 20457;
    private static final int bfY = 20458;
    private static final int bfZ = 20051;
    private static final int bga = 20514;
    private static final int bgb = 20515;
    private static final int bgc = 1350;
    private static final int bgd = 1351;
    private static final int bge = 1352;
    private static final int bgf = 1353;
    private static final int bgg = 1354;
    private static final int bgh = 1355;
    private static final int bgi = 1356;
    private static final int bgj = 1357;
    private static final int bgk = 1358;
    private static final int bgl = 1349;

    public _325_GrimCollector() {
        super(0);
        this.addStartNpc(30336);
        this.addTalkId(30342, 30434);
        this.addKillId(20026, 20029, 20035, 20042, 20045, 20457, 20458, 20051, 20514, 20515);
        this.addQuestItem(1350, 1351, 1352, 1353, 1354, 1355, 1356, 1357, 1358, 1349);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            questState.set("grim_collector", String.valueOf(1), true);
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            string2 = "guard_curtiz_q0325_03.htm";
        } else if (string.equalsIgnoreCase("reply_1")) {
            string2 = "samed_q0325_02.htm";
        } else if (string.equalsIgnoreCase("reply_2")) {
            string2 = "samed_q0325_03.htm";
            questState.giveItems(1349, 1L);
        } else if (string.equalsIgnoreCase("reply_3")) {
            string2 = "samed_q0325_06.htm";
            if (questState.getQuestItemsCount(1350) + questState.getQuestItemsCount(1355) + questState.getQuestItemsCount(1356) + questState.getQuestItemsCount(1351) + questState.getQuestItemsCount(1352) + questState.getQuestItemsCount(1353) + questState.getQuestItemsCount(1354) + questState.getQuestItemsCount(1357) + questState.getQuestItemsCount(1358) > 0L) {
                if (questState.getQuestItemsCount(1350) + questState.getQuestItemsCount(1355) + questState.getQuestItemsCount(1356) + questState.getQuestItemsCount(1351) + questState.getQuestItemsCount(1352) + questState.getQuestItemsCount(1353) + questState.getQuestItemsCount(1354) + questState.getQuestItemsCount(1357) + questState.getQuestItemsCount(1358) >= 10L) {
                    if (questState.getQuestItemsCount(1358) >= 1L) {
                        questState.giveItems(57, 2172L + 30L * questState.getQuestItemsCount(1350) + 20L * questState.getQuestItemsCount(1351) + 20L * questState.getQuestItemsCount(1352) + 100L * questState.getQuestItemsCount(1353) + 40L * questState.getQuestItemsCount(1354) + 14L * questState.getQuestItemsCount(1355) + 14L * questState.getQuestItemsCount(1356) + 14L * questState.getQuestItemsCount(1357) + 341L * questState.getQuestItemsCount(1358));
                    } else {
                        questState.giveItems(57, 1629L + 30L * questState.getQuestItemsCount(1350) + 20L * questState.getQuestItemsCount(1351) + 20L * questState.getQuestItemsCount(1352) + 100L * questState.getQuestItemsCount(1353) + 40L * questState.getQuestItemsCount(1354) + 14L * questState.getQuestItemsCount(1355) + 14L * questState.getQuestItemsCount(1356) + 14L * questState.getQuestItemsCount(1357) + 341L * questState.getQuestItemsCount(1358));
                    }
                } else if (questState.getQuestItemsCount(1358) >= 1L) {
                    questState.giveItems(57, 543L + 30L * questState.getQuestItemsCount(1350) + 20L * questState.getQuestItemsCount(1351) + 20L * questState.getQuestItemsCount(1352) + 100L * questState.getQuestItemsCount(1353) + 40L * questState.getQuestItemsCount(1354) + 14L * questState.getQuestItemsCount(1355) + 14L * questState.getQuestItemsCount(1356) + 14L * questState.getQuestItemsCount(1357) + 341L * questState.getQuestItemsCount(1358));
                } else {
                    questState.giveItems(57, 30L * questState.getQuestItemsCount(1350) + 20L * questState.getQuestItemsCount(1351) + 20L * questState.getQuestItemsCount(1352) + 100L * questState.getQuestItemsCount(1353) + 40L * questState.getQuestItemsCount(1354) + 14L * questState.getQuestItemsCount(1355) + 14L * questState.getQuestItemsCount(1356) + 14L * questState.getQuestItemsCount(1357) + 341L * questState.getQuestItemsCount(1358));
                }
            }
            questState.takeItems(1350, -1L);
            questState.takeItems(1351, -1L);
            questState.takeItems(1352, -1L);
            questState.takeItems(1353, -1L);
            questState.takeItems(1354, -1L);
            questState.takeItems(1355, -1L);
            questState.takeItems(1356, -1L);
            questState.takeItems(1357, -1L);
            questState.takeItems(1358, -1L);
            questState.takeItems(1349, -1L);
            questState.unset("grim_collector");
            questState.exitCurrentQuest(true);
            questState.playSound("ItemSound.quest_finish");
        } else if (string.equalsIgnoreCase("reply_4")) {
            string2 = "samed_q0325_07.htm";
            if (questState.getQuestItemsCount(1350) + questState.getQuestItemsCount(1355) + questState.getQuestItemsCount(1356) + questState.getQuestItemsCount(1351) + questState.getQuestItemsCount(1352) + questState.getQuestItemsCount(1353) + questState.getQuestItemsCount(1354) + questState.getQuestItemsCount(1357) + questState.getQuestItemsCount(1358) > 0L) {
                if (questState.getQuestItemsCount(1350) + questState.getQuestItemsCount(1355) + questState.getQuestItemsCount(1356) + questState.getQuestItemsCount(1351) + questState.getQuestItemsCount(1352) + questState.getQuestItemsCount(1353) + questState.getQuestItemsCount(1354) + questState.getQuestItemsCount(1357) + questState.getQuestItemsCount(1358) >= 10L) {
                    if (questState.getQuestItemsCount(1358) >= 1L) {
                        questState.giveItems(57, 2172L + 30L * questState.getQuestItemsCount(1350) + 20L * questState.getQuestItemsCount(1351) + 20L * questState.getQuestItemsCount(1352) + 100L * questState.getQuestItemsCount(1353) + 40L * questState.getQuestItemsCount(1354) + 14L * questState.getQuestItemsCount(1355) + 14L * questState.getQuestItemsCount(1356) + 14L * questState.getQuestItemsCount(1357) + 341L * questState.getQuestItemsCount(1358));
                    } else {
                        questState.giveItems(57, 1629L + 30L * questState.getQuestItemsCount(1350) + 20L * questState.getQuestItemsCount(1351) + 20L * questState.getQuestItemsCount(1352) + 100L * questState.getQuestItemsCount(1353) + 40L * questState.getQuestItemsCount(1354) + 14L * questState.getQuestItemsCount(1355) + 14L * questState.getQuestItemsCount(1356) + 14L * questState.getQuestItemsCount(1357) + 341L * questState.getQuestItemsCount(1358));
                    }
                } else if (questState.getQuestItemsCount(1358) >= 1L) {
                    questState.giveItems(57, 543L + 30L * questState.getQuestItemsCount(1350) + 20L * questState.getQuestItemsCount(1351) + 20L * questState.getQuestItemsCount(1352) + 100L * questState.getQuestItemsCount(1353) + 40L * questState.getQuestItemsCount(1354) + 14L * questState.getQuestItemsCount(1355) + 14L * questState.getQuestItemsCount(1356) + 14L * questState.getQuestItemsCount(1357) + 341L * questState.getQuestItemsCount(1358));
                } else {
                    questState.giveItems(57, 30L * questState.getQuestItemsCount(1350) + 20L * questState.getQuestItemsCount(1351) + 20L * questState.getQuestItemsCount(1352) + 100L * questState.getQuestItemsCount(1353) + 40L * questState.getQuestItemsCount(1354) + 14L * questState.getQuestItemsCount(1355) + 14L * questState.getQuestItemsCount(1356) + 14L * questState.getQuestItemsCount(1357) + 341L * questState.getQuestItemsCount(1358));
                }
            }
            questState.takeItems(1350, -1L);
            questState.takeItems(1351, -1L);
            questState.takeItems(1352, -1L);
            questState.takeItems(1353, -1L);
            questState.takeItems(1354, -1L);
            questState.takeItems(1355, -1L);
            questState.takeItems(1356, -1L);
            questState.takeItems(1357, -1L);
            questState.takeItems(1358, -1L);
        } else if (string.equalsIgnoreCase("reply_5")) {
            string2 = "samed_q0325_09.htm";
            if (questState.getQuestItemsCount(1358) > 0L) {
                questState.giveItems(57, 543L + 341L * questState.getQuestItemsCount(1358));
            }
            questState.takeItems(1358, -1L);
        } else if (string.equalsIgnoreCase("reply_6")) {
            if (questState.getQuestItemsCount(1355) > 0L && questState.getQuestItemsCount(1356) > 0L && questState.getQuestItemsCount(1353) > 0L && questState.getQuestItemsCount(1354) > 0L && questState.getQuestItemsCount(1357) > 0L) {
                if (Rnd.get(5) < 4) {
                    string2 = "varsak_q0325_03.htm";
                    questState.takeItems(1355, 1L);
                    questState.takeItems(1353, 1L);
                    questState.takeItems(1356, 1L);
                    questState.takeItems(1354, 1L);
                    questState.takeItems(1357, 1L);
                    questState.giveItems(1358, 1L);
                } else {
                    questState.takeItems(1355, 1L);
                    questState.takeItems(1353, 1L);
                    questState.takeItems(1356, 1L);
                    questState.takeItems(1354, 1L);
                    questState.takeItems(1357, 1L);
                    string2 = "varsak_q0325_04.htm";
                }
            } else {
                string2 = "varsak_q0325_02.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getState();
        int n3 = questState.getInt("grim_collector");
        switch (n2) {
            case 1: {
                if (n != 30336) break;
                if (questState.getPlayer().getLevel() >= 15) {
                    string = "guard_curtiz_q0325_02.htm";
                    break;
                }
                string = "guard_curtiz_q0325_01.htm";
                break;
            }
            case 2: {
                if (n == 30336) {
                    if (n3 == 1 && questState.getQuestItemsCount(1349) == 0L) {
                        string = "guard_curtiz_q0325_04.htm";
                        break;
                    }
                    if (n3 != 1 || questState.getQuestItemsCount(1349) != 1L) break;
                    string = "guard_curtiz_q0325_05.htm";
                    break;
                }
                if (n == 30342) {
                    if (n3 != 1 || questState.getQuestItemsCount(1349) != 1L) break;
                    string = "varsak_q0325_01.htm";
                    break;
                }
                if (n != 30434) break;
                if (n3 == 1 && questState.getQuestItemsCount(1349) == 0L) {
                    string = "samed_q0325_01.htm";
                    break;
                }
                if (n3 == 1 && questState.getQuestItemsCount(1349) == 1L && questState.getQuestItemsCount(1350) + questState.getQuestItemsCount(1355) + questState.getQuestItemsCount(1356) + questState.getQuestItemsCount(1351) + questState.getQuestItemsCount(1352) + questState.getQuestItemsCount(1353) + questState.getQuestItemsCount(1354) + questState.getQuestItemsCount(1357) + questState.getQuestItemsCount(1358) < 1L) {
                    string = "samed_q0325_04.htm";
                    break;
                }
                if (n3 == 1 && questState.getQuestItemsCount(1358) == 0L && questState.getQuestItemsCount(1349) == 1L && questState.getQuestItemsCount(1350) + questState.getQuestItemsCount(1355) + questState.getQuestItemsCount(1356) + questState.getQuestItemsCount(1351) + questState.getQuestItemsCount(1352) + questState.getQuestItemsCount(1353) + questState.getQuestItemsCount(1354) + questState.getQuestItemsCount(1357) > 0L) {
                    string = "samed_q0325_05.htm";
                    break;
                }
                if (n3 != 1 || questState.getQuestItemsCount(1349) != 1L || questState.getQuestItemsCount(1358) <= 0L) break;
                string = "samed_q0325_08.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = Rnd.get(100);
        if (n == 20026 || n == 20029 && questState.getQuestItemsCount(1349) > 0L) {
            if (n2 < 30) {
                questState.rollAndGive(1350, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 50) {
                questState.rollAndGive(1351, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 75) {
                questState.rollAndGive(1352, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20035 && questState.getQuestItemsCount(1349) > 0L) {
            if (n2 < 5) {
                questState.rollAndGive(1353, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 15) {
                questState.rollAndGive(1354, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 29) {
                questState.rollAndGive(1355, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 79) {
                questState.rollAndGive(1357, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20042 && questState.getQuestItemsCount(1349) > 0L) {
            if (n2 < 6) {
                questState.rollAndGive(1353, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 19) {
                questState.rollAndGive(1354, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 69) {
                questState.rollAndGive(1356, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 86) {
                questState.rollAndGive(1357, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20045 || n == 20457 && questState.getQuestItemsCount(1349) > 0L) {
            if (n2 < 40) {
                questState.rollAndGive(1350, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 60) {
                questState.rollAndGive(1351, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 80) {
                questState.rollAndGive(1352, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20458 && questState.getQuestItemsCount(1349) > 0L) {
            if (n2 < 40) {
                questState.rollAndGive(1350, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 70) {
                questState.rollAndGive(1351, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 100) {
                questState.rollAndGive(1352, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20051 && questState.getQuestItemsCount(1349) > 0L) {
            if (n2 < 9) {
                questState.rollAndGive(1353, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 59) {
                questState.rollAndGive(1354, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 79) {
                questState.rollAndGive(1355, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 100) {
                questState.rollAndGive(1356, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20514 && questState.getQuestItemsCount(1349) > 0L) {
            if (n2 < 6) {
                questState.rollAndGive(1353, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 21) {
                questState.rollAndGive(1354, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 30) {
                questState.rollAndGive(1355, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 31) {
                questState.rollAndGive(1356, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 64) {
                questState.rollAndGive(1357, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20515 && questState.getQuestItemsCount(1349) > 0L) {
            if (n2 < 5) {
                questState.rollAndGive(1353, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 20) {
                questState.rollAndGive(1354, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 31) {
                questState.rollAndGive(1355, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 33) {
                questState.rollAndGive(1356, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            } else if (n2 < 69) {
                questState.rollAndGive(1357, 1, 100.0);
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

