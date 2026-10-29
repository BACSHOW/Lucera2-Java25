/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;
import quests._050_LanoscosSpecialBait;

public class _027_ChestCaughtWithABaitOfWind
extends Quest
implements ScriptFile {
    private static final int Lanosco = 31570;
    private static final int aBf = 31434;
    private static final int aBg = 7625;
    private static final int aBh = 6500;
    private static final int aBi = 880;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _027_ChestCaughtWithABaitOfWind() {
        super(0);
        this.addStartNpc(31570);
        this.addTalkId(31434);
        this.addQuestItem(7625);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("fisher_lanosco_q0027_0104.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equals("fisher_lanosco_q0027_0201.htm")) {
            if (questState.getQuestItemsCount(6500) > 0L) {
                questState.takeItems(6500, 1L);
                questState.giveItems(7625, 1L);
                questState.setCond(2);
                questState.playSound("ItemSound.quest_middle");
            } else {
                string2 = "fisher_lanosco_q0027_0202.htm";
            }
        } else if (string.equals("blueprint_seller_shaling_q0027_0301.htm")) {
            if (questState.getQuestItemsCount(7625) == 1L) {
                questState.takeItems(7625, -1L);
                questState.giveItems(880, 1L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(false);
            } else {
                string2 = "blueprint_seller_shaling_q0027_0302.htm";
                questState.exitCurrentQuest(true);
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        int n3 = questState.getState();
        if (n == 31570) {
            if (n3 == 1) {
                if (questState.getPlayer().getLevel() < 27) {
                    string = "fisher_lanosco_q0027_0101.htm";
                    questState.exitCurrentQuest(true);
                } else {
                    QuestState questState2 = questState.getPlayer().getQuestState(_050_LanoscosSpecialBait.class);
                    if (questState2 != null) {
                        if (questState2.isCompleted()) {
                            string = "fisher_lanosco_q0027_0101.htm";
                        } else {
                            string = "fisher_lanosco_q0027_0102.htm";
                            questState.exitCurrentQuest(true);
                        }
                    } else {
                        string = "fisher_lanosco_q0027_0103.htm";
                        questState.exitCurrentQuest(true);
                    }
                }
            } else if (n2 == 1) {
                string = "fisher_lanosco_q0027_0105.htm";
                if (questState.getQuestItemsCount(6500) == 0L) {
                    string = "fisher_lanosco_q0027_0106.htm";
                }
            } else if (n2 == 2) {
                string = "fisher_lanosco_q0027_0203.htm";
            }
        } else if (n == 31434) {
            string = n2 == 2 ? "blueprint_seller_shaling_q0027_0201.htm" : "blueprint_seller_shaling_q0027_0302.htm";
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        return null;
    }
}

