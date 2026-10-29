/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _355_FamilyHonor
extends Quest
implements ScriptFile {
    private static final int bqS = 30181;
    private static final int bqT = 30929;
    private static final int bqU = 20767;
    private static final int bqV = 20768;
    private static final int bqW = 20769;
    private static final int bqX = 20770;
    private static final int bqY = 4252;
    private static final int bqZ = 4350;
    private static final int bra = 4351;
    private static final int brb = 4352;
    private static final int brc = 4353;
    private static final int brd = 4354;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _355_FamilyHonor() {
        super(0);
        this.addStartNpc(30181);
        this.addTalkId(30929);
        this.addKillId(20767, 20768, 20769, 20770);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = npcInstance.getNpcId();
        if (n == 30181) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "galicbredo_q0355_04.htm";
            } else if (string.equalsIgnoreCase("reply_1")) {
                string2 = "galicbredo_q0355_03.htm";
            } else if (string.equalsIgnoreCase("reply_2")) {
                if (questState.getQuestItemsCount(4252) < 1L) {
                    string2 = "galicbredo_q0355_07.htm";
                } else if (questState.getQuestItemsCount(4252) >= 100L) {
                    long l = 7800L + questState.getQuestItemsCount(4252) * 120L;
                    questState.takeItems(4252, -1L);
                    questState.giveItems(57, l);
                    string2 = "galicbredo_q0355_07b.htm";
                } else if (questState.getQuestItemsCount(4252) >= 1L && questState.getQuestItemsCount(4252) < 100L) {
                    questState.giveItems(57, questState.getQuestItemsCount(4252) * 120L + 2800L);
                    questState.takeItems(4252, -1L);
                    string2 = "galicbredo_q0355_07a.htm";
                }
            } else if (string.equalsIgnoreCase("reply_3")) {
                string2 = "galicbredo_q0355_08.htm";
            } else if (string.equalsIgnoreCase("reply_4")) {
                if (questState.getQuestItemsCount(4252) > 0L) {
                    questState.giveItems(57, questState.getQuestItemsCount(4252) * 120L);
                }
                questState.takeItems(4252, -1L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
                string2 = "galicbredo_q0355_09.htm";
            }
        } else if (n == 30929 && string.equalsIgnoreCase("reply_1")) {
            int n2 = Rnd.get(100);
            if (questState.getQuestItemsCount(4350) < 1L) {
                string2 = "patrin_q0355_02.htm";
            } else if (questState.getQuestItemsCount(4350) >= 1L && n2 < 2) {
                questState.giveItems(4351, 1L);
                questState.takeItems(4350, 1L);
                string2 = "patrin_q0355_03.htm";
            } else if (questState.getQuestItemsCount(4350) >= 1L && n2 < 32) {
                questState.giveItems(4352, 1L);
                questState.takeItems(4350, 1L);
                string2 = "patrin_q0355_04.htm";
            } else if (questState.getQuestItemsCount(4350) >= 1L && n2 < 62) {
                questState.giveItems(4353, 1L);
                questState.takeItems(4350, 1L);
                string2 = "patrin_q0355_05.htm";
            } else if (questState.getQuestItemsCount(4350) >= 1L && n2 < 77) {
                questState.giveItems(4354, 1L);
                questState.takeItems(4350, 1L);
                string2 = "patrin_q0355_06.htm";
            } else {
                questState.takeItems(4350, 1L);
                string2 = "patrin_q0355_07.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getState();
        switch (n2) {
            case 1: {
                if (n != 30181) break;
                if (questState.getPlayer().getLevel() < 36) {
                    questState.exitCurrentQuest(true);
                    string = "galicbredo_q0355_01.htm";
                    break;
                }
                string = "galicbredo_q0355_02.htm";
                break;
            }
            case 2: {
                if (n == 30181) {
                    if (questState.getQuestItemsCount(4350) < 1L) {
                        string = "galicbredo_q0355_05.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(4350) < 1L) break;
                    string = "galicbredo_q0355_06.htm";
                    break;
                }
                if (n != 30929 || n2 != 2) break;
                string = "patrin_q0355_01.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getState();
        if (n2 == 2) {
            if (n == 20767) {
                int n3 = Rnd.get(1000);
                if (n3 < 560) {
                    questState.giveItems(4252, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                } else if (n3 < 684) {
                    questState.giveItems(4350, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n == 20768) {
                int n4 = Rnd.get(100);
                if (n4 < 53) {
                    questState.giveItems(4252, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                } else if (n4 < 65) {
                    questState.giveItems(4350, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n == 20769) {
                int n5 = Rnd.get(1000);
                if (n5 < 420) {
                    questState.giveItems(4252, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                } else if (n5 < 516) {
                    questState.giveItems(4350, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n == 20770) {
                int n6 = Rnd.get(100);
                if (n6 < 44) {
                    questState.giveItems(4252, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                } else if (n6 < 56) {
                    questState.giveItems(4350, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        }
        return null;
    }
}

