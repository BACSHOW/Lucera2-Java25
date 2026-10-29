/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _405_PathToCleric
extends Quest
implements ScriptFile {
    public final int GALLINT = 30017;
    public final int ZIGAUNT = 30022;
    public final int VIVYAN = 30030;
    public final int SIMPLON = 30253;
    public final int PRAGA = 30333;
    public final int LIONEL = 30408;
    public final int RUIN_ZOMBIE = 20026;
    public final int RUIN_ZOMBIE_LEADER = 20029;
    public final int LETTER_OF_ORDER1 = 1191;
    public final int LETTER_OF_ORDER2 = 1192;
    public final int BOOK_OF_LEMONIELL = 1193;
    public final int BOOK_OF_VIVI = 1194;
    public final int BOOK_OF_SIMLON = 1195;
    public final int BOOK_OF_PRAGA = 1196;
    public final int CERTIFICATE_OF_GALLINT = 1197;
    public final int PENDANT_OF_MOTHER = 1198;
    public final int NECKLACE_OF_MOTHER = 1199;
    public final int LEMONIELLS_COVENANT = 1200;
    public final int MARK_OF_FAITH = 1201;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _405_PathToCleric() {
        super(0);
        this.addStartNpc(30022);
        this.addTalkId(30017);
        this.addTalkId(30030);
        this.addTalkId(30253);
        this.addTalkId(30333);
        this.addTalkId(30408);
        this.addKillId(20026);
        this.addKillId(20029);
        this.addQuestItem(1200, 1192, 1196, 1194, 1195, 1191, 1199, 1198, 1197, 1193);
    }

    public void checkBooks(QuestState questState) {
        if (questState.getQuestItemsCount(1196) + questState.getQuestItemsCount(1194) + questState.getQuestItemsCount(1195) >= 5L) {
            questState.setCond(2);
        }
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("1")) {
            if (questState.getPlayer().getLevel() >= 18 && questState.getPlayer().getClassId().getId() == 10 && questState.getQuestItemsCount(1201) < 1L) {
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                questState.giveItems(1191, 1L);
                string2 = "gigon_q0405_05.htm";
            } else if (questState.getPlayer().getClassId().getId() != 10) {
                string2 = questState.getPlayer().getClassId().getId() == 15 ? "gigon_q0405_02a.htm" : "gigon_q0405_02.htm";
            } else if (questState.getPlayer().getLevel() < 18 && questState.getPlayer().getClassId().getId() == 10) {
                string2 = "gigon_q0405_03.htm";
            } else if (questState.getPlayer().getLevel() >= 18 && questState.getPlayer().getClassId().getId() == 10 && questState.getQuestItemsCount(1201) > 0L) {
                string2 = "gigon_q0405_04.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 30022) {
            if (questState.getQuestItemsCount(1201) > 0L) {
                string = "gigon_q0405_04.htm";
                questState.exitCurrentQuest(true);
            }
            if (n2 < 1 && questState.getQuestItemsCount(1201) < 1L) {
                string = "gigon_q0405_01.htm";
            } else if (n2 == 1 | n2 == 2 && questState.getQuestItemsCount(1191) > 0L) {
                if (questState.getQuestItemsCount(1194) > 0L && questState.getQuestItemsCount(1195) > 2L && questState.getQuestItemsCount(1196) > 0L) {
                    string = "gigon_q0405_08.htm";
                    questState.takeItems(1196, -1L);
                    questState.takeItems(1194, -1L);
                    questState.takeItems(1195, -1L);
                    questState.takeItems(1191, -1L);
                    questState.giveItems(1192, 1L);
                    questState.setCond(3);
                } else {
                    string = "gigon_q0405_06.htm";
                }
            } else if (n2 < 6 && questState.getQuestItemsCount(1192) > 0L) {
                string = "gigon_q0405_07.htm";
            } else if (n2 == 6 && questState.getQuestItemsCount(1192) > 0L && questState.getQuestItemsCount(1200) > 0L) {
                string = "gigon_q0405_09.htm";
                questState.takeItems(1200, -1L);
                questState.takeItems(1192, -1L);
                if (!questState.getPlayer().getVarB("q405")) {
                    questState.getPlayer().setVar("q405", "1", -1L);
                }
                questState.exitCurrentQuest(true);
                if (questState.getPlayer().getClassId().getLevel() == 1) {
                    questState.giveItems(1201, 1L);
                    if (!questState.getPlayer().getVarB("prof1")) {
                        questState.getPlayer().setVar("prof1", "1", -1L);
                        questState.addExpAndSp(3200L, 5610L);
                    }
                }
                questState.playSound("ItemSound.quest_finish");
            }
        } else if (n == 30253 && n2 == 1 && questState.getQuestItemsCount(1191) > 0L) {
            if (questState.getQuestItemsCount(1195) < 1L) {
                string = "trader_simplon_q0405_01.htm";
                questState.giveItems(1195, 3L);
                this.checkBooks(questState);
            } else if (questState.getQuestItemsCount(1195) > 2L) {
                string = "trader_simplon_q0405_02.htm";
            }
        } else if (n == 30030 && n2 == 1 && questState.getQuestItemsCount(1191) > 0L) {
            if (questState.getQuestItemsCount(1194) < 1L) {
                string = "vivi_q0405_01.htm";
                questState.giveItems(1194, 1L);
                this.checkBooks(questState);
            } else if (questState.getQuestItemsCount(1194) > 0L) {
                string = "vivi_q0405_02.htm";
            }
        } else if (n == 30333 && n2 == 1 && questState.getQuestItemsCount(1191) > 0L) {
            if (questState.getQuestItemsCount(1196) < 1L && questState.getQuestItemsCount(1199) < 1L) {
                string = "guard_praga_q0405_01.htm";
                questState.giveItems(1199, 1L);
            } else if (questState.getQuestItemsCount(1196) < 1L && questState.getQuestItemsCount(1199) > 0L && questState.getQuestItemsCount(1198) < 1L) {
                string = "guard_praga_q0405_02.htm";
            } else if (questState.getQuestItemsCount(1196) < 1L && questState.getQuestItemsCount(1199) > 0L && questState.getQuestItemsCount(1198) > 0L) {
                string = "guard_praga_q0405_03.htm";
                questState.takeItems(1199, -1L);
                questState.takeItems(1198, -1L);
                questState.giveItems(1196, 1L);
                this.checkBooks(questState);
            } else if (questState.getQuestItemsCount(1196) > 0L) {
                string = "guard_praga_q0405_04.htm";
            }
        } else if (n == 30408) {
            if (questState.getQuestItemsCount(1192) < 1L) {
                string = "lemoniell_q0405_02.htm";
            } else if (n2 == 3 && questState.getQuestItemsCount(1192) == 1L && questState.getQuestItemsCount(1193) < 1L && questState.getQuestItemsCount(1200) < 1L && questState.getQuestItemsCount(1197) < 1L) {
                string = "lemoniell_q0405_01.htm";
                questState.giveItems(1193, 1L);
                questState.setCond(4);
            } else if (n2 == 4 && questState.getQuestItemsCount(1192) == 1L && questState.getQuestItemsCount(1193) > 0L && questState.getQuestItemsCount(1200) < 1L && questState.getQuestItemsCount(1197) < 1L) {
                string = "lemoniell_q0405_03.htm";
            } else if (questState.getQuestItemsCount(1192) == 1L && questState.getQuestItemsCount(1193) < 1L && questState.getQuestItemsCount(1200) < 1L && questState.getQuestItemsCount(1197) > 0L) {
                string = "lemoniell_q0405_04.htm";
                questState.takeItems(1197, -1L);
                questState.giveItems(1200, 1L);
                questState.setCond(6);
            } else if (questState.getQuestItemsCount(1192) == 1L && questState.getQuestItemsCount(1193) < 1L && questState.getQuestItemsCount(1200) > 0L && questState.getQuestItemsCount(1197) < 1L) {
                string = "lemoniell_q0405_05.htm";
            }
        } else if (n == 30017 && questState.getQuestItemsCount(1192) > 0L) {
            if (n2 == 4 && questState.getQuestItemsCount(1193) > 0L && questState.getQuestItemsCount(1197) < 1L) {
                string = "gallin_q0405_01.htm";
                questState.takeItems(1193, -1L);
                questState.giveItems(1197, 1L);
                questState.setCond(5);
            } else if (n2 == 5 && questState.getQuestItemsCount(1193) < 1L && questState.getQuestItemsCount(1197) > 0L) {
                string = "gallin_q0405_02.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        if (n == 20026 | n == 20029 && questState.getCond() == 1 && questState.getQuestItemsCount(1198) < 1L) {
            questState.giveItems(1198, 1L);
            questState.playSound("ItemSound.quest_middle");
        }
        return null;
    }
}

