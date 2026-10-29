/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;
import quests._109_InSearchOfTheNest;

public class _640_TheZeroHour
extends Quest
implements ScriptFile {
    private static final int bRV = 22108;
    private static final int bRW = 22113;
    private static final int bRX = 22114;
    private static final int bRY = 22109;
    private static final int bRZ = 22110;
    private static final int bSa = 22118;
    private static final int bSb = 22119;
    private static final int bSc = 22105;
    private static final int bSd = 22116;
    private static final int bSe = 22107;
    private static final int bSf = 22117;
    private static final int bSg = 22111;
    private static final int bSh = 22121;
    private static final int bSi = 22115;
    private static final int bSj = 22106;
    private static final int boG = 4042;
    private static final int buT = 4043;
    private static final int buU = 4044;
    private static final int boM = 1887;
    private static final int bor = 1888;
    private static final int bSk = 1889;
    private static final int bSl = 5550;
    private static final int bSm = 1890;
    private static final int bSn = 1893;
    private static final int bQe = 31554;
    private static final int bSo = 8085;

    public _640_TheZeroHour() {
        super(1);
        this.addStartNpc(31554);
        this.addKillId(22108, 22113, 22114, 22109, 22110, 22118, 22119, 22105, 22116, 22107, 22117, 22111, 22121, 22115, 22106);
        this.addQuestItem(8085);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        int n = questState.getCond();
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            if (questState.getPlayer().getLevel() >= 66) {
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "merc_kahmun_q0640_0103.htm";
            }
        } else if (string.equalsIgnoreCase("reply_1") && n == 1) {
            string2 = "merc_kahmun_q0640_0201.htm";
        } else if (string.equalsIgnoreCase("reply_3") && n == 1) {
            string2 = "merc_kahmun_q0640_0202.htm";
        } else if (string.equalsIgnoreCase("reply_4") && n == 1) {
            string2 = "merc_kahmun_q0640_0205.htm";
            questState.exitCurrentQuest(true);
            questState.playSound("ItemSound.quest_finish");
        } else if (string.equalsIgnoreCase("reply_11") && n == 1) {
            if (questState.getQuestItemsCount(8085) >= 12L) {
                questState.takeItems(8085, 12L);
                questState.giveItems(4042, 1L);
                string2 = "merc_kahmun_q0640_0203.htm";
            } else {
                string2 = "merc_kahmun_q0640_0204.htm";
            }
        } else if (string.equalsIgnoreCase("reply_12") && n == 1) {
            if (questState.getQuestItemsCount(8085) >= 6L) {
                questState.takeItems(8085, 6L);
                questState.giveItems(4043, 1L);
                string2 = "merc_kahmun_q0640_0203.htm";
            } else {
                string2 = "merc_kahmun_q0640_0204.htm";
            }
        } else if (string.equalsIgnoreCase("reply_13") && n == 1) {
            if (questState.getQuestItemsCount(8085) >= 6L) {
                questState.takeItems(8085, 6L);
                questState.giveItems(4044, 1L);
                string2 = "merc_kahmun_q0640_0203.htm";
            } else {
                string2 = "merc_kahmun_q0640_0204.htm";
            }
        } else if (string.equalsIgnoreCase("reply_14") && n == 1) {
            if (questState.getQuestItemsCount(8085) >= 81L) {
                questState.takeItems(8085, 81L);
                questState.giveItems(1887, 10L);
                string2 = "merc_kahmun_q0640_0203.htm";
            } else {
                string2 = "merc_kahmun_q0640_0204.htm";
            }
        } else if (string.equalsIgnoreCase("reply_15") && n == 1) {
            if (questState.getQuestItemsCount(8085) >= 33L) {
                questState.takeItems(8085, 33L);
                questState.giveItems(1888, 5L);
                string2 = "merc_kahmun_q0640_0203.htm";
            } else {
                string2 = "merc_kahmun_q0640_0204.htm";
            }
        } else if (string.equalsIgnoreCase("reply_16") && n == 1) {
            if (questState.getQuestItemsCount(8085) >= 30L) {
                questState.takeItems(8085, 30L);
                questState.giveItems(1889, 10L);
                string2 = "merc_kahmun_q0640_0203.htm";
            } else {
                string2 = "merc_kahmun_q0640_0204.htm";
            }
        } else if (string.equalsIgnoreCase("reply_17") && n == 1) {
            if (questState.getQuestItemsCount(8085) >= 150L) {
                questState.takeItems(8085, 150L);
                questState.giveItems(5550, 10L);
                string2 = "merc_kahmun_q0640_0203.htm";
            } else {
                string2 = "merc_kahmun_q0640_0204.htm";
            }
        } else if (string.equalsIgnoreCase("reply_18") && n == 1) {
            if (questState.getQuestItemsCount(8085) >= 131L) {
                questState.takeItems(8085, 131L);
                questState.giveItems(1890, 10L);
                string2 = "merc_kahmun_q0640_0203.htm";
            } else {
                string2 = "merc_kahmun_q0640_0204.htm";
            }
        } else if (string.equalsIgnoreCase("reply_19") && n == 1) {
            if (questState.getQuestItemsCount(8085) >= 123L) {
                questState.takeItems(8085, 123L);
                questState.giveItems(1893, 5L);
                string2 = "merc_kahmun_q0640_0203.htm";
            } else {
                string2 = "merc_kahmun_q0640_0204.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getCond();
        QuestState questState2 = questState.getPlayer().getQuestState(_109_InSearchOfTheNest.class);
        if (questState.getPlayer().getLevel() >= 66 && n == 0) {
            string = questState2 != null && questState2.isCompleted() ? "merc_kahmun_q0640_0101.htm" : "merc_kahmun_q0640_0104.htm";
        } else if (questState.getPlayer().getLevel() < 66 && n == 0) {
            string = "merc_kahmun_q0640_0102.htm";
        } else if (n == 1) {
            string = questState.getQuestItemsCount(8085) == 0L ? "merc_kahmun_q0640_0106.htm" : "merc_kahmun_q0640_0105.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = Rnd.get(1000);
        if (n == 22108) {
            if (n2 < 77) {
                questState.rollAndGive(8085, 2, 100.0);
            } else {
                questState.rollAndGive(8085, 1, 100.0);
            }
        } else if (n == 22113) {
            if (n2 < 235) {
                questState.rollAndGive(8085, 2, 100.0);
            } else {
                questState.rollAndGive(8085, 1, 100.0);
            }
        } else if (n == 22114) {
            if (n2 < 829) {
                questState.rollAndGive(8085, 1, 100.0);
            }
        } else if (n == 22109) {
            if (n2 < 63) {
                questState.rollAndGive(8085, 2, 100.0);
            } else {
                questState.rollAndGive(8085, 1, 100.0);
            }
        } else if (n == 22110) {
            if (n2 < 806) {
                questState.rollAndGive(8085, 1, 100.0);
            }
        } else if (n == 22118) {
            if (n2 < 982) {
                questState.rollAndGive(8085, 1, 100.0);
            }
        } else if (n == 22119 || n == 22105) {
            if (n2 < 727) {
                questState.rollAndGive(8085, 1, 100.0);
            }
        } else if (n == 22116) {
            if (n2 < 702) {
                questState.rollAndGive(8085, 1, 100.0);
            }
        } else if (n == 22107) {
            if (n2 < 773) {
                questState.rollAndGive(8085, 1, 100.0);
            }
        } else if (n == 22117) {
            if (n2 < 723) {
                questState.rollAndGive(8085, 1, 100.0);
            }
        } else if (n == 22111) {
            if (n2 < 776) {
                questState.rollAndGive(8085, 1, 100.0);
            }
        } else if (n == 22121) {
            if (n2 < 704) {
                questState.rollAndGive(8085, 1, 100.0);
            }
        } else if (n == 22115) {
            if (n2 < 682) {
                questState.rollAndGive(8085, 1, 100.0);
            }
        } else if (n == 22106 && n2 < 750) {
            questState.rollAndGive(8085, 1, 100.0);
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

