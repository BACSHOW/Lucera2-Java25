/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _603_DaimontheWhiteEyedPart1
extends Quest
implements ScriptFile {
    private static final int bJz = 31683;
    private static final int bJM = 31548;
    private static final int bJN = 31549;
    private static final int bJO = 31550;
    private static final int bJP = 31551;
    private static final int bJQ = 31552;
    private static final int bJJ = 21299;
    private static final int bJR = 21297;
    private static final int bJK = 21304;
    private static final int bJS = 7190;
    private static final int bJT = 7191;
    private static final int bJU = 7192;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _603_DaimontheWhiteEyedPart1() {
        super(1);
        this.addStartNpc(31683);
        this.addTalkId(31548, 31549, 31550, 31551, 31552);
        this.addKillId(21299, 21297, 21304);
        this.addQuestItem(7190);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = npcInstance.getNpcId();
        if (n == 31683) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("daemon_of_hundred", String.valueOf(11), true);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "eye_of_argos_q0603_0104.htm";
            } else if (string.equalsIgnoreCase("reply_1")) {
                if (questState.getQuestItemsCount(7191) >= 5L) {
                    questState.setCond(7);
                    questState.set("daemon_of_hundred", String.valueOf(71), true);
                    questState.takeItems(7191, 5L);
                    questState.playSound("ItemSound.quest_middle");
                    string2 = "eye_of_argos_q0603_0701.htm";
                } else {
                    string2 = "eye_of_argos_q0603_0702.htm";
                }
            } else if (string.equalsIgnoreCase("reply_3")) {
                if (questState.getQuestItemsCount(7190) >= 200L) {
                    questState.takeItems(7190, -1L);
                    questState.giveItems(7192, 1L);
                    questState.unset("daemon_of_hundred");
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(false);
                    string2 = "eye_of_argos_q0603_0801.htm";
                } else {
                    string2 = "eye_of_argos_q0603_0802.htm";
                }
            }
        } else if (n == 31548) {
            if (string.equalsIgnoreCase("reply_1")) {
                questState.setCond(2);
                questState.set("daemon_of_hundred", String.valueOf(21), true);
                questState.giveItems(7191, 1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "ancient_lithography1_q0603_0201.htm";
            }
        } else if (n == 31549) {
            if (string.equalsIgnoreCase("reply_1")) {
                questState.setCond(3);
                questState.set("daemon_of_hundred", String.valueOf(31), true);
                questState.giveItems(7191, 1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "ancient_lithography2_q0603_0301.htm";
            }
        } else if (n == 31550) {
            if (string.equalsIgnoreCase("reply_1")) {
                questState.setCond(4);
                questState.set("daemon_of_hundred", String.valueOf(41), true);
                questState.giveItems(7191, 1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "ancient_lithography3_q0603_0401.htm";
            }
        } else if (n == 31551) {
            if (string.equalsIgnoreCase("reply_1")) {
                questState.setCond(5);
                questState.set("daemon_of_hundred", String.valueOf(51), true);
                questState.giveItems(7191, 1L);
                questState.playSound("ItemSound.quest_middle");
                string2 = "ancient_lithography4_q0603_0501.htm";
            }
        } else if (n == 31552 && string.equalsIgnoreCase("reply_1")) {
            questState.setCond(6);
            questState.set("daemon_of_hundred", String.valueOf(61), true);
            questState.giveItems(7191, 1L);
            questState.playSound("ItemSound.quest_middle");
            string2 = "ancient_lithography5_q0603_0601.htm";
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("daemon_of_hundred");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 31683) break;
                if (questState.getPlayer().getLevel() >= 73) {
                    string = "eye_of_argos_q0603_0101.htm";
                    break;
                }
                string = "eye_of_argos_q0603_0103.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n2 == 31683) {
                    if (n == 11) {
                        string = "eye_of_argos_q0603_0105.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(7191) >= 1L && n == 61) {
                        string = "eye_of_argos_q0603_0601.htm";
                        break;
                    }
                    if (n > 72 || n < 71) break;
                    if (n == 72 && questState.getQuestItemsCount(7190) >= 200L) {
                        string = "eye_of_argos_q0603_0703.htm";
                        break;
                    }
                    string = "eye_of_argos_q0603_0704.htm";
                    break;
                }
                if (n2 == 31548) {
                    if (n == 11) {
                        string = "ancient_lithography1_q0603_0101.htm";
                        break;
                    }
                    if (n != 21) break;
                    string = "ancient_lithography1_q0603_0203.htm";
                    break;
                }
                if (n2 == 31549) {
                    if (questState.getQuestItemsCount(7191) >= 1L && n == 21) {
                        string = "ancient_lithography2_q0603_0201.htm";
                        break;
                    }
                    if (n != 31) break;
                    string = "ancient_lithography2_q0603_0303.htm";
                    break;
                }
                if (n2 == 31550) {
                    if (questState.getQuestItemsCount(7191) >= 1L && n == 31) {
                        string = "ancient_lithography3_q0603_0301.htm";
                        break;
                    }
                    if (n != 41) break;
                    string = "ancient_lithography3_q0603_0403.htm";
                    break;
                }
                if (n2 == 31551) {
                    if (questState.getQuestItemsCount(7191) >= 1L && n == 41) {
                        string = "ancient_lithography4_q0603_0401.htm";
                        break;
                    }
                    if (n != 51) break;
                    string = "ancient_lithography4_q0603_0503.htm";
                    break;
                }
                if (n2 != 31552) break;
                if (questState.getQuestItemsCount(7191) >= 1L && n == 51) {
                    string = "ancient_lithography5_q0603_0501.htm";
                    break;
                }
                if (n != 61) break;
                string = "ancient_lithography5_q0603_0603.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getInt("daemon_of_hundred");
        int n2 = npcInstance.getNpcId();
        if (n == 71) {
            int n3;
            if (n2 == 21299) {
                int n4 = Rnd.get(1000);
                if (n4 < 519) {
                    if (questState.getQuestItemsCount(7190) + 1L >= 200L) {
                        if (questState.getQuestItemsCount(7190) < 200L) {
                            questState.setCond(8);
                            questState.set("daemon_of_hundred", String.valueOf(72), true);
                            questState.giveItems(7190, 200L - questState.getQuestItemsCount(7190));
                            questState.playSound("ItemSound.quest_middle");
                        }
                    } else {
                        questState.giveItems(7190, 1L);
                        questState.playSound("ItemSound.quest_itemget");
                    }
                }
            } else if (n2 == 21297) {
                int n5 = Rnd.get(1000);
                if (n5 < 500) {
                    if (questState.getQuestItemsCount(7190) + 1L >= 200L) {
                        if (questState.getQuestItemsCount(7190) < 200L) {
                            questState.setCond(8);
                            questState.set("daemon_of_hundred", String.valueOf(72), true);
                            questState.giveItems(7190, 200L - questState.getQuestItemsCount(7190));
                            questState.playSound("ItemSound.quest_middle");
                        }
                    } else {
                        questState.giveItems(7190, 1L);
                        questState.playSound("ItemSound.quest_itemget");
                    }
                }
            } else if (n2 == 21304 && (n3 = Rnd.get(1000)) < 673) {
                if (questState.getQuestItemsCount(7190) + 1L >= 200L) {
                    if (questState.getQuestItemsCount(7190) < 200L) {
                        questState.setCond(8);
                        questState.set("daemon_of_hundred", String.valueOf(72), true);
                        questState.giveItems(7190, 200L - questState.getQuestItemsCount(7190));
                        questState.playSound("ItemSound.quest_middle");
                    }
                } else {
                    questState.giveItems(7190, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        }
        return null;
    }
}

