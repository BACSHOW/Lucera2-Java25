/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _032_AnObviousLie
extends Quest
implements ScriptFile {
    private static final int aBq = 30120;
    private static final int aBr = 30094;
    private static final int aBs = 31706;
    private static final int aBt = 20135;
    private static final int aBu = 7165;
    private static final int aBv = 7166;
    private static final int aBw = 3031;
    private static final int aBx = 1868;
    private static final int aBy = 1866;
    private static final int aBz = 7680;
    private static final int aBA = 6843;
    private static final int aBB = 7683;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _032_AnObviousLie() {
        super(1);
        this.addStartNpc(30120);
        this.addTalkId(30120, 30094, 31706);
        this.addKillId(20135);
        this.addQuestItem(7166, 7165);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getInt("blatant_lie_cookie");
        int n2 = npcInstance.getNpcId();
        if (n2 == 30120) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("blatant_lie", String.valueOf(11), true);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "maximilian_q0032_0104.htm";
            }
        } else if (n2 == 30094) {
            if (string.equalsIgnoreCase("reply_1") && n == 1) {
                questState.setCond(2);
                questState.set("blatant_lie", String.valueOf(21), true);
                questState.giveItems(7165, 1L);
                string2 = "gentler_q0032_0201.htm";
                questState.playSound("ItemSound.quest_middle");
            } else if (string.equalsIgnoreCase("reply_1") && n == 3) {
                if (questState.getQuestItemsCount(7166) >= 20L) {
                    questState.setCond(5);
                    questState.set("blatant_lie", String.valueOf(41), true);
                    questState.takeItems(7166, 20L);
                    string2 = "gentler_q0032_0401.htm";
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    string2 = "gentler_q0032_0402.htm";
                }
            } else if (string.equalsIgnoreCase("reply_1") && n == 4) {
                if (questState.getQuestItemsCount(3031) >= 500L) {
                    questState.setCond(6);
                    questState.set("blatant_lie", String.valueOf(51), true);
                    questState.takeItems(3031, 500L);
                    string2 = "gentler_q0032_0501.htm";
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    string2 = "gentler_q0032_0502.htm";
                }
            } else if (string.equalsIgnoreCase("reply_1") && n == 6) {
                questState.setCond(8);
                questState.set("blatant_lie", String.valueOf(71), true);
                string2 = "gentler_q0032_0701.htm";
                questState.playSound("ItemSound.quest_middle");
            } else if (string.equalsIgnoreCase("reply_3") && n == 7) {
                string2 = "gentler_q0032_0801.htm";
            } else if (string.equalsIgnoreCase("reply_11") && n == 7) {
                if (questState.getQuestItemsCount(1868) >= 1000L && questState.getQuestItemsCount(1866) >= 500L) {
                    questState.takeItems(1868, 1000L);
                    questState.takeItems(1866, 500L);
                    questState.giveItems(6843, 1L);
                    questState.unset("blatant_lie");
                    questState.unset("blatant_lie_cookie");
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(false);
                    string2 = "gentler_q0032_0802.htm";
                } else {
                    string2 = "gentler_q0032_0803.htm";
                }
            } else if (string.equalsIgnoreCase("reply_12") && n == 7) {
                if (questState.getQuestItemsCount(1868) >= 1000L && questState.getQuestItemsCount(1866) >= 500L) {
                    questState.takeItems(1868, 1000L);
                    questState.takeItems(1866, 500L);
                    questState.giveItems(7680, 1L);
                    questState.unset("blatant_lie");
                    questState.unset("blatant_lie_cookie");
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(false);
                    string2 = "gentler_q0032_0802.htm";
                } else {
                    string2 = "gentler_q0032_0803.htm";
                }
            } else if (string.equalsIgnoreCase("reply_13") && n == 7) {
                if (questState.getQuestItemsCount(1868) >= 1000L && questState.getQuestItemsCount(1866) >= 500L) {
                    questState.takeItems(1868, 1000L);
                    questState.takeItems(1866, 500L);
                    questState.giveItems(7683, 1L);
                    questState.unset("blatant_lie");
                    questState.unset("blatant_lie_cookie");
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(false);
                    string2 = "gentler_q0032_0802.htm";
                } else {
                    string2 = "gentler_q0032_0803.htm";
                }
            }
        } else if (n2 == 31706) {
            if (string.equalsIgnoreCase("reply_1") && n == 2) {
                questState.setCond(3);
                questState.set("blatant_lie", String.valueOf(31), true);
                questState.takeItems(7165, 1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "miki_the_cat_q0032_0301.htm";
            } else if (string.equalsIgnoreCase("reply_1") && n == 5) {
                questState.setCond(7);
                questState.set("blatant_lie", String.valueOf(61), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "miki_the_cat_q0032_0601.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("blatant_lie");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 30120) break;
                if (questState.getPlayer().getLevel() >= 45) {
                    string = "maximilian_q0032_0101.htm";
                    break;
                }
                questState.exitCurrentQuest(true);
                string = "maximilian_q0032_0103.htm";
                break;
            }
            case 2: {
                if (n2 == 30120) {
                    if (n != 11) break;
                    string = "maximilian_q0032_0105.htm";
                    break;
                }
                if (n2 == 30094) {
                    if (n == 11) {
                        questState.set("blatant_lie_cookie", String.valueOf(1), true);
                        string = "gentler_q0032_0101.htm";
                        break;
                    }
                    if (n == 21) {
                        string = "gentler_q0032_0202.htm";
                        break;
                    }
                    if (n == 32) {
                        if (questState.getQuestItemsCount(7166) >= 20L) {
                            questState.set("blatant_lie_cookie", String.valueOf(3), true);
                            string = "gentler_q0032_0301.htm";
                            break;
                        }
                        string = "gentler_q0032_0302.htm";
                        break;
                    }
                    if (n == 41) {
                        if (questState.getQuestItemsCount(3031) >= 500L) {
                            questState.set("blatant_lie_cookie", String.valueOf(4), true);
                            string = "gentler_q0032_0403.htm";
                            break;
                        }
                        string = "gentler_q0032_0404.htm";
                        break;
                    }
                    if (n == 51) {
                        string = "gentler_q0032_0503.htm";
                        break;
                    }
                    if (n == 61) {
                        questState.set("blatant_lie_cookie", String.valueOf(6), true);
                        string = "gentler_q0032_0601.htm";
                        break;
                    }
                    if (n != 71) break;
                    if (questState.getQuestItemsCount(1868) >= 1000L && questState.getQuestItemsCount(1866) >= 500L) {
                        questState.set("blatant_lie_cookie", String.valueOf(7), true);
                        string = "gentler_q0032_0702.htm";
                        break;
                    }
                    string = "gentler_q0032_0703.htm";
                    break;
                }
                if (n2 != 31706) break;
                if (questState.getQuestItemsCount(7165) >= 1L && n == 21) {
                    questState.set("blatant_lie_cookie", String.valueOf(2), true);
                    string = "miki_the_cat_q0032_0201.htm";
                    break;
                }
                if (n == 31) {
                    string = "miki_the_cat_q0032_0303.htm";
                    break;
                }
                if (n == 51) {
                    questState.set("blatant_lie_cookie", String.valueOf(5), true);
                    string = "miki_the_cat_q0032_0501.htm";
                    break;
                }
                if (n != 61) break;
                string = "miki_the_cat_q0032_0602.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n;
        int n2 = questState.getInt("blatant_lie");
        int n3 = npcInstance.getNpcId();
        if (n2 == 31 && n3 == 20135 && (n = Rnd.get(500)) < 500) {
            if (questState.getQuestItemsCount(7166) + 1L >= 20L) {
                if (questState.getQuestItemsCount(7166) <= 20L) {
                    questState.giveItems(7166, 20L - questState.getQuestItemsCount(7166));
                    questState.playSound("ItemSound.quest_middle");
                }
                questState.setCond(4);
                questState.set("blatant_lie", String.valueOf(32), true);
            } else {
                questState.giveItems(7166, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

