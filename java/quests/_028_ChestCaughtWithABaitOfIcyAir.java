/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;
import quests._051_OFullesSpecialBait;

public class _028_ChestCaughtWithABaitOfIcyAir
extends Quest
implements ScriptFile {
    int OFulle = 31572;
    int Kiki = 31442;
    int BigYellowTreasureChest = 6503;
    int KikisLetter = 7626;
    int ElvenRing = 881;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _028_ChestCaughtWithABaitOfIcyAir() {
        super(0);
        this.addStartNpc(this.OFulle);
        this.addTalkId(this.Kiki);
        this.addQuestItem(this.KikisLetter);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("fisher_ofulle_q0028_0104.htm")) {
            questState.setState(2);
            questState.setCond(1);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equals("fisher_ofulle_q0028_0201.htm")) {
            if (questState.getQuestItemsCount(this.BigYellowTreasureChest) > 0L) {
                questState.setCond(2);
                questState.takeItems(this.BigYellowTreasureChest, 1L);
                questState.giveItems(this.KikisLetter, 1L);
                questState.playSound("ItemSound.quest_middle");
            } else {
                string2 = "fisher_ofulle_q0028_0202.htm";
            }
        } else if (string.equals("mineral_trader_kiki_q0028_0301.htm")) {
            if (questState.getQuestItemsCount(this.KikisLetter) == 1L) {
                string2 = "mineral_trader_kiki_q0028_0301.htm";
                questState.takeItems(this.KikisLetter, -1L);
                questState.giveItems(this.ElvenRing, 1L);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(false);
            } else {
                string2 = "mineral_trader_kiki_q0028_0302.htm";
                questState.exitCurrentQuest(true);
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getState();
        int n3 = questState.getCond();
        if (n == this.OFulle) {
            if (n2 == 1) {
                if (questState.getPlayer().getLevel() < 36) {
                    string = "fisher_ofulle_q0028_0101.htm";
                    questState.exitCurrentQuest(true);
                } else {
                    QuestState questState2 = questState.getPlayer().getQuestState(_051_OFullesSpecialBait.class);
                    if (questState2 != null) {
                        if (questState2.isCompleted()) {
                            string = "fisher_ofulle_q0028_0101.htm";
                        } else {
                            string = "fisher_ofulle_q0028_0102.htm";
                            questState.exitCurrentQuest(true);
                        }
                    } else {
                        string = "fisher_ofulle_q0028_0103.htm";
                        questState.exitCurrentQuest(true);
                    }
                }
            } else if (n3 == 1) {
                string = "fisher_ofulle_q0028_0105.htm";
                if (questState.getQuestItemsCount(this.BigYellowTreasureChest) == 0L) {
                    string = "fisher_ofulle_q0028_0106.htm";
                }
            } else if (n3 == 2) {
                string = "fisher_ofulle_q0028_0203.htm";
            }
        } else if (n == this.Kiki) {
            string = n3 == 2 ? "mineral_trader_kiki_q0028_0201.htm" : "mineral_trader_kiki_q0028_0302.htm";
        }
        return string;
    }
}

