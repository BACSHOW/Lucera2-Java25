/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _337_AudienceWithLandDragon
extends Quest
implements ScriptFile {
    public final int MOKE = 30498;
    public final int HELTON = 30678;
    public final int CHAKIRIS = 30705;
    public final int KAIENA = 30720;
    public final int GABRIELLE = 30753;
    public final int GILMORE = 30754;
    public final int THEODRIC = 30755;
    public final int KENDRA = 30851;
    public final int ORVEN = 30857;
    public final int MARSH_STALKER = 20679;
    public final int MARSH_DRAKE = 20680;
    public final int BLOOD_QUEEN = 18001;
    public final int HARIT_LIZARDMAN_SHAMAN = 20644;
    public final int HARIT_LIZARDMAN_MATRIARCH = 20645;
    public final int HAMRUT = 20649;
    public final int KRANROT = 20650;
    public final int CAVE_MAIDEN = 20134;
    public final int CAVE_KEEPER = 20246;
    public final int ABYSSAL_JEWEL_1 = 27165;
    public final int ABYSSAL_JEWEL_2 = 27166;
    public final int ABYSSAL_JEWEL_3 = 27167;
    public final int JEWEL_GUARDIAN_MARA = 27168;
    public final int JEWEL_GUARDIAN_MUSFEL = 27169;
    public final int JEWEL_GUARDIAN_PYTON = 27170;
    public final int SACRIFICE_OF_THE_SACRIFICED = 27171;
    public final int HARIT_LIZARDMAN_ZEALOT = 27172;
    public final int FEATHER_OF_GABRIELLE_ID = 3852;
    public final int STALKER_HORN_ID = 3853;
    public final int DRAKE_TALON_ID = 3854;
    public final int REMAINS_OF_SACRIFICED_ID = 3857;
    public final int TOTEM_OF_LAND_DRAGON_ID = 3858;
    public final int HAMRUT_LEG_ID = 3856;
    public final int KRANROT_SKIN_ID = 3855;
    public final int MARA_FANG_ID = 3862;
    public final int MUSFEL_FANG_ID = 3863;
    public final int FIRST_ABYSS_FRAGMENT_ID = 3859;
    public final int SECOND_ABYSS_FRAGMENT_ID = 3860;
    public final int THIRD_ABYSS_FRAGMENT_ID = 3861;
    public final int HERALD_OF_SLAYER_ID = 3890;
    public final int PORTAL_STONE_ID = 3865;
    public final int MARK_OF_WATCHMAN_ID = 3864;
    public final int[][] DROPLIST = new int[][]{{2, 20679, 3853, 1, 50, 1}, {2, 20680, 3854, 1, 50, 1}, {4, 27171, 3857, 1, 50, 1}, {6, 27172, 3858, 1, 50, 1}, {8, 20649, 3856, 1, 50, 1}, {8, 20650, 3855, 1, 50, 1}, {11, 27168, 3862, 1, 50, 1}, {11, 27165, 3859, 1, 100, 1}, {13, 27169, 3863, 1, 50, 1}, {13, 27166, 3860, 1, 100, 1}, {16, 27167, 3861, 1, 100, 1}};
    public final int[][] SPAWNLIST = new int[][]{{4, 18001, 27171, 6}, {6, 20644, 27172, 1}, {6, 20645, 27172, 1}, {11, 27165, 27168, 4}, {13, 27166, 27169, 4}, {16, 20246, 27167, 1}, {16, 20134, 27167, 1}, {16, 27167, 27170, 6}};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _337_AudienceWithLandDragon() {
        super(0);
        this.addStartNpc(30753);
        this.addTalkId(30498);
        this.addTalkId(30678);
        this.addTalkId(30705);
        this.addTalkId(30720);
        this.addTalkId(30754);
        this.addTalkId(30755);
        this.addTalkId(30851);
        this.addTalkId(30857);
        this.addKillId(18001);
        this.addKillId(20679);
        this.addKillId(20680);
        this.addKillId(27171);
        this.addKillId(20644);
        this.addKillId(20645);
        this.addKillId(27172);
        this.addKillId(20649);
        this.addKillId(20650);
        this.addKillId(27165);
        this.addKillId(27166);
        this.addKillId(20246);
        this.addKillId(20134);
        this.addKillId(27167);
        this.addKillId(27168);
        this.addKillId(27169);
        this.addKillId(27170);
        this.addAttackId(27165);
        this.addAttackId(27166);
        this.addAttackId(27167);
        this.addQuestItem(3852, 3890, 3853, 3854, 3857, 3858, 3856, 3855, 3862, 3859, 3863, 3860, 3861, 3864);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("1")) {
            questState.set("step", "1");
            questState.setCond(1);
            questState.set("guard", "0");
            questState.setState(2);
            questState.giveItems(3852, 1L);
            string2 = "30753-02.htm";
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("2")) {
            questState.set("step", "2");
            string2 = "30720-02.htm";
        } else if (string.equalsIgnoreCase("4")) {
            questState.set("step", "4");
            string2 = "30857-02.htm";
        } else if (string.equalsIgnoreCase("6")) {
            questState.set("step", "6");
            string2 = "30851-02.htm";
        } else if (string.equalsIgnoreCase("8")) {
            questState.set("step", "8");
            string2 = "30705-02.htm";
        } else if (string.equalsIgnoreCase("10")) {
            questState.takeItems(3864, -1L);
            questState.set("step", "10");
            questState.setCond(2);
            string2 = "30753-05.htm";
        } else if (string.equalsIgnoreCase("11")) {
            questState.set("step", "11");
            string2 = "30498-02.htm";
        } else if (string.equalsIgnoreCase("13")) {
            questState.set("step", "13");
            string2 = "30678-02.htm";
        } else if (string.equalsIgnoreCase("15")) {
            questState.set("step", "15");
            questState.setCond(3);
            string2 = "30753-06.htm";
            questState.takeItems(3864, -1L);
            questState.takeItems(3852, -1L);
            questState.giveItems(3890, 1L);
            questState.playSound("ItemSound.quest_middle");
        } else if (string.equalsIgnoreCase("16")) {
            questState.set("step", "16");
            questState.setCond(4);
            string2 = "30754-02.htm";
            questState.takeItems(3890, -1L);
            questState.playSound("ItemSound.quest_middle");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getInt("step");
        int n3 = questState.getCond();
        if (n == 30753) {
            if (n3 == 0) {
                if (questState.getPlayer().getLevel() < 50) {
                    string = "30753-00.htm";
                    questState.exitCurrentQuest(true);
                } else {
                    string = "30753-01.htm";
                }
            } else if (n2 < 9) {
                string = "30753-02.htm";
            } else if (n2 == 9) {
                string = "30753-03.htm";
            } else if (n2 > 9 && n2 < 14) {
                string = "30753-05.htm";
            } else if (n2 == 14) {
                string = "30753-04.htm";
            } else if (n2 > 14) {
                string = "30753-06.htm";
            }
        } else if (n == 30720 && n3 == 1 && n2 < 4) {
            if (questState.getQuestItemsCount(3853) < 1L && questState.getQuestItemsCount(3854) < 1L && n2 == 1) {
                string = "30720-01.htm";
            } else if (questState.getQuestItemsCount(3853) > 0L && questState.getQuestItemsCount(3854) > 0L) {
                string = "30720-03.htm";
                questState.takeItems(3853, -1L);
                questState.takeItems(3854, -1L);
                questState.giveItems(3864, 1L);
                questState.set("step", "3");
                questState.playSound("ItemSound.quest_middle");
            } else if (n2 == 2) {
                string = "30720-02.htm";
            } else if (n2 == 3) {
                string = "30720-03.htm";
            }
        } else if (n == 30857 && n3 == 1 && n2 > 2 && n2 < 6) {
            if (questState.getQuestItemsCount(3857) < 1L && n2 == 3) {
                string = "30857-01.htm";
            } else if (questState.getQuestItemsCount(3857) > 0L) {
                string = "30857-03.htm";
                questState.takeItems(3857, -1L);
                questState.giveItems(3864, 1L);
                questState.set("step", "5");
                questState.playSound("ItemSound.quest_middle");
            } else if (n2 == 4) {
                string = "30857-02.htm";
            } else if (n2 == 5) {
                string = "30857-03.htm";
            }
        } else if (n == 30851 && n3 == 1 && n2 > 4 && n2 < 8) {
            if (questState.getQuestItemsCount(3858) < 1L && n2 == 5) {
                string = "30851-01.htm";
            } else if (questState.getQuestItemsCount(3858) > 0L) {
                string = "30851-03.htm";
                questState.takeItems(3858, -1L);
                questState.giveItems(3864, 1L);
                questState.set("step", "7");
                questState.playSound("ItemSound.quest_middle");
            } else if (n2 == 6) {
                string = "30851-02.htm";
            } else if (n2 == 7) {
                string = "30851-03.htm";
            }
        } else if (n == 30705 && n3 == 1 && n2 > 6 && n2 < 10) {
            if (questState.getQuestItemsCount(3856) < 1L && questState.getQuestItemsCount(3855) < 1L && n2 == 7) {
                string = "30705-01.htm";
            } else if (questState.getQuestItemsCount(3856) > 0L && questState.getQuestItemsCount(3855) > 0L) {
                string = "30705-03.htm";
                questState.takeItems(3856, -1L);
                questState.takeItems(3855, -1L);
                questState.giveItems(3864, 1L);
                questState.set("step", "9");
                questState.playSound("ItemSound.quest_middle");
            } else if (n2 == 8) {
                string = "30705-02.htm";
            } else if (n2 == 9) {
                string = "30705-03.htm";
            }
        } else if (n == 30498 && n3 == 2 && n2 < 13) {
            if (questState.getQuestItemsCount(3862) < 1L && questState.getQuestItemsCount(3859) < 1L && n2 == 10) {
                string = "30498-01.htm";
            } else if (questState.getQuestItemsCount(3862) > 0L && questState.getQuestItemsCount(3859) > 0L) {
                string = "30498-03.htm";
                questState.takeItems(3862, -1L);
                questState.takeItems(3859, -1L);
                questState.giveItems(3864, 1L);
                questState.set("step", "12");
                questState.playSound("ItemSound.quest_middle");
            } else if (n2 == 11) {
                string = "30498-02.htm";
            } else if (n2 == 12) {
                string = "30498-03.htm";
            }
        } else if (n == 30678 && n3 == 2 && n2 > 11 && n2 < 15) {
            if (questState.getQuestItemsCount(3863) < 1L && questState.getQuestItemsCount(3860) < 1L && n2 == 12) {
                string = "30678-01.htm";
            } else if (questState.getQuestItemsCount(3863) > 0L && questState.getQuestItemsCount(3860) > 0L) {
                string = "30678-03.htm";
                questState.takeItems(3863, -1L);
                questState.takeItems(3860, -1L);
                questState.giveItems(3864, 1L);
                questState.set("step", "14");
                questState.playSound("ItemSound.quest_middle");
            } else if (n2 == 13) {
                string = "30678-02.htm";
            } else if (n2 == 14) {
                string = "30678-03.htm";
            }
        } else if (n == 30754 && n2 < 17) {
            if (questState.getQuestItemsCount(3890) > 0L && n3 == 3) {
                string = "30754-01.htm";
            } else if (n3 == 4) {
                string = "30754-02.htm";
            }
        } else if (n == 30755 && n3 == 4 && n2 == 16) {
            if (questState.getQuestItemsCount(3861) < 1L) {
                string = "30755-02.htm";
            } else {
                string = "30755-01.htm";
                questState.takeItems(3861, -1L);
                questState.unset("step");
                questState.unset("cond");
                questState.unset("guard");
                questState.exitCurrentQuest(true);
                questState.giveItems(3865, 1L);
                questState.playSound("ItemSound.quest_finish");
            }
        }
        return string;
    }

    @Override
    public String onAttack(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getInt("step");
        for (int[] nArray : this.SPAWNLIST) {
            if (n != nArray[1] || n2 != nArray[0] || !(npcInstance.getCurrentHpPercents() < 50.0) || questState.getInt("guard") != 0) continue;
            for (int i = 0; i < nArray[3]; ++i) {
                questState.addSpawn(nArray[2]);
            }
            questState.playSound("Itemsound.quest_before_battle");
            questState.set("guard", "1");
        }
        return null;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getInt("step");
        for (int[] nArray : this.DROPLIST) {
            if (n != nArray[1] || n2 != nArray[0] || questState.getQuestItemsCount(nArray[2]) >= (long)nArray[3] || !Rnd.chance(nArray[4])) continue;
            questState.giveItems(nArray[2], nArray[5]);
            questState.playSound("ItemSound.quest_itemget");
        }
        for (int[] nArray : this.SPAWNLIST) {
            if (n2 == nArray[0] && n == nArray[1] && Rnd.chance(50) && questState.getInt("guard") == 0) {
                for (int i = 0; i < nArray[3]; ++i) {
                    questState.addSpawn(nArray[2]);
                }
                questState.playSound("Itemsound.quest_before_battle");
            }
            if (n2 != nArray[0] || n != nArray[1] || questState.getInt("guard") != 1) continue;
            questState.set("guard", "0");
        }
        return null;
    }
}

