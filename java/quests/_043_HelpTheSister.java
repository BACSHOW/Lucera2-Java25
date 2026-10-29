/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _043_HelpTheSister
extends Quest
implements ScriptFile {
    private static final int aCm = 30829;
    private static final int aCn = 30097;
    private static final int aCo = 220;
    private static final int aCh = 7550;
    private static final int aCi = 7551;
    private static final int aCj = 7584;
    private static final int aCp = 20171;
    private static final int aCq = 20197;
    private static final int MAX_COUNT = 30;

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _043_HelpTheSister() {
        super(0);
        this.addStartNpc(30829);
        this.addTalkId(30097);
        this.addKillId(20171);
        this.addKillId(20197);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("1")) {
            string2 = "pet_manager_cooper_q0043_0104.htm";
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equals("3") && questState.getQuestItemsCount(220) > 0L) {
            string2 = "pet_manager_cooper_q0043_0201.htm";
            questState.takeItems(220, 1L);
            questState.setCond(2);
        } else if (string.equals("4") && questState.getQuestItemsCount(7550) >= 30L) {
            string2 = "pet_manager_cooper_q0043_0301.htm";
            questState.takeItems(7550, 30L);
            questState.giveItems(7551, 1L);
            questState.setCond(4);
        } else if (string.equals("5") && questState.getQuestItemsCount(7551) > 0L) {
            string2 = "galladuchi_q0043_0401.htm";
            questState.takeItems(7551, 1L);
            questState.setCond(5);
        } else if (string.equals("7")) {
            string2 = "pet_manager_cooper_q0043_0501.htm";
            questState.giveItems(7584, 1L);
            questState.setCond(0);
            questState.exitCurrentQuest(false);
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getState();
        if (n2 == 1) {
            if (questState.getPlayer().getLevel() >= 26) {
                string = "pet_manager_cooper_q0043_0101.htm";
            } else {
                questState.exitCurrentQuest(true);
                string = "pet_manager_cooper_q0043_0103.htm";
            }
        } else if (n2 == 2) {
            int n3 = questState.getCond();
            if (n == 30829) {
                if (n3 == 1) {
                    string = questState.getQuestItemsCount(220) == 0L ? "pet_manager_cooper_q0043_0106.htm" : "pet_manager_cooper_q0043_0105.htm";
                } else if (n3 == 2) {
                    string = "pet_manager_cooper_q0043_0204.htm";
                } else if (n3 == 3) {
                    string = "pet_manager_cooper_q0043_0203.htm";
                } else if (n3 == 4) {
                    string = "pet_manager_cooper_q0043_0303.htm";
                } else if (n3 == 5) {
                    string = "pet_manager_cooper_q0043_0401.htm";
                }
            } else if (n == 30097 && n3 == 4 && questState.getQuestItemsCount(7551) > 0L) {
                string = "galladuchi_q0043_0301.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        long l;
        int n = questState.getCond();
        if (n == 2 && (l = questState.getQuestItemsCount(7550)) < 30L) {
            questState.giveItems(7550, 1L);
            if (l < 29L) {
                questState.playSound("ItemSound.quest_itemget");
            } else {
                questState.playSound("ItemSound.quest_middle");
                questState.setCond(3);
            }
        }
        return null;
    }
}

