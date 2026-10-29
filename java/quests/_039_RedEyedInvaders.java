/*
 * Decompiled with CFR 0.152.
 */
package quests;

import l2.gameserver.model.instances.NpcInstance;
import l2.gameserver.model.quest.Quest;
import l2.gameserver.model.quest.QuestState;
import l2.gameserver.scripts.ScriptFile;

public class _039_RedEyedInvaders
extends Quest
implements ScriptFile {
    int BBN = 7178;
    int RBN = 7179;
    int IP = 7180;
    int GML = 7181;
    int[] REW = new int[]{6521, 6529, 6535};

    @Override
    public void onLoad() {
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public _039_RedEyedInvaders() {
        super(0);
        this.addStartNpc(30334);
        this.addTalkId(30332);
        this.addKillId(20919);
        this.addKillId(20920);
        this.addKillId(20921);
        this.addKillId(20925);
        this.addQuestItem(this.BBN, this.IP, this.RBN, this.GML);
    }

    @Override
    public String onEvent(String string, QuestState questState, NpcInstance npcInstance) {
        String string2 = string;
        if (string.equals("guard_babenco_q0039_0104.htm")) {
            questState.setCond(1);
            questState.setState(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equals("captain_bathia_q0039_0201.htm")) {
            questState.setCond(2);
            questState.playSound("ItemSound.quest_accept");
        } else if (string.equals("captain_bathia_q0039_0301.htm")) {
            if (questState.getQuestItemsCount(this.BBN) == 100L && questState.getQuestItemsCount(this.RBN) == 100L) {
                questState.setCond(4);
                questState.takeItems(this.BBN, -1L);
                questState.takeItems(this.RBN, -1L);
                questState.playSound("ItemSound.quest_accept");
            } else {
                string2 = "captain_bathia_q0039_0203.htm";
            }
        } else if (string.equals("captain_bathia_q0039_0401.htm")) {
            if (questState.getQuestItemsCount(this.IP) == 30L && questState.getQuestItemsCount(this.GML) == 30L) {
                questState.takeItems(this.IP, -1L);
                questState.takeItems(this.GML, -1L);
                questState.giveItems(this.REW[0], 60L);
                questState.giveItems(this.REW[1], 1L);
                questState.giveItems(this.REW[2], 500L);
                questState.setCond(0);
                questState.playSound("ItemSound.quest_finish");
                questState.exitCurrentQuest(false);
            } else {
                string2 = "captain_bathia_q0039_0304.htm";
            }
        }
        return string2;
    }

    @Override
    public String onTalk(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        String string = "noquest";
        int n2 = questState.getCond();
        if (n == 30334) {
            if (n2 == 0) {
                if (questState.getPlayer().getLevel() < 20) {
                    string = "guard_babenco_q0039_0102.htm";
                    questState.exitCurrentQuest(true);
                } else if (questState.getPlayer().getLevel() >= 20) {
                    string = "guard_babenco_q0039_0101.htm";
                }
            } else if (n2 == 1) {
                string = "guard_babenco_q0039_0105.htm";
            }
        } else if (n == 30332) {
            if (n2 == 1) {
                string = "captain_bathia_q0039_0101.htm";
            } else if (n2 == 2 && (questState.getQuestItemsCount(this.BBN) < 100L || questState.getQuestItemsCount(this.RBN) < 100L)) {
                string = "captain_bathia_q0039_0203.htm";
            } else if (n2 == 3 && questState.getQuestItemsCount(this.BBN) == 100L && questState.getQuestItemsCount(this.RBN) == 100L) {
                string = "captain_bathia_q0039_0202.htm";
            } else if (n2 == 4 && (questState.getQuestItemsCount(this.IP) < 30L || questState.getQuestItemsCount(this.GML) < 30L)) {
                string = "captain_bathia_q0039_0304.htm";
            } else if (n2 == 5 && questState.getQuestItemsCount(this.IP) == 30L && questState.getQuestItemsCount(this.GML) == 30L) {
                string = "captain_bathia_q0039_0303.htm";
            }
        }
        return string;
    }

    @Override
    public String onKill(NpcInstance npcInstance, QuestState questState) {
        int n = npcInstance.getNpcId();
        int n2 = questState.getCond();
        if (n2 == 2) {
            if ((n == 20919 || n == 20920) && questState.getQuestItemsCount(this.BBN) <= 99L) {
                questState.giveItems(this.BBN, 1L);
            } else if (n == 20921 && questState.getQuestItemsCount(this.RBN) <= 99L) {
                questState.giveItems(this.RBN, 1L);
            }
            questState.playSound("ItemSound.quest_itemget");
            if (questState.getQuestItemsCount(this.BBN) + questState.getQuestItemsCount(this.RBN) == 200L) {
                questState.setCond(3);
                questState.playSound("ItemSound.quest_middle");
            }
        }
        if (n2 == 4) {
            if ((n == 20920 || n == 20921) && questState.getQuestItemsCount(this.IP) <= 29L) {
                questState.giveItems(this.IP, 1L);
            } else if (n == 20925 && questState.getQuestItemsCount(this.GML) <= 29L) {
                questState.giveItems(this.GML, 1L);
            }
            questState.playSound("ItemSound.quest_itemget");
            if (questState.getQuestItemsCount(this.IP) + questState.getQuestItemsCount(this.GML) == 60L) {
                questState.setCond(5);
                questState.playSound("ItemSound.quest_middle");
            }
        }
        return null;
    }
}

