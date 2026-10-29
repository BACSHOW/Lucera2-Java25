/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.commons.util.Rnd;
import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _612_WarwithKetraOrcs
extends Quest
implements ScriptFile {
    private static final int bLP = 31377;
    private static final int bKN = 21324;
    private static final int bKW = 21327;
    private static final int bKT = 21328;
    private static final int bKV = 21329;
    private static final int bKX = 21331;
    private static final int bKL = 21332;
    private static final int bKP = 21334;
    private static final int bKM = 21336;
    private static final int bKU = 21338;
    private static final int bKQ = 21339;
    private static final int bKS = 21340;
    private static final int bKR = 21342;
    private static final int bKO = 21343;
    private static final int bKK = 21345;
    private static final int bKY = 21347;
    private static final int bLQ = 7234;
    private static final int bLR = 7187;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _612_WarwithKetraOrcs() {
        super(1);
        this.addStartNpc(31377);
        this.addKillId(21324, 21327, 21328, 21329, 21331, 21332, 21334, 21336, 21338, 21339, 21340, 21342, 21343, 21345, 21347);
        this.addQuestItem(7234);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equalsIgnoreCase("quest_accept")) {
            questState.setCond(1);
            questState.set("war_with_ketra_orcs", String.valueOf(11), true);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            string2 = "elder_ashas_barka_durai_q0612_0104.htm";
        } else if (string.equalsIgnoreCase("reply_1")) {
            string2 = "elder_ashas_barka_durai_q0612_0201.htm";
        } else if (string.equalsIgnoreCase("reply_3")) {
            if (questState.getQuestItemsCount(7234) >= 100L) {
                questState.takeItems(7234, 100L);
                questState.giveItems(7187, 20L);
                string2 = "elder_ashas_barka_durai_q0612_0202.htm";
            } else {
                string2 = "elder_ashas_barka_durai_q0612_0203.htm";
            }
        } else if (string.equalsIgnoreCase("reply_4")) {
            questState.takeItems(7234, -1L);
            questState.unset("war_with_ketra_orcs");
            questState.playSound("ItemSound.quest_finish");
            questState.exitCurrentQuest(true);
            string2 = "elder_ashas_barka_durai_q0612_0204.htm";
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("war_with_ketra_orcs");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 31377) break;
                if (questState.getPlayer().getLevel() >= 74) {
                    string = "elder_ashas_barka_durai_q0612_0101.htm";
                    break;
                }
                string = "elder_ashas_barka_durai_q0612_0103.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n2 != 31377 || n != 11) break;
                string = questState.getQuestItemsCount(7234) == 0L ? "elder_ashas_barka_durai_q0612_0106.htm" : "elder_ashas_barka_durai_q0612_0105.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = questState.getInt("war_with_ketra_orcs");
        int n2 = npcInstance.getNpcId();
        if (n == 11) {
            int n3;
            if (n2 == 21324) {
                int n4 = Rnd.get(1000);
                if (n4 < 500) {
                    questState.rollAndGive(7234, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n2 == 21327) {
                int n5 = Rnd.get(1000);
                if (n5 < 510) {
                    questState.rollAndGive(7234, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n2 == 21328) {
                int n6 = Rnd.get(1000);
                if (n6 < 522) {
                    questState.rollAndGive(7234, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n2 == 21329) {
                int n7 = Rnd.get(1000);
                if (n7 < 519) {
                    questState.rollAndGive(7234, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n2 == 21331 || n2 == 21332) {
                int n8 = Rnd.get(1000);
                if (n8 < 529) {
                    questState.rollAndGive(7234, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n2 == 21334) {
                int n9 = Rnd.get(1000);
                if (n9 < 539) {
                    questState.rollAndGive(7234, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n2 == 21336) {
                int n10 = Rnd.get(1000);
                if (n10 < 548) {
                    questState.rollAndGive(7234, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n2 == 21338) {
                int n11 = Rnd.get(1000);
                if (n11 < 558) {
                    questState.rollAndGive(7234, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n2 == 21339 || n2 == 21340) {
                int n12 = Rnd.get(1000);
                if (n12 < 568) {
                    questState.rollAndGive(7234, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n2 == 21342) {
                int n13 = Rnd.get(1000);
                if (n13 < 578) {
                    questState.rollAndGive(7234, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n2 == 21343) {
                int n14 = Rnd.get(1000);
                if (n14 < 664) {
                    questState.rollAndGive(7234, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n2 == 21345) {
                int n15 = Rnd.get(1000);
                if (n15 < 713) {
                    questState.rollAndGive(7234, 1, 100.0);
                    questState.playSound("ItemSound.quest_itemget");
                }
            } else if (n2 == 21347 && (n3 = Rnd.get(1000)) < 738) {
                questState.rollAndGive(7234, 1, 100.0);
                questState.playSound("ItemSound.quest_itemget");
            }
        }
        return null;
    }
}

