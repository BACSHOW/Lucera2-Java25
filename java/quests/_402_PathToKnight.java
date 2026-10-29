/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _402_PathToKnight
extends Quest
implements ScriptFile {
    public final int SIR_KLAUS_VASPER = 30417;
    public final int BIOTIN = 30031;
    public final int LEVIAN = 30037;
    public final int GILBERT = 30039;
    public final int RAYMOND = 30289;
    public final int SIR_COLLIN_WINDAWOOD = 30311;
    public final int BATHIS = 30332;
    public final int BEZIQUE = 30379;
    public final int SIR_ARON_TANFORD = 30653;
    public final int BUGBEAR_RAIDER = 20775;
    public final int UNDEAD_PRIEST = 27024;
    public final int POISON_SPIDER = 20038;
    public final int ARACHNID_TRACKER = 20043;
    public final int ARACHNID_PREDATOR = 20050;
    public final int LANGK_LIZARDMAN = 20030;
    public final int LANGK_LIZARDMAN_SCOUT = 20027;
    public final int LANGK_LIZARDMAN_WARRIOR = 20024;
    public final int GIANT_SPIDER = 20103;
    public final int TALON_SPIDER = 20106;
    public final int BLADE_SPIDER = 20108;
    public final int SILENT_HORROR = 20404;
    public final int SWORD_OF_RITUAL = 1161;
    public final int COIN_OF_LORDS1 = 1162;
    public final int COIN_OF_LORDS2 = 1163;
    public final int COIN_OF_LORDS3 = 1164;
    public final int COIN_OF_LORDS4 = 1165;
    public final int COIN_OF_LORDS5 = 1166;
    public final int COIN_OF_LORDS6 = 1167;
    public final int GLUDIO_GUARDS_MARK1 = 1168;
    public final int BUGBEAR_NECKLACE = 1169;
    public final int EINHASAD_CHURCH_MARK1 = 1170;
    public final int EINHASAD_CRUCIFIX = 1171;
    public final int GLUDIO_GUARDS_MARK2 = 1172;
    public final int POISON_SPIDER_LEG1 = 1173;
    public final int EINHASAD_CHURCH_MARK2 = 1174;
    public final int LIZARDMAN_TOTEM = 1175;
    public final int GLUDIO_GUARDS_MARK3 = 1176;
    public final int GIANT_SPIDER_HUSK = 1177;
    public final int EINHASAD_CHURCH_MARK3 = 1178;
    public final int HORRIBLE_SKULL = 1179;
    public final int MARK_OF_ESQUIRE = 1271;
    public final int[][] DROPLIST = new int[][]{{20775, 1168, 1169, 10, 100}, {27024, 1170, 1171, 12, 100}, {20038, 1172, 1173, 20, 100}, {20043, 1172, 1173, 20, 100}, {20050, 1172, 1173, 20, 100}, {20030, 1174, 1175, 20, 50}, {20027, 1174, 1175, 20, 100}, {20024, 1174, 1175, 20, 100}, {20103, 1176, 1177, 20, 40}, {20106, 1176, 1177, 20, 40}, {20108, 1176, 1177, 20, 40}, {20404, 1178, 1179, 10, 100}};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _402_PathToKnight() {
        super(0);
        this.addStartNpc(30417);
        this.addTalkId(30031);
        this.addTalkId(30037);
        this.addTalkId(30039);
        this.addTalkId(30289);
        this.addTalkId(30311);
        this.addTalkId(30332);
        this.addTalkId(30379);
        this.addTalkId(30653);
        for (int[] nArray : this.DROPLIST) {
            this.addKillId(nArray[0]);
        }
        this.addQuestItem(1169, 1171, 1173, 1175, 1177, 1179);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        Integer n = questState.getPlayer().getClassId().getId();
        int n2 = questState.getPlayer().getLevel();
        long l = questState.getQuestItemsCount(1271);
        long l2 = questState.getQuestItemsCount(1162);
        long l3 = questState.getQuestItemsCount(1163);
        long l4 = questState.getQuestItemsCount(1164);
        long l5 = questState.getQuestItemsCount(1165);
        long l6 = questState.getQuestItemsCount(1166);
        long l7 = questState.getQuestItemsCount(1167);
        long l8 = questState.getQuestItemsCount(1168);
        long l9 = questState.getQuestItemsCount(1172);
        long l10 = questState.getQuestItemsCount(1176);
        long l11 = questState.getQuestItemsCount(1170);
        long l12 = questState.getQuestItemsCount(1174);
        long l13 = questState.getQuestItemsCount(1178);
        if (string.equalsIgnoreCase("sir_karrel_vasper_q0402_02a.htm")) {
            if (n != 0 || n2 < 18) {
                string2 = "sir_karrel_vasper_q0402_02.htm";
                questState.exitCurrentQuest(true);
            } else {
                string2 = questState.getQuestItemsCount(1161) > 0L ? "sir_karrel_vasper_q0402_04.htm" : "sir_karrel_vasper_q0402_05.htm";
            }
        } else if (string.equalsIgnoreCase("sir_karrel_vasper_q0402_08.htm")) {
            if (questState.getCond() == 0 && n == 0 && n2 >= 18) {
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                questState.giveItems(1271, 1L);
            }
        } else if (string.equalsIgnoreCase("captain_bathia_q0402_02.htm")) {
            if (l > 0L && l8 < 1L && l2 < 1L) {
                questState.giveItems(1168, 1L);
            }
        } else if (string.equalsIgnoreCase("bishop_raimund_q0402_03.htm")) {
            if (l > 0L && l11 < 1L && l3 < 1L) {
                questState.giveItems(1170, 1L);
            }
        } else if (string.equalsIgnoreCase("captain_bezique_q0402_02.htm")) {
            if (l > 0L && l9 < 1L && l4 < 1L) {
                questState.giveItems(1172, 1L);
            }
        } else if (string.equalsIgnoreCase("levian_q0402_02.htm")) {
            if (l > 0L && l12 < 1L && l5 < 1L) {
                questState.giveItems(1174, 1L);
            }
        } else if (string.equalsIgnoreCase("gilbert_q0402_02.htm")) {
            if (l > 0L && l10 < 1L && l6 < 1L) {
                questState.giveItems(1176, 1L);
            }
        } else if (string.equalsIgnoreCase("quilt_q0402_02.htm")) {
            if (l > 0L && l13 < 1L && l7 < 1L) {
                questState.giveItems(1178, 1L);
            }
        } else if (string.equalsIgnoreCase("sir_karrel_vasper_q0402_13.htm") | string.equalsIgnoreCase("sir_karrel_vasper_q0402_14.htm") && l > 0L && l2 + l3 + l4 + l5 + l6 + l7 >= 3L) {
            for (int i = 1162; i < 1179; ++i) {
                questState.takeItems(i, -1L);
            }
            questState.takeItems(1271, -1L);
            if (questState.getPlayer().getClassId().getLevel() == 1) {
                questState.giveItems(1161, 1L);
                if (!questState.getPlayer().getVarB("prof1")) {
                    questState.getPlayer().setVar("prof1", "1", -1L);
                    questState.addExpAndSp(3200L, 1500L);
                }
            }
            questState.exitCurrentQuest(true);
            questState.playSound("ItemSound.quest_finish");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        long l = questState.getQuestItemsCount(1271);
        long l2 = questState.getQuestItemsCount(1162);
        long l3 = questState.getQuestItemsCount(1163);
        long l4 = questState.getQuestItemsCount(1164);
        long l5 = questState.getQuestItemsCount(1165);
        long l6 = questState.getQuestItemsCount(1166);
        long l7 = questState.getQuestItemsCount(1167);
        long l8 = questState.getQuestItemsCount(1168);
        long l9 = questState.getQuestItemsCount(1172);
        long l10 = questState.getQuestItemsCount(1176);
        long l11 = questState.getQuestItemsCount(1170);
        long l12 = questState.getQuestItemsCount(1174);
        long l13 = questState.getQuestItemsCount(1178);
        if (n == 30417) {
            if (n2 == 0) {
                string = "sir_karrel_vasper_q0402_01.htm";
            } else if (n2 == 1 && l > 0L) {
                if (l2 + l3 + l4 + l5 + l6 + l7 < 3L) {
                    string = "sir_karrel_vasper_q0402_09.htm";
                } else if (l2 + l3 + l4 + l5 + l6 + l7 == 3L) {
                    string = "sir_karrel_vasper_q0402_10.htm";
                } else if (l2 + l3 + l4 + l5 + l6 + l7 > 3L && l2 + l3 + l4 + l5 + l6 + l7 < 6L) {
                    string = "sir_karrel_vasper_q0402_11.htm";
                } else if (l2 + l3 + l4 + l5 + l6 + l7 == 6L) {
                    string = "sir_karrel_vasper_q0402_12.htm";
                    for (int i = 1162; i < 1179; ++i) {
                        questState.takeItems(i, -1L);
                    }
                    questState.takeItems(1271, -1L);
                    questState.giveItems(1161, 1L);
                    questState.unset("cond");
                    questState.exitCurrentQuest(true);
                    questState.playSound("ItemSound.quest_finish");
                }
            }
        } else if (n == 30332 && n2 == 1 && l > 0L) {
            if (l8 < 1L && l2 < 1L) {
                string = "captain_bathia_q0402_01.htm";
            } else if (l8 > 0L) {
                if (questState.getQuestItemsCount(1169) < 10L) {
                    string = "captain_bathia_q0402_03.htm";
                } else {
                    string = "captain_bathia_q0402_04.htm";
                    questState.takeItems(1169, -1L);
                    questState.takeItems(1168, 1L);
                    questState.giveItems(1162, 1L);
                    questState.playSound("ItemSound.quest_middle");
                }
            } else if (l2 > 0L) {
                string = "captain_bathia_q0402_05.htm";
            }
        } else if (n == 30289 && n2 == 1 && l > 0L) {
            if (l11 < 1L && l3 < 1L) {
                string = "bishop_raimund_q0402_01.htm";
            } else if (l11 > 0L) {
                if (questState.getQuestItemsCount(1171) < 12L) {
                    string = "bishop_raimund_q0402_04.htm";
                } else {
                    string = "bishop_raimund_q0402_05.htm";
                    questState.takeItems(1171, -1L);
                    questState.takeItems(1170, 1L);
                    questState.giveItems(1163, 1L);
                    questState.playSound("ItemSound.quest_middle");
                }
            } else if (l3 > 0L) {
                string = "bishop_raimund_q0402_06.htm";
            }
        } else if (n == 30379 && n2 == 1 && l > 0L) {
            if (l4 < 1L && l9 < 1L) {
                string = "captain_bezique_q0402_01.htm";
            } else if (l9 > 0L) {
                if (questState.getQuestItemsCount(1173) < 20L) {
                    string = "captain_bezique_q0402_03.htm";
                } else {
                    string = "captain_bezique_q0402_04.htm";
                    questState.takeItems(1173, -1L);
                    questState.takeItems(1172, 1L);
                    questState.giveItems(1164, 1L);
                    questState.playSound("ItemSound.quest_middle");
                }
            } else if (l4 > 0L) {
                string = "captain_bezique_q0402_05.htm";
            }
        } else if (n == 30037 && n2 == 1 && l > 0L) {
            if (l5 < 1L && l12 < 1L) {
                string = "levian_q0402_01.htm";
            } else if (l12 > 0L) {
                if (questState.getQuestItemsCount(1175) < 20L) {
                    string = "levian_q0402_03.htm";
                } else {
                    string = "levian_q0402_04.htm";
                    questState.takeItems(1175, -1L);
                    questState.takeItems(1174, 1L);
                    questState.giveItems(1165, 1L);
                    questState.playSound("ItemSound.quest_middle");
                }
            } else if (l5 > 0L) {
                string = "levian_q0402_05.htm";
            }
        } else if (n == 30039 && n2 == 1 && l > 0L) {
            if (l10 < 1L && l6 < 1L) {
                string = "gilbert_q0402_01.htm";
            } else if (l10 > 0L) {
                if (questState.getQuestItemsCount(1177) < 20L) {
                    string = "gilbert_q0402_03.htm";
                } else {
                    string = "gilbert_q0402_04.htm";
                    questState.takeItems(1177, -1L);
                    questState.takeItems(1176, 1L);
                    questState.giveItems(1166, 1L);
                    questState.playSound("ItemSound.quest_middle");
                }
            } else if (l6 > 0L) {
                string = "gilbert_q0402_05.htm";
            }
        } else if (n == 30031 && n2 == 1 && l > 0L) {
            if (l13 < 1L && l7 < 1L) {
                string = "quilt_q0402_01.htm";
            } else if (l13 > 0L) {
                if (questState.getQuestItemsCount(1179) < 10L) {
                    string = "quilt_q0402_03.htm";
                } else {
                    string = "quilt_q0402_04.htm";
                    questState.takeItems(1179, -1L);
                    questState.takeItems(1178, 1L);
                    questState.giveItems(1167, 1L);
                    questState.playSound("ItemSound.quest_middle");
                }
            } else if (l7 > 0L) {
                string = "quilt_q0402_05.htm";
            }
        } else if (n == 30311 && n2 == 1 && l > 0L) {
            string = "sir_collin_windawood_q0402_01.htm";
        } else if (n == 30653 && n2 == 1 && l > 0L) {
            string = "sir_aron_tanford_q0402_01.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        for (int[] nArray : this.DROPLIST) {
            if (questState.getCond() <= 0 || n != nArray[0] || questState.getQuestItemsCount(nArray[1]) <= 0L || questState.getQuestItemsCount(nArray[2]) >= (long)nArray[3] || !Rnd.chance(nArray[4])) continue;
            questState.giveItems(nArray[2], 1L);
            if (questState.getQuestItemsCount(nArray[2]) == (long)nArray[3]) {
                questState.playSound("ItemSound.quest_middle");
                continue;
            }
            questState.playSound("ItemSound.quest_itemget");
        }
        return null;
    }
}

