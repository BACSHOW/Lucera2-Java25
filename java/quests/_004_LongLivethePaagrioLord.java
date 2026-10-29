/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.scripts.ScriptFile;

public class _004_LongLivethePaagrioLord
extends Quest
implements ScriptFile {
    int HONEY_KHANDAR = 1541;
    int BEAR_FUR_CLOAK = 1542;
    int BLOODY_AXE = 1543;
    int ANCESTOR_SKULL = 1544;
    int SPIDER_DUST = 1545;
    int DEEP_SEA_ORB = 1546;
    int CLUB = 14;
    int[][] NPC_GIFTS = new int[][]{{30585, this.BEAR_FUR_CLOAK}, {30566, this.HONEY_KHANDAR}, {30562, this.BLOODY_AXE}, {30560, this.ANCESTOR_SKULL}, {30559, this.SPIDER_DUST}, {30587, this.DEEP_SEA_ORB}};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _004_LongLivethePaagrioLord() {
        super(0);
        this.addStartNpc(30578);
        this.addTalkId(30559, 30560, 30562, 30566, 30578, 30585, 30587);
        this.addQuestItem(this.SPIDER_DUST, this.ANCESTOR_SKULL, this.BLOODY_AXE, this.HONEY_KHANDAR, this.BEAR_FUR_CLOAK, this.DEEP_SEA_ORB);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("30578-03.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n == 30578) {
            if (n2 == 0) {
                if (questState.getPlayer().getRace() != Race.orc) {
                    string = "30578-00.htm";
                    questState.exitCurrentQuest(true);
                } else if (questState.getPlayer().getLevel() >= 2) {
                    string = "30578-02.htm";
                } else {
                    string = "30578-01.htm";
                    questState.exitCurrentQuest(true);
                }
            } else if (n2 == 1) {
                string = "30578-04.htm";
            } else if (n2 == 2) {
                string = "30578-06.htm";
                for (int[] nArray : this.NPC_GIFTS) {
                    questState.takeItems(nArray[1], -1L);
                }
                questState.giveItems(this.CLUB, 1L, false);
                if (questState.getPlayer().getClassId().getLevel() == 1 && !questState.getPlayer().getVarB("ng1")) {
                    questState.getPlayer().sendPacket((IStaticPacket)new ExShowScreenMessage("  Delivery duty complete.\nGo find the Newbie Guide.", 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                }
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(false);
            }
        } else if (n2 == 1) {
            for (int[] nArray : this.NPC_GIFTS) {
                if (nArray[0] != n) continue;
                int n3 = nArray[1];
                if (questState.getQuestItemsCount(n3) > 0L) {
                    string = n + "-02.htm";
                } else {
                    questState.giveItems(n3, 1L, false);
                    string = n + "-01.htm";
                    int n4 = 0;
                    for (int[] nArray2 : this.NPC_GIFTS) {
                        n4 = (int)((long)n4 + questState.getQuestItemsCount(nArray2[1]));
                    }
                    if (n4 == 6) {
                        questState.setCond(2);
                        questState.playSound("ItemSound.quest_middle");
                    } else {
                        questState.playSound("ItemSound.quest_itemget");
                    }
                }
                return string;
            }
        }
        return string;
    }
}

