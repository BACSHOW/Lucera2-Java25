/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.Functions;
import l2.gameserver.scripts.ScriptFile;

public class _604_DaimontheWhiteEyedPart2
extends Quest
implements ScriptFile {
    private static final int bJz = 31683;
    private static final int bJV = 31541;
    private static final int bJq = 25290;
    private static final int bJU = 7192;
    private static final int bJW = 7193;
    private static final int bJX = 7194;
    private static final int bJY = 4595;
    private static final int bJZ = 4596;
    private static final int bKa = 4597;
    private static final int bKb = 4598;
    private static final int bKc = 4599;
    private static final int bKd = 4600;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _604_DaimontheWhiteEyedPart2() {
        super(1);
        this.addStartNpc(31683);
        this.addTalkId(31541);
        this.addKillId(25290);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getInt("spawned_daemon_of_hundred_eyes");
        int n2 = questState.getInt("daemon_of_hundred_eyes_second_cookie");
        int n3 = npcInstance.getNpcId();
        if (n3 == 31683) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("daemon_of_hundred_eyes_second", String.valueOf(11), true);
                questState.set("spawned_daemon_of_hundred_eyes", String.valueOf(0), true);
                questState.takeItems(7192, -1L);
                questState.giveItems(7193, 1L);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "eye_of_argos_q0604_0104.htm";
            } else if (string.equalsIgnoreCase("reply_3") && n2 == 2) {
                if (questState.getQuestItemsCount(7194) >= 1L) {
                    int n4 = Rnd.get(1000);
                    questState.takeItems(7194, 1L);
                    if (n4 < 167) {
                        questState.giveItems(4595, 5L);
                    } else if (n4 < 334) {
                        questState.giveItems(4596, 5L);
                    } else if (n4 < 501) {
                        questState.giveItems(4597, 5L);
                    } else if (n4 < 668) {
                        questState.giveItems(4598, 5L);
                    } else if (n4 < 835) {
                        questState.giveItems(4599, 5L);
                    } else if (n4 < 1000) {
                        questState.giveItems(4600, 5L);
                    }
                    questState.unset("daemon_of_hundred_eyes_second");
                    questState.unset("daemon_of_hundred_eyes_second_cookie");
                    questState.playSound("ItemSound.quest_finish");
                    questState.exitCurrentQuest(true);
                    string2 = "eye_of_argos_q0604_0301.htm";
                } else {
                    string2 = "eye_of_argos_q0604_0302.htm";
                }
            }
        } else if (n3 == 31541) {
            if (string.equalsIgnoreCase("60401")) {
                Functions.npcSay(npcInstance, "Can light exist without darkness?");
                questState.set("spawned_daemon_of_hundred_eyes", String.valueOf(0), true);
                if (npcInstance != null) {
                    npcInstance.deleteMe();
                }
                return null;
            }
            if (string.equalsIgnoreCase("reply_1") && n2 == 1) {
                if (questState.getQuestItemsCount(7193) >= 1L) {
                    if (n == 0) {
                        questState.setCond(2);
                        questState.set("daemon_of_hundred_eyes_second", String.valueOf(21), true);
                        questState.takeItems(7193, 1L);
                        string2 = "daimons_altar_q0604_0201.htm";
                        questState.set("spawned_daemon_of_hundred_eyes", String.valueOf(1), true);
                        NpcInstance npcInstance2 = questState.addSpawn(25290, 186320, -43904, -3175);
                        Functions.npcSay(npcInstance2, "Who is calling me?");
                        questState.startQuestTimer("60401", 1200000L, npcInstance2);
                        questState.playSound("ItemSound.quest_middle");
                        string2 = "daimons_altar_q0604_0201.htm";
                    } else {
                        string2 = "daimons_altar_q0604_0202.htm";
                    }
                } else {
                    string2 = "daimons_altar_q0604_0203.htm";
                }
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("daemon_of_hundred_eyes_second");
        int n2 = questState.getInt("spawned_daemon_of_hundred_eyes");
        int n3 = npcInstance.getNpcId();
        int n4 = questState.getState();
        switch (n4) {
            case 1: {
                if (n3 != 31683) break;
                if (questState.getPlayer().getLevel() >= 73) {
                    if (questState.getQuestItemsCount(7192) >= 1L) {
                        string = "eye_of_argos_q0604_0101.htm";
                        break;
                    }
                    string = "eye_of_argos_q0604_0102.htm";
                    questState.exitCurrentQuest(true);
                    break;
                }
                string = "eye_of_argos_q0604_0103.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n3 == 31683) {
                    if (n == 11) {
                        string = "eye_of_argos_q0604_0105.htm";
                        break;
                    }
                    if (n < 22) break;
                    if (questState.getQuestItemsCount(7194) >= 1L) {
                        questState.set("daemon_of_hundred_eyes_second_cookie", String.valueOf(2), true);
                        string = "eye_of_argos_q0604_0201.htm";
                        break;
                    }
                    string = "eye_of_argos_q0604_0202.htm";
                    break;
                }
                if (n3 != 31541) break;
                if (questState.getQuestItemsCount(7193) >= 1L && n == 11) {
                    questState.set("daemon_of_hundred_eyes_second_cookie", String.valueOf(1), true);
                    string = "daimons_altar_q0604_0101.htm";
                    break;
                }
                if (n == 21) {
                    if (n2 == 0) {
                        questState.set("spawned_daemon_of_hundred_eyes", String.valueOf(1), true);
                        NpcInstance npcInstance2 = questState.addSpawn(25290, 186320, -43904, -3175);
                        Functions.npcSay(npcInstance2, "Who is calling me?");
                        questState.startQuestTimer("60401", 1200000L, npcInstance2);
                        string = "daimons_altar_q0604_0201.htm";
                        break;
                    }
                    string = "daimons_altar_q0604_0202.htm";
                    break;
                }
                if (n < 22) break;
                string = "daimons_altar_q0604_0204.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n;
        int n2 = questState.getInt("daemon_of_hundred_eyes_second");
        int n3 = npcInstance.getNpcId();
        if (n2 >= 11 && n2 <= 21 && n3 == 25290 && (n = Rnd.get(1000)) < 1000) {
            if (questState.getQuestItemsCount(7194) + 1L >= 1L) {
                questState.setCond(3);
                questState.set("daemon_of_hundred_eyes_second", String.valueOf(22), true);
                questState.set("spawned_daemon_of_hundred_eyes", String.valueOf(0), true);
                questState.giveItems(7194, 1L - questState.getQuestItemsCount(7194));
                questState.playSound("ItemSound.quest_middle");
            } else {
                questState.giveItems(7194, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

