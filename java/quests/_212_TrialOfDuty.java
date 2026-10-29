/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _212_TrialOfDuty
extends Quest
implements ScriptFile {
    private static final int aKd = 2633;
    private static final int aKe = 2634;
    private static final int aKf = 2635;
    private static final int aKg = 2636;
    private static final int aKh = 2637;
    private static final int aKi = 2638;
    private static final int aKj = 2639;
    private static final int aKk = 2640;
    private static final int aKl = 2641;
    private static final int aKm = 2642;
    private static final int aKn = 2643;
    private static final int aKo = 2644;
    private static final int aKp = 2645;
    private static final int aKq = 2646;
    private static final int aKr = 3027;
    private static final int aKb = 79832;
    private static final int aKc = 37500;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _212_TrialOfDuty() {
        super(0);
        this.addStartNpc(30109);
        this.addTalkId(30109);
        this.addTalkId(30109);
        this.addTalkId(30109);
        this.addTalkId(30116);
        this.addTalkId(30311);
        int n = 30653;
        while (n < 30657) {
            this.addTalkId(n++);
        }
        this.addKillId(20144);
        this.addKillId(20190);
        this.addKillId(20191);
        this.addKillId(20200);
        this.addKillId(20201);
        this.addKillId(20270);
        this.addKillId(27119);
        n = 20577;
        while (n < 20583) {
            this.addKillId(n++);
        }
        this.addQuestItem(2634, 2635, 3027, 2637, 2636, 2639, 2641, 2643, 2644, 2645, 2646, 2640, 2642, 2638);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("1")) {
            string2 = "hannavalt_q0212_04.htm";
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            questState.setCond(1);
        } else if (string.equalsIgnoreCase("30116_1")) {
            string2 = "dustin_q0212_02.htm";
        } else if (string.equalsIgnoreCase("30116_2")) {
            string2 = "dustin_q0212_03.htm";
        } else if (string.equalsIgnoreCase("30116_3")) {
            string2 = "dustin_q0212_04.htm";
        } else if (string.equalsIgnoreCase("30116_4")) {
            string2 = "dustin_q0212_05.htm";
            questState.takeItems(2640, 1L);
            questState.setCond(14);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        if (questState.getQuestItemsCount(2633) > 0L) {
            questState.exitCurrentQuest(true);
            return "completed";
        }
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getState();
        int n3 = questState.getCond();
        if (n == 30109 && n2 == 1) {
            if (questState.getPlayer().getClassId().ordinal() == 4 || questState.getPlayer().getClassId().ordinal() == 19 || questState.getPlayer().getClassId().ordinal() == 32) {
                if (questState.getPlayer().getLevel() >= 35) {
                    string = "hannavalt_q0212_03.htm";
                } else {
                    string = "hannavalt_q0212_01.htm";
                    questState.exitCurrentQuest(true);
                }
            } else {
                string = "hannavalt_q0212_02.htm";
                questState.exitCurrentQuest(true);
            }
        } else if (n == 30109) {
            if (n3 == 18 && questState.getQuestItemsCount(2634) > 0L) {
                string = "hannavalt_q0212_05.htm";
                questState.takeItems(2634, -1L);
                questState.giveItems(2633, 1L);
                if (!questState.getPlayer().getVarB("prof2.1")) {
                    questState.giveItems(7562, 8L);
                    questState.addExpAndSp(79832L, 37500L);
                    questState.getPlayer().setVar("prof2.1", "1", -1L);
                }
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(false);
            } else if (n3 == 1) {
                string = "hannavalt_q0212_04.htm";
            }
        } else if (n == 30653) {
            if (n3 == 1) {
                string = "sir_aron_tanford_q0212_01.htm";
                if (questState.getQuestItemsCount(3027) == 0L) {
                    questState.giveItems(3027, 1L);
                }
                questState.setCond(2);
            } else if (n3 == 2 && questState.getQuestItemsCount(2635) == 0L) {
                string = "sir_aron_tanford_q0212_02.htm";
            } else if (n3 == 3 && questState.getQuestItemsCount(2635) > 0L) {
                string = "sir_aron_tanford_q0212_03.htm";
                questState.takeItems(2635, 1L);
                questState.takeItems(3027, 1L);
                questState.setCond(4);
            } else if (n3 == 4) {
                string = "sir_aron_tanford_q0212_04.htm";
            }
        } else if (n == 30654) {
            if (n3 == 4) {
                string = "sir_kiel_nighthawk_q0212_01.htm";
                questState.setCond(5);
            } else if (n3 == 5 && questState.getQuestItemsCount(2639) == 0L) {
                string = "sir_kiel_nighthawk_q0212_02.htm";
            } else if (n3 == 6 && questState.getQuestItemsCount(2639) > 0L) {
                string = "sir_kiel_nighthawk_q0212_03.htm";
                questState.setCond(7);
                questState.giveItems(2636, 1L);
            } else if (n3 == 6 && questState.getQuestItemsCount(2636) > 0L) {
                string = "sir_kiel_nighthawk_q0212_04.htm";
            } else if (questState.getQuestItemsCount(2637) > 0L) {
                string = "sir_kiel_nighthawk_q0212_05.htm";
                questState.takeItems(2637, 1L);
                questState.setCond(10);
            } else if (n3 == 10) {
                string = "sir_kiel_nighthawk_q0212_06.htm";
            }
        } else if (n == 30656 && n3 == 8 && questState.getQuestItemsCount(2636) > 0L) {
            string = "spirit_of_sir_talianus_q0212_01.htm";
            questState.takeItems(2636, 1L);
            questState.takeItems(2639, 1L);
            questState.giveItems(2637, 1L);
            questState.setCond(9);
        } else if (n == 30655) {
            if (n3 == 10) {
                if (questState.getPlayer().getLevel() >= 36) {
                    string = "isael_silvershadow_q0212_02.htm";
                    questState.setCond(11);
                } else {
                    string = "isael_silvershadow_q0212_01.htm";
                }
            } else if (n3 == 11) {
                string = "isael_silvershadow_q0212_03.htm";
            } else if (n3 == 12 && questState.getQuestItemsCount(2641) >= 20L) {
                string = "isael_silvershadow_q0212_04.htm";
                questState.takeItems(2641, questState.getQuestItemsCount(2641));
                questState.giveItems(2640, 1L);
                questState.setCond(13);
            } else if (n3 == 13) {
                string = "isael_silvershadow_q0212_05.htm";
            }
        } else if (n == 30116) {
            if (n3 == 13 && questState.getQuestItemsCount(2640) > 0L) {
                string = "dustin_q0212_01.htm";
            } else if (n3 == 14 && (questState.getQuestItemsCount(2643) <= 0L || questState.getQuestItemsCount(2644) <= 0L || questState.getQuestItemsCount(2645) <= 0L)) {
                string = "dustin_q0212_06.htm";
            } else if (n3 == 15) {
                string = "dustin_q0212_07.htm";
                questState.takeItems(2643, 1L);
                questState.takeItems(2644, 1L);
                questState.takeItems(2645, 1L);
                questState.giveItems(2642, 1L);
                questState.setCond(16);
            } else if (n3 == 17 && questState.getQuestItemsCount(2646) > 0L) {
                string = "dustin_q0212_08.htm";
                questState.takeItems(2646, 1L);
                questState.giveItems(2634, 1L);
                questState.setCond(18);
            } else if (n3 == 16) {
                string = "dustin_q0212_09.htm";
            } else if (n3 == 18) {
                string = "dustin_q0212_10.htm";
            }
        } else if (n == 30311) {
            if (n3 == 16 && questState.getQuestItemsCount(2642) > 0L) {
                string = "sir_collin_windawood_q0212_01.htm";
                questState.takeItems(2642, 1L);
                questState.giveItems(2646, 1L);
                questState.setCond(17);
            } else if (n3 == 17) {
                string = "sir_collin_windawood_q0212_02.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 20190 || n == 20191) {
            if (n2 == 2 && Rnd.chance(10)) {
                questState.addSpawn(27119);
                questState.playSound("Itemsound.quest_before_battle");
            }
        } else if (n == 27119 && n2 == 2 && questState.getQuestItemsCount(3027) > 0L) {
            questState.giveItems(2635, 1L);
            questState.playSound("ItemSound.quest_middle");
            questState.setCond(3);
        } else if ((n == 20200 || n == 20201) && n2 == 5 && questState.getQuestItemsCount(2639) == 0L) {
            if (Rnd.chance(50)) {
                questState.giveItems(2638, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
            if (questState.getQuestItemsCount(2638) >= 10L) {
                questState.takeItems(2638, questState.getQuestItemsCount(2638));
                questState.giveItems(2639, 1L);
                questState.setCond(6);
                questState.playSound("ItemSound.quest_middle");
            }
        } else if (n == 20144 && n2 == 7 && Rnd.chance(20)) {
            questState.addSpawn(30656, npcInstance.getX(), npcInstance.getY(), npcInstance.getZ(), 300000);
            questState.setCond(8);
            questState.playSound("ItemSound.quest_middle");
        } else if (n >= 20577 && n <= 20582 && n2 == 11 && questState.getQuestItemsCount(2641) < 20L) {
            if (questState.getQuestItemsCount(2641) == 19L) {
                questState.giveItems(2641, 1L);
                questState.setCond(12);
                questState.playSound("ItemSound.quest_middle");
            } else {
                questState.giveItems(2641, 1L);
                questState.playSound("ItemSound.quest_itemget");
            }
        } else if (n == 20270 && n2 == 14 && Rnd.chance(50)) {
            if (questState.getQuestItemsCount(2643) == 0L) {
                questState.giveItems(2643, 1L);
                questState.playSound("ItemSound.quest_itemget");
            } else if (questState.getQuestItemsCount(2644) == 0L) {
                questState.giveItems(2644, 1L);
                questState.playSound("ItemSound.quest_itemget");
            } else if (questState.getQuestItemsCount(2645) == 0L) {
                questState.giveItems(2645, 1L);
                questState.setCond(15);
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

