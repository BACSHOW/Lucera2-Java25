/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _031_SecretBuriedInTheSwamp
extends Quest
implements ScriptFile {
    private static final int aBj = 31555;
    private static final int aBk = 31661;
    private static final int aBl = 31662;
    private static final int aBm = 31663;
    private static final int aBn = 31664;
    private static final int aBo = 31665;
    private static final int aBp = 7252;

    public _031_SecretBuriedInTheSwamp() {
        super(0);
        this.addStartNpc(31555);
        this.addTalkId(31661, 31662, 31663, 31664, 31665);
        this.addQuestItem(7252);
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

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getInt("secret_in_swamp_cookie");
        int n2 = npcInstance.getNpcId();
        if (n2 == 31555) {
            if (string.equalsIgnoreCase("quest_accept")) {
                questState.setCond(1);
                questState.set("secret_in_swamp", String.valueOf(11), true);
                questState.setState(2);
                questState.playSound("ItemSound.quest_accept");
                string2 = "supplier_abercrombie_q0031_0104.htm";
            } else if (string.equalsIgnoreCase("reply_1") && n == 2) {
                if (questState.getQuestItemsCount(7252) >= 1L) {
                    questState.setCond(3);
                    questState.set("secret_in_swamp", String.valueOf(31), true);
                    questState.takeItems(7252, 1L);
                    string2 = "supplier_abercrombie_q0031_0301.htm";
                    questState.playSound("ItemSound.quest_middle");
                } else {
                    string2 = "supplier_abercrombie_q0031_0302.htm";
                }
            } else if (string.equalsIgnoreCase("reply_3") && n == 7) {
                questState.giveItems(57, 40000L);
                questState.addExpAndSp(130000L, 0L);
                questState.unset("secret_in_swamp");
                questState.unset("secret_in_swamp_cookie");
                questState.exitCurrentQuest(false);
                questState.playSound("ItemSound.quest_finish");
                string2 = "supplier_abercrombie_q0031_0801.htm";
            }
        } else if (n2 == 31661) {
            if (string.equalsIgnoreCase("reply_1") && n == 3) {
                questState.setCond(4);
                questState.set("secret_in_swamp", String.valueOf(41), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "old_slate1_q0031_0401.htm";
            }
        } else if (n2 == 31662) {
            if (string.equalsIgnoreCase("reply_1") && n == 4) {
                questState.setCond(5);
                questState.set("secret_in_swamp", String.valueOf(51), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "old_slate2_q0031_0501.htm";
            }
        } else if (n2 == 31663) {
            if (string.equalsIgnoreCase("reply_1") && n == 5) {
                questState.setCond(6);
                questState.set("secret_in_swamp", String.valueOf(61), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "old_slate3_q0031_0601.htm";
            }
        } else if (n2 == 31664) {
            if (string.equalsIgnoreCase("reply_1") && n == 6) {
                questState.setCond(7);
                questState.set("secret_in_swamp", String.valueOf(71), true);
                questState.playSound("ItemSound.quest_middle");
                string2 = "old_slate4_q0031_0701.htm";
            }
        } else if (n2 == 31665 && string.equalsIgnoreCase("reply_1") && n == 1) {
            questState.setCond(2);
            questState.set("secret_in_swamp", String.valueOf(21), true);
            questState.giveItems(7252, 1L);
            questState.playSound("ItemSound.quest_middle");
            string2 = "dead_dwarf_q0031_0201.htm";
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "no-quest";
        int n = questState.getInt("secret_in_swamp");
        int n2 = npcInstance.getNpcId();
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n2 != 31555) break;
                if (questState.getPlayer().getLevel() < 66 || questState.getPlayer().getLevel() >= 76) {
                    string = "supplier_abercrombie_q0031_0103.htm";
                    break;
                }
                string = "supplier_abercrombie_q0031_0101.htm";
                break;
            }
            case 2: {
                if (n2 == 31555) {
                    if (n == 11) {
                        string = "supplier_abercrombie_q0031_0105.htm";
                        break;
                    }
                    if (questState.getQuestItemsCount(7252) >= 1L && n == 21) {
                        questState.set("secret_in_swamp_cookie", String.valueOf(2), true);
                        string = "supplier_abercrombie_q0031_0201.htm";
                        break;
                    }
                    if (n == 31) {
                        string = "supplier_abercrombie_q0031_0303.htm";
                        break;
                    }
                    if (n != 71) break;
                    questState.set("secret_in_swamp_cookie", String.valueOf(7), true);
                    string = "supplier_abercrombie_q0031_0701.htm";
                    break;
                }
                if (n2 == 31661) {
                    if (n == 31) {
                        questState.set("secret_in_swamp_cookie", String.valueOf(3), true);
                        string = "old_slate1_q0031_0301.htm";
                        break;
                    }
                    if (n != 41) break;
                    string = "old_slate1_q0031_0402.htm";
                    break;
                }
                if (n2 == 31662) {
                    if (n == 41) {
                        questState.set("secret_in_swamp_cookie", String.valueOf(4), true);
                        string = "old_slate2_q0031_0401.htm";
                        break;
                    }
                    if (n != 51) break;
                    string = "old_slate2_q0031_0502.htm";
                    break;
                }
                if (n2 == 31663) {
                    if (n == 51) {
                        questState.set("secret_in_swamp_cookie", String.valueOf(5), true);
                        string = "old_slate3_q0031_0501.htm";
                        break;
                    }
                    if (n != 61) break;
                    string = "old_slate3_q0031_0602.htm";
                    break;
                }
                if (n2 == 31664) {
                    if (n == 61) {
                        questState.set("secret_in_swamp_cookie", String.valueOf(6), true);
                        string = "old_slate4_q0031_0601.htm";
                        break;
                    }
                    if (n != 71) break;
                    string = "old_slate4_q0031_0702.htm";
                    break;
                }
                if (n2 != 31665) break;
                if (n == 11) {
                    questState.set("secret_in_swamp_cookie", String.valueOf(1), true);
                    string = "dead_dwarf_q0031_0101.htm";
                    break;
                }
                if (n != 21) break;
                string = "dead_dwarf_q0031_0203.htm";
            }
        }
        return string;
    }
}

