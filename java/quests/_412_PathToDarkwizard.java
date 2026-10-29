/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _412_PathToDarkwizard
extends Quest
implements ScriptFile {
    public final int CHARKEREN = 30415;
    public final int ANNIKA = 30418;
    public final int ARKENIA = 30419;
    public final int VARIKA = 30421;
    public final int MARSH_ZOMBIE = 20015;
    public final int MARSH_ZOMBIE_LORD = 20020;
    public final int MISERY_SKELETON = 20022;
    public final int SKELETON_SCOUT = 20045;
    public final int SKELETON_HUNTER = 20517;
    public final int SKELETON_HUNTER_ARCHER = 20518;
    public final int SEEDS_OF_DESPAIR_ID = 1254;
    public final int SEEDS_OF_ANGER_ID = 1253;
    public final int SEEDS_OF_HORROR_ID = 1255;
    public final int SEEDS_OF_LUNACY_ID = 1256;
    public final int FAMILYS_ASHES_ID = 1257;
    public final int KNEE_BONE_ID = 1259;
    public final int HEART_OF_LUNACY_ID = 1260;
    public final int JEWEL_OF_DARKNESS_ID = 1261;
    public final int LUCKY_KEY_ID = 1277;
    public final int CANDLE_ID = 1278;
    public final int HUB_SCENT_ID = 1279;
    public final int[][] DROPLIST = new int[][]{{20015, 1277, 1257, 3}, {20020, 1277, 1257, 3}, {20517, 1278, 1259, 2}, {20518, 1278, 1259, 2}, {20022, 1278, 1259, 2}, {20045, 1279, 1260, 3}};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _412_PathToDarkwizard() {
        super(0);
        this.addStartNpc(30421);
        this.addTalkId(30415);
        this.addTalkId(30418);
        this.addTalkId(30419);
        this.addQuestItem(1253);
        this.addQuestItem(1277);
        this.addQuestItem(1255);
        this.addQuestItem(1278);
        this.addQuestItem(1256);
        this.addQuestItem(1279);
        this.addQuestItem(1254);
        this.addQuestItem(1257);
        this.addQuestItem(1259);
        this.addQuestItem(1260);
        for (int[] nArray : this.DROPLIST) {
            this.addKillId(nArray[0]);
        }
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("1")) {
            if (questState.getPlayer().getLevel() >= 18 && questState.getPlayer().getClassId().getId() == 38 && questState.getQuestItemsCount(1261) < 1L) {
                questState.setCond(1);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                questState.giveItems(1254, 1L);
                string2 = "varika_q0412_05.htm";
            } else if (questState.getPlayer().getClassId().getId() != 38) {
                string2 = questState.getPlayer().getClassId().getId() == 39 ? "varika_q0412_02a.htm" : "varika_q0412_03.htm";
            } else if (questState.getPlayer().getLevel() < 18 && questState.getPlayer().getClassId().getId() == 38) {
                string2 = "varika_q0412_02.htm";
            } else if (questState.getPlayer().getLevel() >= 18 && questState.getPlayer().getClassId().getId() == 38 && questState.getQuestItemsCount(1261) > 0L) {
                string2 = "varika_q0412_04.htm";
            }
        } else if (string.equalsIgnoreCase("412_1")) {
            string2 = questState.getQuestItemsCount(1253) > 0L ? "varika_q0412_06.htm" : "varika_q0412_07.htm";
        } else if (string.equalsIgnoreCase("412_2")) {
            string2 = questState.getQuestItemsCount(1255) > 0L ? "varika_q0412_09.htm" : "varika_q0412_10.htm";
        } else if (string.equalsIgnoreCase("412_3")) {
            if (questState.getQuestItemsCount(1256) > 0L) {
                string2 = "varika_q0412_12.htm";
            } else if (questState.getQuestItemsCount(1256) < 1L && questState.getQuestItemsCount(1254) > 0L) {
                string2 = "varika_q0412_13.htm";
            }
        } else if (string.equalsIgnoreCase("412_4")) {
            string2 = "charkeren_q0412_03.htm";
            questState.giveItems(1277, 1L);
        } else if (string.equalsIgnoreCase("30418_1")) {
            string2 = "annsery_q0412_02.htm";
            questState.giveItems(1278, 1L);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 30421) {
            if (n2 < 1) {
                string = questState.getQuestItemsCount(1261) < 1L ? "varika_q0412_01.htm" : "varika_q0412_04.htm";
            } else if (questState.getQuestItemsCount(1254) > 0L && questState.getQuestItemsCount(1255) > 0L && questState.getQuestItemsCount(1256) > 0L && questState.getQuestItemsCount(1253) > 0L) {
                string = "varika_q0412_16.htm";
                if (questState.getPlayer().getClassId().getLevel() == 1) {
                    questState.giveItems(1261, 1L);
                    if (!questState.getPlayer().getVarB("prof1")) {
                        questState.getPlayer().setVar("prof1", "1", -1L);
                        questState.addExpAndSp(3200L, 1650L);
                    }
                }
                questState.exitCurrentQuest(true);
                questState.playSound("ItemSound.quest_finish");
            } else if (questState.getQuestItemsCount(1254) > 0L) {
                if (questState.getQuestItemsCount(1257) < 1L && questState.getQuestItemsCount(1277) < 1L && questState.getQuestItemsCount(1278) < 1L && questState.getQuestItemsCount(1279) < 1L && questState.getQuestItemsCount(1259) < 1L && questState.getQuestItemsCount(1260) < 1L) {
                    string = "varika_q0412_17.htm";
                } else if (questState.getQuestItemsCount(1253) < 1L) {
                    string = "varika_q0412_08.htm";
                } else if (questState.getQuestItemsCount(1255) > 0L) {
                    string = "varika_q0412_19.htm";
                } else if (questState.getQuestItemsCount(1260) < 1L) {
                    string = "varika_q0412_13.htm";
                }
            }
        } else if (n == 30419 && n2 > 0 && questState.getQuestItemsCount(1256) < 1L) {
            if (questState.getQuestItemsCount(1279) < 1L && questState.getQuestItemsCount(1260) < 1L) {
                string = "arkenia_q0412_01.htm";
                questState.giveItems(1279, 1L);
            } else if (questState.getQuestItemsCount(1279) > 0L && questState.getQuestItemsCount(1260) < 3L) {
                string = "arkenia_q0412_02.htm";
            } else if (questState.getQuestItemsCount(1279) > 0L && questState.getQuestItemsCount(1260) >= 3L) {
                string = "arkenia_q0412_03.htm";
                questState.giveItems(1256, 1L);
                questState.takeItems(1260, -1L);
                questState.takeItems(1279, -1L);
            }
        } else if (n == 30415 && n2 > 0) {
            if (questState.getQuestItemsCount(1253) < 1L) {
                if (questState.getQuestItemsCount(1254) > 0L && questState.getQuestItemsCount(1257) < 1L && questState.getQuestItemsCount(1277) < 1L) {
                    string = "charkeren_q0412_01.htm";
                } else if (questState.getQuestItemsCount(1254) > 0L && questState.getQuestItemsCount(1257) < 3L && questState.getQuestItemsCount(1277) > 0L) {
                    string = "charkeren_q0412_04.htm";
                } else if (questState.getQuestItemsCount(1254) > 0L && questState.getQuestItemsCount(1257) >= 3L && questState.getQuestItemsCount(1277) > 0L) {
                    string = "charkeren_q0412_05.htm";
                    questState.giveItems(1253, 1L);
                    questState.takeItems(1257, -1L);
                    questState.takeItems(1277, -1L);
                }
            } else {
                string = "charkeren_q0412_06.htm";
            }
        } else if (n == 30418 && n2 > 0 && questState.getQuestItemsCount(1255) < 1L) {
            if (questState.getQuestItemsCount(1254) > 0L && questState.getQuestItemsCount(1278) < 1L && questState.getQuestItemsCount(1259) < 1L) {
                string = "annsery_q0412_01.htm";
            } else if (questState.getQuestItemsCount(1254) > 0L && questState.getQuestItemsCount(1278) > 0L && questState.getQuestItemsCount(1259) < 2L) {
                string = "annsery_q0412_03.htm";
            } else if (questState.getQuestItemsCount(1254) > 0L && questState.getQuestItemsCount(1278) > 0L && questState.getQuestItemsCount(1259) >= 2L) {
                string = "annsery_q0412_04.htm";
                questState.giveItems(1255, 1L);
                questState.takeItems(1278, -1L);
                questState.takeItems(1259, -1L);
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        for (int[] nArray : this.DROPLIST) {
            if (questState.getCond() != 1 || npcInstance.getNpcId() != nArray[0] || questState.getQuestItemsCount(nArray[1]) <= 0L || !Rnd.chance(50) || questState.getQuestItemsCount(nArray[2]) >= (long)nArray[3]) continue;
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

