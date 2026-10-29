/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.data.xml.holder.ItemHolder;
import l2.gameserver.model.base.Race;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.network.l2.components.IStaticPacket;
import l2.gameserver.network.l2.s2c.ExShowScreenMessage;
import l2.gameserver.scripts.ScriptFile;

public class _276_HestuiTotem
extends Quest
implements ScriptFile {
    private static int aPG = 30571;
    private static int beq = 20479;
    private static int ber = 27044;
    private static int Leather_Pants = 29;
    private static int bes = 1500;
    private static int bet = 1480;
    private static int beu = 1481;

    public _276_HestuiTotem() {
        super(0);
        this.addStartNpc(aPG);
        this.addKillId(beq);
        this.addKillId(ber);
        this.addQuestItem(bet);
        this.addQuestItem(beu);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        if (string.equalsIgnoreCase("seer_tanapi_q0276_03.htm") && questState.getState() == 1 && questState.getPlayer().getRace() == Race.orc && questState.getPlayer().getLevel() >= 15) {
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        }
        return string;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        if (npcInstance.getNpcId() != aPG) {
            return string;
        }
        int n = questState.getState();
        if (n == 1) {
            if (questState.getPlayer().getRace() != Race.orc) {
                string = "seer_tanapi_q0276_00.htm";
                questState.exitCurrentQuest(true);
            } else if (questState.getPlayer().getLevel() < 15) {
                string = "seer_tanapi_q0276_01.htm";
                questState.exitCurrentQuest(true);
            } else {
                string = "seer_tanapi_q0276_02.htm";
                questState.setCond(0);
            }
        } else if (n == 2) {
            if (questState.getQuestItemsCount(beu) > 0L) {
                string = "seer_tanapi_q0276_05.htm";
                questState.takeItems(bet, -1L);
                questState.takeItems(beu, -1L);
                questState.giveItems(Leather_Pants, 1L);
                questState.giveItems(bes, 1L);
                if (questState.getRateQuestsReward() > 1.0) {
                    questState.giveItems(57, Math.round((double)ItemHolder.getInstance().getTemplate(bes).getReferencePrice() * (questState.getRateQuestsAdenaReward() - 1.0) / 2.0), false);
                }
                if (questState.getPlayer().getClassId().getLevel() == 1 && !questState.getPlayer().getVarB("p1q4")) {
                    questState.getPlayer().setVar("p1q4", "1", -1L);
                    questState.getPlayer().sendPacket((IStaticPacket)new ExShowScreenMessage("Now go find the Newbie Guide.", 5000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
                }
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(true);
            } else {
                string = "seer_tanapi_q0276_04.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        if (questState.getState() != 2) {
            return null;
        }
        int n = npcInstance.getNpcId();
        if (n == beq && questState.getQuestItemsCount(beu) == 0L) {
            if (questState.getQuestItemsCount(bet) < 50L) {
                questState.giveItems(bet, 1L);
                questState.playSound("ItemSound.quest_itemget");
            } else {
                questState.takeItems(bet, -1L);
                questState.addSpawn(ber);
            }
        } else if (n == ber && questState.getQuestItemsCount(beu) == 0L) {
            questState.giveItems(beu, 1L);
            questState.playSound("ItemSound.quest_middle");
        }
        return null;
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

