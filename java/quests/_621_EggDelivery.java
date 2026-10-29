/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _621_EggDelivery
extends Quest
implements ScriptFile {
    private static int JEREMY = 31521;
    private static int bPf = 31584;
    private static int bPg = 31543;
    private static int bPh = 31544;
    private static int bPi = 31545;
    private static int bPj = 31546;
    private static int bPk = 31547;
    private static final int bPl = 7206;
    private static final int bPm = 7196;
    private static final int bPn = 734;
    private static final int bPo = 6849;
    private static final int bPp = 6847;
    private static final int bPq = 6851;
    private static int bPr = 20;

    public _621_EggDelivery() {
        super(0);
        this.addStartNpc(JEREMY);
        this.addTalkId(bPf);
        this.addTalkId(bPg);
        this.addTalkId(bPh);
        this.addTalkId(bPi);
        this.addTalkId(bPj);
        this.addTalkId(bPk);
        this.addQuestItem(7206);
        this.addQuestItem(7196);
    }

    private static void h(QuestState questState, int n) {
        questState.setCond(n);
        questState.takeItems(7206, 1L);
        questState.giveItems(7196, 1L);
        questState.playSound("ItemSound.quest_middle");
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        int n = questState.getState();
        int n2 = questState.getCond();
        long l = questState.getQuestItemsCount(7206);
        if (string.equalsIgnoreCase("jeremy_q0621_0104.htm") && n == 1) {
            questState.takeItems(7206, -1L);
            questState.takeItems(7196, -1L);
            questState.setState(2);
            questState.setCond(1);
            questState.giveItems(7206, 5L);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("pulin_q0621_0201.htm") && n2 == 1 && l > 0L) {
            _621_EggDelivery.h(questState, 2);
        } else if (string.equalsIgnoreCase("naff_q0621_0301.htm") && n2 == 2 && l > 0L) {
            _621_EggDelivery.h(questState, 3);
        } else if (string.equalsIgnoreCase("crocus_q0621_0401.htm") && n2 == 3 && l > 0L) {
            _621_EggDelivery.h(questState, 4);
        } else if (string.equalsIgnoreCase("kuber_q0621_0501.htm") && n2 == 4 && l > 0L) {
            _621_EggDelivery.h(questState, 5);
        } else if (string.equalsIgnoreCase("beolin_q0621_0601.htm") && n2 == 5 && l > 0L) {
            _621_EggDelivery.h(questState, 6);
        } else if (string.equalsIgnoreCase("jeremy_q0621_0701.htm") && n2 == 6 && questState.getQuestItemsCount(7196) >= 5L) {
            questState.setCond(7);
        } else if (string.equalsIgnoreCase("brewer_valentine_q0621_0801.htm") && n2 == 7 && questState.getQuestItemsCount(7196) >= 5L) {
            questState.takeItems(7206, -1L);
            questState.takeItems(7196, -1L);
            if (Rnd.chance(bPr)) {
                if (Rnd.chance(40)) {
                    questState.giveItems(6849, 1L, true);
                } else if (Rnd.chance(40)) {
                    questState.giveItems(6847, 1L, true);
                } else {
                    questState.giveItems(6851, 1L, true);
                }
            } else {
                questState.giveItems(57, 18800L);
                questState.giveItems(734, 1L, true);
            }
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        }
        return string;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        if (questState.getState() == 1) {
            if (n != JEREMY) {
                return string;
            }
            if (questState.getPlayer().getLevel() >= 68) {
                questState.setCond(0);
                return "jeremy_q0621_0101.htm";
            }
            questState.exitCurrentQuest(true);
            return "jeremy_q0621_0103.htm";
        }
        int n2 = questState.getCond();
        long l = questState.getQuestItemsCount(7206);
        long l2 = questState.getQuestItemsCount(7196);
        if (n2 == 1 && n == bPg && l > 0L) {
            string = "pulin_q0621_0101.htm";
        }
        if (n2 == 2 && n == bPh && l > 0L) {
            string = "naff_q0621_0201.htm";
        }
        if (n2 == 3 && n == bPi && l > 0L) {
            string = "crocus_q0621_0301.htm";
        }
        if (n2 == 4 && n == bPj && l > 0L) {
            string = "kuber_q0621_0401.htm";
        }
        if (n2 == 5 && n == bPk && l > 0L) {
            string = "beolin_q0621_0501.htm";
        }
        if (n2 == 6 && n == JEREMY && l2 >= 5L) {
            string = "jeremy_q0621_0601.htm";
        }
        if (n2 == 7 && n == JEREMY && l2 >= 5L) {
            string = "jeremy_q0621_0703.htm";
        }
        if (n2 == 7 && n == bPf && l2 >= 5L) {
            string = "brewer_valentine_q0621_0701.htm";
        } else if (n2 > 0 && n == JEREMY && l > 0L) {
            string = "jeremy_q0621_0104.htm";
        }
        return string;
    }

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }
}

