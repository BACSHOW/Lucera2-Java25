/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _407_PathToElvenScout
extends Quest
implements ScriptFile {
    public final int REISA = 30328;
    public final int MORETTI = 30337;
    public final int PIPPEN = 30426;
    public final int OL_MAHUM_SENTRY = 27031;
    public final int OL_MAHUM_PATROL = 20053;
    public final int REORIA_LETTER2_ID = 1207;
    public final int PRIGUNS_TEAR_LETTER1_ID = 1208;
    public final int PRIGUNS_TEAR_LETTER2_ID = 1209;
    public final int PRIGUNS_TEAR_LETTER3_ID = 1210;
    public final int PRIGUNS_TEAR_LETTER4_ID = 1211;
    public final int MORETTIS_HERB_ID = 1212;
    public final int MORETTIS_LETTER_ID = 1214;
    public final int PRIGUNS_LETTER_ID = 1215;
    public final int MONORARY_GUARD_ID = 1216;
    public final int REORIA_RECOMMENDATION_ID = 1217;
    public final int RUSTED_KEY_ID = 1293;
    public final int HONORARY_GUARD_ID = 1216;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _407_PathToElvenScout() {
        super(0);
        this.addStartNpc(30328);
        this.addTalkId(30337);
        this.addTalkId(30426);
        this.addKillId(27031);
        this.addKillId(20053);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("1")) {
            if (questState.getPlayer().getClassId().getId() == 18) {
                if (questState.getPlayer().getLevel() >= 18) {
                    if (questState.getQuestItemsCount(1217) > 0L) {
                        string2 = "master_reoria_q0407_04.htm";
                        questState.exitCurrentQuest(true);
                    } else {
                        string2 = "master_reoria_q0407_05.htm";
                        questState.giveItems(1207, 1L);
                        questState.setCond(1);
                        questState.setState(2);
                        questState.playSound("ItemSound.quest_accept");
                    }
                } else {
                    string2 = "master_reoria_q0407_03.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (questState.getPlayer().getClassId().getId() == 22) {
                string2 = "master_reoria_q0407_02a.htm";
                questState.exitCurrentQuest(true);
            } else {
                string2 = "master_reoria_q0407_02.htm";
                questState.exitCurrentQuest(true);
            }
        } else if (string.equalsIgnoreCase("30337_1")) {
            questState.takeItems(1207, 1L);
            questState.setCond(2);
            string2 = "guard_moretti_q0407_03.htm";
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getState();
        int n3 = 0;
        if (n2 != 1) {
            n3 = questState.getCond();
        }
        if (n == 30328) {
            if (n3 == 0) {
                string = "master_reoria_q0407_01.htm";
            } else if (n3 == 1) {
                string = "master_reoria_q0407_06.htm";
            } else if (n3 > 1 && questState.getQuestItemsCount(1216) == 0L) {
                string = "master_reoria_q0407_08.htm";
            } else if (n3 == 8 && questState.getQuestItemsCount(1216) == 1L) {
                string = "master_reoria_q0407_07.htm";
                questState.takeItems(1216, 1L);
                if (questState.getPlayer().getClassId().getLevel() == 1) {
                    questState.giveItems(1217, 1L);
                    if (!questState.getPlayer().getVarB("prof1")) {
                        questState.getPlayer().setVar("prof1", "1", -1L);
                        questState.addExpAndSp(3200L, 1000L);
                    }
                }
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            }
        } else if (n == 30337) {
            if (n3 == 1) {
                string = "guard_moretti_q0407_01.htm";
            } else if (n3 == 2) {
                string = "guard_moretti_q0407_04.htm";
            } else if (n3 == 3) {
                if (questState.getQuestItemsCount(1208) == 1L && questState.getQuestItemsCount(1209) == 1L && questState.getQuestItemsCount(1210) == 1L && questState.getQuestItemsCount(1211) == 1L) {
                    string = "guard_moretti_q0407_06.htm";
                    questState.takeItems(1208, 1L);
                    questState.takeItems(1209, 1L);
                    questState.takeItems(1210, 1L);
                    questState.takeItems(1211, 1L);
                    questState.giveItems(1212, 1L);
                    questState.giveItems(1214, 1L);
                    questState.setCond(4);
                } else {
                    string = "guard_moretti_q0407_05.htm";
                }
            } else if (n3 == 7 && questState.getQuestItemsCount(1215) == 1L) {
                string = "guard_moretti_q0407_07.htm";
                questState.takeItems(1215, 1L);
                questState.giveItems(1216, 1L);
                questState.setCond(8);
            } else if (n3 > 8) {
                string = "guard_moretti_q0407_08.htm";
            }
        } else if (n == 30426) {
            if (n3 == 4) {
                string = "prigun_q0407_01.htm";
                questState.setCond(5);
            } else if (n3 == 5) {
                string = "prigun_q0407_01.htm";
            } else if (n3 == 6 && questState.getQuestItemsCount(1293) == 1L && questState.getQuestItemsCount(1212) == 1L && questState.getQuestItemsCount(1214) == 1L) {
                string = "prigun_q0407_02.htm";
                questState.takeItems(1293, 1L);
                questState.takeItems(1212, 1L);
                questState.takeItems(1214, 1L);
                questState.giveItems(1215, 1L);
                questState.setCond(7);
            } else if (n3 == 7) {
                string = "prigun_q0407_04.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 20053 && n2 == 2) {
            if (questState.getQuestItemsCount(1208) == 0L) {
                questState.giveItems(1208, 1L);
                questState.playSound("ItemSound.quest_itemget");
                return null;
            }
            if (questState.getQuestItemsCount(1209) == 0L) {
                questState.giveItems(1209, 1L);
                questState.playSound("ItemSound.quest_itemget");
                return null;
            }
            if (questState.getQuestItemsCount(1210) == 0L) {
                questState.giveItems(1210, 1L);
                questState.playSound("ItemSound.quest_itemget");
                return null;
            }
            if (questState.getQuestItemsCount(1211) == 0L) {
                questState.giveItems(1211, 1L);
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(3);
                return null;
            }
        } else if (n == 27031 && n2 == 5 && Rnd.chance(60)) {
            questState.giveItems(1293, 1L);
            questState.playSound("ItemSound.quest_middle");
            questState.setCond(6);
        }
        return null;
    }
}

