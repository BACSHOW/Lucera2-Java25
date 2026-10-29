/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.Config;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _639_GuardiansOfTheHolyGrail
extends Quest
implements ScriptFile {
    private static final int aAa = 31350;
    private static final int bRB = 32008;
    private static final int bRC = 32028;
    private static final int bRD = 8069;
    private static final int bRE = 8070;
    private static final int bRF = 8071;
    private static final int bRG = 8056;
    private static final int bQJ = 960;
    private static final int bQK = 959;
    private static final int bRH = 22123;
    private static final int bRI = 22122;
    private static final int bRJ = 22128;
    private static final int bRK = 22135;
    private static final int bRL = 22132;
    private static final int bRM = 22131;
    private static final int bRN = 22129;
    private static final int bRO = 22133;
    private static final int bRP = 22134;
    private static final int bRQ = 22127;
    private static final int bRR = 22126;
    private static final int bRS = 22124;
    private static final int bRT = 22125;
    private static final int bRU = 22130;

    public _639_GuardiansOfTheHolyGrail() {
        super(1);
        this.addStartNpc(31350);
        this.addTalkId(32008, 32028);
        this.addQuestItem(8069);
        this.addKillId(22123, 22122, 22128, 22135, 22132, 22131, 22129, 22133, 22134, 22127, 22126, 22124, 22125, 22130);
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

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("falsepriest_dominic_q0639_04.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equals("falsepriest_dominic_q0639_09.htm")) {
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        } else if (string.equals("falsepriest_dominic_q0639_08.htm")) {
            questState.giveItems(57, questState.takeAllItems(8069) * 1625L, Config.RATE_QUESTS_ADENA_REWARD < 1.0);
        } else if (string.equals("falsepriest_gremory_q0639_05.htm")) {
            questState.setCond(2);
            questState.playSound("ItemSound.quest_middle");
            questState.giveItems(8070, 1L, false);
        } else if (string.equals("holy_grail_q0639_02.htm")) {
            questState.setCond(3);
            questState.playSound("ItemSound.quest_middle");
            questState.takeItems(8070, -1L);
            questState.giveItems(8071, 1L);
        } else if (string.equals("falsepriest_gremory_q0639_09.htm")) {
            questState.setCond(4);
            questState.playSound("ItemSound.quest_middle");
            questState.takeItems(8071, -1L);
        } else if (string.equals("falsepriest_gremory_q0639_11.htm")) {
            questState.takeItems(8069, 4000L);
            questState.giveItems(959, 1L, true);
        } else if (string.equals("falsepriest_gremory_q0639_13.htm")) {
            questState.takeItems(8069, 400L);
            questState.giveItems(960, 1L, true);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getState();
        int n3 = questState.getCond();
        if (n == 31350) {
            if (n2 == 1) {
                string = questState.getPlayer().getLevel() >= 73 ? "falsepriest_dominic_q0639_01.htm" : "falsepriest_dominic_q0639_02.htm";
                questState.exitCurrentQuest(true);
            } else {
                string = questState.getQuestItemsCount(8069) >= 1L ? "falsepriest_dominic_q0639_05.htm" : "falsepriest_dominic_q0639_06.htm";
            }
        } else if (n == 32008) {
            if (n3 == 1) {
                string = "falsepriest_gremory_q0639_01.htm";
            } else if (n3 == 2) {
                string = "falsepriest_gremory_q0639_06.htm";
            } else if (n3 == 3) {
                string = "falsepriest_gremory_q0639_08.htm";
            } else if (n3 == 4 && questState.getQuestItemsCount(8069) < 400L) {
                string = "falsepriest_gremory_q0639_09.htm";
            } else if (n3 == 4 && questState.getQuestItemsCount(8069) >= 4000L) {
                string = "falsepriest_gremory_q0639_10.htm";
            } else if (n3 == 4 && questState.getQuestItemsCount(8069) >= 400L && questState.getQuestItemsCount(8069) < 4000L) {
                string = "falsepriest_gremory_q0639_14.htm";
            }
        } else if (n == 32028 && n3 == 2) {
            string = "holy_grail_q0639_01.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (n == 22123 && Rnd.get(100) < 75) {
            questState.rollAndGive(8069, 1, 100.0);
        } else if (n == 22122 && Rnd.get(100) < 76) {
            questState.rollAndGive(8069, 1, 100.0);
        } else if (n == 22128 && Rnd.get(100) < 17) {
            questState.rollAndGive(8069, 1, 100.0);
        } else if (n == 22130) {
            if (Rnd.get(100) < 17) {
                questState.rollAndGive(8056, 1, 100.0);
            }
            if (Rnd.get(100) < 85) {
                questState.rollAndGive(8069, 1, 100.0);
            }
        } else if (n == 22135) {
            if (Rnd.get(30) < 1) {
                questState.rollAndGive(8056, 1, 100.0);
            }
            if (Rnd.get(100) < 93) {
                questState.rollAndGive(8069, 1, 100.0);
            }
        } else if (n == 22132) {
            if (Rnd.get(30) < 1) {
                questState.rollAndGive(8056, 1, 100.0);
            }
            if (Rnd.get(100) < 58) {
                questState.rollAndGive(8069, 1, 100.0);
            }
        } else if (n == 22131) {
            if (Rnd.get(30) < 1) {
                questState.rollAndGive(8056, 1, 100.0);
            }
            if (Rnd.get(100) < 92) {
                questState.rollAndGive(8069, 1, 100.0);
            }
        } else if (n == 22129) {
            if (Rnd.get(30) < 1) {
                questState.rollAndGive(8056, 1, 100.0);
            }
            if (Rnd.get(100) < 59) {
                questState.rollAndGive(8069, 1, 100.0);
            }
        } else if (n == 22133) {
            if (Rnd.get(30) < 1) {
                questState.rollAndGive(8056, 1, 100.0);
            }
            if (Rnd.get(100) < 23) {
                questState.rollAndGive(8069, 1, 100.0);
            }
        } else if (n == 22134) {
            if (Rnd.get(30) < 1) {
                questState.rollAndGive(8056, 1, 100.0);
            }
            if (Rnd.get(100) < 58) {
                questState.rollAndGive(8069, 1, 100.0);
            }
        } else if (n == 22127 || n == 22125) {
            if (Rnd.get(100) < 58) {
                questState.rollAndGive(8069, 1, 100.0);
            }
        } else if ((n == 22126 || n == 22124) && Rnd.get(100) < 59) {
            questState.rollAndGive(8069, 1, 100.0);
        }
        return null;
    }
}

