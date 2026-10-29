/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.scripts.ScriptFile;

public class _005_MinersFavor
extends Quest
implements ScriptFile {
    public final int BOLTER = 30554;
    public final int SHARI = 30517;
    public final int GARITA = 30518;
    public final int REED = 30520;
    public final int BRUNON = 30526;
    public final int BOLTERS_LIST = 1547;
    public final int MINING_BOOTS = 1548;
    public final int MINERS_PICK = 1549;
    public final int BOOMBOOM_POWDER = 1550;
    public final int REDSTONE_BEER = 1551;
    public final int BOLTERS_SMELLY_SOCKS = 1552;
    public final int NECKLACE = 906;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _005_MinersFavor() {
        super(0);
        this.addStartNpc(30554);
        this.addTalkId(30517);
        this.addTalkId(30518);
        this.addTalkId(30520);
        this.addTalkId(30526);
        this.addQuestItem(1547, 1552, 1548, 1549, 1550, 1551);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("miner_bolter_q0005_03.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            questState.giveItems(1547, 1L, false);
            questState.giveItems(1552, 1L, false);
        } else if (string.equalsIgnoreCase("blacksmith_bronp_q0005_02.htm")) {
            questState.takeItems(1552, -1L);
            questState.giveItems(1549, 1L, false);
            if (questState.getQuestItemsCount(1547) > 0L && questState.getQuestItemsCount(1548) + questState.getQuestItemsCount(1549) + questState.getQuestItemsCount(1550) + questState.getQuestItemsCount(1551) == 4L) {
                questState.setCond(2);
                questState.playSound("ItemSound.quest_middle");
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getCond();
        if (n == 30554) {
            if (n2 == 0) {
                if (questState.getPlayer().getLevel() >= 2) {
                    string = "miner_bolter_q0005_02.htm";
                } else {
                    string = "miner_bolter_q0005_01.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 1) {
                string = "miner_bolter_q0005_04.htm";
            } else if (n2 == 2 && questState.getQuestItemsCount(1548) + questState.getQuestItemsCount(1549) + questState.getQuestItemsCount(1550) + questState.getQuestItemsCount(1551) == 4L) {
                string = "miner_bolter_q0005_06.htm";
                questState.takeItems(1548, -1L);
                questState.takeItems(1549, -1L);
                questState.takeItems(1550, -1L);
                questState.takeItems(1551, -1L);
                questState.takeItems(1547, -1L);
                questState.giveItems(906, 1L, false);
                if (questState.getPlayer().getClassId().getLevel() == 1 && !questState.getPlayer().getVarB("ng1")) {
                    questState.getPlayer().sendPacket((IStaticPacket)new ExShowScreenMessage("  Delivery duty complete.\nGo find the Newbie Guide.", 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                }
                questState.giveItems(57, 2466L);
                questState.unset("cond");
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(false);
            }
        } else if (n2 == 1 && questState.getQuestItemsCount(1547) > 0L) {
            if (n == 30517) {
                if (questState.getQuestItemsCount(1550) == 0L) {
                    string = "trader_chali_q0005_01.htm";
                    questState.giveItems(1550, 1L, false);
                    questState.playSound("ItemSound.quest_itemget");
                } else {
                    string = "trader_chali_q0005_02.htm";
                }
            } else if (n == 30518) {
                if (questState.getQuestItemsCount(1548) == 0L) {
                    string = "trader_garita_q0005_01.htm";
                    questState.giveItems(1548, 1L, false);
                    questState.playSound("ItemSound.quest_itemget");
                } else {
                    string = "trader_garita_q0005_02.htm";
                }
            } else if (n == 30520) {
                if (questState.getQuestItemsCount(1551) == 0L) {
                    string = "warehouse_chief_reed_q0005_01.htm";
                    questState.giveItems(1551, 1L, false);
                    questState.playSound("ItemSound.quest_itemget");
                } else {
                    string = "warehouse_chief_reed_q0005_02.htm";
                }
            } else if (n == 30526 && questState.getQuestItemsCount(1552) > 0L) {
                string = questState.getQuestItemsCount(1549) == 0L ? "blacksmith_bronp_q0005_01.htm" : "blacksmith_bronp_q0005_03.htm";
            }
            if (questState.getQuestItemsCount(1547) > 0L && questState.getQuestItemsCount(1548) + questState.getQuestItemsCount(1549) + questState.getQuestItemsCount(1550) + questState.getQuestItemsCount(1551) == 4L) {
                questState.setCond(2);
                questState.playSound("ItemSound.quest_middle");
            }
        }
        return string;
    }
}

