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

public class _261_CollectorsDream
extends Quest
implements ScriptFile {
    int GIANT_SPIDER_LEG = 1087;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _261_CollectorsDream() {
        super(0);
        this.addStartNpc(30222);
        this.addTalkId(30222);
        this.addKillId(20308);
        this.addKillId(20460);
        this.addKillId(20466);
        this.addQuestItem(this.GIANT_SPIDER_LEG);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.intern().equalsIgnoreCase("moneylender_alshupes_q0261_03.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = questState.getCond();
        if (n == 0) {
            if (questState.getPlayer().getLevel() >= 15) {
                string = "moneylender_alshupes_q0261_02.htm";
                return string;
            }
            string = "moneylender_alshupes_q0261_01.htm";
            questState.exitCurrentQuest(true);
        } else if (n == 1 || questState.getQuestItemsCount(this.GIANT_SPIDER_LEG) < 8L) {
            string = "moneylender_alshupes_q0261_04.htm";
        } else if (n == 2 && questState.getQuestItemsCount(this.GIANT_SPIDER_LEG) >= 8L) {
            questState.takeItems(this.GIANT_SPIDER_LEG, -1L);
            questState.giveItems(57, 1000L);
            questState.addExpAndSp(2000L, 0L);
            if (questState.getPlayer().getClassId().getLevel() == 1 && !questState.getPlayer().getVarB("p1q4")) {
                questState.getPlayer().setVar("p1q4", "1", -1L);
                questState.getPlayer().sendPacket((IStaticPacket)new ExShowScreenMessage("Now go find the Newbie Guide.", 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
            }
            string = "moneylender_alshupes_q0261_05.htm";
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getCond() == 1 && questState.getQuestItemsCount(this.GIANT_SPIDER_LEG) < 8L) {
            questState.giveItems(this.GIANT_SPIDER_LEG, 1L);
            if (questState.getQuestItemsCount(this.GIANT_SPIDER_LEG) == 8L) {
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(2);
            } else {
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

