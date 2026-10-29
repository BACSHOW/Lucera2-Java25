/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _415_PathToOrcMonk
extends Quest
implements ScriptFile {
    private static final int bzm = 30587;
    private static final int bzn = 30590;
    private static final int aPA = 30501;
    private static final int bzo = 30591;
    private static final int bzp = 1593;
    private static final int bzq = 1600;
    private static final int bzr = 1601;
    private static final int bzs = 1602;
    private static final int bzt = 1594;
    private static final int bzu = 1597;
    private static final int bzv = 1595;
    private static final int bzw = 1598;
    private static final int bzx = 1596;
    private static final int bzy = 1599;
    private static final int bzz = 1607;
    private static final int bzA = 1608;
    private static final int bzB = 1603;
    private static final int bzC = 1604;
    private static final int bzD = 1605;
    private static final int bzE = 1606;
    private static final int bzF = 1609;
    private static final int bzG = 1610;
    private static final int bzH = 1611;
    private static final int bzI = 1612;
    private static final int bzJ = 1613;
    private static final int bzK = 1614;
    private static final int bzL = 1615;
    private static final int bzM = 20479;
    private static final int bzN = 20478;
    private static final int bzO = 20415;
    private static final int bzP = 20017;
    private static final int bzQ = 20359;
    private static final int bzR = 20024;
    private static final int bzS = 20014;
    private static final int[][] DROPLIST_COND = new int[][]{{2, 3, 20479, 1594, 1600, 5, 70, 1}, {4, 5, 20478, 1595, 1601, 5, 70, 1}, {6, 7, 20415, 1596, 1602, 5, 70, 1}, {11, 0, 20017, 1607, 1609, 3, 70, 1}, {11, 0, 20359, 1607, 1610, 3, 70, 1}, {11, 0, 20024, 1607, 1611, 3, 70, 1}, {11, 0, 20014, 1607, 1612, 3, 70, 1}};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _415_PathToOrcMonk() {
        super(0);
        this.addStartNpc(30587);
        this.addTalkId(30590);
        this.addTalkId(30501);
        this.addTalkId(30591);
        for (int i = 0; i < DROPLIST_COND.length; ++i) {
            this.addKillId(DROPLIST_COND[i][2]);
            this.addQuestItem(DROPLIST_COND[i][4]);
        }
        this.addQuestItem(1593, 1594, 1597, 1595, 1598, 1596, 1599, 1606, 1603, 1604, 1605, 1607, 1608, 1613, 1614);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("gantaki_zu_urutu_q0415_06.htm")) {
            questState.giveItems(1593, 1L);
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getCond();
        if (n == 30587) {
            if (questState.getQuestItemsCount(1615) != 0L) {
                string = "gantaki_zu_urutu_q0415_04.htm";
                questState.exitCurrentQuest(true);
            } else if (n2 == 0) {
                if (questState.getPlayer().getClassId().getId() != 44) {
                    string = questState.getPlayer().getClassId().getId() == 47 ? "gantaki_zu_urutu_q0415_02a.htm" : "gantaki_zu_urutu_q0415_02.htm";
                    questState.exitCurrentQuest(true);
                } else if (questState.getPlayer().getLevel() < 18) {
                    string = "gantaki_zu_urutu_q0415_03.htm";
                    questState.exitCurrentQuest(true);
                } else {
                    string = "gantaki_zu_urutu_q0415_01.htm";
                }
            } else if (n2 == 1) {
                string = "gantaki_zu_urutu_q0415_07.htm";
            } else if (n2 >= 2 && n2 <= 7) {
                string = "gantaki_zu_urutu_q0415_08.htm";
            } else if (n2 == 8) {
                questState.takeItems(1604, 1L);
                questState.giveItems(1605, 1L);
                string = "gantaki_zu_urutu_q0415_09.htm";
                questState.setCond(9);
                questState.setState(2);
            } else if (n2 == 9) {
                string = "gantaki_zu_urutu_q0415_10.htm";
            } else if (n2 >= 10) {
                string = "gantaki_zu_urutu_q0415_11.htm";
            }
        } else if (n == 30590) {
            if (n2 == 1) {
                questState.takeItems(1593, -1L);
                questState.giveItems(1594, 1L);
                string = "khavatari_rosheek_q0415_01.htm";
                questState.setCond(2);
                questState.setState(2);
            } else if (n2 == 2) {
                string = "khavatari_rosheek_q0415_02.htm";
            } else if (n2 == 3) {
                string = "khavatari_rosheek_q0415_03.htm";
                questState.takeItems(1597, -1L);
                questState.giveItems(1595, 1L);
                questState.setCond(4);
                questState.setState(2);
            } else if (n2 == 4) {
                string = "khavatari_rosheek_q0415_04.htm";
            } else if (n2 == 5) {
                questState.takeItems(1598, -1L);
                questState.giveItems(1596, 1L);
                string = "khavatari_rosheek_q0415_05.htm";
                questState.setCond(6);
                questState.setState(2);
            } else if (n2 == 6) {
                string = "khavatari_rosheek_q0415_06.htm";
            } else if (n2 == 7) {
                questState.takeItems(1599, -1L);
                questState.giveItems(1603, 1L);
                questState.giveItems(1604, 1L);
                string = "khavatari_rosheek_q0415_07.htm";
                questState.setCond(8);
                questState.setState(2);
            } else if (n2 == 8) {
                string = "khavatari_rosheek_q0415_08.htm";
            } else if (n2 == 9) {
                string = "khavatari_rosheek_q0415_09.htm";
            }
        } else if (n == 30501) {
            if (n2 == 9) {
                questState.takeItems(1605, -1L);
                questState.giveItems(1606, 1L);
                string = "prefect_kasman_q0415_01.htm";
                questState.setCond(10);
                questState.setState(2);
            } else if (n2 == 10) {
                string = "prefect_kasman_q0415_02.htm";
            } else if (n2 == 11 || n2 == 12) {
                string = "prefect_kasman_q0415_03.htm";
            } else if (n2 == 13) {
                questState.takeItems(1603, -1L);
                questState.takeItems(1613, -1L);
                questState.takeItems(1614, -1L);
                string = "prefect_kasman_q0415_04.htm";
                if (questState.getPlayer().getClassId().getLevel() == 1) {
                    questState.giveItems(1615, 1L);
                    if (!questState.getPlayer().getVarB("prof1")) {
                        questState.getPlayer().setVar("prof1", "1", -1L);
                        questState.addExpAndSp(3200L, 3380L);
                    }
                }
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            }
        } else if (n == 30591) {
            if (n2 == 10) {
                questState.takeItems(1606, -1L);
                questState.giveItems(1607, 1L);
                string = "khavatari_toruku_q0415_01.htm";
                questState.setCond(11);
                questState.setState(2);
            } else if (n2 == 11) {
                string = "khavatari_toruku_q0415_02.htm";
            } else if (n2 == 12) {
                questState.takeItems(1608, -1L);
                questState.giveItems(1613, 1L);
                questState.giveItems(1614, 1L);
                string = "khavatari_toruku_q0415_03.htm";
                questState.setCond(13);
                questState.setState(2);
            } else if (n2 == 13) {
                string = "khavatari_toruku_q0415_04.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        for (int i = 0; i < DROPLIST_COND.length; ++i) {
            if (n2 != DROPLIST_COND[i][0] || n != DROPLIST_COND[i][2] || DROPLIST_COND[i][3] != 0 && questState.getQuestItemsCount(DROPLIST_COND[i][3]) <= 0L) continue;
            if (DROPLIST_COND[i][5] == 0) {
                questState.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][6]);
                continue;
            }
            if (!questState.rollAndGive(DROPLIST_COND[i][4], DROPLIST_COND[i][7], DROPLIST_COND[i][7], DROPLIST_COND[i][5], DROPLIST_COND[i][6]) || DROPLIST_COND[i][1] == n2 || DROPLIST_COND[i][1] == 0) continue;
            questState.setCond(DROPLIST_COND[i][1]);
            questState.setState(2);
        }
        if (n2 == 3 && questState.getQuestItemsCount(1597) == 0L) {
            questState.takeItems(1600, -1L);
            questState.takeItems(1594, -1L);
            questState.giveItems(1597, 1L);
        } else if (n2 == 5 && questState.getQuestItemsCount(1598) == 0L) {
            questState.takeItems(1601, -1L);
            questState.takeItems(1595, -1L);
            questState.giveItems(1598, 1L);
        } else if (n2 == 7 && questState.getQuestItemsCount(1599) == 0L) {
            questState.takeItems(1602, -1L);
            questState.takeItems(1596, -1L);
            questState.giveItems(1599, 1L);
        } else if (n2 == 11 && questState.getQuestItemsCount(1610) >= 3L && questState.getQuestItemsCount(1611) >= 3L && questState.getQuestItemsCount(1612) >= 3L && questState.getQuestItemsCount(1609) >= 3L) {
            questState.takeItems(1609, -1L);
            questState.takeItems(1610, -1L);
            questState.takeItems(1611, -1L);
            questState.takeItems(1612, -1L);
            questState.takeItems(1607, -1L);
            questState.giveItems(1608, 1L);
            questState.setCond(12);
            questState.setState(2);
        }
        return null;
    }
}

