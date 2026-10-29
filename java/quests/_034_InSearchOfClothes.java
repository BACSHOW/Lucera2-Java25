/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _034_InSearchOfClothes
extends Quest
implements ScriptFile {
    private static final int aBC = 30088;
    private static final int aBD = 30165;
    private static final int aBE = 30294;
    private static final int aBF = 20560;
    private static final int aBG = 20561;
    private static final int aBH = 7161;
    private static final int aBI = 7528;
    private static final int aBJ = 7076;
    private static final int aBx = 1868;
    private static final int aBy = 1866;

    public _034_InSearchOfClothes() {
        super(0);
        this.addStartNpc(30088);
        this.addTalkId(30165, 30294);
        this.addKillId(20560, 20561);
        this.addQuestItem(7161, 7528);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = npcInstance.getNpcId();
        int n2 = questState.getInt("in_search_html_cookie");
        if (n == 30088) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("in_search_of_clothes", String.valueOf(11), true);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "radia_q0034_0104.htm";
            } else if (string.equalsIgnoreCase("reply_1") && n2 == 2) {
                string2 = "radia_q0034_0301.htm";
                questState.set("in_search_html_cookie", String.valueOf(2), true);
                questState.set("in_search_of_clothes", String.valueOf(31), true);
                questState.setCond(3);
                questState.playSound("ItemSound.quest_middle");
            } else if (string.equalsIgnoreCase("reply_3") && n2 == 5) {
                questState.set("in_search_html_cookie", String.valueOf(5), true);
                if (questState.getQuestItemsCount(7161) >= 1L && questState.getQuestItemsCount(1868) >= 5000L && questState.getQuestItemsCount(1866) >= 3000L) {
                    questState.takeItems(7161, 1L);
                    questState.takeItems(1868, 5000L);
                    questState.takeItems(1866, 3000L);
                    questState.giveItems(7076, 1L);
                    questState.playSound("ItemSound.quest_finish");
                    string2 = "radia_q0034_0601.htm";
                    questState.exitCurrentQuest(false);
                } else {
                    string2 = "radia_q0034_0602.htm";
                }
            }
        } else if (n == 30165) {
            if (n2 == 3 && string.equalsIgnoreCase("reply_1")) {
                string2 = "rapin_q0034_0401.htm";
                questState.set("in_search_of_clothes", String.valueOf(41), true);
                questState.setCond(4);
                questState.playSound("ItemSound.quest_middle");
            }
            if (n2 == 4 && string.equalsIgnoreCase("reply_1")) {
                if (questState.getQuestItemsCount(7528) >= 10L) {
                    questState.takeItems(7528, 10L);
                    questState.giveItems(7161, 1L);
                    string2 = "rapin_q0034_0501.htm";
                    questState.set("in_search_of_clothes", String.valueOf(51), true);
                    questState.setCond(6);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    string2 = "rapin_q0034_0502.htm";
                }
            }
        } else if (n == 30294 && string.equalsIgnoreCase("reply_1") && n2 == 1) {
            string2 = "trader_varanket_q0034_0201.htm";
            questState.set("in_search_of_clothes", String.valueOf(21), true);
            questState.setCond(2);
            questState.playSound("ItemSound.quest_middle");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("in_search_of_clothes");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 30088) break;
                if (questState.getPlayer().getLevel() >= 60) {
                    string = "radia_q0034_0101.htm";
                    break;
                }
                string = "radia_q0034_0103.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n2 == 30088) {
                    if (n == 11) {
                        string = "radia_q0034_0105.htm";
                    } else if (n == 21) {
                        questState.set("in_search_html_cookie", String.valueOf(2), true);
                        string = "radia_q0034_0201.htm";
                    } else if (n == 31) {
                        string = "radia_q0034_0302.htm";
                    } else if (questState.getQuestItemsCount(7161) >= 1L && n == 51) {
                        questState.set("in_search_html_cookie", String.valueOf(5), true);
                        string = questState.getQuestItemsCount(1866) >= 3000L && questState.getQuestItemsCount(1868) >= 5000L ? "radia_q0034_0501.htm" : "radia_q0034_0502.htm";
                    }
                }
                if (n2 == 30165) {
                    if (n == 31) {
                        questState.set("in_search_html_cookie", String.valueOf(3), true);
                        string = "rapin_q0034_0301.htm";
                    } else if (n <= 42 && n >= 41) {
                        if (n == 42 && questState.getQuestItemsCount(7528) >= 10L) {
                            questState.set("in_search_html_cookie", String.valueOf(4), true);
                            string = "rapin_q0034_0402.htm";
                        } else {
                            string = "rapin_q0034_0403.htm";
                        }
                    } else if (n == 51) {
                        string = "rapin_q0034_0503.htm";
                    }
                }
                if (n2 != 30294) break;
                if (n == 11) {
                    questState.set("in_search_html_cookie", String.valueOf(1), true);
                    string = "trader_varanket_q0034_0101.htm";
                    break;
                }
                if (n != 21) break;
                string = "trader_varanket_q0034_0202.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if ((n == 20560 || n == 20561) && Rnd.get(1000) < 500) {
            if (questState.getQuestItemsCount(7528) + 1L >= 10L) {
                if (questState.getQuestItemsCount(7528) <= 10L) {
                    questState.rollAndGive(7528, (int)(10L - questState.getQuestItemsCount(7528)), 100.0);
                }
                questState.setCond(5);
                questState.set("in_search_of_clothes", String.valueOf(42), true);
            } else {
                questState.giveItems(7528, 1L);
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

