/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _411_PathToAssassin
extends Quest
implements ScriptFile {
    public final int TRISKEL = 30416;
    public final int LEIKAN = 30382;
    public final int ARKENIA = 30419;
    public final int MOONSTONE_BEAST = 20369;
    public final int CALPICO = 27036;
    public final int SHILENS_CALL_ID = 1245;
    public final int ARKENIAS_LETTER_ID = 1246;
    public final int LEIKANS_NOTE_ID = 1247;
    public final int ONYX_BEASTS_MOLAR_ID = 1248;
    public final int LEIKANS_KNIFE_ID = 1249;
    public final int SHILENS_TEARS_ID = 1250;
    public final int ARKENIA_RECOMMEND_ID = 1251;
    public final int IRON_HEART_ID = 1252;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _411_PathToAssassin() {
        super(0);
        this.addStartNpc(30416);
        this.addTalkId(30382);
        this.addTalkId(30419);
        this.addKillId(20369);
        this.addKillId(27036);
        this.addQuestItem(1245, 1247, 1249, 1251, 1246, 1248, 1250);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("1")) {
            if (questState.getPlayer().getLevel() >= 18 && questState.getPlayer().getClassId().getId() == 31 && questState.getQuestItemsCount(1252) < 1L) {
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                questState.giveItems(1245, 1L);
                string2 = "triskel_q0411_05.htm";
            } else if (questState.getPlayer().getClassId().getId() != 31) {
                if (questState.getPlayer().getClassId().getId() == 35) {
                    string2 = "triskel_q0411_02a.htm";
                } else {
                    string2 = "triskel_q0411_02.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (questState.getPlayer().getLevel() < 18 && questState.getPlayer().getClassId().getId() == 31) {
                string2 = "triskel_q0411_03.htm";
                questState.exitCurrentQuest(true);
            } else if (questState.getPlayer().getLevel() >= 18 && questState.getPlayer().getClassId().getId() == 31 && questState.getQuestItemsCount(1252) > 0L) {
                string2 = "triskel_q0411_04.htm";
            }
        } else if (string.equalsIgnoreCase("30419_1")) {
            string2 = "arkenia_q0411_05.htm";
            questState.takeItems(1245, -1L);
            questState.giveItems(1246, 1L);
            questState.setCond(2);
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equalsIgnoreCase("30382_1")) {
            string2 = "guard_leikan_q0411_03.htm";
            questState.takeItems(1246, -1L);
            questState.giveItems(1247, 1L);
            questState.setCond(3);
            questState.playSound("ItemSound.quest_middle");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 30416) {
            if (n2 < 1) {
                string = questState.getQuestItemsCount(1252) < 1L ? "triskel_q0411_01.htm" : "triskel_q0411_04.htm";
            } else if (n2 == 7) {
                string = "triskel_q0411_06.htm";
                questState.takeItems(1251, -1L);
                if (questState.getPlayer().getClassId().getLevel() == 1) {
                    questState.giveItems(1252, 1L);
                    if (!questState.getPlayer().getVarB("prof1")) {
                        questState.getPlayer().setVar("prof1", "1", -1L);
                        questState.addExpAndSp(3200L, 3930L);
                    }
                }
                questState.exitCurrentQuest(true);
                questState.playSound("ItemSound.quest_finish");
            } else if (n2 == 2) {
                string = "triskel_q0411_07.htm";
            } else if (n2 == 1) {
                string = "triskel_q0411_11.htm";
            } else if (n2 > 2 && n2 < 7) {
                if (n2 > 2 && n2 < 5) {
                    string = "triskel_q0411_08.htm";
                } else if (n2 > 4 && n2 < 7) {
                    string = questState.getQuestItemsCount(1250) < 1L ? "triskel_q0411_09.htm" : "triskel_q0411_10.htm";
                }
            }
        } else if (n == 30419) {
            if (n2 == 1 && questState.getQuestItemsCount(1245) > 0L) {
                string = "arkenia_q0411_01.htm";
            } else if (n2 == 2 && questState.getQuestItemsCount(1246) > 0L) {
                string = "arkenia_q0411_07.htm";
            } else if (n2 > 2 && n2 < 5 && questState.getQuestItemsCount(1247) > 0L) {
                string = "arkenia_q0411_10.htm";
            } else if (n2 == 5 && questState.getQuestItemsCount(1249) > 0L) {
                string = "arkenia_q0411_11.htm";
            } else if (n2 == 6 && questState.getQuestItemsCount(1250) > 0L) {
                string = "arkenia_q0411_08.htm";
                questState.takeItems(1250, -1L);
                questState.takeItems(1249, -1L);
                questState.giveItems(1251, 1L);
                questState.setCond(7);
                questState.playSound("ItemSound.quest_middle");
            } else if (n2 == 7) {
                string = "arkenia_q0411_09.htm";
            }
        } else if (n == 30382) {
            if (n2 == 2 && questState.getQuestItemsCount(1246) > 0L) {
                string = "guard_leikan_q0411_01.htm";
            } else if (n2 > 2 && n2 < 4 && questState.getQuestItemsCount(1248) < 1L) {
                string = "guard_leikan_q0411_05.htm";
                if (n2 == 4) {
                    questState.setCond(3);
                }
            } else if (n2 > 2 && n2 < 4 && questState.getQuestItemsCount(1248) < 10L) {
                string = "guard_leikan_q0411_06.htm";
                if (n2 == 4) {
                    questState.setCond(3);
                }
            } else if (n2 == 4 && questState.getQuestItemsCount(1248) > 9L) {
                string = "guard_leikan_q0411_07.htm";
                questState.takeItems(1248, -1L);
                questState.takeItems(1247, -1L);
                questState.giveItems(1249, 1L);
                questState.setCond(5);
                questState.playSound("ItemSound.quest_middle");
            } else if (n2 > 4 && n2 < 7 && questState.getQuestItemsCount(1250) < 1L) {
                string = "guard_leikan_q0411_09.htm";
                if (n2 == 6) {
                    questState.setCond(5);
                }
            } else if (n2 == 6 && questState.getQuestItemsCount(1250) > 0L) {
                string = "guard_leikan_q0411_08.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 27036) {
            if (n2 == 5 && questState.getQuestItemsCount(1249) > 0L && questState.getQuestItemsCount(1250) < 1L) {
                questState.giveItems(1250, 1L);
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(6);
            }
        } else if (n == 20369 && n2 == 3 && questState.getQuestItemsCount(1247) > 0L && questState.getQuestItemsCount(1248) < 10L) {
            questState.giveItems(1248, 1L);
            if (questState.getQuestItemsCount(1248) > 9L) {
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(4);
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

