/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _622_DeliveryofSpecialLiquor
extends Quest
implements ScriptFile {
    private static int JEREMY = 31521;
    private static int bPs = 31267;
    private static int bPg = 31543;
    private static int bPh = 31544;
    private static int bPi = 31545;
    private static int bPj = 31546;
    private static int bPk = 31547;
    private static int bPt = 7207;
    private static int bPu = 7198;
    private static int bPo = 6849;
    private static int bPp = 6847;
    private static int bPq = 6851;
    private static int bPn = 734;
    private static int bPr = 20;

    public _622_DeliveryofSpecialLiquor() {
        super(0);
        this.addStartNpc(JEREMY);
        this.addTalkId(bPs);
        this.addTalkId(bPg);
        this.addTalkId(bPh);
        this.addTalkId(bPi);
        this.addTalkId(bPj);
        this.addTalkId(bPk);
        this.addQuestItem(bPt);
        this.addQuestItem(bPu);
    }

    private static void i(QuestState questState, int n) {
        questState.setCond(n);
        questState.takeItems(bPt, 1L);
        questState.giveItems(bPu, 1L);
        questState.playSound("ItemSound.quest_middle");
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        int n = questState.getState();
        int n2 = questState.getCond();
        long l = questState.getQuestItemsCount(bPt);
        if (string.equalsIgnoreCase("jeremy_q0622_0104.htm") && n == 1) {
            questState.setState(2);
            questState.setCond(1);
            questState.takeItems(bPt, -1L);
            questState.takeItems(bPu, -1L);
            questState.giveItems(bPt, 5L);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equalsIgnoreCase("beolin_q0622_0201.htm") && n2 == 1 && l > 0L) {
            _622_DeliveryofSpecialLiquor.i(questState, 2);
        } else if (string.equalsIgnoreCase("kuber_q0622_0301.htm") && n2 == 2 && l > 0L) {
            _622_DeliveryofSpecialLiquor.i(questState, 3);
        } else if (string.equalsIgnoreCase("crocus_q0622_0401.htm") && n2 == 3 && l > 0L) {
            _622_DeliveryofSpecialLiquor.i(questState, 4);
        } else if (string.equalsIgnoreCase("naff_q0622_0501.htm") && n2 == 4 && l > 0L) {
            _622_DeliveryofSpecialLiquor.i(questState, 5);
        } else if (string.equalsIgnoreCase("pulin_q0622_0601.htm") && n2 == 5 && l > 0L) {
            _622_DeliveryofSpecialLiquor.i(questState, 6);
        } else if (string.equalsIgnoreCase("jeremy_q0622_0701.htm") && n2 == 6 && questState.getQuestItemsCount(bPu) >= 5L) {
            questState.setCond(7);
        } else if (string.equalsIgnoreCase("warehouse_keeper_lietta_q0622_0801.htm") && n2 == 7 && questState.getQuestItemsCount(bPu) >= 5L) {
            questState.takeItems(bPt, -1L);
            questState.takeItems(bPu, -1L);
            if (Rnd.chance(bPr)) {
                if (Rnd.chance(40)) {
                    questState.giveItems(bPo, 1L);
                } else if (Rnd.chance(40)) {
                    questState.giveItems(bPp, 1L);
                } else {
                    questState.giveItems(bPq, 1L);
                }
            } else {
                questState.giveItems(57, 18800L);
                questState.giveItems(bPn, 1L, true);
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
                return "jeremy_q0622_0101.htm";
            }
            questState.exitCurrentQuest(true);
            return "jeremy_q0622_0103.htm";
        }
        int n2 = questState.getCond();
        long l = questState.getQuestItemsCount(bPt);
        long l2 = questState.getQuestItemsCount(bPu);
        if (n2 == 1 && n == bPk && l > 0L) {
            string = "beolin_q0622_0101.htm";
        } else if (n2 == 2 && n == bPj && l > 0L) {
            string = "kuber_q0622_0201.htm";
        } else if (n2 == 3 && n == bPi && l > 0L) {
            string = "crocus_q0622_0301.htm";
        } else if (n2 == 4 && n == bPh && l > 0L) {
            string = "naff_q0622_0401.htm";
        } else if (n2 == 5 && n == bPg && l > 0L) {
            string = "pulin_q0622_0501.htm";
        } else if (n2 == 6 && n == JEREMY && l2 >= 5L) {
            string = "jeremy_q0622_0601.htm";
        } else if (n2 == 7 && n == JEREMY && l2 >= 5L) {
            string = "jeremy_q0622_0703.htm";
        } else if (n2 == 7 && n == bPs && l2 >= 5L) {
            string = "warehouse_keeper_lietta_q0622_0701.htm";
        } else if (n2 > 0 && n == JEREMY && l > 0L) {
            string = "jeremy_q0622_0104.htm";
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

