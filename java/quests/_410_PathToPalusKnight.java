/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _410_PathToPalusKnight
extends Quest
implements ScriptFile {
    public final int VIRGIL = 30329;
    public final int KALINTA = 30422;
    public final int POISON_SPIDER = 20038;
    public final int ARACHNID_TRACKER = 20043;
    public final int LYCANTHROPE = 20049;
    public final int PALLUS_TALISMAN_ID = 1237;
    public final int LYCANTHROPE_SKULL_ID = 1238;
    public final int VIRGILS_LETTER_ID = 1239;
    public final int MORTE_TALISMAN_ID = 1240;
    public final int PREDATOR_CARAPACE_ID = 1241;
    public final int TRIMDEN_SILK_ID = 1242;
    public final int COFFIN_ETERNAL_REST_ID = 1243;
    public final int GAZE_OF_ABYSS_ID = 1244;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _410_PathToPalusKnight() {
        super(0);
        this.addStartNpc(30329);
        this.addTalkId(30422);
        this.addKillId(20038);
        this.addKillId(20043);
        this.addKillId(20049);
        this.addQuestItem(1237, 1239, 1243, 1240, 1241, 1242, 1238);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("1")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            string2 = "master_virgil_q0410_06.htm";
            questState.giveItems(1237, 1L);
        } else if (string.equalsIgnoreCase("410_1")) {
            if (questState.getPlayer().getLevel() >= 18 && questState.getPlayer().getClassId().getId() == 31 && questState.getQuestItemsCount(1244) == 0L) {
                string2 = "master_virgil_q0410_05.htm";
            } else if (questState.getPlayer().getClassId().getId() != 31) {
                string2 = questState.getPlayer().getClassId().getId() == 32 ? "master_virgil_q0410_02a.htm" : "master_virgil_q0410_03.htm";
            } else if (questState.getPlayer().getLevel() < 18 && questState.getPlayer().getClassId().getId() == 31) {
                string2 = "master_virgil_q0410_02.htm";
            } else if (questState.getPlayer().getLevel() >= 18 && questState.getPlayer().getClassId().getId() == 31 && questState.getQuestItemsCount(1244) == 1L) {
                string2 = "master_virgil_q0410_04.htm";
            }
        } else if (string.equalsIgnoreCase("30329_2")) {
            string2 = "master_virgil_q0410_10.htm";
            questState.takeItems(1237, -1L);
            questState.takeItems(1238, -1L);
            questState.giveItems(1239, 1L);
            questState.setCond(3);
        } else if (string.equalsIgnoreCase("30422_1")) {
            string2 = "kalinta_q0410_02.htm";
            questState.takeItems(1239, -1L);
            questState.giveItems(1240, 1L);
            questState.setCond(4);
        } else if (string.equalsIgnoreCase("30422_2")) {
            string2 = "kalinta_q0410_06.htm";
            questState.takeItems(1240, -1L);
            questState.takeItems(1242, -1L);
            questState.takeItems(1241, -1L);
            questState.giveItems(1243, 1L);
            questState.setCond(6);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 30329) {
            if (n2 < 1) {
                string = "master_virgil_q0410_01.htm";
            } else if (questState.getQuestItemsCount(1237) > 0L) {
                if (questState.getQuestItemsCount(1238) < 1L) {
                    string = "master_virgil_q0410_07.htm";
                } else if (questState.getQuestItemsCount(1238) > 0L && questState.getQuestItemsCount(1238) < 13L) {
                    string = "master_virgil_q0410_08.htm";
                } else if (questState.getQuestItemsCount(1238) > 12L) {
                    string = "master_virgil_q0410_09.htm";
                }
            } else if (questState.getQuestItemsCount(1243) > 0L) {
                string = "master_virgil_q0410_11.htm";
                questState.takeItems(1243, -1L);
                if (questState.getPlayer().getClassId().getLevel() == 1) {
                    questState.giveItems(1244, 1L);
                    if (!questState.getPlayer().getVarB("prof1")) {
                        questState.getPlayer().setVar("prof1", "1", -1L);
                        questState.addExpAndSp(3200L, 3050L);
                    }
                }
                questState.exitCurrentQuest(true);
                questState.playSound("ItemSound.quest_finish");
            } else if (questState.getQuestItemsCount(1240) > 0L | questState.getQuestItemsCount(1239) > 0L) {
                string = "master_virgil_q0410_12.htm";
            }
        } else if (n == 30422 && n2 > 0) {
            if (questState.getQuestItemsCount(1239) > 0L) {
                string = "kalinta_q0410_01.htm";
            } else if (questState.getQuestItemsCount(1240) > 0L) {
                if (questState.getQuestItemsCount(1242) < 1L && questState.getQuestItemsCount(1241) < 1L) {
                    string = "kalinta_q0410_03.htm";
                } else if (questState.getQuestItemsCount(1242) < 1L | questState.getQuestItemsCount(1241) < 1L) {
                    string = "kalinta_q0410_04.htm";
                } else if (questState.getQuestItemsCount(1242) > 4L && questState.getQuestItemsCount(1241) > 0L) {
                    string = "kalinta_q0410_05.htm";
                }
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 20049) {
            if (n2 == 1 && questState.getQuestItemsCount(1237) > 0L && questState.getQuestItemsCount(1238) < 13L) {
                questState.giveItems(1238, 1L);
                if (questState.getQuestItemsCount(1238) > 12L) {
                    questState.playSound("ItemSound.quest_middle");
                    questState.setCond(2);
                } else {
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20038) {
            if (n2 == 4 && questState.getQuestItemsCount(1240) > 0L && questState.getQuestItemsCount(1241) < 1L) {
                questState.giveItems(1241, 1L);
                questState.playSound("ItemSound.quest_middle");
                if (questState.getQuestItemsCount(1242) > 4L) {
                    questState.setCond(5);
                }
            }
        } else if (n == 20043 && n2 == 4 && questState.getQuestItemsCount(1240) > 0L && questState.getQuestItemsCount(1242) < 5L) {
            questState.giveItems(1242, 1L);
            if (questState.getQuestItemsCount(1242) > 4L) {
                questState.playSound("ItemSound.quest_middle");
                if (questState.getQuestItemsCount(1241) > 0L) {
                    questState.setCond(5);
                }
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

