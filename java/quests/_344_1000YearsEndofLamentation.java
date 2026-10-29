/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _344_1000YearsEndofLamentation
extends Quest
implements ScriptFile {
    private static final int bng = 4269;
    private static final int bnh = 4270;
    private static final int bni = 4271;
    private static final int bnj = 4272;
    private static final int bnk = 4273;
    private static final int CHANCE = 36;
    private static final int bnl = 1000;
    private static final int GILMORE = 30754;
    private static final int bnm = 30756;
    private static final int ORVEN = 30857;
    private static final int bnn = 30623;
    private static final int bno = 30704;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _344_1000YearsEndofLamentation() {
        super(1);
        this.addStartNpc(30754);
        this.addTalkId(30756);
        this.addTalkId(30857);
        this.addTalkId(30704);
        this.addTalkId(30623);
        int n = 20236;
        while (n < 20241) {
            this.addKillId(n++);
        }
        this.addQuestItem(4269, 4270, 4271, 4272, 4273);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        long l = questState.getQuestItemsCount(4269);
        int n = questState.getCond();
        int n2 = questState.getPlayer().getLevel();
        if (string.equalsIgnoreCase("30754-04.htm")) {
            if (n2 >= 48 && n == 0) {
                questState.setState(2);
                questState.setCond(1);
                questState.playSound("ItemSound.quest_accept");
            } else {
                string2 = "noquest";
                questState.exitCurrentQuest(true);
            }
        } else if (string.equalsIgnoreCase("30754-08.htm")) {
            questState.exitCurrentQuest(true);
            questState.playSound("ItemSound.quest_finish");
        } else if (string.equalsIgnoreCase("30754-06.htm") && n == 1) {
            if (l == 0L) {
                string2 = "30754-06a.htm";
            } else {
                if ((long)Rnd.get((int)(1000.0 / questState.getRateQuestsAdenaReward())) >= l) {
                    questState.giveItems(57, l * 60L);
                } else {
                    string2 = "30754-10.htm";
                    questState.set("ok", "1");
                    questState.set("amount", this.str(l));
                }
                questState.takeItems(4269, -1L);
            }
        } else if (string.equalsIgnoreCase("30754-11.htm") && n == 1) {
            if (questState.getInt("ok") != 1) {
                string2 = "noquest";
            } else {
                int n3 = Rnd.get(100);
                questState.setCond(2);
                questState.unset("ok");
                if (n3 < 25) {
                    string2 = "30754-12.htm";
                    questState.giveItems(4270, 1L);
                } else if (n3 < 50) {
                    string2 = "30754-13.htm";
                    questState.giveItems(4271, 1L);
                } else if (n3 < 75) {
                    string2 = "30754-14.htm";
                    questState.giveItems(4272, 1L);
                } else {
                    questState.giveItems(4273, 1L);
                }
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getState();
        int n3 = questState.getCond();
        long l = questState.getQuestItemsCount(4269);
        if (n2 == 1) {
            if (questState.getPlayer().getLevel() >= 48) {
                string = "30754-02.htm";
            } else {
                string = "30754-01.htm";
                questState.exitCurrentQuest(true);
            }
        } else if (n == 30754 && n3 == 1) {
            string = l > 0L ? "30754-05.htm" : "30754-09.htm";
        } else if (n3 == 2) {
            if (n == 30754) {
                string = "30754-15.htm";
            } else if (this.f(questState, n)) {
                string = this.str(n) + "-01.htm";
                questState.setCond(3);
                questState.playSound("ItemSound.quest_middle");
            }
        } else if (n3 == 3) {
            if (n == 30754) {
                int n4 = questState.getInt("amount");
                int n5 = questState.getInt("mission");
                int n6 = 0;
                if (n5 == 1) {
                    n6 = 1500;
                } else if (n5 == 2) {
                    questState.giveItems(4044, 1L);
                } else if (n5 == 3) {
                    questState.giveItems(4043, 1L);
                } else if (n5 == 4) {
                    questState.giveItems(4042, 1L);
                }
                if (n4 > 0) {
                    questState.unset("amount");
                    questState.giveItems(57, n4 * 50 + n6, true);
                }
                string = "30754-16.htm";
                questState.setCond(1);
                questState.unset("mission");
            } else {
                string = this.str(n) + "-02.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getCond() == 1) {
            questState.rollAndGive(4269, 1, 36 + (npcInstance.getNpcId() - 20234) * 2);
        }
        return null;
    }

    private boolean f(QuestState questState, int n) {
        boolean bl = false;
        int n2 = Rnd.get(100);
        if (n == 30857 && questState.getQuestItemsCount(4273) > 0L) {
            questState.set("mission", "1");
            questState.takeItems(4273, -1L);
            bl = true;
            if (n2 < 50) {
                questState.giveItems(1875, 19L);
            } else if (n2 < 70) {
                questState.giveItems(952, 5L);
            } else {
                questState.giveItems(2437, 1L);
            }
        } else if (n == 30704 && questState.getQuestItemsCount(4272) > 0L) {
            questState.set("mission", "2");
            questState.takeItems(4272, -1L);
            bl = true;
            if (n2 < 45) {
                questState.giveItems(1882, 70L);
            } else if (n2 < 95) {
                questState.giveItems(1881, 50L);
            } else {
                questState.giveItems(191, 1L);
            }
        } else if (n == 30623 && questState.getQuestItemsCount(4271) > 0L) {
            questState.set("mission", "3");
            questState.takeItems(4271, -1L);
            bl = true;
            if (n2 < 50) {
                questState.giveItems(1874, 25L);
            } else if (n2 < 75) {
                questState.giveItems(1887, 10L);
            } else if (n2 < 99) {
                questState.giveItems(951, 1L);
            } else {
                questState.giveItems(133, 1L);
            }
        } else if (n == 30756 && questState.getQuestItemsCount(4270) > 0L) {
            questState.set("mission", "4");
            questState.takeItems(4270, -1L);
            bl = true;
            if (n2 < 40) {
                questState.giveItems(1879, 55L);
            } else if (n2 < 90) {
                questState.giveItems(951, 1L);
            } else {
                questState.giveItems(885, 1L);
            }
        }
        return bl;
    }
}

