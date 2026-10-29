/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _217_TestimonyOfTrust
extends Quest
implements ScriptFile {
    private static final int aNo = 2734;
    private static final int aNp = 1558;
    private static final int aNq = 1556;
    private static final int aNr = 2737;
    private static final int aNs = 2738;
    private static final int aNt = 2739;
    private static final int aNu = 2740;
    private static final int aNv = 2741;
    private static final int aNw = 2742;
    private static final int aNx = 2743;
    private static final int aNy = 2744;
    private static final int aNz = 2745;
    private static final int aNA = 2746;
    private static final int aNB = 2747;
    private static final int aNC = 2748;
    private static final int aND = 2749;
    private static final int aNE = 2750;
    private static final int aNF = 2751;
    private static final int aNG = 2752;
    private static final int aNH = 2753;
    private static final int aNI = 2754;
    private static final int aNJ = 2755;
    private static final int aNK = 2756;
    private static final int aNL = 2757;
    private static final int aNM = 2758;
    private static final int aNN = 2759;
    private static final int aNO = 2760;
    private static final int aNP = 2761;
    private static final int aKb = 39571;
    private static final int aKc = 2500;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _217_TestimonyOfTrust() {
        super(0);
        this.addStartNpc(30191);
        this.addTalkId(30031);
        this.addTalkId(30154);
        this.addTalkId(30358);
        this.addTalkId(30464);
        this.addTalkId(30515);
        this.addTalkId(30531);
        this.addTalkId(30565);
        this.addTalkId(30621);
        this.addTalkId(30657);
        this.addKillId(20013);
        this.addKillId(20157);
        this.addKillId(20019);
        this.addKillId(20213);
        this.addKillId(20230);
        this.addKillId(20232);
        this.addKillId(20234);
        this.addKillId(20036);
        this.addKillId(20044);
        this.addKillId(27120);
        this.addKillId(27121);
        this.addKillId(20550);
        this.addKillId(20553);
        this.addKillId(20082);
        this.addKillId(20084);
        this.addKillId(20086);
        this.addKillId(20087);
        this.addKillId(20088);
        this.addQuestItem(2740, 2741, 2742, 2743, 2746, 2747, 2745, 1558, 2755, 2752, 2754, 2753, 1556, 2748, 2739, 2738, 2758, 2757, 2756, 2737, 2759, 2761, 2760, 2744, 2749, 2751, 2750);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("1")) {
            string2 = "hollin_q0217_04.htm";
            questState.setCond(1);
            questState.set("id", "0");
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            questState.giveItems(1558, 1L);
            questState.giveItems(1556, 1L);
        } else if (string.equalsIgnoreCase("30154_1")) {
            string2 = "ozzy_q0217_02.htm";
        } else if (string.equalsIgnoreCase("30154_2")) {
            string2 = "ozzy_q0217_03.htm";
            questState.takeItems(1558, 1L);
            questState.giveItems(2745, 1L);
            questState.setCond(2);
        } else if (string.equalsIgnoreCase("30358_1")) {
            string2 = "tetrarch_thifiell_q0217_02.htm";
            questState.takeItems(1556, 1L);
            questState.giveItems(2748, 1L);
            questState.setCond(5);
        } else if (string.equalsIgnoreCase("30657_1")) {
            if (questState.getPlayer().getLevel() >= 38) {
                string2 = "cardinal_seresin_q0217_03.htm";
                questState.takeItems(2739, 1L);
                questState.giveItems(2738, 1L);
                questState.giveItems(2737, 1L);
                questState.setCond(12);
            } else {
                string2 = "cardinal_seresin_q0217_02.htm";
            }
        } else if (string.equalsIgnoreCase("30565_1")) {
            string2 = "kakai_the_lord_of_flame_q0217_02.htm";
            questState.takeItems(2738, 1L);
            questState.giveItems(2757, 1L);
            questState.setCond(13);
        } else if (string.equalsIgnoreCase("30515_1")) {
            string2 = "seer_manakia_q0217_02.htm";
            questState.takeItems(2757, 1L);
            questState.setCond(14);
        } else if (string.equalsIgnoreCase("30531_1")) {
            string2 = "first_elder_lockirin_q0217_02.htm";
            questState.takeItems(2737, 1L);
            questState.giveItems(2759, 1L);
            questState.setCond(18);
        } else if (string.equalsIgnoreCase("30621_1")) {
            string2 = "maestro_nikola_q0217_02.htm";
            questState.takeItems(2759, 1L);
            questState.giveItems(2760, 1L);
            questState.setCond(19);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        if (questState.getQuestItemsCount(2734) > 0L) {
            questState.exitCurrentQuest(true);
            return "completed";
        }
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 30191) {
            if (n2 == 0) {
                if (questState.getPlayer().getRace() == Race.human) {
                    if (questState.getPlayer().getLevel() >= 37) {
                        string = "hollin_q0217_03.htm";
                    } else {
                        string = "hollin_q0217_01.htm";
                        questState.exitCurrentQuest(true);
                    }
                } else {
                    string = "hollin_q0217_02.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 9 && questState.getQuestItemsCount(2741) > 0L && questState.getQuestItemsCount(2740) > 0L) {
                string = "hollin_q0217_05.htm";
                questState.takeItems(2740, 1L);
                questState.takeItems(2741, 1L);
                questState.giveItems(2739, 1L);
                questState.setCond(10);
            } else if (n2 == 22 && questState.getQuestItemsCount(2742) > 0L && questState.getQuestItemsCount(2743) > 0L) {
                string = "hollin_q0217_06.htm";
                questState.takeItems(2742, 1L);
                questState.takeItems(2743, 1L);
                questState.giveItems(2744, 1L);
                questState.setCond(23);
            } else if (n2 == 19) {
                string = "hollin_q0217_07.htm";
            } else if (n2 == 1) {
                string = "hollin_q0217_08.htm";
            } else if (n2 == 8) {
                string = "hollin_q0217_09.htm";
            }
        } else if (n == 30154) {
            if (n2 == 1 && questState.getQuestItemsCount(1558) > 0L) {
                string = "ozzy_q0217_01.htm";
            } else if (n2 == 2 && questState.getQuestItemsCount(2745) > 0L) {
                string = "ozzy_q0217_04.htm";
            } else if (n2 == 3 && questState.getQuestItemsCount(2746) > 0L && questState.getQuestItemsCount(2747) > 0L) {
                string = "ozzy_q0217_05.htm";
                questState.takeItems(2746, 1L);
                questState.takeItems(2747, 1L);
                questState.takeItems(2745, 1L);
                questState.giveItems(2741, 1L);
                questState.setCond(4);
            } else if (n2 == 4) {
                string = "ozzy_q0217_06.htm";
            }
        } else if (n == 30358) {
            if (n2 == 4 && questState.getQuestItemsCount(1556) > 0L) {
                string = "tetrarch_thifiell_q0217_01.htm";
            } else if (n2 == 8 && questState.getQuestItemsCount(2754) + questState.getQuestItemsCount(2753) + questState.getQuestItemsCount(2752) == 3L) {
                questState.takeItems(2752, 1L);
                questState.takeItems(2754, 1L);
                questState.takeItems(2753, 1L);
                questState.giveItems(2740, 1L);
                questState.setCond(9);
                string = "tetrarch_thifiell_q0217_03.htm";
            } else if (n2 == 7) {
                string = "tetrarch_thifiell_q0217_04.htm";
            } else if (n2 == 5) {
                string = "tetrarch_thifiell_q0217_05.htm";
            }
        } else if (n == 30464) {
            if (n2 == 5 && questState.getQuestItemsCount(2748) > 0L) {
                string = "magister_clayton_q0217_01.htm";
                questState.takeItems(2748, 1L);
                questState.giveItems(2755, 1L);
                questState.setCond(6);
            } else if (n2 == 6 && questState.getQuestItemsCount(2755) > 0L && questState.getQuestItemsCount(2754) + questState.getQuestItemsCount(2753) + questState.getQuestItemsCount(2752) < 3L) {
                string = "magister_clayton_q0217_02.htm";
            } else if (n2 == 7 && questState.getQuestItemsCount(2755) > 0L && questState.getQuestItemsCount(2754) + questState.getQuestItemsCount(2753) + questState.getQuestItemsCount(2752) == 3L) {
                questState.takeItems(2755, 1L);
                questState.setCond(8);
                string = "magister_clayton_q0217_03.htm";
            }
        } else if (n == 30657) {
            if ((n2 == 10 || n2 == 11) && questState.getQuestItemsCount(2739) > 0L && questState.getPlayer().getLevel() >= 38) {
                string = "cardinal_seresin_q0217_01.htm";
            } else if ((n2 == 10 || n2 == 11) && questState.getPlayer().getLevel() < 38) {
                string = "cardinal_seresin_q0217_02.htm";
                if (n2 == 10) {
                    questState.setCond(11);
                }
            } else if (n2 == 18) {
                string = "cardinal_seresin_q0217_05.htm";
            }
        } else if (n == 30565) {
            if (n2 == 12 && questState.getQuestItemsCount(2738) > 0L) {
                string = "kakai_the_lord_of_flame_q0217_01.htm";
            } else if (n2 == 13) {
                string = "kakai_the_lord_of_flame_q0217_03.htm";
            } else if (n2 == 16) {
                string = "kakai_the_lord_of_flame_q0217_04.htm";
                questState.takeItems(2758, 1L);
                questState.giveItems(2743, 1L);
                questState.setCond(17);
            } else if (n2 >= 17) {
                string = "kakai_the_lord_of_flame_q0217_05.htm";
            }
        } else if (n == 30515) {
            if (n2 == 13 && questState.getQuestItemsCount(2757) > 0L) {
                string = "seer_manakia_q0217_01.htm";
            } else if (n2 == 14 && questState.getQuestItemsCount(2756) < 10L) {
                string = "seer_manakia_q0217_03.htm";
            } else if (n2 == 15 && questState.getQuestItemsCount(2756) == 10L) {
                string = "seer_manakia_q0217_04.htm";
                questState.takeItems(2756, -1L);
                questState.giveItems(2758, 1L);
                questState.setCond(16);
            } else if (n2 == 16) {
                string = "seer_manakia_q0217_05.htm";
            }
        } else if (n == 30531) {
            if (n2 == 17 && questState.getQuestItemsCount(2737) > 0L) {
                string = "first_elder_lockirin_q0217_01.htm";
            } else if (n2 == 18) {
                string = "first_elder_lockirin_q0217_03.htm";
            } else if (n2 == 21) {
                string = "first_elder_lockirin_q0217_04.htm";
                questState.giveItems(2742, 1L);
                questState.setCond(22);
            } else if (n2 == 22) {
                string = "first_elder_lockirin_q0217_05.htm";
            }
        } else if (n == 30621) {
            if (n2 == 18 && questState.getQuestItemsCount(2759) > 0L) {
                string = "maestro_nikola_q0217_01.htm";
            } else if (n2 == 19 && questState.getQuestItemsCount(2761) < 1L) {
                string = "maestro_nikola_q0217_03.htm";
            } else if (n2 == 20 && questState.getQuestItemsCount(2761) >= 1L) {
                string = "maestro_nikola_q0217_04.htm";
                questState.takeItems(2761, -1L);
                questState.takeItems(2760, 1L);
                questState.setCond(21);
            } else if (n2 == 21) {
                string = "maestro_nikola_q0217_05.htm";
            }
        } else if (n == 30031 && n2 == 23 && questState.getQuestItemsCount(2744) > 0L) {
            string = "quilt_q0217_01.htm";
            questState.takeItems(2744, -1L);
            questState.giveItems(2734, 1L);
            if (!questState.getPlayer().getVarB("prof2.2")) {
                questState.giveItems(7562, 8L);
                questState.addExpAndSp(39571L, 2500L);
                questState.getPlayer().setVar("prof2.2", "1", -1L);
            }
            questState.playSound("ItemSound.quest_finish");
            questState.unset("cond");
            questState.exitCurrentQuest(true);
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 20036 || n == 20044) {
            if (n2 == 2 && questState.getQuestItemsCount(2746) == 0L) {
                questState.set("id", String.valueOf(questState.getInt("id") + 1));
                if (Rnd.chance(questState.getInt("id") * 33)) {
                    questState.addSpawn(27120);
                    questState.playSound("Itemsound.quest_before_battle");
                }
            }
        } else if (n == 20013 || n == 20019) {
            if (n2 == 2 && questState.getQuestItemsCount(2747) == 0L) {
                questState.set("id", String.valueOf(questState.getInt("id") + 1));
                if (Rnd.chance(questState.getInt("id") * 33)) {
                    questState.addSpawn(27121);
                    questState.playSound("Itemsound.quest_before_battle");
                }
            }
        } else if (n == 27120) {
            if (n2 == 2 && questState.getQuestItemsCount(2746) == 0L) {
                if (questState.getQuestItemsCount(2747) > 0L) {
                    questState.giveItems(2746, 1L);
                    questState.setCond(3);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.giveItems(2746, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 27121) {
            if (n2 == 2 && questState.getQuestItemsCount(2747) == 0L) {
                if (questState.getQuestItemsCount(2746) > 0L) {
                    questState.giveItems(2747, 1L);
                    questState.setCond(3);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.giveItems(2747, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20550) {
            if (n2 == 6 && questState.getQuestItemsCount(2749) < 10L && questState.getQuestItemsCount(2755) > 0L && questState.getQuestItemsCount(2752) == 0L) {
                if (questState.getQuestItemsCount(2749) == 9L) {
                    questState.takeItems(2749, -1L);
                    questState.giveItems(2752, 1L);
                    if (questState.getQuestItemsCount(2754) + questState.getQuestItemsCount(2752) + questState.getQuestItemsCount(2753) == 3L) {
                        questState.setCond(7);
                    }
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.giveItems(2749, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20157 || n == 20230 || n == 20232 || n == 20234) {
            if (n2 == 6 && questState.getQuestItemsCount(2751) < 10L && questState.getQuestItemsCount(2755) > 0L && questState.getQuestItemsCount(2754) == 0L) {
                if (questState.getQuestItemsCount(2751) == 9L) {
                    questState.takeItems(2751, -1L);
                    questState.giveItems(2754, 1L);
                    if (questState.getQuestItemsCount(2754) + questState.getQuestItemsCount(2752) + questState.getQuestItemsCount(2753) == 3L) {
                        questState.setCond(7);
                    }
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.giveItems(2751, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20082 || n == 20086 || n == 20087 || n == 20084 || n == 20088) {
            if (n2 == 6 && questState.getQuestItemsCount(2750) < 10L && questState.getQuestItemsCount(2755) > 0L && questState.getQuestItemsCount(2753) == 0L) {
                if (questState.getQuestItemsCount(2750) == 9L) {
                    questState.takeItems(2750, -1L);
                    questState.giveItems(2753, 1L);
                    if (questState.getQuestItemsCount(2754) + questState.getQuestItemsCount(2752) + questState.getQuestItemsCount(2753) == 3L) {
                        questState.setCond(7);
                    }
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.giveItems(2750, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20553) {
            if (n2 == 14 && questState.getQuestItemsCount(2756) < 10L && Rnd.chance(50)) {
                if (questState.getQuestItemsCount(2756) == 9L) {
                    questState.giveItems(2756, 1L);
                    questState.setCond(15);
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    questState.giveItems(2756, 1L);
                    questState.playSound("ItemSound.quest_itemget");
                }
            }
        } else if (n == 20213 && n2 == 19 && questState.getQuestItemsCount(2761) < 1L) {
            questState.giveItems(2761, 1L);
            questState.setCond(20);
            questState.playSound("ItemSound.quest_middle");
        }
        return null;
    }
}

