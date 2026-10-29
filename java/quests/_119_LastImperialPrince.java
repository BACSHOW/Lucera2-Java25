/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _119_LastImperialPrince
extends Quest
implements ScriptFile {
    private static final int aFO = 31453;
    private static final int aFP = 32009;
    private static final int aFQ = 7262;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _119_LastImperialPrince() {
        super(0);
        this.addStartNpc(31453);
        this.addTalkId(32009);
        this.addQuestItem(7262);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        int n = questState.getInt("the_last_imperial_pr");
        if (string.equalsIgnoreCase("quest_accept")) {
            questState.setCond(1);
            questState.set("the_last_imperial_pr", String.valueOf(1), true);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
            string2 = "printessa_spirit_q0119_06.htm";
        } else if (string.equalsIgnoreCase("reply_4") && n == 2) {
            questState.giveItems(57, 68787L, true);
            questState.playSound("ItemSound.quest_finish");
            questState.unset("the_last_imperial_pr");
            string2 = "printessa_spirit_q0119_10.htm";
            questState.exitCurrentQuest(false);
        } else if (string.equalsIgnoreCase("reply_1")) {
            string2 = questState.getQuestItemsCount(7262) >= 1L ? "frintessa_nurse_q0119_02.htm" : "frintessa_nurse_q0119_02a.htm";
        } else if (string.equalsIgnoreCase("reply_2") && n == 1 && questState.getQuestItemsCount(7262) >= 1L) {
            questState.setCond(2);
            questState.set("the_last_imperial_pr", String.valueOf(2), true);
            questState.playSound("ItemSound.quest_middle");
            string2 = "frintessa_nurse_q0119_03.htm";
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        String string = "noquest";
        int n = npcInstance.getNpcId();
        int n2 = questState.getInt("the_last_imperial_pr");
        int n3 = questState.getState();
        switch (n3) {
            case 1: {
                if (n != 31453) break;
                if (questState.getPlayer().getLevel() >= 74 && questState.getQuestItemsCount(7262) >= 1L) {
                    string = "printessa_spirit_q0119_01.htm";
                    break;
                }
                string = "printessa_spirit_q0119_02.htm";
                questState.exitCurrentQuest(true);
                break;
            }
            case 2: {
                if (n == 31453) {
                    if (n2 == 1 && questState.getQuestItemsCount(7262) >= 1L) {
                        string = "printessa_spirit_q0119_07.htm";
                        break;
                    }
                    if (n2 == 1 && questState.getQuestItemsCount(7262) == 0L) {
                        string = "printessa_spirit_q0119_07a.htm";
                        questState.exitCurrentQuest(true);
                        break;
                    }
                    if (n2 != 2) break;
                    string = "printessa_spirit_q0119_08.htm";
                    break;
                }
                if (n != 32009) break;
                if (n2 == 1) {
                    string = "frintessa_nurse_q0119_01.htm";
                    break;
                }
                if (n2 != 2) break;
                string = "frintessa_nurse_q0119_04.htm";
                break;
            }
            case 3: {
                if (n != 31453) break;
                string = "printessa_spirit_q0119_03.htm";
            }
        }
        return string;
    }
}

